package com.ahmedsameha1.peninbin.intent_services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
    public static final String URI = "uri";
    public static boolean isRunning = false;

    public ImportSWPFileIntentService() {
        super("ImportSWPFileIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }

        Notification notification = new NotificationCompat.Builder(this, IntentServicesUtility.createNotificationChannel(this))
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.import_notifi))
                .setContentText(getString(R.string.import_swp_notifi_content))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
                startForeground((int) System.currentTimeMillis(), notification);
            }
        } else {
            startForeground((int) System.currentTimeMillis(), notification);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(FILE_PATH)) {
                isRunning = true;
                String file_path = intent.getStringExtra(FILE_PATH);
                boolean importUserUsageData = intent.getBooleanExtra(USER_USAGE, false);
                ExportImportDatabase.importFields(this, file_path, importUserUsageData);
                isRunning = false;
                stopForeground(true);
            } else if (intent.hasExtra(URI)) {
                isRunning = true;
                Uri uri = Uri.parse(intent.getStringExtra(URI));
                boolean importUserUsageData = intent.getBooleanExtra(USER_USAGE, false);
                ExportImportDatabase.importFields(this, uri, importUserUsageData);
                isRunning = false;
                stopForeground(true);
            }
        }
    }
}
