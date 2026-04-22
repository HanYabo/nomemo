package com.han.nomemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public final class ReminderAlarmReceiver extends BroadcastReceiver {
    private static final long EARLY_DELIVERY_TOLERANCE_MS = 1000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return;
        }
        String action = intent != null ? intent.getAction() : null;
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action)) {
            ReminderScheduler.scheduleAll(context);
            return;
        }
        if (!ReminderScheduler.ACTION_SHOW_REMINDER.equals(action) || intent == null) {
            return;
        }

        String recordId = intent.getStringExtra(ReminderScheduler.EXTRA_RECORD_ID);
        if (TextUtils.isEmpty(recordId)) {
            return;
        }
        MemoryStore store = new MemoryStore(context);
        MemoryRecord record = store.findRecordById(recordId);
        if (record == null
                || record.isArchived()
                || record.isReminderDone()
                || record.getReminderAt() <= 0L) {
            ReminderScheduler.cancel(context, recordId);
            return;
        }

        long scheduledReminderAt = intent.getLongExtra(
                ReminderScheduler.EXTRA_REMINDER_AT,
                record.getReminderAt()
        );
        if (scheduledReminderAt > 0L && scheduledReminderAt != record.getReminderAt()) {
            ReminderScheduler.schedule(context, record);
            return;
        }
        if (record.getReminderAt() > System.currentTimeMillis() + EARLY_DELIVERY_TOLERANCE_MS) {
            ReminderScheduler.schedule(context, record);
            return;
        }
        if (!ReminderScheduler.wasDelivered(context, record)
                && ReminderNotifier.notifyReminder(context, record)) {
            ReminderScheduler.markDelivered(context, record);
        }
    }
}
