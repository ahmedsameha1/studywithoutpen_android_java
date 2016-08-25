package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;

/**
 * Created by a on 7/25/16.
 */
public class CopyFieldDialogFragment extends DialogFragment {
    public static final String COPY_FIELD_DIALOG_FRAGMENT = "copy_field_dialog_fragment";

    private DatabaseOperations databaseOperations;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        databaseOperations = new DatabaseOperations(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.select_field)
                        .setView(inflater.inflate(R.layout.copy_field_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Spinner existing_fields = (Spinner) alertDialog.findViewById(R.id.existing_fields);
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,databaseOperations.getNonNotebookFieldList(),
                        new String[]{Contract.Field.COLUMN_FIELD_NAME}, new int[]{android.R.id.text1},0);
                simpleCursorAdapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                existing_fields.setAdapter(simpleCursorAdapter);
                Button positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ( (Contact) getActivity()).copyEntries(existing_fields.getSelectedItemId());
                        dismiss();
                        getActivity().finish();
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface Contact {
       void copyEntries(long field_id );
    }
}
