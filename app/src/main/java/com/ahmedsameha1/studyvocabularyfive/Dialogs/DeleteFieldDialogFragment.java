package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 7/22/2015.
 */
public class DeleteFieldDialogFragment extends DialogFragment {
    public static final String CONFIRMATION_MESSAGE = "confirmationMessage";
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString(CONFIRMATION_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Contact) getActivity()).deleteField();
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
