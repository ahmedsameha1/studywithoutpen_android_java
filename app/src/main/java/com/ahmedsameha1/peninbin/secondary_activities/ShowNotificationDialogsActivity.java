package com.ahmedsameha1.peninbin.secondary_activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousStudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FullyRandomTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.QuickTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyDuringDialogFragment;
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
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(this);
        long current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, -1);
        if ( databaseOperations.checkFieldExists(current_field_id) ) {
            int dialog_type = getIntent().getIntExtra(DIALOG_TYPE, -1);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int question_data_type = databaseOperations.getQuestionDataType(current_field_id);
            boolean isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
            int field_type = databaseOperations.getFieldType(current_field_id);
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
                    if (!sharedPreferences.contains(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.PAUSING + current_field_id)) {

                        if (databaseOperations.getEntriesNumberForTests(current_field_id,question_data_type) > 0) {
                            if (databaseOperations.isThisFieldTestPausedEnhancedTest(current_field_id)) {
                                AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                fragment3.setArguments(bundle);
                                fragment3.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_TEST_SESSION);
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
                    if (!sharedPreferences.contains(FullyRandomTestActivity.ACTIVITY_NAME + FullyRandomTestActivity.PAUSING + current_field_id)) {

                        if (databaseOperations.getEntriesNumberForTests(current_field_id,question_data_type) > 0) {
                            if (databaseOperations.isThisFieldTestPausedFullyRandomTest(current_field_id)) {
                                AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                fragment3.setArguments(bundle);
                                fragment3.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_TEST_SESSION);
                            } else {
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
                    if (!sharedPreferences.contains(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.PAUSING + current_field_id)) {

                        if (databaseOperations.getEntriesNumberForStudyAPeriod(current_field_id, question_data_type) > 0) {
                            if (databaseOperations.isThisFieldStudyPeriodPaused(current_field_id)) {
                                AskAboutContinuePreviousStudyAPeriodDialogFragment fragment2 = new AskAboutContinuePreviousStudyAPeriodDialogFragment();
                                fragment2.setArguments(bundle);
                                fragment2.show(getSupportFragmentManager(), FieldActivity.CHECK_IF_CONTINUE_STUDY_DURING_SESSION);
                            } else {
                                StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
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
