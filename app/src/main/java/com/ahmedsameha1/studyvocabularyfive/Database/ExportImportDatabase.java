package com.ahmedsameha1.studyvocabularyfive.Database;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ahmed on 1/5/2016.
 */
public class ExportImportDatabase {
    public static final String EXPORT_IMPORT_DIRECTORY_NAME = "StudyWithoutPen";

    public static void exportFields(Context context, ArrayList<String> fieldNames, String fileName) throws IOException, SQLiteException {
        ContentResolver contentResolver = context.getContentResolver();
        fileName = fileName.toLowerCase();
        SQLiteDatabase toExportDatabase = null;
        try {
            File exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_IMPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }
            File exportedDatabaseFile = new File(exportDirectory, fileName);
            toExportDatabase = SQLiteDatabase.openOrCreateDatabase(exportedDatabaseFile, null);
            toExportDatabase.setVersion(DatabaseHelper.DATABASE_VERSION);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_QUESTION_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_ANSWER_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_LINKER_TABLE);
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Uri uriFieldTable = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
                Cursor fieldCursor = contentResolver.query(uriFieldTable,
                        null,
                        Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                        new String[]{tempFieldName},
                        null);
                fieldCursor.moveToFirst();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME, fieldCursor.getString(0));
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE, fieldCursor.getInt(1));
                contentValues.put(Contract.COLUMN_NAME_CREATED_AT, fieldCursor.getString(2));
                toExportDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
                fieldCursor.close();
                ///////////////////////////////////////////////////////////
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                Cursor linkerCursor = contentResolver.query(uri1, null, Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ?",
                        new String[]{tempFieldName}, null);
                for (linkerCursor.moveToFirst(); linkerCursor.getPosition() < linkerCursor.getCount();
                     linkerCursor.moveToNext()) {
                    Uri uri2 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
                    Cursor questionCursor = contentResolver.query(uri2,
                            null,
                            Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                            new String[]{linkerCursor.getString(0)},
                            null);
                    Cursor checkQuestionCursor = toExportDatabase.query(Contract.Question.TABLE_NAME,
                            new String[]{Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                            Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[]{linkerCursor.getString(0)},
                            null, null, null);
                    if (checkQuestionCursor.getCount() <= 0) {
                        questionCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, questionCursor.getString(0));
                        contentValues1.put(Contract.COLUMN_NAME_CREATED_AT, questionCursor.getString(1));
                        toExportDatabase.insert(Contract.Question.TABLE_NAME, null, contentValues1);
                    }
                    questionCursor.close();
                    checkQuestionCursor.close();
                    ///////////////////////////////////////////////////////////
                    uri2 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
                    Cursor answerCursor = contentResolver.query(uri2,
                            null,
                            Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                            new String[]{linkerCursor.getString(1)},
                            null);
                    Cursor checkAnswerCursor = toExportDatabase.query(Contract.Answer.TABLE_NAME,
                            new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                            Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                            new String[]{linkerCursor.getString(1)},
                            null, null, null);
                    if (checkAnswerCursor.getCount() <= 0) {
                        answerCursor.moveToFirst();
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY, answerCursor.getString(0));
                        contentValues1.put(Contract.COLUMN_NAME_CREATED_AT, answerCursor.getString(1));
                        toExportDatabase.insert(Contract.Answer.TABLE_NAME, null, contentValues1);
                    }
                    answerCursor.close();
                    checkAnswerCursor.close();
                    ///////////////////////////////////////////////////////////
                    contentValues = new ContentValues();
                    contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, linkerCursor.getString(0));
                    contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, linkerCursor.getString(1));
                    contentValues.put(Contract.Linker.COLUMN_NAME_FIELD_LINK, linkerCursor.getString(2));
                    contentValues.put(Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER, linkerCursor.getInt(3));
                    contentValues.put(Contract.Linker.COLUMN_NAME_TEST_ORDER, linkerCursor.getInt(4));
                    contentValues.put(Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS, linkerCursor.getString(5));
                    contentValues.put(Contract.COLUMN_NAME_CREATED_AT, linkerCursor.getString(6));
                    toExportDatabase.insert(Contract.Linker.TABLE_NAME, null, contentValues);
                }
                linkerCursor.close();
            }
            toExportDatabase.execSQL("CREATE TABLE " + Contract.Check.TABLE_NAME + " ( " + Contract.Check.COLUMN_NAME_CHECK_NUMBER
                    + " INTEGER PRIMARY KEY NOT NULL );");
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Check.COLUMN_NAME_CHECK_NUMBER, Contract.Check.CHECK_NUMBER);
            toExportDatabase.insert(Contract.Check.TABLE_NAME, null, contentValues);
        } finally {
            if (toExportDatabase != null) {
                toExportDatabase.close();
            }
        }
    }

    public static void importFields(Context context, String fileName)
            throws IOException, SQLiteException, InvalidStudyWithoutPenFileException {
        fileName = fileName.toLowerCase();
        SQLiteDatabase toImportDatabase = null;
        try {
            toImportDatabase = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory().getPath()
                    + File.separator + EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName, null, SQLiteDatabase.OPEN_READONLY);
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
                importFieldsV6(context, toImportDatabase);
            }
        } finally {
            if (toImportDatabase != null) {
                toImportDatabase.close();
            }
        }
    }

    private static void importFieldsV6(Context context, SQLiteDatabase toImportDatabase)
            throws IOException, SQLiteException, InvalidStudyWithoutPenFileException {
        ArrayList<String> notCopiedFields = new ArrayList<String>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor toImportFieldCursor = toImportDatabase.query(Contract.Field.TABLE_NAME,null, null, null, null, null, null);
        for (toImportFieldCursor.moveToFirst(); toImportFieldCursor.getPosition() < toImportFieldCursor.getCount();
             toImportFieldCursor.moveToNext()) {
            Uri uriFieldTable = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
            Cursor appFieldCursor = contentResolver.query(uriFieldTable, null,
                    Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                    new String [] {toImportFieldCursor.getString(0)},
                    null);
            if ( appFieldCursor.getCount() <= 0 ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME,toImportFieldCursor.getString(0));
                contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE,toImportFieldCursor.getInt(1));
                contentValues.put(Contract.COLUMN_NAME_CREATED_AT,toImportFieldCursor.getString(2));
                contentResolver.insert(uriFieldTable,contentValues);
            } else {
                appFieldCursor.moveToFirst();
                if ( toImportFieldCursor.getInt(1) != appFieldCursor.getInt(1) ) {
                    notCopiedFields.add(toImportFieldCursor.getString(0));
                    String contentText = context.getString(R.string.the_field) + " " + toImportFieldCursor.getString(0) +
                            context.getString(R.string.not_imported_because);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher512)
                            .setContentTitle(context.getResources().getString(R.string.not_imported_field))
                            .setContentText(contentText)
                            .setTicker(context.getResources().getString(R.string.not_imported_field))
                            .setAutoCancel(true);
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, builder.build());
                }
            }
            appFieldCursor.close();
        }
        toImportFieldCursor.close();
        Uri questionTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Cursor toImportQuestionCursor = toImportDatabase.query(Contract.Question.TABLE_NAME, null, null, null, null, null, null);
        Cursor appQuestionCursor;
        for (toImportQuestionCursor.moveToFirst(); toImportQuestionCursor.getPosition() < toImportQuestionCursor.getCount();
             toImportQuestionCursor.moveToNext()) {
            appQuestionCursor = contentResolver.query(questionTableUri,
                    new String[] {Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[] {toImportQuestionCursor.getString(0)},
                    null);
            if (appQuestionCursor.getCount() <= 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, toImportQuestionCursor.getString(0));
                contentValues.put(Contract.COLUMN_NAME_CREATED_AT,toImportQuestionCursor.getString(1));
                contentResolver.insert(questionTableUri,contentValues);
            }
            appQuestionCursor.close();
        }
        toImportQuestionCursor.close();
        Uri answerTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Cursor toImportAnswerCursor = toImportDatabase.query(Contract.Answer.TABLE_NAME, null, null, null, null, null, null);
        Cursor appAnswerCursor;
        for (toImportAnswerCursor.moveToFirst(); toImportAnswerCursor.getPosition() < toImportAnswerCursor.getCount();
             toImportAnswerCursor.moveToNext()) {
            appAnswerCursor = contentResolver.query(answerTableUri,
                    new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[] {toImportAnswerCursor.getString(0)},
                    null);
            if ( appAnswerCursor.getCount() <= 0 ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY,toImportAnswerCursor.getString(0));
                contentValues.put(Contract.COLUMN_NAME_CREATED_AT,toImportAnswerCursor.getString(1));
                contentResolver.insert(answerTableUri,contentValues);
            }
            appAnswerCursor.close();
        }
        toImportAnswerCursor.close();
        String [] notCopiedArray = notCopiedFields.size() <= 0 ? new String[] {""} : (String []) notCopiedFields.toArray();
        String placeholders = "( ";
        if ( notCopiedArray.length <= 1 ) {
            placeholders += "? )";
        } else {
            for ( int i = 0; i < notCopiedArray.length; i++ ) {
                if ( i < notCopiedArray.length - 1 ) {
                    placeholders += "?, ";
                } else {
                    placeholders += "? )";
                }
            }
        }
        Uri linkerTableUri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor toImportLinkerCursor = toImportDatabase.query(Contract.Linker.TABLE_NAME, null,
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " NOT IN " + placeholders,
                notCopiedArray, null,null,null);
        for (toImportLinkerCursor.moveToFirst(); toImportLinkerCursor.getPosition() < toImportLinkerCursor.getCount();
             toImportLinkerCursor.moveToNext()) {
            Cursor appLinkerCursor = contentResolver.query(linkerTableUri,
                    new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK,
                            Contract.Linker.COLUMN_NAME_FIELD_LINK},
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " +
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ?",
                    new String[]{toImportLinkerCursor.getString(0), toImportLinkerCursor.getString(1), toImportLinkerCursor.getString(2)},
                    null);
            if ( appLinkerCursor.getCount() <= 0 ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, toImportLinkerCursor.getString(0));
                contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, toImportLinkerCursor.getString(1));
                contentValues.put(Contract.Linker.COLUMN_NAME_FIELD_LINK, toImportLinkerCursor.getString(2));
                contentValues.put(Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER, toImportLinkerCursor.getInt(3));
                contentValues.put(Contract.Linker.COLUMN_NAME_TEST_ORDER, toImportLinkerCursor.getInt(4));
                contentValues.put(Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS, toImportLinkerCursor.getString(5));
                contentValues.put(Contract.COLUMN_NAME_CREATED_AT, toImportLinkerCursor.getString(6));
                contentResolver.insert(linkerTableUri,contentValues);
            }
            appLinkerCursor.close();
        }
        toImportLinkerCursor.close();
        Cursor questionsCursor = contentResolver.query(questionTableUri,new String[]{Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                null,null,null);
        for (questionsCursor.moveToFirst(); questionsCursor.getPosition() < questionsCursor.getCount();
             questionsCursor.moveToNext()) {
            Cursor unneededQuestionCursor = contentResolver.query(linkerTableUri,new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                    new String[] {questionsCursor.getString(0)},null);
            if ( unneededQuestionCursor.getCount() <= 0 ) {
                contentResolver.delete(questionTableUri, Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                        new String[] {questionsCursor.getString(0)});
            }
            unneededQuestionCursor.close();
        }
        questionsCursor.close();
        Cursor answersCursor = contentResolver.query(answerTableUri,new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                null,null,null);
        for (answersCursor.moveToFirst(); answersCursor.getPosition() < answersCursor.getCount();
             answersCursor.moveToNext()) {
            Cursor unneededAnswerCursor = contentResolver.query(linkerTableUri,new String[] {Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? ",
                    new String[] {answersCursor.getString(0)},null);
            if ( unneededAnswerCursor.getCount() <= 0 ) {
                contentResolver.delete(answerTableUri, Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                        new String[] {answersCursor.getString(0)});
            }
            unneededAnswerCursor.close();
        }
        answersCursor.close();
    }
}
