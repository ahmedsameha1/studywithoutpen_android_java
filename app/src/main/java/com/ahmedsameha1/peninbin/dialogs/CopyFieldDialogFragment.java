package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by a on 7/25/16.
 */
public class CopyFieldDialogFragment extends DialogFragment {
    public static final String COPY_FIELD_DIALOG_FRAGMENT = "copy_field_dialog_fragment";
    public static final String WHAT_TO_DO = "what_to_do";
    private DatabaseReadOperations databaseOperations;
    private int what_to_do;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        what_to_do = getArguments().getInt(WHAT_TO_DO);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        databaseOperations = new DatabaseReadOperations(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.select_field)
                        .setView(inflater.inflate(R.layout.copy_field_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(dialog -> {
            final Spinner existing_fields = (Spinner) alertDialog.findViewById(R.id.existing_fields);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,databaseOperations.getFieldList(),
                    new String[]{Contract.Field.COLUMN_FIELD_NAME}, new int[]{android.R.id.text1},0);
            simpleCursorAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            existing_fields.setAdapter(simpleCursorAdapter);
            Button positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positive_button.setOnClickListener(view -> {
                if ( what_to_do == Constants.CopyMove.COPY_FIELD) {
                    ( (CopyField) getActivity()).copyField(existing_fields.getSelectedItemId());
                } else if ( what_to_do == Constants.CopyMove.COPY_ENTRIES ){
                    ( (CopyEntries) getActivity()).copyEntries(existing_fields.getSelectedItemId());
                } else if ( what_to_do == Constants.CopyMove.MOVE_ENTRIES ) {
                    ( (MoveEntries) getActivity()).moveEntries(existing_fields.getSelectedItemId());
                }
                dismiss();
                getActivity().finish();
            });
        });
        return alertDialog;
    }
    public interface CopyEntries {
       void copyEntries(long field_id );
    }
    public interface MoveEntries {
       void moveEntries(long field_id );
    }
    public interface CopyField {
       void copyField(long field_id );
    }

}
