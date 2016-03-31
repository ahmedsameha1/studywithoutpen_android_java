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
import com.ahmedsameha1.studyvocabulary.activities.TestActivity;

/**
 * Created by ahmed_000 on 10/20/2015.
 */
public class AskAboutContinuePreviousTestDialogFragment extends DialogFragment {
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
        builder.setMessage(R.string.ask_for_continue_previous_test)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), TestActivity.class);
                                intent.putExtra(TestActivity.CURRENT_FIELD, (currentField));
                                intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE,true);
                                startActivity(intent);
                            }
                        }
                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
/*                                ((Contact) getActivity()).continueStudyDuring(false);
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                                databaseOperations.clearStudyDuringDataFromLinkerTable( (currentField);
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                *//*editor.putString(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES, null);
                                editor.putInt(StudyDuringActivity.CURRENT_QUESTION_COUNTER, 0);*//*
                                editor.remove(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                        + StudyDuringActivity.DASH + (currentField);
                                editor.remove(StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                        + StudyDuringActivity.DASH + (currentField);
                                editor.commit();
                                dismiss();*/
//                                ((Contact) getActivity()).continueTest(false);
                                Bundle bundle = new Bundle();
                                bundle.putString(StudyActivity.CURRENT_FIELD,currentField);
                                TestDialogFragment dialogFragment = new TestDialogFragment();
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getActivity().getSupportFragmentManager(), TestDialogFragment.TEST_DIALOG_FRAGMENT);
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                                databaseOperations.clearLinkerTableWrongAnswers((currentField));
                                databaseOperations.clearLinkerTableTestOrder((currentField));
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.CURRENT_QUESTION_COUNTER
                                + TestActivity.DASH + (currentField));
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.NUMBER_OF_WRONG_ANSWERS
                                + TestActivity.DASH + (currentField));
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.PERMITED_TRIES_NUMBER
                                + TestActivity.DASH + (currentField));
                                editor.commit();
                            }
                        }

                );
        return builder.create();
    }

}
