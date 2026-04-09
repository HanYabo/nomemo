package com.han.nomemo

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var aiEnabled: Boolean
        get() = prefs.getBoolean(KEY_AI_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_AI_ENABLED, value).apply()

    var apiBaseUrl: String
        get() = prefs.getString(KEY_API_BASE_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_BASE_URL, value.trim()).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var apiModel: String
        get() = prefs.getString(KEY_API_MODEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_MODEL, value.trim()).apply()

    var imageModelPreset: String
        get() = prefs.getString(KEY_IMAGE_MODEL_PRESET, MODEL_IMAGE_DEFAULT) ?: MODEL_IMAGE_DEFAULT
        set(value) = prefs.edit().putString(KEY_IMAGE_MODEL_PRESET, value).apply()

    var textModelPreset: String
        get() = prefs.getString(KEY_TEXT_MODEL_PRESET, MODEL_TEXT_DEFAULT) ?: MODEL_TEXT_DEFAULT
        set(value) = prefs.edit().putString(KEY_TEXT_MODEL_PRESET, value).apply()

    var multimodalModelPreset: String
        get() = prefs.getString(KEY_MULTIMODAL_MODEL_PRESET, MODEL_MULTIMODAL_DEFAULT) ?: MODEL_MULTIMODAL_DEFAULT
        set(value) = prefs.edit().putString(KEY_MULTIMODAL_MODEL_PRESET, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

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
        return resolveModelByPreset(imageModelPreset, MODEL_IMAGE_DEFAULT)
    }

    fun resolvedTextModel(): String {
        return resolveModelByPreset(textModelPreset, MODEL_TEXT_DEFAULT)
    }

    fun resolvedMultimodalModel(): String {
        return resolveModelByPreset(multimodalModelPreset, MODEL_MULTIMODAL_DEFAULT)
    }

    private fun resolveModelByPreset(preset: String, fallback: String): String {
        return when (preset) {
            MODEL_PRESET_CUSTOM -> resolvedApiModel()
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
        private const val PREF_NAME = "no_memo_settings"
        private const val KEY_AI_ENABLED = "ai_enabled"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_API_MODEL = "api_model"
        private const val KEY_IMAGE_MODEL_PRESET = "image_model_preset"
        private const val KEY_TEXT_MODEL_PRESET = "text_model_preset"
        private const val KEY_MULTIMODAL_MODEL_PRESET = "multimodal_model_preset"
        private const val KEY_THEME_MODE = "theme_mode"

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

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
