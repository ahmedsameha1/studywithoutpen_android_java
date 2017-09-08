package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.utils.Emulation;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class EnableEmulationIntentService extends IntentService {

    public static final String FIELD_ID = "field_id";
    public static final String NUMBER_OF_ENTRIES = "number_of_entries";
    public static final String STARTING_ENTRY = "starting_entry";
    public static final String WEEK_DAYS_CASE = "week_days_case";
    public static boolean is_running = false;

    public EnableEmulationIntentService() {
        super("EnableEmulationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.enable_emulation_notifi))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        startForeground((int) System.currentTimeMillis(), notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            is_running = true;
            long field_id = intent.getLongExtra(FIELD_ID,0);
            int number_of_entries = intent.getIntExtra(NUMBER_OF_ENTRIES, 0);
            boolean [] week_days_case = (boolean[]) intent.getSerializableExtra(WEEK_DAYS_CASE);
            long starting_entry = intent.getLongExtra(STARTING_ENTRY, -1);
            if (starting_entry == -1 ) {
                Emulation.emulate(this, field_id, number_of_entries, week_days_case);
            } else {
                Emulation.emulate(this, field_id, number_of_entries, starting_entry, week_days_case);
            }
            is_running = false;
        }
    }

}
