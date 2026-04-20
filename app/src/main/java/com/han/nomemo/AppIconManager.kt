package com.han.nomemo

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object AppIconManager {

    private const val ALIAS_LIGHT = "com.han.nomemo.MainActivityLight"
    private const val ALIAS_DARK = "com.han.nomemo.MainActivityDark"

    fun getCurrentIconStyle(context: Context): String {
        val pm = context.packageManager
        val lightEnabled = pm.getComponentEnabledSetting(
            ComponentName(context, ALIAS_LIGHT)
        )
        val darkEnabled = pm.getComponentEnabledSetting(
            ComponentName(context, ALIAS_DARK)
        )

        return when {
            darkEnabled == PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> SettingsStore.ICON_STYLE_DARK
            else -> SettingsStore.ICON_STYLE_LIGHT
        }
    }

    fun setIconStyle(context: Context, style: String) {
        val pm = context.packageManager

        val lightState = if (style == SettingsStore.ICON_STYLE_LIGHT) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        val darkState = if (style == SettingsStore.ICON_STYLE_DARK) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        pm.setComponentEnabledSetting(
            ComponentName(context, ALIAS_LIGHT),
            lightState,
            PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            ComponentName(context, ALIAS_DARK),
            darkState,
            PackageManager.DONT_KILL_APP
        )
    }
}
