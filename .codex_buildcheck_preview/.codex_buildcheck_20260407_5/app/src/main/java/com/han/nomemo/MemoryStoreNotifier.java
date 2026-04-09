package com.han.nomemo;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

public final class MemoryStoreNotifier {
    public static final String ACTION_RECORDS_CHANGED = "com.han.nomemo.ACTION_RECORDS_CHANGED";
    public static final String EXTRA_RECORD_ID = "extra_record_id";

    private MemoryStoreNotifier() {
    }

    public static void notifyChanged(Context context, @Nullable String recordId) {
        Intent intent = new Intent(ACTION_RECORDS_CHANGED).setPackage(context.getPackageName());
        if (recordId != null && !recordId.trim().isEmpty()) {
            intent.putExtra(EXTRA_RECORD_ID, recordId);
        }
        context.sendBroadcast(intent);
    }
}
