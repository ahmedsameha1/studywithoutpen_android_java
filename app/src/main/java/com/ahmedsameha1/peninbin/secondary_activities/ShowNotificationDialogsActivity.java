package com.ahmedsameha1.peninbin.secondary_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.AskAgainAfterTestActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousStudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FullyRandomTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.QuickTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.EnhancedTestDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;

public class ShowNotificationDialogsActivity extends AppCompatActivity {

    public static final String DIALOG_TYPE = "dialog_type";
    public static final int QUICK_TEST_DIALOG = 0;
    public static final int ENHANCED_TEST_DIALOG = 1;
    public static final int STUDY_PERIOD_DIALOG = 2;
    public static final String FROM_NOTIFICATION = "from_notification";
    public static final int FULLY_RANDOM_TEST_DIALOG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification_dialogs);
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(this);
        CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        long current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, -1);
        if ( databaseReadOperations.checkFieldExists(current_field_id) ) {
            int dialog_type = getIntent().getIntExtra(DIALOG_TYPE, -1);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int question_data_type = databaseReadOperations.getQuestionDataType(current_field_id);
            boolean isEmulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
            int field_type = databaseReadOperations.getFieldType(current_field_id);
            Bundle bundle = new Bundle();
            bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
            bundle.putBoolean(FROM_NOTIFICATION, true);
            switch (dialog_type) {
                case QUICK_TEST_DIALOG:

                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    QuickTestDialogFragment quickTestDialogFragment = new QuickTestDialogFragment();
                    quickTestDialogFragment.setArguments(bundle);
                    quickTestDialogFragment.show(getSupportFragmentManager(), QuickTestDialogFragment.QUICK_TEST_DIALOG);

                    break;
                case ENHANCED_TEST_DIALOG:
                    if (!sharedPreferences.contains(EnhancedTestActivity.class.getName() + EnhancedTestActivity.PAUSING + current_field_id)
                            && !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED + current_field_id)) {

                        if (databaseReadOperations.getEntriesNumberForTests(current_field_id,question_data_type) > 0) {
                            if (databaseReadOperations.isThisFieldTestPausedEnhancedTest(current_field_id)) {
                                AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                fragment3.setArguments(bundle);
                                fragment3.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_TEST_SESSION);
                            } else if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, current_field_id)) {
                                new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                            Intent intent = new Intent(this, AskAgainAfterTestActivity.class);
                                            intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                            intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                                            intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                            startActivity(intent);
                                        })
                                        .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                                            cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, question_data_type);
                                            cursorLoaderAndUpdateDatabase.
                                                    clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST,
                                                            current_field_id);
                                            EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                            dialogFragment.setArguments(bundle);
                                            dialogFragment.show(getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                                        }).create();
                            } else {
                                EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                            }
                        } else {
                            Toast toast = Toast.makeText(this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            if ( isEmulated ) {
                                Toast toast1 = Toast.makeText(this, R.string.field_emulated, Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            }
                        }

                    } else {
                        Toast toast = Toast.makeText(this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    break;
                case FULLY_RANDOM_TEST_DIALOG:
                    if (!sharedPreferences.contains(FullyRandomTestActivity.class.getName() + FullyRandomTestActivity.PAUSING + current_field_id)
                            && !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM + current_field_id)) {

                        if (databaseReadOperations.getEntriesNumberForTests(current_field_id,question_data_type) > 0) {
                            if (databaseReadOperations.isThisFieldTestPausedFullyRandomTest(current_field_id)) {
                                AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                fragment3.setArguments(bundle);
                                fragment3.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_TEST_SESSION);
                            } else if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id)) {
                                new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                            Intent intent = new Intent(this, AskAgainAfterTestActivity.class);
                                            intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                            intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                                            intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                            startActivity(intent);
                                        })
                                        .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                                            cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST,question_data_type);
                                            cursorLoaderAndUpdateDatabase.clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id);
                                            FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                                            dialogFragment.setArguments(bundle);
                                            dialogFragment.show(getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                                        }).create();
                            }else {
                                FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                                dialogFragment.setArguments(bundle);
                                dialogFragment.show(getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                            }
                        } else {
                            Toast toast = Toast.makeText(this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            if ( isEmulated ) {
                                Toast toast1 = Toast.makeText(this, R.string.field_emulated, Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            }
                        }

                    } else {
                        Toast toast = Toast.makeText(this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    break;
                case STUDY_PERIOD_DIALOG:
                    if (!sharedPreferences.contains(StudyAPeriodActivity.class.getName() + StudyAPeriodActivity.PAUSING + current_field_id)) {

                        if (databaseReadOperations.getEntriesNumberForStudyAPeriod(current_field_id, question_data_type) > 0) {
                            if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_PERIOD, current_field_id)) {
                                AskAboutContinuePreviousStudyAPeriodDialogFragment fragment2 = new AskAboutContinuePreviousStudyAPeriodDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_STUDY_DURING_SESSION);
                            } else {
                                StudyAPeriodDialogFragment fragment2 = new StudyAPeriodDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                            }
                        } else {
                            Toast toast = Toast.makeText(this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            if ( isEmulated ) {
                                Toast toast1 = Toast.makeText(this, R.string.field_emulated, Toast.LENGTH_SHORT);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(this, R.string.wait_until_study_during_pausing_finish, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    break;
            }
        } else {
            Toast toast = Toast.makeText(this, R.string.field_doesnot_exist, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
            finish();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }
}
