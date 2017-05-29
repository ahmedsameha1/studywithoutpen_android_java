package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.ahmedsameha1.peninbin.database.DatabaseHelper.DATABASE_NAME;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ImportProvidedFieldDialogFragment extends DialogFragment {


    private DatabaseReadOperations databaseOperations;
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
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.import_provided_field_dialog_fragment, null))
                .setTitle(R.string.import_provided_field)
                .setPositiveButton(R.string.import_, null)
                .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(dialog -> {
            try {
                String app_database_path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
                String temp_database_path = app_database_path.substring(0, app_database_path.length() - 4) + "temp";
                File temp_database_file = new File(temp_database_path);
                if ( !temp_database_file.exists() ) {
                    OutputStream os = new FileOutputStream(temp_database_path);
                    InputStream is = context.getAssets().open("sqlite" + File.separator + DATABASE_NAME);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    is.close();
                    os.flush();
                    os.close();
                }
                SQLiteDatabase temp_database = SQLiteDatabase.openDatabase(temp_database_path, null, SQLiteDatabase.OPEN_READWRITE);
                if ( temp_database == null ) {
                    throw new IOException();
                }
                databaseOperations = new DatabaseReadOperations(context);
                listView = (ListView) alertDialog.findViewById(R.id.export_fields_list_view);
                Cursor field_cursor = temp_database.query(Contract.Field.TABLE_NAME, new String[]{Contract.COLUMN_ID, Contract.Field.COLUMN_FIELD_NAME},
                        null,null,null,null,null);
                SimpleCursorAdapter simpleCursorAdapter =
                        new SimpleCursorAdapter(context,
                                android.R.layout.simple_list_item_multiple_choice,
                                field_cursor,
                                new String[]{Contract.Field.COLUMN_FIELD_NAME},
                                new int[]{android.R.id.text1}, 0);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(simpleCursorAdapter);
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
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
                    } else {
                        ExportImportDatabase.startImportInternalFieldThread(getActivity(), temp_database, selectedFields);
                        dismiss();
                    }
                });
            } catch (IOException ioe) {
                Toast toast = Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                dismiss();
            }

        });
        return alertDialog;
    }
}
