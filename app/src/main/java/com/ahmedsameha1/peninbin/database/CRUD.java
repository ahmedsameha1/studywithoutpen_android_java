package com.ahmedsameha1.peninbin.database;

import com.ahmedsameha1.peninbin.adapters.WrongItem;

import java.util.ArrayList;

/**
 * Created by ahmed on 9/30/17.
 */

public interface CRUD {
    void increaseWrongAnswersForEntriesWithSameAnswer(long field_id, String answer, int check_type);
    String[] prepareRegexForAnswers (long field_id, int check_type );
    void increaseWrongAnswersForEntriesWithSameAnswer(String [] regex_answer, String answer, int check_type);
    void pauseStudyAPeriod(long field_id, String[][] listForTest, int[] session_data, ArrayList<Integer> repeated_entries, long lastPause);
    void putEntriesTestOrderEnhancedTest(int question_data_type, String[][] array);
    void clearThisFieldFromTestPausedEnhancedTest(long field_id);
    void putTestPausedDataEnhancedTest(long field_id, int[] array, long last_pause);
    void putEntriesTestOrderFullyRandomTest(int question_data_type, String[][] array);
    void clearThisFieldFromTestPausedFullyRandomTest(long field_id);
    void putTestPausedDataFullyRandomTest(long field_id, int[] array, long last_pause);
    void pauseStudyAgainAfterTest(int study_till_correct_type, long field_id,
                                  ArrayList<WrongItem> wrongItemArrayList,
                                  int[] session_data, ArrayList<Integer> repeated_entries, boolean in_repeated, long last_pause);
}
