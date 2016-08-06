package com.ahmedsameha1.peninbin.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.utils.Constants;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 30/07/16.
 */
public class ModifyDatabaseSchema {
    public static void onUpgradeDatabaseToDBV9AppV40(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Field.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Field.COLUMN_FIELD_NAME
                    + " , " + Contract.Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Field.COLUMN_LOCALE
                    + " , " + Contract.Field.COLUMN_CHECK_TYPE
                    + " , " + Contract.Field.COLUMN_SORT_BY
                    + " , " + Contract.Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Field.COLUMN_READ_ANSWER
                    + " , " + Contract.Field.COLUMN_USAGE_COUNT
                    + " , " + Contract.Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Field.COLUMN_FIELD_NAME
                    + " , " + Contract.Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Field.COLUMN_LOCALE
                    + " , " + Contract.Field.COLUMN_CHECK_TYPE
                    + " , " + Contract.Field.COLUMN_SORT_BY
                    + " , " + Contract.Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Field.COLUMN_READ_ANSWER
                    + " , " + Contract.Field.COLUMN_USAGE_COUNT
                    + " , " + Contract.Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.Scheduled_Time_Backup.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_SCHEDULED_TIME_BACKUP_TABLE);
            copy = "INSERT INTO " + Contract.Scheduled_Time_Backup.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_FIELD
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_TIME
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_FIELD
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_TIME
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    public static void onUpgradeDatabaseToDBV10AppV43(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);
            String copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Not_String_Entry.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_NOTES_TABLE);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TYPE_TABLE);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_SYMBOL_TABLE);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TYPE_SYMBOL_TABLE);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public static void migrateDataFromStudyDuringTempTableToLinkerStudyDuringFlagColumnV5 ( SQLiteDatabase sqLiteDatabase ) {
        Cursor fieldCursor = sqLiteDatabase.query(Contract.Old_Field_DBV6_AppV32.TABLE_NAME,
                new String[]{Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME},
                null, null, null, null, null);
        for ( fieldCursor.moveToFirst(); fieldCursor.getPosition() < fieldCursor.getCount();
              fieldCursor.moveToNext() ){
            Cursor studyDuringTempCursor = sqLiteDatabase.query(Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME,
                    new String[] {Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_QUESTION,
                            Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_ANSWER,
                            Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD},
                    Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD + " = ? ",
                    new String[] {fieldCursor.getString(0)},
                    null,null,null);
            for ( studyDuringTempCursor.moveToFirst(); studyDuringTempCursor.getPosition() < studyDuringTempCursor.getCount();
                  studyDuringTempCursor.moveToNext() ) {
                String question = studyDuringTempCursor.getString(0);
                String answer = studyDuringTempCursor.getString(1);
                String fieldName = studyDuringTempCursor.getString(2);
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,studyDuringTempCursor.getPosition());
                sqLiteDatabase.update(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, contentValues,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND "
                                + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                        new String[]{question, answer, fieldName});
            }
        }
        String drop = "DROP TABLE IF EXISTS " + Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForFieldTableV6 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_FIELD_TABLE);
        String copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }

    public static void onUpgradeDatabaseToDBV7AppV33( Context context, SQLiteDatabase sqLiteDatabase ) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.Field.COLUMN_FIELD_NAME
                    + " , " + Contract.Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.CREATION_DATE + " ) "
                    + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                    + "," + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE
                    + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);  // At this point the data of the old field table has been copied to the new field table
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            /////////////////////////////
            Cursor questionCursor = sqLiteDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY},
                    null,null,null,null,null);
            for ( questionCursor.moveToFirst(); questionCursor.getPosition() < questionCursor.getCount();
                  questionCursor.moveToNext() ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE,questionCursor.getString(0));
                sqLiteDatabase.insert(Contract.STring.TABLE_NAME,null,contentValues);
            }
            questionCursor.close();
            Cursor answerCursor = sqLiteDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY},
                    null,null,null,null,null);
            for ( answerCursor.moveToFirst(); answerCursor.getPosition() < answerCursor.getCount();
                  answerCursor.moveToNext() ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE,answerCursor.getString(0));
                sqLiteDatabase.insert(Contract.STring.TABLE_NAME,null,contentValues);
            }
            answerCursor.close();
            // at this point the needed data on the old question and old answer tables has been copied to the new STring table.
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            /////////////////////////////////
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Cursor linkerCursor = sqLiteDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER, Contract.Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS,Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32},
                    null,null,null,null,null,null);
            for ( linkerCursor.moveToFirst(); linkerCursor.getPosition() < linkerCursor.getCount();
                  linkerCursor.moveToNext() ) {
                String field = linkerCursor.getString(2);
                Cursor question_id_cursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(0)},null,null,null,null);
                question_id_cursor.moveToFirst();
                int question_id = question_id_cursor.getInt(0);
                Cursor answer_id_cursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(1)},null,null,null,null);
                answer_id_cursor.moveToFirst();
                int answer_id = answer_id_cursor.getInt(0);
                Cursor field_id_cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{field},null,null,null,null);
                field_id_cursor.moveToFirst();
                int field_id = field_id_cursor.getInt(0);
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION,question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER,answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD,field_id);
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER,linkerCursor.getInt(3));
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER,linkerCursor.getInt(4));
                contentValues.put(Contract.CREATION_DATE,linkerCursor.getString(6));
                long entry_id = sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME,null,contentValues);
                if ( linkerCursor.getString(5).length() > 0 ) {
                    StringTokenizer stringTokenizer = new StringTokenizer(linkerCursor.getString(5),"\t");
                    while ( stringTokenizer.hasMoreTokens() ) {
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_ENTRY,entry_id);
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER,stringTokenizer.nextToken());
                        sqLiteDatabase.insert(Contract.Test_Wrong_Answer_String.TABLE_NAME,null,contentValues);
                    }
                }
                Cursor test_paused_cursor = sqLiteDatabase.query(Contract.Test_Paused.TABLE_NAME,new String[]{Contract.Test_Paused.COLUMN_FIELD},
                        Contract.Test_Paused.COLUMN_FIELD + " = ? ",
                        new String[]{field_id + ""},null,null,null);
                if ( test_paused_cursor.getCount() <= 0 ) {
                    Cursor cursor = sqLiteDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                            new String[]{"0", field},null,null,null);
                    if ( cursor.getCount() > 0 ) {
                        int currentQuestionCounter = sharedPreferences.getInt(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.CURRENT_QUESTION_COUNTER
                                + EnhancedTestActivity.DASH + field,0);
                        int wrongAnswersCounter = sharedPreferences.getInt(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.NUMBER_OF_WRONG_ANSWERS
                                + EnhancedTestActivity.DASH + field,0);
                        int permitedTriesNumber = sharedPreferences.getInt(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.PERMITED_TRIES_NUMBER
                                + EnhancedTestActivity.DASH + field,0);
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Paused.COLUMN_FIELD,field_id);
                        contentValues.put(Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER,currentQuestionCounter);
                        contentValues.put(Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT,wrongAnswersCounter);
                        contentValues.put(Contract.Test_Paused.COLUMN_TRIES_NUMBER,permitedTriesNumber);
                        sqLiteDatabase.insert(Contract.Test_Paused.TABLE_NAME,null,contentValues);
                        editor.remove(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.CURRENT_QUESTION_COUNTER
                                + EnhancedTestActivity.DASH + field);
                        editor.remove(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.NUMBER_OF_WRONG_ANSWERS
                                + EnhancedTestActivity.DASH + field);
                        editor.remove(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.DASH + EnhancedTestActivity.PERMITED_TRIES_NUMBER
                                + EnhancedTestActivity.DASH + field);
                    }
                    cursor.close();
                }
                Cursor study_period_paused_cursor = sqLiteDatabase.query(Contract.Study_Period_Paused.TABLE_NAME,new String[]{Contract.Study_Period_Paused.COLUMN_FIELD},
                        Contract.Study_Period_Paused.COLUMN_FIELD + " = ? ",
                        new String[]{field_id + ""},null,null,null);
                if ( study_period_paused_cursor.getCount() <= 0 ) {
                    Cursor cursor = sqLiteDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                            new String[]{"0", field},null,null,null);
                    if ( cursor.getCount() > 0 ) {
                        String string = sharedPreferences.getString(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                + StudyAPeriodActivity.DASH + field, null);
                        if ( string != null ) {
                            StringTokenizer stringTokenizer = new StringTokenizer(string, "-");
                            while (stringTokenizer.hasMoreTokens()) {
                                contentValues = new ContentValues();
                                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_FIELD,field_id);
                                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY,Integer.parseInt(stringTokenizer.nextToken()));
                                sqLiteDatabase.insert(Contract.Study_Period_Repeated_Entry.TABLE_NAME,null,contentValues);
                            }
                        }
                        int currentQuestionCounter = sharedPreferences.getInt(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.CURRENT_QUESTION_COUNTER
                                + StudyAPeriodActivity.DASH + field, 0);
                        int permitedTriesNumber = sharedPreferences.getInt(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.NUMBER_OF_TRIES + StudyAPeriodActivity.DASH + field, 0);
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Study_Period_Paused.COLUMN_FIELD,field_id);
                        contentValues.put(Contract.Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER,currentQuestionCounter);
                        contentValues.put(Contract.Study_Period_Paused.COLUMN_TRIES_NUMBER,permitedTriesNumber);
                        sqLiteDatabase.insert(Contract.Study_Period_Paused.TABLE_NAME,null,contentValues);
                        editor.remove(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                + StudyAPeriodActivity.DASH + field);
                        editor.remove(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.CURRENT_QUESTION_COUNTER
                                + StudyAPeriodActivity.DASH + field);
                        editor.remove(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.NUMBER_OF_TRIES + StudyAPeriodActivity.DASH + field);
                    }
                    cursor.close();
                }
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                test_paused_cursor.close();
                study_period_paused_cursor.close();
            }
            editor.apply();
            linkerCursor.close();
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    public static void onUpgradeDatabaseToDBV8AppV38(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Field.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Field.COLUMN_FIELD_NAME
                    + " , " + Contract.Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Field.COLUMN_LOCALE
                    + " , " + Contract.Field.COLUMN_CHECK_TYPE
                    + " , " + Contract.Field.COLUMN_SORT_BY
                    + " , " + Contract.Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Field.COLUMN_READ_ANSWER
                    + " , " + Contract.Field.COLUMN_USAGE_COUNT
                    + " , " + Contract.Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Field.COLUMN_FIELD_NAME
                    + " , " + Contract.Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Field.COLUMN_LOCALE
                    + " , " + Contract.Field.COLUMN_CHECK_TYPE
                    + " , " + Contract.Field.COLUMN_SORT_BY
                    + " , " + Contract.Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Field.COLUMN_READ_ANSWER
                    + " , " + Contract.Field.COLUMN_USAGE_COUNT
                    + " , " + Contract.Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);
            copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_EXCLUDED_TIME_TABLE);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_SCHEDULED_TIME_BACKUP_TABLE);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    public static void questionMigration ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + Contract.OldField.TABLE_QUESTION_SUFFIX,
                    new String[] { fieldName + Contract.OldField.COLUMN_QUESTION_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                String s = cursor.getString(0);
                boolean equals = false;
                for ( String question : mainArrayList ) {
                    if ( question.equals(s) ){
                        equals = true;
                        break;
                    }
                }
                if ( !equals ) {
                    mainArrayList.add(s);
                    s = s.replace("'","''");
                    String statement = "INSERT INTO " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY
                            + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                            + "SELECT " + fieldName + Contract.OldField.COLUMN_QUESTION_SUFFIX + "," + Contract.OldField.COLUMN_CREATED_AT
                            + " FROM " + fieldName + Contract.OldField.TABLE_QUESTION_SUFFIX
                            + " WHERE " + fieldName + Contract.OldField.COLUMN_QUESTION_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "questions has been transfered");
/*        ContentValues contentValues = new ContentValues();
        for ( String question : mainArrayList ) {
            contentValues.put(Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY,question);
        }
        sqLiteDatabase.insert(Old_Question_DBV6_AppV32.TABLE_NAME,null,contentValues);*/
    }
    public static void answerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + Contract.OldField.TABLE_ANSWER_SUFFIX, new String[] { fieldName + Contract.OldField.COLUMN_ANSWER_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                String s = cursor.getString(0);
                boolean equals = false;
                for ( String answer : mainArrayList ) {
                    if ( answer.equals(s) ){
                        equals = true;
                        break;
                    }
                }
                if ( !equals ) {
                    mainArrayList.add(s);
                    s = s.replace("'","''");
                    String statement = "INSERT INTO " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY
                            + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                            + "SELECT " + fieldName + Contract.OldField.COLUMN_ANSWER_SUFFIX + "," + Contract.OldField.COLUMN_CREATED_AT
                            + " FROM " + fieldName + Contract.OldField.TABLE_ANSWER_SUFFIX
                            + " WHERE " + fieldName + Contract.OldField.COLUMN_ANSWER_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
//                    mainArrayList.add(s);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "answers has been transfered");
        /*ContentValues contentValues = new ContentValues();
        for ( String answer : mainArrayList ) {
            contentValues.put(Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY,answer);
        }
        sqLiteDatabase.insert(Old_Answer_DBV6_AppV32.TABLE_NAME,null,contentValues);*/
    }
    public static void linkerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        String question, answer;
        cursor.close();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query( fieldName + Contract.OldField.TABLE_LINK_SUFFIX,
                    new String[] {fieldName + Contract.OldField.COLUMN_QUESTION_LINK_SUFFIX, fieldName + Contract.OldField.COLUMN_ANSWER_LINK_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                question = cursor.getString(0);
                answer = cursor.getString(1);
                question = question.replace("'", "''");
                answer = answer.replace("'", "''");
                String statement = "INSERT INTO " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                        + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                        + "SELECT " + fieldName + Contract.OldField.COLUMN_QUESTION_LINK_SUFFIX + ","
                        + fieldName + Contract.OldField.COLUMN_ANSWER_LINK_SUFFIX + "," + Contract.OldField.COLUMN_CREATED_AT
                        + " FROM " + fieldName + Contract.OldField.TABLE_LINK_SUFFIX
                        + " WHERE " + fieldName + Contract.OldField.COLUMN_QUESTION_LINK_SUFFIX + " = " + "'" + question + "'" + " AND "
                        + fieldName + Contract.OldField.COLUMN_ANSWER_LINK_SUFFIX + " = " + "'" + answer + "'" + ";";
                sqLiteDatabase.execSQL(statement);
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK,fieldName);
                int rows = 0;
                int r = 0;
                Cursor c = sqLiteDatabase.query(Old_Linker_DBV6_AppV32.TABLE_NAME,null,Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL",
                        new String[] {question,answer},null,null,null);
                r = c.getCount();
              *//*  rows = sqLiteDatabase.update(Old_Linker_DBV6_AppV32.TABLE_NAME,contentValues, Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL",
                        new String[] {question,answer});*//*
                rows = sqLiteDatabase.update(Old_Linker_DBV6_AppV32.TABLE_NAME,contentValues, Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                                + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                        new String[] {question,answer,""});*/
                String update = "UPDATE " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " SET " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = " + "'" + fieldName + "'"
                        + " WHERE " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " = " + "'" + question + "'" + " AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = " + "'" + answer + "'" + " AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL;";
                sqLiteDatabase.execSQL(update);
            }
        }
        Log.d("From Pen in Bin", "links has been transfered");
    }
    public static void fieldTableMigration(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable" +
                " ( "
                +  Contract.OldField.COLUMN_FIELD_NAME + " TEXT , " +
                Contract.OldField.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + Contract.OldField.COLUMN_FIELD_NAME
                + "," + Contract.OldField.COLUMN_CREATED_AT + " ) "
                + "SELECT " + Contract.OldField.COLUMN_FIELD_NAME + ","
                + Contract.OldField.COLUMN_CREATED_AT
                + " FROM " + Contract.OldField.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + Contract.OldField.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_FIELD_TABLE);
        copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.OldField.COLUMN_FIELD_NAME + ","
                + Contract.OldField.COLUMN_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void tablesDeletion ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        for ( String fieldName: fieldList ) {
            String dropLink = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_LINK_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropLink);
            String dropAnswer = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_ANSWER_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropAnswer);
            String dropQuestion = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_QUESTION_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropQuestion);
        }
        Log.d("from peninbin", "tables deleted");
    }
    public static void onUpgradeForFieldTableV3(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable"
                + " ( "
                + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + " TEXT PRIMARY KEY, "
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_FIELD_TABLE);
        copy =  "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForQuestionTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_QUESTION_TABLE);
        String copy =  "INSERT INTO " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForAnswerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_ANSWER_TABLE);
        String copy =  "INSERT INTO " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForLinkerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_LINKER_TABLE);
        String copy =  "INSERT INTO " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ","
                + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ","
                + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForFieldTableV5 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_FIELD_TABLE);
        String copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static void onUpgradeForLinkerTableV5 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_LINKER_TABLE);
        String copy = "INSERT INTO " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + " , " + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                + " , " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    public static Cursor getFieldList( SQLiteDatabase sqLiteDatabase ) {
        String [] string = {"rowid _id", Contract.OldField.COLUMN_FIELD_NAME};
        Cursor cursor = sqLiteDatabase.query(Contract.OldField.TABLE_NAME, string,null,null,null,null,null);
        return cursor;
    }
}
