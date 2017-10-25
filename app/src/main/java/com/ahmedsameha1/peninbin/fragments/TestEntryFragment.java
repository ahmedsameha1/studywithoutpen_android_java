package com.ahmedsameha1.peninbin.fragments;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.CompareUtility;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 7/6/17.
 */

public class TestEntryFragment extends Fragment implements  TextToSpeech.OnInitListener ,
    StudyActivity.Contact {
    public static final String SCREEN_NAME = "TestEntryFragment";
    public static final String CURRENT_FIELD_ID = "currentField";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int RETURN_TIME = 1000;
    private static final String ENTRIES = "entries";
    private static final String RETAINED_FRAGMENT = "retained_fragment";
    private String[][] entries;
    private StringBuilder current_index;
    private String currentQuestion, currentAnswer;
    private TextView correctAnswers, check, show_question;
    private EditText enterAnswer;
    private int correctAnswersCounter = 0;
    private long current_field_id;
    private LinearLayout answerLinearlayout;
    private LoaderManager.LoaderCallbacks<Cursor> loader;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private DatabaseReadOperations databaseOperations;
    private TextToSpeech tts;
    private boolean tts_available = false;
    private boolean locale_available = false;
    private int check_type;
    private View root_view;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private Tracker mTracker;
    private RetainedFragment retainedFragment;


    public static TestEntryFragment newInstance(String[][] entries, StringBuilder current_index, long current_field_id) {
        TestEntryFragment instance = new TestEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(StudyActivity.DATA, entries);
        bundle.putSerializable(StudyActivity.CURRENT_INDEX, current_index);
        bundle.putLong(CURRENT_FIELD_ID, current_field_id);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(RETAINED_FRAGMENT);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, RETAINED_FRAGMENT).commit();
        }
        if ( savedInstanceState != null ) {
            entries = retainedFragment.getListForTestTestEntryFragment();
        } else {
            entries = (String[][]) getArguments().getSerializable(StudyActivity.DATA);
        }
        current_index = (StringBuilder) getArguments().getSerializable(StudyActivity.CURRENT_INDEX);
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
        boolean isReadAnswer;
        databaseOperations = new DatabaseReadOperations(getActivity());
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(getActivity());
        isReadAnswer = databaseOperations.isReadAnswer(current_field_id);
        check_type = databaseOperations.getFieldCheckType(current_field_id);
        show_question = (TextView) root_view.findViewById(R.id.question);
        enterAnswer = (EditText) root_view.findViewById(R.id.answer);
        correctAnswers = (TextView) root_view.findViewById(R.id.correct_answers);
        check = (TextView) root_view.findViewById(R.id.check);
        answerLinearlayout = (LinearLayout) root_view.findViewById(R.id.answer_linearlayout);
        ImageButton up = (ImageButton) root_view.findViewById(R.id.up);
        final ImageButton down = (ImageButton) root_view.findViewById(R.id.down);
        final CardView check_card_view = (CardView) root_view.findViewById(R.id.check_card_view);
        final CardView up_card_view = (CardView) root_view.findViewById(R.id.up_card_view);
        final CardView down_card_view = (CardView) root_view.findViewById(R.id.down_card_view);
        final Animation animAlpha = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_alpha);
        try {
            if (isReadAnswer) {
                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
            }
        } catch (ActivityNotFoundException e) {

        }
        if ( savedInstanceState != null ) {
            correctAnswersCounter = savedInstanceState.getInt(CORRECT_ANSWERS_COUNTER);
        }
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(getActivity()) + SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        correctAnswers.setText(String.valueOf(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                        }
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
                        correctAnswers.setText(String.valueOf(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                        }
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
                        correctAnswers.setText(String.valueOf(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                        }
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
                        correctAnswers.setText(String.valueOf(correctAnswersCounter));
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterAnswer.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                        }
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up_card_view.startAnimation(animAlpha);
                int position = Integer.parseInt(current_index.toString());
                if (position > 0) {
                    current_index.replace(0, current_index.length(), String.valueOf(--position));
                    currentQuestion = entries[position][0];
                    currentAnswer = entries[position][1];
                    correctAnswersCounter = 0;
                    correctAnswers.setText(String.valueOf(correctAnswersCounter));
                    show_question.setText(currentQuestion);
                    enterAnswer.setText("");
                } else {
                    Toast toast = Toast.makeText(TestEntryFragment.this.getActivity(), R.string.first_entry, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                down_card_view.startAnimation(animAlpha);
                int position = Integer.parseInt(current_index.toString());
                if (position < entries.length - 1) {
                    current_index.replace(0, current_index.length(), String.valueOf(++position));
                    currentQuestion = entries[position][0];
                    currentAnswer = entries[position][1];
                    correctAnswersCounter = 0;
                    correctAnswers.setText(String.valueOf(correctAnswersCounter));
                    show_question.setText(currentQuestion);
                    enterAnswer.setText("");
                } else {
                    Toast toast = Toast.makeText(TestEntryFragment.this.getActivity(), R.string.last_entry, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
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
                if (s.length() > 0 && show_question.getText().length() > 0) {
                        check.setEnabled(true);
                } else {
                    check.setEnabled(false);
                }
            }
        });
        show_question.setMovementMethod(new ScrollingMovementMethod());
        currentQuestion = entries[Integer.parseInt(current_index.toString())][0];
        currentAnswer = entries[Integer.parseInt(current_index.toString())][1];
        show_question.setText(currentQuestion);
        correctAnswers.setText(String.valueOf(correctAnswersCounter));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CORRECT_ANSWERS_COUNTER, correctAnswersCounter);
        retainedFragment.setListForTestTestEntryFragment(entries);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                tts = new TextToSpeech(getActivity().getApplicationContext(), this);
                tts_available = true;
            } else {
                cursorLoaderAndUpdateDatabase.updateFieldReadAnswer(current_field_id, false);
                Toast toast = Toast.makeText(getActivity(), R.string.tts_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        }
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
                Toast toast = Toast.makeText(getActivity(), R.string.locale_is_not_available, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        }
    }

    @Override
    public void changeCurrentTab() {
        if ( entries != null && current_index != null ) {
            int position = Integer.parseInt(current_index.toString());
            currentQuestion = entries[position][0];
            currentAnswer = entries[position][1];
            correctAnswersCounter = 0;
            correctAnswers.setText(String.valueOf(correctAnswersCounter));
            show_question.setText(currentQuestion);
            enterAnswer.setText("");
        }
    }
}
