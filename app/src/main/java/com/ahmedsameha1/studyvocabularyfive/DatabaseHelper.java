package com.ahmedsameha1.studyvocabularyfive;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ahmedsameha1.studyvocabularyfive.Contract.OldField;
import android.util.Log;

import com.ahmedsameha1.studyvocabularyfive.Contract.Question;
import com.ahmedsameha1.studyvocabularyfive.Contract.Answer;
import com.ahmedsameha1.studyvocabularyfive.Contract.Linker;

import java.util.ArrayList;

/**
 * Created by ahmed on 7/4/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "data";
    /*public static final String CREATE_FIELD_TABLE = "CREATE TABLE " + OldField.TABLE_NAME +
            " ( " + OldField._ID + " INTEGER PRIMARY KEY , "
            +  OldField.COLUMN_NAME_FIELD_NAME + " TEXT , " +
             OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";*/
    public static final String CREATE_FIELD_TABLE = "CREATE TABLE " + Contract.Field.TABLE_NAME
            + " ( "
            + Contract.Field.COLUMN_NAME_FIELD_NAME + " TEXT PRIMARY KEY, "
            + Contract.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";
    
    public static final String CREATE_QUESTION_TABLE = "CREATE TABLE " + Question.TABLE_NAME
            + " ( " + Question.COLUMN_NAME_QUESTION_ENTRY
            + " TEXT PRIMARY KEY NOT NULL, "
            + Contract.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";

    public static final String CREATE_ANSWER_TABLE = "CREATE TABLE " + Answer.TABLE_NAME
            + " ( " + Answer.COLUMN_NAME_ANSWER_ENTRY
            + " TEXT PRIMARY KEY NOT NULL, "
            + Contract.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";

    public static final String CREATE_LINKER_TABLE = "CREATE TABLE " + Linker.TABLE_NAME
            + " ( " + Linker.COLUMN_NAME_QUESTION_LINK + " TEXT , "
            + Linker.COLUMN_NAME_ANSWER_LINK + " TEXT , "
            + Linker.COLUMN_NAME_FIELD_LINK + " TEXT , "
            + Contract.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) ,"
            + " FOREIGN KEY " + " (" + Linker.COLUMN_NAME_QUESTION_LINK + ")" + " REFERENCES "
            + Question.TABLE_NAME + "(" + Question.COLUMN_NAME_QUESTION_ENTRY + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " FOREIGN KEY " + " (" + Linker.COLUMN_NAME_ANSWER_LINK + ")" + " REFERENCES "
            + Answer.TABLE_NAME + "(" + Answer.COLUMN_NAME_ANSWER_ENTRY + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " FOREIGN KEY " + " (" + Linker.COLUMN_NAME_FIELD_LINK + ")" + " REFERENCES "
            + Contract.Field.TABLE_NAME + "(" + Contract.Field.COLUMN_NAME_FIELD_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " PRIMARY KEY ( " + Linker.COLUMN_NAME_QUESTION_LINK + ","
            + Linker.COLUMN_NAME_ANSWER_LINK + ","
            + Linker.COLUMN_NAME_FIELD_LINK + ")" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
        sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);
        sqLiteDatabase.execSQL(CREATE_ANSWER_TABLE);
        sqLiteDatabase.execSQL(CREATE_LINKER_TABLE);
        Log.d("from APP to Ahmed", "table have been written");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("from Peninbin","onUpgrade");
        if ( oldVersion < 2 ) {
            fieldTableMigration(sqLiteDatabase);
            sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);
            sqLiteDatabase.execSQL(CREATE_ANSWER_TABLE);
            sqLiteDatabase.execSQL(CREATE_LINKER_TABLE);
            questionMigration(sqLiteDatabase);
            answerMigration(sqLiteDatabase);
            linkerMigration(sqLiteDatabase);
            tablesDeletion(sqLiteDatabase);
        }
        if ( oldVersion < 3 ) {
            onUpgradeForFieldTableV3(sqLiteDatabase);
            onUpgradeForQuestionTableV3(sqLiteDatabase);
            onUpgradeForAnswerTableV3(sqLiteDatabase);
            onUpgradeForLinkerTableV3(sqLiteDatabase);
        }
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public Cursor getFieldList() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String [] string = {"rowid _id", OldField.COLUMN_NAME_FIELD_NAME};
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string,null,null,null,null,null);
        return cursor;
    }
    public Cursor getFieldList( SQLiteDatabase sqLiteDatabase ) {
        String [] string = {"rowid _id", OldField.COLUMN_NAME_FIELD_NAME};
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string,null,null,null,null,null);
        return cursor;
    }

    public void CreateNewField( String fieldName ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String tableQuestion = "CREATE TABLE " + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableAnswer = "CREATE TABLE " + fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableLink = "CREATE TABLE " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " "
                + " TEXT " + ","
                + " " + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " "
                + " TEXT " + ", "
                + OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
                +  " FOREIGN KEY " + " (" + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX +
                "(" + fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                +  " FOREIGN KEY " + " (" + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX +
                "(" + fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                + " PRIMARY KEY ( " + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX +
                "," + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + ")"
                + ");";
        sqLiteDatabase.execSQL(tableQuestion);
        sqLiteDatabase.execSQL(tableAnswer);
        sqLiteDatabase.execSQL(tableLink);
        ContentValues contentValues = new ContentValues();
        contentValues.put(OldField.COLUMN_NAME_FIELD_NAME, fieldName);
        sqLiteDatabase.insert(OldField.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        Log.d("from APP to Ahmed", "an field has been created");
    }
    public boolean checkFieldDuplication(String fieldName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String [] string = {"rowid _id", OldField.COLUMN_NAME_FIELD_NAME};
        String selectionArgs = OldField.COLUMN_NAME_FIELD_NAME + " = ? ";
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string, selectionArgs, new String [] {fieldName}, null, null, null);
        boolean temp = cursor.getCount() == 0 ? false : true;
        sqLiteDatabase.close();
        return temp;
    }
    public void insertQuestion ( String fieldName, String question ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX },
                fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ? ",
                new String[] { question },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX,question);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertAnswer ( String fieldName, String answer ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX },
                fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ? ",
                new String[] { answer },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX,answer);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertQuestionAnswerLink ( String fieldName, String question, String answer ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_NAME_LINK_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX ,fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX },
                fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " = ? AND " + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " = ?",
                new String[] { question, answer },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX, question);
            contentValues.put(fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX, answer);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_NAME_LINK_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertFieldEntry(String fieldName, String question, String answer) {
        insertQuestion(fieldName, question);
        insertAnswer(fieldName, answer);
        insertQuestionAnswerLink(fieldName, question, answer);
    }
    public void deleteField(String fieldName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String linkTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX + ";";
        String questionTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX + ";";
        String answerTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX + ";";
        sqLiteDatabase.execSQL(linkTable);
        sqLiteDatabase.execSQL(questionTable);
        sqLiteDatabase.execSQL(answerTable);
        sqLiteDatabase.delete(OldField.TABLE_NAME, OldField.COLUMN_NAME_FIELD_NAME + " = '" + fieldName + "';", null);
        sqLiteDatabase.close();
        Log.d("from APP to Ahmed", "an field has been deleted");
    }

    public Cursor getFieldEntries(String fieldName) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX + "._id As" +
                " _id, " + fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + "," +
                fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX +
                " From " + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX +
                " INNER JOIN " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX + " ON " +
                fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX +
                " = " + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + "", null);
        return cursor;
    }
    private void editQuestion( String fieldName, String currentQuestion, String newQuestion ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX,
                new String[]{fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX},
                fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ?",
                new String[]{newQuestion},
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX, newQuestion);
            sqLiteDatabase.update(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX, contentValues,
                    fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ?", new String[]{currentQuestion});
        }
        else {
            sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX, fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ?",
                    new String[]{currentQuestion});
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    private void editAnswer( String fieldName, String currentAnswer, String newAnswer ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX,
                new String[]{fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX},
                fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ?",
                new String[]{newAnswer},
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX, newAnswer);
            sqLiteDatabase.update(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX, contentValues,
                    fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ?", new String[]{currentAnswer});
        }
        else {
            sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX,fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ?",
                    new String[]{currentAnswer});
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public void editFieldEntry(String fieldName, String currentQuestion, String newQuestion, String currentAnswer, String newAnswer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        if ( !currentQuestion.equals(newQuestion) ) {
            Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_LINK_SUFFIX,
                    new String[]{fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX},
                    fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " = ?",
                    new String[]{currentQuestion},
                    null,
                    null,
                    null);
            if (cursor.getCount() > 1) {
                insertQuestion(fieldName, newQuestion);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editQuestion(fieldName,currentQuestion,newQuestion);
            }
            cursor.close();
        }
        if ( !currentAnswer.equals(newAnswer) ) {
            Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_LINK_SUFFIX,
                    new String[]{ fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX},
                    fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " = ?",
                    new String[]{currentAnswer},
                    null,
                    null,
                    null);
            if (cursor.getCount() > 1) {
                insertQuestion(fieldName, newAnswer);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editAnswer(fieldName,currentAnswer,newAnswer);
            }
            cursor.close();
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
        sqLiteDatabase.close();
    }


    public void deleteOnlyFieldEntry(String fieldName, String question, String answer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " = ? " +  " AND " +
                fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " = ? " ;
        sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_LINK_SUFFIX,s,new String[] {question,answer});
        sqLiteDatabase.close();
    }
    public void deleteThisFieldQuestion(String fieldName, String question) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX,s,new String[] {question});
        sqLiteDatabase.close();
    }
    public void deleteThisFieldAnswer(String fieldName, String answer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX,s,new String[] {answer});
        sqLiteDatabase.close();
    }
    public void deleteThisEntryAndItsRoots(String fieldName, String question, String answer ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX,s,new String[] {question});
        s = fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX,s,new String[] {answer});
        sqLiteDatabase.close();
    }
    public Cursor searchFor ( String fieldName, String searchString ) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String [] columns = { "rowid _id" , fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX, fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX};
        String selections = fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " LIKE ? "  + " OR " +
                fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " LIKE ?";
        String [] selectionArgs = {searchString + "%",searchString + "%"};
        return sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_LINK_SUFFIX,columns,selections,selectionArgs ,null,null,null);
    }
    private void questionMigration ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX,
                    new String[] { fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX},
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
                    String statement = "INSERT INTO " + Question.TABLE_NAME + " ( " + Question.COLUMN_NAME_QUESTION_ENTRY
                            + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                            + "SELECT " + fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + "," + OldField.COLUMN_NAME_CREATED_AT
                            + " FROM " + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX
                            + " WHERE " + fieldName + OldField.COLUMN_NAME_QUESTION_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "questions has been transfered");
/*        ContentValues contentValues = new ContentValues();
        for ( String question : mainArrayList ) {
            contentValues.put(Question.COLUMN_NAME_QUESTION_ENTRY,question);
        }
        sqLiteDatabase.insert(Question.TABLE_NAME,null,contentValues);*/
    }
    private void answerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX, new String[] { fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX},
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
                    String statement = "INSERT INTO " + Answer.TABLE_NAME + " ( " + Answer.COLUMN_NAME_ANSWER_ENTRY
                            + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                            + "SELECT " + fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + "," + OldField.COLUMN_NAME_CREATED_AT
                            + " FROM " + fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX
                            + " WHERE " + fieldName + OldField.COLUMN_NAME_ANSWER_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
//                    mainArrayList.add(s);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "answers has been transfered");
        /*ContentValues contentValues = new ContentValues();
        for ( String answer : mainArrayList ) {
            contentValues.put(Answer.COLUMN_NAME_ANSWER_ENTRY,answer);
        }
        sqLiteDatabase.insert(Answer.TABLE_NAME,null,contentValues);*/
    }
    private void linkerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        String question, answer;
        cursor.close();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_NAME_LINK_SUFFIX,
                    new String[] {fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX, fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                question = cursor.getString(0);
                answer = cursor.getString(1);
                question = question.replace("'", "''");
                answer = answer.replace("'", "''");
                String statement = "INSERT INTO " + Linker.TABLE_NAME + " ( " + Linker.COLUMN_NAME_QUESTION_LINK
                        + "," + Linker.COLUMN_NAME_ANSWER_LINK
                        + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                        + "SELECT " + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + ","
                        + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + "," + OldField.COLUMN_NAME_CREATED_AT
                        + " FROM " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX
                        + " WHERE " + fieldName + OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX + " = " + "'" + question + "'" + " AND "
                        + fieldName + OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX + " = " + "'" + answer + "'" + ";";
                sqLiteDatabase.execSQL(statement);
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(Linker.COLUMN_NAME_FIELD_LINK,fieldName);
                int rows = 0;
                int r = 0;
                Cursor c = sqLiteDatabase.query(Linker.TABLE_NAME,null,Linker.COLUMN_NAME_QUESTION_LINK
                        + " = ? AND " + Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Linker.COLUMN_NAME_FIELD_LINK + " IS NULL",
                        new String[] {question,answer},null,null,null);
                r = c.getCount();
              *//*  rows = sqLiteDatabase.update(Linker.TABLE_NAME,contentValues, Linker.COLUMN_NAME_QUESTION_LINK
                        + " = ? AND " + Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Linker.COLUMN_NAME_FIELD_LINK + " IS NULL",
                        new String[] {question,answer});*//*
                rows = sqLiteDatabase.update(Linker.TABLE_NAME,contentValues, Linker.COLUMN_NAME_QUESTION_LINK
                                + " = ? AND " + Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND " + Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[] {question,answer,""});*/
                String update = "UPDATE " + Linker.TABLE_NAME + " SET " + Linker.COLUMN_NAME_FIELD_LINK + " = " + "'" + fieldName + "'"
                        + " WHERE " + Linker.COLUMN_NAME_QUESTION_LINK + " = " + "'" + question + "'" + " AND "
                        + Linker.COLUMN_NAME_ANSWER_LINK + " = " + "'" + answer + "'" + " AND "
                        + Linker.COLUMN_NAME_FIELD_LINK + " IS NULL;";
                sqLiteDatabase.execSQL(update);
            }
        }
        Log.d("From Pen in Bin", "links has been transfered");
    }
    private void fieldTableMigration(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable" +
                " ( "
                +  OldField.COLUMN_NAME_FIELD_NAME + " TEXT , " +
                OldField.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + OldField.COLUMN_NAME_FIELD_NAME
                + "," + OldField.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + OldField.COLUMN_NAME_FIELD_NAME + ","
                + OldField.COLUMN_NAME_CREATED_AT
                + " FROM " + OldField.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + OldField.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
        copy = "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.Field.COLUMN_NAME_FIELD_NAME
                + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + OldField.COLUMN_NAME_FIELD_NAME + ","
                + OldField.COLUMN_NAME_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void tablesDeletion ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        for ( String fieldName: fieldList ) {
            String dropLink = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_LINK_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropLink);
            String dropAnswer = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_ANSWER_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropAnswer);
            String dropQuestion = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_NAME_QUESTION_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropQuestion);
        }
        Log.d("from peninbin", "tables deleted");
    }
    private void onUpgradeForFieldTableV3(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable"
                + " ( "
                + Contract.Field.COLUMN_NAME_FIELD_NAME + " TEXT PRIMARY KEY, "
                + Contract.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + Contract.Field.COLUMN_NAME_FIELD_NAME
                + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + Contract.Field.COLUMN_NAME_FIELD_NAME + ","
                + Contract.COLUMN_NAME_CREATED_AT
                + " FROM " + Contract.Field.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + Contract.Field.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
        copy =  "INSERT INTO " + Contract.Field.TABLE_NAME + " ( " + Contract.Field.COLUMN_NAME_FIELD_NAME
                + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + Contract.Field.COLUMN_NAME_FIELD_NAME + ","
                + Contract.COLUMN_NAME_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForQuestionTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Question.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(CREATE_QUESTION_TABLE);
        String copy =  "INSERT INTO " + Question.TABLE_NAME + " ( " + Question.COLUMN_NAME_QUESTION_ENTRY
                + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + Question.COLUMN_NAME_QUESTION_ENTRY + ","
                + Contract.COLUMN_NAME_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForAnswerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Answer.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(CREATE_ANSWER_TABLE);
        String copy =  "INSERT INTO " + Answer.TABLE_NAME + " ( " + Answer.COLUMN_NAME_ANSWER_ENTRY
                + "," + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + Answer.COLUMN_NAME_ANSWER_ENTRY + ","
                + Contract.COLUMN_NAME_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForLinkerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Linker.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(CREATE_LINKER_TABLE);
        String copy =  "INSERT INTO " + Linker.TABLE_NAME + " ( " + Linker.COLUMN_NAME_QUESTION_LINK
                + "," + Linker.COLUMN_NAME_ANSWER_LINK + ","
                + Linker.COLUMN_NAME_FIELD_LINK + ","
                + Contract.COLUMN_NAME_CREATED_AT + " ) "
                + "SELECT " + Linker.COLUMN_NAME_QUESTION_LINK
                + "," + Linker.COLUMN_NAME_ANSWER_LINK + ","
                + Linker.COLUMN_NAME_FIELD_LINK + ","
                + Contract.COLUMN_NAME_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
}
