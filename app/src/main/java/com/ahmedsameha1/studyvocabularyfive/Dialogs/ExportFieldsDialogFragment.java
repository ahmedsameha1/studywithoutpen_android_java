package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.Database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ExportFieldsDialogFragment extends DialogFragment {
    public static final String EXPORT_THREAD_IS_RUNNING = "exportThreadIsRunning";
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    public static final int EXPORT_FAILED_NOTIFICATION = 0;
    public static final int REMOVABLE_STORAGE_NOTIFICATION = 1;
    public static final int EMULATED_STORAGE_NOTIFICATION = 2;

    private DatabaseOperations databaseOperations;
    private EditText enterFileName;
    private ListView listView;


    private Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.export_fields_dialog_fragment, null))
                .setPositiveButton(R.string.export, null)
                .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                databaseOperations = new DatabaseOperations(context.getContentResolver());
                enterFileName = (EditText) alertDialog.findViewById(R.id.file_name);
                listView = (ListView) alertDialog.findViewById(R.id.export_fields_list_view);

                SimpleCursorAdapter simpleCursorAdapter =
                        new SimpleCursorAdapter(context,
                                android.R.layout.simple_list_item_multiple_choice,
                                databaseOperations.getFieldList(),
                                new String[]{Contract.Field.COLUMN_NAME_FIELD_NAME},
                                new int[]{android.R.id.text1}, 0);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(simpleCursorAdapter);
                Button p = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileName = enterFileName.getText().toString();
                        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                        final ArrayList<String> selectedFields = new ArrayList<String>();
                        for (int i = 0; i < sparseBooleanArray.size(); i++) {
                            int key = sparseBooleanArray.keyAt(i);
                            if (sparseBooleanArray.get(key)) {
                                selectedFields.add(((Cursor) listView.getItemAtPosition(key)).getString(1));
                            }
                        }
                        if (selectedFields.size() <= 0) {
                            Toast.makeText(context, R.string.zero_selected_fields_toast, Toast.LENGTH_SHORT).show();
                        } else if (fileName.length() <= 0) {
                            Toast.makeText(context, R.string.no_file_name_toast, Toast.LENGTH_SHORT).show();
                        } else {
                            File file = new File(Environment.getExternalStorageDirectory().getPath()
                                    + File.separator + ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName.toLowerCase());
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
                                Toast.makeText(context, R.string.plug_your_sdcard, Toast.LENGTH_SHORT).show();
                            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                                Toast.makeText(context, R.string.external_storage_unmounted, Toast.LENGTH_SHORT).show();
                            } else if ( file.exists() ) {
                                Toast.makeText(context, R.string.file_with_same_name, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT).show();
                                Toast.makeText(context, R.string.shut_down_warning, Toast.LENGTH_SHORT).show();
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context); // getting the preferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(EXPORT_THREAD_IS_RUNNING, true);
                                        editor.commit();
                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        try {
                                            alertDialog.dismiss();
                                            ExportImportDatabase.exportFields(context, selectedFields, enterFileName.getText().toString());
                                            if (Environment.isExternalStorageRemovable()) {
                                                showNotification(REMOVABLE_STORAGE_NOTIFICATION, notificationManager);
                                            } else if (Environment.isExternalStorageEmulated()) {
                                                showNotification(EMULATED_STORAGE_NOTIFICATION, notificationManager);
                                            }
                                        } catch (SQLiteException | IOException e) {
                                            showNotification(EXPORT_FAILED_NOTIFICATION, notificationManager);
                                        }
                                        editor.putBoolean(EXPORT_THREAD_IS_RUNNING, false);
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

    private void showNotification(int notificationId, NotificationManager notificationManager) {
        NotificationCompat.Builder notificationBuilder = null;
        Intent notifyIntent = null;
        PendingIntent resultPendingIntent = null;
        switch (notificationId) {
            case EXPORT_FAILED_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_failed))
                                .setContentText(context.getResources().getString(R.string.export_failed))
                                .setTicker(context.getResources().getString(R.string.export_failed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportCompletedActivity.class);
                notifyIntent.putExtra(ExportCompletedActivity.NOTIFICATION_ID, EXPORT_FAILED_NOTIFICATION);
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
            case REMOVABLE_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportCompletedActivity.class);
                notifyIntent.putExtra(ExportCompletedActivity.NOTIFICATION_ID, REMOVABLE_STORAGE_NOTIFICATION);
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
            case EMULATED_STORAGE_NOTIFICATION:
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.export_completed))
                                .setContentText(context.getResources().getString(R.string.export_completed))
                                .setTicker(context.getResources().getString(R.string.export_completed))
                                .setAutoCancel(true);
                notifyIntent = new Intent(context, ExportCompletedActivity.class);
                notifyIntent.putExtra(ExportCompletedActivity.NOTIFICATION_ID, EMULATED_STORAGE_NOTIFICATION);
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
