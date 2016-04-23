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
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.TestActivity;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

/**
 * Created by ahmed_000 on 10/20/2015.
 */
public class AskAboutContinuePreviousTestDialogFragment extends DialogFragment {
    private long current_field_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
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
                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE,true);
                                startActivity(intent);
                            }
                        }
                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MySQLiteCursorLoader mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity());
                                mySQLiteCursorLoader.clearThisFieldFromTestPaused(current_field_id);
                                mySQLiteCursorLoader.clearThisFieldFromWrongAnswers(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                                mySQLiteCursorLoader.resetEntriesTestOrder(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                                Bundle bundle = new Bundle();
                                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                TestDialogFragment dialogFragment = new TestDialogFragment();
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getActivity().getSupportFragmentManager(), TestDialogFragment.TEST_DIALOG_FRAGMENT);
                                dismiss();
                            }
                        }

                );
        return builder.create();
    }

}
