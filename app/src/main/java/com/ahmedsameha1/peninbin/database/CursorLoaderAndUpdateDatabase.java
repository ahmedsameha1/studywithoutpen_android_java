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

import com.ahmedsameha1.peninbin.adapters.MainActivityRecyclerViewViewHolder;
import com.ahmedsameha1.peninbin.receivers_services.ShowScheduledNotificationsReceiver;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.SortOptions;
import com.commonsware.cwac.loaderex.AbstractCursorLoader;
import com.commonsware.cwac.loaderex.ContentChangingTask;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ahmed on 4/9/16.
 */
public class CursorLoaderAndUpdateDatabase extends AbstractCursorLoader {
    private SQLiteOpenHelper sqLiteOpenHelper = null;
    private SQLiteDatabase sqLiteDatabase = null;
    private String rawQuery = null;
    private String[] args = null;
    private Context context;


    public CursorLoaderAndUpdateDatabase(Context context,
                                         String rawQuery, String[] args) {
        super(context);
        this.sqLiteOpenHelper = DatabaseHelper.getInstance(context);
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        this.rawQuery = rawQuery;
        this.args = args;
        this.context = context;
    }


    public CursorLoaderAndUpdateDatabase(Context context) {
        super(context);
        this.sqLiteOpenHelper = DatabaseHelper.getInstance(context);
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        this.context = context;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    /**
     * Runs on a worker thread and performs the actual
     * database query to retrieve the Cursor.
     */
    @Override
    protected Cursor buildCursor() {
        return (sqLiteOpenHelper.getReadableDatabase().rawQuery(rawQuery, args));
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, field_name);
        contentValues.put(Contract.Field.COLUMN_FIELD_TYPE, field_type);
        contentValues.put(Contract.Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE, question_data_type);
        sqLiteDatabase.insert(Contract.Field.TABLE_NAME, null, contentValues);
    }


    public void setRandomQuickTestData(long field_id, int number_of_quick_tests, int number_of_tries, int number_of_question, String[][] excluded_times) {
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

    public void setRandomQuickTestData(long field_id, int number_of_quick_tests, int number_of_tries,
                                       int number_of_question, String[][] excluded_times, int controlled_portion, int wrongness_level, int rank_level) {
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
            int s = sqLiteDatabase.delete(Contract.Excluded_Time.TABLE_NAME, Contract.Excluded_Time.COLUMN_FIELD + " = ? ", new String[]{field_id + ""});
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS, number_of_quick_tests);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, number_of_tries);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS, number_of_question);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION, controlled_portion);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, wrongness_level);
            contentValues.put(Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL, rank_level);
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
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                            Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, sqLiteDatabase));
                }
                sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void insertEntryWithRank(int question_data_type, String question, String answer, long field_id, int rank) {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                contentValues.put(Contract.String_Entry.COLUMN_RANK, rank);
                int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                            Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, sqLiteDatabase));
                }
                sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void insertEntry(int question_data_type, String question, String answer, long field_id, int rank, int order) {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                contentValues.put(Contract.String_Entry.COLUMN_RANK, rank);
                if ( order <= -1 ) {
                    contentValues.putNull(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY);
                } else {
                    contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, order);
                }
                int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                            Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, sqLiteDatabase));
                }
                sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void insertEntryWithOrder(int question_data_type, String question, String answer, long field_id, int order) {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);
                contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, order);
                int number_of_entries_everyday = databaseReadOperations.isEmulated(field_id);
                if (number_of_entries_everyday > Constants.NOT_EMULATED) {
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                            Emulation.getEmulationCreationDate(field_id, number_of_entries_everyday, sqLiteDatabase));
                }
                sqLiteDatabase.insert(Contract.String_Entry.TABLE_NAME, null, contentValues);
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void deleteEntry(long entry_id, int question_data_type) {
        buildDeleteEntryTask(this).execute(sqLiteOpenHelper, entry_id, question_data_type);
    }


    public void updateFieldReadAnswer(long field_id, boolean isReadAnswer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_READ_ANSWER, isReadAnswer ? Contract.Field.READ_ANSWER : Contract.Field.DO_NOT_READ_ANSWER);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }

    public void updateFieldCheckType(long field_id, int check_type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CHECK_TYPE, check_type);
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
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }


    protected ContentChangingTask buildDeleteEntryTask(CursorLoaderAndUpdateDatabase loader) {
        return (new DeleteEntryTask(loader));
    }


    protected ContentChangingTask buildIncrementFieldUsageTask(CursorLoaderAndUpdateDatabase loader) {
        return (new IncrementFieldUsageTask(loader));
    }


    public void incrementFieldUsage(long field_id) {
        buildIncrementFieldUsageTask(this).execute(sqLiteOpenHelper, field_id);
    }


    public void putEntriesTestOrderEnhancedTest(long field_id, int question_data_type, String[][] array) {
        sqLiteDatabase.beginTransactionNonExclusive();
try {


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
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER, i);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                            + Contract.String_Entry.COLUMN_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{question_id + "", answer_id + "", field_id + ""});
        }
    } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
    }
    sqLiteDatabase.setTransactionSuccessful();
} finally {
    sqLiteDatabase.endTransaction();
}
    }

    public void putEntriesTestOrderFullyRandomTest(long field_id, int question_data_type, String[][] array) {
        sqLiteDatabase.beginTransactionNonExclusive();
        try {
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
                    contentValues.put(Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER, i);
                    sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                    + Contract.String_Entry.COLUMN_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                            new String[]{question_id + "", answer_id + "", field_id + ""});
                }
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }




    public void clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(long field_id) {
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

    public void putStudyPeriodPausedData(long field_id, int[] array) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_TRIES_NUMBER, array[1]);
        contentValues.put(Contract.Study_Period_Paused.COLUMN_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Study_Period_Paused.TABLE_NAME, null, contentValues);
    }

    public void resetEntriesStudyPeriodOrder(long field_id, int question_data_type) {
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }
    public void putEntriesStudyPeriodOrder( int question_data_type, String[][] array) {
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            for (int i = 0; i < array.length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ",
                        new String[]{array[i][2]});
            }
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }
    public void putEntriesStudyPeriodOrder(long field_id, int question_data_type, String[][] array) {
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
    public void pauseStudyAPeriod( long field_id, String[][] listForTest, int[] session_data, ArrayList<Integer> repeated_entries ) {
        sqLiteDatabase.beginTransactionNonExclusive();
        try {

            sqLiteDatabase.delete(Contract.Study_Period_Paused.TABLE_NAME, Contract.Study_Period_Paused.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
            sqLiteDatabase.delete(Contract.Study_Period_Repeated_Entry.TABLE_NAME, Contract.Study_Period_Repeated_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});



            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER, session_data[0]);
            contentValues.put(Contract.Study_Period_Paused.COLUMN_TRIES_NUMBER, session_data[1]);
            contentValues.put(Contract.Study_Period_Paused.COLUMN_FIELD, field_id);
            sqLiteDatabase.insert(Contract.Study_Period_Paused.TABLE_NAME, null, contentValues);



            contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});





            for (int i = 0; i < listForTest.length; i++) {
                Cursor questionCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{listForTest[i][0]}, null, null, null);
                questionCursor.moveToFirst();
                long question_id = questionCursor.getLong(0);
                questionCursor.close();
                Cursor answerCursor = sqLiteDatabase.query(Contract.STring.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.STring.COLUMN_VALUE + " = ? ",
                        new String[]{listForTest[i][1]}, null, null, null);
                answerCursor.moveToFirst();
                long answer_id = answerCursor.getLong(0);
                answerCursor.close();
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER, i);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.String_Entry.COLUMN_QUESTION + " = ? AND "
                                + Contract.String_Entry.COLUMN_ANSWER + " = ? AND " + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                        new String[]{question_id + "", answer_id + "", field_id + ""});
            }


            for (Integer i : repeated_entries) {
                contentValues = new ContentValues();
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY, i);
                contentValues.put(Contract.Study_Period_Repeated_Entry.COLUMN_FIELD, field_id);
                sqLiteDatabase.insert(Contract.Study_Period_Repeated_Entry.TABLE_NAME, null, contentValues);
            }






            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public void pauseStudyAPeriod( long field_id, String[][] listForTest, int[] session_data, ArrayList<Integer> repeated_entries, int question_data_type ) {
        sqLiteDatabase.beginTransactionNonExclusive();
        try {

            clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(field_id);
            putStudyPeriodPausedData(field_id,session_data);
            resetEntriesStudyPeriodOrder(field_id, question_data_type);
            putEntriesStudyPeriodOrder(field_id,question_data_type, listForTest);
            putStudyPeriodRepeatedEntries(field_id,repeated_entries);


            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    public void putTestPausedDataEnhancedTest(long field_id, int[] array) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Test_Paused.COLUMN_TEST_TYPE, Constants.TestType.ENHANCED_TEST);
        contentValues.put(Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT, array[1]);
        contentValues.put(Contract.Test_Paused.COLUMN_TRIES_NUMBER, array[2]);
        contentValues.put(Contract.Test_Paused.COLUMN_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Test_Paused.TABLE_NAME, null, contentValues);
    }

    public void putTestPausedDataFullyRandomTest(long field_id, int[] array) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Test_Paused.COLUMN_TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
        contentValues.put(Contract.Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER, array[0]);
        contentValues.put(Contract.Test_Paused.COLUMN_WRONG_ANSWERS_COUNT, array[1]);
        contentValues.put(Contract.Test_Paused.COLUMN_TRIES_NUMBER, array[2]);
        contentValues.put(Contract.Test_Paused.COLUMN_FIELD, field_id);
        sqLiteDatabase.insert(Contract.Test_Paused.TABLE_NAME, null, contentValues);
    }



    public void editEntry(long entry_id, int question_data_type, String question, String answer) {
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
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
    }

    public void editEntry(long entry_id, int question_data_type, String question, String answer, int rank, int order) {
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
                    answer_id = c.getLong(0);
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
                    question_id = c.getLong(0);
                    c.close();
                }
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_QUESTION, question_id);
                contentValues.put(Contract.String_Entry.COLUMN_ANSWER, answer_id);

                sqLiteDatabase.updateWithOnConflict(Contract.String_Entry.TABLE_NAME, contentValues,
                        Contract.COLUMN_ID + " = ? ",
                        new String[]{entry_id + ""}, SQLiteDatabase.CONFLICT_IGNORE);
                contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_RANK, rank);
                if ( order <= -1 ) {
                    contentValues.putNull(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY);
                } else {
                    contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, order);
                }
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                        Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
            } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public void updateFieldName(long current_field_id, String s) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_FIELD_NAME, s);
        sqLiteOpenHelper.getWritableDatabase().update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{current_field_id + ""});
    }


    public void updateFieldSort(long field_id, SortOptions sortOption) {
        buildUpdateFieldSortTask(this).execute(sqLiteOpenHelper, field_id, sortOption);
    }

    protected ContentChangingTask buildUpdateFieldSortTask(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateFieldSort(loader));
    }


    protected static class UpdateFieldSort extends ContentChangingTask {
        UpdateFieldSort(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            SortOptions sortOption = (SortOptions) params[2];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_SORT_BY, sortOption.getSortOptionPosition());
            sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
            return (null);
        }
    }

    public void updateFieldAreasSize(long field_id, int question_area_size, int answer_area_size) {
        buildUpdateFieldAreasSize(this).execute(sqLiteOpenHelper, field_id, question_area_size, answer_area_size);
    }

    protected ContentChangingTask buildUpdateFieldAreasSize(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateFieldAreasSize(loader));
    }


    protected static class UpdateFieldAreasSize extends ContentChangingTask {
        UpdateFieldAreasSize(CursorLoaderAndUpdateDatabase loader) {
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

    public void copyEntries(ArrayList<Long> list_of_entries, long field_id) {
        buildCopyEntries(this).execute(sqLiteOpenHelper, list_of_entries, field_id, context);
    }

    protected ContentChangingTask buildCopyEntries(CursorLoaderAndUpdateDatabase loader) {
        return (new CopyEntries(loader));
    }


    protected static class CopyEntries extends ContentChangingTask {
        CopyEntries(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            ArrayList<Long> list_of_entries = (ArrayList<Long>) params[1];
            long field_id = (long) params[2];
            Context context = (Context) params[3];
            int number_of_everyday_entries = new DatabaseReadOperations(context).isEmulated(field_id);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            try {
                for (long entry_id : list_of_entries) {
                    Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                            new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                                    Contract.String_Entry.COLUMN_RANK, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                            Contract.COLUMN_ID + " = ?",
                            new String[]{entry_id + ""}, null, null, null, null);
                    cursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_FIELD, field_id);
                    contentValues.put(Contract.String_Entry.COLUMN_QUESTION, cursor.getLong(0));
                    contentValues.put(Contract.String_Entry.COLUMN_ANSWER, cursor.getLong(1));
                    contentValues.put(Contract.String_Entry.COLUMN_RANK, cursor.getInt(2));
                    contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, cursor.getInt(3));
                    if (number_of_everyday_entries > Constants.NOT_EMULATED) {
                        contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                Emulation.getEmulationCreationDate(field_id, number_of_everyday_entries, sqLiteDatabase));
                    }
                    sqLiteDatabase.insertWithOnConflict(Contract.String_Entry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return (null);
        }
    }

    public void copyField(long from_field_id, long to_field_id) {
        buildCopyField(this).execute(sqLiteOpenHelper, from_field_id, to_field_id, context);
    }

    protected ContentChangingTask buildCopyField(CursorLoaderAndUpdateDatabase loader) {
        return (new CopyField(loader));
    }


    protected static class CopyField extends ContentChangingTask {
        CopyField(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long from_field_id = (long) params[1];
            long to_field_id = (long) params[2];
            Context context = (Context) params[3];
            int number_of_everyday_entries = new DatabaseReadOperations(context).isEmulated(to_field_id);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            try {
                Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                        new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                                Contract.String_Entry.COLUMN_RANK, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                        Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{from_field_id + ""}, null, null, null);
                for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                     cursor.moveToNext()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_FIELD, to_field_id);
                    contentValues.put(Contract.String_Entry.COLUMN_QUESTION, cursor.getLong(0));
                    contentValues.put(Contract.String_Entry.COLUMN_ANSWER, cursor.getLong(1));
                    contentValues.put(Contract.String_Entry.COLUMN_RANK, cursor.getInt(2));
                    contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, cursor.getInt(3));
                    if (number_of_everyday_entries > Constants.NOT_EMULATED) {
                        contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                Emulation.getEmulationCreationDate(to_field_id, number_of_everyday_entries, sqLiteDatabase));
                    }
                    sqLiteDatabase.insertWithOnConflict(Contract.String_Entry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return (null);
        }
    }

    public void moveEntries(ArrayList<Long> list_of_entries, long to_field_id) {
        buildMoveEntries(this).execute(sqLiteOpenHelper, list_of_entries, to_field_id, context);
    }

    protected ContentChangingTask buildMoveEntries(CursorLoaderAndUpdateDatabase loader) {
        return (new MoveEntries(loader));
    }


    protected static class MoveEntries extends ContentChangingTask {
        MoveEntries(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            ArrayList<Long> list_of_entries = (ArrayList<Long>) params[1];
            long to_field_id = (long) params[2];
            Context context = (Context) params[3];
            int number_of_everyday_entries = new DatabaseReadOperations(context).isEmulated(to_field_id);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransaction();
            try {
                for (long entry_id : list_of_entries) {
                    Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                            new String[]{Contract.String_Entry.COLUMN_QUESTION, Contract.String_Entry.COLUMN_ANSWER,
                                    Contract.String_Entry.COLUMN_RANK, Contract.String_Entry.COLUMN_ORDER_OF_ENTRY},
                            Contract.COLUMN_ID + " = ?",
                            new String[]{entry_id + ""}, null, null, null, null);
                    cursor.moveToFirst();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_FIELD, to_field_id);
                    contentValues.put(Contract.String_Entry.COLUMN_QUESTION, cursor.getLong(0));
                    contentValues.put(Contract.String_Entry.COLUMN_ANSWER, cursor.getLong(1));
                    contentValues.put(Contract.String_Entry.COLUMN_RANK, cursor.getInt(2));
                    contentValues.put(Contract.String_Entry.COLUMN_ORDER_OF_ENTRY, cursor.getInt(3));
                    if (number_of_everyday_entries > Constants.NOT_EMULATED) {
                        contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                Emulation.getEmulationCreationDate(to_field_id, number_of_everyday_entries, sqLiteDatabase));
                    }
                    sqLiteDatabase.insertWithOnConflict(Contract.String_Entry.TABLE_NAME, null,
                            contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                    sqLiteDatabase.delete(Contract.String_Entry.TABLE_NAME, Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return (null);
        }
    }

    public void updateFieldAnswerAreaSize(long field_id, int question_area_size) {
        buildUpdateFieldAnswerAreaSize(this).execute(sqLiteOpenHelper, field_id, question_area_size);
    }

    protected ContentChangingTask buildUpdateFieldAnswerAreaSize(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateFieldAnswerAreaSize(loader));
    }


    protected static class UpdateFieldAnswerAreaSize extends ContentChangingTask {
        UpdateFieldAnswerAreaSize(CursorLoaderAndUpdateDatabase loader) {
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
        buildUpdateFieldTestTextSize(this).execute(sqLiteOpenHelper, field_id, test_text_size);
    }

    protected ContentChangingTask buildUpdateFieldTestTextSize(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateFieldTestTextSize(loader));
    }


    protected static class UpdateFieldTestTextSize extends ContentChangingTask {
        UpdateFieldTestTextSize(CursorLoaderAndUpdateDatabase loader) {
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
        buildUpdateFieldActionBarShown(this).execute(sqLiteOpenHelper, field_id, action_bar_shown);
    }

    protected ContentChangingTask buildUpdateFieldActionBarShown(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateFieldActionBarShown(loader));
    }


    protected static class UpdateFieldActionBarShown extends ContentChangingTask {
        UpdateFieldActionBarShown(CursorLoaderAndUpdateDatabase loader) {
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
        IncrementFieldUsageTask(CursorLoaderAndUpdateDatabase loader) {
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
        DeleteEntryTask(CursorLoaderAndUpdateDatabase loader) {
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



    public void resetEntriesTestOrderEnhancedTest(long field_id, int question_data_type) {
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void resetEntriesTestOrderFullyRandomTest(long field_id, int question_data_type) {
        if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER, -1);
            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{field_id + ""});
        } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
        }
    }

    public void deleteField(final long field_id, final Handler handler, final MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder) {
        Thread pauseThread = new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Cursor cursor = sqLiteDatabase.query(Contract.Scheduled_Time_Backup.TABLE_NAME,
                    new String[]{Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST},
                    Contract.Scheduled_Time_Backup.COLUMN_FIELD + " = ? ", new String[]{field_id + ""}, null, null, null);
            if (cursor.getCount() > 0) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
                    Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, cursor.getInt(0), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
            cursor.close();
            cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST},
                    Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " IS NOT NULL"
                    , new String[]{field_id + ""}, null, null, null);
            if (cursor.getCount() > 0) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
                    Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, cursor.getInt(0), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
            cursor.close();
            sqLiteDatabase.delete(Contract.Field.TABLE_NAME,
                    Contract.COLUMN_ID + " = ? ",
                    new String[]{field_id + ""});

            Runnable runnable = () -> {
                CursorLoaderAndUpdateDatabase.this.onContentChanged();
                mainActivityRecyclerViewViewHolder.isDeleting = false;
            };
            handler.post(runnable);

        });
        pauseThread.start();
    }

    public void cleanDatabase() {
        cleanSTringTable(sqLiteDatabase);

        sqLiteDatabase.execSQL("DELETE FROM " + Contract.Not_String.TABLE_NAME + " WHERE " + Contract.COLUMN_ID
                + " IN ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.Not_String.TABLE_NAME + " EXCEPT SELECT "
                + Contract.Not_String_Entry.COLUMN_QUESTION + " FROM " + Contract.Not_String_Entry.TABLE_NAME + " );");
    }





    public void clearThisFieldFromTestPausedEnhancedTest(long field_id) {
        sqLiteDatabase.delete(Contract.Test_Paused.TABLE_NAME,
                Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.ENHANCED_TEST + ""});
    }

    public void clearThisFieldFromTestPausedFullyRandomTest(long field_id) {
        sqLiteDatabase.delete(Contract.Test_Paused.TABLE_NAME,
                Contract.Test_Paused.COLUMN_FIELD + " = ? AND " + Contract.Test_Paused.COLUMN_TEST_TYPE + " = ? ",
                new String[]{field_id + "", Constants.TestType.FULLY_RANDOM_TEST + ""});
    }


    public void clearThisFieldFromWrongAnswersEnhancedTest(long field_id) {
        buildClearThisFieldFromWrongAnswersEnhancedTest(this).execute(sqLiteOpenHelper, field_id, context);
    }

    protected ContentChangingTask buildClearThisFieldFromWrongAnswersEnhancedTest(CursorLoaderAndUpdateDatabase loader) {
        return (new ClearThisFieldFromWrongAnswersEnhancedTest(loader));
    }

    protected static class ClearThisFieldFromWrongAnswersEnhancedTest extends ContentChangingTask {
        ClearThisFieldFromWrongAnswersEnhancedTest(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            Context method_context = (Context) params[2];
            int question_data_type = new DatabaseReadOperations(method_context).getQuestionDataType(field_id);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.String_Entry.TABLE_NAME
                            + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = " + field_id + " )"
                            + " AND " + Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE + " = "
                            + Constants.TestType.ENHANCED_TEST + ";");
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_Not_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_Not_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                            + " WHERE " + Contract.Not_String_Entry.COLUMN_FIELD + " = " + field_id + " )"
                            + " AND " + Contract.Test_Wrong_Answer_Not_String.COLUMN_TEST_TYPE + " = "
                            + Constants.TestType.ENHANCED_TEST + ";");
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void clearThisFieldFromWrongAnswersFullyRandomTest(long field_id) {
        buildClearThisFieldFromWrongAnswersFullyRandomTest(this).execute(sqLiteOpenHelper, field_id, context);
    }

    protected ContentChangingTask buildClearThisFieldFromWrongAnswersFullyRandomTest(CursorLoaderAndUpdateDatabase loader) {
        return (new ClearThisFieldFromWrongAnswersFullyRandomTest(loader));
    }

    protected static class ClearThisFieldFromWrongAnswersFullyRandomTest extends ContentChangingTask {
        ClearThisFieldFromWrongAnswersFullyRandomTest(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            long field_id = (long) params[1];
            Context method_context = (Context) params[2];
            int question_data_type = new DatabaseReadOperations(method_context).getQuestionDataType(field_id);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.String_Entry.TABLE_NAME
                            + " WHERE " + Contract.String_Entry.COLUMN_FIELD + " = " + field_id + " )"
                            + " AND " + Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE + " = "
                            + Constants.TestType.FULLY_RANDOM_TEST + ";");
                } else if (question_data_type == Contract.Field.NOT_STRING_DATA_TYPE) {
                    sqLiteDatabase.execSQL("DELETE FROM " + Contract.Test_Wrong_Answer_Not_String.TABLE_NAME
                            + " WHERE " + Contract.Test_Wrong_Answer_Not_String.COLUMN_ENTRY + " IN "
                            + " ( SELECT " + Contract.COLUMN_ID + " FROM " + Contract.Not_String_Entry.TABLE_NAME
                            + " WHERE " + Contract.Not_String_Entry.COLUMN_FIELD + " = " + field_id + " )"
                            + " AND " + Contract.Test_Wrong_Answer_Not_String.COLUMN_TEST_TYPE + " = "
                            + Constants.TestType.FULLY_RANDOM_TEST + ";");
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
            return null;
        }
    }

    public void putEntryWrongAnswersEnhancedTest(long entry_id, ArrayList<String> wrongAnswer, int question_data_type) {
        buildPutEntryWrongAnswersEnhancedTest(this).execute(sqLiteOpenHelper, entry_id, wrongAnswer, question_data_type);
    }

    protected ContentChangingTask buildPutEntryWrongAnswersEnhancedTest(CursorLoaderAndUpdateDatabase loader) {
        return (new PutEntryWrongAnswersEnhancedTest(loader));
    }

    protected static class PutEntryWrongAnswersEnhancedTest extends ContentChangingTask {
        PutEntryWrongAnswersEnhancedTest(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long entry_id = (long) params[1];
            ArrayList<String> wrongAnswers = (ArrayList<String>) params[2];
            int question_data_type = (int) params[3];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    for (String string : wrongAnswers) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE, Constants.TestType.ENHANCED_TEST);
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

    public void putEntryWrongAnswersFullyRandomTest(long entry_id, ArrayList<String> wrongAnswer, int question_data_type) {
        buildPutEntryWrongAnswersFullyRandomTest(this).execute(sqLiteOpenHelper, entry_id, wrongAnswer, question_data_type);
    }

    protected ContentChangingTask buildPutEntryWrongAnswersFullyRandomTest(CursorLoaderAndUpdateDatabase loader) {
        return (new PutEntryWrongAnswersFullyRandomTest(loader));
    }

    protected static class PutEntryWrongAnswersFullyRandomTest extends ContentChangingTask {
        PutEntryWrongAnswersFullyRandomTest(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long entry_id = (long) params[1];
            ArrayList<String> wrongAnswers = (ArrayList<String>) params[2];
            int question_data_type = (int) params[3];
            sqLiteDatabase.beginTransactionNonExclusive();
            try {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    for (String string : wrongAnswers) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.Test_Wrong_Answer_String.COLUMN_TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_FIELD, field_id);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE, session_type);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_TIME, time);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST, pendingintent_request);
        sqLiteDatabase.insert(Contract.Scheduled_Time_Backup.TABLE_NAME, null, contentValues);
    }

    public void addScheduledTimeForBackup(long field_id, int session_type, long time,
                                          int pendingintent_request, int percentage_or_number, int number, int number_of_tries,
                                          int controlled_portion, int wrongness_level, int rank_level) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_FIELD, field_id);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE, session_type);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_TIME, time);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST, pendingintent_request);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE, percentage_or_number);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER, number);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES, number_of_tries);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_CONTROLLED_PORTION, controlled_portion);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_WRONGNESS_LEVEL, wrongness_level);
        contentValues.put(Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_RANK_LEVEL, rank_level);
        sqLiteDatabase.insert(Contract.Scheduled_Time_Backup.TABLE_NAME, null, contentValues);
    }

    public void deleteScheduledTimeForBackup(long field_id, int session_type, long time) {
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

    public void increaseEntryAskedByOne(long entry_id) {
        Cursor cursor = sqLiteOpenHelper.getWritableDatabase().query(Contract.String_Entry.TABLE_NAME,
                new String[]{Contract.String_Entry.COLUMN_ASKED_COUNT}, Contract.COLUMN_ID + " = ? ",
                new String[]{entry_id + ""}, null, null, null);
        cursor.moveToFirst();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.String_Entry.COLUMN_ASKED_COUNT, cursor.getInt(0) + 1);
        sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
    }

    public void increaseEntryWrongAnsweredCountByOne(long entry_id) {
        Cursor cursor = sqLiteOpenHelper.getWritableDatabase().query(Contract.String_Entry.TABLE_NAME,
                new String[]{Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT}, Contract.COLUMN_ID + " = ? ",
                new String[]{entry_id + ""}, null, null, null);
        cursor.moveToFirst();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT, cursor.getInt(0) + 1);
        sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
    }

    public void setReminderData(long entry_id, long time, int request) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.String_Entry.COLUMN_REMIND_AT, time);
        contentValues.put(Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST, request);
        sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
    }

    public void deleteReminderData(long entry_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.putNull(Contract.String_Entry.COLUMN_REMIND_AT);
        contentValues.putNull(Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST);
        sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{entry_id + ""});
    }

    protected ContentChangingTask buildAddFieldNoteTask(CursorLoaderAndUpdateDatabase loader) {
        return (new AddFieldNote(loader));
    }

    public void addFieldNote(long field_id, String note) {
        buildAddFieldNoteTask(this).execute(sqLiteOpenHelper, field_id, note);
    }

    protected static class AddFieldNote extends ContentChangingTask {
        AddFieldNote(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long field_id = (long) params[1];
            String note = (String) params[2];
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field_Note.COLUMN_FIELD, field_id);
            contentValues.put(Contract.Field_Note.COLUMN_NOTE, note);
            sqLiteDatabase.insertWithOnConflict(Contract.Field_Note.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            return null;
        }
    }

    protected ContentChangingTask buildDeleteFieldNoteTask(CursorLoaderAndUpdateDatabase loader) {
        return (new DeleteFieldNote(loader));
    }

    public void deleteFieldNote(long note_id) {
        buildDeleteFieldNoteTask(this).execute(sqLiteOpenHelper, note_id);
    }

    protected static class DeleteFieldNote extends ContentChangingTask {
        DeleteFieldNote(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long note_id = (long) params[1];
            sqLiteDatabase.delete(Contract.Field_Note.TABLE_NAME, Contract.COLUMN_ID + " = ?", new String[]{note_id + ""});
            return null;
        }
    }

    protected ContentChangingTask buildUpdateCustomableText(CursorLoaderAndUpdateDatabase loader) {
        return (new UpdateCustomableText(loader));
    }

    public void updateCustomableText(long id, String text) {
        buildUpdateCustomableText(this).execute(sqLiteOpenHelper, id, text);
    }

    protected static class UpdateCustomableText extends ContentChangingTask {
        UpdateCustomableText(CursorLoaderAndUpdateDatabase loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db = (SQLiteOpenHelper) params[0];
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            long id = (long) params[1];
            String text = (String) params[2];
            ContentValues contentValues = new ContentValues();
            if ( text != null ) {
                contentValues.put(Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING, text);
            } else {
                contentValues.putNull(Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING);
            }
            sqLiteDatabase.update(Contract.Field_Type_Symbol.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{id + ""});
            return null;
        }
    }

    public void updateFieldType(long field_id, int field_type) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Field.COLUMN_FIELD_TYPE, field_type);
            sqLiteOpenHelper.getWritableDatabase().update(Contract.Field.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});
    }

    public void addOrderId(String order_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.GooglePaymentOrderID.COLUMN_ORDER_ID, order_id);
        sqLiteOpenHelper.getWritableDatabase().insert(Contract.GooglePaymentOrderID.TABLE_NAME, null, contentValues);
    }

    public void restWhetherAskedCurrentTestRound(long field_id, boolean isEmulated, String date_string) {
        if (isEmulated) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, Constants.TestRound.DID_NOT_ASKED_AT_CURRENT_ROUND);
            sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? AND ( ? > " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                            + " OR " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NULL )"
                    , new String[]{field_id + "", date_string});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, Constants.TestRound.DID_NOT_ASKED_AT_CURRENT_ROUND);
            sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{String.valueOf(field_id)});
        }
    }

    public void makeAskedCurrentTestRound(long[] ids) {
        for (long temp : ids) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND, Constants.TestRound.ASKED_AT_CURRENT_ROUND);
            sqLiteOpenHelper.getWritableDatabase().update(Contract.String_Entry.TABLE_NAME, contentValues,
                    Contract.COLUMN_ID + " = ? ", new String[]{temp + ""});
        }
    }

    public void setTimeOfAnswerOfTests(long field_id, int reading_question_letter_second, int finding_answer_time,
                                       int typing_answer_letter_second, int do_what) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_TESTS, reading_question_letter_second);
        contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_TESTS, finding_answer_time);
        contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_TESTS, typing_answer_letter_second);
        contentValues.put(Contract.Field.COLUMN_TIME_OF_ANSWER_TESTS_DO_WHAT, do_what);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
    }

    public void setTimeOfAnswerOfStudyAPeriod(long field_id, int reading_question_letter_second, int finding_answer_time,
                                              int typing_answer_letter_second) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_READING_QUESTION_LETTER_SECOND_STUDY_A_PERIOD, reading_question_letter_second);
        contentValues.put(Contract.Field.COLUMN_FIND_ANSWER_TIME_STUDY_A_PERIOD, finding_answer_time);
        contentValues.put(Contract.Field.COLUMN_TYPING_ANSWER_LETTER_SECOND_STUDY_A_PERIOD, typing_answer_letter_second);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
    }

    public void setElapsedPausedSessionTime(long field_id, int type, long time) {
        ContentValues contentValues = new ContentValues();
        if (type == Constants.ElapsedPausedSessionTime.STUDY_A_PERIOD) {
            contentValues.put(Contract.Field.COLUMN_ELAPSED_TIME_OF_PAUSED_STUDY_A_PERIOD_SESSION, time);
        } else if (type == Constants.ElapsedPausedSessionTime.ENHANCED_TEST) {
            contentValues.put(Contract.Field.COLUMN_ELAPSED_TIME_OF_PAUSED_ENHANCED_TEST_SESSION, time);
        } else if (type == Constants.ElapsedPausedSessionTime.FULLY_RANDOM_TEST) {
            contentValues.put(Contract.Field.COLUMN_ELAPSED_TIME_OF_PAUSED_FULLY_RANDOM_TEST_SESSION, time);
        }
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
    }
    public void setObfuscateQuestion(long field_id, boolean obfuscate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_OBFUSCATE_QUESTION, obfuscate ? 1 : 0);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
    }
}
