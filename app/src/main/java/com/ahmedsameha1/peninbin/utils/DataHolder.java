package com.ahmedsameha1.peninbin.utils;

import java.util.HashMap;

/**
 * Created by ahmed on 12/9/17.
 */

public enum DataHolder {
    INSTANCE;

    private HashMap<String, String[][]> entries_arrays = new HashMap<>();

    public static boolean hasData() {
        return INSTANCE.entries_arrays != null;
    }

    public static void setData(String key, final String[][] entries_array) {
        INSTANCE.entries_arrays.put(key, entries_array);
    }

    public static String[][] getData(String key) {
        return INSTANCE.entries_arrays.remove(key);
    }
}
