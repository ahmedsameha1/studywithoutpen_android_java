package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.IntentServicesUtility;
import com.ahmedsameha1.peninbin.view.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ImportSWPFileIntentService extends IntentService {
    public static final String FILE_PATH = "file_path";
    public static final String USER_USAGE = "user_usage";
    public static boolean isRunning = false;

    public ImportSWPFileIntentService() {
        super("ImportSWPFileIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, IntentServicesUtility.createNotificationChannel(this))
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.import_notifi))
                .setContentText(getString(R.string.import_swp_notifi_content))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        startForeground((int) System.currentTimeMillis(), notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            isRunning = true;
            String file_path = intent.getStringExtra(FILE_PATH);
            boolean importUserUsageData = intent.getBooleanExtra(USER_USAGE, false);
            ExportImportDatabase.importFields(this, file_path, importUserUsageData);
            isRunning = false;
            stopForeground(true);
        }
    }
}
