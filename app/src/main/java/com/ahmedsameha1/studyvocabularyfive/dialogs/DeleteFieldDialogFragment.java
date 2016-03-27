package com.ahmedsameha1.studyvocabularyfive.dialogs;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ahmedsameha1.studyvocabularyfive.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 7/22/2015.
 */
public class DeleteFieldDialogFragment extends DialogFragment {
    public static final String TO_BE_DELETED_FIELD_NAME = "to_be_deleted_field_name";
    private String fieldName;
    private String message;
    private DatabaseOperations databaseOperations;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldName = getArguments().getString(TO_BE_DELETED_FIELD_NAME);
        message = getActivity().getString(R.string.field_deletion_message, fieldName );
        databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_field).setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseOperations.deleteField(fieldName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }
    public interface Contact {
        public void deleteField();
    }
}
