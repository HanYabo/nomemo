package com.han.nomemo;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.InputStream;

public final class NoMemoLiveUpdateNotifier {
    public static final String ACTION_CANCEL_AI_ANALYSIS =
            "com.han.nomemo.action.CANCEL_AI_ANALYSIS";
    public static final String ACTION_CANCEL_GROUP_ORGANIZE =
            "com.han.nomemo.action.CANCEL_GROUP_ORGANIZE";
    public static final String ACTION_COMPLETE_MEMORY_LIVE_STATUS =
            "com.han.nomemo.action.COMPLETE_MEMORY_LIVE_STATUS";
    public static final String ACTION_DISMISS_NOTIFICATION =
            "com.han.nomemo.action.DISMISS_NOTIFICATION";
    public static final String EXTRA_RECORD_ID = "record_id";
    public static final String EXTRA_ALBUM_ID = "album_id";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";

    private static final String CHANNEL_ID = "nomemo_live_updates";
    private static final String CHANNEL_NAME = "实时状态";
    private static final String CHANNEL_DESCRIPTION = "AI 分析、后台整理等进行中状态";
    private static final String GROUP_ACTIVITY_ALBUM_EXTRA = "extra_open_album_id";
    private static final int BRAND_BLUE = 0xFF1677FF;
    private static final int PROGRESS_SEGMENT_PREPARE = 0xFF7C5CFF;
    private static final int PROGRESS_SEGMENT_ANALYZE = 0xFF1677FF;
    private static final int PROGRESS_SEGMENT_WRITE = 0xFF16A34A;

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
        cancelMemoryLiveStatus(appContext, record.getRecordId());
        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(resolveAiAnalysisIcon(record, reanalyze))
                .setColor(resolveAiAnalysisColor(record, reanalyze))
                .setContentTitle(title)
                .setContentText(content)
                .setSubText(reanalyze ? "AI 重新分析" : "AI 分析")
                .setShortCriticalText(reanalyze ? "重分析" : "AI分析")
                .setStyle(buildAiAnalysisProgressStyle(appContext, attempt, attemptLimit))
                .setProgress(100, calculateAiAnalysisProgress(attempt, attemptLimit), false)
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

    public static void notifyAssistantAiAnalysis(
            Context context,
            String sessionId,
            String userText,
            boolean hasImage
    ) {
        if (TextUtils.isEmpty(sessionId) || !canNotify(context)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        ensureChannel(appContext);

        String title = "AI 助手分析中";
        String target = firstNonBlank(userText, hasImage ? "图片内容" : "记忆库");
        String content = hasImage
                ? "正在识别图片并整理相关记忆"
                : "正在整理「" + compactForNotification(target, 18) + "」";

        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(R.drawable.ic_nm_ai_assistant)
                .setContentTitle(title)
                .setContentText(content)
                .setSubText("AI 助手")
                .setShortCriticalText("AI助手")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(content)
                        .setSummaryText("AI 助手"))
                .setContentIntent(buildAiAssistantPendingIntent(appContext))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setUsesChronometer(true);

        safeNotify(appContext, buildAssistantAiNotificationId(sessionId), builder);
    }

    public static void cancelAssistantAiAnalysis(Context context, String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            return;
        }
        NotificationManagerCompat.from(context.getApplicationContext())
                .cancel(buildAssistantAiNotificationId(sessionId));
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
        MemoryLiveStatusPayload payload = buildMemoryLiveStatusPayload(record, pickupInfo);
        boolean hasImage = !TextUtils.isEmpty(record.getImageUri());

        NotificationCompat.Builder builder = baseBuilder(appContext)
                .setSmallIcon(icon)
                .setColor(color)
                .setSubText(payload.headerTitle)
                .setTicker(payload.headerTitle)
                .setContentTitle(payload.title)
                .setContentText(payload.content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(payload.title)
                        .bigText(payload.bigText)
                        .setSummaryText(payload.headerTitle))
                .setContentIntent(buildMemoryDetailPendingIntent(appContext, record.getRecordId()))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(false)
                .setUsesChronometer(false)
                .addAction(
                        R.drawable.ic_sheet_check,
                        "\u5b8c\u6210",
                        buildCompleteMemoryLivePendingIntent(appContext, record.getRecordId())
                )
                .addAction(
                        R.drawable.ic_nm_memory,
                        hasImage ? "查看图片" : "查看详情",
                        hasImage
                                ? buildMemoryImagePreviewPendingIntent(appContext, record.getRecordId())
                                : buildMemoryDetailPendingIntent(appContext, record.getRecordId())
                );

        if (pickupInfo != null && pickupInfo.getHasNavigableLocation()) {
            builder.addAction(
                    R.drawable.ic_nm_card_notification,
                    "导航",
                    buildNavigationPendingIntent(appContext, pickupInfo)
            );
        }

        Bitmap previewBitmap = loadNotificationPreview(appContext, record.getImageUri());
        if (previewBitmap != null) {
            builder.setLargeIcon(previewBitmap);
        }

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

    private static PendingIntent buildMemoryImagePreviewPendingIntent(Context context, String recordId) {
        Intent intent = MemoryDetailActivity.createIntent(context, recordId, false, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                ("live-memory-image:" + recordId).hashCode(),
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

    private static PendingIntent buildAiAssistantPendingIntent(Context context) {
        Intent intent = AiAssistantActivity.Companion.createIntent(context)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                "live-assistant-open".hashCode(),
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

    private static PendingIntent buildNavigationPendingIntent(Context context, StructuredPickupInfo pickupInfo) {
        String query = pickupInfo.getNavigationQuery();
        Double lat = pickupInfo.getNavigationLatitude();
        Double lng = pickupInfo.getNavigationLongitude();
        Uri uri;
        if (lat != null && lng != null) {
            uri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng);
        } else if (!TextUtils.isEmpty(query)) {
            uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        } else {
            uri = Uri.parse("geo:0,0");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(
                context,
                ("live-memory-nav:" + pickupInfo.getCode()).hashCode(),
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

    private static int buildAssistantAiNotificationId(String sessionId) {
        return ("live:assistant-ai:" + sessionId).hashCode();
    }

    private static int buildMemoryLiveNotificationId(String recordId) {
        return ("live:memory:" + recordId).hashCode();
    }

    private static int resolveAiAnalysisIcon(MemoryRecord record, boolean reanalyze) {
        if (reanalyze) {
            return R.drawable.ic_nm_ai_assistant;
        }
        return NotificationIconResolver.forCategory(record.getCategoryCode());
    }

    private static int resolveAiAnalysisColor(MemoryRecord record, boolean reanalyze) {
        if (reanalyze) {
            return BRAND_BLUE;
        }
        return CategoryCatalog.getCategoryAccentColor(record.getCategoryCode());
    }

    private static int calculateAiAnalysisProgress(int attempt, int attemptLimit) {
        int safeAttempt = Math.max(1, attempt);
        int safeLimit = Math.max(1, attemptLimit);
        if (safeLimit <= 1) {
            return 62;
        }
        float attemptFraction = (float) (safeAttempt - 1) / (float) safeLimit;
        int progress = 24 + Math.round(attemptFraction * 64f);
        if (safeAttempt >= safeLimit) {
            progress = Math.max(progress, 82);
        }
        return Math.max(18, Math.min(progress, 90));
    }

    private static NotificationCompat.ProgressStyle buildAiAnalysisProgressStyle(
            Context context,
            int attempt,
            int attemptLimit
    ) {
        int progress = calculateAiAnalysisProgress(attempt, attemptLimit);
        return new NotificationCompat.ProgressStyle()
                .addProgressSegment(
                        new NotificationCompat.ProgressStyle.Segment(24)
                                .setId(1)
                                .setColor(PROGRESS_SEGMENT_PREPARE)
                )
                .addProgressSegment(
                        new NotificationCompat.ProgressStyle.Segment(52)
                                .setId(2)
                                .setColor(PROGRESS_SEGMENT_ANALYZE)
                )
                .addProgressSegment(
                        new NotificationCompat.ProgressStyle.Segment(24)
                                .setId(3)
                                .setColor(PROGRESS_SEGMENT_WRITE)
                )
                .addProgressPoint(
                        new NotificationCompat.ProgressStyle.Point(progress)
                                .setId(1)
                                .setColor(PROGRESS_SEGMENT_ANALYZE)
                )
                .setProgress(progress)
                .setStyledByProgress(true);
    }

    private static int resolveMemoryLiveIcon(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        return NotificationIconResolver.forCategory(record.getCategoryCode());
    }

    private static int resolveMemoryLiveColor(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        return CategoryCatalog.getCategoryAccentColor(record.getCategoryCode());
    }

    private static MemoryLiveStatusPayload buildMemoryLiveStatusPayload(
            MemoryRecord record,
            StructuredPickupInfo pickupInfo
    ) {
        String headerTitle = firstNonBlank(record.getTitle(), record.getCategoryName(), "实时动态");
        if (pickupInfo != null) {
            String code = pickupInfo.getCode();
            String primary = labeledStructuredValueSkippingCode(
                    pickupInfo.getPrimaryLabel(),
                    pickupInfo.getPrimaryValue(),
                    code
            );
            String secondary = labeledStructuredValueSkippingCode(
                    pickupInfo.getSecondaryLabel(),
                    pickupInfo.getSecondaryValue(),
                    code
            );
            String title = firstNonBlank(code, headerTitle);
            String content = firstNonBlank(primary, secondary, headerTitle);
            String bigText = joinLiveBigText(primary, secondary);
            return new MemoryLiveStatusPayload(headerTitle, title, content, bigText);
        }
        String title = firstNonBlank(record.getTitle(), record.getSummary(), record.getCategoryName(), "实时动态");
        String content = firstNonBlank(record.getSummary(), record.getAnalysis(), record.getMemory(), "这条记忆已设为实时动态");
        return new MemoryLiveStatusPayload(headerTitle, title, content, content);
    }

    private static String labeledStructuredValue(String label, String value) {
        String cleaned = cleanStructuredValue(value);
        if (TextUtils.isEmpty(cleaned)) {
            return "";
        }
        if (TextUtils.isEmpty(label) || TextUtils.isEmpty(label.trim())) {
            return cleaned;
        }
        return label.trim() + "\uff1a" + cleaned;
    }

    private static String labeledStructuredValueSkippingCode(String label, String value, String code) {
        String cleaned = cleanStructuredValue(value);
        if (TextUtils.isEmpty(cleaned)) {
            return "";
        }
        String normalizedValue = cleaned.replace(" ", "");
        String normalizedCode = TextUtils.isEmpty(code) ? "" : code.trim().replace(" ", "");
        if (!TextUtils.isEmpty(normalizedCode) && normalizedValue.equals(normalizedCode)) {
            return "";
        }
        if (TextUtils.isEmpty(label) || TextUtils.isEmpty(label.trim())) {
            return cleaned;
        }
        return label.trim() + "\uff1a" + cleaned;
    }

    private static String cleanStructuredValue(String value) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim())) {
            return "";
        }
        String cleaned = value.trim();
        if ("\u672a\u8bc6\u522b".equals(cleaned) || "unknown".equalsIgnoreCase(cleaned)) {
            return "";
        }
        return cleaned;
    }

    private static String joinLiveCompactText(String firstLine, String secondLine) {
        String first = TextUtils.isEmpty(firstLine) ? "" : firstLine.trim();
        String second = TextUtils.isEmpty(secondLine) ? "" : secondLine.trim();
        if (first.isEmpty()) {
            return second;
        }
        if (second.isEmpty()) {
            return first;
        }
        if (first.equals(second)) {
            return first;
        }
        return first + " \u00b7 " + second;
    }

    private static String labeledValueSkippingCode(String label, String value, String code) {
        String cleaned = cleanValueSkippingCode(value, code);
        if (TextUtils.isEmpty(cleaned)) {
            return "";
        }
        if (TextUtils.isEmpty(label) || TextUtils.isEmpty(label.trim())) {
            return cleaned;
        }
        return label.trim() + "：" + cleaned;
    }

    private static String cleanValueSkippingCode(String value, String code) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim())) {
            return "";
        }
        String cleaned = value.trim();
        if ("未识别".equals(cleaned)) {
            return "";
        }
        String normalizedValue = cleaned.replace(" ", "");
        String normalizedCode = TextUtils.isEmpty(code) ? "" : code.trim().replace(" ", "");
        if (!TextUtils.isEmpty(normalizedCode) && normalizedValue.equals(normalizedCode)) {
            return "";
        }
        return cleaned;
    }

    private static void appendLine(StringBuilder builder, String value) {
        if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim())) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(value.trim());
        }
    }

    private static String joinLiveBigText(String firstLine, String... extraLines) {
        StringBuilder builder = new StringBuilder();
        appendLine(builder, firstLine);
        for (String line : extraLines) {
            if (!TextUtils.isEmpty(line) && !builder.toString().contains(line.trim())) {
                appendLine(builder, line);
            }
        }
        return builder.toString();
    }

    private static Bitmap loadNotificationPreview(Context context, String imageUri) {
        if (TextUtils.isEmpty(imageUri)) {
            return null;
        }
        try (InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imageUri))) {
            if (inputStream == null) {
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                return null;
            }
            return scaleBitmap(bitmap, 1200);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Bitmap scaleBitmap(Bitmap source, int maxEdge) {
        int width = source.getWidth();
        int height = source.getHeight();
        int longest = Math.max(width, height);
        if (longest <= maxEdge) {
            return source;
        }
        float ratio = (float) maxEdge / (float) longest;
        int scaledWidth = Math.max(1, Math.round(width * ratio));
        int scaledHeight = Math.max(1, Math.round(height * ratio));
        return Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim())) {
                return value.trim();
            }
        }
        return "";
    }

    private static String compactForNotification(String value, int maxLength) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String compact = value.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        if (compact.length() <= maxLength) {
            return compact;
        }
        return compact.substring(0, maxLength) + "...";
    }

    private static final class MemoryLiveStatusPayload {
        final String headerTitle;
        final String title;
        final String content;
        final String bigText;

        MemoryLiveStatusPayload(String headerTitle, String title, String content, String bigText) {
            this.headerTitle = headerTitle;
            this.title = title;
            this.content = content;
            this.bigText = bigText;
        }
    }
}
