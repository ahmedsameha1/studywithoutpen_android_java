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
    private static ArrayList<String> notImportedFields;

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

    public static void startImportThread(final Context context, final String fileName) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        importThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                importFields(context, fileName);
            }
        });
        importThread.start();
    }


    private static void exportFields(Context context, ArrayList<String> fieldNames, String fileName) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ContentResolver contentResolver = context.getContentResolver();
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
            toExportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_QUESTION_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_ANSWER_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_LINKER_TABLE);
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Uri uriFieldTable = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
                Cursor fieldCursor = contentResolver.query(uriFieldTable,
                        null,
                        Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME + " = ? ",
                        new String[]{tempFieldName},
                        null);
                fieldCursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME, fieldCursor.getString(0));
                contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE, fieldCursor.getInt(1));
                contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, fieldCursor.getString(2));
                toExportDatabase.insert(Contract.Old_Field_DBV6_AppV32.TABLE_NAME, null, contentValues);
                fieldCursor.close();
                ///////////////////////////////////////////////////////////
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                Cursor linkerCursor = contentResolver.query(uri1, null, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ?",
                        new String[]{tempFieldName}, null);
                for (linkerCursor.moveToFirst(); linkerCursor.getPosition() < linkerCursor.getCount();
                     linkerCursor.moveToNext()) {
                    Uri uri2 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
                    Cursor questionCursor = contentResolver.query(uri2,
                            null,
                            Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                            new String[]{linkerCursor.getString(0)},
                            null);
                    Cursor checkQuestionCursor = toExportDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME,
                            new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                            Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[]{linkerCursor.getString(0)},
                            null, null, null);
                    if (checkQuestionCursor.getCount() <= 0) {
                        questionCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY, questionCursor.getString(0));
                        contentValues1.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, questionCursor.getString(1));
                        toExportDatabase.insert(Contract.Old_Question_DBV6_AppV32.TABLE_NAME, null, contentValues1);
                    }
                    questionCursor.close();
                    checkQuestionCursor.close();
                    ///////////////////////////////////////////////////////////
                    uri2 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
                    Cursor answerCursor = contentResolver.query(uri2,
                            null,
                            Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                            new String[]{linkerCursor.getString(1)},
                            null);
                    Cursor checkAnswerCursor = toExportDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME,
                            new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                            Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                            new String[]{linkerCursor.getString(1)},
                            null, null, null);
                    if (checkAnswerCursor.getCount() <= 0) {
                        answerCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY, answerCursor.getString(0));
                        contentValues1.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, answerCursor.getString(1));
                        toExportDatabase.insert(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME, null, contentValues1);
                    }
                    answerCursor.close();
                    checkAnswerCursor.close();
                    ///////////////////////////////////////////////////////////
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, linkerCursor.getString(0));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK, linkerCursor.getString(1));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK, linkerCursor.getString(2));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER, linkerCursor.getInt(3));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER, linkerCursor.getInt(4));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS, linkerCursor.getString(5));
                    contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, linkerCursor.getString(6));
                    toExportDatabase.insert(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, null, contentValues);
                }
                linkerCursor.close();
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
            }
            String journalFileName = fileName + "-journal";
            File journalFile = new File(exportDirectory, journalFileName);
            if (journalFile.exists()) {
                journalFile.delete();
            }
        }
    }

    private static void importFields(Context context, String fileName) {
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
                importFieldsDBV6(context, toImportDatabase);
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

    private static void importFieldsDBV6(Context context, SQLiteDatabase toImportDatabase)
            throws IOException, SQLiteException, InvalidStudyWithoutPenFileException {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notImportedFields = new ArrayList<String>();
        final ContentResolver contentResolver = context.getContentResolver();
        DatabaseInstance.initializeInstance(new DatabaseHelper(context));
        SQLiteDatabase appDatabase = DatabaseInstance.getInstance().openDatabase();
        appDatabase.beginTransactionWithListenerNonExclusive(new SQLiteTransactionListener() {
            @Override
            public void onBegin() {

            }

            @Override
            public void onCommit() {
                contentResolver.notifyChange(DataProvider.CONTENT_URI, null);
            }

            @Override
            public void onRollback() {

            }
        });
        try {
            Cursor toImportFieldCursor = toImportDatabase.query(Contract.Old_Field_DBV6_AppV32.TABLE_NAME, null, null, null, null, null, null);
            for (toImportFieldCursor.moveToFirst(); toImportFieldCursor.getPosition() < toImportFieldCursor.getCount();
                 toImportFieldCursor.moveToNext()) {
                Uri uriFieldTable = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
                Cursor appFieldCursor = contentResolver.query(uriFieldTable, null,
                        Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME + " = ? ",
                        new String[]{toImportFieldCursor.getString(0)},
                        null);
                if (appFieldCursor.getCount() <= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME, toImportFieldCursor.getString(0));
                    contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE, toImportFieldCursor.getInt(1));
                    contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, toImportFieldCursor.getString(2));
//                    contentResolver.insert(uriFieldTable, contentValues);
                    appDatabase.insert(Contract.Old_Field_DBV6_AppV32.TABLE_NAME, null, contentValues);
                } else {
                    appFieldCursor.moveToFirst();
                    if (toImportFieldCursor.getInt(1) != appFieldCursor.getInt(1)) {
                        notImportedFields.add(toImportFieldCursor.getString(0));
                    }
                }
                appFieldCursor.close();
            }
            toImportFieldCursor.close();
            Uri questionTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
            Cursor toImportQuestionCursor = toImportDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME, null, null, null, null, null, null);
            Cursor appQuestionCursor;
            for (toImportQuestionCursor.moveToFirst(); toImportQuestionCursor.getPosition() < toImportQuestionCursor.getCount();
                 toImportQuestionCursor.moveToNext()) {
                appQuestionCursor = contentResolver.query(questionTableUri,
                        new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                        Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                        new String[]{toImportQuestionCursor.getString(0)},
                        null);
                if (appQuestionCursor.getCount() <= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY, toImportQuestionCursor.getString(0));
                    contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, toImportQuestionCursor.getString(1));
//                    contentResolver.insert(questionTableUri, contentValues);
                    appDatabase.insert(Contract.Old_Question_DBV6_AppV32.TABLE_NAME, null, contentValues);
                }
                appQuestionCursor.close();
            }
            toImportQuestionCursor.close();
            Uri answerTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
            Cursor toImportAnswerCursor = toImportDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME, null, null, null, null, null, null);
            Cursor appAnswerCursor;
            for (toImportAnswerCursor.moveToFirst(); toImportAnswerCursor.getPosition() < toImportAnswerCursor.getCount();
                 toImportAnswerCursor.moveToNext()) {
                appAnswerCursor = contentResolver.query(answerTableUri,
                        new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                        Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                        new String[]{toImportAnswerCursor.getString(0)},
                        null);
                if (appAnswerCursor.getCount() <= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY, toImportAnswerCursor.getString(0));
                    contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, toImportAnswerCursor.getString(1));
//                    contentResolver.insert(answerTableUri, contentValues);
                    appDatabase.insert(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME, null, contentValues);
                }
                appAnswerCursor.close();
            }
            toImportAnswerCursor.close();
            String[] notImportedArray = notImportedFields.size() <= 0 ? new String[]{""} : notImportedFields.toArray(new String[notImportedFields.size()]);
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
            Uri linkerTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            Cursor toImportLinkerCursor = toImportDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, null,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " NOT IN " + placeholders,
                    notImportedArray, null, null, null);
            for (toImportLinkerCursor.moveToFirst(); toImportLinkerCursor.getPosition() < toImportLinkerCursor.getCount();
                 toImportLinkerCursor.moveToNext()) {
                Cursor appLinkerCursor = contentResolver.query(linkerTableUri,
                        new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK,
                                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK},
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " +
                                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ?",
                        new String[]{toImportLinkerCursor.getString(0), toImportLinkerCursor.getString(1), toImportLinkerCursor.getString(2)},
                        null);
                if (appLinkerCursor.getCount() <= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, toImportLinkerCursor.getString(0));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK, toImportLinkerCursor.getString(1));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK, toImportLinkerCursor.getString(2));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER, toImportLinkerCursor.getInt(3));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER, toImportLinkerCursor.getInt(4));
                    contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS, toImportLinkerCursor.getString(5));
                    contentValues.put(Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32, toImportLinkerCursor.getString(6));
//                    contentResolver.insert(linkerTableUri, contentValues);
                    appDatabase.insert(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, null, contentValues);
                }
                appLinkerCursor.close();
            }
            toImportLinkerCursor.close();
/*            Cursor questionsCursor = contentResolver.query(questionTableUri, new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                    null, null, null);*/
            Cursor questionsCursor = toImportDatabase.query(Contract.Old_Question_DBV6_AppV32.TABLE_NAME,new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                    null, null, null,null,null);
            for (questionsCursor.moveToFirst(); questionsCursor.getPosition() < questionsCursor.getCount();
                 questionsCursor.moveToNext()) {
                Cursor unneededQuestionCursor = contentResolver.query(linkerTableUri, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                        new String[]{questionsCursor.getString(0)}, null);
                if (unneededQuestionCursor.getCount() <= 0) {
          /*          contentResolver.delete(questionTableUri, Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                            new String[]{questionsCursor.getString(0)});*/
                    appDatabase.delete(Contract.Old_Question_DBV6_AppV32.TABLE_NAME, Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                            new String[]{questionsCursor.getString(0)});
                }
                unneededQuestionCursor.close();
            }
            questionsCursor.close();
            /*Cursor answersCursor = contentResolver.query(answerTableUri, new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                    null, null, null);*/
            Cursor answersCursor = toImportDatabase.query(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME,new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                    null, null, null, null, null);
            for (answersCursor.moveToFirst(); answersCursor.getPosition() < answersCursor.getCount();
                 answersCursor.moveToNext()) {
                Cursor unneededAnswerCursor = contentResolver.query(linkerTableUri, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? ",
                        new String[]{answersCursor.getString(0)}, null);
                if (unneededAnswerCursor.getCount() <= 0) {
/*                    contentResolver.delete(answerTableUri, Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                            new String[]{answersCursor.getString(0)});*/
                    appDatabase.delete(Contract.Old_Answer_DBV6_AppV32.TABLE_NAME, Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                            new String[]{answersCursor.getString(0)});
                }
                unneededAnswerCursor.close();
            }
            answersCursor.close();
            appDatabase.setTransactionSuccessful();
            if ( notImportedFields.size() > 0 ) {
                showNotification(context,NOT_IMPORTED_FIELDS,notificationManager);
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
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, notImportedFields);
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
}
