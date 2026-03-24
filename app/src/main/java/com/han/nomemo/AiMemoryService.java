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

    public AiMemoryService(Context context) {
        this.context = context.getApplicationContext();
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
        return !TextUtils.isEmpty(BuildConfig.OPENAI_API_KEY)
                && !TextUtils.isEmpty(BuildConfig.OPENAI_BASE_URL)
                && !TextUtils.isEmpty(BuildConfig.OPENAI_MODEL);
    }

    private GenerationResult generateByCloud(String userText, @Nullable Uri imageUri) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BuildConfig.OPENAI_BASE_URL + "/chat/completions");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            JSONObject payload = new JSONObject();
            payload.put("model", BuildConfig.OPENAI_MODEL);
            payload.put("temperature", 0.4);
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
            String analysis = resultJson.optString("analysis", "").trim();
            String memory = resultJson.optString("memory", "").trim();
            if (TextUtils.isEmpty(memory)) {
                throw new IllegalStateException("Cloud AI returned empty memory");
            }
            if (TextUtils.isEmpty(analysis)) {
                analysis = "云端分析完成。";
            }
            return new GenerationResult(analysis, memory, "cloud");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private JSONArray buildMessages(String userText, @Nullable Uri imageUri) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content",
                        "You are a memory assistant. Analyze user input and output strict JSON with keys " +
                                "analysis and memory. Keep each value concise and in Chinese."));

        JSONArray userContent = new JSONArray();
        String textForModel = "User text:\n" + (TextUtils.isEmpty(userText) ? "(none)" : userText) + "\n\n" +
                "If screenshot exists, include important visual cues in analysis.";
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

        String analysis;
        if (hasText && hasImage) {
            analysis = "已同时记录文字与截图，这是一个信息完整的记忆点。";
        } else if (hasText) {
            analysis = "已记录文字要点，关键词：" + buildTags(userText);
        } else if (hasImage) {
            analysis = "已记录截图，建议稍后补充一句文字，方便回忆。";
        } else {
            analysis = "未检测到可用内容。";
        }

        String timeText = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        String memory;
        if (hasText) {
            String shortText = userText.length() > 100 ? userText.substring(0, 100) + "..." : userText;
            memory = timeText + " 记下：" + shortText;
        } else if (hasImage) {
            memory = timeText + " 保存了一张截图记忆，已标记为待回看。";
        } else {
            memory = timeText + " 创建了一条空白记忆草稿。";
        }

        return new GenerationResult(analysis, memory, "local");
    }

    private String buildTags(String text) {
        String[] words = text.split("[\\s,.;:!?()\\[\\]{}\"'`]+");
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        for (String word : words) {
            String trimmed = word.trim();
            if (trimmed.length() < 3) {
                continue;
            }
            tags.add(trimmed.toLowerCase(Locale.ROOT));
            if (tags.size() >= 5) {
                break;
            }
        }
        if (tags.isEmpty()) {
            return "无明显关键词";
        }
        return TextUtils.join(", ", tags);
    }

    public static class GenerationResult {
        private final String analysis;
        private final String memory;
        private final String engine;

        public GenerationResult(String analysis, String memory, String engine) {
            this.analysis = analysis;
            this.memory = memory;
            this.engine = engine;
        }

        public String getAnalysis() {
            return analysis;
        }

        public String getMemory() {
            return memory;
        }

        public String getEngine() {
            return engine;
        }
    }
}
