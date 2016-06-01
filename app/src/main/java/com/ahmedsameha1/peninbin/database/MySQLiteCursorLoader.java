package com.ahmedsameha1.peninbin.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import com.ahmedsameha1.peninbin.receivers_services.ShowScheduledNotificationsReceiver;
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


    public void createField(String field_name, int field_type, int question_data_type, int check_type, boolean read_answer, String locale) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, field_name);
        contentValues.put(Contract.Field.COLUMN_FIELD_TYPE, field_type);
        contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, question_data_type);
        contentValues.put(Contract.Field.COLUMN_CHECK_TYPE, check_type);
        contentValues.put(Contract.Field.COLUMN_READ_ANSWER, read_answer ? Contract.Field.READ_ANSWER : Contract.Field.DO_NOT_READ_ANSWER);
        if (locale != null) {
            contentValues.put(Contract.Field.COLUMN_LOCALE, locale);
        }
        sqLiteDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
    }
    public void createField(String field_name, int field_type, int question_data_type) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, field_name);
        contentValues.put(Contract.Field.COLUMN_FIELD_TYPE, field_type);
        contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, question_data_type);
        sqLiteDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
    }


    public void setRandomQuickTestData(long field_id, int number_of_quick_tests, int number_of_tries, int number_of_question, String[][] excluded_times) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            int s = sqLiteDatabase.delete(Contract.Excluded_Time.TABLE_NAME, Contract.Excluded_Time.COLUMN_FIELD + " = ? ", new String[]{field_id + ""});
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, number_of_quick_tests);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, number_of_tries);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, number_of_question);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            if (excluded_times != null) {
                for (String[] temp : excluded_times) {
                    if (temp != null) {
                        contentValues = new ContentValues();
                        contentValues.put(Contract.Excluded_Time.COLUMN_STARTS, temp[0]);
                        contentValues.put(Contract.Excluded_Time.COLUMN_ENDS, temp[1]);
                        contentValues.put(Contract.Excluded_Time.COLUMN_FIELD, field_id);
                        sqLiteDatabase.insert(Contract.Excluded_Time.TABLE_NAME, null, contentValues);
                    }
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public void insertEntry(int question_data_type, String question, String answer, long field_id) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                long question_id, answer_id;
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE, answer);
                try {
                    answer_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                } catch (Exception e) {
                    Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{answer}, null, null, null);
                    c.moveToFirst();
                    answer_id = c.getInt(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.STring.COLUMN_VALUE, question);
                try {
                    question_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                } catch (Exception e) {
                    Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{question}, null, null, null);
                    c.moveToFirst();
                    question_id = c.getInt(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void deleteEntry(long entry_id, int question_data_type) {
        buildDeleteEntryTask(this).execute(db, entry_id, question_data_type);
    }


    public void updateFieldReadAnswer(long field_id, boolean isReadAnswer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_READ_ANSWER, isReadAnswer ? Contract.Field.READ_ANSWER : Contract.Field.DO_NOT_READ_ANSWER);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }

    public void updateFieldCheckType(long field_id, int check_type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CHECK_TYPE, check_type);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }

    public void updateFieldLocale(long field_id, String locale) {
        ContentValues contentValues = new ContentValues();
        if (locale != null) {
            contentValues.put(Contract.Field.COLUMN_LOCALE, locale);
        } else {
            contentValues.putNull(Contract.Field.COLUMN_LOCALE);
        }
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }


    protected ContentChangingTask buildDeleteEntryTask(MySQLiteCursorLoader loader) {
        return (new DeleteEntryTask(loader));
    }


    protected ContentChangingTask buildIncrementFieldUsageTask(MySQLiteCursorLoader loader) {
        return (new IncrementFieldUsageTask(loader));
    }


    public void incrementFieldUsage(long field_id) {
        buildIncrementFieldUsageTask(this).execute(db, field_id);
    }


    public void putEntriesTestOrder(long field_id, int question_data_type, String[][] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            for (int i = 0; i < array.length; i++) {
                Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{array[i][0]}, null, null, null);
                questionCursor.moveToFirst();
                long question_id = questionCursor.getLong(0);
                questionCursor.close();
                Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{array[i][1]}, null, null, null);
                answerCursor.moveToFirst();
                long answer_id = answerCursor.getLong(0);
                answerCursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                + Contract.String_Entry.COLUMN_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                        new String[]{question_id + "", answer_id + "", field_id + ""});
            }
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void putEntriesStudyPeriodOrder(long field_id, int question_data_type, String[][] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            for (int i = 0; i < array.length; i++) {
                Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{array[i][0]}, null, null, null);
                questionCursor.moveToFirst();
                long question_id = questionCursor.getLong(0);
                questionCursor.close();
                Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{array[i][1]}, null, null, null);
                answerCursor.moveToFirst();
                long answer_id = answerCursor.getLong(0);
                answerCursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                + Contract.String_Entry.COLUMN_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
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
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY, i);
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_FIELD, field_id);
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
        contentValues.put(Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT, array[1]);
        contentValues.put(Contract.Test_Paused.COLUMN_TRIES_NUMBER, array[2]);
        contentValues.put(Contract.Test_Paused.COLUMN_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Test_Paused.TABLE_NAME, null, contentValues);
    }

    public void putStudyPeriodPausedData(long field_id, int[] array) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_TRIES_NUMBER, array[1]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Study_Period_Paused.TABLE_NAME, null, contentValues);
    }


    public void editEntry(long entry_id, int question_data_type, String question, String answer) {
        buildEditEntryTask(this).execute(db, context, entry_id, question_data_type, question, answer);
    }

    protected ContentChangingTask buildEditEntryTask(MySQLiteCursorLoader loader) {
        return (new EditEntryTask(loader));
    }

    public void updateFieldName(long current_field_id, String s) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, s);
        db.getWritableDatabase().update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{current_field_id + ""});
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
                    contentValues.put(Contract.STring.COLUMN_VALUE, answer);
                    try {
                        answer_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.STring.COLUMN_VALUE + " = ? ",
                                new String[]{answer}, null, null, null);
                        c.moveToFirst();
                        answer_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.STring.COLUMN_VALUE, question);
                    try {
                        question_id = sqLiteDatabase.insertOrThrow(Contract.STring.TABLE_NAME, null, contentValues);
                    } catch (Exception e) {
                        Cursor c = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                                Contract.STring.COLUMN_VALUE + " = ? ",
                                new String[]{question}, null, null, null);
                        c.moveToFirst();
                        question_id = c.getInt(0);
                        c.close();
                    }
                    contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                    contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);

                    sqLiteDatabase.updateWithOnConflict(Contract.String_Entry.TABLE_NAME, contentValues,
                            Contract.COLUMN_ID + " = ? ",
                            new String[]{entry_id + ""}, SQLiteDatabase.CONFLICT_IGNORE);
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }

            return (null);
        }
    }


    public void updateFieldSort(long field_id, int sort) {
        buildUpdateFieldSortTask(this).execute(db, field_id, sort);
    }

    protected ContentChangingTask buildUpdateFieldSortTask(MySQLiteCursorLoader loader) {
        return (new UpdateFieldSort(loader));
    }


    protected static class UpdateFieldSort extends ContentChangingTask {
        UpdateFieldSort(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int sort = (int) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_SORT_BY, sort);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            return (null);
        }
    }

    public void updateFieldAreasSize(long field_id, int question_area_size, int answer_area_size) {
        buildUpdateFieldAreasSize(this).execute(db, field_id, question_area_size, answer_area_size);
    }

    protected ContentChangingTask buildUpdateFieldAreasSize(MySQLiteCursorLoader loader) {
        return (new UpdateFieldAreasSize(loader));
    }


    protected static class UpdateFieldAreasSize extends ContentChangingTask {
        UpdateFieldAreasSize(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int question_area_size = (int) params[2];
            int answer_area_size = (int) params[3];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_QUESTION_AREA_SIZE, question_area_size);
            contentValues.put(Contract.Field.COLUMN_ANSWER_AREA_SIZE, answer_area_size);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            return (null);
        }
    }

    public void updateFieldAnswerAreaSize(long field_id, int question_area_size) {
        buildUpdateFieldAnswerAreaSize(this).execute(db, field_id, question_area_size);
    }

    protected ContentChangingTask buildUpdateFieldAnswerAreaSize(MySQLiteCursorLoader loader) {
        return (new UpdateFieldAnswerAreaSize(loader));
    }


    protected static class UpdateFieldAnswerAreaSize extends ContentChangingTask {
        UpdateFieldAnswerAreaSize(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int answer_area_size = (int) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_ANSWER_AREA_SIZE, answer_area_size);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            return (null);
        }
    }

    public void updateFieldTestTextSize(long field_id, int test_text_size) {
        buildUpdateFieldTestTextSize(this).execute(db, field_id, test_text_size);
    }

    protected ContentChangingTask buildUpdateFieldTestTextSize(MySQLiteCursorLoader loader) {
        return (new UpdateFieldTestTextSize(loader));
    }


    protected static class UpdateFieldTestTextSize extends ContentChangingTask {
        UpdateFieldTestTextSize(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int test_text_size = (int) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_TEST_TEXT_SIZE, test_text_size);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            return (null);
        }
    }

    public void updateFieldActionBarShown(long field_id, int action_bar_shown) {
        buildUpdateFieldActionBarShown(this).execute(db, field_id, action_bar_shown);
    }

    protected ContentChangingTask buildUpdateFieldActionBarShown(MySQLiteCursorLoader loader) {
        return (new UpdateFieldActionBarShown(loader));
    }


    protected static class UpdateFieldActionBarShown extends ContentChangingTask {
        UpdateFieldActionBarShown(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            int action_bar_shown = (int) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_ACTION_BAR_SHOWN, action_bar_shown);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
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
            Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_USAGE_COUNT},
                    Contract.COLUMN_ID + " = ? ",
                    new String[]{field_id + ""}, null, null, null);
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_USAGE_COUNT, cursor.getInt(0) + 1);
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            cursor.close();
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
                            Contract.COLUMN_ID + " = ? ",
                            new String[]{entry_id + ""});

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
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void resetEntriesTestOrder(long field_id, int question_data_type) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_TEST_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void deleteField(final long field_id, final Handler handler) {
        Thread pauseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.query(Contract.Scheduled_Time_Backup.TABLE_NAME,
                        new String[]{ Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST},
                        Contract.Scheduled_Time_Backup.COLUMN_FIELD + " = ? ", new String[]{field_id + ""}, null, null, null);
                if (cursor.getCount() > 0) {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, cursor.getInt(0), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.cancel(pendingIntent);
                    }
                }
                cursor.close();
                sqLiteDatabase.delete(Contract.Field.TABLE_NAME,
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{field_id + ""});

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        MySQLiteCursorLoader.this.onContentChanged();
                    }
                };
                handler.post(runnable);

            }
        });
        pauseThread.start();
    }

    public void cleanDatabase() {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        cleanSTringTable(sqLiteDatabase);

        sqLiteDatabase.execSQL("DELETE FROM " + Contract.Not_String.TABLE_NAME + " WHERE " + Contract.COLUMN_ID
                + " IN ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.Not_String.TABLE_NAME + " EXCEPT SELECT "
                + Contract.Not_String_Entry.COLUMN_QUESTION + " FROM " + Contract.Not_String_Entry.TABLE_NAME + " );");
    }


    public void clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(long field_id) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            sqLiteDatabase.delete(Contract.Study_Period_Paused.TABLE_NAME, Contract.Study_Period_Paused.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
            sqLiteDatabase.delete(Contract.Study_Period_Repeated_Entry.TABLE_NAME, Contract.Study_Period_Repeated_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public void clearThisFieldFromTestPaused(long field_id) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.delete(Contract.Test_Paused.TABLE_NAME, Contract.Test_Paused.COLUMN_FIELD + " = ? ",
                new String[]{field_id + ""});
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
                            + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.String_Entry.TABLE_NAME
                            + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = " + field_id + " );");
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_Not_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_Not_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                            + " WHERE " + Contract.Not_String_Entry.COLUMN_FIELD + " = " + field_id + " );");
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void putEntryWrongAnswers(String question, String answer, long field_id, ArrayList<String> wrongAnswer, int question_data_type) {
        buildPutEntryWrongAnswers(this).execute(db, question, answer, field_id, wrongAnswer, question_data_type);
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
                    Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{question}, null, null, null);
                    questionCursor.moveToFirst();
                    long question_id = questionCursor.getLong(0);
                    questionCursor.close();
                    Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.STring.COLUMN_VALUE + " = ? ",
                            new String[]{answer}, null, null, null);
                    answerCursor.moveToFirst();
                    long answer_id = answerCursor.getLong(0);
                    answerCursor.close();
                    Cursor entryCursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                            Contract.String_Entry.COLUMN_QUESTION + " = ? AND " + Contract.String_Entry.COLUMN_ANSWER + " = ? AND "
                                    + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                            new String[]{question_id + "", answer_id + "", field_id + ""}, null, null, null);
                    entryCursor.moveToFirst();
                    long entry_id = entryCursor.getLong(0);
                    entryCursor.close();
                    for (String string : wrongAnswers) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_ENTRY, entry_id);
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER, string);
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

    public void addScheduledTimeForBackup(long field_id, int session_type, long time, int pendingintent_request) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_FIELD, field_id);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE, session_type);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_TIME, time);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST, pendingintent_request);
        sqLiteDatabase.insert(Contract.Scheduled_Time_Backup.TABLE_NAME, null, contentValues);
    }
    public void addScheduledTimeForBackup(long field_id, int session_type, long time, int pendingintent_request,int percentage_or_number, int number, int number_of_tries) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_FIELD, field_id);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE, session_type);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_TIME, time);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST, pendingintent_request);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE, percentage_or_number);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER, number);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES, number_of_tries);
        sqLiteDatabase.insert(Contract.Scheduled_Time_Backup.TABLE_NAME, null, contentValues);
    }

    public void deleteScheduledTimeForBackup(long field_id, int session_type, long time) {
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        sqLiteDatabase.delete(Contract.Scheduled_Time_Backup.TABLE_NAME, Contract.Scheduled_Time_Backup.COLUMN_FIELD + " = ? AND "
                        + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE + " = ? AND " + Contract.Scheduled_Time_Backup.COLUMN_TIME + " = ?",
                new String[]{field_id + "", session_type + "", time + ""});
    }

    private static void cleanSTringTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DELETE FROM " + Contract.STring.TABLE_NAME + " WHERE " + Contract.COLUMN_ID
                + " IN ( SELECT "
                + Contract.COLUMN_ID + " FROM " + Contract.STring.TABLE_NAME
                + " EXCEPT SELECT * FROM ( SELECT "
                + Contract.String_Entry.COLUMN_QUESTION + " FROM " + Contract.String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.String_Entry.COLUMN_ANSWER + " FROM " + Contract.String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.Not_String_Entry.COLUMN_ANSWER + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                + " UNION SELECT " + Contract.Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_String.TABLE_NAME
                + " UNION SELECT " + Contract.Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER + " FROM " + Contract.Alternative_Answer_Not_String.TABLE_NAME
                + " ) as jjj"
                + " );");
    }
}
