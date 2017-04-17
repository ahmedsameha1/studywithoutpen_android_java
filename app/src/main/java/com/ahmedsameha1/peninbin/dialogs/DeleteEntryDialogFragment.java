package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by ahmed on 7/27/2015.
 */
public class DeleteEntryDialogFragment extends DialogFragment {
    public static final String DELETE_ENTRY_DIALOG_FRAGMENT = "deleteEntryDialogFragment";
    public static final String TO_BE_DELETED_ENTRY_ID = "to_be_deleted_entry_id";
    public static final String QUESTION_DATA_TYPE = "question_data_type";
    private long entry_id;
    private int question_data_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entry_id = getArguments().getLong(TO_BE_DELETED_ENTRY_ID);
        question_data_type = getArguments().getInt(QUESTION_DATA_TYPE);
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.delete_entry)
                .setTitle(R.string.delete_an_entry)
                .setPositiveButton(R.string.delete, (dialog, which) -> ((Contact) getActivity()).deleteEntry(entry_id,question_data_type)
                )


        .setNegativeButton(R.string.cancel, (dialog, which) -> {
        }

        );
                    return builder.create();
                }
        public interface Contact {
        public void deleteEntry(long entry_id,int question_data_type);
    }
}
