package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.content.Intent;

import com.ahmedsameha1.peninbin.database.CRUD;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class WrongAnswerProvidedIntentService extends IntentService {

    public static final String ANSWER = "answer";
    public static final String CHECK_TYPE = "check_type";
    public static final String REGEX_ANSWERS = "regex_answers";
    private CRUD crud;

    public WrongAnswerProvidedIntentService() {
        super("WrongAnswerProvidedIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        crud = new CursorLoaderAndUpdateDatabase(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String answer = intent.getStringExtra(ANSWER);
            String [] regex_answers = intent.getStringArrayExtra(REGEX_ANSWERS);
            int check_type = intent.getIntExtra(CHECK_TYPE, 0);
            crud.increaseWrongAnswersForEntriesWithSameAnswer(regex_answers, answer, check_type);
        }
    }

}
