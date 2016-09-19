package com.ahmedsameha1.peninbin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmedsameha1.peninbin.utils.Constants;

import java.util.ArrayList;

/**
 * Created by ahmed_000 on 8/31/2015.
 */
public class DatabaseReadOperations {
    public static final String EXPORT_DIRECTORY_NAME = "StudyWithoutPen";
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseReadOperations(Context context) {
        this.context = context;
        sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public Cursor getFieldList() {
        String[] string = {Contract.COLUMN_ID, Contract.Field.COLUMN_FIELD_NAME, Contract.Field.COLUMN_FIELD_TYPE};
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, string, null, null, null, null, null);
        return cursor;
    }

    public Cursor getNonNotebookFieldList() {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,
                new String[]{Contract.COLUMN_ID, Contract.Field.COLUMN_FIELD_NAME, Contract.Field.COLUMN_FIELD_TYPE}
                , Contract.Field.COLUMN_FIELD_TYPE + " != ? ", new String[]{Constants.FieldTypes.NOTEBOOK_TYPE + ""},
                null, null, null);
        return cursor;
    }

    public boolean checkFieldExists( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,
                new String[]{Contract.COLUMN_ID},Contract.COLUMN_ID + " = ? ",new String[]{field_id +""},null,null,null);
        boolean i = cursor.getCount() > 0;
        cursor.close();
        return i;
    }

    public boolean checkEntryDuplication(String question, String answer, long field_id, int question_data_type) {
        boolean thereIsduplication;
        long question_id, answer_id;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                    Contract.STring.COLUMN_VALUE + " = ? ",
                    new String[]{question}, null, null, null);
            if (questionCursor.moveToFirst()) {
                question_id = questionCursor.getLong(0);
                questionCursor.close();
            } else {
                return false;
            }
            Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                    Contract.STring.COLUMN_VALUE + " = ? ",
                    new String[]{answer}, null, null, null);
            if (answerCursor.moveToFirst()) {
                answer_id = answerCursor.getLong(0);
                answerCursor.close();
            } else {
                return false;
            }
            Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, null,
                    Contract.String_Entry.COLUMN_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_ANSWER + " = ? AND "
                            + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null);
            thereIsduplication = cursor.getCount() > 0;
            cursor.close();
            return thereIsduplication;
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
        return true;
    }

    public int getQuestionDataType(long field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE},
                Contract.COLUMN_ID + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        int question_data_type = cursor.getInt(0);
        cursor.close();
        return question_data_type;
    }

    public boolean checkIfIncludedInPausedOperations(long field_id, long entry_id) {
        Cursor cursor = null;
        if (getQuestionDataType(field_id) == Contract.Field.STRING_DATA_TYPE) {
            cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER},
                    Contract.COLUMN_ID + " = ? AND ( " + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER + " >= ? OR "
                            + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " >= ? )",
                    new String[]{entry_id + "", "0", "0"}, null, null, null);
        } else if (getQuestionDataType(field_id) == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
        if (cursor == null || cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public int[] getRandomQuickTestData(long field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,
                new String[]{Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES,
                        Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS,Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION,
                        Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL}
                , Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        int number_of_quick_tests = cursor.getInt(0);
        int number_of_tries = cursor.getInt(1);
        int number_of_questions = cursor.getInt(2);
        int controlled_portion = cursor.getInt(3);
        int wrongness_level = cursor.getInt(4);
        int rank_level = cursor.getInt(5);
        if (number_of_quick_tests > 0 && number_of_tries > 0 && number_of_questions > 0) {
            cursor.close();
            return new int[]{number_of_quick_tests, number_of_tries, number_of_questions,controlled_portion,wrongness_level,rank_level};
        } else {
            cursor.close();
            return null;
        }
    }

    public String[][] get_excluded_times(long field_id) {
        String[][] excluded_times = new String[5][];
        Cursor cursor = sqLiteDatabase.query(Contract.Excluded_Time.TABLE_NAME,
                new String[]{Contract.Excluded_Time.COLUMN_STARTS, Contract.Excluded_Time.COLUMN_ENDS},
                Contract.Excluded_Time.COLUMN_FIELD + " = ? ", new String[]{field_id + ""}, null, null, null);
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            excluded_times[cursor.getPosition()] = new String[]{cursor.getString(0), cursor.getString(1)};
        }
        cursor.close();
        return excluded_times;
    }

    public String getFieldLocale(long field_id) {
        String locale;
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_LOCALE},
                Contract.COLUMN_ID + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        if (cursor.isNull(0)) {
            locale = null;
        } else {
            locale = cursor.getString(0);
        }
        cursor.close();
        return locale;
    }
    public int getEntryRank( long entry_id ) {
        int rank;
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[]{Contract.String_Entry.COLUMN_RANK},
                Contract.COLUMN_ID + " = ? ",new String[]{entry_id + ""},null,null,null);
        cursor.moveToFirst();
        rank = cursor.getInt(0);
        cursor.close();
        return rank;
    }
    public boolean isReadAnswer(long field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_READ_ANSWER},
                Contract.COLUMN_ID + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        int i;
        i = cursor.getInt(0);
        cursor.close();
        return i > 0;
    }

    public int getFieldCheckType(long field_id) {
        int i;
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_CHECK_TYPE},
                Contract.COLUMN_ID + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        i = cursor.getInt(0);
        cursor.close();
        return i;
    }

    public String[][] getWrongAnswersEnhancedTest(long field_id, int question_data_type) {
        String[][] wrong_answers;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor wrong_answers_cursor = sqLiteDatabase.rawQuery("SELECT " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + ","
                    + Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER + " FROM " + Contract.Test_Wrong_Answer_String.TABLE_NAME
                    + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " IN ( SELECT " + Contract.COLUMN_ID + " FROM "
                    + Contract.String_Entry.TABLE_NAME + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = ? )"
                    + " AND " + Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE + " = ? "
                    , new String[]{field_id + "", Constants.TestType.ENHANCED_TEST + ""});
            wrong_answers = new String[wrong_answers_cursor.getCount()][2];
            for (wrong_answers_cursor.moveToFirst(); wrong_answers_cursor.getPosition() < wrong_answers_cursor.getCount();
                 wrong_answers_cursor.moveToNext()) {
                wrong_answers[wrong_answers_cursor.getPosition()][0] = Long.toString(wrong_answers_cursor.getLong(0));
                wrong_answers[wrong_answers_cursor.getPosition()][1] = wrong_answers_cursor.getString(1);
            }
            wrong_answers_cursor.close();
            return wrong_answers;
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            return null;
        } else {
            return null;
        }
    }

    public String[][] getWrongAnswersFullyRandomTest(long field_id, int question_data_type) {
        String[][] wrong_answers;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor wrong_answers_cursor = sqLiteDatabase.rawQuery("SELECT " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + ","
                    + Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER + " FROM " + Contract.Test_Wrong_Answer_String.TABLE_NAME
                    + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " IN ( SELECT " + Contract.COLUMN_ID + " FROM "
                    + Contract.String_Entry.TABLE_NAME + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = ? )"
                    + " AND " + Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE + " = ? "
                    , new String[]{field_id + "", Constants.TestType.FULLY_RANDOM_TEST + ""});
            wrong_answers = new String[wrong_answers_cursor.getCount()][2];
            for (wrong_answers_cursor.moveToFirst(); wrong_answers_cursor.getPosition() < wrong_answers_cursor.getCount();
                 wrong_answers_cursor.moveToNext()) {
                wrong_answers[wrong_answers_cursor.getPosition()][0] = Long.toString(wrong_answers_cursor.getLong(0));
                wrong_answers[wrong_answers_cursor.getPosition()][1] = wrong_answers_cursor.getString(1);
            }
            wrong_answers_cursor.close();
            return wrong_answers;
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            return null;
        } else {
            return null;
        }
    }

    /*public ArrayList<String> getEntryWrongAnswers(String question,String answer, long field_id ,int question_data_type ) {
        Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.STring.COLUMN_VALUE + " = ? ",
                new String[]{question}, null, null, null);
        questionCursor.moveToFirst();
        long question_id = questionCursor.getLong(0);
        questionCursor.close();
        Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.STring.COLUMN_VALUE + " = ? ",
                new String[]{answer}, null, null, null);
        answerCursor.moveToFirst();
        long answer_id = answerCursor.getLong(0);
        answerCursor.close();
        Cursor entryCursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_ANSWER + " = ? AND "
                        + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null);
        entryCursor.moveToFirst();
        long entry_id = entryCursor.getLong(0);
        entryCursor.close();
        ArrayList<String> wrongAnswers = new ArrayList<>();
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE ) {
            Cursor wrong_answers_cursor = sqLiteDatabase.query(Contract.Test_Wrong_Answer_String.TABLE_NAME,new String[]{Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER},
                    Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " = ? ",
                    new String[]{entry_id + ""},null,null,null);
            if ( wrong_answers_cursor.moveToFirst() ) {
                for ( wrong_answers_cursor.moveToFirst(); wrong_answers_cursor.getPosition() < wrong_answers_cursor.getCount();
                      wrong_answers_cursor.moveToNext() ) {
                    wrongAnswers.add(wrong_answers_cursor.getString(0));
                }
            }
            wrong_answers_cursor.close();
        } else if ( question_data_type == Contract.Field.NOT_STRING_DATA_TYPE ) {
        }
        return wrongAnswers;
    }*/
    public ArrayList<Integer> getStudyPeriodRepeatedEntries(long field_id) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(Contract.Study_Period_Repeated_Entry.TABLE_NAME, new String[]{Contract.Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY},
                Contract.Study_Period_Repeated_Entry.COLUMN_FIELD + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return arrayList;
        }
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            arrayList.add(cursor.getInt(0));
        }
        cursor.close();
        return arrayList;
    }

    public int[] getDataFromStudyPeriodPaused(long field_id) {
        int[] array = new int[2];
        Cursor cursor = sqLiteDatabase.query(Contract.Study_Period_Paused.TABLE_NAME,
                new String[]{Contract.Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER, Contract.Study_Period_Paused.COLUMN_TRIES_NUMBER},
                Contract.Study_Period_Paused.COLUMN_FIELD + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        array[0] = cursor.getInt(0);
        array[1] = cursor.getInt(1);
        cursor.close();
        return array;
    }

    public boolean checkFieldDuplication(String fieldName) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_NAME},
                Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                new String[]{fieldName}, null, null, null);
        boolean temp = cursor.getCount() != 0;
        cursor.close();
        return temp;
    }





    public boolean checkEntriesForAnyTestOrderEnhancedTest(long field_id, int question_data_type) {
        boolean result = false;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.String_Entry.TABLE_NAME
                    + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = ?  AND " + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER
                    + " >= ? ", new String[]{field_id + "", "0"});
            cursor.moveToFirst();
            int number = cursor.getInt(0);
            cursor.close();
            result = number > 0;
        }
        return result;
    }
    public boolean checkEntriesForAnyTestOrderFullyRandomTest(long field_id, int question_data_type) {
        boolean result = false;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.String_Entry.TABLE_NAME
                    + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = ?  AND " + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER
                    + " >= ? ", new String[]{field_id + "", "0"});
            cursor.moveToFirst();
            int number = cursor.getInt(0);
            cursor.close();
            result = number > 0;
        }
        return result;
    }

    public boolean isThisFieldTestPausedEnhancedTest(long field_id) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.Test_Paused.TABLE_NAME
                        + " WHERE " + Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.ENHANCED_TEST + ""});
        cursor.moveToFirst();
        int number = cursor.getInt(0);
        cursor.close();
        return number == 1;
    }

    public boolean isThisFieldTestPausedFullyRandomTest(long field_id) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.Test_Paused.TABLE_NAME
                        + " WHERE " + Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.FULLY_RANDOM_TEST + ""});
        cursor.moveToFirst();
        int number = cursor.getInt(0);
        cursor.close();
        return number == 1;
    }

    public boolean isThisFieldStudyPeriodPaused(long field_id) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.Study_Period_Paused.TABLE_NAME
                        + " WHERE " + Contract.Study_Period_Paused.COLUMN_FIELD + " = ? ",
                new String[]{field_id + ""});
        cursor.moveToFirst();
        int number = cursor.getInt(0);
        cursor.close();
        return number == 1;
    }


    public int getFieldType(long field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_TYPE},
                Contract.COLUMN_ID + " = ? ",
                new String[]{field_id + ""}, null, null, null);
        cursor.moveToFirst();
        int n = cursor.getInt(0);
        cursor.close();
        return n;
    }

    public int getFieldSort(long field_id) {
        int sort = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_SORT_BY},
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            sort = cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return sort;
    }

    public int getFieldQuestionAreaSize(long field_id) {
        int question_area_size = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_QUESTION_AREA_SIZE},
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            question_area_size = cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return question_area_size;
    }

    public int getFieldAnswerAreaSize(long field_id) {
        int answer_area_size = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_ANSWER_AREA_SIZE},
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            answer_area_size = cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return answer_area_size;
    }

    public int getFieldTestTextSize(long field_id) {
        int test_text_size = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_TEST_TEXT_SIZE},
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            test_text_size = cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return test_text_size;
    }

    public int getFieldActionBarShown(long field_id) {
        int action_bar_shown = 0;
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_ACTION_BAR_SHOWN},
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            action_bar_shown = cursor.getInt(0);
        } finally {
            cursor.close();
        }
        return action_bar_shown;
    }

    public int[] getDataFromTestPausedEnhancedTest(long field_id) {
        int[] array = new int[3];
        Cursor cursor = sqLiteDatabase.query(Contract.Test_Paused.TABLE_NAME,
                new String[]{Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER,
                        Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT, Contract.Test_Paused.COLUMN_TRIES_NUMBER},
                Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.ENHANCED_TEST + ""}, null, null, null);
        cursor.moveToFirst();
        array[0] = cursor.getInt(0);
        array[1] = cursor.getInt(1);
        array[2] = cursor.getInt(2);
        cursor.close();
        return array;
    }

    public int[] getDataFromTestPausedFullyRandomTest(long field_id) {
        int[] array = new int[3];
        Cursor cursor = sqLiteDatabase.query(Contract.Test_Paused.TABLE_NAME,
                new String[]{Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER,
                        Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT, Contract.Test_Paused.COLUMN_TRIES_NUMBER},
                Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.FULLY_RANDOM_TEST + ""}, null, null, null);
        cursor.moveToFirst();
        array[0] = cursor.getInt(0);
        array[1] = cursor.getInt(1);
        array[2] = cursor.getInt(2);
        cursor.close();
        return array;
    }

    public int getEntriesNumber(long field_id, int question_data_type) {
        int number = 0;
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.String_Entry.TABLE_NAME
                            + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
            cursor.moveToFirst();
            number = cursor.getInt(0);
            cursor.close();
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }

        return number;
    }

    public String getFieldName(long current_field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_NAME},
                Contract.COLUMN_ID + " = ? ", new String[]{current_field_id + ""}, null, null, null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();
        return name;
    }

    public ArrayList<long[]> getRandomQuickTestsSchedulingData() {
        ArrayList<long[]> data = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,
                new String[]{Contract.COLUMN_ID, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS,
                        Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS,
                        Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION, Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL,
                        Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL},
                null, null, null, null, null);
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            if (cursor.getInt(1) > 0) {
                data.add(new long[]{cursor.getLong(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
                cursor.getInt(4),cursor.getInt(5),cursor.getInt(6)});
            }
        }
        cursor.close();
        return data;
    }
    public ArrayList<long[]> getBackupSchedulingData() {
        ArrayList<long[]> data = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(Contract.Scheduled_Time_Backup.TABLE_NAME,
                new String[]{Contract.Scheduled_Time_Backup.COLUMN_FIELD, Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE,
                Contract.Scheduled_Time_Backup.COLUMN_TIME, Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST,
                        Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE, Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER,
                        Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES, Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_CONTROLLED_PORTION,
                        Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_RANK_LEVEL},
        null,null,null,null,null);
        if ( cursor.getCount() > 0 ) {
            for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                 cursor.moveToNext()) {
                data.add(new long[]{cursor.getLong(0),cursor.getInt(1),cursor.getLong(2),cursor.getInt(3),
                        (cursor.isNull(4)? -1 : cursor.getInt(4)),
                        (cursor.isNull(5)? -1 : cursor.getInt(5)),
                        (cursor.isNull(6)? -1 : cursor.getInt(6)), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9)});
            }
        }
        cursor.close();
        return data;
    }
    public Cursor getReminderDataForRescheduling() {
        return sqLiteDatabase.rawQuery("SELECT s." + Contract.STring.COLUMN_VALUE + " , se." + Contract.String_Entry.COLUMN_REMIND_AT
        + " , se." + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " , se." + Contract.COLUMN_ID
                + " FROM " + Contract.String_Entry.TABLE_NAME + " se INNER JOIN "
        + Contract.STring.TABLE_NAME + " s ON s." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
        + " WHERE " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " IS NOT NULL ",null);
        /*return sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                new String[]{Contract.String_Entry.COLUMN_ANSWER, Contract.String_Entry.COLUMN_REMIND_AT, Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST},
                Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " IS NOT NULL",null,null,null,null);*/
    }
    public Integer getReminderRequest( long entry_id ) {
        Integer request;
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[]{Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST},
                Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""},null,null,null);
        cursor.moveToFirst();
        if ( cursor.isNull(0) ) {
            request = null;
        } else {
            request = cursor.getInt(0);
        }
        cursor.close();
        return request;
    }
    public long getFieldId( String field_name ) {
        long field_id = -1;
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.COLUMN_ID},
                Contract.Field.COLUMN_FIELD_NAME + " = ? ", new String[]{field_name},null,null,null);
        cursor.moveToFirst();
        field_id = cursor.getLong(0);
        cursor.close();
        return field_id;
    }

    public void makeAskedCurrentTestRound( long [] ids ) {
        for ( long temp: ids ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, Constants.TestRound.ASKED_AT_CURRENT_ROUND);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME,contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{temp + ""});
        }
    }
    public void restWhetherAskedCurrentTestRound ( long field_id ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, Constants.TestRound.DID_NOT_ASKED_AT_CURRENT_ROUND);
        sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME,contentValues,
                Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{field_id + ""});
    }
    public int countAskedCurrentTestRound( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[]{Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND + " = ? ",
                new String[]{field_id + "", Constants.TestRound.ASKED_AT_CURRENT_ROUND + ""},null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int countModerateOrHigherEntries( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[]{Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                        + Contract.String_Entry.COLUMN_RANK + " > ? " ,
                new String[]{field_id + "", Constants.Rank.LOW +""},null,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public boolean isThereEntryOrder( long field_id ) {
        boolean there_is_entry_order = false;
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[] {Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " IS NOT NULL",
                new String[]{ field_id + ""},null,null,null);
        there_is_entry_order = cursor.getCount() > 0;
        cursor.close();
        return there_is_entry_order;
    }

    public boolean checkCustomableText( int field_type, String text ) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT s."
                        + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                        + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                        " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                        + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ?",
                new String[]{ field_type + ""});
        boolean contained = false;
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            if ( cursor.getString(0).contains(text) || ( cursor.getString(1) != null && cursor.getString(1).contains(text)) ) {
                contained = true;
                break;
            }
        }
        cursor.close();
        return contained;
    }

    public Cursor searchExactlyFor(long field_id, int question_data_type, String searchString) {
        Cursor cursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.STring.COLUMN_VALUE + " = ? ",
                new String[]{searchString}, null, null, null);
        cursor.moveToFirst();
        long searchString_id = cursor.getLong(0);
        cursor.close();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            /*return sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                    Contract.String_Entry.COLUMN_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{searchString_id + "", field_id + ""}, null, null, null);*/
            return sqLiteDatabase.rawQuery("SELECT a." + Contract.STring.COLUMN_VALUE
            + " FROM " + Contract.STring.TABLE_NAME + " a INNER JOIN " + Contract.String_Entry.TABLE_NAME
            + " ON a." + Contract.COLUMN_ID + " = " + Contract.String_Entry.COLUMN_ANSWER
            + " WHERE " + Contract.String_Entry.COLUMN_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{ searchString_id + "", field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
        return null;
    }


}

