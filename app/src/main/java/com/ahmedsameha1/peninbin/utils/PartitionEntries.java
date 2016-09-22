package com.ahmedsameha1.peninbin.utils;

import android.app.AlarmManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by me on 22/09/16.
 */
public class PartitionEntries {

    public static void partition (int number_of_everyday_entries, Cursor cursor, SQLiteDatabase sqLiteDatabase ) {
        long time_at_start = System.currentTimeMillis();
        boolean at_start_of_day = time_at_start % AlarmManager.INTERVAL_DAY == 0;
        long emulated_creation_time_millis = at_start_of_day ? time_at_start + 10 :
                time_at_start + (AlarmManager.INTERVAL_DAY - (time_at_start % AlarmManager.INTERVAL_DAY) ) + 10;
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTimeInMillis(emulated_creation_time_millis);
        String emulated_creation_time_string = new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm", Locale.US).format(calendar.getTime());
        int number_of_days = cursor.getCount() / number_of_everyday_entries;
        if ( number_of_days <= 1 ) {
            for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                 cursor.moveToNext()) {

            }
        } else {
            outter:
            for ( int i = 0; i < number_of_days; i++ ) {
                for ( int k = 0; k < number_of_everyday_entries; k++ ) {
                    cursor.moveToNext();
                    if ( cursor.isAfterLast() ) {
                        break outter;
                    }
                }
            }
        }


    }

}
