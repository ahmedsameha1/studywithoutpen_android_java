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
        String[] string = {Contract.COLUMN_NAME_ID, Contract.Field.COLUMN_NAME_FIELD_NAME, Contract.Field.COLUMN_NAME_FIELD_TYPE};
        Cursor cursor = sqLiteDatabase.query( Contract.Field.TABLE_NAME, string, null, null, null,null,null);
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

    public boolean checkFieldDuplication(String fieldName) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                Contract.Field.COLUMN_NAME_FIELD_NAME + " = ? ",
                new String[]{fieldName},null,null,null);
        boolean temp = cursor.getCount() != 0;
        cursor.close();
        return temp;
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



    public int getFieldType ( long field_id ) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,new String[]{Contract.Field.COLUMN_NAME_FIELD_TYPE},
                Contract.COLUMN_NAME_ID + " = ? ",
                new String[]{field_id + ""},null,null,null);
        cursor.moveToFirst();
        int n = cursor.getInt(0);
        cursor.close();
        return n;
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
