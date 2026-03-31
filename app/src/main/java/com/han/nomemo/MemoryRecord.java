package com.han.nomemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MemoryRecord {
    public static final String MODE_NORMAL = "NORMAL";
    public static final String MODE_AI = "AI";

    private final String recordId;
    private final long createdAt;
    private final String mode;
    private final String title;
    private final String summary;
    private final String sourceText;
    private final String note;
    private final String imageUri;
    private final String analysis;
    private final String memory;
    private final String engine;
    private final String categoryGroupCode;
    private final String categoryCode;
    private final String categoryName;
    private final long reminderAt;
    private final boolean reminderDone;
    private final boolean archived;

    public MemoryRecord(
            long createdAt,
            String mode,
            String title,
            String summary,
            String sourceText,
            String note,
            String imageUri,
            String analysis,
            String memory,
            String engine,
            String categoryGroupCode,
            String categoryCode,
            String categoryName,
            long reminderAt,
            boolean reminderDone,
            boolean archived
    ) {
        this(
                UUID.randomUUID().toString(),
                createdAt,
                mode,
                title,
                summary,
                sourceText,
                note,
                imageUri,
                analysis,
                memory,
                engine,
                categoryGroupCode,
                categoryCode,
                categoryName,
                reminderAt,
                reminderDone,
                archived
        );
    }

    public MemoryRecord(
            String recordId,
            long createdAt,
            String mode,
            String title,
            String summary,
            String sourceText,
            String note,
            String imageUri,
            String analysis,
            String memory,
            String engine,
            String categoryGroupCode,
            String categoryCode,
            String categoryName,
            long reminderAt,
            boolean reminderDone,
            boolean archived
    ) {
        this.recordId = recordId;
        this.createdAt = createdAt;
        this.mode = mode;
        this.title = title;
        this.summary = summary;
        this.sourceText = sourceText;
        this.note = note;
        this.imageUri = imageUri;
        this.analysis = analysis;
        this.memory = memory;
        this.engine = engine;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.reminderAt = reminderAt;
        this.reminderDone = reminderDone;
        this.archived = archived;
    }

    public String getRecordId() {
        return recordId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getMode() {
        return mode;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getSourceText() {
        return sourceText;
    }

    public String getNote() {
        return note;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getAnalysis() {
        return analysis;
    }

    public String getMemory() {
        return memory;
    }

    public String getEngine() {
        return engine;
    }

    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public long getReminderAt() {
        return reminderAt;
    }

    public boolean isReminderDone() {
        return reminderDone;
    }

    public boolean isArchived() {
        return archived;
    }

    public MemoryRecord withReminderDone(boolean done) {
        return new MemoryRecord(
                recordId,
                createdAt,
                mode,
                title,
                summary,
                sourceText,
                note,
                imageUri,
                analysis,
                memory,
                engine,
                categoryGroupCode,
                categoryCode,
                categoryName,
                reminderAt,
                done,
                archived
        );
    }

    public MemoryRecord withArchived(boolean archivedValue) {
        return new MemoryRecord(
                recordId,
                createdAt,
                mode,
                title,
                summary,
                sourceText,
                note,
                imageUri,
                analysis,
                memory,
                engine,
                categoryGroupCode,
                categoryCode,
                categoryName,
                reminderAt,
                reminderDone,
                archivedValue
        );
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("recordId", recordId);
        json.put("createdAt", createdAt);
        json.put("mode", mode);
        json.put("title", title);
        json.put("summary", summary);
        json.put("sourceText", sourceText);
        json.put("note", note);
        json.put("imageUri", imageUri);
        json.put("analysis", analysis);
        json.put("memory", memory);
        json.put("engine", engine);
        json.put("categoryGroupCode", categoryGroupCode);
        json.put("categoryCode", categoryCode);
        json.put("categoryName", categoryName);
        json.put("reminderAt", reminderAt);
        json.put("reminderDone", reminderDone);
        json.put("archived", archived);
        return json;
    }

    public static MemoryRecord fromJson(JSONObject json) throws JSONException {
        long createdAt = json.optLong("createdAt", System.currentTimeMillis());
        String categoryCode = json.optString("categoryCode", CategoryCatalog.CODE_QUICK_NOTE);
        String categoryGroup = json.optString(
                "categoryGroupCode",
                CategoryCatalog.getGroupByCategoryCode(categoryCode)
        );
        String categoryName = json.optString(
                "categoryName",
                CategoryCatalog.getCategoryName(categoryCode)
        );
        String memory = json.optString("memory", "");
        String sourceText = json.optString("sourceText", "");
        String title = json.optString("title", deriveTitle(memory, sourceText, categoryName));
        String summary = json.optString("summary", deriveSummary(memory, sourceText));
        String rawImageUri = json.optString("imageUri", "");
        String imageUri = rawImageUri == null ? "" : rawImageUri;
        if (!imageUri.isEmpty() && !imageUri.contains("://")) {
            imageUri = "file://" + imageUri;
        }

        return new MemoryRecord(
                json.optString("recordId", UUID.randomUUID().toString()),
                createdAt,
                json.optString("mode", MODE_NORMAL),
                title,
                summary,
                sourceText,
                json.optString("note", ""),
            imageUri,
                json.optString("analysis", ""),
                memory,
                json.optString("engine", "manual"),
                categoryGroup,
                categoryCode,
                categoryName,
                json.optLong("reminderAt", 0L),
                json.optBoolean("reminderDone", false),
                json.optBoolean("archived", false)
        );
    }

    private static String deriveTitle(String memory, String sourceText, String fallbackCategoryName) {
        String candidate = !isBlank(memory) ? memory : sourceText;
        if (isBlank(candidate)) {
            return fallbackCategoryName;
        }
        String singleLine = candidate.replace('\n', ' ').trim();
        return singleLine.length() > 24 ? singleLine.substring(0, 24) + "..." : singleLine;
    }

    private static String deriveSummary(String memory, String sourceText) {
        String candidate = !isBlank(sourceText) ? sourceText : memory;
        if (isBlank(candidate)) {
            return "";
        }
        String singleLine = candidate.replace('\n', ' ').trim();
        return singleLine.length() > 48 ? singleLine.substring(0, 48) + "..." : singleLine;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
