package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.CRUD;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.DataHolder;
import com.ahmedsameha1.peninbin.view.activities.AskAgainAfterTestActivity;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.view.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.view.activities.StudyAPeriodActivity;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PauseIntentService extends IntentService {

    public static final String CURRENT_NUMBERS = "current_numbers";
    public static final String CURRENT_FIELD_ID = "current_field_id";
    public static final String STUDY_A_PERIOD_REPEATED_ENTRIES = "repeated_entries";
    public static final String ACTIVITY_NAME = "activity_name";
    public static final String QUESTION_DATA_TYPE = "question_data_type";
    public static final String LAST_PAUSE = "last_pause";
    public static final String KEY = "key";
    public static final String WRONG_ITEM_ARRAYLIST = "wrong_item_arraylist";
    public static final String IN_REPEATED = "in_repeated";
    public static final String TEST_TYPE = "test_type";
    public static final String STRING_OF_PAUSING = "string_of_pausing";
    CRUD crud;
    DatabaseReadOperations databaseReadOperations;

    public PauseIntentService() {
        super("PauseIntentService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        crud = new CursorLoaderAndUpdateDatabase(this);
        databaseReadOperations = new DatabaseReadOperations(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String activity_name = intent.getStringExtra(ACTIVITY_NAME);
            String[][] list_for_test = DataHolder.getData(intent.getStringExtra(KEY));
            long current_field_id = intent.getLongExtra(CURRENT_FIELD_ID, 0);
            int[] array = intent.getIntArrayExtra(CURRENT_NUMBERS);
            long last_pause = intent.getLongExtra(LAST_PAUSE, 0);
            if ( activity_name.equals(StudyAPeriodActivity.class.getName())) {
                ArrayList<Integer> repeated_entries = intent.getIntegerArrayListExtra(STUDY_A_PERIOD_REPEATED_ENTRIES);
                crud.pauseStudyAPeriod(current_field_id, list_for_test, array, repeated_entries, last_pause);
                editor.remove(StudyAPeriodActivity.class.getName() + Constants.PAUSING + current_field_id);
            } else if ( activity_name.equals(EnhancedTestActivity.class.getName())) {
                int question_data_type = intent.getIntExtra(QUESTION_DATA_TYPE, 0);
                if (!databaseReadOperations.checkEntriesForAnyTestOrderEnhancedTest(current_field_id, question_data_type)) {
                    crud.putEntriesTestOrderEnhancedTest(question_data_type, list_for_test);
                }
                crud.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                crud.putTestPausedDataEnhancedTest(current_field_id, array, last_pause);
                editor.remove(EnhancedTestActivity.class.getName() + Constants.PAUSING + current_field_id);
            } else if ( activity_name.equals(FullyRandomTestActivity.class.getName())) {
                int question_data_type = intent.getIntExtra(QUESTION_DATA_TYPE, 0);
                if (!databaseReadOperations.checkEntriesForAnyTestOrderFullyRandomTest(current_field_id, question_data_type)) {
                    crud.putEntriesTestOrderFullyRandomTest(
                            question_data_type, list_for_test);
                }
                crud.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                crud.putTestPausedDataFullyRandomTest(current_field_id, array, last_pause);
                editor.remove(FullyRandomTestActivity.class.getName() + Constants.PAUSING + current_field_id);
            } else if ( activity_name.equals(AskAgainAfterTestActivity.class.getName())) {
                ArrayList<Integer> repeated_entries = intent.getIntegerArrayListExtra(STUDY_A_PERIOD_REPEATED_ENTRIES);
                ArrayList<WrongItem> wrongItemArrayList = (ArrayList<WrongItem>) intent.getSerializableExtra(WRONG_ITEM_ARRAYLIST);
                String string_for_pausing = intent.getStringExtra(STRING_OF_PAUSING);
                boolean in_repeated = intent.getBooleanExtra(IN_REPEATED, false);
                int test_type = intent.getIntExtra(TEST_TYPE, 0);
                if (test_type == Constants.TestType.ENHANCED_TEST) {
                    crud.pauseStudyAgainAfterTest(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, current_field_id, wrongItemArrayList, array, repeated_entries, in_repeated, last_pause);
                } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                    crud.pauseStudyAgainAfterTest(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id, wrongItemArrayList, array, repeated_entries, in_repeated, last_pause);
                }
                editor.remove(string_for_pausing);
            }
            editor.commit();
        }
    }


}
