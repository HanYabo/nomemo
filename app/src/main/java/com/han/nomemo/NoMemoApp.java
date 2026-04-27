package com.han.nomemo;

import android.app.Application;

public class NoMemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SettingsStore.applyTheme(this);
        AiSummaryNotifier.ensureChannel(this);
        ReminderNotifier.ensureChannel(this);
        AiInitialAnalysisWorkScheduler.recoverPendingRecords(this);
        new Thread(() -> ReminderScheduler.scheduleAll(this)).start();
    }
}
