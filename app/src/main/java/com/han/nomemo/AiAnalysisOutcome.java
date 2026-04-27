package com.han.nomemo;

import androidx.annotation.Nullable;

public final class AiAnalysisOutcome {
    private final AiAnalysisStatus status;
    @Nullable
    private final AiMemoryService.GenerationResult generationResult;
    private final int attemptCount;
    private final int attemptLimit;
    private final boolean repairUsed;
    private final boolean fullPromptRescueUsed;
    @Nullable
    private final String structuredFactsJson;
    @Nullable
    private final AiFailureStage failureStage;
    @Nullable
    private final String failureMessage;

    private AiAnalysisOutcome(
            AiAnalysisStatus status,
            @Nullable AiMemoryService.GenerationResult generationResult,
            int attemptCount,
            int attemptLimit,
            boolean repairUsed,
            boolean fullPromptRescueUsed,
            @Nullable String structuredFactsJson,
            @Nullable AiFailureStage failureStage,
            @Nullable String failureMessage
    ) {
        this.status = status;
        this.generationResult = generationResult;
        this.attemptCount = Math.max(0, attemptCount);
        this.attemptLimit = Math.max(1, attemptLimit);
        this.repairUsed = repairUsed;
        this.fullPromptRescueUsed = fullPromptRescueUsed;
        this.structuredFactsJson = structuredFactsJson;
        this.failureStage = failureStage;
        this.failureMessage = failureMessage;
    }

    public static AiAnalysisOutcome success(
            AiMemoryService.GenerationResult generationResult,
            int attemptCount,
            int attemptLimit,
            boolean repairUsed,
            boolean fullPromptRescueUsed
    ) {
        String engine = generationResult == null ? "" : generationResult.getEngine();
        AiAnalysisStatus status = "local".equalsIgnoreCase(engine)
                ? AiAnalysisStatus.SUCCESS_LOCAL
                : AiAnalysisStatus.SUCCESS_CLOUD;
        String structuredFactsJson = generationResult == null ? "" : generationResult.getStructuredFactsJson();
        return new AiAnalysisOutcome(
                status,
                generationResult,
                attemptCount,
                attemptLimit,
                repairUsed,
                fullPromptRescueUsed,
                structuredFactsJson,
                null,
                null
        );
    }

    public static AiAnalysisOutcome failure(
            int attemptCount,
            int attemptLimit,
            boolean repairUsed,
            boolean fullPromptRescueUsed,
            @Nullable AiFailureStage failureStage,
            @Nullable String failureMessage
    ) {
        return new AiAnalysisOutcome(
                AiAnalysisStatus.FAILED,
                null,
                attemptCount,
                attemptLimit,
                repairUsed,
                fullPromptRescueUsed,
                null,
                failureStage,
                failureMessage
        );
    }

    public AiAnalysisStatus getStatus() {
        return status;
    }

    @Nullable
    public AiMemoryService.GenerationResult getGenerationResult() {
        return generationResult;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public int getAttemptLimit() {
        return attemptLimit;
    }

    public boolean isRepairUsed() {
        return repairUsed;
    }

    public boolean isFullPromptRescueUsed() {
        return fullPromptRescueUsed;
    }

    @Nullable
    public String getStructuredFactsJson() {
        return structuredFactsJson;
    }

    @Nullable
    public AiFailureStage getFailureStage() {
        return failureStage;
    }

    @Nullable
    public String getFailureMessage() {
        return failureMessage;
    }

    public boolean isSuccess() {
        return generationResult != null && status != AiAnalysisStatus.FAILED;
    }

    public String getEngine() {
        return generationResult == null ? "" : generationResult.getEngine();
    }
}
