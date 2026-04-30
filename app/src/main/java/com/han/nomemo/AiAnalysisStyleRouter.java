package com.han.nomemo;

import androidx.annotation.Nullable;

import java.util.Locale;

public final class AiAnalysisStyleRouter {
    private static final int DOCUMENT_RICH_MIN_LENGTH = 120;
    private static final int DOCUMENT_RICH_MIN_LINES = 4;

    private AiAnalysisStyleRouter() {
    }

    public static AiAnalysisStyleHint resolve(
            @Nullable String userText,
            @Nullable String ocrVisibleText,
            @Nullable String categoryCode,
            boolean hasImage
    ) {
        String mergedRaw = joinNonBlank(userText, ocrVisibleText);
        String normalized = normalize(mergedRaw);
        if (normalized.isEmpty()) {
            return AiAnalysisStyleHint.TRANSACTIONAL;
        }
        if (isStrongTransactionalCategory(categoryCode)) {
            return AiAnalysisStyleHint.TRANSACTIONAL;
        }

        boolean strongDocumentSignal = hasStrongDocumentSignal(normalized, mergedRaw, hasImage);
        if (strongDocumentSignal) {
            return AiAnalysisStyleHint.DOCUMENT_RICH;
        }

        if (isWorkActionCategory(categoryCode)) {
            return AiAnalysisStyleHint.TRANSACTIONAL;
        }

        int score = 0;
        if (normalized.length() >= DOCUMENT_RICH_MIN_LENGTH) {
            score++;
        }
        if (countMeaningfulLines(mergedRaw) >= DOCUMENT_RICH_MIN_LINES) {
            score++;
        }
        if (containsAny(normalized,
                "邮件", "邮箱", "通知", "说明", "介绍", "邀请", "计划", "活动", "公告",
                "文章", "文档", "报告", "背景", "权益", "申请", "发件人", "收件人", "主题",
                "email", "invite", "invitation", "credits", "token", "announcement", "program")) {
            score++;
        }
        if (hasImage && countMeaningfulLines(mergedRaw) >= 6) {
            score++;
        }
        return score >= 2 ? AiAnalysisStyleHint.DOCUMENT_RICH : AiAnalysisStyleHint.TRANSACTIONAL;
    }

    private static boolean hasStrongDocumentSignal(String normalized, String rawText, boolean hasImage) {
        boolean hasPrimaryKeywords = containsAny(normalized,
                "邮件", "邀请", "邀请函", "发件人", "收件人", "主题", "尊敬", "您好", "亲爱的",
                "公告", "通知", "计划", "权益", "活动说明", "申请", "介绍", "文章", "附件",
                "email", "invite", "invitation", "subject", "dear", "announcement", "program");
        if (!hasPrimaryKeywords) {
            return false;
        }
        return normalized.length() >= 80
                || countMeaningfulLines(rawText) >= 3
                || hasImage;
    }

    private static boolean isStrongTransactionalCategory(@Nullable String categoryCode) {
        return CategoryCatalog.CODE_LIFE_PICKUP.equals(categoryCode)
                || CategoryCatalog.CODE_LIFE_DELIVERY.equals(categoryCode)
                || CategoryCatalog.CODE_LIFE_TICKET.equals(categoryCode)
                || CategoryCatalog.CODE_LIFE_CARD.equals(categoryCode);
    }

    private static boolean isWorkActionCategory(@Nullable String categoryCode) {
        return CategoryCatalog.CODE_WORK_SCHEDULE.equals(categoryCode)
                || CategoryCatalog.CODE_WORK_TODO.equals(categoryCode);
    }

    private static int countMeaningfulLines(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        String[] lines = value.split("\\r?\\n");
        int count = 0;
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static String joinNonBlank(@Nullable String first, @Nullable String second) {
        String a = first == null ? "" : first.trim();
        String b = second == null ? "" : second.trim();
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        return a + "\n" + b;
    }

    private static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace('\r', '\n')
                .replaceAll("[\\t\\x0B\\f ]+", " ")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private static boolean containsAny(String source, String... keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
