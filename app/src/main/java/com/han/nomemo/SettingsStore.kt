package com.han.nomemo

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var aiEnabled: Boolean
        get() = prefs.getBoolean(KEY_AI_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_AI_ENABLED, value).apply()

    var aiConfigVerified: Boolean
        get() = prefs.getBoolean(KEY_AI_CONFIG_VERIFIED, false)
        set(value) = prefs.edit().putBoolean(KEY_AI_CONFIG_VERIFIED, value).apply()

    var apiBaseUrl: String
        get() = prefs.getString(KEY_API_BASE_URL, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_API_BASE_URL, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_API_KEY, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var apiModel: String
        get() = prefs.getString(KEY_API_MODEL, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_API_MODEL, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var imageCustomModel: String
        get() = prefs.getString(KEY_IMAGE_CUSTOM_MODEL, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_IMAGE_CUSTOM_MODEL, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var textCustomModel: String
        get() = prefs.getString(KEY_TEXT_CUSTOM_MODEL, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_TEXT_CUSTOM_MODEL, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var multimodalCustomModel: String
        get() = prefs.getString(KEY_MULTIMODAL_CUSTOM_MODEL, "") ?: ""
        set(value) {
            prefs.edit()
                .putString(KEY_MULTIMODAL_CUSTOM_MODEL, value.trim())
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var imageModelPreset: String
        get() = prefs.getString(KEY_IMAGE_MODEL_PRESET, MODEL_IMAGE_DEFAULT) ?: MODEL_IMAGE_DEFAULT
        set(value) {
            prefs.edit()
                .putString(KEY_IMAGE_MODEL_PRESET, value)
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var textModelPreset: String
        get() = prefs.getString(KEY_TEXT_MODEL_PRESET, MODEL_TEXT_DEFAULT) ?: MODEL_TEXT_DEFAULT
        set(value) {
            prefs.edit()
                .putString(KEY_TEXT_MODEL_PRESET, value)
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var multimodalModelPreset: String
        get() = prefs.getString(KEY_MULTIMODAL_MODEL_PRESET, MODEL_MULTIMODAL_DEFAULT) ?: MODEL_MULTIMODAL_DEFAULT
        set(value) {
            prefs.edit()
                .putString(KEY_MULTIMODAL_MODEL_PRESET, value)
                .putBoolean(KEY_AI_CONFIG_VERIFIED, false)
                .apply()
        }

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var themeGlobalEnabled: Boolean
        get() = prefs.getBoolean(KEY_THEME_GLOBAL_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_THEME_GLOBAL_ENABLED, value).apply()

    var showDividers: Boolean
        get() = prefs.getBoolean(KEY_SHOW_DIVIDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_DIVIDERS, value).apply()

    var themeAccent: String
        get() = prefs.getString(KEY_THEME_ACCENT, THEME_ACCENT_DEFAULT) ?: THEME_ACCENT_DEFAULT
        set(value) = prefs.edit().putString(KEY_THEME_ACCENT, value).apply()

    var autoRetry: Boolean
        get() = prefs.getBoolean(KEY_AUTO_RETRY, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_RETRY, value).apply()

    var economyMode: Boolean
        get() = prefs.getBoolean(KEY_ECONOMY_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_ECONOMY_MODE, value).apply()

    fun resolvedApiBaseUrl(): String {
        return apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }
    }

    fun resolvedApiKey(): String {
        return apiKey.ifBlank { BuildConfig.OPENAI_API_KEY }
    }

    fun resolvedApiModel(): String {
        return apiModel.ifBlank { BuildConfig.OPENAI_MODEL }
    }

    fun resolvedImageModel(): String {
        return resolveModelByPreset(imageModelPreset, MODEL_IMAGE_DEFAULT, imageCustomModel)
    }

    fun resolvedTextModel(): String {
        return resolveModelByPreset(textModelPreset, MODEL_TEXT_DEFAULT, textCustomModel)
    }

    fun resolvedMultimodalModel(): String {
        return resolveModelByPreset(multimodalModelPreset, MODEL_MULTIMODAL_DEFAULT, multimodalCustomModel)
    }

    fun isAiAvailable(): Boolean {
        return aiEnabled &&
            aiConfigVerified &&
            resolvedApiBaseUrl().isNotBlank() &&
            resolvedApiKey().isNotBlank() &&
            resolvedApiModel().isNotBlank()
    }

    private fun resolveModelByPreset(preset: String, fallback: String, customValue: String): String {
        return when (preset) {
            MODEL_PRESET_CUSTOM -> customValue.ifBlank { resolvedApiModel() }
            MODEL_IMAGE_FLASH -> MODEL_IMAGE_FLASH
            MODEL_IMAGE_THINKING -> MODEL_IMAGE_THINKING
            MODEL_TEXT_FLASH -> MODEL_TEXT_FLASH
            MODEL_TEXT_FLASH_47 -> MODEL_TEXT_FLASH_47
            MODEL_MULTIMODAL_FLASH -> MODEL_MULTIMODAL_FLASH
            else -> fallback
        }
    }

    fun applyThemeMode() {
        AppCompatDelegate.setDefaultNightMode(
            when (themeMode) {
                THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    companion object {
        const val PREF_NAME = "no_memo_settings"
        private const val KEY_AI_ENABLED = "ai_enabled"
        private const val KEY_AI_CONFIG_VERIFIED = "ai_config_verified"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_API_MODEL = "api_model"
        private const val KEY_IMAGE_CUSTOM_MODEL = "image_custom_model"
        private const val KEY_TEXT_CUSTOM_MODEL = "text_custom_model"
        private const val KEY_MULTIMODAL_CUSTOM_MODEL = "multimodal_custom_model"
        private const val KEY_IMAGE_MODEL_PRESET = "image_model_preset"
        private const val KEY_TEXT_MODEL_PRESET = "text_model_preset"
        private const val KEY_MULTIMODAL_MODEL_PRESET = "multimodal_model_preset"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_THEME_GLOBAL_ENABLED = "theme_global_enabled"
        private const val KEY_SHOW_DIVIDERS = "show_dividers"
        private const val KEY_THEME_ACCENT = "theme_accent"
        private const val KEY_AUTO_RETRY = "auto_retry"
        private const val KEY_ECONOMY_MODE = "economy_mode"

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

        const val THEME_ACCENT_DEFAULT = "default"
        const val THEME_ACCENT_WARM_GRAY = "warm_gray"
        const val THEME_ACCENT_NOTE_YELLOW = "note_yellow"
        const val THEME_ACCENT_SAKURA_PINK = "sakura_pink"
        const val THEME_ACCENT_SKY_BLUE = "sky_blue"
        const val THEME_ACCENT_MINT_GREEN = "mint_green"
        const val THEME_ACCENT_PEACH_ORANGE = "peach_orange"
        const val THEME_ACCENT_LAVENDER_PURPLE = "lavender_purple"

        const val MODEL_PRESET_CUSTOM = "custom"

        const val MODEL_IMAGE_FLASH = "GLM-4.6V-Flash"
        const val MODEL_IMAGE_THINKING = "GLM-4.1V-Thinking-Flash"
        const val MODEL_TEXT_FLASH = "GLM-4.6-Flash"
        const val MODEL_TEXT_FLASH_47 = "GLM-4.7-Flash"
        const val MODEL_MULTIMODAL_FLASH = "GLM-4.6V-Flash"

        const val MODEL_IMAGE_DEFAULT = MODEL_IMAGE_FLASH
        const val MODEL_TEXT_DEFAULT = MODEL_TEXT_FLASH
        const val MODEL_MULTIMODAL_DEFAULT = MODEL_MULTIMODAL_FLASH

        @JvmStatic
        fun applyTheme(context: Context) {
            SettingsStore(context).applyThemeMode()
        }
    }
}
