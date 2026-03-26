package com.han.nomemo

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

object WindowStyleManager {
    fun apply(activity: Activity, config: AppWindowStyleConfig) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, true)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.statusBarColor = config.statusBarColor
        window.navigationBarColor = config.navigationBarColor

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = config.lightStatusBar
        insetsController.isAppearanceLightNavigationBars = config.lightNavigationBar
    }
}
