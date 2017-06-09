package com.ahmedsameha1.peninbin.receivers_services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 22/05/16.
 */
public class MidnightService extends IntentService {
    private AlarmManager alarmManager;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;

    public MidnightService() {
        super("MidnightService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        Log.i("service:", "service running");
        scheduleRandomQuickTest();
        tryRefreshingAppUsage();
        cursorLoaderAndUpdateDatabase.cleanDatabase();
        Emulation.startReemulateThread(this);
        WakefulBroadcastReceiver.completeWakefulIntent(workIntent);
        Log.i("service:", "service end");
    }

    private void scheduleRandomQuickTest() {
        alarmManager = (AlarmManager) this.getSystemService(AppCompatActivity.ALARM_SERVICE);
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(this);
        ArrayList<long[]> scheduling_data = databaseOperations.getRandomQuickTestsSchedulingData();
        if (scheduling_data.size() > 0) {
            for (long[] field_data : scheduling_data) {
                long field_id = field_data[0];
                int number_of_quick_tests = (int) field_data[1];
                int number_of_tries = (int) field_data[2];
                int number_of_questions = (int) field_data[3];
                String[][] excluded_times = databaseOperations.get_excluded_times(field_id);
                ArrayList<Long> long_excluded_times = new ArrayList<>();
                for (String[] excluded_time : excluded_times) {
                    if (excluded_time != null) {
                        String text;
                        Calendar calendar;
                        StringTokenizer stringTokenizer;
                        text = excluded_time[0];
                        calendar = Calendar.getInstance(Locale.getDefault());
                        stringTokenizer = new StringTokenizer(text, ":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.SECOND, 0);
                        long_excluded_times.add(calendar.getTimeInMillis());
                        text = excluded_time[1];
                        calendar = Calendar.getInstance(Locale.getDefault());
                        stringTokenizer = new StringTokenizer(text, ":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.SECOND, 0);
                        long_excluded_times.add(calendar.getTimeInMillis());
                    }
                }
                ArrayList<Long> randoms = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 5);
                calendar.set(Calendar.SECOND, 0);
                Random random_opject = new Random();
                for (int i = 0; i < number_of_quick_tests; i++) {
                    rrr:
                    for (int t = 0; t < 10000; t++) {
                        Log.i("service:", "try a time");
                        long random = calendar.getTimeInMillis() + 15 * 60 * 1000 * random_opject.nextInt(94);
                        if (long_excluded_times.size() >= 2) {
                            if (random >= long_excluded_times.get(0) && random <= long_excluded_times.get(1)) {
                                continue;
                            }
                        }
                        if (long_excluded_times.size() >= 4) {
                            if (random >= long_excluded_times.get(2) && random <= long_excluded_times.get(3)) {
                                continue;
                            }
                        }
                        if (long_excluded_times.size() >= 6) {
                            if (random >= long_excluded_times.get(4) && random <= long_excluded_times.get(5)) {
                                continue;
                            }
                        }
                        if (long_excluded_times.size() >= 8) {
                            if (random >= long_excluded_times.get(6) && random <= long_excluded_times.get(7)) {
                                continue;
                            }
                        }
                        if (long_excluded_times.size() >= 10) {
                            if (random >= long_excluded_times.get(8) && random <= long_excluded_times.get(9)) {
                                continue;
                            }
                        }
                        for (Long r : randoms) {
                            if (r == random) {
                                continue rrr;
                            }
                        }
                        randoms.add(random);
                        Log.i("service:", "time");
                        Intent intent = new Intent(this, ShowScheduledNotificationsReceiver.class);
                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, number_of_questions);
                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, number_of_tries);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                        intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, random);
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        int request = (int) System.currentTimeMillis();
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, random, pendingIntent);
                        cursorLoaderAndUpdateDatabase.addScheduledTimeForBackup(field_id, ScheduleInFieldDialogFragment.QUICK_TEST, random, request);

                        break;
                    }
                }
            }
        }
    }

    private void tryRefreshingAppUsage() {
        if (System.currentTimeMillis() - UsageLastTime.getUsageLastTime(this) > AlarmManager.INTERVAL_DAY) {
            Random random_object = new Random();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            long random = calendar.getTimeInMillis() + random_object.nextInt((int) (AlarmManager.INTERVAL_DAY - AlarmManager.INTERVAL_HOUR));
            Intent intent = new Intent(this, ShowScheduledNotificationsReceiver.class);
            intent.putExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT, ScheduleInFieldDialogFragment.REFRESHING_USAGE_OF_APP);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, random, pendingIntent);
        }
    }

}
