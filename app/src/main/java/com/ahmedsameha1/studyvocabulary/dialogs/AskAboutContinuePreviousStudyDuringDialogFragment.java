package com.ahmedsameha1.studyvocabulary.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


import com.ahmedsameha1.studyvocabulary.activities.StudyActivity;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.StudyDuringActivity;

/**
 * Created by ahmed_000 on 10/13/2015.
 */
public class AskAboutContinuePreviousStudyDuringDialogFragment extends DialogFragment {
    private String currentField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentField = getArguments().getString(StudyActivity.CURRENT_FIELD);
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.ask_for_continue_previous_study_during)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                        ((Contact) getActivity()).continueStudyDuring(true);
                                Intent intent = new Intent(getActivity(),StudyDuringActivity.class);
                                intent.putExtra(StudyDuringActivity.CURRENT_FIELD,(currentField));
                                intent.putExtra(StudyDuringActivity.LAST_SESSION_OR_NEW_ONE,true);
                                startActivity(intent);
                            }
                        }

                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ((Contact) getActivity()).continueStudyDuring(false);
                                Bundle bundle = new Bundle();
                                bundle.putString(StudyActivity.CURRENT_FIELD,currentField);
                                StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getActivity().getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                                databaseOperations.clearStudyDuringDataFromLinkerTable(currentField);
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                /*editor.putString(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES, null);
                                editor.putInt(StudyDuringActivity.CURRENT_QUESTION_COUNTER, 0);*/
                                editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                        StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                        + StudyDuringActivity.DASH + (currentField));
                                editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                        StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                        + StudyDuringActivity.DASH + (currentField));
                                editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.NUMBER_OF_TRIES +StudyDuringActivity.DASH + (currentField));
                                editor.commit();
                                dismiss();
                            }
                        }

                );
        return builder.create();
    }
/*    public interface Contact {
        public void continueStudyDuring( boolean decision );
    }*/
}

