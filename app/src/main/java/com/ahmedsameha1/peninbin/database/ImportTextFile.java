package com.ahmedsameha1.peninbin.database;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.secondary_activities.ExportSuccessfulActivity;
import com.ahmedsameha1.peninbin.secondary_activities.NotImportedFieldsActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.utils.InvalidTextFileFormatException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Ahmed on 7/3/16.
 */
public class ImportTextFile {
    public static final String EXPORT_IMPORT_DIRECTORY_NAME = "StudyWithoutPen";
    private static Thread importThread;
    public static void startImportThread( final Context context, final String fileName ) {
        importThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                importFields(context, fileName );
            }
        });
        importThread.start();
    }

    public static void importFields(Context context, String fileName ) {
        String filePath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName;
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        boolean valid_structure = true;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            while ( line != null ) {
                int result = CompareUtility.checkTextLineToImport(line);
                if ( result == 0 ) {
                    valid_structure = false;
                    break;
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            if ( ! valid_structure ) {
                throw new InvalidTextFileFormatException();
            }
        }
        catch ( InvalidTextFileFormatException e) {
            showNotification(context,);
        }
        catch (IOException e) {
            showNotification(context);
        }
    }
    private static void showNotification(Context context, int notificationId, NotificationManager notificationManager) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        TaskStackBuilder stackBuilder = null;
        switch (notificationId) {
            case FAILED_EXPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_failed))
                                .setContentText(context.getResources().getString(R.string.export_failed))
                                .setTicker(context.getResources().getString(R.string.export_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, FAILED_EXPORT_NOTIFICATION);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESFUL_EMULATED_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getResources().getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportSuccessfulActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, SUCCESFUL_EMULATED_STORAGE_NOTIFICATION);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESFUL_EXPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getResources().getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case FAILED_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.import_failed))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case FAILED_IMPORT_INVALID_FILE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.invalid_file))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case SUCCESSFUL_IMPORT_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_completed))
                                .setContentText(context.getResources().getString(R.string.import_completed))
                                .setTicker(context.getResources().getString(R.string.import_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, MainActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case NOT_IMPORTED_FIELDS:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.not_imported_fields))
                                .setContentText(context.getString(R.string.not_imported_fields))
                                .setTicker(context.getResources().getString(R.string.not_imported_fields))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, NotImportedFieldsActivity.class);
                notifyIntent.putExtra(ExportSuccessfulActivity.NOTIFICATION_ID, notImportedFieldsStrings);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }
}
