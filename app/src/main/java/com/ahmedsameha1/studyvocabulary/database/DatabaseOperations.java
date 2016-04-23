package com.ahmedsameha1.studyvocabulary.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by ahmed_000 on 8/31/2015.
 */
public class DatabaseOperations {
    public static final String EXPORT_DIRECTORY_NAME = "StudyWithoutPen";
    private ContentResolver contentResolver;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseOperations(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

    }
    public DatabaseOperations(Context context) {
        this.context = context;
        sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
    }

    public Cursor getFieldList() {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
        String[] string = {"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME, Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE};
        Cursor cursor = contentResolver.query(uri, string, null, null, null);
        return cursor;
    }
    public boolean checkEntryDuplication (String question,String answer,long field_id ,int question_data_type) {
        boolean thereIsduplication;
        long question_id,answer_id;
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE) {
            Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                    Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                    new String[]{question},null,null,null);
            if ( questionCursor.moveToFirst() ) {
                question_id = questionCursor.getLong(0);
            } else {
                return false;
            }
            Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                    Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                    new String[]{answer},null,null,null);
            if ( answerCursor.moveToFirst() ) {
                answer_id = answerCursor.getLong(0);
            } else {
                return false;
            }
            Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,null,
                    Contract.String_Entry.COLUMN_NAME_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_NAME_ANSWER + " = ? AND "
            + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{question_id + "",answer_id + "", field_id + ""},null,null,null);
            thereIsduplication = cursor.getCount() > 0;
            cursor.close();
            return thereIsduplication;
        } else if ( question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
        return true;
    }
    public String getFieldName( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                Contract.COLUMN_NAME_ID + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        String field_name = cursor.getString(0);
        cursor.close();
        return field_name;
    }
    public void incrementFieldUsage ( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_USAGE_COUNT},
                Contract.COLUMN_NAME_ID + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_NAME_USAGE_COUNT,cursor.getInt(0) + 1);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME,contentValues, Contract.COLUMN_NAME_ID + " = ? ", new String[]{field_id + ""});
        cursor.close();
    }
    public int getQuestionDataType( long field_id) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_ENTRY_QUESTION_COLUMN_DATA_TYPE},
                Contract.COLUMN_NAME_ID + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        int question_data_type = cursor.getInt(0);
        cursor.close();
        return question_data_type;
    }
    public boolean checkIfIncludedInPausedOperations (long field_id, long entry_id){
        Cursor cursor = null;
        if ( getQuestionDataType(field_id) == Contract.Field.STRING_DATA_TYPE ) {
            cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER},
                    Contract.COLUMN_NAME_ID + " = ? AND ( " + Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER + " >= ? OR "
            + Contract.String_Entry.COLUMN_NAME_PAUSED_STUDY_PERIOD_ORDER + " >= ? )",
                    new String[]{entry_id + "" , "0", "0"},null,null,null);
        } else if (getQuestionDataType(field_id) == Contract.Field.NOT_STRING_DATA_TYPE  ) {}
        if ( cursor == null || cursor.getCount() <= 0 ) {
            return false;
        } else {
            return true;
        }
    }
    public ArrayList<String> getEntryWrongAnswers(String question,String answer, long field_id ,int question_data_type ) {
        Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                new String[]{question}, null, null, null);
        questionCursor.moveToFirst();
        long question_id = questionCursor.getLong(0);
        questionCursor.close();
        Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                new String[]{answer}, null, null, null);
        answerCursor.moveToFirst();
        long answer_id = answerCursor.getLong(0);
        answerCursor.close();
        Cursor entryCursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                Contract.String_Entry.COLUMN_NAME_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_NAME_ANSWER + " = ? AND "
                        + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null);
        entryCursor.moveToFirst();
        long entry_id = entryCursor.getLong(0);
        entryCursor.close();
        ArrayList<String> wrongAnswers = new ArrayList<>();
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE ) {
            Cursor wrong_answers_cursor = sqLiteDatabase.query(Contract.Test_Wrong_Answer_String.TABLE_NAME,new String[]{Contract.Test_Wrong_Answer_String.COLUMN_NAME_WRONG_ANSWER},
                    Contract.Test_Wrong_Answer_String.COLUMN_NAME_ENTRY + " = ? ",
                    new String[]{entry_id + ""},null,null,null);
            if ( wrong_answers_cursor.moveToFirst() ) {
                for ( wrong_answers_cursor.moveToFirst(); wrong_answers_cursor.getPosition() < wrong_answers_cursor.getCount();
                      wrong_answers_cursor.moveToNext() ) {
                    wrongAnswers.add(wrong_answers_cursor.getString(0));
                }
            }
        } else if ( question_data_type == Contract.Field.NOT_STRING_DATA_TYPE ) {
        }
        return wrongAnswers;
    }
    public ArrayList<Integer> getStudyPeriodRepeatedEntries(long field_id) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(Contract.Study_Period_Repeated_Entry.TABLE_NAME,new String[]{Contract.Study_Period_Repeated_Entry.COLUMN_NAME_REPEATED_ENTRY},
                Contract.Study_Period_Repeated_Entry.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""},null,null,null);
        if ( cursor.getCount() <=0 ) {
            cursor.close();
            return arrayList;
        }
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            arrayList.add(cursor.getInt(0));
        }
        cursor.close();
        return arrayList;
    }
    public int[] getDataFromStudyPeriodPaused(long field_id ) {
        int[] array = new int[2];
        Cursor cursor = sqLiteDatabase.query(Contract.Study_Period_Paused.TABLE_NAME,
                new String[]{Contract.Study_Period_Paused.COLUMN_NAME_CURRENT_QUESTION_COUNTER, Contract.Study_Period_Paused.COLUMN_NAME_TRIES_NUMBER},
                Contract.Study_Period_Paused.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        array[0] = cursor.getInt(0);
        array[1] = cursor.getInt(1);
        cursor.close();
        return array;
    }
    public void createNewField(String fieldName, int fieldType) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME, fieldName);
        contentValues.put(Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE, fieldType);
        contentResolver.insert(uri, contentValues);
        /*String tableQuestion = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_QUESTION_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + Contract.OldField.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableAnswer = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_ANSWER_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + Contract.OldField.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableLink = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_LINK_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " "
                + " TEXT " + ","
                + " " + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " "
                + " TEXT " + ", "
                + Contract.OldField.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
                +  " FOREIGN KEY " + " (" + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + Contract.OldField.TABLE_NAME_QUESTION_SUFFIX +
                "(" + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                +  " FOREIGN KEY " + " (" + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + Contract.OldField.TABLE_NAME_ANSWER_SUFFIX +
                "(" + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                + " PRIMARY KEY ( " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX +
                "," + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + ")"
                + ");";
        sqLiteDatabase.execSQL(tableQuestion);
        sqLiteDatabase.execSQL(tableAnswer);
        sqLiteDatabase.execSQL(tableLink);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.OldField.COLUMN_NAME_FIELD_NAME, fieldName);
        sqLiteDatabase.insert(Contract.OldField.TABLE_NAME, null, contentValues);

        Log.d("from APP to Ahmed", "an field has been created");*/
    }

    public boolean checkFieldDuplication(String fieldName) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                new String[]{fieldName},null,null,null);
        boolean temp = cursor.getCount() == 0 ? false : true;
        cursor.close();
        return temp;
    }

    public void insertQuestion(String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY, question);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void insertAnswer(String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY, answer);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void insertQuestionAnswerLink(String fieldName, String question, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, question);
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK, answer);
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK, fieldName);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void updateQuestionAnswerLink(String fieldName, String currentQuestion, String newQuestion,
                                         String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{newQuestion, newAnswer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, newQuestion);
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK, newAnswer);
            long i = contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{currentQuestion, currentAnswer, fieldName});
            Log.d("i", Long.toString(i));
        }
        cursor.close();
    }

    public void insertFieldEntry(String fieldName, String question, String answer) {
        insertQuestion(question);
        insertAnswer(answer);
        insertQuestionAnswerLink(fieldName, question, answer);
    }

    public void deleteField(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName},
                null);
        ArrayList<String> questions = new ArrayList<String>(cursor.getCount());
        ArrayList<String> answers = new ArrayList<String>(cursor.getCount());
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            questions.add(cursor.getString(0));
            answers.add(cursor.getString(1));
        }
        cursor.close();
        for (String question : questions) {
            cursor = contentResolver.query(uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[]{question, fieldName},
                    null);
            if (cursor.getCount() <= 0) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                        new String[]{question});
                /*Cursor cursor1 = contentResolver.query(uri,
                        new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                        new String[] { question},
                        null,
                        null);
                if ( cursor1.getCount() <= 1 ) {
                    Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
                    contentResolver.delete(uri1,
                            Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[] {question});
                }*/
            } else {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[]{question, fieldName});
            }
        }
        for (String answer : answers) {
            cursor = contentResolver.query(uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[]{answer, fieldName},
                    null);
            if (cursor.getCount() <= 0) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                        new String[]{answer});
                /*Cursor cursor1 = contentResolver.query(uri,
                        new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                        new String[] { question},
                        null,
                        null);
                if ( cursor1.getCount() <= 1 ) {
                    Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
                    contentResolver.delete(uri1,
                            Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[] {question});
                }*/
            } else {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[]{answer, fieldName});
            }
        }
        uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME + " = ? ",
                new String[]{fieldName});
        /*String linkTable = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_NAME_LINK_SUFFIX + ";";
        String questionTable = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_NAME_QUESTION_SUFFIX + ";";
        String answerTable = "DROP TABLE IF EXISTS " + fieldName + Contract.OldField.TABLE_NAME_ANSWER_SUFFIX + ";";
        sqLiteDatabase.execSQL(linkTable);
        sqLiteDatabase.execSQL(questionTable);
        sqLiteDatabase.execSQL(answerTable);
        sqLiteDatabase.delete(Contract.OldField.TABLE_NAME, Contract.OldField.COLUMN_NAME_FIELD_NAME + " = '" + fieldName + "';", null);

        Log.d("from APP to Ahmed", "an field has been deleted");*/
    }

    /*public Cursor getFieldEntries(String fieldName) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + fieldName + Contract.OldField.TABLE_NAME_LINK_SUFFIX + "._id As" +
                " _id, " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_SUFFIX + "," +
                fieldName + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX +
                " From " + fieldName + Contract.OldField.TABLE_NAME_QUESTION_SUFFIX +
                " INNER JOIN " + fieldName + Contract.OldField.TABLE_NAME_LINK_SUFFIX + " ON " +
                fieldName + Contract.OldField.COLUMN_NAME_QUESTION_SUFFIX +
                " = " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + "", null);
        return cursor;
    }*/
    public Cursor getFieldEntries(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName},
                null);
        return cursor;
    }

    private void editQuestion(String currentQuestion, String newQuestion) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[]{newQuestion},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY, newQuestion);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        } else {
            contentResolver.delete(uri,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        }
        cursor.close();
    }

    private void editAnswer(String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[]{newAnswer},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY, newAnswer);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        } else {
            contentResolver.delete(uri,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        }
        cursor.close();
    }

    public void deleteUnusedQuestion(String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?  ",
                    new String[]{question});
            Log.d("deleted question", Integer.toString(d));
        }
        cursor.close();
    }

    public void deleteUnusedAnswer(String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?  ",
                    new String[]{answer});
            Log.d("deleted answer", Integer.toString(d));
        }
        cursor.close();
    }

    public boolean editFieldEntry(String fieldName, String currentQuestion, String newQuestion, String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{newQuestion, newAnswer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            if (!currentQuestion.equals(newQuestion)) {
                insertQuestion(newQuestion);

//                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            }
            if (!currentAnswer.equals(newAnswer)) {
                insertAnswer(newAnswer);

//                deleteOnlyFieldEntry(fieldName, currentQuestion, currentAnswer);
            }
//            updateQuestionAnswerLink(fieldName, currentQuestion, newQuestion, currentAnswer, newAnswer);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, newQuestion);
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK, newAnswer);
            long i = contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{currentQuestion, currentAnswer, fieldName});
            Log.d("i", Long.toString(i));
            deleteQuestionAfterCount(0, currentQuestion);
            deleteAnswerAfterCount(0, currentAnswer);
            return true;
        } else {
            return false;
        }
        /*if ( !currentQuestion.equals(newQuestion) && !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName, newQuestion, newAnswer);
        }
        else if ( !currentQuestion.equals(newQuestion) ) {
            insertQuestionAnswerLink(fieldName,newQuestion,currentAnswer);
        }
        else if ( !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName,currentQuestion,newAnswer);
        }
        deleteFieldEntry(currentQuestion, currentAnswer, fieldName);*/


    /*if ( !currentQuestion.equals(newQuestion) ) {
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            Cursor cursor = contentResolver.query(uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? " ,
                    new String[]{currentQuestion},
                    null);
            if (cursor.getCount() > 1) {
                insertQuestion(newQuestion);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editQuestion(currentQuestion,newQuestion);
            }
            cursor.close();
        }
        if ( !currentAnswer.equals(newAnswer) ) {
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            Cursor cursor = contentResolver.query(uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? " ,
                    new String[]{currentAnswer},
                    null);
            if (cursor.getCount() > 1) {
                insertAnswer(newAnswer);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editAnswer(currentAnswer,newAnswer);
            }
            cursor.close();
        }*/
    }

    private void deleteQuestionAfterCount(int count, String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= count) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ?  ",
                    new String[]{question});
            Log.d("deleted question", Integer.toString(d));
        }
        cursor.close();
    }

    public void updateQuestion(String currentQuestion, String newQuestion) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{currentQuestion},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY, newQuestion);
            contentResolver.update(uri1,
                    contentValues,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                    new String[]{currentQuestion});
        } else {
            insertQuestion(newQuestion);
        }
    }

    public void updateAnswer(String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{currentAnswer},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY, newAnswer);
            contentResolver.update(uri1,
                    contentValues,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                    new String[]{currentAnswer});
        } else {
            insertAnswer(newAnswer);
        }
    }

    private void deleteAnswerAfterCount(int count, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= count) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ?  ",
                    new String[]{answer});
            Log.d("deleted answer", Integer.toString(d));
        }
        cursor.close();
    }

    public void deleteFieldEntry(String question, String answer, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? " + " AND " +
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? " + " AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});
        deleteQuestionAfterCount(0, question);
        deleteAnswerAfterCount(0, answer);
    }

    public void deleteOnlyFieldEntry(String fieldName, String question, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? " + " AND " +
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? " + " AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});

    }

    public void deleteThisFieldQuestion(String fieldName, String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Question_DBV6_AppV32.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[]{question, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            contentResolver.delete(uri,
                    Contract.Old_Question_DBV6_AppV32.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                    new String[]{question});
        } else {
            contentResolver.delete(uri1,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{question, fieldName});
        }
    }

    public void deleteThisFieldAnswer(String fieldName, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Answer_DBV6_AppV32.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[]{answer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            contentResolver.delete(uri,
                    Contract.Old_Answer_DBV6_AppV32.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                    new String[]{answer});
        } else {
            contentResolver.delete(uri1,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{answer, fieldName});
        }
    }

    public void deleteThisEntryAndItsRoots(String fieldName, String question, String answer) {
        deleteThisFieldQuestion(fieldName, question);
        deleteThisFieldAnswer(fieldName, answer);
    }

    public Cursor searchFor(String fieldName, String searchString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        return contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " LIKE ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"%" + searchString + "%", fieldName, "%" + searchString + "%", fieldName},
                null);
    }

    public Cursor searchExactlyFor(long field_id, int question_data_type, String searchString) {
        Cursor cursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
            new String[]{searchString},null,null,null);
        cursor.moveToFirst();
        long searchString_id = cursor.getLong(0);
        cursor.close();
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE ) {
            return sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,new String[]{Contract.COLUMN_NAME_ID},
                    Contract.String_Entry.COLUMN_NAME_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                           new String[]{searchString_id + "",field_id + ""},null,null,null);
        } else if ( question_data_type == Contract.Field.NOT_STRING_DATA_TYPE ) {
        }
        return null;
    }
    public Cursor searchExactlyFor(String fieldName, String searchString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        return contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{searchString, fieldName, searchString, fieldName},
                null);
    }

    public void writeToStudyDuringTempTable(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_QUESTION, listForTest[i][0]);
            contentValues.put(Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_ANSWER, listForTest[i][1]);
            contentValues.put(Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_FIELD, fieldName);
            contentResolver.insert(uri, contentValues);
        }
    }

    public boolean checkStudyDuringTemp(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_QUESTION, Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_ANSWER},
                Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_FIELD + " = ?",
                new String[]{fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void clearStudyDuringTempTable(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_NAME_FIELD + " = ? ",
                new String[]{fieldName});
    }

    public void clearStudyDuringDataFromLinkerTable(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER, -1);
        contentResolver.update(uri, contentValues,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }

    public void writeDataToLinkerTableStudyDuringColumns(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER, i);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{listForTest[i][0], listForTest[i][1], fieldName});
        }
    }

    public boolean checkLinkerStudyDuring(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_STUDY_DURING_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"0", fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void writeTestActivityWrongAnswers(String question, String answer, String fieldName, String wrongAnswersString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS, wrongAnswersString);
        contentResolver.update(uri,
                contentValues,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});
    }

    public void clearLinkerTableWrongAnswers(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS, "");
        contentResolver.update(uri,
                contentValues,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }

    public void writeLinkerTableTestOrder(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER, i);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{listForTest[i][0], listForTest[i][1], fieldName});
        }
    }

    public boolean checkLinkerTest(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"0", fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }
    public boolean checkEntriesForAnyTestOrder(long field_id, int question_data_type ) {
        boolean result = false;
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE ) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.String_Entry.TABLE_NAME
            + " WHERE " + Contract.String_Entry.COLUMN_NAME_FIELD + " = ?  AND " + Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER
            + " >= ? ", new String[]{field_id + "", "0"});
            cursor.moveToFirst();
            int number = cursor.getInt(0);
            cursor.close();
            result =  number > 0;
        }
        return result;
    }

    public boolean isThisFieldTestPaused (long field_id ) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.Test_Paused.TABLE_NAME
                        + " WHERE " + Contract.Test_Paused.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""});
        cursor.moveToFirst();
        int number = cursor.getInt(0);
        cursor.close();
        return number == 1;
    }

    public boolean isThisFieldStudyPeriodPaused (long field_id ) {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.Study_Period_Paused.TABLE_NAME
                        + " WHERE " + Contract.Study_Period_Paused.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""});
        cursor.moveToFirst();
        int number = cursor.getInt(0);
        cursor.close();
        return number == 1;
    }

    public void clearLinkerTableTestOrder(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER, -1);
        contentResolver.update(uri,
                contentValues,
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }


    public int getFieldType(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE},
                Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME + " = ? ",
                new String[]{fieldName},
                null);
        cursor.moveToFirst();
        int n = cursor.getInt(0);
        cursor.close();
        return n;
    }
    public int getFieldType ( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_TYPE},
                Contract.COLUMN_NAME_ID + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        int n = cursor.getInt(0);
        cursor.close();
        return n;
    }

    public void exportFields(Context context, ArrayList<String> fieldNames, String fileName) throws IOException, SQLiteException {
        SQLiteDatabase toExportDatabase = context.openOrCreateDatabase(fileName, context.MODE_PRIVATE, null);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            toExportDatabase.setVersion(DatabaseHelper.DATABASE_VERSION);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_FIELD_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_QUESTION_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_ANSWER_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.DBV6_APPV32_CREATE_LINKER_TABLE);
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
                Cursor fieldCursor = contentResolver.query(uri,
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
            File toExportDatabaseFile = context.getDatabasePath(fileName);
            File exportDirectory = null;
            exportDirectory = new File(Environment.getExternalStorageDirectory(), EXPORT_DIRECTORY_NAME);
            if (!exportDirectory.exists()) {
                exportDirectory.mkdir();
            }
            File exportedDatabaseFile = new File(exportDirectory, toExportDatabaseFile.getName());
            exportedDatabaseFile.createNewFile();
            inChannel = new FileInputStream(toExportDatabaseFile).getChannel();
            outChannel = new FileOutputStream(exportedDatabaseFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            toExportDatabase.close();
            context.deleteDatabase(fileName);
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
    public int getEntriesNumber(String fieldName) {
        Cursor cursor = getFieldEntries(fieldName);
        int number = cursor.getCount();
        cursor.close();
        return number;
    }
    public int[] getDataFromTestPaused(long field_id ) {
        int[] array = new int[3];
        Cursor cursor = sqLiteDatabase.query(Contract.Test_Paused.TABLE_NAME,
                new String[]{Contract.Test_Paused.COLUMN_NAME_CURRENT_QUESTION_COUNTER,
                        Contract.Test_Paused.COLUMN_NAME_WRONG_ANSWERS_COUNT, Contract.Test_Paused.COLUMN_NAME_TRIES_NUMBER},
                Contract.Test_Paused.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        array[0] = cursor.getInt(0);
        array[1] = cursor.getInt(1);
        array[2] = cursor.getInt(2);
        cursor.close();
        return array;
    }
    public int getEntriesNumber(long field_id, int question_data_type ) {
        int number = 0;
        if ( question_data_type == Contract.Field.STRING_DATA_TYPE ) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM " + Contract.String_Entry.TABLE_NAME
            + " WHERE " + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
            cursor.moveToFirst();
            number = cursor.getInt(0);
            cursor.close();
        } else if ( question_data_type == Contract.Field.NOT_STRING_DATA_TYPE ) {}

        return number;
    }

}
