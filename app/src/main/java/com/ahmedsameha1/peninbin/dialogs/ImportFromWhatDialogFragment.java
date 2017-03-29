package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ImportFromWhatDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    public static final String IMPORT_FROM_WHAT_DIALOG_FRAGMENT = "import_from_what_dialog_fragment";
    public static final int TEXT_FILE_CODE = 0;
    public static final int APP_FILE_CODE = 1;
    private Context context;
    private Tracker mTracker;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.import_from_what_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button app_file = (Button) alertDialog.findViewById(R.id.app_file);
                Button text_file = (Button) alertDialog.findViewById(R.id.text_file);
                ImageButton text_file_help = (ImageButton) alertDialog.findViewById(R.id.text_file_help);
                app_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // This always works
                        DialogProperties properties=new DialogProperties();

                        properties.selection_mode= DialogConfigs.SINGLE_MODE;
                        properties.selection_type= DialogConfigs.FILE_SELECT;
                        properties.root=Environment.getExternalStorageDirectory();
                        properties.error_dir=Environment.getExternalStorageDirectory();
                        properties.extensions=null;

                        FilePickerDialog dialog = new FilePickerDialog(context,properties);

                        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                            @Override
                            public void onSelectedFilePaths(final String[] files) {
                                //files is the array of the paths of files selected by the Application User.
                                if ( files.length > 0 ) {
                                    AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(context);
                                    importUserUsageData
                                            .setMessage(R.string.import_user_usage)
                                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    ExportImportDatabase.startImportThread(context, files [0],false);
                                                    mTracker.send(new HitBuilders.EventBuilder()
                                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                            .setAction("import field from app file")
                                                            .build());
                                                }
                                            })
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    ExportImportDatabase.startImportThread(context,files [0],true);
                                                    mTracker.send(new HitBuilders.EventBuilder()
                                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                            .setAction("import field from app file")
                                                            .build());
                                                }
                                            })
                                            .create().show();
                                }
                            }
                        });

                        dialog.show();
                        /*Intent i = new Intent(context, FilePickerActivity.class);
                        // This works if you defined the intent filter
                        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                        // Set these depending on your use case. These are the defaults.
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                        // Configure initial directory by specifying a String.
                        // You could specify a String like "/storage/emulated/0/", but that can
                        // dangerous. Always use Android's API calls to get paths to the SD-card or
                        // internal memory.
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                        getActivity().startActivityForResult(i, APP_FILE_CODE);*/
                        dismiss();
                     /*       ImportFieldsDialogFragment importFieldsDialogFragment = new ImportFieldsDialogFragment();
                            importFieldsDialogFragment.show(getActivity().getSupportFragmentManager(), ImportFieldsDialogFragment.IMPORT_FIELD_DIALOG_FRAGMENT);
                        dismiss();*/
               /*         mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(ANALYTICS_CATEGORY)
                                .setAction("open import field")
                                .build());*/
                    }
                });
                text_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProperties properties=new DialogProperties();

                        properties.selection_mode= DialogConfigs.SINGLE_MODE;
                        properties.selection_type= DialogConfigs.FILE_SELECT;
                        properties.root=Environment.getExternalStorageDirectory();
                        properties.error_dir=Environment.getExternalStorageDirectory();
                        properties.extensions=null;

                        FilePickerDialog dialog = new FilePickerDialog(context,properties);

                        dialog.setDialogSelectionListener(new DialogSelectionListener() {
                            @Override
                            public void onSelectedFilePaths(final String[] files) {
                                //files is the array of the paths of files selected by the Application User.
                                if ( files.length > 0 ) {
                                    Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    ImportTextFile.startImportThread(context, files [0]);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                            .setAction("import field from text file")
                                            .build());
                                }
                            }
                        });

                        dialog.show();
                        // This always works
                        /*Intent i = new Intent(context, FilePickerActivity.class);
                        // This works if you defined the intent filter
                        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                        // Set these depending on your use case. These are the defaults.
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                        // Configure initial directory by specifying a String.
                        // You could specify a String like "/storage/emulated/0/", but that can
                        // dangerous. Always use Android's API calls to get paths to the SD-card or
                        // internal memory.
                        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                        getActivity().startActivityForResult(i, TEXT_FILE_CODE);*/
                        dismiss();
                    }
                });
                text_file_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder text_file_help_dialog = new AlertDialog.Builder(context);
                        text_file_help_dialog
                                .setMessage(R.string.import_from_text_file_help).create().show();
                    }
                });
            }

        });
        return alertDialog;
    }

}


