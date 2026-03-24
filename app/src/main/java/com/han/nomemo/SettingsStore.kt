package com.han.nomemo

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var apiBaseUrl: String
        get() = prefs.getString(KEY_API_BASE_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_BASE_URL, value.trim()).apply()

    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var apiModel: String
        get() = prefs.getString(KEY_API_MODEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_MODEL, value.trim()).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var visualIntensity: String
        get() = prefs.getString(KEY_VISUAL_INTENSITY, VISUAL_NORMAL) ?: VISUAL_NORMAL
        set(value) = prefs.edit().putString(KEY_VISUAL_INTENSITY, value).apply()

    fun resolvedApiBaseUrl(): String {
        return apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }
    }

    fun resolvedApiKey(): String {
        return apiKey.ifBlank { BuildConfig.OPENAI_API_KEY }
    }

    fun resolvedApiModel(): String {
        return apiModel.ifBlank { BuildConfig.OPENAI_MODEL }
    }

    fun glassAlphaMultiplier(): Float {
        return when (visualIntensity) {
            VISUAL_SOFT -> 0.82f
            VISUAL_STRONG -> 1.2f
            else -> 1f
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
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_API_MODEL = "api_model"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_VISUAL_INTENSITY = "visual_intensity"

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

        const val VISUAL_SOFT = "SOFT"
        const val VISUAL_NORMAL = "NORMAL"
        const val VISUAL_STRONG = "STRONG"

        @JvmStatic
        fun applyTheme(context: Context) {
            SettingsStore(context).applyThemeMode()
        }
    }
}
