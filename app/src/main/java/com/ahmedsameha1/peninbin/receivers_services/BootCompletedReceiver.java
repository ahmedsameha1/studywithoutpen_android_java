package com.ahmedsameha1.peninbin.receivers_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ahmedsameha1.peninbin.activities.AskAgainAfterTestActivity;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static com.ahmedsameha1.peninbin.database.DatabaseHelper.DATABASE_NAME;

/**
 * Created by ahmed on 24/05/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {// TODO: 9/7/17 test import from provided fields after update app 
        if ( intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED") ) {
            deleteOldProvidedDatabaseFile(context);
        }
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
                intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            Log.i("boot receiver", "running");
            rescheduleMidnightService(context);
            rescheduleAllSessions(context);
            removePendingPausingSharedPreference(context);
            copyProvidedDatabaseFile(context);
        }



    }

    private void rescheduleAllSessions(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<long[]> data = new DatabaseReadOperations(context).getBackupSchedulingData();
        for (long[] session : data) {
            Log.i("reschedule", "time");
            if (session[1] == ScheduleInFieldDialogFragment.QUICK_TEST) {
                if (session[4] == -1 && session[5] == -1 && session[6] == -1) {
                    Cursor cursor = DatabaseHelper.getInstance(context).getWritableDatabase().query(Contract.Field.TABLE_NAME,
                            new String[]{Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS},
                            Contract.COLUMN_ID + " = ? ", new String[]{session[0] + ""}, null, null, null);
                    if (cursor.moveToFirst()) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, cursor.getInt(1));
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, cursor.getInt(0));
                        intent.putExtra(Constants.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) session[3], intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    }
                    cursor.close();
                } else {
                    if (session[4] == QuickTestActivity.PERCENTAGE) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                        intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, (int) session[5]);
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, (int) session[6]);
                        intent.putExtra(Constants.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) session[3], intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    } else if (session[4] == QuickTestActivity.NUMBER) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, (int) session[5]);
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, (int) session[6]);
                        intent.putExtra(Constants.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) session[3], intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    }
                }
            } else {
                Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, (int) session[1]);
                intent.putExtra(Constants.CURRENT_FIELD_ID, session[0]);
                intent.putExtra(ScheduleInFieldDialogFragment.TIME, session[2]);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) session[3], intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
            }
        }
    }

    private void rescheduleMidnightService(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(MainActivity.MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST)) {
            int request = sharedPreferences.getInt(MainActivity.MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + 1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(context, SimpleWakefulReceiver.class);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pintent = PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
        }
    }

    private void removePendingPausingSharedPreference(Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Map<String, ?>  all_keys = sharedPreferences.getAll();
        for ( Map.Entry<String, ?> entry : all_keys.entrySet() ) {
            String key_string = entry.getKey();
            if ( key_string.startsWith(FullyRandomTestActivity.class.getName() + Constants.PAUSING)
                    || key_string.startsWith(EnhancedTestActivity.class.getName() + Constants.PAUSING)
                    || key_string.startsWith(StudyAPeriodActivity.class.getName() + Constants.PAUSING)
                    || key_string.startsWith(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED)
                    || key_string.startsWith(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM) ){
                editor.remove(key_string);
            }
        }
        editor.commit();
    }

    public static void copyProvidedDatabaseFile(Context context) {
        String app_database_path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
        String temp_database_path = app_database_path.substring(0, app_database_path.length() - 4) + "temp";
        File temp_database_file = new File(temp_database_path);
        try {
            if (!temp_database_file.exists()) {
                OutputStream os = new FileOutputStream(temp_database_path);
                InputStream is = context.getAssets().open("sqlite" + File.separator + DATABASE_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                is.close();
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteOldProvidedDatabaseFile(Context context) {
        String app_database_path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
        String temp_database_path = app_database_path.substring(0, app_database_path.length() - 4) + "temp";
        File temp_database_file = new File(temp_database_path);
        try {
            if (!temp_database_file.exists()) {
                temp_database_file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
