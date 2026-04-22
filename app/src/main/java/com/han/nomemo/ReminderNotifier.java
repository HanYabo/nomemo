package com.han.nomemo;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public final class ReminderNotifier {
    private static final String CHANNEL_ID = "nomemo_reminders";
    private static final String CHANNEL_NAME = "\u63d0\u9192\u901a\u77e5";
    private static final String CHANNEL_DESCRIPTION = "\u8bb0\u5fc6\u63d0\u9192\u5230\u671f\u540e\u5f39\u51fa\u901a\u77e5";
    private static final String FALLBACK_TITLE = "\u63d0\u9192\u4e8b\u9879";
    private static final String FALLBACK_TEXT = "\u70b9\u51fb\u67e5\u770b\u8be6\u60c5";

    private ReminderNotifier() {
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.enableVibration(true);
        channel.setShowBadge(true);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static boolean notifyReminder(Context context, MemoryRecord record) {
        if (record == null) {
            return false;
        }
        Context appContext = context.getApplicationContext();
        ensureChannel(appContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        String title = firstNonBlank(record.getTitle(), record.getMemory(), FALLBACK_TITLE);
        String content = firstNonBlank(
                record.getSummary(),
                record.getNote(),
                record.getSourceText(),
                record.getMemory(),
                FALLBACK_TEXT
        );
        String bigText = firstNonBlank(
                record.getMemory(),
                record.getSummary(),
                record.getNote(),
                record.getSourceText(),
                FALLBACK_TEXT
        );

        Intent detailIntent = MemoryDetailActivity.createIntent(appContext, record.getRecordId())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                appContext,
                record.getRecordId().hashCode(),
                detailIntent,
                pendingFlags
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nm_reminder)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(bigText)
                        .setSummaryText(FALLBACK_TITLE))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent)
                .setWhen(record.getReminderAt() > 0L ? record.getReminderAt() : System.currentTimeMillis())
                .setShowWhen(true)
                .setColor(0xFF1677FF);

        NotificationManagerCompat.from(appContext)
                .notify(buildNotificationId(record), builder.build());
        return true;
    }

    private static int buildNotificationId(MemoryRecord record) {
        return ("reminder:" + record.getRecordId()).hashCode();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim())) {
                return value.trim();
            }
        }
        return "";
    }
}
