package com.ahmedsameha1.peninbin.view.dialogs;

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
import com.ahmedsameha1.peninbin.view.activities.MainActivity;
import com.ahmedsameha1.peninbin.intent_services.ImportSWPFileIntentService;
import com.ahmedsameha1.peninbin.intent_services.ImportTextFileIntentService;
import com.ahmedsameha1.peninbin.secondary_activities.SpeciallyFormattedTextFileHelpActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Money;
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
                        DialogProperties properties = new DialogProperties();

                        properties.selection_mode = DialogConfigs.SINGLE_MODE;
                        properties.selection_type = DialogConfigs.FILE_SELECT;
                        properties.root = Environment.getExternalStorageDirectory();
                        properties.error_dir = Environment.getExternalStorageDirectory();
                        properties.extensions = null;

                        final FilePickerDialog dialog14 = new FilePickerDialog(context, properties);

                        dialog14.setDialogSelectionListener(new DialogSelectionListener() {
                            @Override
                            public void onSelectedFilePaths(final String[] files) {
                                //files is the array of the paths of files selected by the Application User.
                                if (files.length > 0) {
                                    AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(context);
                                    importUserUsageData
                                            .setMessage(R.string.import_user_usage)
                                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog13, int which) {
                                                    Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    Intent intent = new Intent(((Activity) context).getBaseContext(), ImportSWPFileIntentService.class);
                                                    intent.putExtra(ImportSWPFileIntentService.FILE_PATH, files[0]);
                                                    intent.putExtra(ImportSWPFileIntentService.USER_USAGE, false);
                                                    context.startService(intent);
                                                    mTracker.send(new HitBuilders.EventBuilder()
                                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                            .setAction((Money.getMe(context) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(context) + "import field from app file")
                                                            .build());
                                                }
                                            })
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog12, int which) {
                                                    Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                                    toast.setGravity(Gravity.TOP, 0, 400);
                                                    toast.show();
                                                    Intent intent = new Intent(((Activity) context).getBaseContext(), ImportSWPFileIntentService.class);
                                                    intent.putExtra(ImportSWPFileIntentService.FILE_PATH, files[0]);
                                                    intent.putExtra(ImportSWPFileIntentService.USER_USAGE, true);
                                                    context.startService(intent);
                                                    mTracker.send(new HitBuilders.EventBuilder()
                                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                            .setAction((Money.getMe(context) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(context) + "import field from app file")
                                                            .build());
                                                }
                                            })
                                            .create().show();
                                }
                            }
                        });
                        new AlertDialog.Builder(getActivity()).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog14.show();
                                ImportFromWhatDialogFragment.this.dismiss();
                            }
                        }).create().show();
                    }
                });
                text_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogProperties properties = new DialogProperties();

                        properties.selection_mode = DialogConfigs.SINGLE_MODE;
                        properties.selection_type = DialogConfigs.FILE_SELECT;
                        properties.root = Environment.getExternalStorageDirectory();
                        properties.error_dir = Environment.getExternalStorageDirectory();
                        properties.extensions = null;

                        final FilePickerDialog dialog1 = new FilePickerDialog(context, properties);

                        dialog1.setDialogSelectionListener(new DialogSelectionListener() {
                            @Override
                            public void onSelectedFilePaths(String[] files) {
                                //files is the array of the paths of files selected by the Application User.
                                if (files.length > 0) {
                                    Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    Intent intent = new Intent(((Activity) context).getBaseContext(), ImportTextFileIntentService.class);
                                    intent.putExtra(ImportTextFileIntentService.FILE_PATH, files[0]);
                                    context.startService(intent);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                            .setAction((Money.getMe(context) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(context) + "import field from text file")
                                            .build());
                                }
                            }
                        });
                        new AlertDialog.Builder(getActivity()).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog1.show();
                                ImportFromWhatDialogFragment.this.dismiss();
                            }
                        }).create().show();

                    }
                });
                text_file_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImportFromWhatDialogFragment.this.startActivity(new Intent(getActivity(), SpeciallyFormattedTextFileHelpActivity.class));
                    }
                });
            }
        });
        return alertDialog;
    }

}


