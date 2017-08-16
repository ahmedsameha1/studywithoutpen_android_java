package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.secondary_activities.SpeciallyFormattedTextFileHelpActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ImportFromWhatDialogFragment extends DialogFragment {
    public static final String IMPORT_FROM_WHAT_DIALOG_FRAGMENT = "import_from_what_dialog_fragment";
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
        alertDialog.setOnShowListener(dialog -> {
            Button app_file = (Button) alertDialog.findViewById(R.id.app_file);
            Button text_file = (Button) alertDialog.findViewById(R.id.text_file);
            ImageButton text_file_help = (ImageButton) alertDialog.findViewById(R.id.text_file_help);
            app_file.setOnClickListener(v -> {
                // This always works
                DialogProperties properties=new DialogProperties();

                properties.selection_mode= DialogConfigs.SINGLE_MODE;
                properties.selection_type= DialogConfigs.FILE_SELECT;
                properties.root=Environment.getExternalStorageDirectory();
                properties.error_dir=Environment.getExternalStorageDirectory();
                properties.extensions=null;

                FilePickerDialog dialog14 = new FilePickerDialog(context,properties);

                dialog14.setDialogSelectionListener(files -> {
                    //files is the array of the paths of files selected by the Application User.
                    if ( files.length > 0 ) {
                        AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(context);
                        importUserUsageData
                                .setMessage(R.string.import_user_usage)
                                .setNegativeButton(R.string.no, (dialog13, which) -> {
                                    Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    ExportImportDatabase.startImportThread(context, files [0],false);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                            .setAction(ActivityUtility.getVersionCode(context) + "import field from app file")
                                            .build());
                                })
                                .setPositiveButton(R.string.yes, (dialog12, which) -> {
                                    Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    ExportImportDatabase.startImportThread(context,files [0],true);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                            .setAction(ActivityUtility.getVersionCode(context) + "import field from app file")
                                            .build());
                                })
                                .create().show();
                    }
                });

                dialog14.show();
                dismiss();
            });
            text_file.setOnClickListener(v -> {
                DialogProperties properties=new DialogProperties();

                properties.selection_mode= DialogConfigs.SINGLE_MODE;
                properties.selection_type= DialogConfigs.FILE_SELECT;
                properties.root=Environment.getExternalStorageDirectory();
                properties.error_dir=Environment.getExternalStorageDirectory();
                properties.extensions=null;

                FilePickerDialog dialog1 = new FilePickerDialog(context,properties);

                dialog1.setDialogSelectionListener(files -> {
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
                                .setAction(ActivityUtility.getVersionCode(context) + "import field from text file")
                                .build());
                    }
                });

                dialog1.show();
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
            });
            text_file_help.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), SpeciallyFormattedTextFileHelpActivity.class));
            });
        });
        return alertDialog;
    }

}


