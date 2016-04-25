package com.ahmedsameha1.studyvocabulary.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabulary.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ExportFieldsDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";

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
                databaseOperations = new DatabaseOperations(context);
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
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
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
                            Toast toast = Toast.makeText(context, R.string.zero_selected_fields_toast, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if (fileName.length() <= 0) {
                            Toast toast = Toast.makeText(context, R.string.no_file_name_toast, Toast.LENGTH_SHORT);
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
                            } else if (file.exists()) {
                                Toast toast = Toast.makeText(context, R.string.file_with_same_name, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                            } else {
                                alertDialog.dismiss();
                                Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                toast = Toast.makeText(context, R.string.shut_down_warning, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                ExportImportDatabase.startExportThread(context, selectedFields, fileName);
                            }
                        }
                    }
                });
            }

        });
        return alertDialog;
    }
}
