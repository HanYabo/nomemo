package com.han.nomemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import java.util.List;

public final class ReminderScheduler {
    public static final String ACTION_SHOW_REMINDER = "com.han.nomemo.action.SHOW_REMINDER";
    public static final String EXTRA_RECORD_ID = "extra_record_id";
    public static final String EXTRA_REMINDER_AT = "extra_reminder_at";

    private static final String DELIVERY_PREF_NAME = "no_memo_reminder_delivery";
    private static final long MISSED_REMINDER_GRACE_MS = 7L * 24L * 60L * 60L * 1000L;

    private ReminderScheduler() {
    }

    public static void schedule(Context context, MemoryRecord record) {
        if (record == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        if (!shouldSchedule(record, System.currentTimeMillis())) {
            cancel(appContext, record.getRecordId());
            return;
        }
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        PendingIntent pendingIntent = buildPendingIntent(
                appContext,
                record.getRecordId(),
                record.getReminderAt(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, record.getReminderAt(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, record.getReminderAt(), pendingIntent);
        }
    }

    public static void cancel(Context context, String recordId) {
        if (TextUtils.isEmpty(recordId)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        PendingIntent pendingIntent = buildPendingIntent(
                appContext,
                recordId,
                0L,
                PendingIntent.FLAG_NO_CREATE
        );
        if (pendingIntent == null) {
            return;
        }
        AlarmManager alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    public static void scheduleAll(Context context) {
        Context appContext = context.getApplicationContext();
        ReminderNotifier.ensureChannel(appContext);
        MemoryStore store = new MemoryStore(appContext);
        List<MemoryRecord> records = store.loadRecords();
        long now = System.currentTimeMillis();
        for (MemoryRecord record : records) {
            if (record == null || TextUtils.isEmpty(record.getRecordId())) {
                continue;
            }
            if (record.isArchived() || record.isReminderDone() || record.getReminderAt() <= 0L) {
                cancel(appContext, record.getRecordId());
                continue;
            }
            if (record.getReminderAt() > now) {
                schedule(appContext, record);
            } else if (now - record.getReminderAt() <= MISSED_REMINDER_GRACE_MS
                    && !wasDelivered(appContext, record)
                    && ReminderNotifier.notifyReminder(appContext, record)) {
                markDelivered(appContext, record);
            }
        }
    }

    static boolean wasDelivered(Context context, MemoryRecord record) {
        if (record == null) {
            return true;
        }
        return deliveryPreferences(context).getBoolean(deliveryKey(record), false);
    }

    static void markDelivered(Context context, MemoryRecord record) {
        if (record == null) {
            return;
        }
        deliveryPreferences(context)
                .edit()
                .putBoolean(deliveryKey(record), true)
                .apply();
    }

    private static boolean shouldSchedule(MemoryRecord record, long now) {
        return !record.isArchived()
                && !record.isReminderDone()
                && record.getReminderAt() > now;
    }

    private static PendingIntent buildPendingIntent(
            Context context,
            String recordId,
            long reminderAt,
            int flags
    ) {
        Intent intent = new Intent(context, ReminderAlarmReceiver.class)
                .setAction(ACTION_SHOW_REMINDER)
                .setPackage(context.getPackageName())
                .putExtra(EXTRA_RECORD_ID, recordId)
                .putExtra(EXTRA_REMINDER_AT, reminderAt);
        int pendingFlags = flags
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        return PendingIntent.getBroadcast(context, requestCode(recordId), intent, pendingFlags);
    }

    private static int requestCode(String recordId) {
        return ("reminder_alarm:" + recordId).hashCode();
    }

    private static SharedPreferences deliveryPreferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(DELIVERY_PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String deliveryKey(MemoryRecord record) {
        return record.getRecordId() + ":" + record.getReminderAt();
    }
}
