package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

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

import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.R;

import java.util.ArrayList;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ExportFieldsDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    public static final String PATH = "path";

    private DatabaseReadOperations databaseOperations;
    private EditText enterFileName;
    private ListView listView;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setRetainInstance(true);
    }


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
                databaseOperations = new DatabaseReadOperations(context);
                enterFileName = (EditText) alertDialog.findViewById(R.id.file_name);
                listView = (ListView) alertDialog.findViewById(R.id.export_fields_list_view);

                SimpleCursorAdapter simpleCursorAdapter =
                        new SimpleCursorAdapter(context,
                                android.R.layout.simple_list_item_multiple_choice,
                                databaseOperations.getFieldList(),
                                new String[]{Contract.Field.COLUMN_FIELD_NAME},
                                new int[]{android.R.id.text1}, 0);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(simpleCursorAdapter);
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String fileName = enterFileName.getText().toString();
                        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                        final ArrayList<String> selectedFields = new ArrayList<String>();
                        for (int i = 0; i < sparseBooleanArray.size(); i++) {
                            int key = sparseBooleanArray.keyAt(i);
                            if (sparseBooleanArray.get(key)) {
                                selectedFields.add(((Cursor) listView.getItemAtPosition(key)).getString(1));
                            }
                        }
                        if (selectedFields.size() <= 0) {
                            Toast toast = Toast.makeText(context, R.string.select_one, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } else if (fileName.length() <= 0) {
                            Toast toast = Toast.makeText(context, R.string.no_file_name_toast, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } else {
                            new AlertDialog.Builder(getActivity()).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((Contact) ExportFieldsDialogFragment.this.getActivity()).exportFields(selectedFields, fileName);
                                    ExportFieldsDialogFragment.this.dismiss();
                                }
                            }).create().show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface Contact {
        void exportFields(ArrayList<String> selectedFields, String file_name);
    }
}
