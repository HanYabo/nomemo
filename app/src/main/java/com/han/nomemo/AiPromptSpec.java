package com.han.nomemo;

import androidx.annotation.Nullable;

public final class AiPromptSpec {
    private final AiPromptMode mode;
    private final AiAnalysisStyleHint analysisStyleHint;
    private final String promptVersion;
    private final String schemaVersion;
    private final String systemPrompt;
    private final String userPrompt;
    private final Integer maxTokens;
    private final double temperature;
    private final int imageMaxSize;
    private final int imageQuality;

    public AiPromptSpec(
            AiPromptMode mode,
            AiAnalysisStyleHint analysisStyleHint,
            String promptVersion,
            String schemaVersion,
            String systemPrompt,
            String userPrompt,
            @Nullable Integer maxTokens,
            double temperature,
            int imageMaxSize,
            int imageQuality
    ) {
        this.mode = mode;
        this.analysisStyleHint = analysisStyleHint;
        this.promptVersion = promptVersion;
        this.schemaVersion = schemaVersion;
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.imageMaxSize = imageMaxSize;
        this.imageQuality = imageQuality;
    }

    public AiPromptMode getMode() {
        return mode;
    }

    public AiAnalysisStyleHint getAnalysisStyleHint() {
        return analysisStyleHint;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    @Nullable
    public Integer getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getImageMaxSize() {
        return imageMaxSize;
    }

    public int getImageQuality() {
        return imageQuality;
    }
}
