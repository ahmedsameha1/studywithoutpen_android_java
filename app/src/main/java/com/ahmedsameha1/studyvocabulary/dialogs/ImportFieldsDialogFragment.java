package com.ahmedsameha1.studyvocabulary.dialogs;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabulary.R;

import java.io.File;

/**
 * Created by ahmed on 1/4/2016.
 */
public class ImportFieldsDialogFragment extends DialogFragment {
    public static final String IMPORT_FIELD_DIALOG_FRAGMENT = "importFieldDialogFragment";
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
                File exportDirectory = new File(Environment.getExternalStorageDirectory(), ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME);
                if (!exportDirectory.exists()) {
                    exportDirectory.mkdir();
                }
                TextView copyFileIn = (TextView) alertDialog.findViewById(R.id.copy_file_to_import);
                TextView filePath = (TextView) alertDialog.findViewById(R.id.import_file_path);
                final EditText enterFileName = (EditText) alertDialog.findViewById(R.id.enter_import_file_name);
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (Environment.isExternalStorageRemovable() ) {
                    copyFileIn.setText(R.string.copy_import_file_removable);
                } else if ( Environment.isExternalStorageEmulated() ) {
                    copyFileIn.setText(R.string.copy_import_file_emulated);
                }
                filePath.setText(getString(R.string.export_import_file_path,Environment.getExternalStorageDirectory().getAbsolutePath()
                        ,File.separator,ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME));
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String fileName = enterFileName.getText().toString();
                        if ( fileName.length() <= 0 ) {
                            Toast toast = Toast.makeText(context,R.string.enter_file_name_to_import,Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else {
                            File file = new File(Environment.getExternalStorageDirectory().getPath()
                                    + File.separator + ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME + File.separator + fileName.toLowerCase());
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
                                Toast toast = Toast.makeText(context, R.string.plug_your_sdcard, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                                Toast toast = Toast.makeText(context, R.string.external_storage_unmounted, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                            } else if ( ! file.exists() ) {
                                Toast toast = Toast.makeText(context,R.string.file_not_exist,Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                                toast = Toast.makeText(context,R.string.lowercase,Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            } else {
                                dismiss();
                                /*
                                Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                ExportImportDatabase.startImportThread(context,fileName);*/
                                AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(getActivity());
                                importUserUsageData
                                        .setMessage(R.string.enter_number_of_tries_help)
                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0, 400);
                                                toast.show();
                                                toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0, 400);
                                                toast.show();
                                                ExportImportDatabase.startImportThread(context,fileName,false);
                                            }
                                        })
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast toast = Toast.makeText(context,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0, 400);
                                                toast.show();
                                                toast = Toast.makeText(context,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0, 400);
                                                toast.show();
                                                ExportImportDatabase.startImportThread(context,fileName,true);
                                            }
                                        })
                                    .create().show();
                        }
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
}
