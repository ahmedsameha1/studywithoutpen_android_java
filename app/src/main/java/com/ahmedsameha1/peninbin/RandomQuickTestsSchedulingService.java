package com.ahmedsameha1.peninbin;

import android.app.IntentService;
import android.content.Intent;

import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.utils.WakefulIntentService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 22/05/16.
 */
public class RandomQuickTestsSchedulingService extends WakefulIntentService {
    public RandomQuickTestsSchedulingService(String name) {
        super(name);
    }

    @Override
    public void doWakefulWork(Intent workIntent) {
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
                        text = excluded_time[0];
                        calendar = Calendar.getInstance(Locale.getDefault());

                    }
                }
                long start = -1, end = -1;
       /*         ;
                StringTokenizer stringTokenizer;
                calendar = Calendar.getInstance(Locale.getDefault());
                text = enter_excluded_time_5_start.getText().toString();
                stringTokenizer = new StringTokenizer(text, ":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                start = calendar.getTimeInMillis();
                text = enter_excluded_time_5_end.getText().toString();
                stringTokenizer = new StringTokenizer(text, ":");
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                end = calendar.getTimeInMillis();*/
            }
        }
    }

}
