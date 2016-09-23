package com.ahmedsameha1.peninbin.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by me on 22/09/16.
 */
public class Emulation {

    private static Thread emulate_thread, unemulate_thread;

    public static void startEmulateThread(final Context context, final long field_id, final int number_of_entries) {
        emulate_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                emulate(context, field_id, number_of_entries );
            }
        });
        emulate_thread.start();
    }



    public static void startEmulateThread(final Context context, final long field_id, final int number_of_entries, final long starting_entry ) {
        emulate_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                emulate(context, field_id, number_of_entries, starting_entry );
            }
        });

        emulate_thread.start();
    }

    public static void startUnemulateThread(final Context context, final long field_id ) {
        unemulate_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                disableEmulation(context, field_id);
            }
        });
        emulate_thread.start();
    }

    private static void partition(int number_of_everyday_entries, Cursor cursor, SQLiteDatabase sqLiteDatabase) {
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

    private static void partition(int number_of_everyday_entries, ArrayList<Long> list_of_entries , SQLiteDatabase sqLiteDatabase) {
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

    private static void emulate(final Context context, final long field_id, final int number_of_entries) {
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, number_of_entries);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                        Contract.COLUMN_ID + " = ? ", new String[] {field_id + ""});
                Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[] {field_id + ""},null,null,
                        Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                                + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
                Emulation.partition(number_of_entries, cursor, sqLiteDatabase);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBuilder = null;
                Intent notifyIntent = null;
                PendingIntent resultPendingIntent = null;
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.emulation_completed))
                                .setContentText(context.getResources().getString(R.string.emulation_has_completed))
                                .setTicker(context.getResources().getString(R.string.emulation_has_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, FieldActivity.class);
                notifyIntent.putExtra(FieldActivity.FIELD_ID, field_id);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(0, notificationBuilder.build());

    }

    private static void emulate(final Context context, final long field_id, final int number_of_entries, final long starting_entry ) {
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, number_of_entries);
                sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                        Contract.COLUMN_ID + " = ? ", new String[] {field_id + ""});
                Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                        Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[] {field_id + ""},null,null,
                        Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                                + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
                ArrayList<Long> list_of_entries_from_starting_entry = new ArrayList<>();
                boolean start = false;
                for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                     cursor.moveToNext()) {
                    if ( cursor.getLong(0) == starting_entry ) {
                        start = true;
                    }
                    if ( start ) {
                        list_of_entries_from_starting_entry.add(cursor.getLong(0));
                    }
                }
                Emulation.partition(number_of_entries, list_of_entries_from_starting_entry, sqLiteDatabase);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBuilder = null;
                Intent notifyIntent = null;
                PendingIntent resultPendingIntent = null;
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.emulation_completed))
                                .setContentText(context.getResources().getString(R.string.emulation_has_completed))
                                .setTicker(context.getResources().getString(R.string.emulation_has_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, FieldActivity.class);
                notifyIntent.putExtra(FieldActivity.FIELD_ID, field_id);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(0, notificationBuilder.build());

    }

    private static void disableEmulation(Context context,final long field_id) {
        final SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, Constants.NOT_EMULATED);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[] {field_id + ""});

                ContentValues contentValues1 = new ContentValues();
                contentValues1.putNull(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues1,
                        Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[] {field_id + ""});
    }

    public static boolean isAnyThreadRunning () {
        if ( (emulate_thread == null || !emulate_thread.isAlive() && ( unemulate_thread == null || !unemulate_thread.isAlive() )) ){
            return false;
        }
        return true;
    }
}
