package com.ahmedsameha1.studyvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.commonsware.cwac.loaderex.AbstractCursorLoader;
import com.commonsware.cwac.loaderex.ContentChangingTask;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ahmed on 4/9/16.
 */
public class MySQLiteCursorLoader extends AbstractCursorLoader {
    SQLiteOpenHelper db = null;
    String rawQuery = null;
    String[] args = null;
    Context context;


    public MySQLiteCursorLoader(Context context,
                                String rawQuery, String[] args) {
        super(context);
        this.db = DatabaseHelper.getInstance(context);
        this.rawQuery = rawQuery;
        this.args = args;
        this.context = context;
    }

    public MySQLiteCursorLoader(Context context) {
        super(context);
        this.db = DatabaseHelper.getInstance(context);
        this.context = context;
    }

    /**
     * Runs on a worker thread and performs the actual
     * database query to retrieve the Cursor.
     */
    @Override
    protected Cursor buildCursor() {
        return (db.getReadableDatabase().rawQuery(rawQuery, args));
    }

    /**
     * Writes a semi-user-readable roster of contents to
     * supplied output.
     */
    @Override
    public void dump(String prefix, FileDescriptor fd,
                     PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("rawQuery=");
        writer.println(rawQuery);
        writer.print(prefix);
        writer.print("args=");
        writer.println(Arrays.toString(args));
    }

    public void insert(String table, String nullColumnHack,
                       ContentValues values) {
        buildInsertTask(this).execute(db, table, nullColumnHack, values);
    }

    public void createField(String field_name, int field_type, int question_data_type) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME, field_name);
        contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE, field_type);
        contentValues.put(Contract.Field.COLUMN_NAME_ENTRY_QUESTION_COLUMN_DATA_TYPE, question_data_type);
        sqLiteDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
    }

    public void deleteField(long field_id) {
        buildDeleteField(this).execute(db, field_id);
    }

    public void update(String table, ContentValues values,
                       String whereClause, String[] whereArgs) {
        buildUpdateTask(this).execute(db, table, values, whereClause,
                whereArgs);
    }

    public void replace(String table, String nullColumnHack,
                        ContentValues values) {
        buildReplaceTask(this).execute(db, table, nullColumnHack, values);
    }

    public void delete(String table, String whereClause,
                       String[] whereArgs) {
        buildDeleteTask(this).execute(db, table, whereClause, whereArgs);
    }

    public void insertEntry(int question_data_type, String question, String answer, long field_id) {
        buildInsertEntryTask(this).execute(db, question_data_type, question, answer, field_id);
    }

    public void deleteEntry(long entry_id, int question_data_type) {
        buildDeleteEntryTask(this).execute(db, entry_id, question_data_type);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        buildExecSQLTask(this).execute(db, sql, bindArgs);
    }

    protected ContentChangingTask buildInsertTask(MySQLiteCursorLoader loader) {
        return (new InsertTask(loader));
    }

    protected ContentChangingTask buildDeleteField(MySQLiteCursorLoader loader) {
        return (new DeleteField(loader));
    }


    protected ContentChangingTask buildUpdateTask(MySQLiteCursorLoader loader) {
        return (new UpdateTask(loader));
    }

    protected ContentChangingTask buildReplaceTask(MySQLiteCursorLoader loader) {
        return (new ReplaceTask(loader));
    }

    protected ContentChangingTask buildDeleteEntryTask(MySQLiteCursorLoader loader) {
        return (new DeleteEntryTask(loader));
    }

    protected ContentChangingTask buildDeleteTask(MySQLiteCursorLoader loader) {
        return (new DeleteTask(loader));
    }

    protected ContentChangingTask buildExecSQLTask(MySQLiteCursorLoader loader) {
        return (new ExecSQLTask(loader));
    }

    protected ContentChangingTask buildCreateFieldTask(MySQLiteCursorLoader loader) {
        return (new CreateField(loader));
    }

    protected ContentChangingTask buildIncrementFieldUsageTask(MySQLiteCursorLoader loader) {
        return (new IncrementFieldUsageTask(loader));
    }


    public void incrementFieldUsage(long field_id) {
        buildIncrementFieldUsageTask(this).execute(db, field_id);
    }

    protected static class InsertTask extends ContentChangingTask {
        InsertTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String table = (String) params[1];
            String nullColumnHack = (String) params[2];
            ContentValues values = (ContentValues) params[3];

            db.getWritableDatabase().insert(table, nullColumnHack, values);

            return (null);
        }
    }

    public void putEntriesTestOrder(long field_id, int question_data_type, String[][] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            for (int i = 0; i < array.length; i++) {
                Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                        Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                        new String[]{array[i][0]}, null, null, null);
                questionCursor.moveToFirst();
                long question_id = questionCursor.getLong(0);
                questionCursor.close();
                Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                        Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                        new String[]{array[i][1]}, null, null, null);
                answerCursor.moveToFirst();
                long answer_id = answerCursor.getLong(0);
                answerCursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_NAME_QUESTION + " = ? AND "
                                + Contract.String_Entry.COLUMN_NAME_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                        new String[]{question_id + "", answer_id + "", field_id + ""});
            }
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void putEntriesStudyPeriodOrder(long field_id, int question_data_type, String[][] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            for (int i = 0; i < array.length; i++) {
                Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                        Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                        new String[]{array[i][0]}, null, null, null);
                questionCursor.moveToFirst();
                long question_id = questionCursor.getLong(0);
                questionCursor.close();
                Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                        Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                        new String[]{array[i][1]}, null, null, null);
                answerCursor.moveToFirst();
                long answer_id = answerCursor.getLong(0);
                answerCursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_NAME_PAUSED_STUDY_PERIOD_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_NAME_QUESTION + " = ? AND "
                                + Contract.String_Entry.COLUMN_NAME_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                        new String[]{question_id + "", answer_id + "", field_id + ""});
            }
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void putStudyPeriodRepeatedEntries(long field_id, ArrayList<Integer> arrayList) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            for (Integer i : arrayList) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_NAME_REPEATED_ENTRY, i);
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_NAME_FIELD, field_id);
                sqLiteDatabase.insert(Contract.Study_Period_Repeated_Entry.TABLE_NAME, null, contentValues);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void putTestPausedData(long field_id, int[] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Test_Paused.COLUMN_NAME_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Test_Paused.COLUMN_NAME_WRONG_ANSWERS_COUNT, array[1]);
        contentValues.put(Contract.Test_Paused.COLUMN_NAME_TRIES_NUMBER, array[2]);
        contentValues.put(Contract.Test_Paused.COLUMN_NAME_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Test_Paused.TABLE_NAME, null, contentValues);
    }

    public void putStudyPeriodPausedData(long field_id, int[] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Study_Period_Paused.COLUMN_NAME_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_NAME_TRIES_NUMBER, array[1]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_NAME_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Study_Period_Paused.TABLE_NAME, null, contentValues);
    }


    public void editEntry(long entry_id, int question_data_type, String question, String answer) {
        buildEditEntryTask(this).execute(db, context, entry_id, question_data_type, question, answer);
    }

    protected ContentChangingTask buildEditEntryTask(MySQLiteCursorLoader loader) {
        return (new EditEntryTask(loader));
    }


    protected ContentChangingTask buildInsertEntryTask(MySQLiteCursorLoader loader) {
        return (new InsertEntryTask(loader));
    }


    protected static class EditEntryTask extends ContentChangingTask {
        EditEntryTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            Context context = (Context) params[1];
            long entry_id = (long) params[2];
            int question_data_type = (int) params[3];
            String question = (String) params[4];
            String answer = (String) params[5];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    long question_id, answer_id;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE, answer);
                    try {
                        answer_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                                new String[]{answer}, null, null, null);
                        c.moveToFirst();
                        answer_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE, question);
                    try {
                        question_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                                new String[]{question}, null, null, null);
                        c.moveToFirst();
                        question_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_QUESTION, question_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_ANSWER, answer_id);

                    sqLiteDatabase.updateWithOnConflict(Contract.String_Entry.TABLE_NAME, contentValues,
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{entry_id + ""}, SQLiteDatabase.CONFLICT_IGNORE);
                    cleanSTringTable(sqLiteDatabase);
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }

            return (null);
        }
    }

    protected static class IncrementFieldUsageTask extends ContentChangingTask {
        IncrementFieldUsageTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long field_id = (long) params[1];
            Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_NAME_USAGE_COUNT},
                    Contract.COLUMN_NAME_ID + " = ? ",
                    new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_NAME_USAGE_COUNT, cursor.getInt(0) + 1);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_NAME_ID + " = ? ", new String[]{field_id + ""});
            cursor.close();
            return (null);
        }
    }

    protected static class InsertEntryTask extends ContentChangingTask {
        InsertEntryTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            int question_data_type = (int) params[1];
            String question = (String) params[2];
            String answer = (String) params[3];
            long field_id = (long) params[4];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    long question_id, answer_id;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE, answer);
                    try {
                        answer_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                                new String[]{answer}, null, null, null);
                        c.moveToFirst();
                        answer_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_NAME_VALUE, question);
                    try {
                        question_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_NAME_ID},
                                Contract.STring.COLUMN_NAME_VALUE + " = ? ",
                                new String[]{question}, null, null, null);
                        c.moveToFirst();
                        question_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_QUESTION, question_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_ANSWER, answer_id);
                    contentValues.put(Contract.String_Entry.COLUMN_NAME_FIELD, field_id);
                    sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return (null);
        }
    }

    protected static class DeleteEntryTask extends ContentChangingTask {
        DeleteEntryTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long entry_id = (long) params[1];
            int question_data_type = (int) params[2];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    sqLiteDatabase.delete(Contract.String_Entry.TABLE_NAME,
                            Contract.COLUMN_NAME_ID + " = ? ",
                            new String[]{entry_id + ""});

                    cleanSTringTable(sqLiteDatabase);
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void resetEntriesStudyPeriodOrder(long field_id, int question_data_type) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_NAME_PAUSED_STUDY_PERIOD_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void resetEntriesTestOrder(long field_id, int question_data_type) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }


    protected static class DeleteField extends ContentChangingTask {
        DeleteField(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                sqLiteDatabase.delete(Contract.Field.TABLE_NAME,
                        Contract.COLUMN_NAME_ID + " = ? ",
                        new String[]{field_id + ""});

                cleanSTringTable(sqLiteDatabase);

            /*    sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                + " IN ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.STring.TABLE_NAME + " EXCEPT SELECT "
                + Contract.String_Entry.COLUMN_NAME_ANSWER + " FROM " + Contract.String_Entry.TABLE_NAME + " );")*/
                ;

                sqLiteDatabase.execSQL("DELETE FROM " + Contract.Not_String.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                        + " IN ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.Not_String.TABLE_NAME + " EXCEPT SELECT "
                        + Contract.Not_String_Entry.COLUMN_NAME_QUESTION + " FROM " + Contract.Not_String_Entry.TABLE_NAME + " );");

           /*     sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                + " IN ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.STring.TABLE_NAME + " EXCEPT SELECT "
                + Contract.Not_String_Entry.COLUMN_NAME_ANSWER + " FROM " + Contract.Not_String_Entry.TABLE_NAME + " );");

                sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                + " IN ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.STring.TABLE_NAME + " EXCEPT SELECT "
                + Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_String.TABLE_NAME + " );");

                sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                + " IN ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.STring.TABLE_NAME + " EXCEPT SELECT "
                + Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_Not_String.TABLE_NAME + " );");*/

                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(long field_id) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            sqLiteDatabase.delete(Contract.Study_Period_Paused.TABLE_NAME, Contract.Study_Period_Paused.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
            sqLiteDatabase.delete(Contract.Study_Period_Repeated_Entry.TABLE_NAME, Contract.Study_Period_Repeated_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public void clearThisFieldFromTestPaused(long field_id) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.delete(Contract.Test_Paused.TABLE_NAME, Contract.Test_Paused.COLUMN_NAME_FIELD + " = ? ",
                new String[]{field_id + ""});
    }

    protected ContentChangingTask buildClearThisFieldFromTestPaused(MySQLiteCursorLoader loader) {
        return (new ClearThisFieldFromTestPaused(loader));
    }

    protected static class ClearThisFieldFromTestPaused extends ContentChangingTask {
        ClearThisFieldFromTestPaused(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.delete(Contract.Test_Paused.TABLE_NAME, Contract.Test_Paused.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{field_id + ""});
            return null;
        }
    }

    public void clearThisFieldFromWrongAnswers(long field_id, int question_data_type) {
        buildClearThisFieldFromWrongAnswers(this).execute(db, field_id, question_data_type);
    }

    protected ContentChangingTask buildClearThisFieldFromWrongAnswers(MySQLiteCursorLoader loader) {
        return (new ClearThisFieldFromWrongAnswers(loader));
    }

    protected static class ClearThisFieldFromWrongAnswers extends ContentChangingTask {
        ClearThisFieldFromWrongAnswers(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int question_data_type = (int) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_NAME_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.String_Entry.TABLE_NAME
                            + " WHERE " + Contract.String_Entry.COLUMN_NAME_FIELD + " = " + field_id + " );");
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_Not_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_Not_String.COLUMN_NAME_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                            + " WHERE " + Contract.Not_String_Entry.COLUMN_NAME_FIELD + " = " + field_id + " );");
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void putEntryWrongAnswers(String question,String answer , long field_id , ArrayList<String> wrongAnswer, int question_data_type) {
        buildPutEntryWrongAnswers(this).execute(db, question,answer,field_id, wrongAnswer, question_data_type);
    }

    protected ContentChangingTask buildPutEntryWrongAnswers(MySQLiteCursorLoader loader) {
        return (new PutEntryWrongAnswers(loader));
    }

    protected static class PutEntryWrongAnswers extends ContentChangingTask {
        PutEntryWrongAnswers(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            String question = (String) params[1];
            String answer = (String) params[2];
            long field_id = (long) params[3];
            ArrayList<String> wrongAnswers = (ArrayList<String>) params[4];
            int question_data_type = (int) params[5];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
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
                    for (String string : wrongAnswers) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_NAME_ENTRY, entry_id);
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_NAME_WRONG_ANSWER, string);
                        sqLiteDatabase.insert(Contract.Test_Wrong_Answer_String.TABLE_NAME, null, contentValues);
                    }
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    protected static class CreateField extends ContentChangingTask {
        CreateField(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String field_name = (String) params[1];
            int field_type = (Integer) params[2];
            int question_data_type = (Integer) params[3];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_NAME_FIELD_NAME, field_name);
            contentValues.put(Contract.Field.COLUMN_NAME_FIELD_TYPE, field_type);
            contentValues.put(Contract.Field.COLUMN_NAME_ENTRY_QUESTION_COLUMN_DATA_TYPE, question_data_type);
            sqLiteDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
            return null;
        }
    }


    protected static class UpdateTask extends ContentChangingTask {
        UpdateTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String table = (String) params[1];
            ContentValues values = (ContentValues) params[2];
            String where = (String) params[3];
            String[] whereParams = (String[]) params[4];

            db.getWritableDatabase()
                    .update(table, values, where, whereParams);

            return (null);
        }
    }


    protected static class ReplaceTask extends ContentChangingTask {
        ReplaceTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String table = (String) params[1];
            String nullColumnHack = (String) params[2];
            ContentValues values = (ContentValues) params[3];

            db.getWritableDatabase().replace(table, nullColumnHack, values);

            return (null);
        }
    }

    protected static class DeleteTask extends ContentChangingTask {
        DeleteTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String table = (String) params[1];
            String where = (String) params[2];
            String[] whereParams = (String[]) params[3];

            db.getWritableDatabase().delete(table, where, whereParams);

            return (null);
        }
    }

    protected static class ExecSQLTask extends ContentChangingTask {
        ExecSQLTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            String sql = (String) params[1];
            Object[] bindParams = (Object[]) params[2];

            db.getWritableDatabase().execSQL(sql, bindParams);

            return (null);
        }
    }

    private static void cleanSTringTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_NAME_ID
                + " IN ( SELECT "
                + Contract.String_Entry.COLUMN_NAME_QUESTION + " FROM " + Contract.String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.String_Entry.COLUMN_NAME_ANSWER + " FROM " + Contract.String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.Not_String_Entry.COLUMN_NAME_ANSWER + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.Alternative_Answer_String.COLUMN_NAME_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_String.TABLE_NAME
                + " UNION SELECT " + Contract.Alternative_Answer_Not_String.COLUMN_NAME_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_Not_String.TABLE_NAME
                + " EXCEPT SELECT " + Contract.COLUMN_NAME_ID + " FROM " + Contract.STring.TABLE_NAME
                + " );");
    }
}
