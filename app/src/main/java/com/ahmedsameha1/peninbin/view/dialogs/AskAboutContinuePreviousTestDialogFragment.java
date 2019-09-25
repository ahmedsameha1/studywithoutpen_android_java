package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.view.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
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
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
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
                                if (test_type == Constants.TestType.ENHANCED_TEST) {
                                    intent = new Intent(AskAboutContinuePreviousTestDialogFragment.this.getActivity(), EnhancedTestActivity.class);
                                    intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(EnhancedTestActivity.LAST_SESSION_OR_NEW_ONE, true);
                                } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                                    intent = new Intent(AskAboutContinuePreviousTestDialogFragment.this.getActivity(), FullyRandomTestActivity.class);
                                    intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(FullyRandomTestActivity.LAST_SESSION_OR_NEW_ONE, true);
                                }
                                AskAboutContinuePreviousTestDialogFragment.this.startActivity(intent);
                                if (from_notification) {
                                    AskAboutContinuePreviousTestDialogFragment.this.getActivity().finish();
                                }
                            }
                        }
                )
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(AskAboutContinuePreviousTestDialogFragment.this.getActivity());
                                if (test_type == Constants.TestType.ENHANCED_TEST) {
                                    cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                                    cursorLoaderAndUpdateDatabase.resetEntriesTestOrderEnhancedTest(current_field_id, question_data_type);
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                                    EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(AskAboutContinuePreviousTestDialogFragment.this.getActivity().getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                                    AskAboutContinuePreviousTestDialogFragment.this.dismiss();
                                } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                                    cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                                    cursorLoaderAndUpdateDatabase.resetEntriesTestOrderFullyRandomTest(current_field_id, question_data_type);
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                                    FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(AskAboutContinuePreviousTestDialogFragment.this.getActivity().getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                                    AskAboutContinuePreviousTestDialogFragment.this.dismiss();
                                }
                            }
                        }
                );
        return builder.create();
    }

}
