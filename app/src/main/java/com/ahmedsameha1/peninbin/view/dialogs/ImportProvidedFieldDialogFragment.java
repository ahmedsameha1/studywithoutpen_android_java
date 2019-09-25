package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.intent_services.ImportProvidedFieldsIntentService;

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
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                try {
                    String app_database_path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
                    String temp_database_path = app_database_path.substring(0, app_database_path.length() - 4) + "temp";
                    final SQLiteDatabase temp_database = SQLiteDatabase.openDatabase(temp_database_path, null, SQLiteDatabase.OPEN_READWRITE);
                    databaseOperations = new DatabaseReadOperations(context);
                    listView = (ListView) alertDialog.findViewById(R.id.export_fields_list_view);
                    final Cursor field_cursor = temp_database.query(Contract.Field.TABLE_NAME, new String[]{Contract.COLUMN_ID, Contract.Field.COLUMN_FIELD_NAME},
                            null, null, null, null, null);
                    SimpleCursorAdapter simpleCursorAdapter =
                            new SimpleCursorAdapter(context,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    field_cursor,
                                    new String[]{Contract.Field.COLUMN_FIELD_NAME},
                                    new int[]{android.R.id.text1}, 0);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(simpleCursorAdapter);
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                                Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                temp_database.close();
                                Intent intent = new Intent(((Activity) context).getBaseContext(), ImportProvidedFieldsIntentService.class);
                                intent.putExtra(ImportProvidedFieldsIntentService.FIELDS, selectedFields);
                                context.startService(intent);
                                ImportProvidedFieldDialogFragment.this.dismiss();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast toast = Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                    ImportProvidedFieldDialogFragment.this.dismiss();
                }

            }
        });
        return alertDialog;
    }

}
