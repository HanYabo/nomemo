package com.han.nomemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseComposeActivity : AppCompatActivity() {
    open fun provideWindowStyleConfig(): AppWindowStyleConfig = UiConfig.windowStyleFor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowStyleManager.apply(this, provideWindowStyleConfig())
    }

    override fun onResume() {
        super.onResume()
        WindowStyleManager.apply(this, provideWindowStyleConfig())
    }
}

