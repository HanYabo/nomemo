package com.han.nomemo;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

public final class AiAnalysisOrchestrator {
    private final SettingsStore settingsStore;
    private final AiMemoryService aiMemoryService;

    public AiAnalysisOrchestrator(Context context) {
        Context appContext = context.getApplicationContext();
        this.settingsStore = new SettingsStore(appContext);
        this.aiMemoryService = new AiMemoryService(appContext);
    }

    public AiExecutionPolicy initialPolicy() {
        return AiAnalysisPolicies.resolve(settingsStore, AiOperationKind.INITIAL_ANALYSIS);
    }

    public AiExecutionPolicy reanalyzePolicy() {
        return AiAnalysisPolicies.resolve(settingsStore, AiOperationKind.REANALYZE);
    }

    public AiAnalysisOutcome runInitialAnalysis(
            String userText,
            @Nullable Uri imageUri,
            @Nullable AiMemoryService.AttemptListener attemptListener
    ) {
        return aiMemoryService.analyzeWithPolicy(
                userText,
                imageUri,
                false,
                null,
                attemptListener,
                initialPolicy()
        );
    }

    public AiAnalysisOutcome runReanalysis(
            String userText,
            @Nullable Uri imageUri,
            @Nullable String detailContext,
            @Nullable AiMemoryService.AttemptListener attemptListener
    ) {
        return aiMemoryService.analyzeWithPolicy(
                userText,
                imageUri,
                true,
                detailContext,
                attemptListener,
                reanalyzePolicy()
        );
    }
}
