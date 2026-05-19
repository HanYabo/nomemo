package com.han.nomemo;

import android.content.Context;
import android.content.Intent;

public final class GroupAlbumStoreNotifier {
    public static final String ACTION_ALBUMS_CHANGED = "com.han.nomemo.ACTION_GROUP_ALBUMS_CHANGED";

    private GroupAlbumStoreNotifier() {
    }

    public static void notifyChanged(Context context) {
        Intent intent = new Intent(ACTION_ALBUMS_CHANGED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
