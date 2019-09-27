package com.ahmedsameha1.peninbin.utils;

import java.util.HashMap;

/**
 * Created by ahmed on 12/9/17.
 */

public enum DataHolder {
    INSTANCE;

    private HashMap<String, String[][]> listsForTest = new HashMap<>();
    private HashMap<String, String[]> answers_regexes = new HashMap<>();

    public static void setListForTest(String key, final String[][] entries_array) {
        INSTANCE.listsForTest.put(key, entries_array);
    }

    public static void setAnswersRegexes(String key, final String[] answers_regexes) {
        INSTANCE.answers_regexes.put(key, answers_regexes);
    }

    public static String[][] getListForTest(String key) {
        return INSTANCE.listsForTest.remove(key);
    }

    public static String[] getAnswersRegexes(String key) {
        return INSTANCE.answers_regexes.remove(key);
    }
}
