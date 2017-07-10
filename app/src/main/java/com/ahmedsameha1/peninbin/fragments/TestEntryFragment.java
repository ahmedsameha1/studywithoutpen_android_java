package com.ahmedsameha1.peninbin.fragments;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.NewStudyActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 7/6/17.
 */

public class TestEntryFragment extends Fragment {
    public static final String SCREEN_NAME = "StudyActivity";
    public static final String CURRENT_FIELD_ID = "currentField";
    public static final String ENTRY_ID = "entry_id";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int SELECT_ENTRY_REQUEST = 1;
    public static final int RETURN_TIME = 1000;
    private static final String CURRENT_ENTRY_ID = "current_entry_id";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int GET_SPECIAL_TEXT_LOADER_ID = 1;
    private String[][] entries;
    private StringBuilder current_index;
    private String currentQuestion, currentAnswer;
    private TextView correctAnswers, check, show_question;
    private EditText enterAnswer;
    private int correctAnswersCounter = 0;
    private long current_field_id, current_entry_id;
    private LinearLayout answerLinearlayout, questionLinearlayout, container;
    private Cursor fieldEntriesCursor;
    private LoaderManager.LoaderCallbacks<Cursor> loader;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private DatabaseReadOperations databaseOperations;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private int check_type;
    private Tracker mTracker;
    private Chronometer timer;
    private long lastPause;
    private String[] symbol, customable_text;
    private int current_field_type;
    private View root_view;


    public static TestEntryFragment newInstance(String[][] entries, StringBuilder current_index, long current_field_id) {
        TestEntryFragment instance = new TestEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewStudyActivity.DATA, entries);
        bundle.putSerializable(NewStudyActivity.CURRENT_INDEX, current_index);
        bundle.putLong(CURRENT_FIELD_ID, current_field_id);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entries = (String[][]) getArguments().getSerializable(NewStudyActivity.DATA);
        current_index = (StringBuilder) getArguments().getSerializable(NewStudyActivity.CURRENT_INDEX);
        current_field_id = getArguments().getLong(CURRENT_FIELD_ID, 0);
    }

  
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return root_view = inflater.inflate(R.layout.test_entry_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        boolean isReadAnswer;
        databaseOperations = new DatabaseReadOperations(getActivity());
        current_field_type = databaseOperations.getFieldType(current_field_id);
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        show_question = (TextView) root_view.findViewById(R.id.question);
        enterAnswer = (EditText) root_view.findViewById(R.id.answer);
        correctAnswers = (TextView) root_view.findViewById(R.id.correct_answers);
        check = (TextView) root_view.findViewById(R.id.check);
        answerLinearlayout = (LinearLayout) root_view.findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) root_view.findViewById(R.id.question_linearlayout);
        container = (LinearLayout) root_view.findViewById(R.id.container);
        ImageButton up = (ImageButton) root_view.findViewById(R.id.up);
        final ImageButton down = (ImageButton) root_view.findViewById(R.id.down);
        final CardView check_card_view = (CardView) root_view.findViewById(R.id.check_card_view);
        final CardView up_card_view = (CardView) root_view.findViewById(R.id.up_card_view);
        final CardView down_card_view = (CardView) root_view.findViewById(R.id.down_card_view);
        timer = (Chronometer) root_view.findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        if (isReadAnswer) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        }
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        check.setOnClickListener(v -> {
            check_card_view.startAnimation(animAlpha);
            String entered_answer = enterAnswer.getText().toString();


                if (check_type == Contract.Field.NON_STRICT_IGNORE_CASE) {
                    if (CompareUtility.nonStrictCompareIgnoreCase(currentAnswer, entered_answer)) {
                        if (locale_available) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null, null);
                            } else {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null);
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
                } else if (check_type == Contract.Field.NON_STRICT_DO_NOT_IGNORE_CASE) {
                    if (CompareUtility.nonStrictCompare(currentAnswer, entered_answer)) {
                        if (locale_available) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null, null);
                            } else {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null);
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
                } else if (check_type == Contract.Field.IGNORE_CASE) {
                    if (entered_answer.equalsIgnoreCase(currentAnswer)) {
                        if (locale_available) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null, null);
                            } else {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null);
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
                } else if (check_type == Contract.Field.DO_NOT_IGNORE_CASE) {
                    if (entered_answer.equals(currentAnswer)) {
                        if (locale_available) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null, null);
                            } else {
                                tts.speak(currentAnswer, TextToSpeech.QUEUE_ADD, null);
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
        });
        up.setOnClickListener(v -> {
            up_card_view.startAnimation(animAlpha);
            int position = Integer.parseInt(current_index.toString());
            if ( position > 0 ) {
                current_index.replace(0, current_index.length(), String.valueOf(--position));
                currentQuestion = entries[position][0];
                currentAnswer = entries[position][1];
                correctAnswersCounter = 0;
                correctAnswers.setText(Integer.toString(correctAnswersCounter));
                show_question.setText(currentQuestion);
                enterAnswer.setText("");
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.first_entry, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        down.setOnClickListener(v -> {
            down_card_view.startAnimation(animAlpha);
            int position = Integer.parseInt(current_index.toString());
            if ( position < entries.length - 1 ) {
                current_index.replace(0, current_index.length(), String.valueOf(++position));
                currentQuestion = entries[position][0];
                currentAnswer = entries[position][1];
                correctAnswersCounter = 0;
                correctAnswers.setText(Integer.toString(correctAnswersCounter));
                show_question.setText(currentQuestion);
                enterAnswer.setText("");
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.last_entry, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
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
//                    resultImage.setImageResource(R.drawable.ic_question_marks);
                    answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);

                }
                if (s.length() > 0 && show_question.getText().length() > 0) {
                        check.setEnabled(true);
                } else {
                    check.setEnabled(false);
                }
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = s.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enterAnswer.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enterAnswer.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                            answerLinearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                                answerLinearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
                }
            }
        });
        show_question.setMovementMethod(new ScrollingMovementMethod());
        currentQuestion = entries[Integer.parseInt(current_index.toString())][0];
        currentAnswer = entries[Integer.parseInt(current_index.toString())][1];
        show_question.setText(currentQuestion);
    }
    public void changeAreas(float questionArea, float answerArea) {
        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        questionLinearlayout.setLayoutParams(question_params);
        answerLinearlayout.setLayoutParams(answer_params);
    }
    public void changeTextSize(float textSize) {
        show_question.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
}
