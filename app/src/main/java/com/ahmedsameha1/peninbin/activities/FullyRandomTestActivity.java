package com.ahmedsameha1.peninbin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.SystemClock;
import android.provider.AlarmClock;
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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.dialogs.ObfuscateQuestionDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TimeOfAnswerOfTestsDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
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
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class FullyRandomTestActivity extends FullyRandomTest implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener,
        TimeOfAnswerOfTestsDialogFragment.Contact,
        ActivityUtility.ActivityTimers,
        ObfuscateQuestionDialogFragment.ObfuscateText {

    public static final String SCREEN_NAME = "FullyRandomTestActivity";
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
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    public static final String PAUSING = "pausing";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    private static final String BEFORE_SHOWING_RESULT_DIALOG = "before_showing_result_dialog";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    public static final String TIME_BETWEEN_ANSWERS_PASSED = "time_between_answers_passed";
    private static final String HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER = "how_much_time_remaining_from_time_for_answer";
    private static final String TIME_FOR_ANSWER_SAVED = "time_for_answer_saved";
    private static final String TIME_FOR_ANSWER_DO_WHAT = "time_for_answer_do_what";
    private static final long MOVE_NEXT_NOTIFICATION_TIME = 300;
    private ArrayList<String> hints, wrongAnswers;
    private boolean boolean_continue = false, actionBarShown, before_showing_result_dialog, time_of_answer_saved;
    private boolean is_testing_now = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber, wrongAnswersCounter, percent_or_number, number;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private double percentage;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuestions, check, nextHint, showActionbar, dont_know;
    private LinearLayout show_result_linearlayout, show_question_linearlayout, container;
    private Handler mHandler = new Handler();
    private Tracker mTracker;
    private boolean isEmulated, isStartingFirstTime, is_starting_after_destruction;
    private Chronometer tool_bar_timer;
    private long lastPause;
    private Thread pauseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Runnable pauseRunnable;
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FullyRandomTestActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(FullyRandomTestActivity.class.getName() + PAUSING + current_field_id, true);
                editor.commit();
                if (!databaseOperations.checkEntriesForAnyTestOrderFullyRandomTest(current_field_id, question_data_type)) {
                    cursorLoaderAndUpdateDatabase.putEntriesTestOrderFullyRandomTest(current_field_id,
                            question_data_type, listForTest);
                }
            } finally {
                pauseRunnable = () -> {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FullyRandomTestActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(FullyRandomTestActivity.class.getName() + PAUSING + current_field_id);
                    editor.commit();
                };
                mHandler.post(pauseRunnable);
            }
        }
    });
    private int question_data_type;
    private int check_type;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean is_time_of_answer_elapsed = false;
    private boolean locale_available = false;
    private RetainedFragment retainedFragment; // to save the list for test array when the activity orientation change
    private RelativeLayout content;
    private int mShortAnimationDuration;
    private View mLoadingView;
    private String[] symbol, customable_text;
    private int current_field_type;
    private Runnable show_result_dialog_runnable;
    private int time_of_answer, time_of_answer_do_what, reading_question_letter_second, typing_answer_letter_second, find_answer_time;
    private long time_of_starting_time_of_answer = -1, effective_time_of_answer;
    private Runnable what_to_do_after_time_of_answer_elapsed_runnable = this::DoWhatYouHaveToDoAfterTimeOfAnswerElapsed;
    private boolean obfuscate;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        obfuscate = databaseOperations.isObfuscateQuestion(current_field_id);
        int [] time_of_answer_data = databaseOperations.getTimeOfAnswerOfTests(current_field_id);
        reading_question_letter_second = time_of_answer_data[0];
        find_answer_time = time_of_answer_data[1];
        typing_answer_letter_second = time_of_answer_data[2];
        time_of_answer_do_what = time_of_answer_data[3];
        time_of_answer_saved = reading_question_letter_second > 0 && find_answer_time > 0 && typing_answer_letter_second > 0;
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
        final CardView dont_know_card_view = (CardView) findViewById(R.id.dont_know_card_view);
        tool_bar_timer = (Chronometer) findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
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
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(FullyRandomTestActivity.this,
                            "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a , se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " , se." + Contract.COLUMN_ID
                                    + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_FULLY_RANDOM_TEST_ORDER + " >= ? ",
                            new String[]{current_field_id + "", "0"});
                } else if (id == GET_ENTRIES_LOADER_ID) {
                    if (isEmulated) {
                        String date_string = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US)
                                .format(new Date(System.currentTimeMillis()));
                        return new CursorLoaderAndUpdateDatabase(FullyRandomTestActivity.this,
                                "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a " + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND ( ? > " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE
                                        + " OR " + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " IS NULL )",
                                new String[]{current_field_id + "", date_string});
                    } else {
                        return new CursorLoaderAndUpdateDatabase(FullyRandomTestActivity.this,
                                "SELECT " + "se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a " + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{current_field_id + ""});
                    }
                } else if (id == GET_SPECIAL_TEXT_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(FullyRandomTestActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
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
                            listForTest = readTestDataFromLinkerTable();
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            showQuestion();
                            enterAnswer.setEnabled(true);
                            enterAnswer.requestFocus();
                            is_testing_now = true;
                            boolean_continue = false;
                    } else {
                            if (percent_or_number == PERCENTAGE) {
                                listForTest = getListForTest(percentage);
                            } else if (percent_or_number == NUMBER) {
                                listForTest = getListForTest(number);
                            }
                            questionsNumber = listForTest.length;
                            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                            showQuestion();
                            enterAnswer.requestFocus();
                            is_testing_now = true;
                            pauseThread.start();
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
        check.setOnClickListener(view -> {
                check_card_view.startAnimation(animAlpha);

                    boolean result = checkAnAnswer();
                    if (triesCounter < permitedTriesNumber && !result) {
                        if ( obfuscate ) {
                            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        }
                        showTries.setText(Integer.toString(triesCounter + 1));
                    } else {
                        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
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
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            String time = simpleDateFormat.format(new Date(SystemClock.elapsedRealtime() - tool_bar_timer.getBase()));
                            Toast toast = Toast.makeText(FullyRandomTestActivity.this, time, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                            is_testing_now = false;
                            enterAnswer.setEnabled(false);
                            showNumberOfQuestions.setText("");
                            remainingQuestionCounter.setText("");
                            showTries.setText("");
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            cursorLoaderAndUpdateDatabase.resetEntriesTestOrderFullyRandomTest(current_field_id, question_data_type);
                            cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                            mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
                            time_of_starting_time_of_answer = -1;
                            is_time_of_answer_elapsed = false;
                            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
                            tool_bar_timer.stop();
                        } else {
                            showQuestion();
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
                            show_result_linearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            show_result_linearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                show_result_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                                show_result_linearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
                }
            }
        });
        nextHint.setOnClickListener( view -> {
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
        });
        dont_know.setOnClickListener( view -> {
            dont_know_card_view.startAnimation(animAlpha);
            dontKnow();} );
        showActionbar.setOnClickListener( view ->  {
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
        });
        showNumberOfQuestions.setOnClickListener( view ->  {
                view.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuestions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();

        });
        remainingQuestionCounter.setOnClickListener( view ->  {
                view.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
        });
        showTries.setOnClickListener( view ->  {
                view.startAnimation(animAlpha);
                Toast toast = Toast.makeText(FullyRandomTestActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
        });
        getLoaderManager().restartLoader(GET_SPECIAL_TEXT_LOADER_ID, null, loader);
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
        if (savedInstanceState == null) {
            boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
            content.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            isStartingFirstTime = true;
            if (boolean_continue) {
                getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID, null, loader);
                int[] array = databaseOperations.getDataFromTestPausedFullyRandomTest(current_field_id);
                currentQuestionCounter = array[0];
                wrongAnswersCounter = array[1];
                permitedTriesNumber = array[2];
                lastPause = array[3];
            } else {
                getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, loader);
            }
        } else {
            boolean_continue = savedInstanceState.getBoolean(BOOLEAN_CONTINUE);
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
            currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
            wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
            permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
            triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
            is_testing_now = savedInstanceState.getBoolean(TESTING_NOW);
            questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
            hints = savedInstanceState.getStringArrayList(HINTS);
            hintCounter = savedInstanceState.getInt(HINT_COUNTER);
            wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
            customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
            symbol = savedInstanceState.getStringArray(SYMBOL);
            before_showing_result_dialog = savedInstanceState.getBoolean(BEFORE_SHOWING_RESULT_DIALOG);
            time_of_answer_do_what = savedInstanceState.getInt(TIME_FOR_ANSWER_DO_WHAT);
            time_of_answer_saved = savedInstanceState.getBoolean(TIME_FOR_ANSWER_SAVED);
            is_time_of_answer_elapsed = savedInstanceState.getBoolean(TIME_BETWEEN_ANSWERS_PASSED);
            boolean nextState = savedInstanceState.getBoolean(NEXT_BUTTON_STATE);
            listForTest = retainedFragment.getListForTest_FullyRandomTestActivity();
            if ( listForTest == null ) {
                if (boolean_continue) {
                    getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID, null, loader);
                } else {
                    getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, loader);
                }
            } else {
                is_starting_after_destruction = true;
                restoreUserLayoutChanges(savedInstanceState);
                if (!before_showing_result_dialog) {
                    if (is_testing_now) {
                        if ( obfuscate ) {
                            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        }
                        showTries.setText(Integer.toString(triesCounter + 1));
                        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                        showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                        check.setEnabled(nextState);
                        enterAnswer.setEnabled(true);
                        ifMoreThanOnce();
                        if ( is_time_of_answer_elapsed && time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY ) {
                            show_question_linearlayout.setBackgroundResource(R.drawable.time_for_answer_passed_background);
                            show_question_linearlayout.setPadding(0, 0, 0, 0);
                            showQuestion.setTextColor(Color.BLACK);
                        }
                        if (savedInstanceState.containsKey(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER)) {
                            long how_much_time_remaining_time_for_answer = savedInstanceState.getLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER);
                            if (how_much_time_remaining_time_for_answer <= 0) {
                                DoWhatYouHaveToDoAfterTimeOfAnswerElapsed();
                            } else {
                                effective_time_of_answer = how_much_time_remaining_time_for_answer;
                                mHandler.postDelayed(what_to_do_after_time_of_answer_elapsed_runnable, effective_time_of_answer);
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( !ActivityUtility.hasOpenedDialogs(this) ) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();

            // to set the timer on the tool_bar_timer after recreation of activity due configuration change to the last value
        } else if ( !is_testing_now ) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
        }
        /* do not call scheduleWhatToDoAfterTimeOfAnswerElapsed() if first time because the it will be called from the loader onfinished()
         * do not call it after destruction because it will be called from onrestoreinstancestate method
         * do not call it if the test has already finished
         */
        if ( !isStartingFirstTime && !is_starting_after_destruction && is_testing_now) {
            scheduleWhatToDoAfterTimeOfAnswerElapsed();
        }
        // at this point this flag has performed its role so I return it to its default value
        if ( isStartingFirstTime ) {
            isStartingFirstTime = false;
        }
        // at this point this flag has performed its role so I return it to its default value
        if ( is_starting_after_destruction ) {
            is_starting_after_destruction = false;
        }
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) +SCREEN_NAME);
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
                bundle2.putInt(TimeOfAnswerOfTestsDialogFragment.READING_QUESTION_LETTER_SECOND, reading_question_letter_second);
                bundle2.putInt(TimeOfAnswerOfTestsDialogFragment.FIND_ANSWER_TIME, find_answer_time);
                bundle2.putInt(TimeOfAnswerOfTestsDialogFragment.TYPING_ANSWER_LETTER_SECOND, typing_answer_letter_second);
                bundle2.putInt(TimeOfAnswerOfTestsDialogFragment.CURRENT_TIME_OF_ANSWER_DO_WHAT, time_of_answer_do_what);
                bundle2.putBoolean(TimeOfAnswerOfTestsDialogFragment.TIME_OF_ANSWER_SAVED, time_of_answer_saved);
                TimeOfAnswerOfTestsDialogFragment timeOfAnswerOfTestsDialogFragment = new TimeOfAnswerOfTestsDialogFragment();
                timeOfAnswerOfTestsDialogFragment.setArguments(bundle2);
                timeOfAnswerOfTestsDialogFragment.show(getSupportFragmentManager(), TimeOfAnswerOfTestsDialogFragment.TIME_OF_ANSWER_DIALOG_FRAGMENT);
                return true;

            case R.id.obfuscate_question:
                Bundle bundle3 = new Bundle();
                bundle3.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                ObfuscateQuestionDialogFragment obfuscateQuestionDialogFragment = new ObfuscateQuestionDialogFragment();
                obfuscateQuestionDialogFragment.setArguments(bundle3);
                obfuscateQuestionDialogFragment.show(getSupportFragmentManager(), ObfuscateQuestionDialogFragment.OBFUSCATE_QUESTION_DIALOG_FRAGMENT);
                return true;

            case R.id.timer:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                            .putExtra(AlarmClock.EXTRA_SKIP_UI, false);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(this, R.string.no_timer_app, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(this, R.string.kitkat_or_newer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }


    /*private String[][] getListForTest(double percent) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][3];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][3];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            listOfEntries[fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries[fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
            listOfEntries[fieldEntriesCursor.getPosition()][2] = String.valueOf(fieldEntriesCursor.getLong(0));
        }
        ActivityUtility.shuffleArray(listOfEntries);
        for (int n = 0; n < listForTest.length; n++) {
            listForTest[n][0] = listOfEntries[n][0];
            listForTest[n][1] = listOfEntries[n][1];
            listForTest[n][2] = listOfEntries[n][2];
        }
        return listForTest;
    }

    private String[][] getListForTest(int numberOfQuestions) {
        String[][] listForTest = new String[numberOfQuestions][3];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][3];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            listOfEntries[fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries[fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
            listOfEntries[fieldEntriesCursor.getPosition()][2] = String.valueOf(fieldEntriesCursor.getLong(0));
        }
        ActivityUtility.shuffleArray(listOfEntries);
        for (int n = 0; n < listForTest.length; n++) {
            listForTest[n][0] = listOfEntries[n][0];
            listForTest[n][1] = listOfEntries[n][1];
            listForTest[n][2] = listOfEntries[n][2];
        }
        return listForTest;
    }
*/

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
        show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
        show_result_linearlayout.setPadding(0, 0, 0, 0);
        cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
        setNormalEnterAnswerBackground();
        if ( falseString == ActivityUtility.FalseString.TIME_OUT ) {
            wrongAnswers.add(getString(R.string.time_out));
        } else if ( falseString == ActivityUtility.FalseString.DONT_KNOW ){
            wrongAnswers.add(getString(R.string.dont_know));
        }
        wrongAnswersCounter++;
        cursorLoaderAndUpdateDatabase.putEntryWrongAnswersFullyRandomTest(Long.parseLong(listForTest[currentQuestionCounter][2]), wrongAnswers, question_data_type);
        wrongAnswers = null;
    }

    @SuppressWarnings("deprecation")
    private boolean checkAnAnswer() {
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
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersFullyRandomTest(Long.parseLong(listForTest[currentQuestionCounter][2]), wrongAnswers, question_data_type);
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
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersFullyRandomTest(Long.parseLong(listForTest[currentQuestionCounter][2]), wrongAnswers, question_data_type);
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
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersFullyRandomTest(Long.parseLong(listForTest[currentQuestionCounter][2]), wrongAnswers, question_data_type);
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
                setNormalEnterAnswerBackground();
                result = false;
                wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                wrongAnswersCounter++;
                cursorLoaderAndUpdateDatabase.putEntryWrongAnswersFullyRandomTest(Long.parseLong(listForTest[currentQuestionCounter][2]), wrongAnswers, question_data_type);
                wrongAnswers = null;
            }
        }
        return result;
    }

    private String[][] readTestDataFromLinkerTable() {
        String[][] listForTest = new String[fieldEntriesCursor.getCount()][3];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            int order = fieldEntriesCursor.getInt(2);
            listForTest[order][0] = fieldEntriesCursor.getString(0);
            listForTest[order][1] = fieldEntriesCursor.getString(1);
            listForTest[order][2] = String.valueOf(fieldEntriesCursor.getLong(3));
        }
        return listForTest;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( !ActivityUtility.hasOpenedDialogs(this)) {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
        }
        /* Canceling the timers of time of answer when the user go away from the activity
         or before the activity finishes
         or before the activity destroyed due a change of configuration*/
        mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
        retainedFragment.setListForTest_FullyRandomTestActivity(listForTest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tts_available && isFinishing()) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
        if ( is_testing_now ) {
            cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
            int[] array = new int[3];
            array[0] = currentQuestionCounter;
            array[1] = wrongAnswersCounter;
            array[2] = permitedTriesNumber;
            cursorLoaderAndUpdateDatabase.putTestPausedDataFullyRandomTest(current_field_id, array, lastPause);
        }
    }

/*    @Override
    public void onBackPressed() {
        super.onBackPressed();
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.FULLY_RANDOM_TEST, lastPause);
        ExitActivity.exit(this);
    }*/

    @Override
    public void onSaveInstanceState(Bundle savedInsatanceState) {
        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInsatanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInsatanceState.putBoolean(NEXT_BUTTON_STATE, check.isEnabled());
        savedInsatanceState.putBoolean(TESTING_NOW, is_testing_now);
        savedInsatanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        savedInsatanceState.putInt(HINT_COUNTER, hintCounter);
        savedInsatanceState.putStringArrayList(HINTS, hints);
        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
        savedInsatanceState.putInt(CONTENT_VISIBILITY, content.getVisibility());
        savedInsatanceState.putInt(LOADING_VISIBILITY, mLoadingView.getVisibility());
        savedInsatanceState.putBoolean(BOOLEAN_CONTINUE, boolean_continue);
        savedInsatanceState.putStringArray(SYMBOL, symbol);
        savedInsatanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        savedInsatanceState.putBoolean(BEFORE_SHOWING_RESULT_DIALOG, before_showing_result_dialog);
        savedInsatanceState.putLong(TIMER_LAST_PAUSE, lastPause);
        savedInsatanceState.putBoolean(TIME_BETWEEN_ANSWERS_PASSED, is_time_of_answer_elapsed);
        savedInsatanceState.putInt(TIME_FOR_ANSWER_DO_WHAT, time_of_answer_do_what);
        savedInsatanceState.putBoolean(TIME_FOR_ANSWER_SAVED, time_of_answer_saved);
        saveUserLayoutChanges(savedInsatanceState);
        mHandler.removeCallbacks(show_result_dialog_runnable);
               /* Saving the remaining time till completion of the current timer of time of answer because the activity
        will be destroyed, not finishing normally.
        * We don't need to save this remaining time when the activity finishes normally
        * or when the user go away from the activity */
        if (time_of_starting_time_of_answer > -1) {
            savedInsatanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER,
                    effective_time_of_answer - (System.currentTimeMillis() - time_of_starting_time_of_answer));
        }
              /* To run scheduleWhatToDoAfterTimeOfAnswerElapsed() method after restarting the activity
        in case the activity destroyed while a dialog is shown */
        else if ( ActivityUtility.hasOpenedDialogs(this) && time_of_answer > 0 ) {
            savedInsatanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER, time_of_answer * 1000 );
        }
        // call this method to prevent the dialogs from being shown after the recreation of the activity
        // this to prevent the call of manageTimersDueDialogsShow() when the dialog is shown while it really will not appear
        if ( is_testing_now ) { // don't close dialogs (exactly the result dialog) if the test already has finished
            ActivityUtility.closeOpenedDialogs(this);
        }
        super.onSaveInstanceState(savedInsatanceState);
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
        bundle.putInt(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
        bundle.putInt(PERMITED_TRIES_NUMBER,permitedTriesNumber);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), ShowTestResultDialogFragment.SHOW_TEST_RESULT_DIALOG_FRAGMENT);
    }
    private void showQuestion() {
        if ( obfuscate ) {
            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
        } else {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
        }
        time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1],
                reading_question_letter_second,find_answer_time, typing_answer_letter_second);
        showQuestion.scrollTo(0, 0);
        showTries.setText(Integer.toString(triesCounter + 1));
        ifMoreThanOnce();
        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
        enterAnswer.setText("");
        enterAnswer.setHint(R.string.enter_your_answer);
        hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
        wrongAnswers = new ArrayList<>(permitedTriesNumber);
        mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
        time_of_starting_time_of_answer = -1;
        is_time_of_answer_elapsed = false;
        scheduleWhatToDoAfterTimeOfAnswerElapsed();
    }

    private void setNormalEnterAnswerBackground() {
        Runnable mRunnable = () -> {
            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
            show_result_linearlayout.setPadding(0, 0, 0, 0);
        };
        mHandler.postDelayed(mRunnable, RETURN_TIME);
    }


    @Override
    public void setTimeOfAnswer(int reading_question_letter_second, int find_answer_time, int typing_answer_letter_second, int time_of_answer_do_what, boolean time_for_answer_saved) {
        this.reading_question_letter_second = reading_question_letter_second;
        this.find_answer_time = find_answer_time;
        this.typing_answer_letter_second = typing_answer_letter_second;
        this.time_of_answer_do_what = time_of_answer_do_what;
        this.time_of_answer_saved = time_for_answer_saved;
        // to avoid the case if the listfortest has not been prepared yet
        if ( is_testing_now ) {
            time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1],
                    reading_question_letter_second,find_answer_time, typing_answer_letter_second);
            scheduleWhatToDoAfterTimeOfAnswerElapsed();
        }
    }


    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {
        if ( isCounting ) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
            // to avoid the case if the listfortest has not been prepared yet
            if ( is_testing_now ) {
                scheduleWhatToDoAfterTimeOfAnswerElapsed();
            }
        } else {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
            mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
            time_of_starting_time_of_answer = -1;
            is_time_of_answer_elapsed = false;
            if ( time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY ) {
                show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                show_question_linearlayout.setPadding(0, 0, 0, 0);
                showQuestion.setTextColor(Color.WHITE);
            }
        }
    }
    void DoWhatYouHaveToDoAfterTimeOfAnswerElapsed() {
        if ( time_of_answer_do_what == Constants.TimeOfAnswer.MOVE_NEXT ) {
            setFalse(ActivityUtility.FalseString.TIME_OUT);
            triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
            cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String time = simpleDateFormat.format(new Date(SystemClock.elapsedRealtime() - tool_bar_timer.getBase()));
                Toast toast = Toast.makeText(FullyRandomTestActivity.this, time, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                is_testing_now = false;
                enterAnswer.setEnabled(false);
                showNumberOfQuestions.setText("");
                remainingQuestionCounter.setText("");
                showTries.setText("");
                enterAnswer.setHint(R.string.enter_your_answer);
                hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                cursorLoaderAndUpdateDatabase.resetEntriesTestOrderFullyRandomTest(current_field_id, question_data_type);
                cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
                mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
                time_of_starting_time_of_answer = -1;
                is_time_of_answer_elapsed = false;
                lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
                tool_bar_timer.stop();
            } else {
                showQuestion();
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
            is_time_of_answer_elapsed = true;
            time_of_starting_time_of_answer = -1;
        }
    }
    private void scheduleWhatToDoAfterTimeOfAnswerElapsed() {
        if (time_of_answer > 0) {
            effective_time_of_answer = time_of_answer * 1000;
            mHandler.postDelayed(what_to_do_after_time_of_answer_elapsed_runnable, effective_time_of_answer);
            time_of_starting_time_of_answer = System.currentTimeMillis();
        }
    }

    private void dontKnow() {
        setFalse(ActivityUtility.FalseString.DONT_KNOW);
        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = simpleDateFormat.format(new Date(SystemClock.elapsedRealtime() - tool_bar_timer.getBase()));
            Toast toast = Toast.makeText(FullyRandomTestActivity.this, time, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
            currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
            is_testing_now = false;
            enterAnswer.setEnabled(false);
            showNumberOfQuestions.setText("");
            remainingQuestionCounter.setText("");
            showTries.setText("");
            enterAnswer.setHint(R.string.enter_your_answer);
            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
            cursorLoaderAndUpdateDatabase.resetEntriesTestOrderFullyRandomTest(current_field_id, question_data_type);
            cursorLoaderAndUpdateDatabase.clearThisFieldFromTestPausedFullyRandomTest(current_field_id);
            mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
            time_of_starting_time_of_answer = -1;
            is_time_of_answer_elapsed = false;
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
        } else {
            showQuestion();
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
    @Override
    public void obfuscateText(boolean obfuscate) {
        this.obfuscate = obfuscate;
        if ( obfuscate ) {
            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
        } else {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
        }
    }
}
