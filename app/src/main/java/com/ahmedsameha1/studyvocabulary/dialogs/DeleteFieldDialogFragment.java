package com.ahmedsameha1.studyvocabulary.dialogs;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.ahmedsameha1.studyvocabulary.adapters.MainActivityRecyclerViewViewHolder;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

/**
 * Created by ahmed on 7/22/2015.
 */
public class DeleteFieldDialogFragment extends DialogFragment {
    public static final String TO_BE_DELETED_FIELD_NAME = "to_be_deleted_field_name";
    public static final java.lang.String TO_BE_DELETED_FIELD_ID = "to_be_deleted_field_id";
    private String fieldName;
    private long field_id;
    private String message;
    private MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder;

    public void setMainActivityRecyclerViewViewHolder(MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder) {
        this.mainActivityRecyclerViewViewHolder = mainActivityRecyclerViewViewHolder;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        field_id = getArguments().getLong(TO_BE_DELETED_FIELD_ID);
        fieldName = getArguments().getString(TO_BE_DELETED_FIELD_NAME);
        message = getActivity().getString(R.string.field_deletion_message, fieldName );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_field).setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Contact) getActivity()).deleteField(field_id);
                        mainActivityRecyclerViewViewHolder.progressBar.setVisibility(View.VISIBLE);
                        mainActivityRecyclerViewViewHolder.isDeleting = true;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }
    public interface Contact {
        public void deleteField(long field_id);
    }
}
