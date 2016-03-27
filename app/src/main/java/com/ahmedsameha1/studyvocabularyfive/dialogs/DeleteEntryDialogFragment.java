package com.ahmedsameha1.studyvocabularyfive.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.studyvocabularyfive.activities.StudyActivity;
import com.ahmedsameha1.studyvocabularyfive.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 7/27/2015.
 */
public class DeleteEntryDialogFragment extends DialogFragment {
    public static final String DELETE_ENTRY_DIALOG_FRAGMENT = "deleteEntryDialogFragment";
    private String current_field;
    private String current_question;
    private String current_answer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field = getArguments().getString(StudyActivity.CURRENT_FIELD);
        current_question = getArguments().getString(StudyActivity.CURRENT_QUESTION);
        current_answer = getArguments().getString(StudyActivity.CURRENT_ANSWER);
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.delete_entry)
                .setTitle(R.string.delete_an_entry)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DatabaseOperations(getActivity().getContentResolver()).deleteFieldEntry(current_question, current_answer, current_field);
                            }
                        }
                )


        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }

        );
                    return builder.create();
                }
        public interface Contact {
        public void deleteFieldEntry(DeleteEntryDialogFragment dialog);
    }
}
