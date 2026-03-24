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

    private final SharedPreferences preferences;

    public MemoryStore(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized List<MemoryRecord> loadRecords() {
        List<MemoryRecord> records = new ArrayList<>();
        String raw = preferences.getString(KEY_RECORDS, "[]");
        try {
            JSONArray jsonArray = new JSONArray(raw);
            for (int i = 0; i < jsonArray.length(); i++) {
                records.add(MemoryRecord.fromJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException ignored) {
            preferences.edit().putString(KEY_RECORDS, "[]").apply();
        }
        return records;
    }

    public synchronized void prependRecord(MemoryRecord record) {
        List<MemoryRecord> records = loadRecords();
        records.add(0, record);
        if (records.size() > MAX_RECORDS) {
            records = records.subList(0, MAX_RECORDS);
        }
        persist(records);
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
        }
    }

    public synchronized List<MemoryRecord> loadReminderRecords() {
        List<MemoryRecord> all = loadRecords();
        List<MemoryRecord> reminders = new ArrayList<>();
        for (MemoryRecord record : all) {
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

    private void persist(List<MemoryRecord> records) {
        JSONArray jsonArray = new JSONArray();
        for (MemoryRecord record : records) {
            try {
                jsonArray.put(record.toJson());
            } catch (JSONException ignored) {
                // Ignore malformed records and keep remaining data.
            }
        }
        preferences.edit().putString(KEY_RECORDS, jsonArray.toString()).apply();
    }
}
