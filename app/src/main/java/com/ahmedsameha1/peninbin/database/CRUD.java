package com.ahmedsameha1.peninbin.database;

/**
 * Created by ahmed on 9/30/17.
 */

public interface CRUD {
    void increaseWrongAnswersForEntriesWithSameAnswer(long field_id, String answer, int check_type);
    String[] prepareRegexForAnswers (long field_id, int check_type );
    void increaseWrongAnswersForEntriesWithSameAnswer(String [] regex_answer, String answer, int check_type);
}
