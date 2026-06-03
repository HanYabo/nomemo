package com.han.nomemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;

public final class AiSummaryNotifier {
    private static final String CHANNEL_ID = "nomemo_ai_summary";
    private static final String CHANNEL_NAME = "AI摘要通知";
    private static final String CHANNEL_DESCRIPTION = "AI或本地兜底完成记忆整理后发送通知";

    private static final String MIUI_FOCUS_PARAM_KEY = "miui.focus.param";
    private static final String MIUI_FOCUS_PICS_KEY = "miui.focus.pics";
    private static final String MIUI_FOCUS_ACTIONS_KEY = "miui.focus.actions";
    private static final String MIUI_FOCUS_ACTION_OPEN = "miui.focus.action_open_detail";
    private static final String MIUI_FOCUS_PIC_TICKER = "miui.focus.pic_ticker";
    private static final String MIUI_FOCUS_PIC_AOD = "miui.focus.pic_aod";
    private static final String MIUI_FOCUS_PIC_IMAGE_TEXT = "miui.focus.pic_imageText";
    private static final String MIUI_FOCUS_PERMISSION_URI = "content://miui.statusbar.notification.public";
    private static final String SYSTEM_PROPERTY_SUPPORT_ISLAND = "persist.sys.feature.island";
    private static final long RESULT_LIVE_TIMEOUT_MS = 60_000L;

    private AiSummaryNotifier() {
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

    public static void notifyAnalysisReady(Context context, MemoryRecord record) {
        if (record == null) {
            return;
        }
        ensureChannel(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        boolean localFallback = "local".equalsIgnoreCase(record.getEngine());
        StructuredPickupInfo pickupInfo = MemoryDetailParser.INSTANCE.parseStructuredPickupInfo(record);
        String recordTitle = firstNonBlank(
                record.getTitle(),
                record.getCategoryName(),
                localFallback
                        ? context.getString(R.string.ai_local_notification_title)
                        : context.getString(R.string.ai_notification_title)
        );
        String resultHeadline = buildResultHeadline(context, record, pickupInfo, localFallback);
        String resultDetail = buildResultDetail(context, record, pickupInfo, localFallback);
        String displayBigText = buildResultBigText(context, record, pickupInfo, localFallback);
        String compactTitle = pickupInfo != null
                ? firstNonBlank(pickupInfo.getCode(), resultHeadline, recordTitle)
                : resultHeadline;
        int notificationIcon = resolveResultIcon(record, pickupInfo);
        int notificationColor = resolveResultColor(record, pickupInfo);
        String displayStyleSummary = localFallback
                ? context.getString(R.string.ai_local_notification_style_summary)
                : context.getString(R.string.ai_notification_style_summary);

        Intent detailIntent = MemoryDetailActivity.createIntent(context, record.getRecordId())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                record.getRecordId().hashCode(),
                detailIntent,
                pendingFlags
        );

        int notificationId = record.getRecordId().hashCode();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(notificationIcon)
                .setContentTitle(compactTitle)
                .setContentText(resultDetail)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOnlyAlertOnce(false)
                .setContentIntent(pendingIntent)
                .setColor(notificationColor)
                .setRequestPromotedOngoing(true)
                .setTimeoutAfter(RESULT_LIVE_TIMEOUT_MS)
                .addAction(
                        R.drawable.ic_sheet_close,
                        "完成",
                        buildDismissPendingIntent(context, notificationId)
                )
                .addAction(
                        R.drawable.ic_nm_memory,
                        TextUtils.isEmpty(record.getImageUri()) ? "查看详情" : "查看图片",
                        TextUtils.isEmpty(record.getImageUri())
                                ? pendingIntent
                                : buildImagePreviewPendingIntent(context, record.getRecordId(), pendingFlags)
                );

        Bitmap previewBitmap = loadNotificationPreview(context, record.getImageUri());
        if (previewBitmap != null) {
            builder.setLargeIcon(previewBitmap);
        }
        builder.setStyle(new NotificationCompat.BigTextStyle()
                .setBigContentTitle(recordTitle)
                .bigText(displayBigText)
                .setSummaryText(displayStyleSummary));

        applyMiuiIslandExtras(context, builder, recordTitle, compactTitle, resultDetail, displayBigText, notificationIcon);
        NotificationManagerCompat.from(context)
                .notify(notificationId, builder.build());
    }

    private static int resolveResultIcon(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        return NotificationIconResolver.forCategory(record.getCategoryCode());
    }

    private static int resolveResultColor(MemoryRecord record, StructuredPickupInfo pickupInfo) {
        return CategoryCatalog.getCategoryAccentColor(record.getCategoryCode());
    }

    private static String buildResultHeadline(
            Context context,
            MemoryRecord record,
            StructuredPickupInfo pickupInfo,
            boolean localFallback
    ) {
        if (pickupInfo != null && !TextUtils.isEmpty(pickupInfo.getCode())) {
            return pickupInfo.getCode();
        }
        if (localFallback) {
            return context.getString(R.string.ai_local_notification_title);
        }
        return firstNonBlank(
                record.getSummary(),
                record.getAnalysis(),
                record.getMemory(),
                context.getString(R.string.ai_notification_title)
        );
    }

    private static String buildResultDetail(
            Context context,
            MemoryRecord record,
            StructuredPickupInfo pickupInfo,
            boolean localFallback
    ) {
        if (pickupInfo != null) {
            String primary = labeledValue(pickupInfo.getPrimaryLabel(), pickupInfo.getPrimaryValue());
            String secondary = labeledValue(pickupInfo.getSecondaryLabel(), pickupInfo.getSecondaryValue());
            return firstNonBlank(primary, secondary, pickupInfo.getLocationText(), record.getSummary());
        }
        if (localFallback) {
            return context.getString(R.string.ai_local_notification_summary);
        }
        return firstNonBlank(
                record.getSummary(),
                record.getAnalysis(),
                record.getMemory(),
                context.getString(R.string.ai_notification_summary)
        );
    }

    private static String buildResultBigText(
            Context context,
            MemoryRecord record,
            StructuredPickupInfo pickupInfo,
            boolean localFallback
    ) {
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
        if (localFallback) {
            return firstNonBlank(
                    record.getAnalysis(),
                    record.getMemory(),
                    context.getString(R.string.ai_local_notification_big_text)
            );
        }
        return firstNonBlank(
                record.getSummary(),
                record.getAnalysis(),
                record.getMemory(),
                context.getString(R.string.ai_notification_big_text)
        );
    }

    private static String labeledValue(String label, String value) {
        if (TextUtils.isEmpty(value) || "未识别".equals(value.trim())) {
            return "";
        }
        if (TextUtils.isEmpty(label)) {
            return value.trim();
        }
        return label.trim() + "：" + value.trim();
    }

    private static void appendLine(StringBuilder builder, String value) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.trim())) {
            return;
        }
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(value.trim());
    }

    private static void applyMiuiIslandExtras(
            Context context,
            NotificationCompat.Builder builder,
            String frontTitle,
            String title,
            String summary,
            String bigText,
            int iconRes
    ) {
        if (!supportsMiuiIsland(context) || !hasMiuiFocusPermission(context)) {
            return;
        }
        try {
            Icon memoryIcon = Icon.createWithResource(context, iconRes);

            Bundle pics = new Bundle();
            pics.putParcelable(MIUI_FOCUS_PIC_TICKER, memoryIcon);
            pics.putParcelable(MIUI_FOCUS_PIC_AOD, memoryIcon);
            pics.putParcelable(MIUI_FOCUS_PIC_IMAGE_TEXT, memoryIcon);

            Bundle extras = new Bundle();
            extras.putBundle(MIUI_FOCUS_PICS_KEY, pics);

            Notification.Action openAction = new Notification.Action.Builder(
                    memoryIcon,
                    "查看详情",
                    buildIslandOpenPendingIntent(context)
            ).build();
            Bundle actions = new Bundle();
            actions.putParcelable(MIUI_FOCUS_ACTION_OPEN, openAction);
            extras.putBundle(MIUI_FOCUS_ACTIONS_KEY, actions);
            builder.addExtras(extras);

            JSONObject root = new JSONObject();
            JSONObject paramV2 = new JSONObject();
            root.put("param_v2", paramV2);

            paramV2.put("protocol", 1);
            paramV2.put("business", "nomemo_ai");
            paramV2.put("islandFirstFloat", true);
            paramV2.put("enableFloat", true);
            paramV2.put("updatable", true);
            paramV2.put("filterWhenNoPermission", false);
            paramV2.put("ticker", title);
            paramV2.put("tickerPic", MIUI_FOCUS_PIC_TICKER);
            paramV2.put("aodTitle", title);
            paramV2.put("aodPic", MIUI_FOCUS_PIC_AOD);

            JSONObject paramIsland = new JSONObject();
            paramIsland.put("islandProperty", 1);
            paramIsland.put("islandTimeout", 60);
            paramIsland.put("islandOrder", true);

            JSONObject bigIslandArea = new JSONObject();
            JSONObject imageTextInfoLeft = new JSONObject();
            imageTextInfoLeft.put("type", 1);

            JSONObject leftPicInfo = new JSONObject();
            leftPicInfo.put("type", 1);
            leftPicInfo.put("pic", MIUI_FOCUS_PIC_IMAGE_TEXT);
            imageTextInfoLeft.put("picInfo", leftPicInfo);

            JSONObject leftTextInfo = new JSONObject();
            leftTextInfo.put("frontTitle", frontTitle);
            leftTextInfo.put("title", title);
            leftTextInfo.put("content", summary);
            leftTextInfo.put("useHighLight", false);
            imageTextInfoLeft.put("miui.focus.paramtextInfo", leftTextInfo);
            bigIslandArea.put("imageTextInfoLeft", imageTextInfoLeft);

            JSONObject rightPicInfo = new JSONObject();
            rightPicInfo.put("type", 1);
            rightPicInfo.put("pic", MIUI_FOCUS_PIC_IMAGE_TEXT);
            bigIslandArea.put("picInfo", rightPicInfo);
            paramIsland.put("bigIslandArea", bigIslandArea);

            JSONObject smallIslandArea = new JSONObject();
            JSONObject smallPicInfo = new JSONObject();
            smallPicInfo.put("type", 1);
            smallPicInfo.put("pic", MIUI_FOCUS_PIC_IMAGE_TEXT);
            smallIslandArea.put("picInfo", smallPicInfo);
            JSONObject smallTextInfo = new JSONObject();
            smallTextInfo.put("frontTitle", frontTitle);
            smallTextInfo.put("title", title);
            smallTextInfo.put("content", summary);
            smallTextInfo.put("useHighLight", false);
            smallIslandArea.put("miui.focus.paramtextInfo", smallTextInfo);
            paramIsland.put("smallIslandArea", smallIslandArea);

            JSONObject shareData = new JSONObject();
            shareData.put("pic", MIUI_FOCUS_PIC_IMAGE_TEXT);
            shareData.put("title", title);
            shareData.put("content", summary);
            shareData.put("shareContent", bigText);
            paramIsland.put("shareData", shareData);
            paramV2.put("param_island", paramIsland);

            JSONObject baseInfo = new JSONObject();
            baseInfo.put("type", 2);
            baseInfo.put("title", title);
            baseInfo.put("content", bigText);
            paramV2.put("baseInfo", baseInfo);

            JSONObject hintInfo = new JSONObject();
            hintInfo.put("type", 1);
            hintInfo.put("title", frontTitle);
            JSONObject actionInfo = new JSONObject();
            actionInfo.put("action", MIUI_FOCUS_ACTION_OPEN);
            hintInfo.put("actionInfo", actionInfo);
            paramV2.put("hintInfo", hintInfo);

            JSONArray actionsArray = new JSONArray();
            JSONObject openActionInfo = new JSONObject();
            openActionInfo.put("action", MIUI_FOCUS_ACTION_OPEN);
            actionsArray.put(openActionInfo);
            paramV2.put("actions", actionsArray);

            Bundle builderExtras = builder.getExtras();
            if (builderExtras != null) {
                builderExtras.putString(MIUI_FOCUS_PARAM_KEY, root.toString());
            }
        } catch (Exception ignored) {
            // Keep the standard notification if MIUI island extras fail.
        }
    }

    private static boolean supportsMiuiIsland(Context context) {
        if (!isSupportIslandSystemProperty()) {
            return false;
        }
        try {
            int protocol = Settings.System.getInt(
                    context.getContentResolver(),
                    "notification_focus_protocol",
                    0
            );
            return protocol >= 3;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean hasMiuiFocusPermission(Context context) {
        try {
            Bundle extras = new Bundle();
            extras.putString("package", context.getPackageName());
            Bundle result = context.getContentResolver().call(
                    Uri.parse(MIUI_FOCUS_PERMISSION_URI),
                    "canShowFocus",
                    null,
                    extras
            );
            return result != null && result.getBoolean("canShowFocus", false);
        } catch (Exception ignored) {
            return false;
        }
    }

    private static boolean isSupportIslandSystemProperty() {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("getBoolean", String.class, boolean.class);
            Object value = method.invoke(null, SYSTEM_PROPERTY_SUPPORT_ISLAND, false);
            return value instanceof Boolean && (Boolean) value;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static PendingIntent buildIslandOpenPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        return PendingIntent.getActivity(context, 0, intent, flags);
    }

    private static PendingIntent buildImagePreviewPendingIntent(
            Context context,
            String recordId,
            int pendingFlags
    ) {
        Intent intent = MemoryDetailActivity.createIntent(context, recordId, false, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                context,
                ("ai-summary-image:" + recordId).hashCode(),
                intent,
                pendingFlags
        );
    }

    private static PendingIntent buildDismissPendingIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NoMemoLiveUpdateActionReceiver.class)
                .setAction(NoMemoLiveUpdateNotifier.ACTION_DISMISS_NOTIFICATION)
                .putExtra(NoMemoLiveUpdateNotifier.EXTRA_NOTIFICATION_ID, notificationId);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT
                | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0);
        return PendingIntent.getBroadcast(
                context,
                ("ai-summary-dismiss:" + notificationId).hashCode(),
                intent,
                flags
        );
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
}
