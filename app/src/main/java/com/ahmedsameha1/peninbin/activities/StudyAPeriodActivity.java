package com.ahmedsameha1.peninbin.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.provider.AlarmClock;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.dialogs.ObfuscateQuestionDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TimeOfAnswerOfStudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TimerDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by ahmed_000 on 9/23/2015.
 */
public class StudyAPeriodActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener,
        TimeOfAnswerOfStudyAPeriodDialogFragment.Contact,
        ActivityUtility.ActivityTimers,
        ObfuscateQuestionDialogFragment.ObfuscateText {
    public static final String SCREEN_NAME = "StudyAPeriodActivity";
    public static final String ACTIVITY_NAME = "StudyAPeriodActivity";
    public static final String STARTING_TIME = "startingTime";
    public static final String REPEATED_ENTRIES = "repeatedEntries";
    public static final String STRING_REPRESENTING_REPEATED_ENTRIES = "strigRepresentingRepeatedEntries";
    public static final String LAST_SESSION_OR_NEW_ONE = "lastSessionOrNewOne";
    public static final String DASH = "-";
    public static final String NUMBER_OF_TRIES = "numberOftries";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String PAUSING = "pausing";
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    public static final String RESULT = "result";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int CONTINUE_PREVIOUS_SESSION_LOADER_ID = 1;
    public static final int GET_SPECIAL_TEXT_LOADER_ID = 2;
    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int RETURN_TIME = 1000;
    public static final String LIST_FOR_TEST = "listForTest";
    public static final String CURRENT_QUESTION_COUNTER = "currentQuestionCounter";
    public static final String TRIES_COUNTER = "triesCounter";
    public static final String CHECK_BUTTON_STATE = "checkButtonState";
    public static final String NUMBER_OF_QUESTIONS = "numberOfQuestions";
    public static final String HINT_COUNTER = "hintCounter";
    public static final String HINTS = "hints";
    public static final String _CONTINUE_BUTTON_STATE = "_continueButtonState";
    public static final String TIME_BETWEEN_ANSWERS_PASSED = "time_between_answers_passed";
    private static final String HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER = "how_much_time_remaining_from_time_for_answer";
    private static final String TIME_FOR_ANSWER_SAVED = "time_for_answer_saved";
    public static final String PERMITED_TRIES_NUMBER = "permitedTriesNumber";
    private static final String WHILE_STUDYING = "while_studying";
    private static StudyAPeriodActivity studyAPeriodActivity;
    private long startingTime;
    boolean boolean_continue, actionBarShown, is_time_of_answer_elapsed = false;
    private Cursor cursor;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private ArrayList<Integer> repeatedEntries = new ArrayList<Integer>();
    private String[][] listForTest;
    private ArrayList<String> hints;
    private boolean result, time_of_answer_saved;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuetions, showActionbar, dont_know;
    private TextView nextHint, check, _continue;
    private LinearLayout show_result_linearlayout, show_question_linearlayout, container;
    private CardView check_card_view, next_hint_card_view, show_action_bar_card_view, continue_card_view;
    private DatabaseReadOperations databaseReadOperations;
    private RetainedFragment retainedFragment;
    private Handler mHandler = new Handler();
    private long current_field_id;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private int check_type;
    private Tracker mTracker;
    private View mLoadingView;
    private RelativeLayout content;
    private Chronometer tool_bar_timer;
    private long lastPause;
    private String[] symbol, customable_text;
    private int current_field_type;
    private boolean isEmulated, isStartingFirstTime, is_starting_after_destruction;
    private int time_of_answer, reading_question_letter_second, typing_answer_letter_second, find_answer_time;
    private long time_of_starting_time_of_answer = -1, effective_time_of_answer;
    private Runnable what_to_do_after_time_of_answer_elapsed_runnable = this::DoWhatYouHaveToDoAfterTimeOfAnswerElapsed;
    private int question_data_type;
    private boolean obfuscate;
    private boolean while_studying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_a_period);
        studyAPeriodActivity = this;
        boolean isReadAnswer;
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        startingTime = getIntent().getLongExtra(STARTING_TIME, 0);
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        databaseReadOperations = new DatabaseReadOperations(this);
        current_field_type = databaseReadOperations.getFieldType(current_field_id);
        question_data_type = databaseReadOperations.getQuestionDataType(current_field_id);
        isEmulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        obfuscate = databaseReadOperations.isObfuscateQuestion(current_field_id);
        int[] time_of_answer_data = databaseReadOperations.getTimeOfAnswerOfStudyTillCorrect(current_field_id);
        reading_question_letter_second = time_of_answer_data[0];
        find_answer_time = time_of_answer_data[1];
        typing_answer_letter_second = time_of_answer_data[2];
        time_of_answer_saved = reading_question_letter_second > 0 && find_answer_time > 0 && typing_answer_letter_second > 0;
        action_bar_shown = databaseReadOperations.getFieldActionBarShown(current_field_id);
        isReadAnswer = databaseReadOperations.isReadAnswer(current_field_id);
        check_type = databaseReadOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseReadOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseReadOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseReadOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        show_result_linearlayout = (LinearLayout) findViewById(R.id.result);
        show_question_linearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        container = (LinearLayout) findViewById(R.id.container);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        check = (TextView) findViewById(R.id.check);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);
        _continue = (TextView) findViewById(R.id._continue);
        nextHint = (TextView) findViewById(R.id.next_hint);
        dont_know = (TextView) findViewById(R.id.dont_know);
        showTries = (TextView) findViewById(R.id.trys);
        showNumberOfQuetions = (TextView) findViewById(R.id.number_of_questions);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        check_card_view = (CardView) findViewById(R.id.check_card_view);
        continue_card_view = (CardView) findViewById(R.id.continue_card_view);
        next_hint_card_view = (CardView) findViewById(R.id.next_hint_card_view);
        show_action_bar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView dont_know_card_view = (CardView) findViewById(R.id.dont_know_card_view);
        tool_bar_timer = (Chronometer) findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, LIST_FOR_TEST).commit();
        }
        LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
                if (id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(StudyAPeriodActivity.this,
                            "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                    + " a , se." + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " , se." + Contract.COLUMN_ID
                                    + " FROM " + Contract.STring.TABLE_NAME
                                    + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                    + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                    + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                    + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " >= ? ",
                            new String[]{current_field_id + "", "0"});
                } else if (id == GET_ENTRIES_LOADER_ID) {
                    if (isEmulated) {
                        String date_string = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US)
                                .format(new Date(System.currentTimeMillis()));
                        return new CursorLoaderAndUpdateDatabase(StudyAPeriodActivity.this,
                                "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " , se." + Contract.COLUMN_ID
                                        + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND ? > "
                                        + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE,
                                new String[]{current_field_id + "", date_string});
                    } else {
                        return new CursorLoaderAndUpdateDatabase(StudyAPeriodActivity.this,
                                "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.CREATION_DATE + " , se." + Contract.COLUMN_ID
                                        + " FROM " + Contract.STring.TABLE_NAME
                                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                                new String[]{current_field_id + ""});
                    }
                } else if (id == GET_SPECIAL_TEXT_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(StudyAPeriodActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
                            + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                            + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                            " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                            + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ?",
                            new String[]{current_field_type + ""});
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                int id = loader.getId();
                if (id == GET_ENTRIES_LOADER_ID || id == CONTINUE_PREVIOUS_SESSION_LOADER_ID) {
                    StudyAPeriodActivity.this.cursor = cursor;
                    PrepareListForTest prepareListForTest = new PrepareListForTest();
                    new Thread(() -> {
                        if (boolean_continue) {
                            listForTest = readFromLinkerTableStudyDuringColumn();
                        } else {
                            listForTest = prepareListForStudyFirstTime();
                        }
                        questionsNumber = listForTest.length;
                        prepareListForTest.sendMessage(Message.obtain());
                    }).start();
                } else if (id == GET_SPECIAL_TEXT_LOADER_ID) {
                    if (symbol == null && customable_text == null) {
                        symbol = new String[cursor.getCount()];
                        customable_text = new String[cursor.getCount()];
                        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                             cursor.moveToNext()) {
                            symbol[cursor.getPosition()] = cursor.getString(1);
                            customable_text[cursor.getPosition()] = cursor.getString(2);
                        }
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
        check.setOnClickListener(view -> {
            check_card_view.startAnimation(animAlpha);
            result = checkAnAnswer();
            if (triesCounter < permitedTriesNumber && !result) {
                if (obfuscate) {
                    showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                } else {
                    showQuestion.setText(listForTest[currentQuestionCounter][0]);
                }
                time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1],
                        reading_question_letter_second, find_answer_time, typing_answer_letter_second);
                showTries.setText(Integer.toString(triesCounter + 1));
            } else if (!result) {
                enterAnswer.setText(listForTest[currentQuestionCounter][1]);
                triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                showTries.setText(Integer.toString(triesCounter + 1));
                check.setEnabled(false);
                _continue.setEnabled(true);
                enterAnswer.setEnabled(false);
            } else {
                triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                showTries.setText(Integer.toString(triesCounter + 1));
                cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                currentQuestionCounter++;
                if (currentQuestionCounter >= questionsNumber) {
                    currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                    questionsNumber = 0;
                    enterAnswer.setHint(R.string.enter_your_answer);
                    hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                    if (repeatedEntries.size() > 0) {
                        listForTest = prepareListForStudySecondAndMore(repeatedEntries);
                        questionsNumber = listForTest.length;
                        repeatedEntries.clear();
                        if (obfuscate) {
                            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        }
                        time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1],
                                reading_question_letter_second, find_answer_time, typing_answer_letter_second);
                        showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                        ifMoreThanOnce();
                        mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
                        time_of_starting_time_of_answer = -1;
                        scheduleWhatToDoAfterTimeOfAnswerElapsed();
                    } else {
                        check.setEnabled(false);
                        dont_know.setEnabled(false);
                        while_studying = false;
                        cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_PERIOD, question_data_type);
                        cursorLoaderAndUpdateDatabase.clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_PERIOD, current_field_id);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String time = simpleDateFormat.format(new Date(SystemClock.elapsedRealtime() - tool_bar_timer.getBase()));
                        Toast toast = Toast.makeText(StudyAPeriodActivity.this, time, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        if (tts_available) {
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    while (true) {
                                        if (!tts.isSpeaking()) {
                                            finish();
                                            ExitActivity.exit(StudyAPeriodActivity.this);
                                            break;
                                        }
                                    }
                                    return null;
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                        } else {
                            finish();
                            ExitActivity.exit(StudyAPeriodActivity.this);
                        }
                    }
                } else {
                    if (obfuscate) {
                        showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                    } else {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                    }
                    time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1],
                            reading_question_letter_second, find_answer_time, typing_answer_letter_second);
                    showTries.setText(Integer.toString(triesCounter + 1));
                    ifMoreThanOnce();
                    remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                    hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                    mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
                    time_of_starting_time_of_answer = -1;
                    scheduleWhatToDoAfterTimeOfAnswerElapsed();
                }
                show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
                show_question_linearlayout.setPadding(0, 0, 0, 0);
                showQuestion.setTextColor(Color.WHITE);
                is_time_of_answer_elapsed = false;
            }
            enterAnswer.setHint(R.string.enter_your_answer);
        });
        nextHint.setOnClickListener(v -> {
            next_hint_card_view.startAnimation(animAlpha);
            if (hints != null) {
                if (enterAnswer.getText().toString().length() <= 0) {
                    enterAnswer.setHint(hints.get(hintCounter++));
                } else {
                    Toast toast = Toast.makeText(StudyAPeriodActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
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
                if (s.length() > 0 && permitedTriesNumber != 1 && time_of_answer <= 0) {
                    show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    show_result_linearlayout.setPadding(0, 0, 0, 0);
                }
                if (s.length() > 0) {
                    check.setEnabled(true);
                    dont_know.setEnabled(false);
                } else {
                    check.setEnabled(false);
                    dont_know.setEnabled(true);
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
        _continue.setOnClickListener(v -> {
            continue_card_view.startAnimation(animAlpha);
            enterAnswer.setText("");
            _continue.setEnabled(false);
            enterAnswer.setEnabled(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(enterAnswer.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            enterAnswer.requestFocus();
            enterAnswer.setHint(R.string.enter_your_answer);
            if (permitedTriesNumber == 1) {
                show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
            }
        });
        dont_know.setOnClickListener(view -> {
            dont_know_card_view.startAnimation(animAlpha);
            dontKnow();
        });
        showActionbar.setOnClickListener(v -> {
            show_action_bar_card_view.startAnimation(animAlpha);
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
        showNumberOfQuetions.setOnClickListener(v -> {
            v.startAnimation(animAlpha);
            Toast toast = Toast.makeText(StudyAPeriodActivity.this,
                    getString(R.string.number_of_questions, showNumberOfQuetions.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        remainingQuestionCounter.setOnClickListener(v -> {
            v.startAnimation(animAlpha);
            Toast toast = Toast.makeText(StudyAPeriodActivity.this,
                    getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        showTries.setOnClickListener(v -> {
            v.startAnimation(animAlpha);
            Toast toast = Toast.makeText(StudyAPeriodActivity.this,
                    getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        setSupportActionBar(toolbar);
        if (action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        if (isReadAnswer) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        showTries.setText(Integer.toString(triesCounter + 1));
        changeAreas(question_area_size, answer_area_size);
        if (test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.SMALL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.NORMAL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.LARGE_SIZE);
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        getLoaderManager().restartLoader(GET_SPECIAL_TEXT_LOADER_ID, null, loader);
        if (savedInstanceState == null) {
            content.setVisibility(View.GONE);
            isStartingFirstTime = true;
            if (boolean_continue) {
                int[] array = databaseReadOperations.getDataFromStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_PERIOD, current_field_id);
                currentQuestionCounter = array[0];
                permitedTriesNumber = array[1];
                lastPause = array[2];
                repeatedEntries = databaseReadOperations.getStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_PERIOD, current_field_id);
                getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID, null, loader);
            } else {
                permitedTriesNumber = getIntent().getExtras().getInt(NUMBER_OF_TRIES);
                getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, loader);
            }
        } else {
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
            currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
            permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
            triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
            boolean checkState = savedInstanceState.getBoolean(CHECK_BUTTON_STATE);
            boolean _continueState = savedInstanceState.getBoolean(_CONTINUE_BUTTON_STATE);
            questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
            hints = savedInstanceState.getStringArrayList(HINTS);
            hintCounter = savedInstanceState.getInt(HINT_COUNTER);
            result = savedInstanceState.getBoolean(RESULT);
            repeatedEntries = savedInstanceState.getIntegerArrayList(REPEATED_ENTRIES);
            customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
            symbol = savedInstanceState.getStringArray(SYMBOL);
            time_of_answer_saved = savedInstanceState.getBoolean(TIME_FOR_ANSWER_SAVED);
            is_time_of_answer_elapsed = savedInstanceState.getBoolean(TIME_BETWEEN_ANSWERS_PASSED);
            while_studying = savedInstanceState.getBoolean(WHILE_STUDYING);
            listForTest = retainedFragment.getListForTest_StudyDuringActivity();
            if (listForTest == null) {
                if (boolean_continue) {
                    getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION_LOADER_ID, null, loader);
                } else {
                    getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, loader);
                }
            } else {
                is_starting_after_destruction = true;
                restoreUserLayoutChanges(savedInstanceState);
                if (obfuscate) {
                    showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
                } else {
                    showQuestion.setText(listForTest[currentQuestionCounter][0]);
                }
                showTries.setText(Integer.toString(triesCounter + 1));
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                check.setEnabled(checkState);
                _continue.setEnabled(_continueState);
                enterAnswer.setEnabled(!_continueState);
                ifMoreThanOnce();
                if (is_time_of_answer_elapsed) {
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
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!ActivityUtility.hasOpenedDialogs(this)) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
        }
        /* do not call scheduleWhatToDoAfterTimeOfAnswerElapsed() if first time because the it will be called from the loader onfinished()
         * do not call it after destruction because it will be called from onrestoreinstancestate method
         * do not call it if the test has already finished
         */
        if (!isStartingFirstTime && !is_starting_after_destruction) {
            // to avoid the case if the listfortest has not been prepared yet
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    while (true) {
                        if (listForTest != null) {
                            scheduleWhatToDoAfterTimeOfAnswerElapsed();
                            break;
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute();
        }
        // at this point this flag has performed its role so I return it to its default value
        if (isStartingFirstTime) {
            isStartingFirstTime = false;
        }
        // at this point this flag has performed its role so I return it to its default value
        if (is_starting_after_destruction) {
            is_starting_after_destruction = false;
        }
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) + this.getLocalClassName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts_available && isFinishing()) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
        if (while_studying) {
            new Thread(() -> {
                Runnable pauseRunnable;
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudyAPeriodActivity.this);
                    if (!sharedPreferences.contains(StudyAPeriodActivity.class.getName() + Constants.PAUSING + current_field_id)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(StudyAPeriodActivity.class.getName() + Constants.PAUSING + current_field_id, true);
                        editor.commit();
                        toPause();
                    }
                } finally {
                    pauseRunnable = () -> {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudyAPeriodActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(StudyAPeriodActivity.class.getName() + Constants.PAUSING + current_field_id);
                        editor.commit();
                    };
                    mHandler.post(pauseRunnable);
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_a_period_activity_menu, menu);
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
                bundle2.putInt(TimeOfAnswerOfStudyAPeriodDialogFragment.READING_QUESTION_LETTER_SECOND, reading_question_letter_second);
                bundle2.putInt(TimeOfAnswerOfStudyAPeriodDialogFragment.FIND_ANSWER_TIME, find_answer_time);
                bundle2.putInt(TimeOfAnswerOfStudyAPeriodDialogFragment.TYPING_ANSWER_LETTER_SECOND, typing_answer_letter_second);
                bundle2.putBoolean(TimeOfAnswerOfStudyAPeriodDialogFragment.TIME_OF_ANSWER_SAVED, time_of_answer_saved);
                TimeOfAnswerOfStudyAPeriodDialogFragment timeOfAnswerOfStudyAPeriodDialogFragment = new TimeOfAnswerOfStudyAPeriodDialogFragment();
                timeOfAnswerOfStudyAPeriodDialogFragment.setArguments(bundle2);
                timeOfAnswerOfStudyAPeriodDialogFragment.show(getSupportFragmentManager(), timeOfAnswerOfStudyAPeriodDialogFragment.getClass().getName());
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
                            .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putString(TimerDialogFragment.MESSAGE, getString(R.string.app_name) + " " + getString(R.string.study_a_period));
                        TimerDialogFragment timerDialogFragment = new TimerDialogFragment();
                        timerDialogFragment.setArguments(bundle4);
                        timerDialogFragment.show(getSupportFragmentManager(), TimerDialogFragment.class.getName());
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

    private void toPause() {
        int[] array = new int[2];
        array[0] = currentQuestionCounter;
        array[1] = permitedTriesNumber;
        cursorLoaderAndUpdateDatabase.pauseStudyAPeriod(current_field_id, listForTest, array, repeatedEntries, lastPause);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!ActivityUtility.hasOpenedDialogs(this)) {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
        }
        /* Canceling the timers of time of answer when the user go away from the activity
         or before the activity finishes
         or before the activity destroyed due a change of configuration*/
        mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
        retainedFragment.setListForTest_StudyDuringActivity(listForTest);
    }

 /*   @Override
    public void onBackPressed() {
        super.onBackPressed();
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.STUDY_A_PERIOD, lastPause);
        ExitActivity.exit(this);
    }*/


    private String[][] prepareListForStudyFirstTime() {
        ArrayList<String> questions = new ArrayList<>(), answers = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            Timestamp timestamp = Timestamp.valueOf(cursor.getString(2));
            long thisTime = timestamp.getTime();
            if (thisTime >= startingTime) {
                questions.add(cursor.getString(0));
                answers.add(cursor.getString(1));
                ids.add(cursor.getLong(3));
            }
        }
        String[][] listForTest = new String[questions.size()][3];
        for (int i = 0; i < questions.size(); i++) {
            listForTest[i][0] = questions.get(i);
            listForTest[i][1] = answers.get(i);
            listForTest[i][2] = String.valueOf(ids.get(i));
        }
        ActivityUtility.shuffleArray(listForTest);
        return listForTest;
    }

    private String[][] prepareListForStudySecondAndMore(ArrayList<Integer> indices) {
        String[][] temp = new String[indices.size()][3];
        for (int i = 0; i < indices.size(); i++) {
            temp[i][0] = listForTest[indices.get(i)][0];
            temp[i][1] = listForTest[indices.get(i)][1];
            temp[i][2] = listForTest[indices.get(i)][2];
        }
        ActivityUtility.shuffleArray(temp);
        return temp;
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
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(currentQuestionCounter)) {
                    repeatedEntries.add(currentQuestionCounter);
                }
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
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(currentQuestionCounter)) {
                    repeatedEntries.add(currentQuestionCounter);
                }
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
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(currentQuestionCounter)) {
                    repeatedEntries.add(currentQuestionCounter);
                }
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
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(currentQuestionCounter)) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        }
        return result;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInstanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInstanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInstanceState.putBoolean(CHECK_BUTTON_STATE, check.isEnabled());
        savedInstanceState.putBoolean(_CONTINUE_BUTTON_STATE, _continue.isEnabled());
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        savedInstanceState.putBoolean(RESULT, result);
        savedInstanceState.putInt(HINT_COUNTER, hintCounter);
        savedInstanceState.putStringArrayList(HINTS, hints);
        savedInstanceState.putIntegerArrayList(REPEATED_ENTRIES, repeatedEntries);
        savedInstanceState.putInt(CONTENT_VISIBILITY, content.getVisibility());
        savedInstanceState.putInt(LOADING_VISIBILITY, mLoadingView.getVisibility());
        savedInstanceState.putLong(TIMER_LAST_PAUSE, lastPause);
        savedInstanceState.putStringArray(SYMBOL, symbol);
        savedInstanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        savedInstanceState.putBoolean(TIME_BETWEEN_ANSWERS_PASSED, is_time_of_answer_elapsed);
        savedInstanceState.putBoolean(TIME_FOR_ANSWER_SAVED, time_of_answer_saved);
        savedInstanceState.putBoolean(WHILE_STUDYING, while_studying);
        saveUserLayoutChanges(savedInstanceState);
                       /* Saving the remaining time till completion of the current timer of time of answer because the activity
        will be destroyed, not finishing normally.
        * We don't need to save this remaining time when the activity finishes normally
        * or when the user go away from the activity */
        if (time_of_starting_time_of_answer > -1) {
            savedInstanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER,
                    effective_time_of_answer - (System.currentTimeMillis() - time_of_starting_time_of_answer));
        }
                     /* To run scheduleWhatToDoAfterTimeOfAnswerElapsed() method after restarting the activity
        in case the activity destroyed while a dialog is shown */
        else if (ActivityUtility.hasOpenedDialogs(this) && time_of_answer > 0) {
            savedInstanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER, time_of_answer * 1000);
        }
        // call this method to prevent the dialogs from being shown after the recreation of the activity
        // this to prevent the call of manageTimersDueDialogsShow() when the dialog is shown while it really will not appear
        ActivityUtility.closeOpenedDialogs(this);
        super.onSaveInstanceState(savedInstanceState);
    }


    private String[][] readFromLinkerTableStudyDuringColumn() {
        String array[][] = new String[cursor.getCount()][3];
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            int order = cursor.getInt(2);
            array[order][0] = cursor.getString(0);
            array[order][1] = cursor.getString(1);
            array[order][2] = String.valueOf(cursor.getLong(3));
        }
        return array;
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
            String locale_short_name = databaseReadOperations.getFieldLocale(current_field_id);
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


    private void setNormalEnterAnswerBackground() {
        Runnable mRunnable = () -> {
            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
            show_result_linearlayout.setPadding(0, 0, 0, 0);
        };
        mHandler.postDelayed(mRunnable, RETURN_TIME);
    }

    @Override
    public void setTimeOfAnswer(int reading_question_letter_second, int find_answer_time, int typing_answer_letter_second, boolean time_for_answer_saved) {
        this.reading_question_letter_second = reading_question_letter_second;
        this.find_answer_time = find_answer_time;
        this.typing_answer_letter_second = typing_answer_letter_second;
        this.time_of_answer_saved = time_for_answer_saved;
        // to avoid the case if the listfortest has not been prepared yet
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                while (true) {
                    if (listForTest != null) {
                        time_of_answer = ActivityUtility.calculate_time_of_answer(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1],
                                reading_question_letter_second, find_answer_time, typing_answer_letter_second);
                        scheduleWhatToDoAfterTimeOfAnswerElapsed();
                        break;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        }.execute();
    }


    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {
        if (isCounting) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
            // to avoid the case if the listfortest has not been prepared yet
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    while (true) {
                        if (listForTest != null) {
                            scheduleWhatToDoAfterTimeOfAnswerElapsed();
                            break;
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute();
        } else {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            tool_bar_timer.stop();
            mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
            time_of_starting_time_of_answer = -1;
            is_time_of_answer_elapsed = false;
            show_question_linearlayout.setBackgroundResource(R.drawable.show_question_background);
            show_question_linearlayout.setPadding(0, 0, 0, 0);
            showQuestion.setTextColor(Color.WHITE);
        }
    }

    private void DoWhatYouHaveToDoAfterTimeOfAnswerElapsed() {
        show_question_linearlayout.setBackgroundResource(R.drawable.time_for_answer_passed_background);
        show_question_linearlayout.setPadding(0, 0, 0, 0);
        showQuestion.setTextColor(Color.BLACK);
        is_time_of_answer_elapsed = true;
        time_of_starting_time_of_answer = -1;
    }

    private void scheduleWhatToDoAfterTimeOfAnswerElapsed() {
        if (time_of_answer > 0) {
            effective_time_of_answer = time_of_answer * 1000;
            mHandler.postDelayed(what_to_do_after_time_of_answer_elapsed_runnable, effective_time_of_answer);
            time_of_starting_time_of_answer = System.currentTimeMillis();
        }
    }

    private void dontKnow() {
        enterAnswer.setText(listForTest[currentQuestionCounter][1]);
        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
        showTries.setText(Integer.toString(triesCounter + 1));
        check.setEnabled(false);
        _continue.setEnabled(true);
        enterAnswer.setEnabled(false);
        if (!repeatedEntries.contains(currentQuestionCounter)) {
            repeatedEntries.add(currentQuestionCounter);
        }
        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
        cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(Long.parseLong(listForTest[currentQuestionCounter][2]));
    }

    @Override
    public void obfuscateText(boolean obfuscate) {
        this.obfuscate = obfuscate;
        if (obfuscate) {
            showQuestion.setText(ActivityUtility.spanText(listForTest[currentQuestionCounter][0], showQuestion));
        } else {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
        }
    }

    private static class PrepareListForTest extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (studyAPeriodActivity.questionsNumber > 0) {
                studyAPeriodActivity.while_studying = true;
                if (studyAPeriodActivity.obfuscate) {
                    studyAPeriodActivity.showQuestion.setText(ActivityUtility.spanText(studyAPeriodActivity.listForTest[studyAPeriodActivity.currentQuestionCounter][0], studyAPeriodActivity.showQuestion));
                } else {
                    studyAPeriodActivity.showQuestion.setText(studyAPeriodActivity.listForTest[studyAPeriodActivity.currentQuestionCounter][0]);
                }
                studyAPeriodActivity.time_of_answer = ActivityUtility.calculate_time_of_answer(studyAPeriodActivity.listForTest[studyAPeriodActivity.currentQuestionCounter][0], studyAPeriodActivity.listForTest[studyAPeriodActivity.currentQuestionCounter][1],
                        studyAPeriodActivity.reading_question_letter_second, studyAPeriodActivity.find_answer_time, studyAPeriodActivity.typing_answer_letter_second);
                studyAPeriodActivity.ifMoreThanOnce();
                studyAPeriodActivity.showNumberOfQuetions.setText(Integer.toString(studyAPeriodActivity.questionsNumber));
                if (studyAPeriodActivity.boolean_continue) {
                    studyAPeriodActivity.remainingQuestionCounter.setText(Integer.toString(studyAPeriodActivity.questionsNumber - 1 - studyAPeriodActivity.currentQuestionCounter));
                } else {
                    studyAPeriodActivity.remainingQuestionCounter.setText(Integer.toString(studyAPeriodActivity.questionsNumber - 1));
                }
                studyAPeriodActivity.enterAnswer.requestFocus();
                studyAPeriodActivity.scheduleWhatToDoAfterTimeOfAnswerElapsed();
            } else {
                Toast toast = Toast.makeText(studyAPeriodActivity, studyAPeriodActivity.getString(R.string.no_entries_in_this_time_range), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                studyAPeriodActivity.finish();
                ExitActivity.exit(studyAPeriodActivity);
            }
            ActivityUtility.crossFade(studyAPeriodActivity.content, studyAPeriodActivity.mLoadingView, studyAPeriodActivity);
            studyAPeriodActivity.tool_bar_timer.start();
        }
    }
}
