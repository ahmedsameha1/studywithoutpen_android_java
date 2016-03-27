package com.ahmedsameha1.studyvocabularyfive.databasetemp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.ExampleFields;
import com.ahmedsameha1.studyvocabularyfive.R;

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

    public DatabaseOperations(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

    }

    public Cursor getFieldList() {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        String[] string = {"rowid _id", Contract.Field.COLUMN_NAME_FIELD_NAME, Contract.Field.COLUMN_NAME_FIELD_TYPE};
        Cursor cursor = contentResolver.query(uri, string, null, null, null);
        return cursor;
    }


    public void createNewField(String fieldName, int fieldType) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME, fieldName);
        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE, fieldType);
        contentResolver.insert(uri, contentValues);
        /*String tableQuestion = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_QUESTION_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + Contract.OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableAnswer = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_ANSWER_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + Contract.OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableLink = "CREATE TABLE " + fieldName + Contract.OldField.TABLE_NAME_LINK_SUFFIX + " "
                + " ( " + Contract.OldField._ID + " INTEGER , " + fieldName + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " "
                + " TEXT " + ","
                + " " + fieldName + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " "
                + " TEXT " + ", "
                + Contract.OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
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
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        String[] string = {"rowid _id", Contract.Field.COLUMN_NAME_FIELD_NAME};
        String selection = Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ";
        Cursor cursor = contentResolver.query(uri, string, selection, new String[]{fieldName}, null);
        boolean temp = cursor.getCount() == 0 ? false : true;

        return temp;
    }

    public void insertQuestion(String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, question);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void insertAnswer(String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY, answer);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void insertQuestionAnswerLink(String fieldName, String question, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, question);
            contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, answer);
            contentValues.put(Contract.Linker.COLUMN_NAME_FIELD_LINK, fieldName);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void updateQuestionAnswerLink(String fieldName, String currentQuestion, String newQuestion,
                                         String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{newQuestion, newAnswer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, newQuestion);
            contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, newAnswer);
            long i = contentResolver.update(uri,
                    contentValues,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
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
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
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
                    new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[]{question, fieldName},
                    null);
            if (cursor.getCount() <= 0) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                        new String[]{question});
                /*Cursor cursor1 = contentResolver.query(uri,
                        new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                        new String[] { question},
                        null,
                        null);
                if ( cursor1.getCount() <= 1 ) {
                    Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
                    contentResolver.delete(uri1,
                            Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[] {question});
                }*/
            } else {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[]{question, fieldName});
            }
        }
        for (String answer : answers) {
            cursor = contentResolver.query(uri,
                    new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[]{answer, fieldName},
                    null);
            if (cursor.getCount() <= 0) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                        new String[]{answer});
                /*Cursor cursor1 = contentResolver.query(uri,
                        new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                        new String[] { question},
                        null,
                        null);
                if ( cursor1.getCount() <= 1 ) {
                    Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
                    contentResolver.delete(uri1,
                            Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                            new String[] {question});
                }*/
            } else {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[]{answer, fieldName});
            }
        }
        uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
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
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName},
                null);
        return cursor;
    }

    private void editQuestion(String currentQuestion, String newQuestion) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[]{newQuestion},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, newQuestion);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        } else {
            contentResolver.delete(uri,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        }
        cursor.close();
    }

    private void editAnswer(String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[]{newAnswer},
                null);
        if (cursor.getCount() <= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY, newAnswer);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        } else {
            contentResolver.delete(uri,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        }
        cursor.close();
    }

    public void deleteUnusedQuestion(String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?  ",
                    new String[]{question});
            Log.d("deleted question", Integer.toString(d));
        }
        cursor.close();
    }

    public void deleteUnusedAnswer(String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?  ",
                    new String[]{answer});
            Log.d("deleted answer", Integer.toString(d));
        }
        cursor.close();
    }

    public boolean editFieldEntry(String fieldName, String currentQuestion, String newQuestion, String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
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
            contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, newQuestion);
            contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, newAnswer);
            long i = contentResolver.update(uri,
                    contentValues,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
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
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
            Cursor cursor = contentResolver.query(uri,
                    new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " ,
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
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
            Cursor cursor = contentResolver.query(uri,
                    new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " ,
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
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{question},
                null);
        if (cursor.getCount() <= count) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?  ",
                    new String[]{question});
            Log.d("deleted question", Integer.toString(d));
        }
        cursor.close();
    }

    public void updateQuestion(String currentQuestion, String newQuestion) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? ",
                new String[]{currentQuestion},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, newQuestion);
            contentResolver.update(uri1,
                    contentValues,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                    new String[]{currentQuestion});
        } else {
            insertQuestion(newQuestion);
        }
    }

    public void updateAnswer(String currentAnswer, String newAnswer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{currentAnswer},
                null);
        if (cursor.getCount() <= 1) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY, newAnswer);
            contentResolver.update(uri1,
                    contentValues,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                    new String[]{currentAnswer});
        } else {
            insertAnswer(newAnswer);
        }
    }

    private void deleteAnswerAfterCount(int count, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? ",
                new String[]{answer},
                null);
        if (cursor.getCount() <= count) {
            Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
            int d = contentResolver.delete(uri1,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?  ",
                    new String[]{answer});
            Log.d("deleted answer", Integer.toString(d));
        }
        cursor.close();
    }

    public void deleteFieldEntry(String question, String answer, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " + " AND " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " + " AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});
        deleteQuestionAfterCount(0, question);
        deleteAnswerAfterCount(0, answer);
    }

    public void deleteOnlyFieldEntry(String fieldName, String question, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " + " AND " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " + " AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});

    }

    public void deleteThisFieldQuestion(String fieldName, String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[]{question, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            contentResolver.delete(uri,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                    new String[]{question});
        } else {
            contentResolver.delete(uri1,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{question, fieldName});
        }
    }

    public void deleteThisFieldAnswer(String fieldName, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[]{answer, fieldName},
                null);
        if (cursor.getCount() <= 0) {
            contentResolver.delete(uri,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                    new String[]{answer});
        } else {
            contentResolver.delete(uri1,
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{answer, fieldName});
        }
    }

    public void deleteThisEntryAndItsRoots(String fieldName, String question, String answer) {
        deleteThisFieldQuestion(fieldName, question);
        deleteThisFieldAnswer(fieldName, answer);
    }

    public Cursor searchFor(String fieldName, String searchString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " LIKE ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"%" + searchString + "%", fieldName, "%" + searchString + "%", fieldName},
                null);
    }

    public Cursor searchExactlyFor(String fieldName, String searchString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return contentResolver.query(uri,
                new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{searchString, fieldName, searchString, fieldName},
                null);
    }

    public void writeToStudyDuringTempTable(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Study_During_Temp.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Study_During_Temp.COLUMN_NAME_QUESTION, listForTest[i][0]);
            contentValues.put(Contract.Study_During_Temp.COLUMN_NAME_ANSWER, listForTest[i][1]);
            contentValues.put(Contract.Study_During_Temp.COLUMN_NAME_FIELD, fieldName);
            contentResolver.insert(uri, contentValues);
        }
    }

    public boolean checkStudyDuringTemp(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Study_During_Temp.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Study_During_Temp.COLUMN_NAME_QUESTION, Contract.Study_During_Temp.COLUMN_NAME_ANSWER},
                Contract.Study_During_Temp.COLUMN_NAME_FIELD + " = ?",
                new String[]{fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void clearStudyDuringTempTable(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Study_During_Temp.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Study_During_Temp.COLUMN_NAME_FIELD + " = ? ",
                new String[]{fieldName});
    }

    public void clearStudyDuringDataFromLinkerTable(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER, -1);
        contentResolver.update(uri, contentValues,
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }

    public void writeDataToLinkerTableStudyDuringColumns(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER, i);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{listForTest[i][0], listForTest[i][1], fieldName});
        }
    }

    public boolean checkLinkerStudyDuring(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER},
                Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER + " >= ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"0", fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void writeTestActivityWrongAnswers(String question, String answer, String fieldName, String wrongAnswersString) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS, wrongAnswersString);
        contentResolver.update(uri,
                contentValues,
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{question, answer, fieldName});
    }

    public void clearLinkerTableWrongAnswers(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS, "");
        contentResolver.update(uri,
                contentValues,
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }

    public void writeLinkerTableTestOrder(String[][] listForTest, String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        for (int i = 0; i < listForTest.length; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Linker.COLUMN_NAME_TEST_ORDER, i);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                            + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{listForTest[i][0], listForTest[i][1], fieldName});
        }
    }

    public boolean checkLinkerTest(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_TEST_ORDER},
                Contract.Linker.COLUMN_NAME_TEST_ORDER + " >= ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"0", fieldName},
                null);
        return cursor.getCount() > 0 ? true : false;
    }

    public void clearLinkerTableTestOrder(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Linker.COLUMN_NAME_TEST_ORDER, -1);
        contentResolver.update(uri,
                contentValues,
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{fieldName});
    }

    public void createExampleField(Context context) {
        if (!checkFieldDuplication(context.getString(R.string.example_field))) {
            createNewField(context.getString(R.string.example_field), Contract.Field.DEFAULT_TYPE);
            ExampleFields exampleFields = new ExampleFields();
            String[][] array = exampleFields.basicExampleField;
            for (int i = 0; i < array.length; i++) {
                insertFieldEntry(context.getString(R.string.example_field), array[i][0], array[i][1]);
            }
        } else {
            Toast.makeText(context, R.string.cannot_create_example_field, Toast.LENGTH_LONG).show();
        }
    }

    public int getFieldType(String fieldName) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Field.COLUMN_NAME_FIELD_TYPE},
                Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                new String[]{fieldName},
                null);
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
            toExportDatabase.execSQL(DatabaseHelper.CREATE_QUESTION_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_ANSWER_TABLE);
            toExportDatabase.execSQL(DatabaseHelper.CREATE_LINKER_TABLE);
            toExportDatabase.execSQL("PRAGMA foreign_keys=ON;");
            for (String tempFieldName : fieldNames) {
                Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
                Cursor fieldCursor = contentResolver.query(uri,
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
}
