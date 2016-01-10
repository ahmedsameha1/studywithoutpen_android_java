package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabularyfive.Database.InvalidStudyWithoutPenFileException;
import com.ahmedsameha1.studyvocabularyfive.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by ahmed on 1/4/2016.
 */
public class ImportFieldsDialogFragment extends DialogFragment {
    public static final String IMPORT_THREAD_IS_RUNNING = "importThreadIsRunning";
    public static final String IMPORT_FIELD_DIALOG_FRAGMENT = "importFieldDialogFragment";
    public static final int IMPORT_FAILED_INVALID_FILE_NOTIFICATION = 0;
    public static final int IMPORT_FAILED_NOTIFICATION = 1;
    public static final int IMPORT_SUCCESSFUL_NOTIFICATION = 2;
    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.import_fields_dialog_fragment,null))
                .setPositiveButton(R.string.import_,null)
                .setNegativeButton(R.string.cancel,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                TextView copyFileIn = (TextView) alertDialog.findViewById(R.id.copy_file_to_import);
                TextView filePath = (TextView) alertDialog.findViewById(R.id.import_file_path);
                final EditText enterFileName = (EditText) alertDialog.findViewById(R.id.enter_import_file_name);
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (Environment.isExternalStorageRemovable() ) {
                    copyFileIn.setText(R.string.copy_import_file_removable);
                } else if ( Environment.isExternalStorageEmulated() ) {
                    copyFileIn.setText(R.string.copy_import_file_emulated);
                }
                filePath.setText(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String fileName = enterFileName.getText().toString();
                        if ( fileName.length() <= 0 ) {
                            Toast.makeText(context,R.string.enter_file_name_to_import,Toast.LENGTH_SHORT).show();
                        } else {
                            File file = new File(Environment.getExternalStorageDirectory().getPath()
                                    + File.separator + ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName.toLowerCase());
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
                                Toast.makeText(context, R.string.plug_your_sdcard, Toast.LENGTH_SHORT).show();
                            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                                Toast.makeText(context, R.string.external_storage_unmounted, Toast.LENGTH_SHORT).show();
                            } else if ( ! file.exists() ) {
                                Toast.makeText(context,R.string.file_not_exist,Toast.LENGTH_SHORT).show();
                                Toast.makeText(context,R.string.lowercase,Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_SHORT).show();
                                Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_SHORT).show();
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(IMPORT_THREAD_IS_RUNNING,true);
                                        editor.commit();
                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        try {
                                            alertDialog.dismiss();
                                            ExportImportDatabase.importFields(context, fileName);
                                            showNotification(IMPORT_SUCCESSFUL_NOTIFICATION,notificationManager);
                                        } catch (InvalidStudyWithoutPenFileException iswpfe ) {
                                            showNotification(IMPORT_FAILED_INVALID_FILE_NOTIFICATION,notificationManager);
                                        } catch ( SQLiteException | IOException e ) {
                                            e.printStackTrace();
                                            showNotification(IMPORT_FAILED_NOTIFICATION, notificationManager);
                                        }
                                        editor.putBoolean(IMPORT_THREAD_IS_RUNNING,false);
                                        editor.commit();
                                    }
                                });
                                thread.start();
                            }
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    private void showNotification(int notificationId, NotificationManager notificationManager ) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        TaskStackBuilder stackBuilder = null;
        switch (notificationId) {
            case IMPORT_FAILED_INVALID_FILE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.invalid_file))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, FieldActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(FieldActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case IMPORT_FAILED_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_failed))
                                .setContentText(context.getResources().getString(R.string.import_failed))
                                .setTicker(context.getResources().getString(R.string.import_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, FieldActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(FieldActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
            case IMPORT_SUCCESSFUL_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.import_completed))
                                .setContentText(context.getResources().getString(R.string.import_completed))
                                .setTicker(context.getResources().getString(R.string.import_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, FieldActivity.class);
                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(FieldActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(resultPendingIntent);
                notificationManager.notify(notificationId, notificationBuilder.build());
                break;
        }
    }
}
