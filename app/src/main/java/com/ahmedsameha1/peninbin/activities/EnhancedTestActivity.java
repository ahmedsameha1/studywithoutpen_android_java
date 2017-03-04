package com.ahmedsameha1.peninbin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.TimeOfAnswerDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 8/3/2015.
 */
public class EnhancedTestActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener,
        TimeOfAnswerDialogFragment.Contact,
        ActivityUtility.ActivityTimers {
    public static final String SCREEN_NAME = "EnhancedTestActivity";
    public static final String ACTIVITY_NAME = "EnhancedTestActivity";
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
    public static final String LIST_FOR_TEST = "listForTest";
    public static final String CURRENT_QUESTION_COUNTER = "currentQuestionCounter";
    public static final String TRIES_COUNTER = "triesCounter";
    public static final String NEXT_BUTTON_STATE = "newtButtonState";
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
    public static final String PAUSING = "pausing";
    private static final String CURRENT_ENTRY_ID = "current_entry_id";
    public static final String CONTROLLED_PORTION = "controlled_portion";
    public static final String RANK_LEVEL = "importance_level";
    public static final String WRONGNESS_LEVEL = "wrongness_level";
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    private static final String BEFORE_SHOWING_RESULT_DIALOG = "before_showing_result_dialog";
    public static final String TIME_BETWEEN_ANSWERS_PASSED = "time_between_answers_passed";
    private static final String HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER = "how_much_time_remaining_from_time_for_answer";
    private static final String TIME_FOR_ANSWER_SAVED = "time_for_answer_saved";
    private static final String TIME_FOR_ANSWER_DO_WHAT = "time_for_answer_do_what";
    private static final long MOVE_NEXT_NOTIFICATION_TIME = 300;
    private long current_field_id, current_entry_id;
    private String[][] listForTest;
    private ArrayList<String> hints, wrongAnswers;
    private boolean boolean_continue = false, actionBarShown, before_showing_result_dialog, time_of_answer_saved;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private boolean testingNow = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber, wrongAnswersCounter, percent_or_number, number;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private double percentage;
    private Cursor fieldEntriesCursor;
    private Toolbar toolbar;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuestions, check, nextHint, showActionbar, dont_know;
    private LinearLayout show_result_linearlayout, show_question_linearlayout, container;
    private Handler mHandler = new Handler();
    private Tracker mTracker;
    private Chronometer tool_bar_timer;
    private long lastPause;
    private boolean isEmulated;
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
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EnhancedTestActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(ACTIVITY_NAME + PAUSING + current_field_id);
                        editor.commit();
                        Toast toast = Toast.makeText(EnhancedTestActivity.this, R.string.pause_shutdown, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                };
                mHandler.post(pauseRunnable);
            }
        }
    });
    private int question_data_type;
    private int check_type, controlled_portion, rank_level, wrongness_level;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private boolean time_between_answers_passed = false;
    private DatabaseReadOperations databaseOperations;
    private RetainedFragment retainedFragment; // to save the list for test array when the activity orientation change
    private int mShortAnimationDuration;
    private View mLoadingView;
    private RelativeLayout content;
    private LoaderManager.LoaderCallbacks<Cursor> loader;
    private String[] symbol, customable_text;
    private int current_field_type;
    private Runnable show_result_dialog_runnable, show_time_for_answer_passed_background_runnable;
    private int time_of_answer, time_of_answer_do_what;
    private long time_of_starting_time_of_answer = -1, effictive_time_of_answer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        boolean isReadAnswer;
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, LIST_FOR_TEST).commit();
        }
        databaseOperations = new DatabaseReadOperations(this);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        listForTest = retainedFragment.getListForTest_EnhancedTestActivity();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        int [] time_of_answer_data = databaseOperations.getTimeOfAnswer(current_field_id, Constants.TimeOfAnswer.TEST);
        time_of_answer = time_of_answer_data[0];
        time_of_answer_do_what = time_of_answer_data[1];
        time_of_answer_saved = time_of_answer > 0;
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        permitedTriesNumber = getIntent().getIntExtra(PERMITED_TRIES_NUMBER, 1);
        percent_or_number = getIntent().getIntExtra(PERCENTAGE_OR_NUMBER, PERCENTAGE);
        controlled_portion = getIntent().getIntExtra(CONTROLLED_PORTION, -1);
        rank_level = getIntent().getIntExtra(RANK_LEVEL, -1);
        wrongness_level = getIntent().getIntExtra(WRONGNESS_LEVEL, -1);
        number = getIntent().getIntExtra(QUESTION_NUMBER, 0);
        percentage = getIntent().getDoubleExtra(QUESTION_PERCENTAGE, 0);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        check = (TextView) findViewById(R.id.check);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);
        showTries = (TextView) findViewById(R.id.trys);
        nextHint = (TextView) findViewById(R.id.next_hint);
        dont_know = (TextView) findViewById(R.id.dont_know);
        showNumberOfQuestions = (TextView) findViewById(R.id.number_of_questions);
        show_result_linearlayout = (LinearLayout) findViewById(R.id.result);
        show_question_linearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        final CardView show_actionbar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView check_card_view = (CardView) findViewById(R.id.check_card_view);
        final CardView next_hint_card_view = (CardView) findViewById(R.id.next_hint_card_view);
        tool_bar_timer = (Chronometer) findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        if (savedInstanceState == null) {
            content.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            lastPause = databaseOperations.getElapsedPausedSessionTime(current_field_id, Constants.ElapsedPausedSessionTime.ENHANCED_TEST);
        } else {
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        }
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        setSupportActionBar(toolbar);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        if (action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        if (isReadAnswer) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            String date_string;

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(EnhancedTestActivity.this,
                            "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a , se." + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER + " , se." + Contract.COLUMN_ID
                                    + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_ENHANCED_TEST_ORDER + " >= ? ",
                            new String[]{current_field_id + "", "0"});
                } else if (id == GET_ENTRIES_LOADER_ID) {
                    if (isEmulated) {
                        date_string = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US)
                                .format(new Date(System.currentTimeMillis()));
                        return new CursorLoaderAndUpdateDatabase(EnhancedTestActivity.this,
                                "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.String_Entry.COLUMN_RANK + " , se." + Contract.String_Entry.COLUMN_ASKED_COUNT
                                        + " , se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                        + " , se." + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                                        + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND ( ? > " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                                        + " OR " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NULL )",
                                new String[]{current_field_id + "", date_string});
                    } else {
                        return new CursorLoaderAndUpdateDatabase(EnhancedTestActivity.this,
                                "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.String_Entry.COLUMN_RANK + " , se." + Contract.String_Entry.COLUMN_ASKED_COUNT
                                        + " , se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                        + " , se." + Contract.String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND
                                        + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{current_field_id + ""});
                    }
                } else if (id == GET_SPECIAL_TEXT_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(EnhancedTestActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
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
                if (id == GET_ENTRIES_LOADER_ID || id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    fieldEntriesCursor = data;
                    if (boolean_continue) {
                        if (listForTest == null) {
                            listForTest = readTestDataFromLinkerTable();
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            showNextQuestion();
                            enterAnswer.setEnabled(true);
                            enterAnswer.requestFocus();
                            testingNow = true;
                        }
                    } else {
                        if (listForTest == null) {
                            if (percent_or_number == PERCENTAGE) {
                                listForTest = getListForTest(percentage, date_string);
                            } else if (percent_or_number == NUMBER) {
                                listForTest = getListForTest(number, date_string);
                            }
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            showNextQuestion();
                            enterAnswer.requestFocus();
                            testingNow = true;
                            boolean_continue = false;
                        }
                    }
                    crossfade();
                } else if (id == GET_SPECIAL_TEXT_LOADER_ID) {
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
                if (id == GET_ENTRIES_LOADER_ID || id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    fieldEntriesCursor = null;
                }
            }

        };
        getLoaderManager().restartLoader(GET_SPECIAL_TEXT_LOADER_ID, null, loader);
        check.setOnClickListener(view -> {
            check_card_view.startAnimation(animAlpha);
            boolean result = checkAnAnswer();
            if (triesCounter < permitedTriesNumber && !result) {
                showQuestion.setText(listForTest[currentQuestionCounter][0]);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                showTries.setText(Integer.toString(triesCounter + 1));
            } else {
                triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                currentQuestionCounter++;
                if (currentQuestionCounter >= questionsNumber) {
                    check.setEnabled(false);
                    before_showing_result_dialog = true;
                    show_result_dialog_runnable = () -> {
                            showQuestion.setText(R.string.test_has_completed);
                            enterAnswer.setText(R.string.test_has_completed);
                            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            show_result_linearlayout.setPadding(0, 0, 0, 0);
                            show_result_dialog();
                    };
                    mHandler.postDelayed(show_result_dialog_runnable, RETURN_TIME);
                    currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                    testingNow = false;
                    enterAnswer.setEnabled(false);
                    showNumberOfQuestions.setText("");
                    remainingQuestionCounter.setText("");
                    showTries.setText("");
                    enterAnswer.setHint(R.string.enter_your_answer);
                    hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                    cursorLoaderAndUpdateDatabase.resetEntriesTestOrderEnhancedTest(current_field_id, question_data_type);
                    cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                    mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
                    time_between_answers_passed = false;
                    tool_bar_timer.stop();
                    cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                            Constants.ElapsedPausedSessionTime.ENHANCED_TEST, Constants.ElapsedPausedSessionTime.NOT_USED);
                } else {
                    showNextQuestion();
                }
            }
            enterAnswer.setHint(R.string.enter_your_answer);
            if (triesCounter == INITIAL_VALUE_OF_TRIES_COUNTER) {
                show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                show_question_linearlayout.setPadding(0, 0, 0, 0);
                showQuestion.setTextColor(Color.WHITE);
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
                    dont_know.setEnabled(false);
                    show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    show_result_linearlayout.setPadding(0, 0, 0, 0);
                } else {
                    dont_know.setEnabled(true);
                }
                if (s.length() <= 0) {
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
                        Toast toast = Toast.makeText(EnhancedTestActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
            }
        });
        dont_know.setOnClickListener( view -> dontKnow() );
        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_actionbar_card_view.startAnimation(animAlpha);
                if (actionBarShown) {
                    getSupportActionBar().hide();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.show);
                    cursorLoaderAndUpdateDatabase.updateFieldActionBarShown(current_field_id, Contract.Field.DO_NOT_SHOW_ACTION_BAR);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.hide);
                    cursorLoaderAndUpdateDatabase.updateFieldActionBarShown(current_field_id, Contract.Field.SHOW_ACTION_BAR);
                }
            }
        });
        showNumberOfQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(EnhancedTestActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuestions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        remainingQuestionCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(EnhancedTestActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        showTries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(EnhancedTestActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        if (boolean_continue) {
            getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID, null, loader);
            int[] array = databaseOperations.getDataFromTestPausedEnhancedTest(current_field_id);
            currentQuestionCounter = array[0];
            wrongAnswersCounter = array[1];
            permitedTriesNumber = array[2];
        } else {
            getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, loader);
        }
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        changeAreas(question_area_size, answer_area_size);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        if (test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.SMALL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.NORMAL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.LARGE_SIZE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( !ActivityUtility.hasOpenedDialogs(this) ) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
        }
        mTracker.setScreenName(SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
                Toast toast = Toast.makeText(EnhancedTestActivity.this, R.string.pause_do_not_shutdown, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
                cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                        Constants.ElapsedPausedSessionTime.ENHANCED_TEST, lastPause);
                pauseThread.start();
                finish();
                ExitActivity.exit(EnhancedTestActivity.this);
                return true;
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) show_question_linearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) show_result_linearlayout.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (showQuestion.getTextSize() / scaledDensity));
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;

            case R.id.time_of_answer:
                Bundle bundle2 = new Bundle();
                bundle2.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                bundle2.putInt(TimeOfAnswerDialogFragment.TYPE, Constants.TimeOfAnswer.TEST);
                bundle2.putInt(TimeOfAnswerDialogFragment.CURRENT_TIME_OF_ANSWER, time_of_answer);
                bundle2.putInt(TimeOfAnswerDialogFragment.CURRENT_TIME_OF_ANSWER_DO_WHAT, time_of_answer_do_what);
                bundle2.putBoolean(TimeOfAnswerDialogFragment.TIME_OF_ANSWER_SAVED, time_of_answer_saved);
                TimeOfAnswerDialogFragment timeOfAnswerDialogFragment = new TimeOfAnswerDialogFragment();
                timeOfAnswerDialogFragment.setArguments(bundle2);
                timeOfAnswerDialogFragment.show(getSupportFragmentManager(), TimeOfAnswerDialogFragment.TIME_OF_ANSWER_DIALOG_FRAGMENT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void toPause() {
        lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
        cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                Constants.ElapsedPausedSessionTime.ENHANCED_TEST, lastPause);
        if (!databaseOperations.checkEntriesForAnyTestOrderEnhancedTest(current_field_id, question_data_type)) {
            cursorLoaderAndUpdateDatabase.putEntriesTestOrderEnhancedTest(current_field_id,
                    question_data_type, listForTest);
        }
        cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
        int[] array = new int[3];
        array[0] = currentQuestionCounter;
        array[1] = wrongAnswersCounter;
        array[2] = permitedTriesNumber;
        cursorLoaderAndUpdateDatabase.putTestPausedDataEnhancedTest(current_field_id, array);
        if ( time_of_answer_do_what == Constants.TimeOfAnswer.MOVE_NEXT ) {
            mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
        }
    }

    private String[][] getListForTest(double percent, final String date_string) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][2];
        long[] list_for_test_ids = new long[numberOfQuestions];
        String[][] list_of_entries_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        String[][] list_of_entries_did_not_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        long[] list_of_entries_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        long[] list_of_entries_did_not_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        if (wrongness_level == -1 && rank_level == -1 && controlled_portion == -1) {
            int asked = 0, didnot_asked = 0;
            for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
                 fieldEntriesCursor.moveToNext()) {
                if (fieldEntriesCursor.getInt(6) == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                    list_of_entries_asked_current_round[asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_asked_current_round[asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_asked_current_round_ids[asked] = fieldEntriesCursor.getLong(0);
                    asked++;
                } else {
                    list_of_entries_did_not_asked_current_round[didnot_asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_did_not_asked_current_round[didnot_asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_did_not_asked_current_round_ids[didnot_asked] = fieldEntriesCursor.getLong(0);
                    didnot_asked++;
                }
            }
            list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
            list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
            list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
            list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
            if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            } else {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                }
                ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                    listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                    listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                    list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            }
        } else {
            int number_of_controlled_questions;
            int number_of_uncontrolled_questions;
            double controlled_percentage;
            if (controlled_portion == 0) {
                controlled_percentage = 0.2;
            } else if (controlled_portion == 1) {
                controlled_percentage = 0.4;
            } else if (controlled_portion == 2) {
                controlled_percentage = 0.5;
            } else if (controlled_portion == 3) {
                controlled_percentage = 0.6;
            } else if (controlled_portion == 4) {
                controlled_percentage = 0.8;
            } else {
                controlled_percentage = 1;
            }
            number_of_controlled_questions = (int) (numberOfQuestions * controlled_percentage);
            number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
            ArrayList<Object> arrayList = ActivityUtility.prepareControlledAndUncontrolled(fieldEntriesCursor,rank_level, wrongness_level);
            String[][] controlled_entries = (String[][]) arrayList.get(0);
            String[][] uncontrolled_entries = (String[][]) arrayList.get(1);
            long[] uncontrolled_entries_ids = (long[]) arrayList.get(2);
            int[] uncontrolled_entries_whether_asked = (int[]) arrayList.get(3);
            if (controlled_entries == null) {
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < listForTest.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                        list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                }
            } else if (uncontrolled_entries == null) {
                ActivityUtility.shuffleArray(controlled_entries);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];
                }
                return listForTest;
            } else {
                if (number_of_controlled_questions > controlled_entries.length) {
                    number_of_controlled_questions = controlled_entries.length;
                    number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
                } else if (number_of_uncontrolled_questions > uncontrolled_entries.length) {
                    number_of_uncontrolled_questions = uncontrolled_entries.length;
                    number_of_controlled_questions = numberOfQuestions - number_of_uncontrolled_questions;
                }
                ActivityUtility.shuffleArray(controlled_entries);
                for (int n = 0; n < number_of_controlled_questions; n++) {
                    listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];
                }
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (number_of_uncontrolled_questions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + number_of_uncontrolled_questions; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_did_not_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                }
                ActivityUtility.shuffleArray(listForTest);
                return listForTest;
            }
        }
    }

    private String[][] getListForTest(int numberOfQuestions, final String date_string) {
        String[][] listForTest = new String[numberOfQuestions][2];
        long[] list_for_test_ids = new long[numberOfQuestions];
        String[][] list_of_entries_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        String[][] list_of_entries_did_not_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        long[] list_of_entries_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        long[] list_of_entries_did_not_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        if (wrongness_level == -1 && rank_level == -1 && controlled_portion == -1) {
            int asked = 0, didnot_asked = 0;
            for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
                 fieldEntriesCursor.moveToNext()) {
                if (fieldEntriesCursor.getInt(6) == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                    list_of_entries_asked_current_round[asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_asked_current_round[asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_asked_current_round_ids[asked] = fieldEntriesCursor.getLong(0);
                    asked++;
                } else {
                    list_of_entries_did_not_asked_current_round[didnot_asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_did_not_asked_current_round[didnot_asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_did_not_asked_current_round_ids[didnot_asked] = fieldEntriesCursor.getLong(0);
                    didnot_asked++;
                }
            }
            list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
            list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
            list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
            list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
            if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            } else {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                }
                ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                    listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                    listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                    list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            }
        } else {
            int number_of_controlled_questions;
            int number_of_uncontrolled_questions;
            double controlled_percentage;
            if (controlled_portion == 0) {
                controlled_percentage = 0.2;
            } else if (controlled_portion == 1) {
                controlled_percentage = 0.4;
            } else if (controlled_portion == 2) {
                controlled_percentage = 0.5;
            } else if (controlled_portion == 3) {
                controlled_percentage = 0.6;
            } else if (controlled_portion == 4) {
                controlled_percentage = 0.8;
            } else {
                controlled_percentage = 1;
            }
            number_of_controlled_questions = (int) (numberOfQuestions * controlled_percentage);
            number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
            ArrayList<Object> arrayList = ActivityUtility.prepareControlledAndUncontrolled(fieldEntriesCursor,rank_level, wrongness_level);
            String[][] controlled_entries = (String[][]) arrayList.get(0);
            String[][] uncontrolled_entries = (String[][]) arrayList.get(1);
            long[] uncontrolled_entries_ids = (long[]) arrayList.get(2);
            int[] uncontrolled_entries_whether_asked = (int[]) arrayList.get(3);
            if (controlled_entries == null) {
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < listForTest.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                        list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                }
            } else if (uncontrolled_entries == null) {
                ActivityUtility.shuffleArray(controlled_entries);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];
                }
                return listForTest;
            } else {
                if (number_of_controlled_questions > controlled_entries.length) {
                    number_of_controlled_questions = controlled_entries.length;
                    number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
                } else if (number_of_uncontrolled_questions > uncontrolled_entries.length) {
                    number_of_uncontrolled_questions = uncontrolled_entries.length;
                    number_of_controlled_questions = numberOfQuestions - number_of_uncontrolled_questions;
                }
                ActivityUtility.shuffleArray(controlled_entries);
                for (int n = 0; n < number_of_controlled_questions; n++) {
                    listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];
                }
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (number_of_uncontrolled_questions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + number_of_uncontrolled_questions; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_did_not_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                }
                ActivityUtility.shuffleArray(listForTest);
                return listForTest;
            }
        }
    }





    private void ifMoreThanOnce() {
        hints = ActivityUtility.getHints(this, current_field_id, question_data_type,
                listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
        if (hints != null) {
            nextHint.setEnabled(true);
        } else {
            nextHint.setEnabled(false);
        }
    }

    private void setFalse( ActivityUtility.FalseString falseString) {
        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(current_entry_id);
        show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
        show_result_linearlayout.setPadding(0, 0, 0, 0);
        cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(current_entry_id);
        setNormalEnterAnswerBackground();
        if ( falseString == ActivityUtility.FalseString.TIME_OUT ) {
            wrongAnswers.add(getString(R.string.time_out));
        } else if ( falseString == ActivityUtility.FalseString.DONT_KNOW ){
            wrongAnswers.add(getString(R.string.dont_know));
        }
        wrongAnswersCounter++;
        cursorLoaderAndUpdateDatabase.putEntryWrongAnswersEnhancedTest(current_entry_id, wrongAnswers, question_data_type);
        wrongAnswers = null;
    }

    @SuppressWarnings("deprecation")
    private boolean checkAnAnswer() {
        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(current_entry_id);
        boolean result = false;
        if (check_type == Contract.Field.NON_STRICT_IGNORE_CASE) {
            if (CompareUtility.nonStrictCompareIgnoreCase(listForTest[currentQuestionCounter][1], enterAnswer.getText().toString())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersEnhancedTest(current_entry_id, wrongAnswers, question_data_type);
                wrongAnswers = null;
            }
        } else if (check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE) {
            if (CompareUtility.nonStrictCompare(listForTest[currentQuestionCounter][1], enterAnswer.getText().toString())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersEnhancedTest(current_entry_id, wrongAnswers, question_data_type);
                wrongAnswers = null;
            }
        } else if (check_type == Contract.Field.IGNORE_CASE) {
            if (enterAnswer.getText().toString().equalsIgnoreCase(listForTest[currentQuestionCounter][1])) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersEnhancedTest(current_entry_id, wrongAnswers, question_data_type);
                wrongAnswers = null;
            }
        } else if (check_type == Contract.Field.DO_NOT_IGNORE_CASE) {
            if (enterAnswer.getText().toString().equals(listForTest[currentQuestionCounter][1])) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1], TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersEnhancedTest(current_entry_id, wrongAnswers, question_data_type);
                wrongAnswers = null;
            }
        }
        return result;
    }

    private String[][] readTestDataFromLinkerTable() {
        String[][] listForTest = new String[fieldEntriesCursor.getCount()][2];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            int order = fieldEntriesCursor.getInt(2);
            listForTest[order][0] = fieldEntriesCursor.getString(0);
            listForTest[order][1] = fieldEntriesCursor.getString(1);
        }
        return listForTest;
    }

    private long getEntry_id(String queston, String answer) {
        long entry_id = 0;
        boolean which_cursor = boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            if (which_cursor) { // continue previous session
                if (fieldEntriesCursor.getString(0).equals(queston) && fieldEntriesCursor.getString(1).equals(answer)) {
                    entry_id = fieldEntriesCursor.getLong(3);
                    break;
                }
            } else {
                if (fieldEntriesCursor.getString(1).equals(queston) && fieldEntriesCursor.getString(2).equals(answer)) {
                    entry_id = fieldEntriesCursor.getLong(0);
                    break;
                }
            }
        }
        return entry_id;
    }

    @Override
    protected void onPause() {
        if ( !ActivityUtility.hasOpenedDialogs(this)) {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
        }
        retainedFragment.setListForTest_EnhancedTestActivity(listForTest);
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tts_available && isFinishing()) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ( databaseOperations.isThisFieldTestPausedEnhancedTest(current_field_id)) {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.ENHANCED_TEST, lastPause);
        } else {
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.ENHANCED_TEST, Constants.ElapsedPausedSessionTime.NOT_USED);
        }
        mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
        time_between_answers_passed = false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInsatanceState) {
        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInsatanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInsatanceState.putBoolean(NEXT_BUTTON_STATE, check.isEnabled());
        savedInsatanceState.putBoolean(TESTING_NOW, testingNow);
        savedInsatanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        savedInsatanceState.putInt(HINT_COUNTER, hintCounter);
        savedInsatanceState.putStringArrayList(HINTS, hints);
        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
        savedInsatanceState.putBoolean(BOOLEAN_CONTINUE, boolean_continue);
        savedInsatanceState.putLong(CURRENT_ENTRY_ID, current_entry_id);
        savedInsatanceState.putInt(CONTENT_VISIBILITY, content.getVisibility());
        savedInsatanceState.putInt(LOADING_VISIBILITY, mLoadingView.getVisibility());
        savedInsatanceState.putLong(TIMER_LAST_PAUSE, lastPause);
        savedInsatanceState.putStringArray(SYMBOL, symbol);
        savedInsatanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        savedInsatanceState.putBoolean(BEFORE_SHOWING_RESULT_DIALOG, before_showing_result_dialog);
        savedInsatanceState.putBoolean(TIME_BETWEEN_ANSWERS_PASSED, time_between_answers_passed);
        savedInsatanceState.putInt(TIME_FOR_ANSWER_DO_WHAT, time_of_answer_do_what);
        savedInsatanceState.putBoolean(TIME_FOR_ANSWER_SAVED, time_of_answer_saved);
        saveUserLayoutChanges(savedInsatanceState);
        mHandler.removeCallbacks(show_result_dialog_runnable);
        mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
        if (time_of_starting_time_of_answer > -1) {
            savedInsatanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER,
                    effictive_time_of_answer - (System.currentTimeMillis() - time_of_starting_time_of_answer));
        }
        super.onSaveInstanceState(savedInsatanceState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean nextState = savedInstanceState.getBoolean(NEXT_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
        current_entry_id = savedInstanceState.getLong(CURRENT_ENTRY_ID);
        boolean_continue = savedInstanceState.getBoolean(BOOLEAN_CONTINUE);
        customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
        time_of_answer_do_what = savedInstanceState.getInt(TIME_FOR_ANSWER_DO_WHAT);
        time_of_answer_saved = savedInstanceState.getBoolean(TIME_FOR_ANSWER_SAVED);
        symbol = savedInstanceState.getStringArray(SYMBOL);
        before_showing_result_dialog = savedInstanceState.getBoolean(BEFORE_SHOWING_RESULT_DIALOG);
        time_between_answers_passed = savedInstanceState.getBoolean(TIME_BETWEEN_ANSWERS_PASSED);
        restoreUserLayoutChanges(savedInstanceState);
        if (!before_showing_result_dialog) {
            if (testingNow) {
                showQuestion.setText(listForTest[currentQuestionCounter][0]);
                showTries.setText(Integer.toString(triesCounter + 1));
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                check.setEnabled(nextState);
                enterAnswer.setEnabled(true);
                ifMoreThanOnce();
                if ( time_between_answers_passed && time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY ) {
                    show_question_linearlayout.setBackgroundResource(R.drawable.time_for_answer_passed_background);
                    show_question_linearlayout.setPadding(0, 0, 0, 0);
                    showQuestion.setTextColor(Color.BLACK);
                }
                if (savedInstanceState.containsKey(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER)) {
                    long how_much_time_remaining_time_for_answer = savedInstanceState.getLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER);
                    if (how_much_time_remaining_time_for_answer <= 0) {
                      doWhat();
                    } else {
                        effictive_time_of_answer = how_much_time_remaining_time_for_answer;
                        show_time_for_answer_passed_background_runnable = () -> {
                                doWhat();
                        };
                        mHandler.postDelayed(show_time_for_answer_passed_background_runnable, effictive_time_of_answer);
                        time_of_starting_time_of_answer = System.currentTimeMillis();
                    }
                }
                if (hintCounter > 0 && enterAnswer.getText().toString().length() <= 0) {
                    enterAnswer.setHint(hints.get(hintCounter - 1));
                }
            }

        } else {
            show_result_dialog();
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
                cursorLoaderAndUpdateDatabase.updateFieldReadAnswer(current_field_id, false);
                Toast toast = Toast.makeText(this, R.string.tts_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        }
    }

    @Override
    public void changeAreas(float questionArea, float answerArea) {
        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) show_question_linearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) show_result_linearlayout.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        show_question_linearlayout.setLayoutParams(question_params);
        show_result_linearlayout.setLayoutParams(answer_params);
    }

    @Override
    public void changeTextSize(float textSize) {
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) show_question_linearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) show_result_linearlayout.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, showQuestion.getTextSize());
    }

    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT), bundle.getFloat(ANSWER_LAYOUT));
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }

    @Override
    public void onInit(int i) {
        if (tts_available) {
            String locale_short_name = databaseOperations.getFieldLocale(current_field_id);
            StringTokenizer stringTokenizer = new StringTokenizer(locale_short_name, ",");
            String lang = stringTokenizer.nextToken();
            String country = stringTokenizer.nextToken();
            Locale locale = new Locale(lang, country);
            if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                tts.setLanguage(locale);
                locale_available = true;
            } else {
                cursorLoaderAndUpdateDatabase.updateFieldReadAnswer(current_field_id, false);
                Toast toast = Toast.makeText(this, R.string.locale_is_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak("Hello", TextToSpeech.QUEUE_ADD, null, null);
            } else {
                tts.speak("Hello", TextToSpeech.QUEUE_ADD, null);
            }
        }
    }



    private void show_result_dialog() {
        final ShowTestResultDialogFragment fragment = new ShowTestResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        bundle.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        bundle.putInt(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), ShowTestResultDialogFragment.SHOW_TEST_RESULT_DIALOG_FRAGMENT);
    }

    private void showNextQuestion() {
        showQuestion.setText(listForTest[currentQuestionCounter][0]);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        showQuestion.scrollTo(0, 0);
        showTries.setText(Integer.toString(triesCounter + 1));
        ifMoreThanOnce();
        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
        enterAnswer.setHint(R.string.enter_your_answer);
        hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
        wrongAnswers = new ArrayList<>(permitedTriesNumber);
        mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
        time_between_answers_passed = false;
        doWhat();
    }

    // TODO: 2/21/17 make proguard options on gradle before publish true
    private void setNormalEnterAnswerBackground() {
        Runnable mRunnable = () -> {
            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
            show_result_linearlayout.setPadding(0, 0, 0, 0);
        };
        mHandler.postDelayed(mRunnable, RETURN_TIME);
    }

    @Override
    public void setTimeOfAnswer(int time_of_answer, int time_of_answer_do_what, boolean time_for_answer_saved) {
        this.time_of_answer = time_of_answer;
        this.time_of_answer_do_what = time_of_answer_do_what;
        this.time_of_answer_saved = time_for_answer_saved;
        doWhat();
    }


    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {
        if ( isCounting ) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
            doWhat();
        } else {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
            mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
            time_between_answers_passed = false;
            if ( time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY ) {
                show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                show_question_linearlayout.setPadding(0, 0, 0, 0);
                showQuestion.setTextColor(Color.WHITE);
            }
        }
    }

    private void doWhat() {
        if (time_of_answer > 0) {
            effictive_time_of_answer = time_of_answer * 1000;
            show_time_for_answer_passed_background_runnable = () -> {
                if ( time_of_answer_do_what == Constants.TimeOfAnswer.MOVE_NEXT ) {
                    setFalse(ActivityUtility.FalseString.TIME_OUT);
                    triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                    currentQuestionCounter++;
                    if (currentQuestionCounter >= questionsNumber) {
                        check.setEnabled(false);
                        before_showing_result_dialog = true;
                        show_result_dialog_runnable = () -> {
                            showQuestion.setText(R.string.test_has_completed);
                            enterAnswer.setText(R.string.test_has_completed);
                            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            show_result_linearlayout.setPadding(0, 0, 0, 0);
                            show_result_dialog();
                        };
                        mHandler.postDelayed(show_result_dialog_runnable, RETURN_TIME);
                        currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                        testingNow = false;
                        enterAnswer.setEnabled(false);
                        showNumberOfQuestions.setText("");
                        remainingQuestionCounter.setText("");
                        showTries.setText("");
                        enterAnswer.setHint(R.string.enter_your_answer);
                        hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                        cursorLoaderAndUpdateDatabase.resetEntriesTestOrderEnhancedTest(current_field_id, question_data_type);
                        cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                        mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
                        time_between_answers_passed = false;
                        tool_bar_timer.stop();
                    } else {
                        showNextQuestion();
                        show_question_linearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                        show_question_linearlayout.setPadding(0, 0, 0, 0);
                        Runnable mRunnable = () -> {
                            show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                            show_question_linearlayout.setPadding(0, 0, 0, 0);
                        };
                        mHandler.postDelayed(mRunnable, MOVE_NEXT_NOTIFICATION_TIME);
                    }
                } else if ( time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY ) {
                    show_question_linearlayout.setBackgroundResource(R.drawable.time_for_answer_passed_background);
                    show_question_linearlayout.setPadding(0, 0, 0, 0);
                    showQuestion.setTextColor(Color.BLACK);
                    time_between_answers_passed = true;
                    time_of_starting_time_of_answer = -1;
                }
            };
            mHandler.postDelayed(show_time_for_answer_passed_background_runnable, effictive_time_of_answer);
            time_of_starting_time_of_answer = System.currentTimeMillis();
        }
    }

    private void dontKnow() {
                    setFalse(ActivityUtility.FalseString.DONT_KNOW);
                    triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                    currentQuestionCounter++;
        showQuestion.setTextColor(Color.WHITE);
                    if (currentQuestionCounter >= questionsNumber) {
                        check.setEnabled(false);
                        before_showing_result_dialog = true;
                        show_result_dialog_runnable = () -> {
                            showQuestion.setText(R.string.test_has_completed);
                            enterAnswer.setText(R.string.test_has_completed);
                            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            show_result_linearlayout.setPadding(0, 0, 0, 0);
                            show_result_dialog();
                        };
                        mHandler.postDelayed(show_result_dialog_runnable, RETURN_TIME);
                        currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                        testingNow = false;
                        enterAnswer.setEnabled(false);
                        showNumberOfQuestions.setText("");
                        remainingQuestionCounter.setText("");
                        showTries.setText("");
                        enterAnswer.setHint(R.string.enter_your_answer);
                        hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                        cursorLoaderAndUpdateDatabase.resetEntriesTestOrderEnhancedTest(current_field_id, question_data_type);
                        cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedEnhancedTest(current_field_id);
                        mHandler.removeCallbacks(show_time_for_answer_passed_background_runnable);
                        time_between_answers_passed = false;
                        tool_bar_timer.stop();
                    } else {
                        showNextQuestion();
                        show_question_linearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                        show_question_linearlayout.setPadding(0, 0, 0, 0);
                        Runnable mRunnable = () -> {
                            show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                            show_question_linearlayout.setPadding(0, 0, 0, 0);
                        };
                        mHandler.postDelayed(mRunnable, MOVE_NEXT_NOTIFICATION_TIME);
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
