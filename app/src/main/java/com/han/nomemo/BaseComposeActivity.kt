package com.han.nomemo

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

    protected fun switchPrimaryPage(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.primary_page_enter, R.anim.primary_page_exit)
        finish()
    }
}
