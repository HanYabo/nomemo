package com.han.nomemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseComposeActivity : AppCompatActivity() {
    open fun provideWindowStyleConfig(): AppWindowStyleConfig = UiConfig.windowStyleFor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingsStore.applyTheme(this)
        super.onCreate(savedInstanceState)
        WindowStyleManager.apply(this, provideWindowStyleConfig())
    }

    override fun onResume() {
        super.onResume()
        SettingsStore.applyTheme(this)
        WindowStyleManager.apply(this, provideWindowStyleConfig())
    }

    protected fun openTopLevelPage(
        destination: Class<out Activity>,
        enterAnim: Int,
        exitAnim: Int
    ) {
        if (javaClass == destination) {
            return
        }
        val intent = Intent(this, destination).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        overridePendingTransition(enterAnim, exitAnim)
    }
}
