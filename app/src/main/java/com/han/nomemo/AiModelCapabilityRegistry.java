package com.han.nomemo;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class AiModelCapabilityRegistry {
    private static final Map<String, ModelCapabilities> RUNTIME_OVERRIDES = new ConcurrentHashMap<>();

    private AiModelCapabilityRegistry() {
    }

    static ModelCapabilities resolve(String modelName) {
        String normalized = normalize(modelName);
        ModelCapabilities override = RUNTIME_OVERRIDES.get(normalized);
        if (override != null) {
            return override;
        }
        return defaultCapabilities(normalized);
    }

    static void markResponseFormatUnsupported(String modelName) {
        String normalized = normalize(modelName);
        ModelCapabilities base = resolve(normalized);
        RUNTIME_OVERRIDES.put(normalized, base.withResponseFormatJson(false));
    }

    static void markSystemRoleUnsupported(String modelName) {
        String normalized = normalize(modelName);
        ModelCapabilities base = resolve(normalized);
        RUNTIME_OVERRIDES.put(normalized, base.withSystemRole(false));
    }

    static void clearRuntimeOverridesForTest() {
        RUNTIME_OVERRIDES.clear();
    }

    private static ModelCapabilities defaultCapabilities(String normalizedModel) {
        if (normalizedModel.isEmpty()) {
            return ModelCapabilities.allEnabled();
        }
        if (normalizedModel.contains("glm-4.6v") || normalizedModel.contains("glm-4.1v")) {
            return new ModelCapabilities(true, true, true);
        }
        if (normalizedModel.contains("glm-4.6-flash") || normalizedModel.contains("glm-4.7-flash")) {
            return new ModelCapabilities(false, true, true);
        }
        return ModelCapabilities.allEnabled();
    }

    private static String normalize(String modelName) {
        return modelName == null ? "" : modelName.trim().toLowerCase(Locale.ROOT);
    }

    static final class ModelCapabilities {
        private final boolean supportsImageInput;
        private final boolean supportsResponseFormatJson;
        private final boolean supportsSystemRole;

        ModelCapabilities(
                boolean supportsImageInput,
                boolean supportsResponseFormatJson,
                boolean supportsSystemRole
        ) {
            this.supportsImageInput = supportsImageInput;
            this.supportsResponseFormatJson = supportsResponseFormatJson;
            this.supportsSystemRole = supportsSystemRole;
        }

        static ModelCapabilities allEnabled() {
            return new ModelCapabilities(true, true, true);
        }

        boolean supportsImageInput() {
            return supportsImageInput;
        }

        boolean supportsResponseFormatJson() {
            return supportsResponseFormatJson;
        }

        boolean supportsSystemRole() {
            return supportsSystemRole;
        }

        @NonNull
        ModelCapabilities withResponseFormatJson(boolean enabled) {
            return new ModelCapabilities(supportsImageInput, enabled, supportsSystemRole);
        }

        @NonNull
        ModelCapabilities withSystemRole(boolean enabled) {
            return new ModelCapabilities(supportsImageInput, supportsResponseFormatJson, enabled);
        }
    }
}
