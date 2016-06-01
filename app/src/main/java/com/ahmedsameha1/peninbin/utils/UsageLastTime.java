package com.ahmedsameha1.peninbin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ahmed on 29/05/16.
 */
public class UsageLastTime {
    private static final String USAGE_LAST_TIME = "usage_last_time";

    public static void updateUsageLastTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(USAGE_LAST_TIME,System.currentTimeMillis());
        editor.apply();
    }
    public static long getUsageLastTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(USAGE_LAST_TIME,System.currentTimeMillis());
    }
}
