package com.ahmedsameha1.studyvocabularyfive;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ahmed_000 on 8/31/2015.
 */
public class DatabaseOperations {
    private ContentResolver contentResolver;

    public DatabaseOperations( ContentResolver contentResolver ) {
        this.contentResolver = contentResolver;

    }
    public Cursor getFieldList() {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        String [] string = {"rowid _id", Contract.Field.COLUMN_NAME_FIELD_NAME};
        Cursor cursor = contentResolver.query(uri, string,null,null,null);
        return cursor;
    }


    public void CreateNewField( String fieldName ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME,fieldName);
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
        String [] string = {"rowid _id", Contract.Field.COLUMN_NAME_FIELD_NAME};
        String selection = Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ";
        Cursor cursor = contentResolver.query(uri, string, selection, new String [] {fieldName}, null);
        boolean temp = cursor.getCount() == 0 ? false : true;

        return temp;
    }
    public void insertQuestion ( String question ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Cursor cursor = contentResolver.query( uri,
                new String[] {Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[] { question },
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY,question);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }
    public void insertAnswer ( String answer ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Cursor cursor = contentResolver.query( uri,
                new String[] {Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[] { answer },
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY,answer);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }

    public void insertQuestionAnswerLink ( String fieldName, String question, String answer ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query( uri,
                new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] { question, answer ,fieldName },
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Linker.COLUMN_NAME_QUESTION_LINK, question);
            contentValues.put(Contract.Linker.COLUMN_NAME_ANSWER_LINK, answer);
            contentValues.put(Contract.Linker.COLUMN_NAME_FIELD_LINK,fieldName);
            contentResolver.insert(uri, contentValues);
        }
        cursor.close();
    }
    public void insertFieldEntry(String fieldName, String question, String answer) {
        insertQuestion(question);
        insertAnswer( answer);
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
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            questions.add(cursor.getString(0));
            answers.add(cursor.getString(1));
        }
        cursor.close();
        for ( String question : questions ) {
            cursor = contentResolver.query(uri,
                    new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[] { question, fieldName},
                    null);
            if ( cursor.getCount() <= 0 ) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                        new String[] {question});
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
                        new String[] {question, fieldName});
            }
        }
        for ( String answer : answers ) {
            cursor = contentResolver.query(uri,
                    new String[] {Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                    new String[] { answer, fieldName},
                    null);
            if ( cursor.getCount() <= 0 ) {
                Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
                contentResolver.delete(uri1,
                        Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                        new String[] {answer});
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
                        new String[] {answer, fieldName});
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
                new String[] {"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {fieldName},
                null);
        return cursor;
    }
    private void editQuestion( String currentQuestion, String newQuestion ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Question.COLUMN_NAME_QUESTION_ENTRY},
                Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                new String[]{newQuestion},
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Question.COLUMN_NAME_QUESTION_ENTRY, newQuestion);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        }
        else {
            contentResolver.delete(uri,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ?",
                    new String[]{currentQuestion});
        }
        cursor.close();
    }
    private void editAnswer( String currentAnswer, String newAnswer ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Answer.COLUMN_NAME_ANSWER_ENTRY},
                Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                new String[]{newAnswer},
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Answer.COLUMN_NAME_ANSWER_ENTRY, newAnswer);
            contentResolver.update(uri,
                    contentValues,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        }
        else {
            contentResolver.delete(uri,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ?",
                    new String[]{currentAnswer});
        }
        cursor.close();
    }
    public void deleteUnusedQuestion(String question ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " ,
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
    public void deleteUnusedAnswer(String answer ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " ,
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
    public void editFieldEntry(String fieldName,String currentQuestion,String newQuestion,String currentAnswer,String newAnswer) {
        if ( !currentQuestion.equals(newQuestion) ) {
                insertQuestion(newQuestion);
                deleteQuestionAfterCount(1, currentQuestion);
//                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
        }
        if ( !currentAnswer.equals(newAnswer) ) {
                insertAnswer(newAnswer);
                deleteAnswerAfterCount(1, currentAnswer);
//                deleteOnlyFieldEntry(fieldName, currentQuestion, currentAnswer);
        }
        if ( !currentQuestion.equals(newQuestion) && !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName, newQuestion, newAnswer);
        }
        else if ( !currentQuestion.equals(newQuestion) ) {
            insertQuestionAnswerLink(fieldName,newQuestion,currentAnswer);
        }
        else if ( !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName,currentQuestion,newAnswer);
        }
        deleteFieldEntry(currentQuestion, currentAnswer, fieldName);


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
    private void deleteQuestionAfterCount(int count, String question ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " ,
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
    private void deleteAnswerAfterCount( int count, String answer ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " ,
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
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " +  " AND " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " +  " AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {question,answer,fieldName});
        deleteQuestionAfterCount(0,question);
        deleteAnswerAfterCount(0,answer);
    }

    public void deleteOnlyFieldEntry(String fieldName, String question, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        contentResolver.delete(uri,
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? " +  " AND " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? " +  " AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {question,answer,fieldName});

    }
    public void deleteThisFieldQuestion(String fieldName, String question) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Question.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[] {question, fieldName},
                null);
        if ( cursor.getCount() <= 0 ) {
            contentResolver.delete(uri,
                    Contract.Question.COLUMN_NAME_QUESTION_ENTRY + " = ? ",
                    new String[] {question});
        } else  {
            contentResolver.delete(uri1,
                    Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[] {question,fieldName});
        }
    }
    public void deleteThisFieldAnswer(String fieldName, String answer) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Answer.TABLE_NAME);
        Uri uri1 = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        Cursor cursor = contentResolver.query(uri1,
                new String[]{Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " != ? ",
                new String[] {answer, fieldName},
                null);
        if ( cursor.getCount() <= 0 ) {
            contentResolver.delete(uri,
                    Contract.Answer.COLUMN_NAME_ANSWER_ENTRY + " = ? ",
                    new String[] {answer});
        } else  {
            contentResolver.delete(uri1,
                    Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[] {answer,fieldName});
        }
    }

    public void deleteThisEntryAndItsRoots(String fieldName, String question, String answer ) {
        deleteThisFieldQuestion(fieldName,question);
        deleteThisFieldAnswer(fieldName,answer);
    }

    public Cursor searchFor ( String fieldName, String searchString ) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return contentResolver.query(uri,
                new String[]{ "rowid _id" , Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK + " LIKE ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {searchString + "%",fieldName,searchString + "%",fieldName},
                null);
    }
}
