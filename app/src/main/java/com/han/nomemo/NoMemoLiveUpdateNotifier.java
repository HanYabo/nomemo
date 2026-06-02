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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern PRODUCT_PATTERN = Pattern.compile(
            "(?:商品|品名|餐品|物品|内容)(?:名称)?[：:\\s为]*([^，。；;\\n]{2,28})"
    );

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
                .setSmallIcon(R.drawable.ic_nm_memory_notification)
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
                        "完成",
                        buildCompleteMemoryLivePendingIntent(appContext, record.getRecordId())
                )
                .addAction(
                        R.drawable.ic_nm_memory,
                        hasImage ? "查看图片" : "查看详情",
                        hasImage
                                ? buildMemoryImagePreviewPendingIntent(appContext, record.getRecordId())
                                : buildMemoryDetailPendingIntent(appContext, record.getRecordId())
                );

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
        return R.drawable.ic_nm_memory_notification;
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
            String title = firstNonBlank(code, headerTitle);
            String primaryLine = labeledValueSkippingCode(
                    pickupInfo.getPrimaryLabel(),
                    pickupInfo.getPrimaryValue(),
                    code
            );
            String secondaryLine = labeledValueSkippingCode(
                    pickupInfo.getSecondaryLabel(),
                    resolveSecondaryLiveValue(record, pickupInfo, code),
                    code
            );
            String locationLine = labeledValueSkippingCode("地点", pickupInfo.getLocationText(), code);
            String content = firstNonBlank(
                    joinLiveContentInline(primaryLine, secondaryLine),
                    joinLiveContentInline(primaryLine, locationLine),
                    cleanValueSkippingCode(record.getSummary(), code),
                    cleanValueSkippingCode(record.getAnalysis(), code),
                    headerTitle
            );
            String bigText = joinLiveBigText(content, locationLine);
            return new MemoryLiveStatusPayload(headerTitle, title, content, bigText);
        }
        String title = firstNonBlank(record.getTitle(), record.getSummary(), record.getCategoryName(), "实时动态");
        String content = firstNonBlank(record.getSummary(), record.getAnalysis(), record.getMemory(), "这条记忆已设为实时动态");
        return new MemoryLiveStatusPayload(headerTitle, title, content, content);
    }

    private static String joinLiveContentInline(String... values) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String value : values) {
            if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim())) {
                continue;
            }
            if (count > 0) {
                builder.append("｜");
            }
            builder.append(value.trim());
            count++;
            if (count >= 2) {
                break;
            }
        }
        return builder.toString();
    }

    private static String resolveSecondaryLiveValue(
            MemoryRecord record,
            StructuredPickupInfo pickupInfo,
            String code
    ) {
        String explicit = cleanValueSkippingCode(pickupInfo.getSecondaryValue(), code);
        if (!TextUtils.isEmpty(explicit)) {
            return explicit;
        }
        MemoryStructuredFacts facts = MemoryStructuredFactsJson.parse(record.getStructuredFactsJson());
        if (CategoryCatalog.CODE_LIFE_PICKUP.equals(record.getCategoryCode())) {
            return firstNonBlank(
                    cleanValueSkippingCode(facts == null ? null : facts.getItemName(), code),
                    cleanValueSkippingCode(extractProductFromText(record.getSummary()), code),
                    cleanValueSkippingCode(extractProductFromText(record.getAnalysis()), code),
                    cleanValueSkippingCode(extractProductFromText(record.getMemory()), code),
                    cleanValueSkippingCode(extractProductFromText(record.getSourceText()), code),
                    cleanValueSkippingCode(extractProductFromText(record.getNote()), code),
                    cleanValueSkippingCode(extractProductFromText(facts == null ? null : facts.getRawVisibleText()), code)
            );
        }
        if (CategoryCatalog.CODE_LIFE_DELIVERY.equals(record.getCategoryCode())) {
            return firstNonBlank(
                    cleanValueSkippingCode(facts == null ? null : facts.getLocation(), code),
                    cleanValueSkippingCode(pickupInfo.getLocationText(), code)
            );
        }
        return "";
    }

    private static String extractProductFromText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        String sectionValue = extractProductFromSection(text);
        if (!TextUtils.isEmpty(sectionValue)) {
            return sectionValue;
        }
        Matcher matcher = PRODUCT_PATTERN.matcher(text);
        if (!matcher.find()) {
            return "";
        }
        String value = matcher.group(1);
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value
                .replace("为", "")
                .replace("是", "")
                .trim();
    }

    private static String extractProductFromSection(String text) {
        String[] lines = text.split("\\r?\\n");
        boolean afterProductSection = false;
        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            if (line.contains("商品明细") || line.contains("商品详情") || line.contains("餐品明细")) {
                afterProductSection = true;
                continue;
            }
            if (!afterProductSection) {
                continue;
            }
            String candidate = cleanupProductCandidate(line);
            if (!TextUtils.isEmpty(candidate)) {
                return candidate;
            }
        }
        return "";
    }

    private static String cleanupProductCandidate(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        String cleaned = value
                .replaceAll("[￥¥]\\s*\\d+(?:\\.\\d+)?", "")
                .replaceAll("\\bx\\s*\\d+\\b", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
        if (cleaned.length() < 2 || cleaned.length() > 36) {
            return "";
        }
        if (cleaned.contains("商品总价") ||
                cleaned.contains("商品总计") ||
                cleaned.contains("订单") ||
                cleaned.contains("发票") ||
                cleaned.contains("合计") ||
                cleaned.contains("优惠") ||
                cleaned.contains("积分")) {
            return "";
        }
        if (cleaned.matches("^[\\d\\s.,，。:：;；/\\\\-]+$")) {
            return "";
        }
        return cleaned;
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
