package com.ahmedsameha1.peninbin.receivers_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.adapters.NotebookFieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by ahmed on 24/05/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("boot receiver","running");
        rescheduleMidnightService(context);
        rescheduleAllSessions(context);
        rescheduleReminders(context);
    }

    private void rescheduleAllSessions(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ArrayList<long[]> data = new DatabaseReadOperations(context).getBackupSchedulingData();
        for ( long[] session : data ) {
            Log.i("reschedule","time");
            if ( session[1] == ScheduleInFieldDialogFragment.QUICK_TEST ) {
                if ( session[4] == -1 && session [5] == -1 && session[6] == -1 ) {
                    Cursor cursor = DatabaseHelper.getInstance(context).getWritableDatabase().query(Contract.Field.TABLE_NAME,
                            new String[]{Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES, Contract.Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS,
                                    Contract.Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION, Contract.Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL,
                                    Contract.Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL},
                            Contract.COLUMN_ID + " = ? ", new String[]{session[0] + ""}, null, null, null);
                    if ( cursor.moveToFirst() ) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, cursor.getInt(1));
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, cursor.getInt(0));
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, cursor.getInt(2));
                        intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, cursor.getInt(3));
                        intent.putExtra(EnhancedTestActivity.RANK_LEVEL, cursor.getInt(4));
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME,session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) session[3] , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    }
                    cursor.close();
                } else {
                    if ( session [4] == QuickTestActivity.PERCENTAGE) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                        intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, (int) session[5]);
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, (int) session[6]);
                        intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, (int) session[8]);
                        intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, (int) session[9]);
                        intent.putExtra(EnhancedTestActivity.RANK_LEVEL, (int) session[10]);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME,session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) session[3] , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    } else if ( session [4] == QuickTestActivity.NUMBER ) {
                        Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, (int) session[5]);
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, (int) session[6]);
                        intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, (int) session[8]);
                        intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, (int) session[9]);
                        intent.putExtra(EnhancedTestActivity.RANK_LEVEL, (int) session[10]);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, session[0]);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME,session[2]);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) session[3] , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
                    }
                }
            } else {
                Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
                intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,(int) session[1]);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,session[0]);
                intent.putExtra(ScheduleInFieldDialogFragment.TIME,session[2]);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,(int) session[3] , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC, session[2], pendingIntent);
            }
        }
    }

    private void rescheduleMidnightService(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if ( sharedPreferences.contains(MainActivity.MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST) ) {
            int request = sharedPreferences.getInt(MainActivity.MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST,0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + 1);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND,0);
            Intent intent = new Intent(context, SimpleWakefulReceiver.class);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pintent = PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
        }
    }

    private void rescheduleReminders(Context context) {
        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Cursor cursor = new DatabaseReadOperations(context).getReminderDataForRescheduling();
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            Intent intent = new Intent(context, ShowScheduledNotificationsReceiver.class);
            intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,ScheduleInFieldDialogFragment.REMINDER);
            intent.putExtra(NotebookFieldActivityRecyclerViewAdapter.NOTE_TEXT,cursor.getString(0));
            intent.putExtra(NotebookFieldActivityRecyclerViewAdapter.ENTRY_ID,cursor.getLong(3));
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, cursor.getInt(2), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if ( Build.VERSION.SDK_INT >= 19 ){
                alarm_manager.setExact(AlarmManager.RTC, cursor.getLong(1), pendingIntent);
            } else {
                alarm_manager.set(AlarmManager.RTC, cursor.getLong(1), pendingIntent);
            }
        }
        cursor.close();
    }
}
