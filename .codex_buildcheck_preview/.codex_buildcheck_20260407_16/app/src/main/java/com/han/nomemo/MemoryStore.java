package com.han.nomemo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoryStore {
    private static final String PREF_NAME = "no_memo_pref";
    private static final String KEY_RECORDS = "records";
    private static final int MAX_RECORDS = 200;

    private final Context appContext;
    private final SharedPreferences preferences;
    private String cachedRawRecords;
    private List<MemoryRecord> cachedRecords;

    public MemoryStore(Context context) {
        appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized List<MemoryRecord> loadRecords() {
        String raw = preferences.getString(KEY_RECORDS, "[]");
        if (raw != null && raw.equals(cachedRawRecords) && cachedRecords != null) {
            return new ArrayList<>(cachedRecords);
        }
        List<MemoryRecord> records = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(raw);
            for (int i = 0; i < jsonArray.length(); i++) {
                records.add(MemoryRecord.fromJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException ignored) {
            preferences.edit().putString(KEY_RECORDS, "[]").apply();
            raw = "[]";
        }
        // Ensure images are stored in app private files directory to avoid cache cleanup data loss.
        boolean updatedAny = false;
        List<MemoryRecord> normalized = new ArrayList<>();
        for (MemoryRecord r : records) {
            String img = r.getImageUri();
            String normalizedUri = img == null ? "" : img;
            if (!normalizedUri.isEmpty()) {
                try {
                    android.net.Uri parsed = android.net.Uri.parse(normalizedUri);
                    String scheme = parsed.getScheme();
                    String copied = null;
                    if ("file".equalsIgnoreCase(scheme)) {
                        copied = ImageUtils.INSTANCE.migrateFileUriToPrivateStorage(appContext, parsed);
                    } else {
                        copied = ImageUtils.INSTANCE.copyUriToCache(appContext, parsed);
                    }
                    if (copied != null && !copied.isEmpty() && !copied.equals(normalizedUri)) {
                        normalizedUri = copied;
                    }
                } catch (Exception ignored) {
                }
            }
            if (!normalizedUri.equals(img)) {
                // create updated record with new imageUri
                MemoryRecord updated = new MemoryRecord(
                        r.getRecordId(),
                        r.getCreatedAt(),
                        r.getMode(),
                        r.getTitle(),
                        r.getSummary(),
                        r.getSourceText(),
                        r.getNote(),
                        normalizedUri,
                        r.getAnalysis(),
                        r.getMemory(),
                        r.getEngine(),
                        r.getCategoryGroupCode(),
                        r.getCategoryCode(),
                        r.getCategoryName(),
                        r.getReminderAt(),
                        r.isReminderDone(),
                        r.isArchived()
                );
                normalized.add(updated);
                updatedAny = true;
            } else {
                normalized.add(r);
            }
        }
        if (updatedAny) {
            records = normalized;
            // persist normalized records
            persist(records);
            cachedRawRecords = new org.json.JSONArray().toString();
        }
        sortNewestFirst(records);
        cachedRawRecords = raw;
        cachedRecords = new ArrayList<>(records);
        return records;
    }

    public synchronized List<MemoryRecord> loadActiveRecords() {
        List<MemoryRecord> active = new ArrayList<>();
        for (MemoryRecord record : loadRecords()) {
            if (!record.isArchived()) {
                active.add(record);
            }
        }
        return active;
    }

    public synchronized List<MemoryRecord> loadArchivedRecords() {
        List<MemoryRecord> archived = new ArrayList<>();
        for (MemoryRecord record : loadRecords()) {
            if (record.isArchived()) {
                archived.add(record);
            }
        }
        return archived;
    }

    public synchronized void prependRecord(MemoryRecord record) {
        List<MemoryRecord> records = loadRecords();
        records.removeIf(item -> item.getRecordId().equals(record.getRecordId()));
        records.add(0, record);
        if (records.size() > MAX_RECORDS) {
            records = new ArrayList<>(records.subList(0, MAX_RECORDS));
        }
        persist(records);
        MemoryStoreNotifier.notifyChanged(appContext, record.getRecordId());
    }

    public synchronized void updateReminderDone(String recordId, boolean done) {
        List<MemoryRecord> records = loadRecords();
        boolean changed = false;
        for (int i = 0; i < records.size(); i++) {
            MemoryRecord record = records.get(i);
            if (record.getRecordId().equals(recordId)) {
                records.set(i, record.withReminderDone(done));
                changed = true;
                break;
            }
        }
        if (changed) {
            persist(records);
            MemoryStoreNotifier.notifyChanged(appContext, recordId);
        }
    }

    public synchronized void archiveRecord(String recordId, boolean archived) {
        List<MemoryRecord> records = loadRecords();
        boolean changed = false;
        for (int i = 0; i < records.size(); i++) {
            MemoryRecord record = records.get(i);
            if (record.getRecordId().equals(recordId)) {
                records.set(i, record.withArchived(archived));
                changed = true;
                break;
            }
        }
        if (changed) {
            persist(records);
            MemoryStoreNotifier.notifyChanged(appContext, recordId);
        }
    }

    public synchronized boolean deleteRecord(String recordId) {
        List<MemoryRecord> records = loadRecords();
        boolean changed = false;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getRecordId().equals(recordId)) {
                records.remove(i);
                changed = true;
                break;
            }
        }
        if (changed) {
            persist(records);
            MemoryStoreNotifier.notifyChanged(appContext, recordId);
        }
        return changed;
    }

    public synchronized boolean updateRecord(MemoryRecord updatedRecord) {
        if (updatedRecord == null) {
            return false;
        }
        List<MemoryRecord> records = loadRecords();
        boolean changed = false;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getRecordId().equals(updatedRecord.getRecordId())) {
                records.set(i, updatedRecord);
                changed = true;
                break;
            }
        }
        if (changed) {
            persist(records);
            MemoryStoreNotifier.notifyChanged(appContext, updatedRecord.getRecordId());
        }
        return changed;
    }

    public synchronized MemoryRecord findRecordById(String recordId) {
        if (recordId == null || recordId.trim().isEmpty()) {
            return null;
        }
        for (MemoryRecord record : loadRecords()) {
            if (recordId.equals(record.getRecordId())) {
                return record;
            }
        }
        return null;
    }

    public synchronized List<MemoryRecord> loadReminderRecords() {
        List<MemoryRecord> reminders = new ArrayList<>();
        for (MemoryRecord record : loadActiveRecords()) {
            if (CategoryCatalog.isReminderCategory(record.getCategoryCode()) || record.getReminderAt() > 0L) {
                reminders.add(record);
            }
        }
        Collections.sort(reminders, Comparator.comparingLong(record -> {
            long time = record.getReminderAt();
            return time > 0L ? time : record.getCreatedAt();
        }));
        return reminders;
    }

    public synchronized void clearAll() {
        preferences.edit().putString(KEY_RECORDS, "[]").apply();
        cachedRawRecords = "[]";
        cachedRecords = new ArrayList<>();
        MemoryStoreNotifier.notifyChanged(appContext, null);
    }

    private void persist(List<MemoryRecord> records) {
        JSONArray jsonArray = new JSONArray();
        for (MemoryRecord record : records) {
            try {
                jsonArray.put(record.toJson());
            } catch (JSONException ignored) {
                // Ignore malformed records and keep remaining data.
            }
        }
        String raw = jsonArray.toString();
        preferences.edit().putString(KEY_RECORDS, raw).apply();
        cachedRawRecords = raw;
        cachedRecords = new ArrayList<>(records);
    }

    private void sortNewestFirst(List<MemoryRecord> records) {
        records.sort((left, right) -> Long.compare(right.getCreatedAt(), left.getCreatedAt()));
    }
}
