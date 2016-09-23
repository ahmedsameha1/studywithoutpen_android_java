package com.ahmedsameha1.peninbin.utils;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ahmedsameha1.peninbin.database.Contract;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by me on 22/09/16.
 */
public class PartitionEntries {

    public static void partition(int number_of_everyday_entries, Cursor cursor, SQLiteDatabase sqLiteDatabase) {
        long time_at_start = System.currentTimeMillis();
        boolean at_start_of_day = time_at_start % AlarmManager.INTERVAL_DAY == 0;
        long emulated_creation_time_millis = at_start_of_day ? time_at_start + 10 :
                time_at_start + (AlarmManager.INTERVAL_DAY - (time_at_start % AlarmManager.INTERVAL_DAY)) + 10;
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTimeInMillis(emulated_creation_time_millis);
        String emulated_creation_time_string = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
        int number_of_days = cursor.getCount() / number_of_everyday_entries;
        sqLiteDatabase.beginTransaction();
        try {
            if (number_of_days <= 1) {
                for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                     cursor.moveToNext()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, emulated_creation_time_string);
                    sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                            Contract.COLUMN_ID + " = ? ", new String[]{cursor.getLong(0) + ""});
                }
            } else {
                outter:
                for (int i = 0; i < number_of_days; i++) {
                    if (i >= 1) {
                        emulated_creation_time_millis += AlarmManager.INTERVAL_DAY;
                        calendar.setTimeInMillis(emulated_creation_time_millis);
                        emulated_creation_time_string = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
                    }
                    for (int k = 0; k < number_of_everyday_entries; k++) {
                        if (i == 0 && k == 0) {
                            cursor.moveToFirst();
                        } else {
                            cursor.moveToNext();
                        }
                        if (cursor.isAfterLast()) {
                            break outter;
                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, emulated_creation_time_string);
                        sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                                Contract.COLUMN_ID + " = ? ", new String[]{cursor.getLong(0) + ""});
                    }
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    public static void partition(int number_of_everyday_entries, ArrayList<Long> list_of_entries , SQLiteDatabase sqLiteDatabase) {
        long time_at_start = System.currentTimeMillis();
        boolean at_start_of_day = time_at_start % AlarmManager.INTERVAL_DAY == 0;
        long emulated_creation_time_millis = at_start_of_day ? time_at_start + 10 :
                time_at_start + (AlarmManager.INTERVAL_DAY - (time_at_start % AlarmManager.INTERVAL_DAY)) + 10;
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTimeInMillis(emulated_creation_time_millis);
        String emulated_creation_time_string = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
        int number_of_days = list_of_entries.size() / number_of_everyday_entries;
        sqLiteDatabase.beginTransaction();
        try {
            if (number_of_days <= 1) {
                for ( Long temp : list_of_entries ) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, emulated_creation_time_string);
                    sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                            Contract.COLUMN_ID + " = ? ", new String[]{temp + ""});
                }
            } else {
                int counter = -1;
                outter:
                for (int i = 0; i < number_of_days; i++) {
                    if (i >= 1) {
                        emulated_creation_time_millis += AlarmManager.INTERVAL_DAY;
                        calendar.setTimeInMillis(emulated_creation_time_millis);
                        emulated_creation_time_string = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
                    }
                    for (int k = 0; k < number_of_everyday_entries; k++) {
                        if ( ++counter > list_of_entries.size() - 1 ) {
                            break outter;
                        } else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, emulated_creation_time_string);
                            sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                                    Contract.COLUMN_ID + " = ? ", new String[]{list_of_entries.get(counter) + ""});
                        }
                    }
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

   public static String getEmulationCreationDate ( long field_id, int number_of_everyday_entries, SQLiteDatabase sqLiteDatabase ) {
       Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
               new String[] {Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE},
               Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NOT NULL",
               new String[] { field_id + ""}, null, null, "datetime(" + Contract.CREATION_DATE + ") DESC");
       int count = cursor.getCount();
       String date_string = null;
       if ( count > 0 ) {
           cursor.moveToFirst();
           date_string = cursor.getString(0);
           int remaining_this_day = count % number_of_everyday_entries;
           if ( remaining_this_day == 0 ) {
               Calendar calendar = Calendar.getInstance(Locale.US);
               long date_millis = Timestamp.valueOf(date_string).getTime() + AlarmManager.INTERVAL_DAY;
               calendar.setTimeInMillis(date_millis);
               return new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
           } else {
               return date_string;
           }
       } else {
           Calendar calendar = Calendar.getInstance(Locale.US);
           calendar.setTimeInMillis(System.currentTimeMillis());
           return new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.US).format(calendar.getTime());
       }
   }
}
