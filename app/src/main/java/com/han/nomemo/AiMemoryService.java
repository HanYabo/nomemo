package com.han.nomemo;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

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
    private static final String TAG = "AiMemoryService";
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    private static final int MIN_REPAIR_MAX_TOKENS = 900;
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

    public AiAnalysisOutcome analyzeWithPolicy(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext,
            @Nullable AttemptListener attemptListener,
            AiExecutionPolicy policy
    ) {
        String safeText = userText == null ? "" : userText.trim();
        String safeContext = detailContext == null ? "" : detailContext.trim();
        CloudRequestMode requestMode = resolveRequestMode(safeText, imageUri);
        LocalEvidenceBundle localEvidence = buildLocalEvidence(safeText, imageUri);
        Exception lastCloudError = null;
        AiFailureStage lastFailureStage = null;
        boolean repairUsed = false;
        boolean fullPromptRescueUsed = false;
        int attemptsPerformed = 0;
        int totalAttemptLimit = policy.getTotalAttemptLimit();
        if (hasCloudConfigFor(requestMode)) {
            for (int attempt = 1; attempt <= policy.getCloudAttemptLimit(); attempt++) {
                attemptsPerformed = attempt;
                if (attemptListener != null) {
                    attemptListener.onAttempt(attempt, totalAttemptLimit);
                }
                try {
                    CloudGenerationResult cloudResult = generateByCloud(
                            safeText,
                            imageUri,
                            enhanced,
                            safeContext,
                            policy,
                            requestMode,
                            policy.getCostMode() == AiCostMode.ECONOMY,
                            localEvidence
                    );
                    repairUsed = repairUsed || cloudResult.isRepairUsed();
                    logAttemptSuccess(policy, attempt, totalAttemptLimit, cloudResult);
                    return AiAnalysisOutcome.success(
                            cloudResult.getResult(),
                            attempt,
                            totalAttemptLimit,
                            repairUsed,
                            false
                    );
                } catch (AiGenerationException exception) {
                    lastCloudError = exception;
                    lastFailureStage = exception.getFailureStage();
                    logAttemptFailure(policy, attempt, totalAttemptLimit, exception);
                } catch (Exception exception) {
                    lastCloudError = exception;
                    lastFailureStage = AiFailureStage.CLOUD_REQUEST;
                    logAttemptFailure(policy, attempt, totalAttemptLimit, exception, requestMode, resolveModelForMode(requestMode));
                }
                if (attempt < policy.getCloudAttemptLimit() && !sleepBeforeRetry(attempt)) {
                    break;
                }
            }
            if (lastCloudError != null && policy.isAllowFullPromptRescue()) {
                fullPromptRescueUsed = true;
                attemptsPerformed = totalAttemptLimit;
                if (attemptListener != null) {
                    attemptListener.onAttempt(totalAttemptLimit, totalAttemptLimit);
                }
                try {
                    CloudGenerationResult cloudResult = generateByCloud(
                            safeText,
                            imageUri,
                            enhanced,
                            safeContext,
                            policy,
                            requestMode,
                            false,
                            localEvidence
                    );
                    repairUsed = repairUsed || cloudResult.isRepairUsed();
                    logAttemptSuccess(policy, totalAttemptLimit, totalAttemptLimit, cloudResult);
                    return AiAnalysisOutcome.success(
                            cloudResult.getResult(),
                            totalAttemptLimit,
                            totalAttemptLimit,
                            repairUsed,
                            true
                    );
                } catch (AiGenerationException exception) {
                    lastCloudError = exception;
                    lastFailureStage = exception.getFailureStage();
                    logAttemptFailure(policy, totalAttemptLimit, totalAttemptLimit, exception);
                } catch (Exception exception) {
                    lastCloudError = exception;
                    lastFailureStage = AiFailureStage.CLOUD_REQUEST;
                    logAttemptFailure(policy, totalAttemptLimit, totalAttemptLimit, exception, requestMode, resolveModelForMode(requestMode));
                }
            }
        } else if (!policy.isAllowLocalFallback()) {
            return AiAnalysisOutcome.failure(
                    0,
                    totalAttemptLimit,
                    false,
                    false,
                    AiFailureStage.CLOUD_REQUEST,
                    "Cloud AI config unavailable"
            );
        }

        if (!policy.isAllowLocalFallback()) {
            return AiAnalysisOutcome.failure(
                    attemptsPerformed,
                    totalAttemptLimit,
                    repairUsed,
                    fullPromptRescueUsed,
                    lastFailureStage,
                    buildFailureMessage(lastCloudError, lastFailureStage)
            );
        }

        int localAttemptCount = attemptsPerformed > 0 ? attemptsPerformed : 1;
        if (attemptListener != null && attemptsPerformed == 0) {
            attemptListener.onAttempt(localAttemptCount, totalAttemptLimit);
        }
        try {
            GenerationResult result = enhanced
                    ? generateByRulesEnhanced(safeText, imageUri, safeContext)
                    : generateByRules(safeText, imageUri);
            return AiAnalysisOutcome.success(
                    result,
                    localAttemptCount,
                    totalAttemptLimit,
                    repairUsed,
                    fullPromptRescueUsed
            );
        } catch (Exception localError) {
            GenerationResult emergencyResult = buildEmergencyLocalResult(safeText, imageUri, enhanced, safeContext);
            return AiAnalysisOutcome.success(
                    emergencyResult,
                    localAttemptCount,
                    totalAttemptLimit,
                    repairUsed,
                    fullPromptRescueUsed
            );
        }
    }

    private GenerationResult generateMemoryInternal(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext,
            @Nullable AttemptListener attemptListener,
            boolean requireCloudSuccess
    ) {
        AiOperationKind operationKind = enhanced ? AiOperationKind.REANALYZE : AiOperationKind.INITIAL_ANALYSIS;
        AiExecutionPolicy basePolicy = AiAnalysisPolicies.resolve(settingsStore, operationKind);
        AiExecutionPolicy policy = new AiExecutionPolicy(
                basePolicy.getOperationKind(),
                basePolicy.getCostMode(),
                basePolicy.getCloudAttemptLimit(),
                basePolicy.isAllowFullPromptRescue(),
                !requireCloudSuccess
        );
        AiAnalysisOutcome outcome = analyzeWithPolicy(
                userText,
                imageUri,
                enhanced,
                detailContext,
                attemptListener,
                policy
        );
        if (outcome.isSuccess() && outcome.getGenerationResult() != null) {
            return outcome.getGenerationResult();
        }
        throw new IllegalStateException(
                outcome.getFailureMessage() == null ? "AI generation failed" : outcome.getFailureMessage()
        );
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

    private boolean hasCloudConfigFor(CloudRequestMode requestMode) {
        String resolvedModel = resolveModelForMode(requestMode);
        return settingsStore.isAiAvailable()
                && !TextUtils.isEmpty(settingsStore.resolvedApiKey())
                && !TextUtils.isEmpty(settingsStore.resolvedApiBaseUrl())
                && !TextUtils.isEmpty(resolvedModel);
    }

    private CloudGenerationResult generateByCloud(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext,
            AiExecutionPolicy policy,
            CloudRequestMode requestMode,
            boolean economyMode,
            LocalEvidenceBundle localEvidence
    ) throws Exception {
        String localCandidatesJson = localEvidence.getLocalCandidatesJson();
        AiPromptSpec promptSpec = AiPromptBuilder.build(
                requestMode.name(),
                enhanced,
                economyMode,
                userText,
                detailContext,
                localCandidatesJson
        );
        AiPreparedRequest preparedRequest = buildPreparedRequest(requestMode, imageUri, promptSpec);

        Exception firstError = null;
        AiModelCapabilityRegistry.ModelCapabilities capabilities =
                AiModelCapabilityRegistry.resolve(preparedRequest.getModel());
        if (capabilities.supportsResponseFormatJson()) {
            try {
                return requestCloudGeneration(preparedRequest, userText, imageUri, policy, localEvidence, true);
            } catch (AiGenerationException exception) {
                if (isResponseFormatUnsupported(exception)) {
                    AiModelCapabilityRegistry.markResponseFormatUnsupported(preparedRequest.getModel());
                    firstError = exception;
                } else if (isSystemRoleUnsupported(exception) && capabilities.supportsSystemRole()) {
                    AiModelCapabilityRegistry.markSystemRoleUnsupported(preparedRequest.getModel());
                    preparedRequest = buildPreparedRequest(requestMode, imageUri, promptSpec);
                    firstError = exception;
                } else {
                    throw exception;
                }
            }
        }

        try {
            return requestCloudGeneration(preparedRequest, userText, imageUri, policy, localEvidence, false);
        } catch (AiGenerationException exception) {
            if (isSystemRoleUnsupported(exception)
                    && AiModelCapabilityRegistry.resolve(preparedRequest.getModel()).supportsSystemRole()) {
                AiModelCapabilityRegistry.markSystemRoleUnsupported(preparedRequest.getModel());
                AiPreparedRequest adjustedRequest = buildPreparedRequest(requestMode, imageUri, promptSpec);
                return requestCloudGeneration(adjustedRequest, userText, imageUri, policy, localEvidence, false);
            }
            if (firstError != null) {
                exception.addSuppressed(firstError);
            }
            throw exception;
        } catch (Exception secondError) {
            if (firstError != null) {
                secondError.addSuppressed(firstError);
            }
            throw secondError;
        }
    }

    private AiPreparedRequest buildPreparedRequest(
            CloudRequestMode requestMode,
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec
    ) throws Exception {
        String model = resolveModelForMode(requestMode);
        AiModelCapabilityRegistry.ModelCapabilities capabilities = AiModelCapabilityRegistry.resolve(model);
        if (requestMode != CloudRequestMode.TEXT && !capabilities.supportsImageInput()) {
            throw new AiGenerationException(
                    AiFailureStage.CLOUD_REQUEST,
                    "Selected AI model does not support image input",
                    null,
                    requestMode.name(),
                    model,
                    promptSpec.getMaxTokens(),
                    0,
                    false,
                    0,
                    "image_input_unsupported",
                    null
            );
        }
        BuiltMessages builtMessages = buildMessages(requestMode, imageUri, promptSpec, capabilities.supportsSystemRole());
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("temperature", promptSpec.getTemperature());
        Integer maxTokens = promptSpec.getMaxTokens();
        if (maxTokens != null) {
            payload.put("max_tokens", maxTokens);
        }
        payload.put("messages", builtMessages.getMessages());
        return new AiPreparedRequest(requestMode, model, promptSpec, payload, builtMessages.getImageBytes());
    }

    private LocalEvidenceBundle buildLocalEvidence(String userText, @Nullable Uri imageUri) {
        String ocrVisibleText = extractLocalOcrVisibleText(imageUri);
        try {
            MemoryStructuredFacts facts = MemoryFactExtractor.extractLocalFacts(
                    userText,
                    ocrVisibleText,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            return new LocalEvidenceBundle(
                    MemoryStructuredFactsJson.toJson(facts),
                    ocrVisibleText
            );
        } catch (Exception ignored) {
            return new LocalEvidenceBundle("{}", ocrVisibleText);
        }
    }

    private CloudGenerationResult requestCloudGeneration(
            AiPreparedRequest preparedRequest,
            String userText,
            @Nullable Uri imageUri,
            AiExecutionPolicy policy,
            LocalEvidenceBundle localEvidence,
            boolean useResponseFormat
    ) throws Exception {
        JSONObject payload = new JSONObject(preparedRequest.getPayload().toString());
        if (useResponseFormat) {
            payload.put("response_format", new JSONObject().put("type", "json_object"));
        }
        AiCloudResponse response = executeCloudRequest(payload, preparedRequest, useResponseFormat);
        if (shouldTreatAsTokenExhausted(response.getFinishReason(), response.getContent())) {
            throw buildTokenExhaustedException(response, null);
        }
        JSONObject resultJson;
        boolean repairUsed = false;
        try {
            resultJson = parseAndValidateResultJson(response.getContent());
        } catch (AiGenerationException parseException) {
            if (parseException.getFailureStage() == AiFailureStage.TOKEN_EXHAUSTED
                    || shouldTreatAsTokenExhausted(response.getFinishReason(), response.getContent())) {
                throw buildTokenExhaustedException(response, parseException);
            }
            JSONObject repairPayload = buildRepairPayload(preparedRequest, payload, policy, response.getContent());
            AiCloudResponse repairedResponse;
            try {
                repairedResponse = executeCloudRequest(
                        repairPayload,
                        preparedRequest,
                        repairPayload.has("response_format")
                );
            } catch (Exception repairRequestException) {
                throw new AiGenerationException(
                        AiFailureStage.JSON_REPAIR,
                        "Repair request failed",
                        repairRequestException,
                        response.getRequestMode(),
                        response.getModel(),
                        repairPayload.optInt("max_tokens", MIN_REPAIR_MAX_TOKENS),
                        response.getImageBytes(),
                        repairPayload.has("response_format"),
                        response.getHttpStatus(),
                        response.getProviderErrorCode(),
                        response.getFinishReason()
                );
            }
            if (shouldTreatAsTokenExhausted(repairedResponse.getFinishReason(), repairedResponse.getContent())) {
                throw buildTokenExhaustedException(repairedResponse, parseException);
            }
            try {
                resultJson = parseAndValidateResultJson(repairedResponse.getContent());
                repairUsed = true;
            } catch (AiGenerationException repairedParseException) {
                if (repairedParseException.getFailureStage() == AiFailureStage.TOKEN_EXHAUSTED
                        || shouldTreatAsTokenExhausted(repairedResponse.getFinishReason(), repairedResponse.getContent())) {
                    throw buildTokenExhaustedException(repairedResponse, repairedParseException);
                }
                throw new AiGenerationException(
                        AiFailureStage.JSON_REPAIR,
                        "Repair output is still invalid",
                        repairedParseException,
                        repairedResponse.getRequestMode(),
                        repairedResponse.getModel(),
                        repairPayload.optInt("max_tokens", MIN_REPAIR_MAX_TOKENS),
                        repairedResponse.getImageBytes(),
                        repairPayload.has("response_format"),
                        repairedResponse.getHttpStatus(),
                        repairedResponse.getProviderErrorCode(),
                        repairedResponse.getFinishReason()
                );
            }
        }

        String title = resultJson.optString("title", "").trim();
        String summary = resultJson.optString("summary", "").trim();
        String analysis = resultJson.optString("analysis", "").trim();
        String memory = resultJson.optString("memory", "").trim();
        String suggestedCategoryCode = resultJson.optString("suggestedCategoryCode", "").trim();
        JSONObject structuredFactsObject = resultJson.optJSONObject("structuredFacts");
        String aiStructuredFactsJson = structuredFactsObject == null ? "" : structuredFactsObject.toString();
        aiStructuredFactsJson = mergeRawVisibleText(aiStructuredFactsJson, localEvidence.getOcrVisibleText());

        String effectiveText = mergePrimaryText(userText, localEvidence.getOcrVisibleText());
        if (TextUtils.isEmpty(memory)) {
            memory = fallbackMemory(effectiveText, imageUri);
        }
        if (TextUtils.isEmpty(title)) {
            title = cropSingleLine(memory, 18);
        }
        if (TextUtils.isEmpty(summary)) {
            summary = cropSingleLine(effectiveText, 36);
        }
        if (TextUtils.isEmpty(analysis)) {
            analysis = "AI 已完成内容整理";
        }
        if (TextUtils.isEmpty(suggestedCategoryCode)) {
            suggestedCategoryCode = classifyCategoryCode(effectiveText, imageUri != null);
        }
        String structuredFactsJson = reconcileSafely(
                effectiveText,
                aiStructuredFactsJson,
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        suggestedCategoryCode = normalizeCategoryCodeSafely(suggestedCategoryCode, structuredFactsJson);
        summary = stableSummarySafely(suggestedCategoryCode, summary, structuredFactsJson);
        return new CloudGenerationResult(
                new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "cloud", structuredFactsJson),
                repairUsed,
                response
        );
    }

    @Nullable
    private String extractLocalOcrVisibleText(@Nullable Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        try {
            MemoryImageOcrResult result = MemoryImageOcrService.extractVisibleText(context, imageUri);
            if (result == null) {
                return null;
            }
            String mergedText = result.getMergedText();
            return TextUtils.isEmpty(mergedText) ? null : mergedText;
        } catch (Exception exception) {
            Log.w(TAG, "Local OCR evidence extraction failed", exception);
            return null;
        }
    }

    private String mergeRawVisibleText(@Nullable String structuredFactsJson, @Nullable String ocrVisibleText) {
        if (TextUtils.isEmpty(ocrVisibleText)) {
            return structuredFactsJson == null ? "" : structuredFactsJson;
        }
        try {
            JSONObject json = TextUtils.isEmpty(structuredFactsJson)
                    ? new JSONObject()
                    : new JSONObject(structuredFactsJson);
            String existing = json.optString("rawVisibleText", "").trim();
            if (TextUtils.isEmpty(existing)) {
                json.put("rawVisibleText", ocrVisibleText);
            }
            return json.toString();
        } catch (Exception ignored) {
            return structuredFactsJson == null ? "" : structuredFactsJson;
        }
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

    private AiCloudResponse executeCloudRequest(
            JSONObject payload,
            AiPreparedRequest preparedRequest,
            boolean usedResponseFormat
    ) throws Exception {
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
                ProviderErrorDetails errorDetails = parseProviderErrorDetails(responseBody);
                throw new AiGenerationException(
                        AiFailureStage.CLOUD_REQUEST,
                        "Cloud AI request failed: " + code + " " + errorDetails.getMessage(),
                        null,
                        preparedRequest.getRequestMode().name(),
                        preparedRequest.getModel(),
                        payload.optInt("max_tokens", 0),
                        preparedRequest.getImageBytes(),
                        usedResponseFormat,
                        code,
                        errorDetails.getCode(),
                        null
                );
            }

            JSONObject json = new JSONObject(responseBody);
            return new AiCloudResponse(
                    extractMessageContent(json),
                    code,
                    null,
                    extractFinishReason(json),
                    usedResponseFormat,
                    preparedRequest.getModel(),
                    preparedRequest.getRequestMode().name(),
                    payload.optInt("max_tokens", 0),
                    preparedRequest.getImageBytes()
            );
        } catch (AiGenerationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AiGenerationException(
                    AiFailureStage.CLOUD_REQUEST,
                    "Cloud request failed",
                    exception,
                    preparedRequest.getRequestMode().name(),
                    preparedRequest.getModel(),
                    payload.optInt("max_tokens", 0),
                    preparedRequest.getImageBytes(),
                    usedResponseFormat,
                    0,
                    null,
                    null
            );
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

    private BuiltMessages buildMessages(
            CloudRequestMode requestMode,
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec,
            boolean supportsSystemRole
    ) throws Exception {
        switch (requestMode) {
            case IMAGE:
                return buildImageMessages(imageUri, promptSpec, supportsSystemRole);
            case MULTIMODAL:
                return buildMultimodalMessages(imageUri, promptSpec, supportsSystemRole);
            case TEXT:
            default:
                return buildTextMessages(promptSpec, supportsSystemRole);
        }
    }

    private BuiltMessages buildTextMessages(AiPromptSpec promptSpec, boolean supportsSystemRole) throws Exception {
        JSONArray messages = new JSONArray();
        if (supportsSystemRole) {
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", promptSpec.getSystemPrompt()));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", promptSpec.getUserPrompt()));
        } else {
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", composeSingleUserPrompt(promptSpec)));
        }
        return new BuiltMessages(messages, 0);
    }

    private String composeSingleUserPrompt(AiPromptSpec promptSpec) {
        return promptSpec.getSystemPrompt() + "\n\n" + promptSpec.getUserPrompt();
    }

    private BuiltMessages buildImageMessages(
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec,
            boolean supportsSystemRole
    ) throws Exception {
        EncodedImagePayload imagePayload = requireImagePayload(imageUri, promptSpec);
        JSONArray messages = new JSONArray();
        if (supportsSystemRole) {
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", promptSpec.getSystemPrompt()));
        }

        JSONArray userContent = new JSONArray();
        userContent.put(new JSONObject()
                .put("type", "text")
                .put("text", supportsSystemRole ? promptSpec.getUserPrompt() : composeSingleUserPrompt(promptSpec)));
        userContent.put(new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", imagePayload.getDataUri())));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userContent));
        return new BuiltMessages(messages, imagePayload.getByteSize());
    }

    private BuiltMessages buildMultimodalMessages(
            @Nullable Uri imageUri,
            AiPromptSpec promptSpec,
            boolean supportsSystemRole
    ) throws Exception {
        EncodedImagePayload imagePayload = requireImagePayload(imageUri, promptSpec);
        JSONArray messages = new JSONArray();
        if (supportsSystemRole) {
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", promptSpec.getSystemPrompt()));
        }

        JSONArray userContent = new JSONArray();
        userContent.put(new JSONObject()
                .put("type", "text")
                .put("text", supportsSystemRole ? promptSpec.getUserPrompt() : composeSingleUserPrompt(promptSpec)));
        userContent.put(new JSONObject()
                .put("type", "image_url")
                .put("image_url", new JSONObject().put("url", imagePayload.getDataUri())));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userContent));
        return new BuiltMessages(messages, imagePayload.getByteSize());
    }

    private EncodedImagePayload requireImagePayload(@Nullable Uri uri, AiPromptSpec promptSpec) {
        EncodedImagePayload imagePayload = uri == null ? null : buildImageDataUri(uri, promptSpec);
        if (imagePayload == null || TextUtils.isEmpty(imagePayload.getDataUri())) {
            throw new IllegalStateException("Image content is unavailable for the selected AI route.");
        }
        return imagePayload;
    }

    @Nullable
    private EncodedImagePayload buildImageDataUri(Uri uri, AiPromptSpec promptSpec) {
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
            return new EncodedImagePayload(
                    "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP),
                    bytes.length
            );
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

    static boolean shouldTreatAsTokenExhausted(@Nullable String finishReason, @Nullable String content) {
        return isTokenExhaustedFinishReason(finishReason) || looksLikeTruncatedJson(content);
    }

    static boolean isTokenExhaustedFinishReason(@Nullable String finishReason) {
        return finishReason != null && "length".equalsIgnoreCase(finishReason.trim());
    }

    static boolean looksLikeTruncatedJson(@Nullable String content) {
        String normalized = content == null ? "" : content.trim();
        if (normalized.isEmpty()) {
            return false;
        }
        if (normalized.startsWith("```")) {
            normalized = normalized.replace("```json", "").replace("```", "").trim();
        }
        if (!normalized.startsWith("{")) {
            return false;
        }
        if (normalized.endsWith("}")) {
            return false;
        }
        return normalized.contains("\"structuredFacts\"")
                || normalized.contains("\"summary\"")
                || normalized.contains("\"analysis\"")
                || normalized.contains("\"memory\"");
    }

    private JSONObject parseAndValidateResultJson(String content) throws Exception {
        final JSONObject parsed;
        try {
            parsed = parseStrictJson(content);
        } catch (Exception exception) {
            throw new AiGenerationException(AiFailureStage.JSON_PARSE, "Failed to parse AI JSON", exception);
        }
        try {
            return AiResultValidator.validate(parsed);
        } catch (Exception exception) {
            throw new AiGenerationException(AiFailureStage.SCHEMA_VALIDATE, "AI JSON schema validation failed", exception);
        }
    }

    private JSONObject buildRepairPayload(
            AiPreparedRequest preparedRequest,
            JSONObject originalPayload,
            AiExecutionPolicy policy,
            String rawModelOutput
    ) throws Exception {
        JSONObject repairPayload = new JSONObject();
        repairPayload.put("model", preparedRequest.getModel());
        repairPayload.put("temperature", 0);
        int originalMaxTokens = originalPayload.optInt("max_tokens", 0);
        repairPayload.put("max_tokens", Math.max(originalMaxTokens, MIN_REPAIR_MAX_TOKENS));
        if (originalPayload.has("response_format")
                && AiModelCapabilityRegistry.resolve(preparedRequest.getModel()).supportsResponseFormatJson()) {
            repairPayload.put("response_format", new JSONObject().put("type", "json_object"));
        }
        boolean supportsSystemRole = AiModelCapabilityRegistry.resolve(preparedRequest.getModel()).supportsSystemRole();
        repairPayload.put(
                "messages",
                buildRepairMessages(rawModelOutput, supportsSystemRole)
        );
        return repairPayload;
    }

    private JSONArray buildRepairMessages(String rawModelOutput, boolean supportsSystemRole) throws Exception {
        JSONArray messages = new JSONArray();
        if (supportsSystemRole) {
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", AiPromptBuilder.repairSystemPrompt()));
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", AiPromptBuilder.repairUserPrompt(rawModelOutput)));
        } else {
            messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", AiPromptBuilder.repairSystemPrompt() + "\n\n" + AiPromptBuilder.repairUserPrompt(rawModelOutput)));
        }
        return messages;
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

    @Nullable
    private String extractFinishReason(JSONObject responseJson) {
        JSONArray choices = responseJson.optJSONArray("choices");
        if (choices == null || choices.length() == 0) {
            return null;
        }
        return choices.optJSONObject(0) == null
                ? null
                : choices.optJSONObject(0).optString("finish_reason", null);
    }

    private ProviderErrorDetails parseProviderErrorDetails(String responseBody) {
        if (TextUtils.isEmpty(responseBody)) {
            return new ProviderErrorDetails(null, "empty error response");
        }
        try {
            JSONObject json = new JSONObject(responseBody);
            JSONObject error = json.optJSONObject("error");
            if (error == null) {
                return new ProviderErrorDetails(null, responseBody);
            }
            String code = error.optString("code", error.optString("type", ""));
            String message = error.optString("message", responseBody);
            return new ProviderErrorDetails(TextUtils.isEmpty(code) ? null : code, message);
        } catch (Exception ignored) {
            return new ProviderErrorDetails(null, responseBody);
        }
    }

    private AiGenerationException buildTokenExhaustedException(
            AiCloudResponse response,
            @Nullable Exception cause
    ) {
        return new AiGenerationException(
                AiFailureStage.TOKEN_EXHAUSTED,
                "AI response was truncated before a complete JSON object was returned",
                cause,
                response.getRequestMode(),
                response.getModel(),
                response.getMaxTokens(),
                response.getImageBytes(),
                response.isUsedResponseFormat(),
                response.getHttpStatus(),
                response.getProviderErrorCode(),
                response.getFinishReason()
        );
    }

    private boolean isResponseFormatUnsupported(AiGenerationException exception) {
        String haystack = ((exception.getProviderErrorCode() == null ? "" : exception.getProviderErrorCode()) + " "
                + (exception.getMessage() == null ? "" : exception.getMessage())).toLowerCase(Locale.ROOT);
        return haystack.contains("response_format")
                || haystack.contains("json_object")
                || haystack.contains("json schema")
                || haystack.contains("unsupported_response_format");
    }

    private boolean isSystemRoleUnsupported(AiGenerationException exception) {
        String haystack = ((exception.getProviderErrorCode() == null ? "" : exception.getProviderErrorCode()) + " "
                + (exception.getMessage() == null ? "" : exception.getMessage())).toLowerCase(Locale.ROOT);
        return haystack.contains("system role")
                || haystack.contains("unsupported role")
                || haystack.contains("role 'system'")
                || haystack.contains("messages[0].role");
    }

    private void logAttemptSuccess(
            AiExecutionPolicy policy,
            int attempt,
            int attemptLimit,
            CloudGenerationResult result
    ) {
        AiCloudResponse response = result.getResponse();
        if (response == null) {
            return;
        }
        Log.d(
                TAG,
                "AI attempt success operationKind=" + policy.getOperationKind()
                        + " costMode=" + policy.getCostMode()
                        + " requestMode=" + response.getRequestMode()
                        + " model=" + response.getModel()
                        + " attempt=" + attempt + "/" + attemptLimit
                        + " maxTokens=" + response.getMaxTokens()
                        + " imageBytes=" + response.getImageBytes()
                        + " usedResponseFormat=" + response.isUsedResponseFormat()
                        + " repairUsed=" + result.isRepairUsed()
                        + " finishReason=" + nullToEmpty(response.getFinishReason())
        );
    }

    private void logAttemptFailure(
            AiExecutionPolicy policy,
            int attempt,
            int attemptLimit,
            AiGenerationException exception
    ) {
        Log.w(
                TAG,
                "AI attempt failed operationKind=" + policy.getOperationKind()
                        + " costMode=" + policy.getCostMode()
                        + " requestMode=" + nullToEmpty(exception.getRequestMode())
                        + " model=" + nullToEmpty(exception.getModel())
                        + " attempt=" + attempt + "/" + attemptLimit
                        + " maxTokens=" + exception.getMaxTokens()
                        + " imageBytes=" + exception.getImageBytes()
                        + " usedResponseFormat=" + exception.isUsedResponseFormat()
                        + " failureStage=" + exception.getFailureStage()
                        + " httpStatus=" + exception.getHttpStatus()
                        + " finishReason=" + nullToEmpty(exception.getFinishReason())
                        + " providerErrorCode=" + nullToEmpty(exception.getProviderErrorCode())
                        + " message=" + nullToEmpty(exception.getMessage())
        );
    }

    private void logAttemptFailure(
            AiExecutionPolicy policy,
            int attempt,
            int attemptLimit,
            Exception exception,
            CloudRequestMode requestMode,
            String model
    ) {
        Log.w(
                TAG,
                "AI attempt failed operationKind=" + policy.getOperationKind()
                        + " costMode=" + policy.getCostMode()
                        + " requestMode=" + requestMode.name()
                        + " model=" + model
                        + " attempt=" + attempt + "/" + attemptLimit
                        + " failureStage=" + AiFailureStage.CLOUD_REQUEST
                        + " message=" + nullToEmpty(exception.getMessage())
        );
    }

    private String buildFailureMessage(@Nullable Exception exception, @Nullable AiFailureStage failureStage) {
        if (exception instanceof AiGenerationException) {
            AiGenerationException aiException = (AiGenerationException) exception;
            return "Cloud AI request failed"
                    + " requestMode=" + nullToEmpty(aiException.getRequestMode())
                    + " model=" + nullToEmpty(aiException.getModel())
                    + " failureStage=" + (failureStage == null ? aiException.getFailureStage() : failureStage)
                    + " httpStatus=" + aiException.getHttpStatus()
                    + " finishReason=" + nullToEmpty(aiException.getFinishReason())
                    + " providerErrorCode=" + nullToEmpty(aiException.getProviderErrorCode())
                    + " message=" + nullToEmpty(aiException.getMessage());
        }
        if (exception == null) {
            return "Cloud AI request failed";
        }
        return nullToEmpty(exception.getMessage());
    }

    private String nullToEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    private String mergePrimaryText(@Nullable String userText, @Nullable String ocrVisibleText) {
        boolean hasUserText = !TextUtils.isEmpty(userText);
        boolean hasOcrText = !TextUtils.isEmpty(ocrVisibleText);
        if (hasUserText && hasOcrText) {
            return userText.trim() + "\n" + ocrVisibleText.trim();
        }
        if (hasUserText) {
            return userText.trim();
        }
        if (hasOcrText) {
            return ocrVisibleText.trim();
        }
        return "";
    }

    private GenerationResult generateByRules(String userText, @Nullable Uri imageUri) {
        String ocrVisibleText = extractLocalOcrVisibleText(imageUri);
        String effectiveText = mergePrimaryText(userText, ocrVisibleText);
        boolean hasText = !TextUtils.isEmpty(effectiveText);
        boolean hasImage = imageUri != null;
        String suggestedCategoryCode = classifyCategoryCode(effectiveText, hasImage);

        String analysis;
        if (hasText && hasImage) {
            analysis = "已同时记录文字和截图，这是一条信息较完整的记忆。";
        } else if (hasText) {
            analysis = "已提取文字要点，关键词：" + buildTags(effectiveText);
        } else if (hasImage) {
            analysis = "已保存截图，建议之后补一句说明，便于回看。";
        } else {
            analysis = "未检测到可分析内容。";
        }

        String title;
        String summary;
        if (hasText) {
            title = cropSingleLine(effectiveText, 18);
            summary = cropSingleLine(effectiveText, 40);
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
            memory = timeText + " 记录了一条内容：" + cropSingleLine(effectiveText, 56);
        } else if (hasImage) {
            memory = timeText + " 保存了一张截图记忆。";
        } else {
            memory = timeText + " 创建了一条空白记忆草稿。";
        }

        String structuredFactsJson = reconcileSafely(
                effectiveText,
                mergeRawVisibleText("", ocrVisibleText),
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        suggestedCategoryCode = normalizeCategoryCodeSafely(suggestedCategoryCode, structuredFactsJson);
        summary = stableSummarySafely(suggestedCategoryCode, summary, structuredFactsJson);
        return new GenerationResult(title, summary, analysis, memory, suggestedCategoryCode, "local", structuredFactsJson);
    }

    private GenerationResult generateByRulesEnhanced(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext
    ) {
        String ocrVisibleText = extractLocalOcrVisibleText(imageUri);
        String effectiveText = mergePrimaryText(userText, ocrVisibleText);
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
                effectiveText,
                mergeRawVisibleText(base.getStructuredFactsJson(), ocrVisibleText),
                base.getTitle(),
                base.getSummary(),
                analysis,
                memory,
                base.getSuggestedCategoryCode()
        );
        String normalizedCategoryCode = normalizeCategoryCodeSafely(
                base.getSuggestedCategoryCode(),
                structuredFactsJson
        );
        String summary = stableSummarySafely(
                normalizedCategoryCode,
                base.getSummary(),
                structuredFactsJson
        );
        return new GenerationResult(
                base.getTitle(),
                summary,
                analysis,
                memory,
                normalizedCategoryCode,
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

    private String normalizeCategoryCodeSafely(
            @Nullable String categoryCode,
            @Nullable String structuredFactsJson
    ) {
        try {
            return MemoryFactReconciler.normalizeCategoryCode(categoryCode, structuredFactsJson);
        } catch (Exception ignored) {
            return categoryCode == null ? "" : categoryCode;
        }
    }

    private GenerationResult buildEmergencyLocalResult(
            String userText,
            @Nullable Uri imageUri,
            boolean enhanced,
            @Nullable String detailContext
    ) {
        String ocrVisibleText = extractLocalOcrVisibleText(imageUri);
        String effectiveText = mergePrimaryText(userText, ocrVisibleText);
        String suggestedCategoryCode = classifyCategoryCode(effectiveText, imageUri != null);
        String memory = fallbackMemory(effectiveText, imageUri);
        String title = cropSingleLine(memory, 18);
        String summary = cropSingleLine(TextUtils.isEmpty(effectiveText) ? memory : effectiveText, 40);
        String analysis = enhanced
                ? "已完成本地兜底重整"
                : "已完成本地兜底分析";
        if (enhanced && !TextUtils.isEmpty(detailContext)) {
            analysis = analysis + "，并已参考现有内容。";
        }
        String structuredFactsJson = reconcileSafely(
                effectiveText,
                mergeRawVisibleText("", ocrVisibleText),
                title,
                summary,
                analysis,
                memory,
                suggestedCategoryCode
        );
        suggestedCategoryCode = normalizeCategoryCodeSafely(suggestedCategoryCode, structuredFactsJson);
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

    private static final class CloudGenerationResult {
        private final GenerationResult result;
        private final boolean repairUsed;
        @Nullable
        private final AiCloudResponse response;

        private CloudGenerationResult(
                GenerationResult result,
                boolean repairUsed,
                @Nullable AiCloudResponse response
        ) {
            this.result = result;
            this.repairUsed = repairUsed;
            this.response = response;
        }

        private GenerationResult getResult() {
            return result;
        }

        private boolean isRepairUsed() {
            return repairUsed;
        }

        @Nullable
        private AiCloudResponse getResponse() {
            return response;
        }
    }

    private static final class BuiltMessages {
        private final JSONArray messages;
        private final int imageBytes;

        private BuiltMessages(JSONArray messages, int imageBytes) {
            this.messages = messages;
            this.imageBytes = Math.max(0, imageBytes);
        }

        private JSONArray getMessages() {
            return messages;
        }

        private int getImageBytes() {
            return imageBytes;
        }
    }

    private static final class EncodedImagePayload {
        private final String dataUri;
        private final int byteSize;

        private EncodedImagePayload(String dataUri, int byteSize) {
            this.dataUri = dataUri;
            this.byteSize = Math.max(0, byteSize);
        }

        private String getDataUri() {
            return dataUri;
        }

        private int getByteSize() {
            return byteSize;
        }
    }

    private static final class AiPreparedRequest {
        private final CloudRequestMode requestMode;
        private final String model;
        private final AiPromptSpec promptSpec;
        private final JSONObject payload;
        private final int imageBytes;

        private AiPreparedRequest(
                CloudRequestMode requestMode,
                String model,
                AiPromptSpec promptSpec,
                JSONObject payload,
                int imageBytes
        ) {
            this.requestMode = requestMode;
            this.model = model;
            this.promptSpec = promptSpec;
            this.payload = payload;
            this.imageBytes = Math.max(0, imageBytes);
        }

        private CloudRequestMode getRequestMode() {
            return requestMode;
        }

        private String getModel() {
            return model;
        }

        private AiPromptSpec getPromptSpec() {
            return promptSpec;
        }

        private JSONObject getPayload() {
            return payload;
        }

        private int getImageBytes() {
            return imageBytes;
        }
    }

    private static final class LocalEvidenceBundle {
        private final String localCandidatesJson;
        @Nullable
        private final String ocrVisibleText;

        private LocalEvidenceBundle(String localCandidatesJson, @Nullable String ocrVisibleText) {
            this.localCandidatesJson = TextUtils.isEmpty(localCandidatesJson) ? "{}" : localCandidatesJson;
            this.ocrVisibleText = TextUtils.isEmpty(ocrVisibleText) ? null : ocrVisibleText;
        }

        private String getLocalCandidatesJson() {
            return localCandidatesJson;
        }

        @Nullable
        private String getOcrVisibleText() {
            return ocrVisibleText;
        }
    }

    private static final class AiCloudResponse {
        private final String content;
        private final int httpStatus;
        @Nullable
        private final String providerErrorCode;
        @Nullable
        private final String finishReason;
        private final boolean usedResponseFormat;
        private final String model;
        private final String requestMode;
        private final int maxTokens;
        private final int imageBytes;

        private AiCloudResponse(
                String content,
                int httpStatus,
                @Nullable String providerErrorCode,
                @Nullable String finishReason,
                boolean usedResponseFormat,
                String model,
                String requestMode,
                int maxTokens,
                int imageBytes
        ) {
            this.content = content == null ? "" : content;
            this.httpStatus = httpStatus;
            this.providerErrorCode = providerErrorCode;
            this.finishReason = finishReason;
            this.usedResponseFormat = usedResponseFormat;
            this.model = model;
            this.requestMode = requestMode;
            this.maxTokens = Math.max(0, maxTokens);
            this.imageBytes = Math.max(0, imageBytes);
        }

        private String getContent() {
            return content;
        }

        private int getHttpStatus() {
            return httpStatus;
        }

        @Nullable
        private String getProviderErrorCode() {
            return providerErrorCode;
        }

        @Nullable
        private String getFinishReason() {
            return finishReason;
        }

        private boolean isUsedResponseFormat() {
            return usedResponseFormat;
        }

        private String getModel() {
            return model;
        }

        private String getRequestMode() {
            return requestMode;
        }

        private int getMaxTokens() {
            return maxTokens;
        }

        private int getImageBytes() {
            return imageBytes;
        }
    }

    private static final class ProviderErrorDetails {
        @Nullable
        private final String code;
        private final String message;

        private ProviderErrorDetails(@Nullable String code, String message) {
            this.code = code;
            this.message = message == null ? "" : message;
        }

        @Nullable
        private String getCode() {
            return code;
        }

        private String getMessage() {
            return message;
        }
    }

    private static final class AiGenerationException extends Exception {
        private final AiFailureStage failureStage;
        @Nullable
        private final String requestMode;
        @Nullable
        private final String model;
        private final int maxTokens;
        private final int imageBytes;
        private final boolean usedResponseFormat;
        private final int httpStatus;
        @Nullable
        private final String providerErrorCode;
        @Nullable
        private final String finishReason;

        private AiGenerationException(
                AiFailureStage failureStage,
                String message,
                @Nullable Throwable cause
        ) {
            this(failureStage, message, cause, null, null, 0, 0, false, 0, null, null);
        }

        private AiGenerationException(
                AiFailureStage failureStage,
                String message,
                @Nullable Throwable cause,
                @Nullable String requestMode,
                @Nullable String model,
                int maxTokens,
                int imageBytes,
                boolean usedResponseFormat,
                int httpStatus,
                @Nullable String providerErrorCode,
                @Nullable String finishReason
        ) {
            super(message, cause);
            this.failureStage = failureStage;
            this.requestMode = requestMode;
            this.model = model;
            this.maxTokens = Math.max(0, maxTokens);
            this.imageBytes = Math.max(0, imageBytes);
            this.usedResponseFormat = usedResponseFormat;
            this.httpStatus = Math.max(0, httpStatus);
            this.providerErrorCode = providerErrorCode;
            this.finishReason = finishReason;
        }

        private AiFailureStage getFailureStage() {
            return failureStage;
        }

        @Nullable
        private String getRequestMode() {
            return requestMode;
        }

        @Nullable
        private String getModel() {
            return model;
        }

        private int getMaxTokens() {
            return maxTokens;
        }

        private int getImageBytes() {
            return imageBytes;
        }

        private boolean isUsedResponseFormat() {
            return usedResponseFormat;
        }

        private int getHttpStatus() {
            return httpStatus;
        }

        @Nullable
        private String getProviderErrorCode() {
            return providerErrorCode;
        }

        @Nullable
        private String getFinishReason() {
            return finishReason;
        }
    }
}
