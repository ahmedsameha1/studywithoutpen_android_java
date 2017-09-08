package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ImportTextFile;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ImportTextFileIntentService extends IntentService {
    public static final String FILE_PATH = "file_path";
    public static boolean isRunning = false;


    public ImportTextFileIntentService() {
        super("ImportTextFileIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.import_notifi))
                .setContentText(getString(R.string.import_txt_notifi_content))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        startForeground((int) System.currentTimeMillis(), notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String file_path = intent.getStringExtra(FILE_PATH);
            isRunning = true;
            ImportTextFile.importFields(this, file_path);
            isRunning = false;
            stopForeground(true);
        }
    }

}
