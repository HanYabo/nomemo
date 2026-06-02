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

public final class NoMemoLiveUpdateNotifier {
    public static final String ACTION_CANCEL_AI_ANALYSIS =
            "com.han.nomemo.action.CANCEL_AI_ANALYSIS";
    public static final String ACTION_CANCEL_GROUP_ORGANIZE =
            "com.han.nomemo.action.CANCEL_GROUP_ORGANIZE";
    public static final String ACTION_DISMISS_NOTIFICATION =
            "com.han.nomemo.action.DISMISS_NOTIFICATION";
    public static final String ACTION_COMPLETE_MEMORY_LIVE_STATUS =
            "com.han.nomemo.action.COMPLETE_MEMORY_LIVE_STATUS";
    public static final String EXTRA_RECORD_ID = "record_id";
    public static final String EXTRA_ALBUM_ID = "album_id";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    private static final String CHANNEL_ID = "nomemo_live_updates";
    private static final String CHANNEL_NAME = "实时状态";
    private static final String CHANNEL_DESCRIPTION = "AI 分析、后台整理等进行中状态";
    private static final String GROUP_ACTIVITY_ALBUM_EXTRA = "extra_open_album_id";
    private static final int BRAND_BLUE = 0xFF1677FF;

    private NoMemoLiveUpdateNotifier() {
    }

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        channel.setSound(null, null);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public static void notifyAiAnalysis(Context context, MemoryRecord record) {
        if (record == null) {
            return;
        }
        AiAnalysisState state = AiAnalysisStateJson.parse(record.getAiAnalysisStateJson());
        int attempt = state != null ? Math.max(1, state.getAttemptCount()) : 1;
        int attemptLimit = state != null ? Math.max(attempt, state.getAttemptLimit()) : attempt;
        notifyAiAnalysis(context, record, attempt, attemptLimit);
    }

    public static void notifyAiAnalysis(
            Context context,
            MemoryRecord record,
            int attempt,
            int attemptLimit
    ) {
        if (record == null || !canNotify(context)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        ensureChannel(appContext);

        AiAnalysisState state = AiAnalysisStateJson.parse(record.getAiAnalysisStateJson());
        boolean reanalyze = state != null && state.getOperationKind() == AiOperationKind.REANALYZE;
        boolean retrying = Math.max(1, attempt) >= 2;
        String title = retrying ? "AI重试中" : "AI分析中";
        String target = firstNonBlank(record.getTitle(), record.getSummary(), record.getMemory(), "当前记忆");
        String content = reanalyze
                ? "正在重新整理「" + target + "」"
                : "正在整理「" + target + "」";
        String bigText = content + "，完成后会自动更新记忆详情。";

        PendingIntent contentIntent = buildMemoryDetailPendingIntent(appContext, record.getRecordId());
        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(R.drawable.ic_nm_memory)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(bigText)
                        .setSummaryText(reanalyze ? "AI 重新分析" : "AI 分析"))
                .setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setUsesChronometer(true);

        if (!reanalyze) {
            builder.addAction(
                    R.drawable.ic_sheet_close,
                    "取消分析",
                    buildCancelAiPendingIntent(appContext, record.getRecordId())
            );
        }

        safeNotify(appContext, buildAiNotificationId(record.getRecordId()), builder);
    }

    public static void cancelAiAnalysis(Context context, String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            return;
        }
        NotificationManagerCompat.from(context.getApplicationContext())
                .cancel(buildAiNotificationId(recordId));
    }

    public static void notifyGroupOrganizing(
            Context context,
            GroupAlbumStore.GroupAlbum album
    ) {
        if (album == null || !canNotify(context)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        ensureChannel(appContext);

        String albumName = firstNonBlank(album.getName(), "当前分组");
        String title = "正在整理记忆";
        String content = "正在分析历史记忆并匹配到「" + albumName + "」分组";

        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(R.drawable.ic_nm_group)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(content)
                        .setSummaryText("整理历史记忆"))
                .setContentIntent(buildGroupDetailPendingIntent(appContext, album.getAlbumId()))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setUsesChronometer(true)
                .addAction(
                        R.drawable.ic_sheet_close,
                        "取消",
                        buildCancelGroupPendingIntent(appContext, album.getAlbumId())
                );

        safeNotify(appContext, buildGroupNotificationId(album.getAlbumId()), builder);
    }

    public static void cancelGroupOrganize(Context context, String albumId) {
        if (albumId == null || albumId.trim().isEmpty()) {
            return;
        }
        NotificationManagerCompat.from(context.getApplicationContext())
                .cancel(buildGroupNotificationId(albumId));
    }

    public static void notifyMemoryLiveStatus(Context context, MemoryRecord record) {
        if (record == null || !record.isLiveStatusActive() || record.isArchived() || !canNotify(context)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        ensureChannel(appContext);

        StructuredPickupInfo pickupInfo = MemoryDetailParser.INSTANCE.parseStructuredPickupInfo(record);
        int icon = resolveMemoryLiveIcon(record, pickupInfo);
        int color = resolveMemoryLiveColor(record, pickupInfo);
        String title = buildMemoryLiveTitle(record, pickupInfo);
        String content = buildMemoryLiveContent(record, pickupInfo);
        String bigText = buildMemoryLiveBigText(record, pickupInfo);

        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(icon)
                .setColor(color)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(bigText)
                        .setSummaryText("实时动态"))
                .setContentIntent(buildMemoryDetailPendingIntent(appContext, record.getRecordId()))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(false)
                .setUsesChronometer(false)
                .addAction(
                        R.drawable.ic_sheet_check,
                        "完成",
                        buildCompleteMemoryLivePendingIntent(appContext, record.getRecordId())
                )
                .addAction(
                        R.drawable.ic_nm_memory,
                        "查看详情",
                        buildMemoryDetailPendingIntent(appContext, record.getRecordId())
                );

        safeNotify(appContext, buildMemoryLiveNotificationId(record.getRecordId()), builder);
    }

    public static void cancelMemoryLiveStatus(Context context, String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            return;
        }
        NotificationManagerCompat.from(context.getApplicationContext())
                .cancel(buildMemoryLiveNotificationId(recordId));
    }

    public static void recoverMemoryLiveStatuses(Context context) {
        Context appContext = context.getApplicationContext();
        new Thread(() -> {
            MemoryStore store = new MemoryStore(appContext);
            for (MemoryRecord record : store.loadRecords()) {
                if (record.isLiveStatusActive() && !record.isArchived()) {
                    notifyMemoryLiveStatus(appContext, record);
                }
            }
        }).start();
    }

    private static NotificationCompat.Builder baseBuilder(Context context) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setColor(BRAND_BLUE)
                .setRequestPromotedOngoing(true);
    }

    private static boolean canNotify(Context context) {
        Context appContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return NotificationManagerCompat.from(appContext).areNotificationsEnabled();
    }

    private static void safeNotify(
            Context context,
            int notificationId,
            NotificationCompat.Builder builder
    ) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        } catch (SecurityException ignored) {
        }
    }

    private static PendingIntent buildMemoryDetailPendingIntent(Context context, String recordId) {
        Intent intent = MemoryDetailActivity.createIntent(context, recordId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                ("live-ai-open:" + recordId).hashCode(),
                intent,
                pendingFlags()
        );
    }

    private static PendingIntent buildGroupDetailPendingIntent(Context context, String albumId) {
        Intent intent = new Intent(context, GroupActivity.class)
                .putExtra(GROUP_ACTIVITY_ALBUM_EXTRA, albumId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                ("live-group-open:" + albumId).hashCode(),
                intent,
                pendingFlags()
        );
    }

    private static PendingIntent buildCancelAiPendingIntent(Context context, String recordId) {
        Intent intent = new Intent(context, NoMemoLiveUpdateActionReceiver.class)
                .setAction(ACTION_CANCEL_AI_ANALYSIS)
                .putExtra(EXTRA_RECORD_ID, recordId);
        return PendingIntent.getBroadcast(
                context,
                ("live-ai-cancel:" + recordId).hashCode(),
                intent,
                pendingFlags()
        );
    }

    private static PendingIntent buildCancelGroupPendingIntent(Context context, String albumId) {
        Intent intent = new Intent(context, NoMemoLiveUpdateActionReceiver.class)
                .setAction(ACTION_CANCEL_GROUP_ORGANIZE)
                .putExtra(EXTRA_ALBUM_ID, albumId);
        return PendingIntent.getBroadcast(
                context,
                ("live-group-cancel:" + albumId).hashCode(),
                intent,
                pendingFlags()
        );
    }

    private static PendingIntent buildCompleteMemoryLivePendingIntent(Context context, String recordId) {
        Intent intent = new Intent(context, NoMemoLiveUpdateActionReceiver.class)
                .setAction(ACTION_COMPLETE_MEMORY_LIVE_STATUS)
                .putExtra(EXTRA_RECORD_ID, recordId);
        return PendingIntent.getBroadcast(
                context,
                ("live-memory-complete:" + recordId).hashCode(),
                intent,
                pendingFlags()
        );
    }

    private static int pendingFlags() {
        return PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
    }

    private static int buildAiNotificationId(String recordId) {
        return ("live:ai:" + recordId).hashCode();
    }

    private static int buildGroupNotificationId(String albumId) {
        return ("live:group:" + albumId).hashCode();
    }

    private static int buildMemoryLiveNotificationId(String recordId) {
        return ("live:memory:" + recordId).hashCode();
    }

    private static int resolveMemoryLiveIcon(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        if (pickupInfo != null && CategoryCatalog.CODE_LIFE_PICKUP.equals(record.getCategoryCode())) {
            return R.drawable.ic_nm_food_notification;
        }
        if (pickupInfo != null && CategoryCatalog.CODE_LIFE_DELIVERY.equals(record.getCategoryCode())) {
            return R.drawable.ic_nm_package_notification;
        }
        return R.drawable.ic_nm_memory;
    }

    private static int resolveMemoryLiveColor(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        if (pickupInfo != null && CategoryCatalog.CODE_LIFE_PICKUP.equals(record.getCategoryCode())) {
            return 0xFFFF8A2A;
        }
        return BRAND_BLUE;
    }

    private static String buildMemoryLiveTitle(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        if (pickupInfo != null && !TextUtils.isEmpty(pickupInfo.getCode())) {
            return pickupInfo.getCode().trim();
        }
        return firstNonBlank(record.getTitle(), record.getSummary(), record.getCategoryName(), "实时动态");
    }

    private static String buildMemoryLiveContent(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        if (pickupInfo != null) {
            return firstNonBlank(
                    labeledValue(pickupInfo.getPrimaryLabel(), pickupInfo.getPrimaryValue()),
                    labeledValue(pickupInfo.getSecondaryLabel(), pickupInfo.getSecondaryValue()),
                    pickupInfo.getLocationText(),
                    record.getSummary(),
                    record.getTitle()
            );
        }
        return firstNonBlank(record.getSummary(), record.getAnalysis(), record.getMemory(), "这条记忆已设为实时动态");
    }

    private static String buildMemoryLiveBigText(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        if (pickupInfo != null) {
            StringBuilder builder = new StringBuilder();
            appendLine(builder, pickupInfo.getCode());
            appendLine(builder, labeledValue(pickupInfo.getPrimaryLabel(), pickupInfo.getPrimaryValue()));
            appendLine(builder, labeledValue(pickupInfo.getSecondaryLabel(), pickupInfo.getSecondaryValue()));
            appendLine(builder, labeledValue("地点", pickupInfo.getLocationText()));
            String value = builder.toString().trim();
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }
        return firstNonBlank(record.getSummary(), record.getAnalysis(), record.getMemory(), record.getTitle(), "实时动态");
    }

    private static String labeledValue(String label, String value) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim()) || "未识别".equals(value.trim())) {
            return "";
        }
        if (TextUtils.isEmpty(label) || TextUtils.isEmpty(label.trim())) {
            return value.trim();
        }
        return label.trim() + "：" + value.trim();
    }

    private static void appendLine(StringBuilder builder, String value) {
        if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim())) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(value.trim());
        }
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
