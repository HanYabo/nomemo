package com.han.nomemo;

public final class AiAnalysisPolicies {
    private static final int STANDARD_TOTAL_ATTEMPTS_WITH_RETRY = 7;
    private static final int ECONOMY_TOTAL_ATTEMPTS_WITH_RETRY = 2;

    private AiAnalysisPolicies() {
    }

    public static AiExecutionPolicy resolve(SettingsStore settingsStore, AiOperationKind operationKind) {
        boolean economyMode = settingsStore.getEconomyMode();
        boolean autoRetry = settingsStore.getAutoRetry();
        return resolve(economyMode, autoRetry, operationKind);
    }

    public static AiExecutionPolicy resolve(
            boolean economyMode,
            boolean autoRetry,
            AiOperationKind operationKind
    ) {
        AiCostMode costMode = economyMode ? AiCostMode.ECONOMY : AiCostMode.STANDARD;
        int cloudAttemptLimit = economyMode
                ? (autoRetry ? ECONOMY_TOTAL_ATTEMPTS_WITH_RETRY : 1)
                : (autoRetry ? STANDARD_TOTAL_ATTEMPTS_WITH_RETRY : 1);
        boolean allowFullPromptRescue = economyMode;
        boolean allowLocalFallback = operationKind == AiOperationKind.INITIAL_ANALYSIS;
        return new AiExecutionPolicy(
                operationKind,
                costMode,
                cloudAttemptLimit,
                allowFullPromptRescue,
                allowLocalFallback
        );
    }

    public static AiExecutionPolicy restore(
            AiOperationKind operationKind,
            AiCostMode costMode,
            int totalAttemptLimit
    ) {
        boolean allowFullPromptRescue = costMode == AiCostMode.ECONOMY;
        boolean allowLocalFallback = operationKind == AiOperationKind.INITIAL_ANALYSIS;
        int normalizedTotal = Math.max(1, totalAttemptLimit);
        int cloudAttemptLimit = allowFullPromptRescue
                ? Math.max(1, normalizedTotal - 1)
                : normalizedTotal;
        return new AiExecutionPolicy(
                operationKind,
                costMode,
                cloudAttemptLimit,
                allowFullPromptRescue,
                allowLocalFallback
        );
    }
}
