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
import com.ahmedsameha1.peninbin.database.Contract.Field;
import com.ahmedsameha1.peninbin.utils.Constants;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 30/07/16.
 */
public class ModifyDatabaseSchema {

    static void onUpgradeDatabaseToDBV13AppV87(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(Field.TABLE_NAME, Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE + " = ? ",
                    new String[]{String.valueOf(Constants.FieldTypes.NOTEBOOK_TYPE_REMOVED_FEATURE_V68)});
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Field.COLUMN_LOCALE
                    + " , " + Field.COLUMN_CHECK_TYPE
                    + " , " + Field.COLUMN_SORT_BY
                    + " , " + Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Field.COLUMN_READ_ANSWER
                    + " , " + Field.COLUMN_USAGE_COUNT
                    + " , " + Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LOCALE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CHECK_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_SORT_BY
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_READ_ANSWER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_USAGE_COUNT
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL
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
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                    + " , " + Contract.String_Entry.COLUMN_ORDER_OF_ENTRY
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                    + " , " + Contract.String_Entry.COLUMN_ORDER_OF_ENTRY
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, 999999999);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " IS NULL ", null);
            drop = "DROP TABLE IF EXISTS " + Contract.Not_String_Entry.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            rename = "ALTER TABLE " + Contract.Test_Paused.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_TEST_PAUSED_TABLE);
            copy = "INSERT INTO " + Contract.Test_Paused.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Paused.COLUMN_TEST_TYPE
                    + " , " + Contract.Test_Paused.COLUMN_FIELD
                    + " , " + Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT
                    + " , " + Contract.Test_Paused.COLUMN_TRIES_NUMBER
                    + " , " + Contract.Test_Paused.COLUMN_ELAPSED_TIME_OF_PAUSED_TEST_SESSION
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Paused.COLUMN_TEST_TYPE
                    + " , " + Contract.Test_Paused.COLUMN_FIELD
                    + " , " + Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT
                    + " , " + Contract.Test_Paused.COLUMN_TRIES_NUMBER
                    + " , 0 " // insert 0 to fix the problem of not null to the existing rows
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STUDY_TILL_CORRECT_REPEATED_ENTRY_TABLE);
            copy = "INSERT INTO " + Contract.Study_Till_Correct_Repeated_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Study_Till_Correct_Repeated_Entry.COLUMN_FIELD
                    + " , " + Contract.Study_Till_Correct_Repeated_Entry.COLUMN_TYPE
                    + " , " + Contract.Study_Till_Correct_Repeated_Entry.COLUMN_REPEATED_ENTRY
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.COLUMN_FIELD
                    + " , " + Constants.Study_Till_Correct.STUDY_PERIOD
                    + " , " + Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.COLUMN_REPEATED_ENTRY
                    + " FROM " + Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.TABLE_NAME
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STUDY_TILL_CORRECT_PAUSED_TABLE);
            copy = "INSERT INTO " + Contract.Study_Till_Correct_Paused.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Study_Till_Correct_Paused.COLUMN_FIELD
                    + " , " + Contract.Study_Till_Correct_Paused.COLUMN_TYPE
                    + " , " + Contract.Study_Till_Correct_Paused.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Study_Till_Correct_Paused.COLUMN_TRIES_NUMBER
                    + " , " + Contract.Study_Till_Correct_Paused.COLUMN_ELAPSED_TIME_OF_PAUSED_STUDY_TILL_CORRECT_SESSION
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_FIELD
                    + " , " + Constants.Study_Till_Correct.STUDY_PERIOD
                    + " , " + Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_TRIES_NUMBER
                    + " , 0 " // insert 0 to fix the problem of not null to the existing rows
                    + " FROM " + Contract.Old_Study_Period_Paused_DBV12_AppV68.TABLE_NAME
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Study_Period_Paused_DBV12_AppV68.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Symbol.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Field_Type_Symbol.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public static void onUpgradeDatabaseToDBV11AppV47(SQLiteDatabase sqLiteDatabase ) {
        sqLiteDatabase.beginTransaction();
        try {
            String drop = "DROP TABLE IF EXISTS " + Contract.Field_Type_Symbol.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Field_Type.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            ////
            String rename = "ALTER TABLE " + Contract.Old_String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);
            String copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER
                    + " , " + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.String_Entry.COLUMN_RANK
                    + " , " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                    + " , " + Contract.String_Entry.COLUMN_ORDER_OF_ENTRY
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_String_Entry.COLUMN_QUESTION
                    + " , " + Contract.Old_String_Entry.COLUMN_ANSWER
                    + " , " + Contract.Old_String_Entry.COLUMN_FIELD
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.Old_String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.Old_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.Old_String_Entry.COLUMN_RANK
                    + " , " + Contract.Old_String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.Old_String_Entry.COLUMN_REMIND_AT
                    + " , " + Contract.Old_String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                    + " , " + Contract.Old_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                    + " , " + Contract.Old_String_Entry.COLUMN_ORDER_OF_ENTRY
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Not_String_Entry.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            /////
            rename = "ALTER TABLE " + Contract.Test_Wrong_Answer_String.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_TEST_WRONG_ANSWER_STRING_TABLE);
            copy = "INSERT INTO " + Contract.Test_Wrong_Answer_String.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY
                    + " , " + Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY
                    + " , " + Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Test_Wrong_Answer_Not_String.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_TEST_WRONG_ANSWER_NOT_STRING_TABLE);
            ////
            rename = "ALTER TABLE " + Contract.Test_Paused.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_TEST_PAUSED_TABLE);
            copy = "INSERT INTO " + Contract.Test_Paused.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Paused.COLUMN_FIELD
                    + " , " + Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT
                    + " , " + Contract.Test_Paused.COLUMN_TRIES_NUMBER
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Test_Paused.COLUMN_FIELD
                    + " , " + Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER
                    + " , " + Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT
                    + " , " + Contract.Test_Paused.COLUMN_TRIES_NUMBER
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
    public static void onUpgradeDatabaseToDBV9AppV40(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
            String copy = "INSERT INTO " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LOCALE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CHECK_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_SORT_BY
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_READ_ANSWER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_USAGE_COUNT
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LOCALE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CHECK_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_SORT_BY
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_READ_ANSWER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_USAGE_COUNT
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
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
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE_OLD);
            String copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
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
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
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
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_NOTE_TABLE);
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
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
            String copy = "INSERT INTO " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " ( " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
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
                long question_id = question_id_cursor.getLong(0);
                Cursor answer_id_cursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(1)},null,null,null,null);
                answer_id_cursor.moveToFirst();
                long answer_id = answer_id_cursor.getLong(0);
                Cursor field_id_cursor = sqLiteDatabase.query(Contract.Old_Field_DBV12_AppV68.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{field},null,null,null,null);
                field_id_cursor.moveToFirst();
                long field_id = field_id_cursor.getLong(0);
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION,question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER,answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD,field_id);
                contentValues.put(Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER,linkerCursor.getInt(3));
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
                Cursor study_period_paused_cursor = sqLiteDatabase.query(Contract.Old_Study_Period_Paused_DBV12_AppV68.TABLE_NAME,new String[]{Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_FIELD},
                        Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_FIELD + " = ? ",
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
                                contentValues.put(Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.COLUMN_FIELD,field_id);
                                contentValues.put(Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.COLUMN_REPEATED_ENTRY,Integer.parseInt(stringTokenizer.nextToken()));
                                sqLiteDatabase.insert(Contract.Old_Study_Period_Repeated_Entry_DBV12_AppV68.TABLE_NAME,null,contentValues);
                            }
                        }
                        int currentQuestionCounter = sharedPreferences.getInt(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.CURRENT_QUESTION_COUNTER
                                + StudyAPeriodActivity.DASH + field, 0);
                        int permitedTriesNumber = sharedPreferences.getInt(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.DASH +
                                StudyAPeriodActivity.NUMBER_OF_TRIES + StudyAPeriodActivity.DASH + field, 0);
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_FIELD,field_id);
                        contentValues.put(Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_CURRENT_QUESTION_COUNTER,currentQuestionCounter);
                        contentValues.put(Contract.Old_Study_Period_Paused_DBV12_AppV68.COLUMN_TRIES_NUMBER,permitedTriesNumber);
                        sqLiteDatabase.insert(Contract.Old_Study_Period_Paused_DBV12_AppV68.TABLE_NAME,null,contentValues);
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
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
            String copy = "INSERT INTO " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LOCALE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CHECK_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_SORT_BY
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_READ_ANSWER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_USAGE_COUNT
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LOCALE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CHECK_TYPE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_SORT_BY
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_LIST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_TEST_TEXT_SIZE
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_READ_ANSWER
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_USAGE_COUNT
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE_OLD);
            copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
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
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
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
