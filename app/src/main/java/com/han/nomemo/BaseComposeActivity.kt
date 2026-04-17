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

    protected fun switchPrimaryPage(intent: Intent, pulseTab: NoMemoDockTab? = null) {
        pulseTab?.let { intent.putExtra(EXTRA_PRIMARY_DOCK_PULSE_TAB, it.name) }
        startActivity(intent)
        overridePendingTransition(R.anim.primary_page_enter, R.anim.primary_page_exit)
        finish()
    }

    protected fun consumePrimaryDockPulse(): NoMemoDockTab? {
        val encoded = intent?.getStringExtra(EXTRA_PRIMARY_DOCK_PULSE_TAB) ?: return null
        intent?.removeExtra(EXTRA_PRIMARY_DOCK_PULSE_TAB)
        return runCatching { NoMemoDockTab.valueOf(encoded) }.getOrNull()
    }

    protected fun resetDoubleBackExitState() {
        lastBackPressedAt = 0L
    }

    fun clearDoubleBackExitState() {
        resetDoubleBackExitState()
    }

    companion object {
        private const val DOUBLE_BACK_INTERVAL_MS = 2000L
        private const val EXTRA_PRIMARY_DOCK_PULSE_TAB = "extra_primary_dock_pulse_tab"
    }
}
