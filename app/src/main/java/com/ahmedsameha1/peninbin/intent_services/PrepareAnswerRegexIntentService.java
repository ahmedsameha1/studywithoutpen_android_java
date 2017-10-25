package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ahmedsameha1.peninbin.database.CRUD;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 */
public class PrepareAnswerRegexIntentService extends IntentService {

    public static final String CHECK_TYPE = "check_type";
    public static final String FIELD_ID = "field_id";
    public static final String RESULT = "prepare_answer_regex_Intent_service_result";
    public static final String ACTIVITY_NAME = "activity_name";
    private static long field_id;
    private static String activity_name;
    private CRUD crud;

    @Override
    public void onCreate() {
        super.onCreate();
        crud = new CursorLoaderAndUpdateDatabase(this);
    }

    public PrepareAnswerRegexIntentService() {
        super("PrepareAnswerRegexIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int check_type = intent.getIntExtra(CHECK_TYPE, 0);
            long field_id = intent.getLongExtra(FIELD_ID, 0);
            String activity_name = intent.getStringExtra(ACTIVITY_NAME);
            PrepareAnswerRegexIntentService.field_id = field_id;
            PrepareAnswerRegexIntentService.activity_name = activity_name;
            String [] array = crud.prepareRegexForAnswers(field_id, check_type);
            Intent result_intent = new Intent(RESULT);
            result_intent.putExtra(RESULT, array);
            LocalBroadcastManager.getInstance(this).sendBroadcast(result_intent);
            PrepareAnswerRegexIntentService.field_id = 0;
            PrepareAnswerRegexIntentService.activity_name = null;
        }
    }

    public static boolean checkRunningForThisActivity( String activity_name, long field_id ) {
        if ( field_id == 0 && activity_name == null ) {
            return false;
        } else {
            if ( PrepareAnswerRegexIntentService.field_id == field_id
                    && PrepareAnswerRegexIntentService.activity_name.equals(activity_name) ) {
                return true;
            } else {
                return false;
            }
        }
    }
}
