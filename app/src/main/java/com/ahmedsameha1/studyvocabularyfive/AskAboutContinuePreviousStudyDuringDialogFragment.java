package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                /*editor.putString(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES, null);
                                editor.putInt(StudyDuringActivity.CURRENT_QUESTION_COUNTER, 0);*/
                                editor.remove(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                        + StudyDuringActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.remove(StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                        + StudyDuringActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.commit();
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
