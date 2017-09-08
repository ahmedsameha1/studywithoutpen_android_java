package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;

import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ExportFieldsIntentService extends IntentService {
    public static final String FILE_PATH = "file_path";
    public static final String FILE_NAME = "file_name";
    public static final String SELECTED_FIELDS = "selected_fields";
    public static boolean isRunning = false;

    public ExportFieldsIntentService() {
        super("ExportFieldsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.export_notifi))
                .setContentText(getString(R.string.export_notifi_content))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        startForeground((int) System.currentTimeMillis(), notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            isRunning = true;
            ArrayList<String> selectedFields = intent.getStringArrayListExtra(SELECTED_FIELDS);
            String path = intent.getStringExtra(FILE_PATH);
            String fileName = intent.getStringExtra(FILE_NAME);
            ExportImportDatabase.exportFields(this, selectedFields, path, fileName);
            isRunning = false;
            stopForeground(true);
        }
    }

}
