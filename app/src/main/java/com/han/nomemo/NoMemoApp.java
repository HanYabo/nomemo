package com.han.nomemo;

import android.app.Application;

public class NoMemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SettingsStore.applyTheme(this);
        AiSummaryNotifier.ensureChannel(this);
        ReminderNotifier.ensureChannel(this);
        NoMemoLiveUpdateNotifier.ensureChannel(this);
        NoMemoLiveUpdateNotifier.recoverMemoryLiveStatuses(this);
        AiInitialAnalysisWorkScheduler.recoverPendingRecords(this);
        GroupAiOrganizeWorkScheduler.recoverProcessingAlbums(this);
        new Thread(() -> ReminderScheduler.scheduleAll(this)).start();
    }
}
