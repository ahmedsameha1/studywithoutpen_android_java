package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by ahmed_000 on 10/20/2015.
 */
public class AskAboutContinuePreviousTestDialogFragment extends DialogFragment {
    public static final String TEST_TYPE = "test_type";
    private long current_field_id;
    private DatabaseReadOperations databaseOperations;
    private int question_data_type;
    private boolean from_notification;
    private int test_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        test_type = getArguments().getInt(TEST_TYPE);
        databaseOperations = new DatabaseReadOperations(getActivity());
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION,false);
    }
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.ask_for_continue_previous_test)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                if ( test_type == Constants.TestType.ENHANCED_TEST) {
                                    intent = new Intent(getActivity(), EnhancedTestActivity.class);
                                    intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(EnhancedTestActivity.LAST_SESSION_OR_NEW_ONE,true);
                                } else if ( test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                                    intent = new Intent(getActivity(), FullyRandomTestActivity.class);
                                    intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(FullyRandomTestActivity.LAST_SESSION_OR_NEW_ONE,true);
                                }
                                startActivity(intent);
                                if ( from_notification ) {
                                    getActivity().finish();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CursorLoaderAndUpdateDatabase mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
                                if ( test_type == Constants.TestType.ENHANCED_TEST ) {
                                    mySQLiteCursorLoader.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                                    mySQLiteCursorLoader.resetEntriesTestOrderEnhancedTest(current_field_id,question_data_type);
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                    EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(getActivity().getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                                    dismiss();
                                } else if ( test_type == Constants.TestType.FULLY_RANDOM_TEST ) {
                                    mySQLiteCursorLoader.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                                    mySQLiteCursorLoader.resetEntriesTestOrderFullyRandomTest(current_field_id,question_data_type);
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                    FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(getActivity().getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                                    dismiss();
                                }
                            }
                        }

                );
        return builder.create();
    }

}
