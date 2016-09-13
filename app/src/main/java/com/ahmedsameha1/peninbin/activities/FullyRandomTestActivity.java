package com.ahmedsameha1.peninbin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

public class FullyRandomTestActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener {

    public static final String SCREEN_NAME = "FullyRandomTestActivity";
    public static final String ACTIVITY_NAME = "FullyRandomTestActivity";
    public static final String PERCENTAGE_OR_NUMBER = "percentage_or_number";
    public static final String QUESTION_PERCENTAGE = "question_percentage";
    public static final String QUESTION_NUMBER = "question_number";
    public static final int PERCENTAGE = 0;
    public static final int NUMBER = 1;
    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int CONTINUE_PREVIOUS_SESSION_LOADER_ID = 1;
    public static final int GET_SPECIAL_TEXT_LOADER_ID = 2;
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int RETURN_TIME = 1000;
    public static final String DASH = "-";
    public static final int START_NEW_SESSION = 2;
    public static final String LIST_FOR_TEST = "listForTest";
    public static final String CURRENT_QUESTION_COUNTER = "currentQuestionCounter";
    public static final String TRIES_COUNTER = "triesCounter";
    public static final String NEXT_BUTTON_STATE = "newtButtonState";
    public static final String RESULT = "result";
    public static final String PERMITED_TRIES_NUMBER = "permitedTriesNumber";
    public static final String TESTING_NOW = "testingNow";
    public static final String NUMBER_OF_WRONG_ANSWERS = "numberOfWrongAnswers";
    public static final String NUMBER_OF_QUESTIONS = "numberOfQuestions";
    public static final String HINT_COUNTER = "hintCounter";
    public static final String HINTS = "hints";
    public static final String WRONG_ANSWERS = "wrongAnswers";
    public static final String LAST_SESSION_OR_NEW_ONE = "lastSessionOrNewOne";
    public static final String BOOLEAN_CONTINUE = "booleanContinue";
    private static final String QUESTION_LAYOUT = "user_defined_question_layout";
    private static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    private static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    public static final String PAUSING = "pausing";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    private long current_field_id;
    private String [][] listForTest;
    private ArrayList<String> hints, wrongAnswers;
    private boolean boolean_continue = false, actionBarShown;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private boolean result, testingNow = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber,wrongAnswersCounter , percent_or_number,number;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private double percentage;
    private Cursor fieldEntriesCursor, searchForCursor;
    private Toolbar toolbar;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter,showTries, showNumberOfQuestions,check,nextHint,showActionbar;
    private LinearLayout showResult,questionLinearlayout, container;
    private Handler mHandler = new Handler();
    private Tracker mTracker;
    private Thread pauseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Runnable pauseRunnable;
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                toPause();
            } finally {
                pauseRunnable = new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FullyRandomTestActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(ACTIVITY_NAME + PAUSING + current_field_id);
                        editor.commit();
                        Toast toast = Toast.makeText(FullyRandomTestActivity.this, R.string.pause_shutdown, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                };
                mHandler.post(pauseRunnable);
            }
        }
    });
    private int question_data_type;
    private int check_type;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private DatabaseOperations databaseOperations;
    private RetainedFragment retainedFragment; // to save the list for test array when the activity orientation change
    private RelativeLayout content;
    private int mShortAnimationDuration;
    private View mLoadingView;
    private LoaderManager.LoaderCallbacks<Cursor> loader,get_entries, get_symbol_string;
    private String[] symbol, customable_text;
    private int current_field_type;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        int question_area_size,answer_area_size,test_text_size,action_bar_shown;
        boolean isReadAnswer;
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if ( retainedFragment == null ) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment,LIST_FOR_TEST).commit();
        }
        databaseOperations = new DatabaseOperations(this);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        listForTest = retainedFragment.getListForTest_TestActivity();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,0);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        permitedTriesNumber = getIntent().getIntExtra(PERMITED_TRIES_NUMBER, 1);
        percent_or_number = getIntent().getIntExtra(PERCENTAGE_OR_NUMBER, PERCENTAGE);
        number = getIntent().getIntExtra(QUESTION_NUMBER, 0);
        percentage = getIntent().getDoubleExtra(QUESTION_PERCENTAGE, 0);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        check = (TextView) findViewById(R.id.check);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);


        showTries = (TextView) findViewById(R.id.trys);

        nextHint = (TextView) findViewById(R.id.next_hint);
        showNumberOfQuestions = (TextView) findViewById(R.id.number_of_questions);
        showResult = (LinearLayout) findViewById(R.id.result);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        final CardView show_actionbar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView check_card_view = (CardView) findViewById(R.id.check_card_view);
        final CardView next_hint_card_view = (CardView) findViewById(R.id.next_hint_card_view);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        if ( savedInstanceState == null ) {
            content.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        } else {
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE? View.VISIBLE : View.GONE);
        }
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        setSupportActionBar(toolbar);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        if ( action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show );
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        if ( isReadAnswer ) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if ( id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    return new MySQLiteCursorLoader(FullyRandomTestActivity.this,
                            "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a , se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " >= ? ",
                            new String[]{current_field_id + "", "0"});
                } else if ( id == GET_ENTRIES_LOADER_ID ){
                    return new MySQLiteCursorLoader(FullyRandomTestActivity.this,
                            "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a " + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                            new String[]{current_field_id + ""});
                } else if ( id == GET_SPECIAL_TEXT_LOADER_ID ) {
                    return new MySQLiteCursorLoader(FullyRandomTestActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
                            + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                            + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                            " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                            + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ?",
                            new String[]{current_field_type + ""});
                }
                return null;
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                int id = loader.getId();
                if ( id == GET_ENTRIES_LOADER_ID || id == CONTINUE_PREVIOUS_SESSION_LOADER_ID ) {
                    fieldEntriesCursor = data;
                    if ( boolean_continue ) {
                        if ( listForTest == null ) {
                            listForTest = readTestDataFromLinkerTable();
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            showTries.setText(Integer.toString(triesCounter + 1));
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            enterAnswer.setEnabled(true);
                            enterAnswer.requestFocus();
                            testingNow = true;
                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                            boolean_continue = false;
                            ifMoreThanOnce();
                        }
                    } else {
                        if ( listForTest == null ) {
                            if ( percent_or_number == PERCENTAGE ) {
                                listForTest = getListForTest(percentage);
                            } else if ( percent_or_number == NUMBER ) {
                                listForTest = getListForTest(number);
                            }
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            showTries.setText(Integer.toString(triesCounter + 1));
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            enterAnswer.requestFocus();
                            testingNow = true;
                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                            boolean_continue = false;
                            ifMoreThanOnce();
                        }
                    }
                    crossfade();
                } else if ( id == GET_SPECIAL_TEXT_LOADER_ID ) {
                    if (symbol == null && customable_text == null) {
                        symbol = new String[data.getCount()];
                        customable_text = new String[data.getCount()];
                        for (data.moveToFirst(); data.getPosition() < data.getCount();
                             data.moveToNext()) {
                            symbol[data.getPosition()] = data.getString(1);
                            customable_text[data.getPosition()] = data.getString(2);
                        }
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                int id = loader.getId();
                if ( id == GET_ENTRIES_LOADER_ID || id == CONTINUE_PREVIOUS_SESSION_LOADER_ID ) {
                    fieldEntriesCursor = null;
                }
            }
        };
        get_entries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if ( id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    return new MySQLiteCursorLoader(FullyRandomTestActivity.this,
                            "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a , se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " >= ? ",
                            new String[]{current_field_id + "", "0"});
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,uri,
                    new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                    new String[] {"0", currentField},
                    null);*/
                } else {
                    return new MySQLiteCursorLoader(FullyRandomTestActivity.this,
                            "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a " + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                            new String[]{current_field_id + ""});
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,
                    uri,
                    new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                    new String[]{currentField},
                    null);*/
                }
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                fieldEntriesCursor = data;
                if ( boolean_continue ) {
                    if ( listForTest == null ) {
                        listForTest = readTestDataFromLinkerTable();
                        questionsNumber = listForTest.length;
                        showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                        showTries.setText(Integer.toString(triesCounter + 1));
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        enterAnswer.setEnabled(true);
                        enterAnswer.requestFocus();
                        testingNow = true;
                        wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        boolean_continue = false;
                        ifMoreThanOnce();
                    }
                } else {
                    if ( listForTest == null ) {
                        if ( percent_or_number == PERCENTAGE ) {
                            listForTest = getListForTest(percentage);
                        } else if ( percent_or_number == NUMBER ) {
                            listForTest = getListForTest(number);
                        }
                        questionsNumber = listForTest.length;
                        showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                        showTries.setText(Integer.toString(triesCounter + 1));
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        enterAnswer.requestFocus();
                        testingNow = true;
                        wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        boolean_continue = false;
                        ifMoreThanOnce();
                    }
                }
                crossfade();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                fieldEntriesCursor = null;
            }
        };
        get_symbol_string = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new MySQLiteCursorLoader(FullyRandomTestActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
                        + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                        + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                        " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                        + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ?",
                        new String[]{current_field_type + ""});

            }

            @Override
            public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
                if (symbol == null && customable_text == null) {
                    symbol = new String[data.getCount()];
                    customable_text = new String[data.getCount()];
                    for (data.moveToFirst(); data.getPosition() < data.getCount();
                         data.moveToNext()) {
                        symbol[data.getPosition()] = data.getString(1);
                        customable_text[data.getPosition()] = data.getString(2);
                    }
                }
            }

            @Override
            public void onLoaderReset(android.content.Loader<Cursor> loader) {

            }
        };
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_card_view.startAnimation(animAlpha);
                if (enterAnswer.getText().toString().length() > 0) {
                    result = checkAnAnswer();
                    if (triesCounter < permitedTriesNumber && !result) {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        showTries.setText(Integer.toString(triesCounter + 1));
                    } else {
                        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                        currentQuestionCounter++;
                        if (currentQuestionCounter >= questionsNumber) {
                            check.setEnabled(false);
//                            start.setEnabled(true);


                            final ShowTestResultDialogFragment fragment = new ShowTestResultDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
                            bundle.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
                            bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                            bundle.putInt(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
//                            wrongItems.trimToSize();
//                            bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItems);
                            fragment.setArguments(bundle);
                            Runnable mRunnable;
                            mRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    showQuestion.setText(R.string.test_has_completed);
                                    enterAnswer.setText(R.string.test_has_completed);
                                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                                    showResult.setPadding(0,0,0,0);
                                    fragment.show(getSupportFragmentManager(), ShowTestResultDialogFragment.SHOW_TEST_RESULT_DIALOG_FRAGMENT);
                                }
                            };
                            mHandler.postDelayed(mRunnable, RETURN_TIME);
                            currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                            questionsNumber = 0;
                            wrongAnswersCounter = 0;
                            testingNow = false;
                            enterAnswer.setEnabled(false);
                            showNumberOfQuestions.setText("");
                            remainingQuestionCounter.setText("");
                            showTries.setText("");
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
//                            getSupportLoaderManager().restartLoader(START_NEW_SESSION, null, FullyRandomTestActivity.this);
                            mySQLiteCursorLoader.resetEntriesTestOrderFullyRandomTest(current_field_id,question_data_type);
                            mySQLiteCursorLoader.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            showQuestion.scrollTo(0,0);
                            showTries.setText(Integer.toString(triesCounter + 1));
                            ifMoreThanOnce();
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(FullyRandomTestActivity.this, R.string.where_the_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                enterAnswer.setHint(R.string.enter_your_answer);
            }
        });
        enterAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    check.setEnabled(true);
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
                if ( s.length() <= 0 ) {
                    check.setEnabled(false);
                }
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = s.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enterAnswer.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enterAnswer.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                        }
                    }
                }
            }
        });
        nextHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_hint_card_view.startAnimation(animAlpha);
                if (hints != null) {
                    if (enterAnswer.getText().toString().length() <= 0) {
                        enterAnswer.setHint(hints.get(hintCounter++));
                    } else {
                        Toast toast = Toast.makeText(FullyRandomTestActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
            }
        });
        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_actionbar_card_view.startAnimation(animAlpha);
                if ( actionBarShown ) {
                    getSupportActionBar().hide();
                    actionBarShown = (! actionBarShown);
                    showActionbar.setText(R.string.show);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id,Contract.Field.DO_NOT_SHOW_ACTION_BAR);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (! actionBarShown);
                    showActionbar.setText(R.string.hide);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id,Contract.Field.SHOW_ACTION_BAR);
                }
            }
        });
        showNumberOfQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuestions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        remainingQuestionCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        showTries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        if ( boolean_continue ) {
            getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID,null, loader);
            int[] array = databaseOperations.getDataFromTestPausedFullyRandomTest(current_field_id);
            currentQuestionCounter = array[0];
            wrongAnswersCounter = array[1];
            permitedTriesNumber = array[2];
        } else {
            getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID,null, loader);
        }
        getLoaderManager().restartLoader(GET_SPECIAL_TEXT_LOADER_ID, null, loader);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        changeAreas(question_area_size,answer_area_size);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.SMALL_SIZE);
        } else if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.NORMAL_SIZE);
        } else if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.LARGE_SIZE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.pause:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ACTIVITY_NAME + PAUSING + current_field_id, true);
                editor.commit();
                Toast toast = Toast.makeText(FullyRandomTestActivity.this, R.string.pause_do_not_shutdown, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                pauseThread.start();
                finish();
                return true;
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                bundle.putFloat(QUESTION_LAYOUT,((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT,((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (showQuestion.getTextSize()/scaledDensity) );
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void toPause() {
        if ( ! databaseOperations.checkEntriesForAnyTestOrderFullyRandomTest(current_field_id,question_data_type) ) {
            mySQLiteCursorLoader.putEntriesTestOrderFullyRandomTest(current_field_id,
                    question_data_type,listForTest);
        }
        mySQLiteCursorLoader.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
        int[] array = new int[3];
        array[0] = currentQuestionCounter;
        array[1] = wrongAnswersCounter;
        array[2] = permitedTriesNumber;
        mySQLiteCursorLoader.putTestPausedDataFullyRandomTest(current_field_id,array);
    }
    /*    private String [][] getListForTest ( double percent ) {
            int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
            String[][] listForTest = new String[numberOfQuestions][2];
            for (int i = 0; i < listForTest.length; i++) {
                Arrays.fill(listForTest[i], "");
            }
            int counter = 0;
            while (counter < numberOfQuestions) {
                String question = null;
                String answer = null;
                boolean newPair = false;
                int random = (int) (Math.random() * fieldEntriesCursor.getCount());
                while (! newPair) {
                    for (int i = 0; i < listForTest.length; i++) {
                        fieldEntriesCursor.moveToPosition(random);
                        question = fieldEntriesCursor.getString(1);
                        answer = fieldEntriesCursor.getString(2);
                        if ( listForTest[i][0].equals(question) && listForTest[i][1].equals(answer) ) {
                            newPair = false;
                            random = (int) (Math.random() * fieldEntriesCursor.getCount());
                            break;
                        }
                        newPair = true;
                    }
                }
                listForTest[counter][0] = question;
                listForTest[counter][1] = answer;
                counter++;
            }
            return listForTest;
        }*/
    private String [][] getListForTest ( double percent ) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][2];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            listOfEntries [fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries [fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
        }
        shuffleArray(listOfEntries);
        for ( int n = 0; n < listForTest.length; n++ ) {
            listForTest [n][0] = listOfEntries [n][0];
            listForTest [n][1] = listOfEntries [n][1];
        }
        return listForTest;
    }
    private String [][] getListForTest ( int numberOfQuestions ) {
        String[][] listForTest = new String[numberOfQuestions][2];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            listOfEntries [fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries [fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
        }
        shuffleArray(listOfEntries);
        for ( int n = 0; n < listForTest.length; n++ ) {
            listForTest [n][0] = listOfEntries [n][0];
            listForTest [n][1] = listOfEntries [n][1];
        }
        return listForTest;
    }
    private void shuffleArray( String [][] array )
    {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = array[index][0];
            String answer = array [index][1];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[i][0] = question;
            array[i][1] = answer;
        }
    }
    private void ifMoreThanOnce() {
        if ( searchForCursor != null ) {
            searchForCursor.close();
        }
        searchForCursor = databaseOperations.searchExactlyFor(current_field_id,
                question_data_type, listForTest[currentQuestionCounter][0]);
        if ( searchForCursor.getCount() > 1 ) {
            hints = new ArrayList<String>(3);
            hints.add( getString(R.string.first_character) + listForTest[currentQuestionCounter][1].charAt(0));
            hints.add( getString(R.string.number_of_characters) + Integer.toString(listForTest[currentQuestionCounter][1].length()));
            hints.add(getString(R.string.last_character) + listForTest[currentQuestionCounter][1]
                    .charAt(listForTest[currentQuestionCounter][1].length() - 1));
            nextHint.setEnabled(true);
//            hintTableRow.setBackgroundResource(R.color.enabled_color);
        }
        else {
            hints = null;
            nextHint.setEnabled(false);
//            hintTableRow.setBackgroundResource(R.color.default_background_color);
        }
        searchForCursor.close();
    }
    @SuppressWarnings("deprecation")
    private boolean checkAnAnswer() {
        Runnable mRunnable;
        boolean result = false;
        if ( check_type == Contract.Field.NON_STRICT_IGNORE_CASE ) {
            if ( CompareUtility.nonStrictCompareIgnoreCase(listForTest [currentQuestionCounter][1],enterAnswer.getText().toString()) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            }
            else {
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if ( triesCounter == permitedTriesNumber && !result ) {
                wrongAnswersCounter++;
                mySQLiteCursorLoader.putEntryWrongAnswersFullyRandomTest(listForTest [currentQuestionCounter][0],
                        listForTest[currentQuestionCounter][1],current_field_id,wrongAnswers,question_data_type);
                wrongAnswers = null;
            }
        } else if ( check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE ) {
            if ( CompareUtility.nonStrictCompare(listForTest [currentQuestionCounter][1],enterAnswer.getText().toString()) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            }
            else {
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if ( triesCounter == permitedTriesNumber && !result ) {
                wrongAnswersCounter++;
                mySQLiteCursorLoader.putEntryWrongAnswersFullyRandomTest(listForTest [currentQuestionCounter][0],
                        listForTest[currentQuestionCounter][1],current_field_id,wrongAnswers,question_data_type);
                wrongAnswers = null;
            }
        } else if ( check_type == Contract.Field.IGNORE_CASE ) {
            if ( enterAnswer.getText().toString().equalsIgnoreCase(listForTest [currentQuestionCounter][1]) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            }
            else {
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if ( triesCounter == permitedTriesNumber && !result ) {
                wrongAnswersCounter++;
                mySQLiteCursorLoader.putEntryWrongAnswersFullyRandomTest(listForTest [currentQuestionCounter][0],
                        listForTest[currentQuestionCounter][1],current_field_id,wrongAnswers,question_data_type);
                wrongAnswers = null;
            }
        } else if ( check_type == Contract.Field.DO_NOT_IGNORE_CASE ) {
            if ( enterAnswer.getText().toString().equals(listForTest [currentQuestionCounter][1]) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest [currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            }
            else {
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0,0,0,0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if ( triesCounter == permitedTriesNumber && !result ) {
                wrongAnswersCounter++;
                mySQLiteCursorLoader.putEntryWrongAnswersFullyRandomTest(listForTest [currentQuestionCounter][0],
                        listForTest[currentQuestionCounter][1],current_field_id,wrongAnswers,question_data_type);
                wrongAnswers = null;
            }
        }
        return result;
    }
    private String convertWrongAnswersArrayListToString( ArrayList<String> arrayList ) {
        String s = "";
        for ( int i = 0; i < arrayList.size(); i++ ) {
            s += arrayList.get(i);
            if ( i < arrayList.size() - 1) {
                s += "\t";
            }
        }
        return s;
    }
    private String [][] readTestDataFromLinkerTable() {
        String [][] listForTest = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            int order = fieldEntriesCursor.getInt(2);
            listForTest [order][0] = fieldEntriesCursor.getString(0);
            listForTest [order][1] = fieldEntriesCursor.getString(1);
        }
        return listForTest;
    }

    @Override
    protected void onPause() {
        super.onPause();
        retainedFragment.setListForTest_TestActivity(listForTest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( tts_available && isFinishing() ) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
    }

    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER,currentQuestionCounter);
        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS,wrongAnswersCounter);
        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER,permitedTriesNumber);
//        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInsatanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInsatanceState.putBoolean(RESULT, result);
//        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInsatanceState.putBoolean(NEXT_BUTTON_STATE, check.isEnabled());
        savedInsatanceState.putBoolean(TESTING_NOW, testingNow);
        savedInsatanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
//        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInsatanceState.putInt(HINT_COUNTER, hintCounter);
        savedInsatanceState.putStringArrayList(HINTS, hints);
        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
        savedInsatanceState.putInt(CONTENT_VISIBILITY,content.getVisibility());
        savedInsatanceState.putInt(LOADING_VISIBILITY,mLoadingView.getVisibility());
//        savedInsatanceState.putSerializable(WRONG_ITEMS,wrongItems);
        savedInsatanceState.putBoolean(BOOLEAN_CONTINUE,boolean_continue);
        savedInsatanceState.putStringArray(SYMBOL, symbol);
        savedInsatanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        saveUserLayoutChanges(savedInsatanceState);
       /* if ( ! isFinishing() ) {
            pauseThread.start();
        }*/
        super.onSaveInstanceState(savedInsatanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName( SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
//        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
//        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean nextState = savedInstanceState.getBoolean(NEXT_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
//        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
//        wrongItems = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEMS);
        boolean_continue = savedInstanceState.getBoolean(BOOLEAN_CONTINUE);
        customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
        symbol = savedInstanceState.getStringArray(SYMBOL);
        restoreUserLayoutChanges(savedInstanceState);
        if ( testingNow ) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            showTries.setText(Integer.toString(triesCounter + 1));
            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
//            start.setEnabled(false);
            check.setEnabled(nextState);


            enterAnswer.setEnabled(true);
            ifMoreThanOnce();
        }
        if ( hintCounter > 0 ) {
//            hintIfMoreThanOneAnswer.setText(hints.get(hintCounter - 1));
            if ( enterAnswer.getText().toString().length() <= 0 ) {
                enterAnswer.setHint(hints.get(hintCounter - 1));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(getApplicationContext(), this);
                tts_available = true;
            } else {
                mySQLiteCursorLoader.updateFieldReadAnswer(current_field_id,false);
                Toast toast = Toast.makeText(this, R.string.tts_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,400);
                toast.show();
            }
        }
    }

    @Override
    public void changeAreas(float questionArea, float answerArea) {
        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) showResult.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        questionLinearlayout.setLayoutParams(question_params);
        showResult.setLayoutParams(answer_params);
    }

    @Override
    public void changeTextSize(float textSize) {
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }
    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, showQuestion.getTextSize());
    }
    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT), bundle.getFloat(ANSWER_LAYOUT));
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }



    @Override
    public void onInit(int i) {
        if ( tts_available ) {
            String locale_short_name = databaseOperations.getFieldLocale(current_field_id);
            StringTokenizer stringTokenizer = new StringTokenizer(locale_short_name, ",");
            String lang = stringTokenizer.nextToken();
            String country = stringTokenizer.nextToken();
            Locale locale = new Locale(lang, country);
            if ( tts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE ) {
                tts.setLanguage(locale);
                locale_available = true;
            } else {
                mySQLiteCursorLoader.updateFieldReadAnswer(current_field_id,false);
                Toast toast = Toast.makeText(this, R.string.locale_is_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0,400);
                toast.show();
            }
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                tts.speak("Hello",TextToSpeech.QUEUE_ADD,null,null);
            } else {
                tts.speak("Hello",TextToSpeech.QUEUE_ADD,null);
            }
        }
    }

    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        content.setAlpha(0f);
        content.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        content.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }
}
