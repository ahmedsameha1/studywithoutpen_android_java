package com.ahmedsameha1.peninbin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.inputmethod.InputMethodManager;
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
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.utils.OnSwipeTouchListener;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by ahmed_000 on 9/23/2015.
 */
public class StudyDuringActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener {
    public static final String SCREEN_NAME = "StudyDuringActivity";
    public static final String ACTIVITY_NAME = "StudyDuringActivity";
    public static final String STARTING_TIME = "startingTime";
    public static final String REPEATED_ENTRIES = "repeatedEntries";
    public static final String STRING_REPRESENTING_REPEATED_ENTRIES = "strigRepresentingRepeatedEntries";
    public static final String LAST_SESSION_OR_NEW_ONE = "lastSessionOrNewOne";
    public static final String DASH = "-";
    public static final String NUMBER_OF_TRIES = "numberOftries";
    public static final int CONTINUE_PREVIOUS_SESSION = 1;
    public static final int START_NEW_SESSION = 2;
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String PAUSING = "pausing";
    private static final String CONTENT_VISIBILITY = "content_visibility";
    private static final String LOADING_VISIBILITY = "loading_visibility";
    private static final String CURRENT_ENTRY_ID = "current_entry_id";
    private long startingTime;
    boolean boolean_continue, actionBarShown;
    private Cursor cursor;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private ArrayList<String> questions = new ArrayList<String>(), answers = new ArrayList<String>();
    private ArrayList<Integer> repeatedEntries = new ArrayList<Integer>();
    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int RETURN_TIME = 1000;
    public static final String LIST_FOR_TEST = "listForTest";
    public static final String CURRENT_QUESTION_COUNTER = "currentQuestionCounter";
    public static final String TRIES_COUNTER = "triesCounter";
    public static final String CHECK_BUTTON_STATE = "checkButtonState";
    public static final String RESULT = "result";
    public static final String TESTING_NOW = "testingNow";
    public static final String NUMBER_OF_QUESTIONS = "numberOfQuestions";
    public static final String HINT_COUNTER = "hintCounter";
    public static final String HINTS = "hints";
    public static final String _CONTINUE_BUTTON_STATE = "_continueButtonState";
    private String[][] listForTest;
    private ArrayList<String> hints;
    private boolean result, testingNow = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private Cursor searchForCursor;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuetions, showActionbar;
    private TextView nextHint, check, _continue;
    private LinearLayout showResult, questionLinearlayout, container;
    private CardView check_card_view, next_hint_card_view, show_action_bar_card_view,continue_card_view;
    private DatabaseOperations databaseOperations;
    private RetainedFragment retainedFragment;
    private Handler mHandler = new Handler();
    private long current_field_id, current_entry_id;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private int check_type;
    private Tracker mTracker;
    private int mShortAnimationDuration;
    private View mLoadingView;
    private RelativeLayout content;

    private int question_data_type;private Thread pauseThread = new Thread(new Runnable() {
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
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudyDuringActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(ACTIVITY_NAME + PAUSING + current_field_id);
                        editor.commit();
                        Toast toast = Toast.makeText(StudyDuringActivity.this, R.string.pause_shutdown, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                };
                mHandler.post(pauseRunnable);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_during);
        boolean isReadAnswer;
        int question_area_size,answer_area_size,test_text_size,action_bar_shown;
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        startingTime = getIntent().getLongExtra(STARTING_TIME, 0);
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        databaseOperations = new DatabaseOperations(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        showResult = (LinearLayout) findViewById(R.id.result);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        container = (LinearLayout) findViewById(R.id.container);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        check = (TextView) findViewById(R.id.check);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);
        _continue = (TextView) findViewById(R.id._continue);
        nextHint = (TextView) findViewById(R.id.next_hint);
        showTries = (TextView) findViewById(R.id.trys);
        showNumberOfQuetions = (TextView) findViewById(R.id.number_of_questions);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        check_card_view = (CardView) findViewById(R.id.check_card_view);
        continue_card_view = (CardView) findViewById(R.id.continue_card_view);
        next_hint_card_view = (CardView) findViewById(R.id.next_hint_card_view);
        show_action_bar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        if ( savedInstanceState == null ) {
            content.setVisibility(View.GONE);
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        } else {
            content.setVisibility(savedInstanceState.getInt(CONTENT_VISIBILITY) == View.VISIBLE? View.VISIBLE : View.GONE);
            mLoadingView.setVisibility(savedInstanceState.getInt(LOADING_VISIBILITY) == View.VISIBLE? View.VISIBLE : View.GONE);
        }
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, LIST_FOR_TEST).commit();
        }
        setSupportActionBar(toolbar);
        if ( action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        if ( isReadAnswer ) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        showTries.setText(Integer.toString(triesCounter + 1));
        if (boolean_continue) {
            int[] array = databaseOperations.getDataFromStudyPeriodPaused(current_field_id);
            currentQuestionCounter = array[0];
            permitedTriesNumber = array[1];
            repeatedEntries = databaseOperations.getStudyPeriodRepeatedEntries(current_field_id);
            getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION, null, this);
        } else {
            permitedTriesNumber = getIntent().getExtras().getInt(NUMBER_OF_TRIES);
            getLoaderManager().initLoader(START_NEW_SESSION, null, this);
        }
        changeAreas(question_area_size,answer_area_size);
        if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.SMALL_SIZE);
        } else if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.NORMAL_SIZE);
        } else if ( test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE ) {
            changeTextSize(ChangeTextSizeDialogFragment.LARGE_SIZE);
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_card_view.startAnimation(animAlpha);
                testingNow = true;
                if (enterAnswer.getText().toString().length() > 0) {

                    result = checkAnAnswer();
                    if (triesCounter < permitedTriesNumber && !result) {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1]);
                                return null;
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
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
                                showQuestion.setText(listForTest[currentQuestionCounter][0]);
                                new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1]);
                                        return null;
                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
                                showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                                ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                            } else {
                                mySQLiteCursorLoader.resetEntriesStudyPeriodOrder(current_field_id, question_data_type);
                                mySQLiteCursorLoader.clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(current_field_id);
                                finish();
                            }
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1]);
                                    return null;
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void []) null);
                            showTries.setText(Integer.toString(triesCounter + 1));
                            ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(StudyDuringActivity.this, R.string.where_the_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                enterAnswer.setHint(R.string.enter_your_answer);
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
                        Toast toast = Toast.makeText(StudyDuringActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
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
                if (s.length() > 0 && permitedTriesNumber != 1) {
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
                if ( s.length() > 0 ) {
                    check.setEnabled(true);
                } else {
                    check.setEnabled(false);
                }
            }
        });

        _continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continue_card_view.startAnimation(animAlpha);
                enterAnswer.setText("");
                _continue.setEnabled(false);
                enterAnswer.setEnabled(true);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(enterAnswer.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                enterAnswer.requestFocus();
                enterAnswer.setHint(R.string.enter_your_answer);
                if (permitedTriesNumber == 1) {
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
            }
        });
        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_action_bar_card_view.startAnimation(animAlpha);
                if (actionBarShown) {
                    getSupportActionBar().hide();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.show);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id,Contract.Field.DO_NOT_SHOW_ACTION_BAR);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.hide);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id,Contract.Field.SHOW_ACTION_BAR);
                }
            }
        });
        showNumberOfQuetions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuetions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        remainingQuestionCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        showTries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animAlpha);
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
    }
    @Override
    public void onStop(){
        super.onStop();
        if ( tts_available && isFinishing() ) {
            tts.stop();
        }
        UsageLastTime.updateUsageLastTime(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_during_activity_menu, menu);
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
                Toast toast = Toast.makeText(this, R.string.pause_do_not_shutdown, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                pauseThread.start();
                finish();
                return true;
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (showQuestion.getTextSize() / scaledDensity));
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        retainedFragment.setListForTest_StudyDuringActivity(listForTest);
    }

    private void toPause() {
        int[] array = new int[2];
        array[0] = currentQuestionCounter;
        array[1] = permitedTriesNumber;
        mySQLiteCursorLoader.clearThisFieldFromStudyPeriodPausedAndFromStudyPeriodRepeatedEntries(current_field_id);
        mySQLiteCursorLoader.putStudyPeriodPausedData(current_field_id, array);
        mySQLiteCursorLoader.resetEntriesStudyPeriodOrder(current_field_id, question_data_type);
        mySQLiteCursorLoader.putEntriesStudyPeriodOrder(current_field_id, question_data_type, listForTest);
        mySQLiteCursorLoader.putStudyPeriodRepeatedEntries(current_field_id, repeatedEntries);
    }

    private String[][] prepareListForStudyFirstTime() {
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            Timestamp timestamp = Timestamp.valueOf(cursor.getString(2));
            long thisTime = timestamp.getTime();
            if (thisTime >= startingTime) {
                questions.add(cursor.getString(0));
                answers.add(cursor.getString(1));
            }
        }
        String[][] listForTest = new String[questions.size()][2];
        for (int i = 0; i < questions.size(); i++) {
            listForTest[i][0] = questions.get(i);
            listForTest[i][1] = answers.get(i);
        }
        shuffleArray(listForTest);
        return listForTest;
    }

    private String[][] prepareListForStudySecondAndMore(ArrayList<Integer> indices) {
        String[][] temp = new String[indices.size()][2];
        for (int i = 0; i < indices.size(); i++) {
            temp[i][0] = listForTest[indices.get(i)][0];
            temp[i][1] = listForTest[indices.get(i)][1];
        }
        shuffleArray(temp);
        return temp;
    }

    private void ifMoreThanOnce(String currentQuestion, String currentAnswer) {
        if (searchForCursor != null) {
            searchForCursor.close();
        }
        searchForCursor = databaseOperations.searchExactlyFor(current_field_id, question_data_type, currentQuestion);
        if (searchForCursor.getCount() > 1) {
            hints = new ArrayList<String>(3);
            hints.add(getString(R.string.first_character) + currentAnswer.charAt(0));
            hints.add(getString(R.string.number_of_characters) + Integer.toString(currentAnswer.length()));
            hints.add(getString(R.string.last_character) + currentAnswer.charAt(currentAnswer.length() - 1));
            nextHint.setEnabled(true);

        } else {
            hints = null;
            nextHint.setEnabled(false);

        }
        searchForCursor.close();
    }
    @SuppressWarnings("deprecation")
    private boolean checkAnAnswer() {
        mySQLiteCursorLoader.increaseEntryAskedByOne(current_entry_id);
        Runnable mRunnable;
        boolean result = false;
        if ( check_type == Contract.Field.NON_STRICT_IGNORE_CASE ) {
            if ( CompareUtility.nonStrictCompareIgnoreCase(listForTest[currentQuestionCounter][1],enterAnswer.getText().toString()) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            } else {
    //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mySQLiteCursorLoader.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
    //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
    //            wrongAnswersCounter++;
    //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
    //            wrongItems.add(wrongItem);
    //            wrongAnswers = null;
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if ( check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE ) {
            if ( CompareUtility.nonStrictCompare(listForTest[currentQuestionCounter][1],enterAnswer.getText().toString()) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            } else {
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mySQLiteCursorLoader.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if ( check_type == Contract.Field.IGNORE_CASE ) {
            if ( enterAnswer.getText().toString().equalsIgnoreCase(listForTest[currentQuestionCounter][1]) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            } else {
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mySQLiteCursorLoader.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        } else if ( check_type == Contract.Field.DO_NOT_IGNORE_CASE ) {
            if ( enterAnswer.getText().toString().equals(listForTest[currentQuestionCounter][1]) ) {
                if ( locale_available ) {
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null,null);
                    } else {
                        tts.speak(listForTest[currentQuestionCounter][1],TextToSpeech.QUEUE_ADD,null);
                    }
                }
                showResult.setBackgroundResource(R.drawable.correct_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = true;
            } else {
                //            check.setBackgroundResource(R.drawable.ic_wrong_sign);
                showResult.setBackgroundResource(R.drawable.wrong_answer_background);
                showResult.setPadding(0, 0, 0, 0);
                mySQLiteCursorLoader.increaseEntryWrongAnsweredCountByOne(current_entry_id);
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showResult.setBackgroundResource(R.drawable.enter_answer_background);
                        showResult.setPadding(0, 0, 0, 0);
                    }
                };
                mHandler.postDelayed(mRunnable, RETURN_TIME);
                result = false;
                //            wrongAnswers.add(enterAnswer.getText().toString());
            }
            enterAnswer.setText("");
            triesCounter++;
            if (triesCounter == permitedTriesNumber && !result) {
                //            wrongAnswersCounter++;
                //            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
                //            wrongItems.add(wrongItem);
                //            wrongAnswers = null;
                if (!repeatedEntries.contains(Integer.valueOf(currentQuestionCounter))) {
                    repeatedEntries.add(currentQuestionCounter);
                }
            }
        }
        return result;
    }

    private void shuffleArray(String[][] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = array[index][0];
            String answer = array[index][1];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[i][0] = question;
            array[i][1] = answer;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInstanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInstanceState.putBoolean(RESULT, result);
        savedInstanceState.putBoolean(CHECK_BUTTON_STATE, check.isEnabled());
        savedInstanceState.putBoolean(_CONTINUE_BUTTON_STATE, _continue.isEnabled());
        savedInstanceState.putBoolean(TESTING_NOW, testingNow);
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        savedInstanceState.putInt(HINT_COUNTER, hintCounter);
        savedInstanceState.putStringArrayList(HINTS, hints);
        savedInstanceState.putIntegerArrayList(REPEATED_ENTRIES, repeatedEntries);
        savedInstanceState.putInt(CONTENT_VISIBILITY,content.getVisibility());
        savedInstanceState.putInt(LOADING_VISIBILITY,mLoadingView.getVisibility());
        savedInstanceState.putLong(CURRENT_ENTRY_ID,current_entry_id);
        saveUserLayoutChanges(savedInstanceState);
      /*  if (!isFinishing()) {
            pauseThread.start();
        }*/
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean checkState = savedInstanceState.getBoolean(CHECK_BUTTON_STATE);
        boolean _continueState = savedInstanceState.getBoolean(_CONTINUE_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        repeatedEntries = savedInstanceState.getIntegerArrayList(REPEATED_ENTRIES);
        current_entry_id = savedInstanceState.getLong(CURRENT_ENTRY_ID);
        restoreUserLayoutChanges(savedInstanceState);
        if (testingNow) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            showTries.setText(Integer.toString(triesCounter + 1));
            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            showNumberOfQuetions.setText(Integer.toString(questionsNumber));
            check.setEnabled(checkState);
//            enterAnswer.setEnabled(checkState);
            _continue.setEnabled(_continueState);
            enterAnswer.setEnabled(! _continueState);

//            enterAnswer.setEnabled(true);
            ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
        }
        if (hintCounter > 0) {
            enterAnswer.setHint(hints.get(hintCounter - 1));
        }
    }

    private String convertRepeatedEntriesArrayListToString() {
//        StringBuilder stringBuilder = new StringBuilder();
        String s = "";
        for (int i = 0; i < repeatedEntries.size(); i++) {
            s += Integer.toString(repeatedEntries.get(i));
            if (i < repeatedEntries.size() - 1) {
                s += "-";
            }
        }
        return s;
    }

    private ArrayList<Integer> convertStringToRepeatedEntriesArrayList(String s) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        if (s == null) {
            return arrayList;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(s, "-");
        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(Integer.parseInt(stringTokenizer.nextToken()));
        }
        return arrayList;
    }

    /*    private String [][] readFromLinkerTableStudyDuringColumn() {
            String array [][] = new String [cursor.getCount()][2];
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                array [cursor.getPosition()][0] = cursor.getString(0);
                array [cursor.getPosition()][1] = cursor.getString(1);
            }
            return array;
        }*/
    private String[][] readFromLinkerTableStudyDuringColumn() {
        String array[][] = new String[cursor.getCount()][2];
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            int order = cursor.getInt(2);
            array[order][0] = cursor.getString(0);
            array[order][1] = cursor.getString(1);
        }
        return array;
    }
    private long getEntry_id( String queston, String answer ) {
        long entry_id = 0;
        boolean which_cursor = boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
              cursor.moveToNext() ) {
            if ( which_cursor ) { // continue previous session
                if ( cursor.getString(0).equals(queston) && cursor.getString(1).equals(answer) ) {
                    entry_id = cursor.getLong(3);
                    break;
                }
            } else {
                if ( cursor.getString(0).equals(queston) && cursor.getString(1).equals(answer) ) {
                    entry_id = cursor.getLong(3);
                    break;
                }
            }
        }
        return entry_id;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == CONTINUE_PREVIOUS_SESSION) {

            return new MySQLiteCursorLoader(StudyDuringActivity.this,
                    "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a , se." + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " , se." + Contract.COLUMN_ID
                            + " FROM " + Contract.STring.TABLE_NAME
                            + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " >= ? ",
                    new String[]{current_field_id + "", "0"});
           /* return new CursorLoader(this, uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                    new String[]{"0", current_field_name},
                    null);*/
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,uri,
                    new String[] {Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_QUESTION, Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_ANSWER},
                    Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD + " = ?",
                    new String[] {current_field_name},
                    null);*/
        } else {
            return new MySQLiteCursorLoader(StudyDuringActivity.this,
                    "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a , se." + Contract.CREATION_DATE + " , se." + Contract.COLUMN_ID
                            + " FROM " + Contract.STring.TABLE_NAME
                            + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                    new String[]{current_field_id + ""});
           /* Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,
                    uri,
                    new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK, Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                    new String[]{current_field_name},
                    null);*/
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.cursor = cursor;
        listForTest = retainedFragment.getListForTest_StudyDuringActivity();
        if (listForTest == null) {
            if (boolean_continue) {
                listForTest = readFromLinkerTableStudyDuringColumn();
            } else {
                listForTest = prepareListForStudyFirstTime();
            }
        }
        questionsNumber = listForTest.length;
        if (questionsNumber > 0) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    current_entry_id = getEntry_id(listForTest[currentQuestionCounter][0],listForTest[currentQuestionCounter][1]);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
            showNumberOfQuetions.setText(Integer.toString(questionsNumber));
            if (boolean_continue) {
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            } else {
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1));
            }

        } else if (questionsNumber <= 0 && loader.getId() == START_NEW_SESSION) {
            Toast toast = Toast.makeText(this, getString(R.string.no_entries_in_this_time_range), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
            finish();
        }
        crossfade();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
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
