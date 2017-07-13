package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;


import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by ahmed_000 on 10/13/2015.
 */
public class AskAboutContinuePreviousStudyAPeriodDialogFragment extends DialogFragment {
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private boolean from_notification;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION,false);
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ask_for_continue_previous_study_during)
                .setPositiveButton(R.string.yes, (dialog , which) -> {
                                Intent intent = new Intent(getActivity(),StudyAPeriodActivity.class);
                                intent.putExtra(Constants.CURRENT_FIELD_ID,(current_field_id));
                                intent.putExtra(StudyAPeriodActivity.LAST_SESSION_OR_NEW_ONE,true);
                                startActivity(intent);
                                if ( from_notification ) {
                                    getActivity().finish();
                                }
                        }
                )
                .setNegativeButton(R.string.no, (dialog , which) -> {
                                cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(getActivity());
                                DatabaseReadOperations databaseOperations = new DatabaseReadOperations(getActivity());
                                cursorLoaderAndUpdateDatabase
                                        .resetEntriesStudyPeriodOrder(current_field_id,Constants.Study_Till_Correct.STUDY_PERIOD,
                                                databaseOperations.getQuestionDataType(current_field_id));
                                cursorLoaderAndUpdateDatabase.clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_PERIOD,
                                        current_field_id);
                                Bundle bundle = new Bundle();
                                bundle.putLong(Constants.CURRENT_FIELD_ID,current_field_id);
                                StudyAPeriodDialogFragment fragment2 = new StudyAPeriodDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getActivity().getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                                dismiss();
                            }
                );
        return builder.create();
    }
}

