package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ahmed_000 on 10/20/2015.
 */
public class AskAboutContinuePreviousTestDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.ask_for_continue_previous_test)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//
                                Intent intent = new Intent(getActivity(), TestActivity.class);
                                intent.putExtra(TestActivity.CURRENT_FIELD, ((FieldActivity) getActivity() ).getCurrentField());
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
                                databaseOperations.clearStudyDuringDataFromLinkerTable( ((FieldActivity) getActivity() ).getCurrentField());
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                *//*editor.putString(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES, null);
                                editor.putInt(StudyDuringActivity.CURRENT_QUESTION_COUNTER, 0);*//*
                                editor.remove(StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                        + StudyDuringActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.remove(StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                        + StudyDuringActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.commit();
                                dismiss();*/
//                                ((Contact) getActivity()).continueTest(false);
                                Intent intent = new Intent(getActivity(), TestActivity.class);
                                intent.putExtra(TestActivity.CURRENT_FIELD, ((FieldActivity) getActivity() ).getCurrentField());
                                intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE, false);
                                startActivity(intent);
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                                databaseOperations.clearLinkerTableWrongAnswers(((FieldActivity) getActivity() ).getCurrentField());
                                databaseOperations.clearLinkerTableTestOrder(((FieldActivity) getActivity() ).getCurrentField());
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.CURRENT_QUESTION_COUNTER
                                + TestActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.NUMBER_OF_WRONG_ANSWERS
                                + TestActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.PERMITED_TRIES_NUMBER
                                + TestActivity.DASH + ((FieldActivity) getActivity() ).getCurrentField());
                                editor.commit();
                            }
                        }

                );
        return builder.create();
    }

}
