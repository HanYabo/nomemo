package com.han.nomemo

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color

data class AppWindowStyleConfig(
    val statusBarColor: Int,
    val navigationBarColor: Int,
    val lightStatusBar: Boolean,
    val lightNavigationBar: Boolean
)

data class AppAnimationConfig(
    val quickDurationMs: Int = 220,
    val normalDurationMs: Int = 320,
    val springDampingRatio: Float = 0.72f
)

data class AppLayoutConfig(
    val compactBreakpointDp: Int = 420,
    val mediumBreakpointDp: Int = 700,
    val expandedBreakpointDp: Int = 900
)

object UiConfig {
    val animation: AppAnimationConfig = AppAnimationConfig()
    val layout: AppLayoutConfig = AppLayoutConfig()

    fun windowStyleFor(activity: Activity): AppWindowStyleConfig {
        val isNight = (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        return AppWindowStyleConfig(
            statusBarColor = Color.TRANSPARENT,
            navigationBarColor = Color.TRANSPARENT,
            lightStatusBar = !isNight,
            lightNavigationBar = !isNight
        )
    }
}

