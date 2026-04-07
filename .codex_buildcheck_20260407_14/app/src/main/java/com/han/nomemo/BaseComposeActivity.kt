package com.han.nomemo

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

abstract class BaseComposeActivity : AppCompatActivity() {
    private var lastBackPressedAt = 0L
    private val doubleBackExitCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val now = SystemClock.elapsedRealtime()
            if (now - lastBackPressedAt <= DOUBLE_BACK_INTERVAL_MS) {
                lastBackPressedAt = 0L
                moveTaskToBack(true)
            } else {
                lastBackPressedAt = now
                Toast.makeText(
                    this@BaseComposeActivity,
                    "再按一次返回桌面",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    open fun provideWindowStyleConfig(): AppWindowStyleConfig = UiConfig.windowStyleFor(this)
    protected open fun enableDoubleBackToDesktop(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        SettingsStore.applyTheme(this)
        super.onCreate(savedInstanceState)
        WindowStyleManager.apply(this, provideWindowStyleConfig())
        onBackPressedDispatcher.addCallback(this, doubleBackExitCallback)
        doubleBackExitCallback.isEnabled = enableDoubleBackToDesktop()
    }

    override fun onResume() {
        super.onResume()
        SettingsStore.applyTheme(this)
        WindowStyleManager.apply(this, provideWindowStyleConfig())
        doubleBackExitCallback.isEnabled = enableDoubleBackToDesktop()
    }

    protected fun switchPrimaryPage(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.primary_page_enter, R.anim.primary_page_exit)
        finish()
    }

    protected fun resetDoubleBackExitState() {
        lastBackPressedAt = 0L
    }

    companion object {
        private const val DOUBLE_BACK_INTERVAL_MS = 2000L
    }
}
