package com.han.nomemo

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat

object WindowStyleManager {
    fun apply(activity: Activity, config: AppWindowStyleConfig) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = config.statusBarColor
        window.navigationBarColor = config.navigationBarColor

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = config.lightStatusBar
        insetsController.isAppearanceLightNavigationBars = config.lightNavigationBar
    }
}

