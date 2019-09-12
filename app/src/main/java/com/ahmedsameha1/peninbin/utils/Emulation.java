package com.ahmedsameha1.peninbin.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.FieldActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.intent_services.EnableEmulationIntentService;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.ahmedsameha1.peninbin.utils.Constants.CHANNEL_ID;

/**
 * Created by me on 22/09/16.
 */
public class Emulation {

    public static final String DATE_FORMAT_SQLITE = "yyyy-MM-dd HH:mm:ss";

    private static boolean reemulate_is_running = false;
    private static boolean unemulate_is_running = false;


    public static void startReemulateThread(final Context context) {
        reemulate_is_running = true;
        repartition(context);
        reemulate_is_running = false;
    }

    public static void startUnemulateThread(final Context context, final long field_id) {
        unemulate_is_running = true;
        disableEmulation(context, field_id);
        unemulate_is_running = false;
    }

    private static void partition(int number_of_everyday_entries, Cursor cursor, boolean[] what_days, SQLiteDatabase sqLiteDatabase) {
        ArrayList<Integer> not_days = new ArrayList<>(6);
        for ( int i = 0; i < what_days.length; i++ ) {
            if ( !what_days[i] ) {
                not_days.add(i + 1);
            }
        }
        Calendar calendar = Calendar.getInstance();
        long long_utc = System.currentTimeMillis();
        calendar.setTimeInMillis(long_utc);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);
        if ( not_days.size() <= 0 ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
            String emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
            int number_of_days = cursor.getCount() / number_of_everyday_entries;
            if (cursor.getCount() % number_of_everyday_entries > 0) {
                number_of_days++;
            }
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
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
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
        } else {
            do {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } while ( not_days.contains(calendar.get(Calendar.DAY_OF_WEEK)) );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
            String emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
            int number_of_days = cursor.getCount() / number_of_everyday_entries;
            if (cursor.getCount() % number_of_everyday_entries > 0) {
                number_of_days++;
            }
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
                            do {
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                            } while ( not_days.contains(calendar.get(Calendar.DAY_OF_WEEK)) );
                            emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
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
    }

    private static void partition(int number_of_everyday_entries, ArrayList<Long> list_of_entries, boolean[] what_days, SQLiteDatabase sqLiteDatabase) {
        ArrayList<Integer> not_days = new ArrayList<>(6);
        for ( int i = 0; i < what_days.length; i++ ) {
            if ( !what_days[i] ) {
                not_days.add(i + 1);
            }
        }
        Calendar calendar = Calendar.getInstance();
        long long_utc = System.currentTimeMillis();
        calendar.setTimeInMillis(long_utc);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);
        if ( not_days.size() <= 0 ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
            String emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
            int number_of_days = list_of_entries.size() / number_of_everyday_entries;
            if (list_of_entries.size() % number_of_everyday_entries > 0) {
                number_of_days++;
            }
            sqLiteDatabase.beginTransaction();
            try {
                if (number_of_days <= 1) {
                    for (Long temp : list_of_entries) {
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
                      /*  emulated_creation_time_millis += AlarmManager.INTERVAL_DAY;
                        date = new Date(emulated_creation_time_millis);*/
//                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
                        }
                        for (int k = 0; k < number_of_everyday_entries; k++) {
                            if (++counter > list_of_entries.size() - 1) {
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
        } else {
            do {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } while ( not_days.contains(calendar.get(Calendar.DAY_OF_WEEK)) );
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
            String emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
            int number_of_days = list_of_entries.size() / number_of_everyday_entries;
            if (list_of_entries.size() % number_of_everyday_entries > 0) {
                number_of_days++;
            }
            sqLiteDatabase.beginTransaction();
            try {
                if (number_of_days <= 1) {
                    for (Long temp : list_of_entries) {
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
                            do {
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                            } while ( not_days.contains(calendar.get(Calendar.DAY_OF_WEEK)) );
                            emulated_creation_time_string = simpleDateFormat.format(calendar.getTime());
                        }
                        for (int k = 0; k < number_of_everyday_entries; k++) {
                            if (++counter > list_of_entries.size() - 1) {
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
    }
    public static String getEmulationCreationDate(long field_id, int number_of_everyday_entries, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME, new String[]{Contract.Field.COLUMN_CREATION_EMULATION_WHAT_DAYS},
                Contract.COLUMN_ID + " = ? ", new String[] {String.valueOf(field_id)}, null, null, null);
        cursor.moveToFirst();
        String what_days = cursor.getString(0);
        cursor.close();
        ArrayList<Integer> not_days = new ArrayList<>(6);
        for ( int i = 0; i < what_days.length(); i++ ) {
            if ( what_days.charAt(i) == '0' ) {
                not_days.add(i + 1);
            }
        }
        Calendar calendar_1 = Calendar.getInstance();
        long long_utc = System.currentTimeMillis();
        calendar_1.setTimeInMillis(long_utc);
        calendar_1.set(Calendar.HOUR_OF_DAY, 0);
        calendar_1.set(Calendar.MINUTE, 5);
        calendar_1.set(Calendar.SECOND, 0);
        if ( not_days.size() <= 0 ) {
            calendar_1.add(Calendar.DAY_OF_MONTH, 1);
        } else {
            do {
                calendar_1.add(Calendar.DAY_OF_MONTH, 1);
            } while (not_days.contains(calendar_1.get(Calendar.DAY_OF_WEEK)));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
        cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                new String[]{Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE},
                Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NOT NULL",
                new String[]{String.valueOf(field_id)}, null, null, "datetime(" + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + ") DESC");
        int count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            String date_string = cursor.getString(0);
            cursor.close();
            Cursor cursor1 = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE},
                    Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " = ? ", new String[]{date_string}, null, null, null);
            int number_of_entries_last_date = cursor1.getCount();
            cursor1.close();
            long date_millis = Timestamp.valueOf(date_string).getTime();
            long now_millis = System.currentTimeMillis();
            Calendar calendar_date = Calendar.getInstance(Locale.getDefault());
            calendar_date.setTimeInMillis(date_millis);
            Calendar calendar_now = Calendar.getInstance(Locale.getDefault());
            calendar_now.setTimeInMillis(now_millis);
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            int remaining_this_day = number_of_everyday_entries - number_of_entries_last_date;
            Date utc_date_of_date_string = null;
            try {
                utc_date_of_date_string = simpleDateFormat.parse(date_string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar_2 = Calendar.getInstance();
            calendar_2.setTime(utc_date_of_date_string);
            if ( not_days.size() <= 0 ) {
                calendar_2.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                do {
                    calendar_2.add(Calendar.DAY_OF_MONTH, 1);
                } while (not_days.contains(calendar_2.get(Calendar.DAY_OF_WEEK)));
            }
            if (now_millis >= date_millis) {
                if (fmt.format(calendar_date.getTime()).equals(fmt.format(calendar_now.getTime()))) {     // in the same day
                    if (remaining_this_day <= 0) {
                        return simpleDateFormat.format(calendar_2.getTime());
                    } else {
                        return date_string;
                    }
                } else {
                    return simpleDateFormat.format(calendar_1.getTime());
                }
            } else {
                if (remaining_this_day <= 0) {
                    return simpleDateFormat.format(calendar_2.getTime());
                } else {
                    return date_string;
                }
            }
        } else {
            return simpleDateFormat.format(calendar_1.getTime());
        }
    }

    public static void emulate(final Context context, final long field_id, final int number_of_entries, final boolean[] week_days_case) {
        String emulation_days_string = "";
        for ( boolean bool: week_days_case ) {
            emulation_days_string = bool ? emulation_days_string + 1 : emulation_days_string + 0;
        }
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, number_of_entries);
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_WHAT_DAYS, emulation_days_string);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{field_id + ""}, null, null,
                Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                        + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
        Emulation.partition(number_of_entries, cursor, week_days_case, sqLiteDatabase);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        notificationBuilder =
                new NotificationCompat.Builder(context, IntentServicesUtility.createNotificationChannel(context))
                        .setSmallIcon(R.drawable.ic_launcher48)
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
                        (int) System.currentTimeMillis(),
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, notificationBuilder.build());

    }

    public static void emulate(final Context context, final long field_id, final int number_of_entries, final long starting_entry, final boolean[] week_days_case) {
        String emulation_days_string = "";
        for ( boolean bool: week_days_case ) {
            emulation_days_string = bool ? emulation_days_string + 1 : emulation_days_string + 0;
        }
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, number_of_entries);
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_WHAT_DAYS, emulation_days_string);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{String.valueOf(field_id)});
        Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID},
                Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{field_id + ""}, null, null,
                Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                        + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
        ArrayList<Long> list_of_entries_from_starting_entry = new ArrayList<>();
        boolean start = false;
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            if (cursor.getLong(0) == starting_entry) {
                start = true;
            }
            if (start) {
                list_of_entries_from_starting_entry.add(cursor.getLong(0));
            }
        }
        cursor.close();
        Emulation.partition(number_of_entries, list_of_entries_from_starting_entry, week_days_case, sqLiteDatabase);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        notificationBuilder =
                new NotificationCompat.Builder(context, IntentServicesUtility.createNotificationChannel(context))
                        .setSmallIcon(R.drawable.ic_launcher48)
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
                        (int) System.currentTimeMillis(),
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, notificationBuilder.build());

    }

    private static void disableEmulation(Context context, final long field_id) {
        final SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, Constants.NOT_EMULATED);
        contentValues.putNull(Contract.Field.COLUMN_CREATION_EMULATION_WHAT_DAYS);
        sqLiteDatabase.update(Contract.Field.TABLE_NAME, contentValues,
                Contract.COLUMN_ID + " = ? ", new String[]{field_id + ""});

        ContentValues contentValues1 = new ContentValues();
        contentValues1.putNull(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE);
        sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues1,
                Contract.String_Entry.COLUMN_FIELD + " = ? ", new String[]{field_id + ""});
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        notificationBuilder =
                new NotificationCompat.Builder(context, IntentServicesUtility.createNotificationChannel(context))
                        .setSmallIcon(R.drawable.ic_launcher48)
                        .setContentTitle(context.getResources().getString(R.string.emulation_disabled))
                        .setContentText(context.getResources().getString(R.string.emulation_has_disabled))
                        .setTicker(context.getResources().getString(R.string.emulation_has_disabled))
                        .setAutoCancel(true);
        notifyIntent = new Intent(context, FieldActivity.class);
        notifyIntent.putExtra(FieldActivity.FIELD_ID, field_id);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        (int) System.currentTimeMillis(),
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public static boolean isAnyThreadRunning() {
        if ( !EnableEmulationIntentService.is_running
                && !unemulate_is_running
                && !reemulate_is_running ) {
            return false;
        }
        return true;
    }

    public static void modifyEmulationCreationDatesOfAppV53AndHigher(SQLiteDatabase sqLiteDatabase) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
        sqLiteDatabase.beginTransaction();
        try {
            Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME,
                    new String[]{Contract.COLUMN_ID, Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE},
                    Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NOT NULL", null, null, null, null);
            for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                 cursor.moveToNext()) {
                String date_string = cursor.getString(1);
                Date utc_date_of_date_string = null;
                try {
                    utc_date_of_date_string = simpleDateFormat.parse(date_string);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar_2 = Calendar.getInstance();
                calendar_2.setTime(utc_date_of_date_string);
                calendar_2.set(Calendar.HOUR_OF_DAY, 0);
                calendar_2.set(Calendar.MINUTE, 5);
                calendar_2.set(Calendar.SECOND, 0);
                String modified_date_string = simpleDateFormat.format(calendar_2.getTime());
                ContentValues contentValues = new ContentValues();
                contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, modified_date_string);
                sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues, Contract.COLUMN_ID + " = ? ", new String[]{cursor.getLong(0) +""});
            }
            cursor.close();
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    private static void repartition( Context context ) {
        SQLiteDatabase sqLiteDatabase = DatabaseHelper.getInstance(context).getWritableDatabase();
        Calendar today_calendar = Calendar.getInstance();
        long long_utc = System.currentTimeMillis();
        today_calendar.setTimeInMillis(long_utc);
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        today_calendar.set(Calendar.HOUR_OF_DAY, 0);
        today_calendar.set(Calendar.MINUTE, 5);
        today_calendar.set(Calendar.SECOND, 0);
      /*  long emulated_creation_time_millis = System.currentTimeMillis();
        Date date = new Date(emulated_creation_time_millis);*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
        String today_emulation_date_string = simpleDateFormat.format(today_calendar.getTime());
        Cursor field_cursor = sqLiteDatabase.query(Contract.Field.TABLE_NAME,
                new String[]{Contract.COLUMN_ID, Contract.Field.COLUMN_CREATION_EMULATION_NUMBER, Contract.Field.COLUMN_CREATION_EMULATION_WHAT_DAYS},
                Contract.Field.COLUMN_CREATION_EMULATION_NUMBER + " > ? ", new String[]{String.valueOf(Constants.NOT_EMULATED)},
                null,null,null);
        if ( field_cursor.getCount() > 0 ) {
            for (field_cursor.moveToFirst(); field_cursor.getPosition() < field_cursor.getCount();
                 field_cursor.moveToNext()) {
                long field_id = field_cursor.getLong(0);
                int number_of_everyday_entries = field_cursor.getInt(1);
                String what_days = field_cursor.getString(2);
                ArrayList<Integer> not_days = new ArrayList<>(6);
                for ( int i = 0; i < what_days.length(); i++ ) {
                    if ( what_days.charAt(i) == '0') {
                        not_days.add(i + 1);
                    }
                }
                Calendar emulation_calendar = Calendar.getInstance();
                emulation_calendar.setTimeInMillis(long_utc);
                emulation_calendar.set(Calendar.HOUR_OF_DAY, 0);
                emulation_calendar.set(Calendar.MINUTE, 5);
                emulation_calendar.set(Calendar.SECOND, 0);
                if ( not_days.size() > 0 ) {
                    while ( not_days.contains(emulation_calendar.get(Calendar.DAY_OF_WEEK))) {
                        emulation_calendar.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
                String emulated_creation_time_string = simpleDateFormat.format(emulation_calendar.getTime());
                Cursor cursor = sqLiteDatabase.query(Contract.String_Entry.TABLE_NAME, new String[]{Contract.COLUMN_ID, Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE},
                        Contract.String_Entry.COLUMN_FIELD + " = ? AND " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                                + " >= datetime(?) ", new String[]{String.valueOf(field_id), today_emulation_date_string}, null, null,
                        Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                                + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC");
                int number_of_days = cursor.getCount() / number_of_everyday_entries;
                if (cursor.getCount() % number_of_everyday_entries > 0) {
                    number_of_days++;
                }
                sqLiteDatabase.beginTransaction();
                try {
                    if (number_of_days == 1) {
                        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                             cursor.moveToNext()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE, emulated_creation_time_string);
                            if ( sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                                    Contract.COLUMN_ID + " = ? ", new String[]{cursor.getLong(0) + ""}) == 1 ) {
                            }
                        }
                    } else if ( number_of_days > 1 ) {
                        outter:
                        for (int i = 0; i < number_of_days; i++) {
                            if (i >= 1) {
                                if ( not_days.size() <= 0 ) {
                                    emulation_calendar.add(Calendar.DAY_OF_MONTH, 1);
                                } else {
                                    do {
                                        emulation_calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    } while ( not_days.contains(emulation_calendar.get(Calendar.DAY_OF_WEEK)));
                                }
                                emulated_creation_time_string = simpleDateFormat.format(emulation_calendar.getTime());
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
                                if ( sqLiteDatabase.update(Contract.String_Entry.TABLE_NAME, contentValues,
                                        Contract.COLUMN_ID + " = ? ", new String[]{cursor.getLong(0) + ""}) == 1 ) {
                                }
                            }
                        }
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }
                cursor.close();
            }
            field_cursor.close();
        }
    }
}
