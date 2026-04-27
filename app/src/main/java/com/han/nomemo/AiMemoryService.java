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
    private static final int MAX_AUTO_RETRY_COUNT = 6;
    private static final int ECONOMY_TOTAL_ATTEMPTS = 2;
    private static final long RETRY_BASE_DELAY_MS = 1_500L;
    private static final long RETRY_MAX_DELAY_MS = 8_000L;

    private final Context context;
    private final SettingsStore settingsStore;

    private enum CloudRequestMode {
        TEXT,
        IMAGE,
        MULTIMODAL
    }

    public interface AttemptListener {
        void onAttempt(int attempt, int attemptLimit);
    }

    public AiMemoryService(Context context) {
        this.context = context.getApplicationContext();
        this.settingsStore = new SettingsStore(this.context);
    }

    public GenerationResult generateMemory(String userText, @Nullable Uri imageUri) {
        return generateMemory(userText, imageUri, null);
    }

    public GenerationResult generateMemory(
            String userText,
            @Nullable Uri imageUri,
            @Nullable AttemptListener attemptListener
    ) {
        return generateMemoryInternal(userText, imageUri, false, null, attemptListener, false);
    }

    public GenerationResult generateEnhancedMemory(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext
    ) {
        return generateEnhancedMemory(userText, imageUri, detailContext, null);
    }

    public GenerationResult generateEnhancedMemory(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext,
            @Nullable AttemptListener attemptListener
    ) {
        return generateMemoryInternal(userText, imageUri, true, detailContext, attemptListener, false);
    }

    public GenerationResult generateEnhancedMemoryStrict(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext,
            @Nullable AttemptListener attemptListener
    ) {
        return generateMemoryInternal(userText, imageUri, true, detailContext, attemptListener, true);
    }

    private GenerationResult generateMemoryInternal(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext,
            @Nullable AttemptListener attemptListener,
            boolean requireCloudSuccess
    ) {
        String safeText = userText == null ? "" : userText.trim();
        String safeContext = detailContext == null ? "" : detailContext.trim();
        boolean economyMode = isEconomyMode();
        boolean retryWithFullPromptProfile = shouldRetryWithFullPromptProfile(enhanced, economyMode);
        boolean allowLocalFallback = shouldAllowLocalFallback(requireCloudSuccess);
        if (hasCloudConfig()) {
            Exception lastCloudError = null;
            int attemptLimit = resolveCloudAttemptLimit();
            for (int attempt = 1; attempt <= attemptLimit; attempt++) {
                if (attemptListener != null) {
                    attemptListener.onAttempt(attempt, attemptLimit);
                }
                try {
                    return generateByCloud(safeText, imageUri, enhanced, safeContext, economyMode);
                } catch (Exception exception) {
                    lastCloudError = exception;
                    if (attempt < attemptLimit) {
                        if (!sleepBeforeRetry(attempt)) {
                            break;
                        }
                    }
                }
            }
            // Economy reanalysis fallback: retry once with full prompt profile before giving up.
            if (lastCloudError != null && retryWithFullPromptProfile) {
                try {
                    return generateByCloud(safeText, imageUri, enhanced, safeContext, false);
                } catch (Exception recoveryError) {
                    lastCloudError.addSuppressed(recoveryError);
                }
            }
            if (lastCloudError != null) {
                if (!allowLocalFallback) {
                    throw new IllegalStateException("Cloud AI request failed after retries", lastCloudError);
                }
                // Falls back to local generation below after cloud attempts are exhausted.
            }
        }
        if (!allowLocalFallback) {
            throw new IllegalStateException("Cloud AI config unavailable");
        }
        if (attemptListener != null) {
            attemptListener.onAttempt(1, 1);
        }
        try {
            return enhanced
                    ? generateByRulesEnhanced(safeText, imageUri, safeContext)
                    : generateByRules(safeText, imageUri);
        } catch (Exception localError) {
            if (!allowLocalFallback) {
                throw new IllegalStateException("Strict AI generation cannot fall back to local", localError);
            }
            return buildEmergencyLocalResult(safeText, imageUri, enhanced, safeContext);
        }
    }

    static boolean shouldRetryWithFullPromptProfile(boolean enhanced, boolean economyMode) {
        return economyMode && enhanced;
    }

    static boolean shouldAllowLocalFallback(boolean requireCloudSuccess) {
        return !requireCloudSuccess;
    }

    private int resolveCloudAttemptLimit() {
        if (settingsStore.getEconomyMode()) {
            return settingsStore.getAutoRetry()
                    ? ECONOMY_TOTAL_ATTEMPTS
                    : 1;
        }
        return settingsStore.getAutoRetry()
                ? MAX_AUTO_RETRY_COUNT + 1
                : 1;
    }

    private boolean isEconomyMode() {
        return settingsStore.getEconomyMode();
    }

    private boolean sleepBeforeRetry(int retryIndex) {
        long delayMs = Math.min(RETRY_BASE_DELAY_MS * retryIndex, RETRY_MAX_DELAY_MS);
        try {
            Thread.sleep(delayMs);
            return true;
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private boolean hasCloudConfig() {
        return settingsStore.isAiAvailable()
                && !TextUtils.isEmpty(settingsStore.resolvedApiKey())
                && !TextUtils.isEmpty(settingsStore.resolvedApiBaseUrl())
                && !TextUtils.isEmpty(settingsStore.resolvedApiModel());
    }

    private GenerationResult generateByCloud(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext,
            boolean economyMode
    ) throws Exception {
        CloudRequestMode requestMode = resolveRequestMode(userText, imageUri);
        String localCandidatesJson = buildLocalCandidatesJson(userText);
        AiPromptSpec promptSpec = AiPromptBuilder.build(
                requestMode.name(),
                enhanced,
                economyMode,
                userText,
                detailContext,
                localCandidatesJson
        );
        JSONObject payload = new JSONObject();
        payload.put("model", resolveModelForMode(requestMode));
        payload.put("temperature", promptSpec.getTemperature());
        Integer maxTokens = promptSpec.getMaxTokens();
        if (maxTokens != null) {
            payload.put("max_tokens", maxTokens);
        }
        payload.put("messages", buildMessages(requestMode, imageUri, promptSpec));

        Exception firstError = null;
        try {
            JSONObject payloadWithFormat = new JSONObject(payload.toString());
            payloadWithFormat.put("response_format", new JSONObject().put("type", "json_object"));
            return requestCloudGeneration(payloadWithFormat, userText, imageUri);
        } catch (Exception exception) {
            firstError = exception;
        }

        try {
            return requestCloudGeneration(payload, userText, imageUri);
        } catch (Exception secondError) {
            if (firstError != null) {
                secondError.addSuppressed(firstError);
            }
            throw secondError;
        }
    }

    private String buildLocalCandidatesJson(String userText) {
        try {
            MemoryStructuredFacts facts = MemoryFactExtractor.extractLocalFacts(
                    userText,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            return MemoryStructuredFactsJson.toJson(facts);
        } catch (Exception ignored) {
            return "{}";
        }
    }

    private GenerationResult requestCloudGeneration(
            JSONObject payload,
            String userText,
            @Nullable Uri imageUri
    ) throws Exception {
        String content = executeCloudRequest(payload);
        JSONObject resultJson;
        try {
            resultJson = parseAndValidateResultJson(content);
        } catch (Exception parseException) {
            JSONObject repairPayload = buildRepairPayload(payload, content);
            String repairedContent = executeCloudRequest(repairPayload);
            resultJson = parseAndValidateResultJson(repairedContent);
        }

        String title = resultJson.optString("title", "").trim();
        String summary = resultJson.optString("summary", "").trim();
        String analysis = resultJson.optString("analysis", "").trim();
        String memory = resultJson.optString("memory", "").trim();
        String suggestedCategoryCode = resultJson.optString("suggestedCategoryCode", "").trim();
        JSONObject structuredFactsObject = resultJson.optJSONObject("structuredFacts");
        String aiStructuredFactsJson = structuredFactsObject == null ? "" : structuredFactsObject.toString();

        if (TextUtils.isEmpty(memory)) {
            memory = fallbackMemory(userText, imageUri);
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
        String structuredFactsJson = reconcileSafely(
                userText,
                aiStructuredFactsJson,
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        summary = stableSummarySafely(suggestedCategoryCode, summary, structuredFactsJson);
        return new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "cloud", structuredFactsJson);
    }

    private String fallbackMemory(String userText, @Nullable Uri imageUri) {
        if (!TextUtils.isEmpty(userText)) {
            return cropSingleLine(userText, 80);
        }
        if (imageUri != null) {
            return "已保存截图记忆。";
        }
        return "已记录一条记忆。";
    }

    private String executeCloudRequest(JSONObject payload) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(resolveChatCompletionsUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + settingsStore.resolvedApiKey());
            connection.setRequestProperty("Content-Type", "application/json");

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
            return extractMessageContent(json);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String resolveChatCompletionsUrl() {
        String baseUrl = settingsStore.resolvedApiBaseUrl().trim();
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (baseUrl.endsWith("/chat/completions")) {
            return baseUrl;
        }
        if (baseUrl.endsWith("/responses")) {
            return baseUrl.substring(0, baseUrl.length() - "/responses".length()) + "/chat/completions";
        }
        return baseUrl + "/chat/completions";
    }

    private CloudRequestMode resolveRequestMode(String userText, @Nullable Uri imageUri) {
        boolean hasImage = imageUri != null;
        boolean hasText = !TextUtils.isEmpty(userText);
        if (hasImage && hasText) {
            return CloudRequestMode.MULTIMODAL;
        }
        if (hasImage) {
            return CloudRequestMode.IMAGE;
        }
        return CloudRequestMode.TEXT;
    }

    private String resolveModelForMode(CloudRequestMode requestMode) {
        switch (requestMode) {
            case IMAGE:
                return settingsStore.resolvedImageModel();
            case MULTIMODAL:
                return settingsStore.resolvedMultimodalModel();
            case TEXT:
            default:
                return settingsStore.resolvedTextModel();
        }
    }

    private JSONArray buildMessages(
            CloudRequestMode requestMode,
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec
    ) throws Exception {
        switch (requestMode) {
            case IMAGE:
                return buildImageMessages(imageUri, promptSpec);
            case MULTIMODAL:
                return buildMultimodalMessages(imageUri, promptSpec);
            case TEXT:
            default:
                return buildTextMessages(promptSpec);
        }
    }

    private JSONArray buildTextMessages(AiPromptSpec promptSpec) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", promptSpec.getSystemPrompt()));
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", promptSpec.getUserPrompt()));
        return messages;
    }

    private JSONArray buildImageMessages(
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec
    ) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", promptSpec.getSystemPrompt()));

        JSONArray userContent = new JSONArray();
        userContent.put(new JSONObject()
                .put("type", "text")
                .put("text", promptSpec.getUserPrompt()));
        userContent.put(new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", requireImageDataUri(imageUri, promptSpec))));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userContent));
        return messages;
    }

    private JSONArray buildMultimodalMessages(
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec
    ) throws Exception {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", promptSpec.getSystemPrompt()));

        JSONArray userContent = new JSONArray();
        userContent.put(new JSONObject()
                .put("type", "text")
                .put("text", promptSpec.getUserPrompt()));
        userContent.put(new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", requireImageDataUri(imageUri, promptSpec))));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userContent));
        return messages;
    }

    private String requireImageDataUri(@Nullable Uri uri, AiPromptSpec promptSpec) {
        String dataUri = uri == null ? null : buildImageDataUri(uri, promptSpec);
        if (TextUtils.isEmpty(dataUri)) {
            throw new IllegalStateException("Image content is unavailable for the selected AI route.");
        }
        return dataUri;
    }

    @Nullable
    private String buildImageDataUri(Uri uri, AiPromptSpec promptSpec) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Bitmap bitmap = readBitmap(resolver, uri);
            if (bitmap == null) {
                return null;
            }
            Bitmap scaled = scaleBitmap(bitmap, promptSpec.getImageMaxSize());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaled.compress(
                    Bitmap.CompressFormat.JPEG,
                    promptSpec.getImageQuality(),
                    baos
            );
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
        int objectStart = cleaned.indexOf('{');
        int objectEnd = cleaned.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            cleaned = cleaned.substring(objectStart, objectEnd + 1).trim();
        }
        return new JSONObject(cleaned);
    }

    private JSONObject parseAndValidateResultJson(String content) throws Exception {
        return parseStrictJson(content);
    }

    private JSONObject buildRepairPayload(JSONObject originalPayload, String rawModelOutput) throws Exception {
        JSONObject repairPayload = new JSONObject();
        repairPayload.put("model", originalPayload.optString("model", settingsStore.resolvedTextModel()));
        repairPayload.put("temperature", 0);
        int originalMaxTokens = originalPayload.optInt("max_tokens", 0);
        if (originalMaxTokens > 0) {
            repairPayload.put("max_tokens", Math.max(originalMaxTokens, 360));
        } else if (isEconomyMode()) {
            repairPayload.put("max_tokens", 420);
        }
        if (originalPayload.has("response_format")) {
            repairPayload.put("response_format", new JSONObject().put("type", "json_object"));
        }
        repairPayload.put(
                "messages",
                new JSONArray()
                        .put(new JSONObject()
                                .put("role", "system")
                                .put("content", AiPromptBuilder.repairSystemPrompt()))
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", AiPromptBuilder.repairUserPrompt(rawModelOutput)))
        );
        return repairPayload;
    }

    private String extractMessageContent(JSONObject responseJson) throws Exception {
        Object content = responseJson.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .opt("content");
        if (content instanceof String) {
            return (String) content;
        }
        if (content instanceof JSONArray) {
            JSONArray parts = (JSONArray) content;
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < parts.length(); index++) {
                Object part = parts.get(index);
                if (part instanceof JSONObject) {
                    JSONObject partJson = (JSONObject) part;
                    String text = partJson.optString("text", "");
                    if (!TextUtils.isEmpty(text)) {
                        if (builder.length() > 0) {
                            builder.append('\n');
                        }
                        builder.append(text);
                    }
                } else if (part != null) {
                    if (builder.length() > 0) {
                        builder.append('\n');
                    }
                    builder.append(part.toString());
                }
            }
            return builder.toString();
        }
        return content == null ? "" : content.toString();
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

        String structuredFactsJson = reconcileSafely(
                userText,
                "",
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        summary = stableSummarySafely(suggestedCategoryCode, summary, structuredFactsJson);
        return new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "local", structuredFactsJson);
    }

    private GenerationResult generateByRulesEnhanced(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext
    ) {
        GenerationResult base = generateByRules(userText, imageUri);
        String analysis = base.getAnalysis();
        if (!TextUtils.isEmpty(detailContext)) {
            analysis = analysis + " 已结合现有条目内容进行二次整理与校对。";
        } else {
            analysis = analysis + " 已执行更细致的二次整理。";
        }
        String memory = base.getMemory();
        if (!TextUtils.isEmpty(detailContext)) {
            memory = memory + " 这次结果已参考旧版本信息并尽量补足上下文。";
        }
        String structuredFactsJson = reconcileSafely(
                userText,
                base.getStructuredFactsJson(),
                base.getTitle(),
                base.getSummary(),
                analysis,
                memory,
                base.getSuggestedCategoryCode()
        );
        String summary = stableSummarySafely(
                base.getSuggestedCategoryCode(),
                base.getSummary(),
                structuredFactsJson
        );
        return new GenerationResult(
                base.getTitle(),
                summary,
                analysis,
                memory,
                base.getSuggestedCategoryCode(),
                base.getEngine(),
                structuredFactsJson
        );
    }

    private String reconcileSafely(
            String userText,
            @Nullable String aiStructuredFactsJson,
            @Nullable String title,
            @Nullable String summary,
            @Nullable String analysis,
            @Nullable String memory,
            @Nullable String categoryCode
    ) {
        try {
            return MemoryFactReconciler.reconcileToJson(
                    userText,
                    aiStructuredFactsJson,
                    title,
                    summary,
                    analysis,
                    memory,
                    categoryCode
            );
        } catch (Exception ignored) {
            return "";
        }
    }

    private String stableSummarySafely(
            @Nullable String categoryCode,
            @Nullable String fallbackSummary,
            @Nullable String structuredFactsJson
    ) {
        try {
            return MemoryFactReconciler.stableSummary(categoryCode, fallbackSummary, structuredFactsJson);
        } catch (Exception ignored) {
            return fallbackSummary == null ? "" : fallbackSummary;
        }
    }

    private GenerationResult buildEmergencyLocalResult(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext
    ) {
        String suggestedCategoryCode = classifyCategoryCode(userText, imageUri != null);
        String memory = fallbackMemory(userText, imageUri);
        String title = cropSingleLine(memory, 18);
        String summary = cropSingleLine(TextUtils.isEmpty(userText) ? memory : userText, 40);
        String analysis = enhanced
                ? "已完成本地兜底重整"
                : "已完成本地兜底分析";
        if (enhanced && !TextUtils.isEmpty(detailContext)) {
            analysis = analysis + "，并已参考现有内容。";
        }
        String structuredFactsJson = reconcileSafely(
                userText,
                "",
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        summary = stableSummarySafely(suggestedCategoryCode, summary, structuredFactsJson);
        return new GenerationResult(
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode,
                "local",
                structuredFactsJson
        );
    }

    private String classifyCategoryCode(String text, boolean hasImage) {
        String source = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (containsAny(source, "待办", "todo", "完成", "处理", "跟进", "提交")) {
            return CategoryCatalog.CODE_WORK_TODO;
        }
        if (containsAny(source, "会议", "日程", "calendar", "预约", "时间")) {
            return CategoryCatalog.CODE_WORK_SCHEDULE;
        }
        if (containsAny(source, "快递", "包裹", "取件", "取件码", "提货码", "取货码", "自提码", "驿站", "菜鸟", "丰巢", "快递柜")) {
            return CategoryCatalog.CODE_LIFE_DELIVERY;
        }
        if (containsAny(source, "取餐", "取餐码", "外卖", "奶茶", "咖啡", "餐", "饭", "门店自取", "核销码")) {
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
        private final String structuredFactsJson;

        public GenerationResult(
                String title,
                String summary,
                String analysis,
                String memory,
                String suggestedCategoryCode,
                String engine
        ) {
            this(title, summary, analysis, memory, suggestedCategoryCode, engine, "");
        }

        public GenerationResult(
                String title,
                String summary,
                String analysis,
                String memory,
                String suggestedCategoryCode,
                String engine,
                String structuredFactsJson
        ) {
            this.title = title;
            this.summary = summary;
            this.analysis = analysis;
            this.memory = memory;
            this.suggestedCategoryCode = suggestedCategoryCode;
            this.engine = engine;
            this.structuredFactsJson = structuredFactsJson == null ? "" : structuredFactsJson;
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

        public String getStructuredFactsJson() {
            return structuredFactsJson;
        }
    }
}
