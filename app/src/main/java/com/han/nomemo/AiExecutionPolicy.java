package com.han.nomemo;

public final class AiExecutionPolicy {
    private final AiOperationKind operationKind;
    private final AiCostMode costMode;
    private final int cloudAttemptLimit;
    private final boolean allowFullPromptRescue;
    private final boolean allowLocalFallback;

    public AiExecutionPolicy(
            AiOperationKind operationKind,
            AiCostMode costMode,
            int cloudAttemptLimit,
            boolean allowFullPromptRescue,
            boolean allowLocalFallback
    ) {
        this.operationKind = operationKind;
        this.costMode = costMode;
        this.cloudAttemptLimit = Math.max(1, cloudAttemptLimit);
        this.allowFullPromptRescue = allowFullPromptRescue;
        this.allowLocalFallback = allowLocalFallback;
    }

    public AiOperationKind getOperationKind() {
        return operationKind;
    }

    public AiCostMode getCostMode() {
        return costMode;
    }

    public int getCloudAttemptLimit() {
        return cloudAttemptLimit;
    }

    public boolean isAllowFullPromptRescue() {
        return allowFullPromptRescue;
    }

    public boolean isAllowLocalFallback() {
        return allowLocalFallback;
    }

    public int getTotalAttemptLimit() {
        return cloudAttemptLimit + (allowFullPromptRescue ? 1 : 0);
    }

    public boolean requiresCloudSuccess() {
        return !allowLocalFallback;
    }
}
