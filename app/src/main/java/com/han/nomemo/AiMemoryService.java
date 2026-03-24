package com.han.nomemo;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Scanner;

public class AiMemoryService {
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    private static final int IMAGE_MAX_SIZE = 1024;

    private final Context context;
    private final SettingsStore settingsStore;

    public AiMemoryService(Context context) {
        this.context = context.getApplicationContext();
        this.settingsStore = new SettingsStore(this.context);
    }

    public GenerationResult generateMemory(String userText, @Nullable Uri imageUri) {
        String safeText = userText == null ? "" : userText.trim();
        if (hasCloudConfig()) {
            try {
                return generateByCloud(safeText, imageUri);
            } catch (Exception ignored) {
                // Falls back to local generation below.
            }
        }
        return generateByRules(safeText, imageUri);
    }

    private boolean hasCloudConfig() {
        return !TextUtils.isEmpty(settingsStore.resolvedApiKey())
                && !TextUtils.isEmpty(settingsStore.resolvedApiBaseUrl())
                && !TextUtils.isEmpty(settingsStore.resolvedApiModel());
    }

    private GenerationResult generateByCloud(String userText, @Nullable Uri imageUri) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(settingsStore.resolvedApiBaseUrl() + "/chat/completions");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + settingsStore.resolvedApiKey());
            connection.setRequestProperty("Content-Type", "application/json");

            JSONObject payload = new JSONObject();
            payload.put("model", settingsStore.resolvedApiModel());
            payload.put("temperature", 0.35);
            payload.put("response_format", new JSONObject().put("type", "json_object"));
            payload.put("messages", buildMessages(userText, imageUri));

            byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body);
            outputStream.flush();
            outputStream.close();

            int code = connection.getResponseCode();
            String responseBody = readStream(code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream());

            if (code < 200 || code >= 300) {
                throw new IllegalStateException("Cloud AI request failed: " + code + " " + responseBody);
            }

            JSONObject json = new JSONObject(responseBody);
            String content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .optString("content", "");

            JSONObject resultJson = parseStrictJson(content);
            String title = resultJson.optString("title", "").trim();
            String summary = resultJson.optString("summary", "").trim();
            String analysis = resultJson.optString("analysis", "").trim();
            String memory = resultJson.optString("memory", "").trim();
            String suggestedCategoryCode = resultJson.optString("suggestedCategoryCode", "").trim();

            if (TextUtils.isEmpty(memory)) {
                throw new IllegalStateException("Cloud AI returned empty memory");
            }
            if (TextUtils.isEmpty(title)) {
                title = cropSingleLine(memory, 18);
            }
            if (TextUtils.isEmpty(summary)) {
                summary = cropSingleLine(userText, 36);
            }
            if (TextUtils.isEmpty(analysis)) {
                analysis = "AI 已完成内容整理";
            }
            if (TextUtils.isEmpty(suggestedCategoryCode)) {
                suggestedCategoryCode = classifyCategoryCode(userText, imageUri != null);
            }
            return new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "cloud");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    String SYSTEM_PROMPT = "# Role\n" +
            "你是一个极简主义的记忆助手。你的任务是分析用户输入的文本或 OCR 提取内容，并严格以 JSON 格式输出。\n" +
            "\n" +
            "# Response Protocol\n" +
            "- **语言：** 所有文本值必须使用简洁、高级的中文。\n" +
            "- **语气：** 冷静、专业，禁止任何寒暄或解释性文字。\n" +
            "- **格式：** 必须且只能输出 JSON 对象，严禁包含 Markdown 代码块标记（如 ```json）。\n" +
            "\n" +
            "# Classification Logic (Priority Order)\n" +
            "1. **WORK_SCHEDULE (日程):** 包含明确的具体时间点、会议、约会。\n" +
            "2. **WORK_TODO (待办):** 包含明确的动作指令，如“去写代码”、“联系某人”。\n" +
            "3. **LIFE_PICKUP (取餐):** 包含取餐码、排队号、餐饮单号。\n" +
            "4. **LIFE_DELIVERY (取件):** 包含快递单号、驿站取件码（如 5-2-101）。\n" +
            "5. **LIFE_CARD (卡证):** 包含身份证、银行卡、工牌、会员卡等证件号码。\n" +
            "6. **LIFE_TICKET (票券):** 包含车票、电影票、展会门票、行程单。\n" +
            "7. **QUICK_NOTE (小记):** 【默认/兜底】任何无法归入上述类别的信息、灵感、碎碎念或不确定的 OCR 乱码。\n" +
            "\n" +
            "# JSON Schema\n" +
            "{\n" +
            "  \"title\": \"5字内核心主题\",\n" +
            "  \"summary\": \"15字内摘要\",\n" +
            "  \"analysis\": \"对原始信息的关键点提炼（保留单号、时间等硬核数据）\",\n" +
            "  \"memory\": \"还原后的规范化完整内容\",\n" +
            "  \"suggestedCategoryCode\": \"必须是上述定义的枚举值之一\"\n" +
            "}";

    private JSONArray buildMessages(String userText, @Nullable Uri imageUri) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", SYSTEM_PROMPT));

        JSONArray userContent = new JSONArray();
        String textForModel = "User text:\n" + (TextUtils.isEmpty(userText) ? "(none)" : userText) + "\n\n" +
                "If screenshot exists, include important visual cues. " +
                "Return a short title and a short summary suitable for a memory card.";
        userContent.put(new JSONObject()
                .put("type", "text")
                .put("text", textForModel));

        if (imageUri != null) {
            String dataUri = buildImageDataUri(imageUri);
            if (!TextUtils.isEmpty(dataUri)) {
                userContent.put(new JSONObject()
                        .put("type", "image_url")
                        .put("image_url", new JSONObject().put("url", dataUri)));
            }
        }

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userContent));
        return messages;
    }

    @Nullable
    private String buildImageDataUri(Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Bitmap bitmap = readBitmap(resolver, uri);
            if (bitmap == null) {
                return null;
            }
            Bitmap scaled = scaleBitmap(bitmap, IMAGE_MAX_SIZE);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    private Bitmap readBitmap(ContentResolver resolver, Uri uri) throws Exception {
        InputStream inputStream = resolver.openInputStream(uri);
        if (inputStream == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeStream(inputStream);
        } finally {
            inputStream.close();
        }
    }

    private Bitmap scaleBitmap(Bitmap source, int maxEdge) {
        int width = source.getWidth();
        int height = source.getHeight();
        int longest = Math.max(width, height);
        if (longest <= maxEdge) {
            return source;
        }
        float ratio = (float) maxEdge / longest;
        int scaledWidth = Math.max(1, Math.round(width * ratio));
        int scaledHeight = Math.max(1, Math.round(height * ratio));
        return Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true);
    }

    private String readStream(@Nullable InputStream stream) {
        if (stream == null) {
            return "";
        }
        Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return result;
    }

    private JSONObject parseStrictJson(String content) throws Exception {
        String cleaned = content == null ? "" : content.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replace("```json", "").replace("```", "").trim();
        }
        return new JSONObject(cleaned);
    }

    private GenerationResult generateByRules(String userText, @Nullable Uri imageUri) {
        boolean hasText = !TextUtils.isEmpty(userText);
        boolean hasImage = imageUri != null;
        String suggestedCategoryCode = classifyCategoryCode(userText, hasImage);

        String analysis;
        if (hasText && hasImage) {
            analysis = "已同时记录文字和截图，这是一条信息较完整的记忆。";
        } else if (hasText) {
            analysis = "已提取文字要点，关键词：" + buildTags(userText);
        } else if (hasImage) {
            analysis = "已保存截图，建议之后补一句说明，便于回看。";
        } else {
            analysis = "未检测到可分析内容。";
        }

        String title;
        String summary;
        if (hasText) {
            title = cropSingleLine(userText, 18);
            summary = cropSingleLine(userText, 40);
        } else if (hasImage) {
            title = "截图记忆";
            summary = "保存了一张截图，可稍后补充说明。";
        } else {
            title = "空白记忆";
            summary = "当前没有可用内容。";
        }

        String timeText = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        String memory;
        if (hasText) {
            memory = timeText + " 记录了一条内容：" + cropSingleLine(userText, 56);
        } else if (hasImage) {
            memory = timeText + " 保存了一张截图记忆。";
        } else {
            memory = timeText + " 创建了一条空白记忆草稿。";
        }

        return new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "local");
    }

    private String classifyCategoryCode(String text, boolean hasImage) {
        String source = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (containsAny(source, "待办", "todo", "完成", "处理", "跟进", "提交")) {
            return CategoryCatalog.CODE_WORK_TODO;
        }
        if (containsAny(source, "会议", "日程", "calendar", "预约", "时间")) {
            return CategoryCatalog.CODE_WORK_SCHEDULE;
        }
        if (containsAny(source, "快递", "包裹", "取件")) {
            return CategoryCatalog.CODE_LIFE_DELIVERY;
        }
        if (containsAny(source, "取餐", "外卖", "奶茶", "餐", "饭")) {
            return CategoryCatalog.CODE_LIFE_PICKUP;
        }
        if (containsAny(source, "卡", "证", "身份证", "门禁", "驾照")) {
            return CategoryCatalog.CODE_LIFE_CARD;
        }
        if (containsAny(source, "票", "车票", "机票", "电影票", "券")) {
            return CategoryCatalog.CODE_LIFE_TICKET;
        }
        if (hasImage && TextUtils.isEmpty(source)) {
            return CategoryCatalog.CODE_LIFE_TICKET;
        }
        return CategoryCatalog.CODE_QUICK_NOTE;
    }

    private boolean containsAny(String source, String... keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildTags(String text) {
        String[] words = text.split("[\\s,.;:!?()\\[\\]{}\"'`，。；：！？（）]+");
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        for (String word : words) {
            String trimmed = word.trim();
            if (trimmed.length() < 2) {
                continue;
            }
            tags.add(trimmed.toLowerCase(Locale.ROOT));
            if (tags.size() >= 5) {
                break;
            }
        }
        if (tags.isEmpty()) {
            return "暂无明显关键词";
        }
        return TextUtils.join("、", tags);
    }

    private String cropSingleLine(String text, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        String singleLine = text.replace('\n', ' ').trim();
        if (singleLine.length() <= maxLength) {
            return singleLine;
        }
        return singleLine.substring(0, maxLength) + "...";
    }

    public static class GenerationResult {
        private final String title;
        private final String summary;
        private final String analysis;
        private final String memory;
        private final String suggestedCategoryCode;
        private final String engine;

        public GenerationResult(
                String title,
                String summary,
                String analysis,
                String memory,
                String suggestedCategoryCode,
                String engine
        ) {
            this.title = title;
            this.summary = summary;
            this.analysis = analysis;
            this.memory = memory;
            this.suggestedCategoryCode = suggestedCategoryCode;
            this.engine = engine;
        }

        public String getTitle() {
            return title;
        }

        public String getSummary() {
            return summary;
        }

        public String getAnalysis() {
            return analysis;
        }

        public String getMemory() {
            return memory;
        }

        public String getSuggestedCategoryCode() {
            return suggestedCategoryCode;
        }

        public String getEngine() {
            return engine;
        }
    }
}
