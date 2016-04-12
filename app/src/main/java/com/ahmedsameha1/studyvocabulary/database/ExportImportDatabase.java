package com.ahmedsameha1.studyvocabulary.database;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteTransactionListener;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.studyvocabulary.activities.MainActivity;
import com.ahmedsameha1.studyvocabulary.dialogs.ExportSuccessfulActivity;
import com.ahmedsameha1.studyvocabulary.dialogs.NotImportedFieldsActivity;
import com.ahmedsameha1.studyvocabulary.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ahmed on 1/5/2016.
 */
public class ExportImportDatabase {
    public static final String EXPORT_IMPORT_DIRECTORY_NAME = "StudyWithoutPen";
    public static final int FAILED_EXPORT_NOTIFICATION = 0;
    public static final int SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION = 1;
    public static final int SUCCESFUL_EMULATED_STORAGE_NOTIFICATION = 2;
    public static final int FAILED_IMPORT_NOTIFICATION = 3;
    public static final int FAILED_IMPORT_INVALID_FILE_NOTIFICATION = 4;
    public static final int SUCCESSFUL_IMPORT_NOTIFICATION = 5;
    public static final int NOT_IMPORTED_FIELDS = 6;

    private static Thread exportThread, importThread;
    private static ArrayList<Integer> notImportedFieldsIntegers;
    private static ArrayList<String> notImportedFieldsStrings;
    public static Thread getExportThread() {
        return exportThread;
    }
    public static Thread getImportThread() {
        return importThread;
    }

    public static void startExportThread(final Context context, final ArrayList<String> selectedFields, final String fileName) {
        exportThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                exportFields(context, selectedFields, fileName);
            }
        });
        exportThread.start();
    }

    public static void startImportThread(final Context context, final String fileName, final boolean importUserUsageData ) {
        importThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                importFields(context, fileName, importUserUsageData );
            }
        });
        importThread.start();
    }

    private static void exportFields(Context context, ArrayList<String> fieldNames, String fileName) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        DatabaseInstance.initializeInstance(new DatabaseHelper(context));
        SQLiteDatabase appDatabase = DatabaseInstance.getInstance().openDatabase();
        fileName = fileName.toLowerCase();
        SQLiteDatabase toExportDatabase = null;
        File exportDirectory = null;
        File exportedDatabaseFile = null;
        try {
            exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }
            exportedDatabaseFile = new File(exportDirectory, fileName);
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
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Cursor fieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME, Contract.Field.COLUMN_NAME_FIELD_TYPE,
                                Contract.Field.COLUMN_NAME_ENTRY_ANSWER_COLUMN_DATA_TYPE, Contract.Field.COLUMN_NAME_SORT_BY,
                                Contract.Field.COLUMN_NAME_LIST_TEXT_SIZE, Contract.Field.COLUMN_NAME_QUESTION_AREA_SIZE,
                                Contract.Field.COLUMN_NAME_ANSWER_AREA_SIZE, Contract.Field.COLUMN_NAME_TEST_TEXT_SIZE,
                                Contract.Field.COLUMN_NAME_ACTION_BAR_SHOWN, Contract.Field.COLUMN_NAME_USAGE_COUNT,
                                Contract.CREATION_DATE,Contract.Field.COLUMN_NAME_CREATION_EMULATION_NUMBER},
                        Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                        new String[]{tempFieldName}, null, null, null, null);
                fieldCursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME, fieldCursor.getString(0));
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE, fieldCursor.getInt(1));
                contentValues.put(Contract.Field.COLUMN_NAME_ENTRY_ANSWER_COLUMN_DATA_TYPE, fieldCursor.getInt(2));
                contentValues.put(Contract.Field.COLUMN_NAME_SORT_BY, fieldCursor.getInt(3));
                contentValues.put(Contract.Field.COLUMN_NAME_LIST_TEXT_SIZE, fieldCursor.getInt(4));
                contentValues.put(Contract.Field.COLUMN_NAME_QUESTION_AREA_SIZE, fieldCursor.getInt(5));
                contentValues.put(Contract.Field.COLUMN_NAME_ANSWER_AREA_SIZE, fieldCursor.getInt(6));
                contentValues.put(Contract.Field.COLUMN_NAME_TEST_TEXT_SIZE, fieldCursor.getInt(7));
                contentValues.put(Contract.Field.COLUMN_NAME_ACTION_BAR_SHOWN, fieldCursor.getInt(8));
                contentValues.put(Contract.Field.COLUMN_NAME_USAGE_COUNT, fieldCursor.getInt(9));
                contentValues.put(Contract.CREATION_DATE, fieldCursor.getString(10));
                contentValues.put(Contract.Field.COLUMN_NAME_CREATION_EMULATION_NUMBER, fieldCursor.getInt(11));
                Long field_id = toExportDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
                fieldCursor.close();
                Cursor stringEntryCursor = appDatabase.query(Contract.String_Entry.TABLE_NAME,
                        new String[]{Contract.String_Entry.COLUMN_NAME_QUESTION, Contract.String_Entry.COLUMN_NAME_ANSWER,
                                Contract.String_Entry.COLUMN_NAME_FIELD, Contract.String_Entry.COLUMN_NAME_ASKED_COUNT,
                                Contract.String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT, Contract.String_Entry.COLUMN_NAME_RANK,
                                Contract.CREATION_DATE, Contract.COLUMN_NAME_ID, Contract.String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE},
                        Contract.String_Entry.COLUMN_NAME_FIELD + " = ?",
                        new String[]{tempFieldName}, null, null, null, null);
                for (stringEntryCursor.moveToFirst(); stringEntryCursor.getPosition() < stringEntryCursor.getCount();
                     stringEntryCursor.moveToNext()) {
                    Cursor questionCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{stringEntryCursor.getInt(0) + ""}, null, null, null, null);
                    questionCursor.moveToFirst();
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, questionCursor.getString(0));
                    long question_id = toExportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                    questionCursor.close();
                    Cursor answerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{stringEntryCursor.getInt(1) + ""}, null, null, null, null);
                    answerCursor.moveToFirst();
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, answerCursor.getString(0));
                    long answer_id = toExportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                    answerCursor.close();
                    ///////////////////////////////////////////////////////////
                    contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_FIELD,field_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_ASKED_COUNT,stringEntryCursor.getInt(3));
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT,stringEntryCursor.getInt(4));
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_RANK,stringEntryCursor.getInt(5));
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE,stringEntryCursor.getString(8));
                    contentValues.put(Contract.CREATION_DATE,stringEntryCursor.getString(6));
                    long stringEntry_id = toExportDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
                    Cursor alternativeStringCursor = appDatabase.query(Contract.Alternative_Answer_String.TABLE_NAME,
                            new String[]{Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER,
                                    Contract.Alternative_Answer_String.COLUMN_NAME_ANSWERED_COUNT},
                            Contract.Alternative_Answer_String.COLUMN_NAME_ENTRY + " = ? ",
                            new String[]{stringEntryCursor.getInt(7) + ""}, null,null,null,null);
                    for (alternativeStringCursor.moveToFirst(); alternativeStringCursor.getPosition() < alternativeStringCursor.getCount();
                         alternativeStringCursor.moveToNext()) {
                        Cursor alternativeAnswerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                                Contract.COLUMN_NAME_ID + " = ? ",
                                new String[]{alternativeStringCursor.getInt(0) + ""}, null, null, null, null);
                        alternativeAnswerCursor.moveToFirst();
                        contentValues = new ContentValues();
                        contentValues.put(Contract.STring.COLUMN_NAME_VALUE,alternativeAnswerCursor.getString(0));
                        long wrongAnswer_id = toExportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ENTRY,stringEntry_id);
                        contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER,wrongAnswer_id);
                        contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ANSWERED_COUNT,alternativeStringCursor.getInt(1));
                        toExportDatabase.insert(Contract.Alternative_Answer_String.TABLE_NAME,null,contentValues);
                        alternativeAnswerCursor.close();
                    }
                    questionCursor.close();
                    answerCursor.close();
                    alternativeStringCursor.close();
                }
                stringEntryCursor.close();
                Cursor notstringEntryCursor = appDatabase.query(Contract.Not_String_Entry.TABLE_NAME,
                        new String[]{Contract.Not_String_Entry.COLUMN_NAME_QUESTION, Contract.Not_String_Entry.COLUMN_NAME_ANSWER,
                                Contract.Not_String_Entry.COLUMN_NAME_FIELD, Contract.Not_String_Entry.COLUMN_NAME_ASKED_COUNT,
                                Contract.Not_String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT, Contract.Not_String_Entry.COLUMN_NAME_RANK,
                                Contract.CREATION_DATE, Contract.COLUMN_NAME_ID, Contract.Not_String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE},
                        Contract.Not_String_Entry.COLUMN_NAME_FIELD + " = ?",
                        new String[]{tempFieldName}, null, null, null, null);
                for (notstringEntryCursor.moveToFirst(); notstringEntryCursor.getPosition() < notstringEntryCursor.getCount();
                     notstringEntryCursor.moveToNext()) {
                    Cursor questionCursor = appDatabase.query(Contract.Not_String.TABLE_NAME, new String[]{Contract.Not_String.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{notstringEntryCursor.getInt(0) + ""}, null, null, null, null);
                    questionCursor.moveToFirst();
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Not_String.COLUMN_NAME_VALUE, questionCursor.getBlob(0));
                    long question_id = toExportDatabase.insertWithOnConflict(Contract.Not_String.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                    questionCursor.close();
                    Cursor answerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{notstringEntryCursor.getInt(1) + ""}, null, null, null, null);
                    answerCursor.moveToFirst();
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, answerCursor.getString(0));
                    long answer_id = toExportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                    answerCursor.close();
                    ///////////////////////////////////////////////////////////
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_FIELD,field_id);
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_ASKED_COUNT,notstringEntryCursor.getInt(3));
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT,notstringEntryCursor.getInt(4));
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_RANK,notstringEntryCursor.getInt(5));
                    contentValues.put(Contract.Not_String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE,notstringEntryCursor.getString(8));
                    contentValues.put(Contract.CREATION_DATE,notstringEntryCursor.getString(6));
                    long stringEntry_id = toExportDatabase.insert(Contract.Not_String_Entry.TABLE_NAME, null, contentValues);
                    Cursor alternativeNotStringCursor = appDatabase.query(Contract.Alternative_Answer_Not_String.TABLE_NAME,
                            new String[]{Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER,
                                    Contract.Alternative_Answer_Not_String.COLUMN_NAME_ANSWERED_COUNT},
                            Contract.Alternative_Answer_Not_String.COLUMN_NAME_ENTRY + " = ? ",
                            new String[]{notstringEntryCursor.getInt(7) + ""}, null,null,null,null);
                    for (alternativeNotStringCursor.moveToFirst(); alternativeNotStringCursor.getPosition() < alternativeNotStringCursor.getCount();
                         alternativeNotStringCursor.moveToNext()) {
                        Cursor alternativeAnswerCursor = appDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                                Contract.COLUMN_NAME_ID + " = ? ",
                                new String[]{alternativeNotStringCursor.getInt(0) + ""}, null, null, null, null);
                        alternativeAnswerCursor.moveToFirst();
                        contentValues = new ContentValues();
                        contentValues.put(Contract.STring.COLUMN_NAME_VALUE,alternativeAnswerCursor.getString(0));
                        long wrongAnswer_id = toExportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ENTRY,stringEntry_id);
                        contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER,wrongAnswer_id);
                        contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ANSWERED_COUNT,alternativeNotStringCursor.getInt(1));
                        toExportDatabase.insert(Contract.Alternative_Answer_Not_String.TABLE_NAME,null,contentValues);
                        alternativeAnswerCursor.close();
                    }
                    questionCursor.close();
                    answerCursor.close();
                    alternativeNotStringCursor.close();
                }
                notstringEntryCursor.close();
            }
            toExportDatabase.execSQL("CREATE TABLE " + Contract.Check.TABLE_NAME + " ( " + Contract.Check.COLUMN_NAME_CHECK_NUMBER
                    + " INTEGER PRIMARY KEY NOT NULL );");
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Check.COLUMN_NAME_CHECK_NUMBER, Contract.Check.CHECK_NUMBER);
            toExportDatabase.insert(Contract.Check.TABLE_NAME, null, contentValues);
            toExportDatabase.setTransactionSuccessful();
            if (Environment.isExternalStorageRemovable()) {
                showNotification(context, SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION, notificationManager);
            } else if (Environment.isExternalStorageEmulated()) {
                showNotification(context, SUCCESFUL_EMULATED_STORAGE_NOTIFICATION, notificationManager);
            }
        } catch (SQLiteException e) {
            if (exportedDatabaseFile != null && exportedDatabaseFile.exists()) {
                exportedDatabaseFile.delete();
            }
            showNotification(context, FAILED_EXPORT_NOTIFICATION, notificationManager);
        } finally {
            if (toExportDatabase != null) {
                toExportDatabase.endTransaction();
                toExportDatabase.close();
                DatabaseInstance.getInstance().closeDatabase();
            }
            String journalFileName = fileName + "-journal";
            File journalFile = new File(exportDirectory, journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void importFields(Context context, String fileName, boolean importUserUsageData ) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        fileName = fileName.toLowerCase();
        SQLiteDatabase toImportDatabase = null;
        try {
            String filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName;
            toImportDatabase = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = toImportDatabase.query(Contract.Check.TABLE_NAME, new String[]{Contract.Check.COLUMN_NAME_CHECK_NUMBER},
                    null, null, null, null, null);
            cursor.moveToFirst();
            int checkNumber = cursor.getInt(0);
            cursor.close();
            if (checkNumber != Contract.Check.CHECK_NUMBER) {
                throw new InvalidStudyWithoutPenFileException();
            }
            int databaseVersion = toImportDatabase.getVersion();
            if (databaseVersion == 6) {
                upgradeImportedDatabaseToDBV7(toImportDatabase);
                importFields(context, toImportDatabase, importUserUsageData );
            } else if (databaseVersion == 7) {
                importFields(context, toImportDatabase, importUserUsageData );
            }
            showNotification(context, SUCCESSFUL_IMPORT_NOTIFICATION, notificationManager);
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


    private static void importFields(Context context, SQLiteDatabase toImportDatabase, boolean importUserUsageData )
            throws IOException, SQLiteException, InvalidStudyWithoutPenFileException {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notImportedFieldsIntegers = new ArrayList<Integer>();
        DatabaseInstance.initializeInstance(new DatabaseHelper(context));
        SQLiteDatabase appDatabase = DatabaseInstance.getInstance().openDatabase();
        MySQLiteCursorLoader mySQLiteCursorLoader = new MySQLiteCursorLoader(context,new DatabaseHelper(context));
        appDatabase.beginTransactionNonExclusive();
        try {
            Cursor toImportFieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME,
                    new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME, Contract.Field.COLUMN_NAME_FIELD_TYPE,
                            Contract.Field.COLUMN_NAME_ENTRY_ANSWER_COLUMN_DATA_TYPE, Contract.Field.COLUMN_NAME_SORT_BY,
                            Contract.Field.COLUMN_NAME_LIST_TEXT_SIZE, Contract.Field.COLUMN_NAME_QUESTION_AREA_SIZE,
                            Contract.Field.COLUMN_NAME_ANSWER_AREA_SIZE, Contract.Field.COLUMN_NAME_TEST_TEXT_SIZE,
                            Contract.Field.COLUMN_NAME_ACTION_BAR_SHOWN, Contract.Field.COLUMN_NAME_USAGE_COUNT,
                            Contract.CREATION_DATE,Contract.Field.COLUMN_NAME_CREATION_EMULATION_NUMBER,Contract.COLUMN_NAME_ID}, null, null, null, null, null);
            for (toImportFieldCursor.moveToFirst(); toImportFieldCursor.getPosition() < toImportFieldCursor.getCount();
                 toImportFieldCursor.moveToNext()) {

                Cursor appFieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,
                        new String[]{Contract.Field.COLUMN_NAME_FIELD_TYPE},
                        Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                        new String[]{toImportFieldCursor.getString(0)},null,null,null,null);
                if (appFieldCursor.getCount() <= 0) {
                    if ( importUserUsageData ) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME,toImportFieldCursor.getString(0));
                        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE,toImportFieldCursor.getInt(1));
                        contentValues.put(Contract.Field.COLUMN_NAME_ENTRY_ANSWER_COLUMN_DATA_TYPE,toImportFieldCursor.getInt(2));
                        contentValues.put(Contract.Field.COLUMN_NAME_SORT_BY,toImportFieldCursor.getInt(3));
                        contentValues.put(Contract.Field.COLUMN_NAME_LIST_TEXT_SIZE,toImportFieldCursor.getInt(4));
                        contentValues.put(Contract.Field.COLUMN_NAME_QUESTION_AREA_SIZE,toImportFieldCursor.getInt(5));
                        contentValues.put(Contract.Field.COLUMN_NAME_ANSWER_AREA_SIZE,toImportFieldCursor.getInt(6));
                        contentValues.put(Contract.Field.COLUMN_NAME_TEST_TEXT_SIZE,toImportFieldCursor.getInt(7));
                        contentValues.put(Contract.Field.COLUMN_NAME_ACTION_BAR_SHOWN,toImportFieldCursor.getInt(8));
                        contentValues.put(Contract.Field.COLUMN_NAME_USAGE_COUNT,toImportFieldCursor.getInt(9));
                        contentValues.put(Contract.Field.COLUMN_NAME_CREATION_EMULATION_NUMBER,toImportFieldCursor.getInt(11));
                        contentValues.put(Contract.CREATION_DATE,toImportFieldCursor.getString(10));
                        mySQLiteCursorLoader.insert(Contract.Field.TABLE_NAME, null, contentValues);
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME,toImportFieldCursor.getString(0));
                        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE,toImportFieldCursor.getInt(1));
                        contentValues.put(Contract.Field.COLUMN_NAME_ENTRY_ANSWER_COLUMN_DATA_TYPE,toImportFieldCursor.getInt(2));
                        mySQLiteCursorLoader.insert(Contract.Field.TABLE_NAME, null, contentValues);
                    }
                } else {
                    appFieldCursor.moveToFirst();
                    if (toImportFieldCursor.getInt(1) != appFieldCursor.getInt(0)) {
                        notImportedFieldsIntegers.add(toImportFieldCursor.getInt(12));
                    }
                }
                appFieldCursor.close();
            }
            toImportFieldCursor.close();

            String[] notImportedArray = notImportedFieldsIntegers.size() <= 0 ? new String[]{""} : notImportedFieldsIntegers.toArray(new String[notImportedFieldsIntegers.size()]);
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
            for ( Integer field_id : notImportedFieldsIntegers ) {
                Cursor fieldNameCursor = toImportDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{field_id.toString()},null,null,null,null);
                fieldNameCursor.moveToFirst();
                notImportedFieldsStrings.add(fieldNameCursor.getString(0));
            }
            Cursor toImportStringEntryCursor = toImportDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.String_Entry.COLUMN_NAME_QUESTION, Contract.String_Entry.COLUMN_NAME_ANSWER,
                            Contract.String_Entry.COLUMN_NAME_FIELD, Contract.String_Entry.COLUMN_NAME_ASKED_COUNT,
                            Contract.String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT, Contract.String_Entry.COLUMN_NAME_RANK,
                            Contract.CREATION_DATE, Contract.COLUMN_NAME_ID, Contract.String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE},
                    Contract.COLUMN_NAME_ID + " NOT IN " + placeholders,
                    notImportedArray, null, null, null);
            for (toImportStringEntryCursor.moveToFirst(); toImportStringEntryCursor.getPosition() < toImportStringEntryCursor.getCount();
                 toImportStringEntryCursor.moveToNext()) {
                Cursor questionCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getInt(0) + ""}, null, null, null, null);
                questionCursor.moveToFirst();
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, questionCursor.getString(0));
                long question_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                questionCursor.close();
                Cursor answerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getInt(1) + ""}, null, null, null, null);
                answerCursor.moveToFirst();
                contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, answerCursor.getString(0));
                long answer_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                answerCursor.close();
                Cursor toimportfieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportStringEntryCursor.getInt(2) + ""}, null, null, null, null);
                toimportfieldCursor.moveToFirst();
                Cursor appfieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                        Contract.Field.COLUMN_NAME_FIELD_NAME + " = ",
                        new String[]{toimportfieldCursor.getString(0)},null,null,null,null);
                appfieldCursor.moveToFirst();
                int field_id = appfieldCursor.getInt(0);
                appfieldCursor.close();
                toimportfieldCursor.close();
                long string_entry_id;
                if ( importUserUsageData ) {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_FIELD,field_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_ASKED_COUNT,toImportStringEntryCursor.getInt(3));
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT,toImportStringEntryCursor.getInt(4));
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_RANK,toImportStringEntryCursor.getInt(5));
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE,toImportStringEntryCursor.getString(8));
                    contentValues1.put(Contract.CREATION_DATE,toImportStringEntryCursor.getString(6));
                    string_entry_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.String_Entry.TABLE_NAME,null,contentValues1,SQLiteDatabase.CONFLICT_IGNORE);
                } else {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues1.put(Contract.String_Entry.COLUMN_NAME_FIELD,field_id);
                    string_entry_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.String_Entry.TABLE_NAME,null,contentValues1,SQLiteDatabase.CONFLICT_IGNORE);
                }
                Cursor string_alternative_cursor = toImportDatabase.query(Contract.Alternative_Answer_String.TABLE_NAME,
                        new String[]{Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER,
                                Contract.Alternative_Answer_String.COLUMN_NAME_ANSWERED_COUNT},
                        Contract.Alternative_Answer_String.COLUMN_NAME_ENTRY + " = ? ",
                        new String[]{toImportStringEntryCursor.getInt(7) + ""},null,null,null,null);
                for (string_alternative_cursor.moveToFirst(); string_alternative_cursor.getPosition() < string_alternative_cursor.getCount();
                     string_alternative_cursor.moveToNext()) {
                    Cursor alternativeAnswerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{string_alternative_cursor.getInt(0) + ""}, null, null, null, null);
                    alternativeAnswerCursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE,alternativeAnswerCursor.getString(0));
                    long wrongAnswer_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.STring.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ENTRY,string_entry_id);
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER,wrongAnswer_id);
                    contentValues.put(Contract.Alternative_Answer_String.COLUMN_NAME_ANSWERED_COUNT,string_alternative_cursor.getInt(1));
                    mySQLiteCursorLoader.insertWithOnConflict(Contract.Alternative_Answer_String.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                    alternativeAnswerCursor.close();
                }
                string_alternative_cursor.close();
            }
            toImportStringEntryCursor.close();

            Cursor toImportNotStringEntryCursor = toImportDatabase.query(Contract.Not_String_Entry.TABLE_NAME,
                    new String[]{Contract.Not_String_Entry.COLUMN_NAME_QUESTION, Contract.Not_String_Entry.COLUMN_NAME_ANSWER,
                            Contract.Not_String_Entry.COLUMN_NAME_FIELD, Contract.Not_String_Entry.COLUMN_NAME_ASKED_COUNT,
                            Contract.Not_String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT, Contract.Not_String_Entry.COLUMN_NAME_RANK,
                            Contract.CREATION_DATE, Contract.COLUMN_NAME_ID, Contract.Not_String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE},
                    Contract.COLUMN_NAME_ID + " NOT IN " + placeholders,
                    notImportedArray, null, null, null);
            for (toImportNotStringEntryCursor.moveToFirst(); toImportNotStringEntryCursor.getPosition() < toImportNotStringEntryCursor.getCount();
                 toImportNotStringEntryCursor.moveToNext()) {
                Cursor questionCursor = toImportDatabase.query(Contract.Not_String.TABLE_NAME, new String[]{Contract.Not_String.COLUMN_NAME_VALUE},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getInt(0) + ""}, null, null, null, null);
                questionCursor.moveToFirst();
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(Contract.Not_String.COLUMN_NAME_VALUE, questionCursor.getBlob(0));
                long question_id = mySQLiteCursorLoader.insert(Contract.Not_String.TABLE_NAME, null, contentValues1);
                questionCursor.close();
                Cursor answerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getInt(1) + ""}, null, null, null, null);
                answerCursor.moveToFirst();
                contentValues1 = new ContentValues();
                contentValues1.put(Contract.STring.COLUMN_NAME_VALUE, answerCursor.getString(0));
                long answer_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues1, SQLiteDatabase.CONFLICT_IGNORE);
                answerCursor.close();
                Cursor toimportfieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getInt(2) + ""}, null, null, null, null);
                toimportfieldCursor.moveToFirst();
                Cursor appfieldCursor = appDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                        Contract.Field.COLUMN_NAME_FIELD_NAME + " = ",
                        new String[]{toimportfieldCursor.getString(0)},null,null,null,null);
                appfieldCursor.moveToFirst();
                int field_id = appfieldCursor.getInt(0);
                appfieldCursor.close();
                toimportfieldCursor.close();

                long not_string_entry_id;
                if ( importUserUsageData ) {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_FIELD,field_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_ASKED_COUNT,toImportNotStringEntryCursor.getInt(3));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_WRONGLY_ANSWERED_COUNT,toImportNotStringEntryCursor.getInt(4));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_RANK,toImportNotStringEntryCursor.getInt(5));
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_CREATION_EMULATED_DATE,toImportNotStringEntryCursor.getString(8));
                    contentValues1.put(Contract.CREATION_DATE,toImportNotStringEntryCursor.getString(6));
                    not_string_entry_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.Not_String_Entry.TABLE_NAME,null,contentValues1,SQLiteDatabase.CONFLICT_IGNORE);
                } else {
                    contentValues1 = new ContentValues();
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_QUESTION,question_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_ANSWER,answer_id);
                    contentValues1.put(Contract.Not_String_Entry.COLUMN_NAME_FIELD,field_id);
                    not_string_entry_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.Not_String_Entry.TABLE_NAME,null,contentValues1,SQLiteDatabase.CONFLICT_IGNORE);
                }
                Cursor not_string_alternative_cursor = toImportDatabase.query(Contract.Alternative_Answer_Not_String.TABLE_NAME,
                        new String[]{Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER,
                                Contract.Alternative_Answer_Not_String.COLUMN_NAME_ANSWERED_COUNT},
                        Contract.Alternative_Answer_Not_String.COLUMN_NAME_ENTRY + " = ? ",
                        new String[]{toImportNotStringEntryCursor.getInt(7) + ""},null,null,null,null);
                for (not_string_alternative_cursor.moveToFirst(); not_string_alternative_cursor.getPosition() < not_string_alternative_cursor.getCount();
                     not_string_alternative_cursor.moveToNext()) {
                    Cursor alternativeAnswerCursor = toImportDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.STring.COLUMN_NAME_VALUE},
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{not_string_alternative_cursor.getInt(0) + ""}, null, null, null, null);
                    alternativeAnswerCursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE,alternativeAnswerCursor.getString(0));
                    long wrongAnswer_id = mySQLiteCursorLoader.insertWithOnConflict(Contract.STring.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ENTRY,not_string_entry_id);
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER,wrongAnswer_id);
                    contentValues.put(Contract.Alternative_Answer_Not_String.COLUMN_NAME_ANSWERED_COUNT,not_string_alternative_cursor.getInt(1));
                    mySQLiteCursorLoader.insertWithOnConflict(Contract.Alternative_Answer_Not_String.TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
                    alternativeAnswerCursor.close();
                }
                not_string_alternative_cursor.close();
            }
            toImportNotStringEntryCursor.close();
            appDatabase.setTransactionSuccessful();
            if (notImportedFieldsIntegers.size() > 0) {
                showNotification(context, NOT_IMPORTED_FIELDS, notificationManager);
            }
        } finally {
            appDatabase.endTransaction();
            DatabaseInstance.getInstance().closeDatabase();
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
            case SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION);
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
            case SUCCESFUL_EMULATED_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getResources().getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, SUCCESFUL_EMULATED_STORAGE_NOTIFICATION);
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
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
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
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }

    private static void upgradeImportedDatabaseToDBV7(SQLiteDatabase toImportDatabase) {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        toImportDatabase.execSQL(rename);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_STRING_ENTRY_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_NOT_STRING_ENTRY_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_STRING_TABLE);
        toImportDatabase.execSQL(DatabaseHelper.CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE);
        String copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.Field.COLUMN_NAME_FIELD_NAME
                + " , " + Contract.Field.COLUMN_NAME_FIELD_TYPE
                + " , " + Contract.CREATION_DATE + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME
                + "," + Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        toImportDatabase.execSQL(copy);  // At this point the data of the old field table has been copied to the new field table
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        toImportDatabase.execSQL(drop);
        /////////////////////////////
        Cursor questionCursor = toImportDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME,
                new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                null, null, null, null, null);
        for (questionCursor.moveToFirst(); questionCursor.getPosition() < questionCursor.getCount();
             questionCursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.STring.COLUMN_NAME_VALUE, questionCursor.getString(0));
            toImportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
        questionCursor.close();
        Cursor answerCursor = toImportDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME,
                new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                null, null, null, null, null);
        for (answerCursor.moveToFirst(); answerCursor.getPosition() < answerCursor.getCount();
             answerCursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.STring.COLUMN_NAME_VALUE, answerCursor.getString(0));
            toImportDatabase.insertWithOnConflict(Contract.STring.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
        answerCursor.close();
        // at this point the needed data on the old question and old answer tables has been copied to the new STring table.
        drop = "DROP TABLE IF EXISTS " + Contract.Old_Question_DBV6_AppV32.TABLE_NAME + ";";
        toImportDatabase.execSQL(drop);
        drop = "DROP TABLE IF EXISTS " + Contract.Old_Answer_DBV6_AppV32.TABLE_NAME + ";";
        toImportDatabase.execSQL(drop);
        /////////////////////////////////
        copy = "INSERT INTO " + Contract.String_Entry.TABLE_NAME + " ( " + Contract.String_Entry.COLUMN_NAME_QUESTION
                + " , " + Contract.String_Entry.COLUMN_NAME_ANSWER
                + " , " + Contract.String_Entry.COLUMN_NAME_FIELD
                + " , " + Contract.CREATION_DATE
                + " ) "
                + "SELECT " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME
                + ";";
        toImportDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + ";";
        toImportDatabase.execSQL(drop);
        toImportDatabase.setVersion(7);
    }
}
