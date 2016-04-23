package com.ahmedsameha1.studyvocabulary.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


import com.ahmedsameha1.studyvocabulary.activities.StudyActivity;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.StudyDuringActivity;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

/**
 * Created by ahmed_000 on 10/13/2015.
 */
public class AskAboutContinuePreviousStudyDuringDialogFragment extends DialogFragment {
    private long current_field_id;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
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
                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,(current_field_id));
                                intent.putExtra(StudyDuringActivity.LAST_SESSION_OR_NEW_ONE,true);
                                startActivity(intent);
                            }
                        }

                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ((Contact) getActivity()).continueStudyDuring(false);
                                mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
                                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity());
                                mySQLiteCursorLoader
                                        .resetEntriesStudyPeriodOrder(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                                mySQLiteCursorLoader.clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(current_field_id);
                                Bundle bundle = new Bundle();
                                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getActivity().getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
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

