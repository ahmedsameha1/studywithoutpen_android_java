package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed_000 on 10/6/2015.
 */
public class StudyWrongAnswersActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        TextToSpeech.OnInitListener,
        ActivityUtility.ActivityTimers {
    public static final String SCREEN_NAME = "StudyWrongAnswersActivity";
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int RETURN_TIME = 1000;
    private String currentQuestion, currentAnswer;
    private TextView correctAnswers,check,showActionbar;
    private ToggleButton showCurrentEntry;
    private CheckBox studyQuestion, studyAnswer;
    private EditText enterQuestion, enterAnswer;
    private LinearLayout answerLinearlayout,questionLinearlayout, container;
    private int correctAnswersCounter = 0;
    private ArrayList<WrongItem> wrongItemsList;
    private boolean actionBarShown;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private DatabaseReadOperations databaseOperations;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;
    private int check_type;
    private Tracker mTracker;
    private Chronometer timer;
    private long lastPause;
    private LoaderManager.LoaderCallbacks<Cursor> loader;
    private String[] symbol, customable_text;
    private int current_field_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_wrong_answers);
        boolean isReadAnswer;
        int question_area_size,answer_area_size,test_text_size,action_bar_shown;
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        currentQuestion = bundle.getString(CURRENT_QUESTION);
        currentAnswer = bundle.getString(CURRENT_ANSWER);
        wrongItemsList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
        current_field_id = bundle.getLong(StudyActivity.CURRENT_FIELD_ID);
        databaseOperations = new DatabaseReadOperations(this);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(this);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        showCurrentEntry = (ToggleButton) findViewById(R.id.show_current_study_entry);
        studyQuestion = (CheckBox) findViewById(R.id.study_question);
        studyAnswer = (CheckBox) findViewById(R.id.study_answer);
        enterQuestion = (EditText) findViewById(R.id.question);
        enterAnswer = ( EditText) findViewById(R.id.answer);
        correctAnswers = ( TextView) findViewById(R.id.correct_answers);
        check = (TextView) findViewById(R.id.check);
        ImageButton up = (ImageButton) findViewById(R.id.up);
        final ImageButton down = (ImageButton) findViewById(R.id.down);
        answerLinearlayout = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        final CardView show_actionbar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView check_card_view = (CardView) findViewById(R.id.check_card_view);
        final CardView show_current_study_entry_card_view = (CardView) findViewById(R.id.show_current_study_entry_card_view);
        final CardView up_card_view = (CardView) findViewById(R.id.up_card_view);
        final CardView down_card_view = (CardView) findViewById(R.id.down_card_view);
        timer = (Chronometer)findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        setSupportActionBar(toolbar);
        if ( action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show );
        enterQuestion.setText(currentQuestion);
        if ( isReadAnswer ) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        correctAnswers.setText(Integer.toString(0));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoaderAndUpdateDatabase(StudyWrongAnswersActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
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
        getLoaderManager().restartLoader(0, null, loader);
        showCurrentEntry.setOnCheckedChangeListener((buttonView, isChecked) -> {
            show_current_study_entry_card_view.startAnimation(animAlpha);
            if (isChecked) {
                enterQuestion.setText(currentQuestion);
                enterAnswer.setText(currentAnswer);
                enterQuestion.setEnabled(false);
                enterAnswer.setEnabled(false);
                check.setEnabled(false);
                questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                questionLinearlayout.setPadding(0, 0, 0, 0);
                answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                answerLinearlayout.setPadding(0, 0, 0, 0);
            } else {
                if (studyQuestion.isChecked()) {
                    enterQuestion.setText("");
                    enterQuestion.setEnabled(true);
                    questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
                } else {
                    enterQuestion.setText(currentQuestion);
                    enterQuestion.setEnabled(false);
                    questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
                }
                if (studyAnswer.isChecked()) {
                    enterAnswer.setText("");
                    enterAnswer.setEnabled(true);
                    answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);
                } else {
                    enterAnswer.setText(currentAnswer);
                    enterAnswer.setEnabled(false);
                    answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);
                }
            }
        });
        check.setOnClickListener(v -> {
            check_card_view.startAnimation(animAlpha);
            String entered_question = enterQuestion.getText().toString();
            String entered_answer = enterAnswer.getText().toString();
            if (studyQuestion.isChecked() && studyAnswer.isChecked()) {
                if ( check_type == Contract.Field.NON_STRICT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompareIgnoreCase(currentQuestion,entered_question) ) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( CompareUtility.nonStrictCompareIgnoreCase(currentAnswer,entered_answer)) {
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( CompareUtility.nonStrictCompareIgnoreCase(currentQuestion,entered_question) &&
                            CompareUtility.nonStrictCompareIgnoreCase(currentAnswer,entered_answer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                    }
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompare(currentQuestion,entered_question) ) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( CompareUtility.nonStrictCompare(currentAnswer,entered_answer)) {
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( CompareUtility.nonStrictCompare(currentQuestion,entered_question) &&
                            CompareUtility.nonStrictCompare(currentAnswer,entered_answer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                    }
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.IGNORE_CASE ) {
                    if ( entered_question.equalsIgnoreCase(currentQuestion) ) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( entered_answer.equalsIgnoreCase(currentAnswer) ) {
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( entered_question.equalsIgnoreCase(currentQuestion) && entered_answer.equalsIgnoreCase(currentAnswer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                    }
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.DO_NOT_IGNORE_CASE ) {
                    if ( entered_question.equals(currentQuestion) ) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( entered_answer.equals(currentAnswer) ) {
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( entered_question.equals(currentQuestion) && entered_answer.equals(currentAnswer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                    }
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                }
            } else if (studyQuestion.isChecked()) {
                if ( check_type == Contract.Field.NON_STRICT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompareIgnoreCase(currentQuestion,entered_question)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterQuestion.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompare(currentQuestion,entered_question)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterQuestion.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.IGNORE_CASE ) {
                    if ( entered_question.equalsIgnoreCase(currentQuestion) ) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterQuestion.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.DO_NOT_IGNORE_CASE ) {
                    if ( entered_question.equals(currentQuestion) ) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterQuestion.setText("");
                    mRunnable = () -> {
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                }
            } else if (studyAnswer.isChecked()) {
                if ( check_type == Contract.Field.NON_STRICT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompareIgnoreCase(currentAnswer,entered_answer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE ) {
                    if ( CompareUtility.nonStrictCompare(currentAnswer,entered_answer)) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.IGNORE_CASE ) {
                    if ( entered_answer.equalsIgnoreCase(currentAnswer)) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if ( check_type == Contract.Field.DO_NOT_IGNORE_CASE ) {
                    if ( entered_answer.equals(currentAnswer) ) {
                        if ( locale_available ) {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null,null);
                            } else {
                                tts.speak(currentAnswer,TextToSpeech.QUEUE_ADD,null);
                            }
                        }
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = () -> {
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                }
            }
        });
        studyQuestion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            correctAnswersCounter = 0;
            correctAnswers.setText(Integer.toString(correctAnswersCounter));
            if ( ! showCurrentEntry.isChecked() ) {
                if (isChecked) {
                    enterQuestion.setEnabled(true);
                    enterQuestion.setText("");
                    questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
                } else {
                    if (studyAnswer.isChecked()) {
                        enterQuestion.setEnabled(false);
                        enterQuestion.setText(currentQuestion);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        studyQuestion.setChecked(true);
                    }
                }
            } else {
                if (! isChecked && ! studyAnswer.isChecked() ) {
                    studyQuestion.setChecked(true);
                }
            }
        });
        studyAnswer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            correctAnswersCounter = 0;
            correctAnswers.setText(Integer.toString(correctAnswersCounter));
            if ( ! showCurrentEntry.isChecked() ) {
                if (isChecked) {
                    enterAnswer.setEnabled(true);
                    enterAnswer.setText("");
                    answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);
                } else {
                    if ( studyQuestion.isChecked() ) {
                        enterAnswer.setEnabled(false);
                        enterAnswer.setText(currentAnswer);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        studyAnswer.setChecked(true);
                    }
                }
            } else {
                if (! isChecked && ! studyQuestion.isChecked() ) {
                    studyAnswer.setChecked(true);
                }
            }
        });
        up.setOnClickListener(v -> {
            up_card_view.startAnimation(animAlpha);
            for (int i = 0; i < wrongItemsList.size(); i++) {
                if (wrongItemsList.get(i).getQuestion().equals(currentQuestion) &&
                        wrongItemsList.get(i).getAnswer().equals(currentAnswer)) {
                    if (i >= 1) {
                        currentQuestion = wrongItemsList.get(i - 1).getQuestion();
                        currentAnswer = wrongItemsList.get(i - 1).getAnswer();
                        correctAnswersCounter = 0;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
//                    showCurrentEntry.setChecked(true);
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                            enterQuestion.setEnabled(false);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                            enterAnswer.setEnabled(false);
                        }
                    } else {
                        Toast toast = Toast.makeText(this, R.string.first_entry, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    if (showCurrentEntry.isChecked()) {
                        enterQuestion.setText(currentQuestion);
                        enterAnswer.setText(currentAnswer);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    break;
                }
            }
        });
        down.setOnClickListener(v -> {
            down_card_view.startAnimation(animAlpha);
            for ( int i = 0; i < wrongItemsList.size(); i++ ) {
                if ( wrongItemsList.get(i).getQuestion().equals(currentQuestion) &&
                        wrongItemsList.get(i).getAnswer().equals(currentAnswer) ) {
                    if ( i < wrongItemsList.size() - 1 ) {
                        currentQuestion = wrongItemsList.get(i+1).getQuestion();
                        currentAnswer = wrongItemsList.get(i+1).getAnswer();
                        correctAnswersCounter = 0;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                            enterQuestion.setEnabled(false);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                            enterAnswer.setEnabled(false);
                        }
                    } else {
                        Toast toast = Toast.makeText(this, R.string.last_entry, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    if ( showCurrentEntry.isChecked() ) {
                        enterQuestion.setText(currentQuestion);
                        enterAnswer.setText(currentAnswer);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    break;
                }
            }
        });
        enterQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
                }
                if ( s.length() > 0 && enterAnswer.getText().length() > 0 ) {
                    if ( ! showCurrentEntry.isChecked() ) {
                        check.setEnabled(true);
                    }
                } else{
                    check.setEnabled(false);
                }
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = s.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enterQuestion.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enterQuestion.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                            questionLinearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                                questionLinearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
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
                if (s.length() > 0) {
                    answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);
                }
                if ( s.length() > 0 && enterQuestion.getText().length() > 0 ) {
                    if ( ! showCurrentEntry.isChecked() ) {
                        check.setEnabled(true);
                    }
                }else {
                    check.setEnabled(false);
                }
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = s.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enterAnswer.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enterAnswer.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                            questionLinearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                                questionLinearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
                }
            }
        });


        showActionbar.setOnClickListener(v -> {
            show_actionbar_card_view.startAnimation(animAlpha);
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
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_worng_answers_activity_menu, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                bundle.putFloat(QUESTION_LAYOUT,((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT,((LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (enterQuestion.getTextSize()/scaledDensity) );
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
//        searchFor.setText("");
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString(CURRENT_FIELD, currentField);
        savedInstanceState.putString(CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putString(CURRENT_ANSWER, currentAnswer);
        savedInstanceState.putInt(CORRECT_ANSWERS_COUNTER, correctAnswersCounter);
        savedInstanceState.putLong(TIMER_LAST_PAUSE,lastPause);
        savedInstanceState.putStringArray(SYMBOL, symbol);
        savedInstanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        saveUserLayoutChanges(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.setBase( SystemClock.elapsedRealtime() - lastPause);
        timer.start();
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) + SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onPause() {
        lastPause = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
        super.onPause();
    }

    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
//        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
//        databaseOperations = new DatabaseReadOperations(getContentResolver());
//        currentField = savedInstanceState.getString(CURRENT_FIELD);
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestion = savedInstanceState.getString(CURRENT_QUESTION);
        currentAnswer = savedInstanceState.getString(CURRENT_ANSWER);
        correctAnswersCounter = savedInstanceState.getInt(CORRECT_ANSWERS_COUNTER);
        lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
        symbol = savedInstanceState.getStringArray(SYMBOL);
        correctAnswers.setText(Integer.toString(correctAnswersCounter));
        restoreUserLayoutChanges(savedInstanceState);
        if ( showCurrentEntry.isChecked() ) {
            enterQuestion.setText(currentQuestion);
            enterAnswer.setText(currentAnswer);
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
                mySQLiteCursorLoader.updateFieldReadAnswer(current_field_id, false);
                Toast toast = Toast.makeText(this, R.string.tts_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        }
    }

    @Override
    public void changeAreas(float questionArea, float answerArea) {
        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        questionLinearlayout.setLayoutParams(question_params);
        answerLinearlayout.setLayoutParams(answer_params);
    }

    @Override
    public void changeTextSize(float textSize) {
        enterQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, enterQuestion.getTextSize());
    }
    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT),bundle.getFloat(ANSWER_LAYOUT));
        enterQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX,bundle.getFloat(TEXT_SIZE));
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }
    private long getEntry_id( String question, String answer ) {
        long entry_id = 0;
        for ( WrongItem wrongItem : wrongItemsList ) {
            if ( wrongItem.getQuestion().equals(question) && wrongItem.getAnswer().equals(answer) ) {
                entry_id = wrongItem.getEntry_id();
                break;
            }
        }
        return entry_id;
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

    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {

    }
}
