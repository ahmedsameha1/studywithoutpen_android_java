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

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.nononsenseapps.filepicker.FilePickerActivity;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ImportFromWhatDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    public static final String IMPORT_FROM_WHAT_DIALOG_FRAGMENT = "import_from_what_dialog_fragment";
    public static final int TEXT_FILE_CODE = 0;
    public static final int APP_FILE_CODE = 1;
    private Context context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
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
                        Intent i = new Intent(context, FilePickerActivity.class);
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

                        getActivity().startActivityForResult(i, APP_FILE_CODE);
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
                        // This always works
                        Intent i = new Intent(context, FilePickerActivity.class);
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

                        getActivity().startActivityForResult(i, TEXT_FILE_CODE);
                        dismiss();
                    }
                });
                text_file_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder text_file_help_dialog = new AlertDialog.Builder(getActivity());
                        text_file_help_dialog
                                .setMessage(R.string.import_from_text_file_help).create().show();
                    }
                });
            }

        });
        return alertDialog;
    }

}


