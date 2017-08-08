package com.ahmedsameha1.peninbin.database;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.secondary_activities.ExportSuccessfulActivity;
import com.ahmedsameha1.peninbin.secondary_activities.NotImportedFieldsActivity;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.InvalidStudyWithoutPenFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ahmed on 1/5/2016.
 */
public class ExportImportDatabase {
    public static final int FAILED_EXPORT_NOTIFICATION = 0;
    public static final int FAILED_IMPORT_NOTIFICATION = 3;
    public static final int FAILED_IMPORT_INVALID_FILE_NOTIFICATION = 4;
    public static final int SUCCESSFUL_IMPORT_NOTIFICATION = 5;
    public static final int NOT_IMPORTED_FIELDS = 6;
    public static final int SUCCESFUL_EXPORT_NOTIFICATION = 7;
    public static final String PATH = "path";

    private static Thread exportThread, importThread, importInternalFieldThread;
    private static ArrayList<String> notImportedFieldsIDs;
    private static ArrayList<String> notImportedFieldsStrings;

    public static void startExportThread(final Context context, final ArrayList<String> selectedFields, final String path, final String fileName) {
        exportThread = new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            exportFields(context, selectedFields, path, fileName);
        });
        exportThread.start();
    }

    public static void startImportThread(final Context context, final String file_path, final boolean importUserUsageData) {
        importThread = new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            importFields(context, file_path, importUserUsageData);
        });
        importThread.start();
    }

    public static void startImportInternalFieldThread(final Context context, SQLiteDatabase toImportDatabase, ArrayList<String> wanted_fields) {
        importInternalFieldThread = new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            importInternalFields(context, toImportDatabase, wanted_fields);
        });
        importInternalFieldThread.start();
    }

    public static boolean isAnyThreadRunning() {
        if ((exportThread == null || !exportThread.isAlive()) &&
                (importThread == null || !importThread.isAlive()) &&
                (importInternalFieldThread == null || !importInternalFieldThread.isAlive())) {
            return false;
        }
        return true;
    }

    private static void exportFields(Context context, ArrayList<String> fieldNames, String path, String user_fileName) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SQLiteDatabase appDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        SQLiteDatabase toExportDatabase = null;
        File exportDirectory = null;
        File exportedDatabaseFile = null;
        String file_name = user_fileName + ".swp";
        try {
            exportDirectory = new File(path);
            exportedDatabaseFile = new File(exportDirectory, file_name);
            toExportDatabase = SQLiteDatabase.openOrCreateDatabase(exportedDatabaseFile, null);
            toExportDatabase.beginTransactionNonExclusive();
            toExportDatabase.setVersion(DatabaseHelper.DATABASE_VERSION);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);//
            toExportDatabase.execSQL(DatabaseHelper.CREATE_STRING_TABLE);//
            toExportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);//
            toExportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_STRING_TABLE);//
            toExportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_EXCLUDED_TIME_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_NOTE_TABLE);
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Cursor fieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_FIELD_NAME,
                                Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, Contract.Field.COLUMN_SORT_BY,
                                Contract.Field.COLUMN_LIST_TEXT_SIZE, Contract.Field.COLUMN_QUESTION_AREA_SIZE,
                                Contract.Field.COLUMN_ANSWER_AREA_SIZE, Contract.Field.COLUMN_TEST_TEXT_SIZE,
                                Contract.Field.COLUMN_ACTION_BAR_SHOWN, Contract.Field.COLUMN_USAGE_COUNT,
                                Contract.CREATION_DATE,
                                Contract.COLUMN_ID, Contract.Field.COLUMN_CHECK_TYPE,
                                Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES,
                                Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION,
                                Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL,
                                Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_TESTS, Contract.Field.COLUMN_FIND_ANSWER_TIME_TESTS,
                                Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_TESTS, Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_STUDY_TILL_CORRECT,
                                Contract.Field.COLUMN_FIND_ANSWER_TIME_STUDY_TILL_CORRECT, Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_STUDY_TILL_CORRECT,
                                Contract.Field.COLUMN_TIME_OF_ANSWER_TESTS_DO_WHAT, Contract.Field.COLUMN_OBFUSCATE_QUESTION, Contract.Field.COLUMN_COLOR},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{tempFieldName}, null, null, null, null);
                fieldCursor.moveToFirst();
                long temp_field_id = fieldCursor.getLong(10);
                int question_data_type = fieldCursor.getInt(1);
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_FIELD_NAME, fieldCursor.getString(0));
                contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, fieldCursor.getInt(1));
                contentValues.put(Contract.Field.COLUMN_SORT_BY, fieldCursor.getInt(2));
                contentValues.put(Contract.Field.COLUMN_LIST_TEXT_SIZE, fieldCursor.getInt(3));
                contentValues.put(Contract.Field.COLUMN_QUESTION_AREA_SIZE, fieldCursor.getInt(4));
                contentValues.put(Contract.Field.COLUMN_ANSWER_AREA_SIZE, fieldCursor.getInt(5));
                contentValues.put(Contract.Field.COLUMN_TEST_TEXT_SIZE, fieldCursor.getInt(6));
                contentValues.put(Contract.Field.COLUMN_ACTION_BAR_SHOWN, fieldCursor.getInt(7));
                contentValues.put(Contract.Field.COLUMN_USAGE_COUNT, fieldCursor.getInt(8));
                contentValues.put(Contract.CREATION_DATE, fieldCursor.getString(9));
                contentValues.put(Contract.Field.COLUMN_CHECK_TYPE, fieldCursor.getInt(11));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, fieldCursor.getInt(12));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, fieldCursor.getInt(13));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, fieldCursor.getInt(14));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION, fieldCursor.getInt(15));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, fieldCursor.getInt(16));
                contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL, fieldCursor.getInt(17));
                contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_TESTS, fieldCursor.getInt(18));
                contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_TESTS, fieldCursor.getInt(19));
                contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_TESTS, fieldCursor.getInt(20));
                contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_STUDY_TILL_CORRECT, fieldCursor.getInt(21));
                contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_STUDY_TILL_CORRECT, fieldCursor.getInt(22));
                contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_STUDY_TILL_CORRECT, fieldCursor.getInt(23));
                contentValues.put(Contract.Field.COLUMN_TIME_OF_ANSWER_TESTS_DO_WHAT, fieldCursor.getInt(24));
                contentValues.put(Contract.Field.COLUMN_OBFUSCATE_QUESTION, fieldCursor.getInt(25));
                contentValues.put(Contract.Field.COLUMN_COLOR, fieldCursor.getInt(26));
                Long field_id = toExportDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
                fieldCursor.close();
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    Cursor stringEntryCursor = appDatabase.query(Contract.String_Entry.TABLE_NAME,
                            new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                                    Contract.String_Entry.COLUMN_FIELD, Contract.String_Entry.COLUMN_ASKED_COUNT,
                                    Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, Contract.String_Entry.COLUMN_RANK,
                                    Contract.CREATION_DATE, Contract.COLUMN_ID,
                                    Contract.String_Entry.COLUMN_REMIND_AT, Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND,
                                    Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                            Contract.String_Entry.COLUMN_FIELD + " = ?",
                            new String[]{temp_field_id + ""}, null, null, null, null);
                    for (stringEntryCursor.moveToFirst(); stringEntryCursor.getPosition() < stringEntryCursor.getCount();
                         stringEntryCursor.moveToNext()) {
                        Cursor questionCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{stringEntryCursor.getLong(0) + ""}, null, null, null, null);
                        questionCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.STring.COLUMN_VALUE, questionCursor.getString(0));
                        long question_id = 0;
                        try {
                            question_id = toExportDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                        } catch (Exception e) {
                            Cursor c = toExportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                    Contract.STring.COLUMN_VALUE + " = ? ",
                                    new String[]{questionCursor.getString(0)}, null, null, null, null);
                            c.moveToFirst();
                            question_id = c.getLong(0);
                            c.close();
                        }
                        questionCursor.close();
                        Cursor answerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{stringEntryCursor.getLong(1) + ""}, null, null, null, null);
                        answerCursor.moveToFirst();
                        contentValues1 = new ContentValues();
                        contentValues1.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                        long answer_id = 0;
                        try {
                            answer_id = toExportDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                        } catch (Exception e) {
                            Cursor c = toExportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                    Contract.STring.COLUMN_VALUE + " = ? ",
                                    new String[]{answerCursor.getString(0)}, null, null, null, null);
                            c.moveToFirst();
                            answer_id = c.getLong(0);
                            c.close();
                        }
                        answerCursor.close();
                        ///////////////////////////////////////////////////////////
                        contentValues = new ContentValues();
                        contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                        contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                        contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                        contentValues.put(Contract.String_Entry.COLUMN_ASKED_COUNT, stringEntryCursor.getInt(3));
                        contentValues.put(Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, stringEntryCursor.getInt(4));
                        contentValues.put(Contract.String_Entry.COLUMN_RANK, stringEntryCursor.getInt(5));
                        contentValues.put(Contract.CREATION_DATE, stringEntryCursor.getString(6));
                        contentValues.put(Contract.String_Entry.COLUMN_REMIND_AT, stringEntryCursor.getLong(8));
                        contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, stringEntryCursor.getInt(9));
                        contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, stringEntryCursor.getInt(10));
                        long stringEntry_id = toExportDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
                        Cursor alternativeStringCursor = appDatabase.query(Contract.Alternative_Answer_String.TABLE_NAME,
                                new String[]{Contract.Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER,
                                        Contract.Alternative_Answer_String.COLUMN_ANSWERED_COUNT},
                                Contract.Alternative_Answer_String.COLUMN_ENTRY + " = ? ",
                                new String[]{stringEntryCursor.getLong(7) + ""}, null, null, null, null);
                        for (alternativeStringCursor.moveToFirst(); alternativeStringCursor.getPosition() < alternativeStringCursor.getCount();
                             alternativeStringCursor.moveToNext()) {
                            Cursor alternativeAnswerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                    Contract.COLUMN_ID + " = ? ",
                                    new String[]{alternativeStringCursor.getLong(0) + ""}, null, null, null, null);
                            alternativeAnswerCursor.moveToFirst();
                            contentValues = new ContentValues();
                            contentValues.put(Contract.STring.COLUMN_VALUE, alternativeAnswerCursor.getString(0));
                            long wrongAnswer_id = 0;
                            try {
                                wrongAnswer_id = toExportDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                            } catch (Exception e) {
                                Cursor c = toExportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                        Contract.STring.COLUMN_VALUE + " = ? ",
                                        new String[]{alternativeAnswerCursor.getString(0)}, null, null, null, null);
                                c.moveToFirst();
                                wrongAnswer_id = c.getLong(0);
                                c.close();
                            }
                            contentValues = new ContentValues();
                            contentValues.put(Contract.Alternative_Answer_String.COLUMN_ENTRY, stringEntry_id);
                            contentValues.put(Contract.Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER, wrongAnswer_id);
                            contentValues.put(Contract.Alternative_Answer_String.COLUMN_ANSWERED_COUNT, alternativeStringCursor.getInt(1));
                            toExportDatabase.insert(Contract.Alternative_Answer_String.TABLE_NAME, null, contentValues);
                            alternativeAnswerCursor.close();
                        }
                        questionCursor.close();
                        answerCursor.close();
                        alternativeStringCursor.close();
                    }
                    stringEntryCursor.close();
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                    Cursor notstringEntryCursor = appDatabase.query(Contract.Not_String_Entry.TABLE_NAME,
                            new String[]{Contract.Not_String_Entry.COLUMN_QUESTION, Contract.Not_String_Entry.COLUMN_ANSWER,
                                    Contract.Not_String_Entry.COLUMN_FIELD, Contract.Not_String_Entry.COLUMN_ASKED_COUNT,
                                    Contract.Not_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, Contract.Not_String_Entry.COLUMN_RANK,
                                    Contract.CREATION_DATE, Contract.COLUMN_ID,
                                    Contract.Not_String_Entry.COLUMN_REMIND_AT, Contract.Not_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND},
                            Contract.Not_String_Entry.COLUMN_FIELD + " = ?",
                            new String[]{tempFieldName}, null, null, null, null);
                    for (notstringEntryCursor.moveToFirst(); notstringEntryCursor.getPosition() < notstringEntryCursor.getCount();
                         notstringEntryCursor.moveToNext()) {
                        Cursor questionCursor = appDatabase.query(Contract.Not_String.TABLE_NAME, new String[]{Contract.Not_String.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{notstringEntryCursor.getLong(0) + ""}, null, null, null, null);
                        questionCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.Not_String.COLUMN_VALUE, questionCursor.getBlob(0));
                        long question_id = toExportDatabase.insert(Contract.Not_String.TABLE_NAME, null, contentValues1);
                        questionCursor.close();
                        Cursor answerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{notstringEntryCursor.getLong(1) + ""}, null, null, null, null);
                        answerCursor.moveToFirst();
                        contentValues1 = new ContentValues();
                        contentValues1.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                        long answer_id = 0;
                        try {
                            answer_id = toExportDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                        } catch (Exception e) {
                            Cursor c = toExportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                    Contract.STring.COLUMN_VALUE + " = ? ",
                                    new String[]{answerCursor.getString(0)}, null, null, null, null);
                            c.moveToFirst();
                            answer_id = c.getLong(0);
                            c.close();
                        }
                        answerCursor.close();
                        ///////////////////////////////////////////////////////////
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Not_String_Entry.COLUMN_QUESTION, question_id);
                        contentValues.put(Contract.Not_String_Entry.COLUMN_ANSWER, answer_id);
                        contentValues.put(Contract.Not_String_Entry.COLUMN_FIELD, field_id);
                        contentValues.put(Contract.Not_String_Entry.COLUMN_ASKED_COUNT, notstringEntryCursor.getInt(3));
                        contentValues.put(Contract.Not_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, notstringEntryCursor.getInt(4));
                        contentValues.put(Contract.Not_String_Entry.COLUMN_RANK, notstringEntryCursor.getInt(5));
                        contentValues.put(Contract.CREATION_DATE, notstringEntryCursor.getString(6));
                        contentValues.put(Contract.Not_String_Entry.COLUMN_REMIND_AT, notstringEntryCursor.getLong(8));
                        contentValues.put(Contract.Not_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, notstringEntryCursor.getInt(9));
                        long stringEntry_id = toExportDatabase.insert(Contract.Not_String_Entry.TABLE_NAME, null, contentValues);
                        Cursor alternativeNotStringCursor = appDatabase.query(Contract.Alternative_Answer_Not_String.TABLE_NAME,
                                new String[]{Contract.Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER,
                                        Contract.Alternative_Answer_Not_String.COLUMN_ANSWERED_COUNT},
                                Contract.Alternative_Answer_Not_String.COLUMN_ENTRY + " = ? ",
                                new String[]{notstringEntryCursor.getLong(7) + ""}, null, null, null, null);
                        for (alternativeNotStringCursor.moveToFirst(); alternativeNotStringCursor.getPosition() < alternativeNotStringCursor.getCount();
                             alternativeNotStringCursor.moveToNext()) {
                            Cursor alternativeAnswerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                    Contract.COLUMN_ID + " = ? ",
                                    new String[]{alternativeNotStringCursor.getLong(0) + ""}, null, null, null, null);
                            alternativeAnswerCursor.moveToFirst();
                            contentValues = new ContentValues();
                            contentValues.put(Contract.STring.COLUMN_VALUE, alternativeAnswerCursor.getString(0));
                            long wrongAnswer_id = 0;
                            try {
                                wrongAnswer_id = toExportDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                            } catch (Exception e) {
                                Cursor c = toExportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                        Contract.STring.COLUMN_VALUE + " = ? ",
                                        new String[]{alternativeAnswerCursor.getString(0)}, null, null, null, null);
                                c.moveToFirst();
                                wrongAnswer_id = c.getLong(0);
                                c.close();
                            }
                            contentValues = new ContentValues();
                            contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ENTRY, stringEntry_id);
                            contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER, wrongAnswer_id);
                            contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ANSWERED_COUNT, alternativeNotStringCursor.getInt(1));
                            toExportDatabase.insert(Contract.Alternative_Answer_Not_String.TABLE_NAME, null, contentValues);
                            alternativeAnswerCursor.close();
                        }
                        questionCursor.close();
                        answerCursor.close();
                        alternativeNotStringCursor.close();
                    }
                    notstringEntryCursor.close();
                }
                Cursor excluded_time_cursor = appDatabase.query(Contract.Excluded_Time.TABLE_NAME,
                        new String[]{Contract.Excluded_Time.COLUMN_STARTS, Contract.Excluded_Time.COLUMN_ENDS},
                        Contract.Excluded_Time.COLUMN_FIELD + " = ? ", new String[]{temp_field_id + ""}, null, null, null);
                for (excluded_time_cursor.moveToFirst(); excluded_time_cursor.getPosition() < excluded_time_cursor.getCount();
                     excluded_time_cursor.moveToNext()) {
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Excluded_Time.COLUMN_FIELD, field_id);
                    contentValues1.put(Contract.Excluded_Time.COLUMN_STARTS, excluded_time_cursor.getString(0));
                    contentValues1.put(Contract.Excluded_Time.COLUMN_ENDS, excluded_time_cursor.getString(1));
                    toExportDatabase.insert(Contract.Excluded_Time.TABLE_NAME, null, contentValues1);
                }
                excluded_time_cursor.close();
                Cursor field_notes_cursor = appDatabase.query(Contract.Field_Note.TABLE_NAME,
                        new String[]{Contract.Field_Note.COLUMN_NOTE},
                        Contract.Field_Note.COLUMN_FIELD + " = ? ", new String[]{temp_field_id + ""}, null, null, null);
                for (field_notes_cursor.moveToFirst(); field_notes_cursor.getPosition() < field_notes_cursor.getCount();
                     field_notes_cursor.moveToNext()) {
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Field_Note.COLUMN_FIELD, field_id);
                    contentValues1.put(Contract.Field_Note.COLUMN_NOTE, field_notes_cursor.getString(0));
                    toExportDatabase.insert(Contract.Field_Note.TABLE_NAME, null, contentValues1);
                }
                field_notes_cursor.close();
            }
            toExportDatabase.execSQL("CREATE TABLE " + Contract.Check.TABLE_NAME + " ( " + Contract.Check.COLUMN_CHECK_NUMBER
                    + " INTEGER PRIMARY KEY NOT NULL );");
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Check.COLUMN_CHECK_NUMBER, Contract.Check.CHECK_NUMBER);
            toExportDatabase.insert(Contract.Check.TABLE_NAME, null, contentValues);
            toExportDatabase.setTransactionSuccessful();
            showNotification(context, path, SUCCESFUL_EXPORT_NOTIFICATION, notificationManager);

        } catch (SQLiteException e) {
            if (exportedDatabaseFile != null && exportedDatabaseFile.exists()) {
                exportedDatabaseFile.delete();
            }
            showNotification(context, FAILED_EXPORT_NOTIFICATION, notificationManager);
        } finally {
            if (toExportDatabase != null) {
                toExportDatabase.endTransaction();
                toExportDatabase.close();
            }
            String journalFileName = file_name + "-journal";
            File journalFile = new File(exportDirectory, journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void importFields(Context context, String filePath, boolean importUserUsageData) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SQLiteDatabase toImportDatabase = null;
        try {
            toImportDatabase = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = toImportDatabase.query(Contract.Check.TABLE_NAME, new String[]{Contract.Check.COLUMN_CHECK_NUMBER},
                    null, null, null, null, null);
            cursor.moveToFirst();
            int checkNumber = cursor.getInt(0);
            cursor.close();
            if (checkNumber != Contract.Check.CHECK_NUMBER) {
                throw new InvalidStudyWithoutPenFileException();
            }
            int databaseVersion = toImportDatabase.getVersion();
            if (databaseVersion == 6) {
                try {
                    upgradeImportedDatabaseFromDBV6ToDBV7(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV7ToDBV8(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV8ToDBV9(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV9ToDBV10(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV12ToDBV13(toImportDatabase, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
                    return;
                }
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else if (databaseVersion == 7) {
                try {
                    upgradeImportedDatabaseFromDBV7ToDBV8(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV8ToDBV9(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV9ToDBV10(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV12ToDBV13(toImportDatabase, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
                    return;
                }
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else if (databaseVersion == 8) {
                try {
                    upgradeImportedDatabaseFromDBV8ToDBV9(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV9ToDBV10(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV12ToDBV13(toImportDatabase, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
                    return;
                }
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else if (databaseVersion == 9) {
                try {
                    upgradeImportedDatabaseFromDBV9ToDBV10(toImportDatabase, filePath);
                    upgradeImportedDatabaseFromDBV12ToDBV13(toImportDatabase, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
                    return;
                }
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else if (databaseVersion == 10 || databaseVersion == 11 || databaseVersion == 12) {
                try {
                    upgradeImportedDatabaseFromDBV12ToDBV13(toImportDatabase, filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
                    return;
                }
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else if (databaseVersion == 13) {
                importFields(context, toImportDatabase, importUserUsageData);
                showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
            } else {
                throw new InvalidStudyWithoutPenFileException();
            }
        } catch (InvalidStudyWithoutPenFileException e) {
            showNotification(context, FAILED_IMPORT_INVALID_FILE_NOTIFICATION, notificationManager);
        } catch (IOException | SQLiteException e) {
            e.printStackTrace();
            showNotification(context, FAILED_IMPORT_NOTIFICATION, notificationManager);
        } finally {
            if (toImportDatabase != null) {
                toImportDatabase.close();
            }
        }
    }

    private static void importFields(Context context, SQLiteDatabase toImportDatabase, boolean importUserUsageData)
            throws IOException, SQLiteException, InvalidStudyWithoutPenFileException {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notImportedFieldsIDs = new ArrayList<>();
        SQLiteDatabase appDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        appDatabase.beginTransactionNonExclusive();
        try {
            Cursor toImportFieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME,
                    new String[]{Contract.Field.COLUMN_FIELD_NAME,
                            Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, Contract.Field.COLUMN_SORT_BY,
                            Contract.Field.COLUMN_LIST_TEXT_SIZE, Contract.Field.COLUMN_QUESTION_AREA_SIZE,
                            Contract.Field.COLUMN_ANSWER_AREA_SIZE, Contract.Field.COLUMN_TEST_TEXT_SIZE,
                            Contract.Field.COLUMN_ACTION_BAR_SHOWN, Contract.Field.COLUMN_USAGE_COUNT,
                            Contract.CREATION_DATE,
                            Contract.COLUMN_ID, Contract.Field.COLUMN_CHECK_TYPE,
                            Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES,
                            Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION,
                            Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL,
                            Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_TESTS, Contract.Field.COLUMN_FIND_ANSWER_TIME_TESTS,
                            Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_TESTS, Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_STUDY_TILL_CORRECT,
                            Contract.Field.COLUMN_FIND_ANSWER_TIME_STUDY_TILL_CORRECT, Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_STUDY_TILL_CORRECT,
                            Contract.Field.COLUMN_TIME_OF_ANSWER_TESTS_DO_WHAT, Contract.Field.COLUMN_OBFUSCATE_QUESTION, Contract.Field.COLUMN_COLOR},
                    null, null, null, null, null);
            for (toImportFieldCursor.moveToFirst(); toImportFieldCursor.getPosition() < toImportFieldCursor.getCount();
                 toImportFieldCursor.moveToNext()) {

                Cursor appFieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_FIELD_NAME},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{toImportFieldCursor.getString(0)}, null, null, null, null);
                long field_id = -1;
                if (appFieldCursor.getCount() <= 0) {
                    if (importUserUsageData) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, toImportFieldCursor.getString(0));
                        contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, toImportFieldCursor.getInt(1));
                        contentValues.put(Contract.Field.COLUMN_SORT_BY, toImportFieldCursor.getInt(2));
                        contentValues.put(Contract.Field.COLUMN_LIST_TEXT_SIZE, toImportFieldCursor.getInt(3));
                        contentValues.put(Contract.Field.COLUMN_QUESTION_AREA_SIZE, toImportFieldCursor.getInt(4));
                        contentValues.put(Contract.Field.COLUMN_ANSWER_AREA_SIZE, toImportFieldCursor.getInt(5));
                        contentValues.put(Contract.Field.COLUMN_TEST_TEXT_SIZE, toImportFieldCursor.getInt(6));
                        contentValues.put(Contract.Field.COLUMN_ACTION_BAR_SHOWN, toImportFieldCursor.getInt(7));
                        contentValues.put(Contract.Field.COLUMN_USAGE_COUNT, toImportFieldCursor.getInt(8));
                        contentValues.put(Contract.Field.COLUMN_CHECK_TYPE, toImportFieldCursor.getInt(11));
                        contentValues.put(Contract.CREATION_DATE, toImportFieldCursor.getString(9));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, toImportFieldCursor.getInt(12));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, toImportFieldCursor.getInt(13));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, toImportFieldCursor.getInt(14));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION, toImportFieldCursor.getInt(15));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, toImportFieldCursor.getInt(16));
                        contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL, toImportFieldCursor.getInt(17));
                        contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_TESTS, toImportFieldCursor.getInt(18));
                        contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_TESTS, toImportFieldCursor.getInt(19));
                        contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_TESTS, toImportFieldCursor.getInt(20));
                        contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_STUDY_TILL_CORRECT, toImportFieldCursor.getInt(21));
                        contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_STUDY_TILL_CORRECT, toImportFieldCursor.getInt(22));
                        contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_STUDY_TILL_CORRECT, toImportFieldCursor.getInt(23));
                        contentValues.put(Contract.Field.COLUMN_TIME_OF_ANSWER_TESTS_DO_WHAT, toImportFieldCursor.getInt(24));
                        contentValues.put(Contract.Field.COLUMN_OBFUSCATE_QUESTION, toImportFieldCursor.getInt(25));
                        contentValues.put(Contract.Field.COLUMN_COLOR, toImportFieldCursor.getInt(26));
                        field_id = appDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, toImportFieldCursor.getString(0));
                        contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, toImportFieldCursor.getInt(1));
                        field_id = appDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
                    }
                    Cursor excluded_times_cursor = toImportDatabase.query(Contract.Excluded_Time.TABLE_NAME,
                            new String[]{Contract.Excluded_Time.COLUMN_STARTS, Contract.Excluded_Time.COLUMN_ENDS},
                            Contract.Excluded_Time.COLUMN_FIELD + " = ? ", new String[]{toImportFieldCursor.getLong(10) + ""}, null, null, null);
                    for (excluded_times_cursor.moveToFirst(); excluded_times_cursor.getPosition() < excluded_times_cursor.getCount();
                         excluded_times_cursor.moveToNext()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Excluded_Time.COLUMN_FIELD, field_id);
                        contentValues.put(Contract.Excluded_Time.COLUMN_STARTS, excluded_times_cursor.getString(0));
                        contentValues.put(Contract.Excluded_Time.COLUMN_ENDS, excluded_times_cursor.getString(1));
                        appDatabase.insert(Contract.Excluded_Time.TABLE_NAME, null, contentValues);
                    }
                    excluded_times_cursor.close();
                    Cursor field_notes_cursor = toImportDatabase.query(Contract.Field_Note.TABLE_NAME,
                            new String[]{Contract.Field_Note.COLUMN_NOTE},
                            Contract.Field_Note.COLUMN_FIELD + " = ? ", new String[]{toImportFieldCursor.getLong(10) + ""}, null, null, null);
                    for (field_notes_cursor.moveToFirst(); field_notes_cursor.getPosition() < field_notes_cursor.getCount();
                         field_notes_cursor.moveToNext()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Field_Note.COLUMN_FIELD, field_id);
                        contentValues.put(Contract.Field_Note.COLUMN_NOTE, field_notes_cursor.getString(0));
                        appDatabase.insert(Contract.Field_Note.TABLE_NAME, null, contentValues);
                    }
                    field_notes_cursor.close();
                } else {
                    appFieldCursor.moveToFirst();
                    notImportedFieldsIDs.add(String.valueOf(toImportFieldCursor.getLong(10)));
                }
                appFieldCursor.close();
            }
            toImportFieldCursor.close();

            String[] notImportedArray = notImportedFieldsIDs.size() <= 0 ? new String[]{""} : notImportedFieldsIDs.toArray(new String[notImportedFieldsIDs.size()]);
            String placeholders = "( ";
            if (notImportedArray.length <= 1) {
                placeholders += "? )";
            } else {
                for (int i = 0; i < notImportedArray.length; i++) {
                    if (i < notImportedArray.length - 1) {
                        placeholders += "?, ";
                    } else {
                        placeholders += "? )";
                    }
                }
            }
            notImportedFieldsStrings = new ArrayList<String>();
            for (String field_id : notImportedFieldsIDs) {
                Cursor fieldNameCursor = toImportDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_NAME},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{field_id}, null, null, null, null);
                fieldNameCursor.moveToFirst();
                notImportedFieldsStrings.add(fieldNameCursor.getString(0));
            }
            Cursor toImportStringEntryCursor = toImportDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                            Contract.String_Entry.COLUMN_FIELD, Contract.String_Entry.COLUMN_ASKED_COUNT,
                            Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, Contract.String_Entry.COLUMN_RANK,
                            Contract.CREATION_DATE, Contract.COLUMN_ID,
                            Contract.String_Entry.COLUMN_REMIND_AT, Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND,
                            Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                    Contract.String_Entry.COLUMN_FIELD + " NOT IN " + placeholders,
                    notImportedArray, null, null, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                            + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
            for (toImportStringEntryCursor.moveToFirst(); toImportStringEntryCursor.getPosition() < toImportStringEntryCursor.getCount();
                 toImportStringEntryCursor.moveToNext()) {
                Cursor questionCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getLong(0) + ""}, null, null, null, null);
                questionCursor.moveToFirst();
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_VALUE, questionCursor.getString(0));
                long question_id = 0;
                try {
                    question_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                } catch (Exception e) {
                    Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{questionCursor.getString(0)}, null, null, null, null);
                    c.moveToFirst();
                    question_id = c.getLong(0);
                    c.close();
                }
                questionCursor.close();
                Cursor answerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getLong(1) + ""}, null, null, null, null);
                answerCursor.moveToFirst();
                contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                long answer_id = 0;
                try {
                    answer_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                } catch (SQLException e) {
                    Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{answerCursor.getString(0)}, null, null, null, null);
                    c.moveToFirst();
                    answer_id = c.getLong(0);
                    c.close();
                }
                answerCursor.close();
                Cursor toimportfieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_NAME},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getLong(2) + ""}, null, null, null, null);
                toimportfieldCursor.moveToFirst();
                Cursor appfieldCursor = appDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{toimportfieldCursor.getString(0)}, null, null, null, null);
                appfieldCursor.moveToFirst();
                long field_id = appfieldCursor.getLong(0);
                appfieldCursor.close();
                toimportfieldCursor.close();
                long string_entry_id;
                if (importUserUsageData) {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_ASKED_COUNT, toImportStringEntryCursor.getInt(3));
                    contentValues1.put(Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, toImportStringEntryCursor.getInt(4));
                    contentValues1.put(Contract.String_Entry.COLUMN_RANK, toImportStringEntryCursor.getInt(5));
                    contentValues1.put(Contract.CREATION_DATE, toImportStringEntryCursor.getString(6));
                    contentValues1.put(Contract.String_Entry.COLUMN_REMIND_AT, toImportStringEntryCursor.getLong(8));
                    contentValues1.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, toImportStringEntryCursor.getInt(9));
                    contentValues1.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, toImportStringEntryCursor.getInt(10));
                    int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                    if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                        contentValues1.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, appDatabase));
                    }
                    try {
                        string_entry_id = appDatabase.insertOrThrow(Contract.String_Entry.TABLE_NAME, null, contentValues1);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                        + Contract.String_Entry.COLUMN_ANSWER + " = ? AND "
                                        + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null, null);
                        c.moveToFirst();
                        string_entry_id = c.getLong(0);
                        c.close();
                    }
                } else {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, toImportStringEntryCursor.getInt(10));
                    int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                    if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                        contentValues1.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, appDatabase));
                    }
                    try {
                        string_entry_id = appDatabase.insertOrThrow(Contract.String_Entry.TABLE_NAME, null, contentValues1);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                        + Contract.String_Entry.COLUMN_ANSWER + " = ? AND "
                                        + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null, null);
                        c.moveToFirst();
                        string_entry_id = c.getLong(0);
                        c.close();
                    }
                }
                Cursor string_alternative_cursor = toImportDatabase.query(Contract.Alternative_Answer_String.TABLE_NAME,
                        new String[]{Contract.Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER,
                                Contract.Alternative_Answer_String.COLUMN_ANSWERED_COUNT},
                        Contract.Alternative_Answer_String.COLUMN_ENTRY + " = ? ",
                        new String[]{toImportStringEntryCursor.getLong(7) + ""}, null, null, null, null);
                for (string_alternative_cursor.moveToFirst(); string_alternative_cursor.getPosition() < string_alternative_cursor.getCount();
                     string_alternative_cursor.moveToNext()) {
                    Cursor alternativeAnswerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                            Contract.COLUMN_ID + " = ? ",
                            new String[]{string_alternative_cursor.getLong(0) + ""}, null, null, null, null);
                    alternativeAnswerCursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_VALUE, alternativeAnswerCursor.getString(0));
                    long wrongAnswer_id = 0;
                    try {
                        wrongAnswer_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.STring.COLUMN_VALUE + " = ? ",
                                new String[]{alternativeAnswerCursor.getString(0)}, null, null, null, null);
                        c.moveToFirst();
                        wrongAnswer_id = c.getLong(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_ENTRY, string_entry_id);
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER, wrongAnswer_id);
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_ANSWERED_COUNT, string_alternative_cursor.getInt(1));
                    appDatabase.insert(Contract.Alternative_Answer_String.TABLE_NAME, null, contentValues);
                    alternativeAnswerCursor.close();
                }
                string_alternative_cursor.close();
            }
            toImportStringEntryCursor.close();

            Cursor toImportNotStringEntryCursor = toImportDatabase.query(Contract.Not_String_Entry.TABLE_NAME,
                    new String[]{Contract.Not_String_Entry.COLUMN_QUESTION, Contract.Not_String_Entry.COLUMN_ANSWER,
                            Contract.Not_String_Entry.COLUMN_FIELD, Contract.Not_String_Entry.COLUMN_ASKED_COUNT,
                            Contract.Not_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, Contract.Not_String_Entry.COLUMN_RANK,
                            Contract.CREATION_DATE, Contract.COLUMN_ID,
                            Contract.Not_String_Entry.COLUMN_REMIND_AT, Contract.Not_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND},
                    Contract.String_Entry.COLUMN_FIELD + " NOT IN " + placeholders,
                    notImportedArray, null, null, null);
            for (toImportNotStringEntryCursor.moveToFirst(); toImportNotStringEntryCursor.getPosition() < toImportNotStringEntryCursor.getCount();
                 toImportNotStringEntryCursor.moveToNext()) {
                Cursor questionCursor = toImportDatabase.query(Contract.Not_String.TABLE_NAME, new String[]{Contract.Not_String.COLUMN_VALUE},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getLong(0) + ""}, null, null, null, null);
                questionCursor.moveToFirst();
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(Contract.Not_String.COLUMN_VALUE, questionCursor.getBlob(0));
                long question_id = appDatabase.insert(Contract.Not_String.TABLE_NAME, null, contentValues1);
                questionCursor.close();
                Cursor answerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getLong(1) + ""}, null, null, null, null);
                answerCursor.moveToFirst();
                contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                long answer_id = 0;
                try {
                    answer_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                } catch (Exception e) {
                    Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{answerCursor.getString(0)}, null, null, null, null);
                    c.moveToFirst();
                    answer_id = c.getLong(0);
                    c.close();
                }
                answerCursor.close();
                Cursor toimportfieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_FIELD_NAME},
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getLong(2) + ""}, null, null, null, null);
                toimportfieldCursor.moveToFirst();
                Cursor appfieldCursor = appDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.Field.COLUMN_FIELD_NAME + " = ?",
                        new String[]{toimportfieldCursor.getString(0)}, null, null, null, null);
                appfieldCursor.moveToFirst();
                long field_id = appfieldCursor.getLong(0);
                appfieldCursor.close();
                toimportfieldCursor.close();

                long not_string_entry_id;
                if (importUserUsageData) {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_QUESTION, question_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_ANSWER, answer_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_FIELD, field_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_ASKED_COUNT, toImportNotStringEntryCursor.getInt(3));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, toImportNotStringEntryCursor.getInt(4));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_RANK, toImportNotStringEntryCursor.getInt(5));
                    contentValues1.put(Contract.CREATION_DATE, toImportNotStringEntryCursor.getString(6));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_REMIND_AT, toImportNotStringEntryCursor.getLong(8));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, toImportNotStringEntryCursor.getInt(9));
                    try {
                        not_string_entry_id = appDatabase.insertOrThrow(Contract.Not_String_Entry.TABLE_NAME, null, contentValues1);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.Not_String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.Not_String_Entry.COLUMN_QUESTION + " = ? AND "
                                        + Contract.Not_String_Entry.COLUMN_ANSWER + " = ? AND "
                                        + Contract.Not_String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null, null);
                        c.moveToFirst();
                        not_string_entry_id = c.getLong(0);
                        c.close();
                    }
                } else {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_QUESTION, question_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_ANSWER, answer_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_FIELD, field_id);
                    try {
                        not_string_entry_id = appDatabase.insertOrThrow(Contract.Not_String_Entry.TABLE_NAME, null, contentValues1);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.Not_String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.Not_String_Entry.COLUMN_QUESTION + " = ? AND "
                                        + Contract.Not_String_Entry.COLUMN_ANSWER + " = ? AND "
                                        + Contract.Not_String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null, null);
                        c.moveToFirst();
                        not_string_entry_id = c.getLong(0);
                        c.close();
                    }
                }
                Cursor not_string_alternative_cursor = toImportDatabase.query(Contract.Alternative_Answer_Not_String.TABLE_NAME,
                        new String[]{Contract.Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER,
                                Contract.Alternative_Answer_Not_String.COLUMN_ANSWERED_COUNT},
                        Contract.Alternative_Answer_Not_String.COLUMN_ENTRY + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getLong(7) + ""}, null, null, null, null);
                for (not_string_alternative_cursor.moveToFirst(); not_string_alternative_cursor.getPosition() < not_string_alternative_cursor.getCount();
                     not_string_alternative_cursor.moveToNext()) {
                    Cursor alternativeAnswerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                            Contract.COLUMN_ID + " = ? ",
                            new String[]{not_string_alternative_cursor.getLong(0) + ""}, null, null, null, null);
                    alternativeAnswerCursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_VALUE, alternativeAnswerCursor.getString(0));
                    long wrongAnswer_id = 0;
                    try {
                        wrongAnswer_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.STring.COLUMN_VALUE + " = ? ",
                                new String[]{alternativeAnswerCursor.getString(0)}, null, null, null, null);
                        c.moveToFirst();
                        wrongAnswer_id = c.getLong(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ENTRY, not_string_entry_id);
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER, wrongAnswer_id);
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_ANSWERED_COUNT, not_string_alternative_cursor.getInt(1));
                    appDatabase.insert(Contract.Alternative_Answer_Not_String.TABLE_NAME, null, contentValues);
                    alternativeAnswerCursor.close();
                }
                not_string_alternative_cursor.close();
            }
            toImportNotStringEntryCursor.close();
            appDatabase.setTransactionSuccessful();
            if (notImportedFieldsIDs.size() > 0) {
                showNotification(context, NOT_IMPORTED_FIELDS, notificationManager);
            }
        } finally {
            appDatabase.endTransaction();
        }
    }

    private static void importInternalFields(Context context, SQLiteDatabase toImportDatabase, ArrayList<String> wanted_fields) {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notImportedFieldsIDs = new ArrayList<>();
        SQLiteDatabase appDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        notImportedFieldsStrings = new ArrayList<String>();
        appDatabase.beginTransactionNonExclusive();
        try {
            for (String temp_wanted_field : wanted_fields) {
                Cursor toImportFieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_FIELD_NAME,
                                Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, Contract.COLUMN_ID},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ", new String[]{temp_wanted_field}, null, null, null);
                toImportFieldCursor.moveToFirst();
                Cursor appFieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_FIELD_NAME},
                        Contract.Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{toImportFieldCursor.getString(0)}, null, null, null, null);
                long field_id = -1;
                if (appFieldCursor.getCount() <= 0) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Field.COLUMN_FIELD_NAME, toImportFieldCursor.getString(0));
                    contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, toImportFieldCursor.getInt(1));
                    field_id = appDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);

                    Cursor field_notes_cursor = toImportDatabase.query(Contract.Field_Note.TABLE_NAME,
                            new String[]{Contract.Field_Note.COLUMN_NOTE},
                            Contract.Field_Note.COLUMN_FIELD + " = ? ", new String[]{toImportFieldCursor.getLong(2) + ""}, null, null, null);
                    for (field_notes_cursor.moveToFirst(); field_notes_cursor.getPosition() < field_notes_cursor.getCount();
                         field_notes_cursor.moveToNext()) {
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Field_Note.COLUMN_FIELD, field_id);
                        contentValues.put(Contract.Field_Note.COLUMN_NOTE, field_notes_cursor.getString(0));
                        appDatabase.insert(Contract.Field_Note.TABLE_NAME, null, contentValues);
                    }
                    field_notes_cursor.close();
                    Cursor toImportStringEntryCursor = toImportDatabase.query(Contract.String_Entry.TABLE_NAME,
                            new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                                    Contract.String_Entry.COLUMN_FIELD, Contract.String_Entry.COLUMN_ASKED_COUNT,
                                    Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, Contract.String_Entry.COLUMN_RANK,
                                    Contract.CREATION_DATE, Contract.COLUMN_ID,
                                    Contract.String_Entry.COLUMN_REMIND_AT, Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND,
                                    Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                            Contract.String_Entry.COLUMN_FIELD + " = ? ",
                            new String[]{String.valueOf(toImportFieldCursor.getLong(2))}, null, null, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                                    + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
                    for (toImportStringEntryCursor.moveToFirst(); toImportStringEntryCursor.getPosition() < toImportStringEntryCursor.getCount();
                         toImportStringEntryCursor.moveToNext()) {
                        Cursor questionCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{toImportStringEntryCursor.getLong(0) + ""}, null, null, null, null);
                        questionCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.STring.COLUMN_VALUE, questionCursor.getString(0));
                        long question_id = 0;
                        try {
                            question_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                        } catch (Exception e) {
                            Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                    Contract.STring.COLUMN_VALUE + " = ? ",
                                    new String[]{questionCursor.getString(0)}, null, null, null, null);
                            c.moveToFirst();
                            question_id = c.getLong(0);
                            c.close();
                        }
                        questionCursor.close();
                        Cursor answerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_VALUE},
                                Contract.COLUMN_ID + " = ? ",
                                new String[]{toImportStringEntryCursor.getLong(1) + ""}, null, null, null, null);
                        answerCursor.moveToFirst();
                        contentValues1 = new ContentValues();
                        contentValues1.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                        long answer_id = 0;
                        try {
                            answer_id = appDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues1);
                        } catch (SQLException e) {
                            Cursor c = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                    Contract.STring.COLUMN_VALUE + " = ? ",
                                    new String[]{answerCursor.getString(0)}, null, null, null, null);
                            c.moveToFirst();
                            answer_id = c.getLong(0);
                            c.close();
                        }
                        answerCursor.close();
                        contentValues1 = new ContentValues();
                        contentValues1.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                        contentValues1.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                        contentValues1.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                        contentValues1.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, toImportStringEntryCursor.getInt(10));
                        int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                        if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                            contentValues1.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                    Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, appDatabase));
                        }
                        appDatabase.insertOrThrow(Contract.String_Entry.TABLE_NAME, null, contentValues1);
                    }
                } else {
                    notImportedFieldsStrings.add(toImportFieldCursor.getString(0));
                    continue;
                }
                appFieldCursor.close();
                toImportFieldCursor.close();
            }
            appDatabase.setTransactionSuccessful();
        } finally {
            appDatabase.endTransaction();
        }
        if (notImportedFieldsStrings.size() > 0) {
            showNotification(context, NOT_IMPORTED_FIELDS, notificationManager);
        }
        if (wanted_fields.size() > notImportedFieldsStrings.size()) {
            showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
        }
    }


    private static void showNotification(Context context, int notificationId, NotificationManager notificationManager) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        TaskStackBuilder stackBuilder = null;
        switch (notificationId) {
            case FAILED_EXPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_failed))
                                .setContentText(context.getResources().getString(R.string.export_failed))
                                .setTicker(context.getResources().getString(R.string.export_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, FAILED_EXPORT_NOTIFICATION);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case FAILED_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.import_failed))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case FAILED_IMPORT_INVALID_FILE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.invalid_file))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESSFUL_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_completed))
                                .setContentText(context.getResources().getString(R.string.import_completed))
                                .setTicker(context.getResources().getString(R.string.import_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case NOT_IMPORTED_FIELDS:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.not_imported_fields))
                                .setContentText(context.getString(R.string.not_imported_fields))
                                .setTicker(context.getResources().getString(R.string.not_imported_fields))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, NotImportedFieldsActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, notImportedFieldsStrings);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                (int) System.currentTimeMillis(),
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }

    private static void showNotification(Context context, String path, int notificationId, NotificationManager notificationManager) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher512)
                        .setContentTitle(context.getResources().getString(R.string.export_completed))
                        .setContentText(context.getResources().getString(R.string.export_completed))
                        .setTicker(context.getResources().getString(R.string.export_completed))
                        .setAutoCancel(true);
        notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
        notifyIntent.putExtra(PATH, path);
        notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, SUCCESFUL_EXPORT_NOTIFICATION);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        (int) System.currentTimeMillis(),
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private static void upgradeImportedDatabaseFromDBV6ToDBV7(SQLiteDatabase toImportDatabase, String file_path) throws Exception {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        toImportDatabase.beginTransaction();
        try {
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE_OLD);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_STRING_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_EXCLUDED_TIME_TABLE);
            String copy = "INSERT INTO " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " ( " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME
                    + " , " + Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE
                    + " , " + Contract.CREATION_DATE + " ) "
                    + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                    + "," + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE
                    + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                    + " FROM " + "tempTable"
                    + ";";
            toImportDatabase.execSQL(copy);  // At this point the data of the old field table has been copied to the new field table
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            /////////////////////////////
            Cursor questionCursor = toImportDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY},
                    null, null, null, null, null);
            for (questionCursor.moveToFirst(); questionCursor.getPosition() < questionCursor.getCount();
                 questionCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE, questionCursor.getString(0));
                toImportDatabase.insert(Contract.STring.TABLE_NAME, null, contentValues);
            }
            questionCursor.close();
            Cursor answerCursor = toImportDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY},
                    null, null, null, null, null);
            for (answerCursor.moveToFirst(); answerCursor.getPosition() < answerCursor.getCount();
                 answerCursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE, answerCursor.getString(0));
                toImportDatabase.insert(Contract.STring.TABLE_NAME, null, contentValues);
            }
            answerCursor.close();
            // at this point the needed data on the old question and old answer tables has been copied to the new STring table.
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + ";";
            toImportDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + ";";
            toImportDatabase.execSQL(drop);
            /////////////////////////////////
            Cursor linkerCursor = toImportDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER, Contract.Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS, Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32},
                    null, null, null, null, null, null);
            for (linkerCursor.moveToFirst(); linkerCursor.getPosition() < linkerCursor.getCount();
                 linkerCursor.moveToNext()) {
                String field = linkerCursor.getString(2);
                Cursor question_id_cursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(0)}, null, null, null, null);
                question_id_cursor.moveToFirst();
                long question_id = question_id_cursor.getLong(0);
                Cursor answer_id_cursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(1)}, null, null, null, null);
                answer_id_cursor.moveToFirst();
                long answer_id = answer_id_cursor.getLong(0);
                Cursor field_id_cursor = toImportDatabase.query(Contract.Old_Field_DBV12_AppV68.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{field}, null, null, null, null);
                field_id_cursor.moveToFirst();
                long field_id = field_id_cursor.getLong(0);
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Old_String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.Old_String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.Old_String_Entry.COLUMN_FIELD, field_id);
                contentValues.put(Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER, linkerCursor.getInt(3));
                contentValues.put(Contract.Old_String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, linkerCursor.getInt(4));
                contentValues.put(Contract.CREATION_DATE, linkerCursor.getString(6));
                toImportDatabase.insert(Contract.Old_String_Entry.TABLE_NAME, null, contentValues);
            }
            drop = "DROP TABLE IF EXISTS " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + ";";
            toImportDatabase.execSQL(drop);
            toImportDatabase.setVersion(7);
            toImportDatabase.setTransactionSuccessful();

        } finally {
            toImportDatabase.endTransaction();
/*            File exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }*/
            String journalFileName = file_path + "-journal";
            File journalFile = new File(journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void upgradeImportedDatabaseFromDBV7ToDBV8(SQLiteDatabase toImportDatabase, String file_path) throws Exception {
        toImportDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
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
            toImportDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.Old_String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE_OLD);
            copy = "INSERT INTO " + Contract.Old_String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Old_String_Entry.COLUMN_QUESTION
                    + " , " + Contract.Old_String_Entry.COLUMN_ANSWER
                    + " , " + Contract.Old_String_Entry.COLUMN_FIELD
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + Contract.Old_String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + Contract.Old_String_Entry.COLUMN_ASKED_COUNT
                    + " , " + Contract.Old_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + Contract.Old_String_Entry.COLUMN_RANK
                    + " , " + Contract.Old_String_Entry.COLUMN_CREATION_EMULATED_DATE
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
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            toImportDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_EXCLUDED_TIME_TABLE);
            toImportDatabase.setVersion(8);
            toImportDatabase.setTransactionSuccessful();
        } finally {
            toImportDatabase.endTransaction();
 /*           File exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }*/
            String journalFileName = file_path + "-journal";
            File journalFile = new File(journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void upgradeImportedDatabaseFromDBV8ToDBV9(SQLiteDatabase toImportDatabase, String file_path) throws Exception {
        toImportDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV12_AppV68.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_DBV12_APPV68_TABLE);
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
            toImportDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            toImportDatabase.setVersion(9);
            toImportDatabase.setTransactionSuccessful();
        } finally {
            toImportDatabase.endTransaction();
  /*          File exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }*/
            String journalFileName = file_path + "-journal";
            File journalFile = new File(journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void upgradeImportedDatabaseFromDBV9ToDBV10(SQLiteDatabase toImportDatabase, String file_path) throws Exception {
        toImportDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE_OLD);
            String copy = "INSERT INTO " + Contract.Old_String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
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
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            toImportDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Contract.Not_String_Entry.TABLE_NAME + ";";
            toImportDatabase.execSQL(drop);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_NOTE_TABLE);
            toImportDatabase.setVersion(10);
            toImportDatabase.setTransactionSuccessful();
        } finally {
            toImportDatabase.endTransaction();
            String journalFileName = file_path + "-journal";
            File journalFile = new File(journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void upgradeImportedDatabaseFromDBV12ToDBV13(SQLiteDatabase toImportDatabase, String file_path) throws Exception {
        toImportDatabase.beginTransaction();
        try {
            toImportDatabase.delete(Contract.Field.TABLE_NAME, Contract.Old_Field_DBV12_AppV68.COLUMN_FIELD_TYPE + " = ? ",
                    new String[]{String.valueOf(Constants.FieldTypes.NOTEBOOK_TYPE_REMOVED_FEATURE_V68)});
            ContentValues contentValues;
            String rename = "ALTER TABLE " + Contract.Field.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Field.COLUMN_FIELD_NAME
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
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL
                    + " , " + Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL
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
            toImportDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            toImportDatabase.execSQL(rename);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);
            copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.String_Entry.COLUMN_QUESTION
                    + " , " + Contract.String_Entry.COLUMN_ANSWER
                    + " , " + Contract.String_Entry.COLUMN_FIELD
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
            toImportDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            toImportDatabase.execSQL(drop);
            contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, 999999999);
            toImportDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " IS NULL ", null);
            drop = "DROP TABLE IF EXISTS " + Contract.Not_String_Entry.TABLE_NAME + ";";
            toImportDatabase.execSQL(drop);
            toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
            toImportDatabase.setVersion(13);
            toImportDatabase.setTransactionSuccessful();
        } finally {
            toImportDatabase.endTransaction();
            String journalFileName = file_path + "-journal";
            File journalFile = new File(journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

}
