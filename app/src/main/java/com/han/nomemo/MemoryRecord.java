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
    private final String sourceText;
    private final String imageUri;
    private final String analysis;
    private final String memory;
    private final String engine;
    private final String categoryGroupCode;
    private final String categoryCode;
    private final String categoryName;
    private final long reminderAt;
    private final boolean reminderDone;

    public MemoryRecord(
            long createdAt,
            String mode,
            String sourceText,
            String imageUri,
            String analysis,
            String memory,
            String engine
    ) {
        this(
                UUID.randomUUID().toString(),
                createdAt,
                mode,
                sourceText,
                imageUri,
                analysis,
                memory,
                engine,
                CategoryCatalog.GROUP_LIFE,
                CategoryCatalog.CODE_LIFE_DELIVERY,
                "快递",
                0L,
                false
        );
    }

    public MemoryRecord(
            long createdAt,
            String mode,
            String sourceText,
            String imageUri,
            String analysis,
            String memory,
            String engine,
            String categoryGroupCode,
            String categoryCode,
            String categoryName,
            long reminderAt,
            boolean reminderDone
    ) {
        this(
                UUID.randomUUID().toString(),
                createdAt,
                mode,
                sourceText,
                imageUri,
                analysis,
                memory,
                engine,
                categoryGroupCode,
                categoryCode,
                categoryName,
                reminderAt,
                reminderDone
        );
    }

    public MemoryRecord(
            String recordId,
            long createdAt,
            String mode,
            String sourceText,
            String imageUri,
            String analysis,
            String memory,
            String engine,
            String categoryGroupCode,
            String categoryCode,
            String categoryName,
            long reminderAt,
            boolean reminderDone
    ) {
        this.recordId = recordId;
        this.createdAt = createdAt;
        this.mode = mode;
        this.sourceText = sourceText;
        this.imageUri = imageUri;
        this.analysis = analysis;
        this.memory = memory;
        this.engine = engine;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.reminderAt = reminderAt;
        this.reminderDone = reminderDone;
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

    public String getSourceText() {
        return sourceText;
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

    public MemoryRecord withReminderDone(boolean done) {
        return new MemoryRecord(
                recordId,
                createdAt,
                mode,
                sourceText,
                imageUri,
                analysis,
                memory,
                engine,
                categoryGroupCode,
                categoryCode,
                categoryName,
                reminderAt,
                done
        );
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("recordId", recordId);
        json.put("createdAt", createdAt);
        json.put("mode", mode);
        json.put("sourceText", sourceText);
        json.put("imageUri", imageUri);
        json.put("analysis", analysis);
        json.put("memory", memory);
        json.put("engine", engine);
        json.put("categoryGroupCode", categoryGroupCode);
        json.put("categoryCode", categoryCode);
        json.put("categoryName", categoryName);
        json.put("reminderAt", reminderAt);
        json.put("reminderDone", reminderDone);
        return json;
    }

    public static MemoryRecord fromJson(JSONObject json) throws JSONException {
        long createdAt = json.optLong("createdAt", System.currentTimeMillis());
        String categoryCode = json.optString("categoryCode", CategoryCatalog.CODE_LIFE_DELIVERY);
        String categoryGroup = json.optString("categoryGroupCode", CategoryCatalog.getGroupByCategoryCode(categoryCode));
        String categoryName = json.optString("categoryName", CategoryCatalog.getCategoryName(categoryCode));
        return new MemoryRecord(
                json.optString("recordId", UUID.randomUUID().toString()),
                createdAt,
                json.optString("mode", MODE_NORMAL),
                json.optString("sourceText", ""),
                json.optString("imageUri", ""),
                json.optString("analysis", ""),
                json.optString("memory", ""),
                json.optString("engine", "manual"),
                categoryGroup,
                categoryCode,
                categoryName,
                json.optLong("reminderAt", 0L),
                json.optBoolean("reminderDone", false)
        );
    }
}
