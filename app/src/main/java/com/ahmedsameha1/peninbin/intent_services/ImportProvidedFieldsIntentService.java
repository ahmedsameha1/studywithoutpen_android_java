package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.IntentServicesUtility;
import com.ahmedsameha1.peninbin.view.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;

import java.util.ArrayList;

import static com.ahmedsameha1.peninbin.database.DatabaseHelper.DATABASE_NAME;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ImportProvidedFieldsIntentService extends IntentService {
    public static final String FIELDS = "fields";
    public static boolean isRunning = false;

    public ImportProvidedFieldsIntentService() {
        super("ImportProvidedFieldsIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, IntentServicesUtility.createNotificationChannel(this) )
                .setSmallIcon(R.drawable.ic_launcher48)
                .setContentTitle(getString(R.string.import_notifi))
                .setContentText(getString(R.string.import_provided_field_notifi_content))
                .setOngoing(true)
                .setContentIntent(pendingIntent).build();

        startForeground((int) System.currentTimeMillis(), notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            isRunning = true;
            String app_database_path = getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
            String temp_database_path = app_database_path.substring(0, app_database_path.length() - 4) + "temp";
            final SQLiteDatabase toImportDatabase = SQLiteDatabase.openDatabase(temp_database_path, null, SQLiteDatabase.OPEN_READWRITE);
            ArrayList<String> wanted_fields = intent.getStringArrayListExtra(FIELDS);
            ExportImportDatabase.importInternalFields(this, toImportDatabase, wanted_fields);
            isRunning = false;
            stopForeground(true);
        }
    }

}
