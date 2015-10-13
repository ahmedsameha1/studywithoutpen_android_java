package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ahmed_000 on 10/13/2015.
 */
public class AskAboutContinuePreviousStudyDuringDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.ask_for_continue_previous_study_during)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                        ((Contact) getActivity()).continueStudyDuring(true);
                            }
                        }

                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Contact) getActivity()).continueStudyDuring(false);
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                                databaseOperations.clearStudyDuringTempTable( ((FieldActivity) getActivity() ).getCurrentField());
                                dismiss();
                            }
                        }

                );
        return builder.create();
    }
    public interface Contact {
        public void continueStudyDuring( boolean decision );
    }
}
