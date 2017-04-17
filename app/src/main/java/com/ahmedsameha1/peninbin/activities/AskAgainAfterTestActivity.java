package com.ahmedsameha1.peninbin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TimeOfAnswerOfStudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed_000 on 9/23/2015.
 */
public class AskAgainAfterTestActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener,
        TimeOfAnswerOfStudyAPeriodDialogFragment.Contact,
        ActivityUtility.ActivityTimers {
    public static final String SCREEN_NAME = "StudyAPeriodActivity";
    public static final String REPEATED_ENTRIES = "repeatedEntries";
    public static final String LAST_SESSION_OR_NEW_ONE = "lastSessionOrNewOne";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    public static final String RESULT = "result";
    private static final String CUSTOMABLE_TEXT = "customable_text";
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
    boolean boolean_continue, actionBarShown, is_time_of_answer_elapsed = false;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private ArrayList<Integer> repeatedEntries = new ArrayList<Integer>();
    private ArrayList<String> hints;
    private boolean result, time_of_answer_saved;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuetions, showActionbar, dont_know;
    private TextView nextHint, check, _continue;
    private LinearLayout show_result_linearlayout, show_question_linearlayout, container;
    private CardView check_card_view, next_hint_card_view, show_action_bar_card_view, continue_card_view;
    private DatabaseReadOperations databaseOperations;
    private RetainedFragment retainedFragment;
    private Handler mHandler = new Handler();
    private long current_field_id;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private int check_type;
    private Tracker mTracker;
    private int mShortAnimationDuration;
    private View mLoadingView;
    private RelativeLayout content;
    private Chronometer tool_bar_timer;
    private long lastPause;
    private LoaderManager.LoaderCallbacks<Cursor> loader;
    private String[] symbol, customable_text;
    private int current_field_type;
    private boolean isStartingFirstTime, is_starting_after_destruction;
    private int time_of_answer;
    private long time_of_starting_time_of_answer = -1, effictive_time_of_answer;
    private Runnable what_to_do_after_time_of_answer_elapsed_runnable = this::DoWhatYouHaveToDoAfterTimeOfAnswerElapsed;
    private ArrayList<WrongItem> wrongItemArrayList;

    private int question_data_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_a_period);
        boolean isReadAnswer;
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        permitedTriesNumber = getIntent().getIntExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, 0);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        databaseOperations = new DatabaseReadOperations(this);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        time_of_answer = databaseOperations.getTimeOfAnswerOfStudyAPeroid(current_field_id);
        time_of_answer_saved = time_of_answer > 0;
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
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
        tool_bar_timer = (Chronometer) findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
                if (id == GET_SPECIAL_TEXT_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(AskAgainAfterTestActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
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
                if (id == GET_SPECIAL_TEXT_LOADER_ID) {
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
        if (savedInstanceState == null) {
            content.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
            lastPause = databaseOperations.getElapsedPausedSessionTime(current_field_id, Constants.ElapsedPausedSessionTime.STUDY_A_PERIOD);
            isStartingFirstTime = true;
        } else {
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE ? View.VISIBLE : View.GONE);
            lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        }
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, LIST_FOR_TEST).commit();
        }
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
        wrongItemArrayList = retainedFragment.getWrongItemArrayList_ask_again_after_test_activity() != null ?
                retainedFragment.getWrongItemArrayList_ask_again_after_test_activity()
                :(ArrayList<WrongItem>) getIntent().
                getBundleExtra(WrongAnswersActivity.WRONG_ITEM_LIST)
                .getSerializable(WrongAnswersActivity.WRONG_ITEM_LIST);
        questionsNumber = wrongItemArrayList.size();
        showQuestion.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getQuestion()));
        ifMoreThanOnce();
        showNumberOfQuetions.setText(Integer.toString(questionsNumber));
        showTries.setText(Integer.toString(triesCounter + 1));
        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1));

        scheduleWhatToDoAfterTimeOfAnswerElapsed();
        crossfade();
        check.setOnClickListener(view -> {
            check_card_view.startAnimation(animAlpha);
            result = checkAnAnswer();
            if (triesCounter < permitedTriesNumber && !result) {
                showQuestion.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getQuestion()));
                showTries.setText(Integer.toString(triesCounter + 1));
            } else if (!result) {
                enterAnswer.setText(wrongItemArrayList.get(currentQuestionCounter).getAnswer());
                triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                showTries.setText(Integer.toString(triesCounter + 1));
                check.setEnabled(false);
                _continue.setEnabled(true);
                enterAnswer.setEnabled(false);
            } else {
                triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                showTries.setText(Integer.toString(triesCounter + 1));
                currentQuestionCounter++;
                if (currentQuestionCounter >= questionsNumber) {
                    currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                    questionsNumber = 0;
                    enterAnswer.setHint(R.string.enter_your_answer);
                    hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                    if (repeatedEntries.size() > 0) {
                        wrongItemArrayList = prepareListForStudySecondAndMore(repeatedEntries);
                        questionsNumber = wrongItemArrayList.size();
                        repeatedEntries.clear();
                        showQuestion.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getQuestion()));
                        showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                        remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                        ifMoreThanOnce();
                        mHandler.removeCallbacks(what_to_do_after_time_of_answer_elapsed_runnable);
                        time_of_starting_time_of_answer = -1;
                        scheduleWhatToDoAfterTimeOfAnswerElapsed();
                    } else {
                        cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, question_data_type);
                        cursorLoaderAndUpdateDatabase.clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(current_field_id);
                        if (tts_available) {
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    while (true) {
                                        if (!tts.isSpeaking()) {
                                            finish();
                                            ExitActivity.exit(AskAgainAfterTestActivity.this);
                                            break;
                                        }
                                    }
                                    return null;
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                        } else {
                            finish();
                            ExitActivity.exit(AskAgainAfterTestActivity.this);
                        }
                    }
                } else {
                    showQuestion.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getQuestion()));
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
                    Toast toast = Toast.makeText(AskAgainAfterTestActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
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
        dont_know.setOnClickListener(view -> dontKnow());
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
            Toast toast = Toast.makeText(AskAgainAfterTestActivity.this,
                    getString(R.string.number_of_questions, showNumberOfQuetions.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        remainingQuestionCounter.setOnClickListener(v -> {
            v.startAnimation(animAlpha);
            Toast toast = Toast.makeText(AskAgainAfterTestActivity.this,
                    getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        showTries.setOnClickListener(v -> {
            v.startAnimation(animAlpha);
            Toast toast = Toast.makeText(AskAgainAfterTestActivity.this,
                    getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!ActivityUtility.hasOpenedDialogs(this)) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
        }
        /* do not call scheduleWhatToDoAfterTimeOfAnswerElapsed() if first time because the it will be called from the loader onfinished()
         * do not call it after destruction because it will be called from onrestoreinstancestate method
         * do not call it if the test has already finished
         */
        if (!isStartingFirstTime && !is_starting_after_destruction) {
            scheduleWhatToDoAfterTimeOfAnswerElapsed();
        }
        // at this point this flag has performed its role so I return it to its default value
        if (isStartingFirstTime) {
            isStartingFirstTime = false;
        }
        // at this point this flag has performed its role so I return it to its default value
        if (is_starting_after_destruction) {
            is_starting_after_destruction = false;
        }
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) +SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onStop() {
        super.onStop();
        if (tts_available && isFinishing()) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ask_again_after_test_activity_menu, menu);
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
                bundle2.putInt(TimeOfAnswerOfStudyAPeriodDialogFragment.CURRENT_TIME_OF_ANSWER, time_of_answer);
                bundle2.putBoolean(TimeOfAnswerOfStudyAPeriodDialogFragment.TIME_OF_ANSWER_SAVED, time_of_answer_saved);
                TimeOfAnswerOfStudyAPeriodDialogFragment timeOfAnswerOfStudyAPeriodDialogFragment = new TimeOfAnswerOfStudyAPeriodDialogFragment();
                timeOfAnswerOfStudyAPeriodDialogFragment.setArguments(bundle2);
                timeOfAnswerOfStudyAPeriodDialogFragment.show(getSupportFragmentManager(), timeOfAnswerOfStudyAPeriodDialogFragment.getClass().getName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        retainedFragment.setWrongItemArrayList_ask_again_after_test_activity(wrongItemArrayList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (databaseOperations.isThisFieldStudyPeriodPaused(current_field_id)) {
            lastPause = SystemClock.elapsedRealtime() - tool_bar_timer.getBase();
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.STUDY_A_PERIOD, lastPause);
        } else {
            cursorLoaderAndUpdateDatabase.setElapsedPausedSessionTime(current_field_id,
                    Constants.ElapsedPausedSessionTime.STUDY_A_PERIOD, Constants.ElapsedPausedSessionTime.NOT_USED);
        }
    }


    private ArrayList<WrongItem> prepareListForStudySecondAndMore(ArrayList<Integer> indices) {
        ArrayList<WrongItem> arrayList = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            arrayList.add(new WrongItem(wrongItemArrayList.get(indices.get(i)).getEntry_id(),
                    wrongItemArrayList.get(indices.get(i)).getQuestion(),
                    wrongItemArrayList.get(indices.get(i)).getAnswer(),null ));
        }
        Collections.shuffle(arrayList);
        return arrayList;
    }

    private void ifMoreThanOnce() {
        hints = ActivityUtility.getHints(this, current_field_id, question_data_type,
                wrongItemArrayList.get(currentQuestionCounter).getQuestion(), wrongItemArrayList.get(currentQuestionCounter).getAnswer());
        if (hints != null) {
            nextHint.setEnabled(true);
        } else {
            nextHint.setEnabled(false);
        }
    }


    @SuppressWarnings("deprecation")
    private boolean checkAnAnswer() {
        cursorLoaderAndUpdateDatabase.increaseEntryAskedByOne(wrongItemArrayList.get(currentQuestionCounter).getEntry_id());
        boolean result = false;
        if (check_type == Contract.Field.NON_STRICT_IGNORE_CASE) {
            if (CompareUtility.nonStrictCompareIgnoreCase(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), enterAnswer.getText().toString())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(wrongItemArrayList.get(currentQuestionCounter).getEntry_id());
                setNormalEnterAnswerBackground();
                result = false;
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if (check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE) {
            if (CompareUtility.nonStrictCompare(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), enterAnswer.getText().toString())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(wrongItemArrayList.get(currentQuestionCounter).getEntry_id());
                setNormalEnterAnswerBackground();
                result = false;
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if (check_type == Contract.Field.IGNORE_CASE) {
            if (enterAnswer.getText().toString().equalsIgnoreCase(wrongItemArrayList.get(currentQuestionCounter).getAnswer())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(wrongItemArrayList.get(currentQuestionCounter).getEntry_id());
                setNormalEnterAnswerBackground();
                result = false;
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if (check_type == Contract.Field.DO_NOT_IGNORE_CASE) {
            if (enterAnswer.getText().toString().equals(wrongItemArrayList.get(currentQuestionCounter).getAnswer())) {
                if (locale_available) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null, null);
                    } else {
                        tts.speak(wrongItemArrayList.get(currentQuestionCounter).getAnswer(), TextToSpeech.QUEUE_ADD, null);
                    }
                }
                show_result_linearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                setNormalEnterAnswerBackground();
                result = true;
            } else {
                show_result_linearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                show_result_linearlayout.setPadding(0, 0, 0, 0);
                cursorLoaderAndUpdateDatabase.increaseEntryWrongAnsweredCountByOne(wrongItemArrayList.get(currentQuestionCounter).getEntry_id());
                setNormalEnterAnswerBackground();
                result = false;
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        }
        return result;
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
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
        saveUserLayoutChanges(savedInstanceState);
                       /* Saving the remaining time till completion of the current timer of time of answer because the activity
        will be destroyed, not finishing normally.
        * We don't need to save this remaining time when the activity finishes normally
        * or when the user go away from the activity */
        if (time_of_starting_time_of_answer > -1) {
            savedInstanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER,
                    effictive_time_of_answer - (System.currentTimeMillis() - time_of_starting_time_of_answer));
        }
                     /* To run scheduleWhatToDoAfterTimeOfAnswerElapsed() method after restarting the activity
        in case the activity destroyed while a dialog is shown */
        else if (ActivityUtility.hasOpenedDialogs(this)) {
            savedInstanceState.putLong(HOW_MUCH_TIME_REMAINING_FROM_TIME_FOR_ANSWER, time_of_answer * 1000);
        }
        // call this method to prevent the dialogs from being shown after the recreation of the activity
        // this to prevent the call of manageTimersDueDialogsShow() when the dialog is shown while it really will not appear
        ActivityUtility.closeOpenedDialogs(this);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        boolean checkState = savedInstanceState.getBoolean(CHECK_BUTTON_STATE);
        boolean _continueState = savedInstanceState.getBoolean(_CONTINUE_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
        repeatedEntries = savedInstanceState.getIntegerArrayList(REPEATED_ENTRIES);
        lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
        symbol = savedInstanceState.getStringArray(SYMBOL);
        time_of_answer_saved = savedInstanceState.getBoolean(TIME_FOR_ANSWER_SAVED);
        is_time_of_answer_elapsed = savedInstanceState.getBoolean(TIME_BETWEEN_ANSWERS_PASSED);
        is_starting_after_destruction = true;
        restoreUserLayoutChanges(savedInstanceState);
        showQuestion.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getQuestion()));
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
                effictive_time_of_answer = how_much_time_remaining_time_for_answer;
                mHandler.postDelayed(what_to_do_after_time_of_answer_elapsed_runnable, effictive_time_of_answer);
                time_of_starting_time_of_answer = System.currentTimeMillis();
            }
        }
        if (hintCounter > 0 && enterAnswer.getText().toString().length() <= 0) {
            enterAnswer.setHint(hints.get(hintCounter - 1));
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

    private void setNormalEnterAnswerBackground() {
        Runnable mRunnable = () -> {
            show_result_linearlayout.setBackgroundResource(R.drawable.enter_answer_background);
            show_result_linearlayout.setPadding(0, 0, 0, 0);
        };
        mHandler.postDelayed(mRunnable, RETURN_TIME);
    }

    @Override
    public void setTimeOfAnswer(int time_of_answer, boolean time_for_answer_saved) {
        this.time_of_answer = time_of_answer;
        this.time_of_answer_saved = time_for_answer_saved;
        scheduleWhatToDoAfterTimeOfAnswerElapsed();
    }


    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {
        if (isCounting) {
            tool_bar_timer.setBase(SystemClock.elapsedRealtime() - lastPause);
            tool_bar_timer.start();
            scheduleWhatToDoAfterTimeOfAnswerElapsed();
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
            effictive_time_of_answer = time_of_answer * 1000;
            mHandler.postDelayed(what_to_do_after_time_of_answer_elapsed_runnable, effictive_time_of_answer);
            time_of_starting_time_of_answer = System.currentTimeMillis();
        }
    }

    private void dontKnow() {
        enterAnswer.setText(ActivityUtility.spanText(wrongItemArrayList.get(currentQuestionCounter).getAnswer()));
        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
        showTries.setText(Integer.toString(triesCounter + 1));
        check.setEnabled(false);
        _continue.setEnabled(true);
        enterAnswer.setEnabled(false);
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
