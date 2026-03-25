package com.han.nomemo;

import android.app.Application;

public class NoMemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SettingsStore.applyTheme(this);
        AiSummaryNotifier.ensureChannel(this);
    }
}
