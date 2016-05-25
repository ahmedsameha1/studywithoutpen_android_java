package com.ahmedsameha1.peninbin.receivers_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.receivers_services.ScheduleInFieldReceiver;
import com.ahmedsameha1.peninbin.utils.WakefulIntentService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 22/05/16.
 */
public class RandomQuickTestsSchedulingService extends WakefulIntentService {
    public static final String SCHEDULE_WHAT = "schedule_what";
    private AlarmManager alarmManager;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    public RandomQuickTestsSchedulingService(String name) {
        super(name);
    }

    @Override
    public void doWakefulWork(Intent workIntent) {
        alarmManager = (AlarmManager) this.getSystemService(AppCompatActivity.ALARM_SERVICE);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        DatabaseOperations databaseOperations = new DatabaseOperations(this);
        ArrayList<long[]> scheduling_data = databaseOperations.getRandomQuickTestsSchedulingData();
        if ( scheduling_data.size() > 0 ) {
            for ( long [] field_data : scheduling_data ) {
                long field_id = field_data [0];
                int number_of_quick_tests = (int) field_data[1];
                int number_of_tries = (int) field_data[2];
                int number_of_questions = (int) field_data[3];
                String [][] excluded_times = databaseOperations.get_excluded_times(field_id);
                ArrayList<Long> long_excluded_times = new ArrayList<>();
                for ( String[] excluded_time : excluded_times ) {
                    if ( excluded_time != null ) {
                        String text;
                        Calendar calendar;
                        StringTokenizer stringTokenizer;
                        text = excluded_time[0];
                        calendar = Calendar.getInstance(Locale.getDefault());
                        stringTokenizer = new StringTokenizer(text, ":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                        long_excluded_times.add(calendar.getTimeInMillis());
                        text = excluded_time[1];
                        calendar = Calendar.getInstance(Locale.getDefault());
                        stringTokenizer = new StringTokenizer(text, ":");
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                        long_excluded_times.add(calendar.getTimeInMillis());
                    }
                }
                ArrayList<Long> randoms = new ArrayList<>();
                for ( int i = 0; i < number_of_quick_tests; i++ ) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,0);
                    calendar.set(Calendar.MINUTE,5);
                    calendar.set(Calendar.SECOND,0);
                    Random random_opject = new Random();
                    rrr:
                    for ( int t = 0; t < 1000000; t++ ) {
                        long random = calendar.getTimeInMillis() + 15 * 60 * 1000 * random_opject.nextInt(94);
                        if ( long_excluded_times.size() >= 2 ) {
                            if ( random >= long_excluded_times.get(0) && random <= long_excluded_times.get(1) ) {
                                continue;
                            }
                        } else if ( long_excluded_times.size() >= 4 ) {
                            if ( random >= long_excluded_times.get(2) && random <= long_excluded_times.get(3) ) {
                                continue;
                            }
                        } else if ( long_excluded_times.size() >= 6 ) {
                            if ( random >= long_excluded_times.get(4) && random <= long_excluded_times.get(5) ) {
                                continue;
                            }
                        } else if ( long_excluded_times.size() >= 8 ) {
                            if ( random >= long_excluded_times.get(6) && random <= long_excluded_times.get(7) ) {
                                continue;
                            }
                        } else if ( long_excluded_times.size() >= 10 ) {
                            if ( random >= long_excluded_times.get(8) && random <= long_excluded_times.get(9) ) {
                                continue;
                            }
                        }
                        for ( Long r : randoms ) {
                            if ( r == random ) {
                                continue rrr;
                            }
                        }
                        randoms.add(random);
                        Intent intent = new Intent(this, ScheduleInFieldReceiver.class);
                        intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER, TestActivity.NUMBER);
                        intent.putExtra(TestActivity.QUESTION_NUMBER, number_of_questions);
                        intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER, number_of_tries);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                        intent.putExtra(SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                        intent.putExtra(ScheduleInFieldDialogFragment.TIME,random);
                        int request = (int)System.currentTimeMillis();
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC, random, pendingIntent);
                        mySQLiteCursorLoader.addScheduledTimeForBackup(field_id,ScheduleInFieldDialogFragment.QUICK_TEST,random,request);// TODO: 24/05/16 must do like this for all schduled sessions

                        break;
                    }
                }
            }
        }
    }
}
