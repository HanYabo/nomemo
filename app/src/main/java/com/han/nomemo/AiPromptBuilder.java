package com.han.nomemo;

import androidx.annotation.Nullable;

public final class AiPromptBuilder {
    public static final String PROMPT_VERSION = "nomemo-prompt-v3";
    public static final String SCHEMA_VERSION = "memory-facts-v1";

    private static final int FULL_IMAGE_MAX_SIZE = 1024;
    private static final int FULL_IMAGE_QUALITY = 80;
    private static final int ECONOMY_IMAGE_MAX_SIZE = 768;
    private static final int ECONOMY_IMAGE_QUALITY = 68;

    private AiPromptBuilder() {
    }

    public static AiPromptSpec build(
            String requestMode,
            boolean enhanced,
            boolean economy,
            @Nullable String userText,
            @Nullable String detailContext,
            @Nullable String localCandidatesJson
    ) {
        AiPromptMode mode = resolvePromptMode(enhanced, economy);
        String systemPrompt = economy ? economySystemPrompt(mode) : fullSystemPrompt(mode);
        String userPrompt = buildUserPrompt(
                requestMode,
                mode,
                economy,
                userText,
                detailContext,
                localCandidatesJson
        );
        return new AiPromptSpec(
                mode,
                PROMPT_VERSION,
                SCHEMA_VERSION,
                systemPrompt,
                userPrompt,
                resolveMaxTokens(requestMode, mode),
                economy ? 0.15 : 0.30,
                economy ? ECONOMY_IMAGE_MAX_SIZE : FULL_IMAGE_MAX_SIZE,
                economy ? ECONOMY_IMAGE_QUALITY : FULL_IMAGE_QUALITY
        );
    }

    public static String repairSystemPrompt() {
        return "You repair NoMemo JSON only. Return one valid JSON object matching schemaVersion "
                + SCHEMA_VERSION + ". Do not add Markdown. Do not reanalyze or invent facts.";
    }

    public static String repairUserPrompt(String rawModelOutput) {
        return "Repair this model output into the required JSON schema. Preserve existing values when possible, "
                + "fill missing scalar fields with concise Chinese text only if supported by the output, "
                + "and use null/0.0 for unsupported structured facts.\n\n"
                + "Required schema:\n" + schemaBlock() + "\n\n"
                + "Raw output:\n" + compact(rawModelOutput, 1800);
    }

    public static String schemaBlock() {
        return "{\n"
                + "  \"promptVersion\": \"" + PROMPT_VERSION + "\",\n"
                + "  \"schemaVersion\": \"" + SCHEMA_VERSION + "\",\n"
                + "  \"title\": \"5字内核心主题\",\n"
                + "  \"summary\": \"展示摘要，不作为详情卡片数据源\",\n"
                + "  \"analysis\": \"关键点提炼，保留硬事实\",\n"
                + "  \"memory\": \"规范化完整内容\",\n"
                + "  \"suggestedCategoryCode\": \"WORK_SCHEDULE|WORK_TODO|LIFE_PICKUP|LIFE_DELIVERY|LIFE_CARD|LIFE_TICKET|QUICK_NOTE\",\n"
                + "  \"structuredFacts\": {\n"
                + "    \"domain\": \"pickup|delivery|ticket|schedule|todo|card|note\",\n"
                + "    \"pickupCode\": null,\n"
                + "    \"pickupCodeType\": null,\n"
                + "    \"pickupCodeConfidence\": 0.0,\n"
                + "    \"pickupCodeEvidence\": null,\n"
                + "    \"location\": null,\n"
                + "    \"locationConfidence\": 0.0,\n"
                + "    \"locationEvidence\": null,\n"
                + "    \"merchantOrCompany\": null,\n"
                + "    \"itemName\": null,\n"
                + "    \"orderNumber\": null,\n"
                + "    \"trackingNumber\": null,\n"
                + "    \"amount\": null,\n"
                + "    \"timeWindow\": null,\n"
                + "    \"rawVisibleText\": null\n"
                + "  }\n"
                + "}";
    }

    private static AiPromptMode resolvePromptMode(boolean enhanced, boolean economy) {
        if (enhanced && economy) {
            return AiPromptMode.REANALYZE_ECONOMY;
        }
        if (enhanced) {
            return AiPromptMode.REANALYZE_FULL;
        }
        if (economy) {
            return AiPromptMode.ECONOMY;
        }
        return AiPromptMode.FULL;
    }

    @Nullable
    private static Integer resolveMaxTokens(String requestMode, AiPromptMode mode) {
        if (mode == AiPromptMode.FULL || mode == AiPromptMode.REANALYZE_FULL) {
            return null;
        }
        boolean enhanced = mode == AiPromptMode.REANALYZE_ECONOMY;
        if ("IMAGE".equals(requestMode)) {
            return enhanced ? 420 : 340;
        }
        if ("MULTIMODAL".equals(requestMode)) {
            return enhanced ? 460 : 380;
        }
        return enhanced ? 380 : 300;
    }

    private static String fullSystemPrompt(AiPromptMode mode) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Task\n");
        builder.append("You are NoMemo's enterprise information extraction engine. Create a memory card from user text, screenshot OCR, or visible content, and extract verifiable structured facts.\n\n");
        builder.append("# Classification\n");
        builder.append("- LIFE_PICKUP: takeout, coffee, milk tea, cafeteria, store pickup, takeout code, meal redeem code.\n");
        builder.append("- LIFE_DELIVERY: courier, package, station, Cainiao, Hive Box, locker, pickup code, shelf code, self-pickup code.\n");
        builder.append("- LIFE_TICKET: tickets, coupons, train/movie/flight/exhibition passes.\n");
        builder.append("- LIFE_CARD: ID, bank card, membership card, access card, certificate.\n");
        builder.append("- WORK_SCHEDULE: meeting, appointment, explicit date/time arrangement.\n");
        builder.append("- WORK_TODO: task or action to complete.\n");
        builder.append("- QUICK_NOTE: fallback when uncertain.\n\n");
        builder.append("# Fact Extraction\n");
        builder.append("- summary is display-only; detail cards consume structuredFacts.\n");
        builder.append("- Extract pickupCode and location only when supported by visible evidence.\n");
        builder.append("- pickupCodeEvidence and locationEvidence must be short evidence snippets from input/OCR.\n");
        builder.append("- Other fields may be null without evidence when uncertain.\n\n");
        builder.append("# Forbidden\n");
        builder.append("- Do not treat order numbers, tracking numbers, waybill numbers, phone numbers, amounts, dates, or times as pickup/takeout codes.\n");
        builder.append("- Do not invent facts. Use null and low confidence when unsupported.\n");
        builder.append("- Do not include Markdown or explanatory text.\n\n");
        builder.append("# Output JSON\n");
        builder.append("Return exactly one JSON object matching this schema. Include promptVersion and schemaVersion.\n");
        builder.append(schemaBlock());
        if (mode == AiPromptMode.REANALYZE_FULL) {
            builder.append("\n\n# Reanalysis\n");
            builder.append("- Treat this as a second pass. Preserve confirmed hard facts and improve title, summary, analysis and memory conservatively.\n");
            builder.append("- Existing structured facts are context, not authority; current raw evidence wins on conflict.\n");
        }
        return builder.toString();
    }

    private static String economySystemPrompt(AiPromptMode mode) {
        StringBuilder builder = new StringBuilder();
        builder.append("Return JSON only. promptVersion=").append(PROMPT_VERSION)
                .append(", schemaVersion=").append(SCHEMA_VERSION).append(".\n");
        builder.append("Use localCandidatesJson first: choose supported facts from candidates; only add facts directly visible in input/OCR.\n");
        builder.append("Never treat order/tracking/waybill/phone/amount/date/time as pickupCode. Unsupported facts must be null/0.0.\n");
        builder.append("summary is display-only; structuredFacts drives detail cards. Required schema:\n");
        builder.append(schemaBlock());
        if (mode == AiPromptMode.REANALYZE_ECONOMY) {
            builder.append("\nReanalyze conservatively; current raw evidence wins over old context.");
        }
        return builder.toString();
    }

    private static String buildUserPrompt(
            String requestMode,
            AiPromptMode mode,
            boolean economy,
            @Nullable String userText,
            @Nullable String detailContext,
            @Nullable String localCandidatesJson
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("promptVersion: ").append(PROMPT_VERSION).append('\n');
        builder.append("schemaVersion: ").append(SCHEMA_VERSION).append('\n');
        builder.append("promptMode: ").append(mode.name()).append('\n');
        builder.append("requestMode: ").append(requestMode).append('\n');
        builder.append("imageAttached: ").append(!"TEXT".equals(requestMode)).append("\n\n");

        if (!isBlank(localCandidatesJson)) {
            builder.append("localCandidatesJson:\n")
                    .append(compact(localCandidatesJson, economy ? 700 : 1200))
                    .append("\n\n");
        } else if (economy) {
            builder.append("localCandidatesJson: {}\n\n");
        }

        if (!isBlank(userText)) {
            builder.append(economy ? "compactText:\n" : "rawUserText:\n")
                    .append(compact(userText, economy ? 360 : 1800))
                    .append("\n\n");
        } else {
            builder.append("rawUserText: (none)\n\n");
        }

        if (!isBlank(detailContext)) {
            builder.append(economy ? "compactExistingContext:\n" : "existingMemoryContext:\n")
                    .append(compact(detailContext, economy ? 320 : 1600))
                    .append("\n\n");
        }

        if ("IMAGE".equals(requestMode)) {
            builder.append("Use the attached screenshot as the primary evidence. Put key visible OCR text in structuredFacts.rawVisibleText.\n");
        } else if ("MULTIMODAL".equals(requestMode)) {
            builder.append("Use both text and screenshot. When they conflict, prefer visible hard facts from the screenshot or explicit user text.\n");
        }
        builder.append("Produce concise Chinese fields. Preserve exact codes, dates, addresses and names.");
        return builder.toString();
    }

    private static String compact(@Nullable String value, int maxLength) {
        if (isBlank(value)) {
            return "";
        }
        String normalized = value
                .replace('\r', ' ')
                .replace('\n', ' ')
                .replaceAll("\\s+", " ")
                .trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength).trim();
    }

    private static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }
}
