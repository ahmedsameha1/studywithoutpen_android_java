package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.content.Intent;

import com.ahmedsameha1.peninbin.database.CRUD;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 */
public class WrongAnswerNotProvidedIntentService extends IntentService {

    public static final String ANSWER = "answer";
    public static final String CHECK_TYPE = "check_type";
    public static final String FIELD_ID = "field_id";
    private CRUD crud;

    public WrongAnswerNotProvidedIntentService() {
        super("WrongAnswerNotProvidedIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        crud = new CursorLoaderAndUpdateDatabase(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int check_type = intent.getIntExtra(CHECK_TYPE, 0);
            String answer = intent.getStringExtra(ANSWER);
            long field_id = intent.getLongExtra(FIELD_ID, 0);
            crud.increaseWrongAnswersForEntriesWithSameAnswer(field_id, answer, check_type);
        }
    }

}
