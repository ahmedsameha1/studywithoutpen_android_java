package com.ahmedsameha1.studyvocabularyfive.activitiestemp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmedsameha1.studyvocabularyfive.dialogstemp.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.dialogstemp.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.R;
import com.ahmedsameha1.studyvocabularyfive.WrongItem;

import java.util.ArrayList;

/**
 * Created by ahmed_000 on 10/6/2015.
 */
public class StudyWrongAnswersActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final int RETURN_TIME = 1000;
    private String currentQuestion, currentAnswer;
    private TextView correctAnswers,check,showActionbar;
    private ToggleButton showCurrentEntry;
    private CheckBox studyQuestion, studyAnswer;
    private EditText enterQuestion, enterAnswer;
    private ImageButton  up , down;
    private LinearLayout answerLinearlayout,questionLinearlayout, container;
    private int correctAnswersCounter = 0;
    private ArrayList<WrongItem> wrongItemsList;
    private boolean actionBarShown;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_wrong_answers);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        wrongItemsList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
        currentQuestion = bundle.getString(CURRENT_QUESTION);
        currentAnswer = bundle.getString(CURRENT_ANSWER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        showCurrentEntry = (ToggleButton) findViewById(R.id.show_current_study_entry);
        studyQuestion = (CheckBox) findViewById(R.id.study_question);
        studyAnswer = (CheckBox) findViewById(R.id.study_answer);
        enterQuestion = (EditText) findViewById(R.id.question);
        enterAnswer = ( EditText) findViewById(R.id.answer);
        correctAnswers = ( TextView) findViewById(R.id.correct_answers);
        check = (TextView) findViewById(R.id.check);
        up = (ImageButton) findViewById(R.id.up);
        down = (ImageButton) findViewById(R.id.down);
        answerLinearlayout = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        setSupportActionBar(toolbar);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if ( screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
                || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show );
        enterQuestion.setText(currentQuestion);
        showCurrentEntry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enterQuestion.setText(currentQuestion);
                    enterAnswer.setText(currentAnswer);
                    enterQuestion.setFocusable(false);
                    enterAnswer.setFocusable(false);
                    check.setEnabled(false);
                    questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
                    answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);
                } else {
                    check.setEnabled(true);
                    if (studyQuestion.isChecked()) {
                        enterQuestion.setText("");
                        enterQuestion.setFocusable(true);
                        enterQuestion.setFocusableInTouchMode(true);
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        enterQuestion.setText(currentQuestion);
                        enterQuestion.setFocusable(false);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if (studyAnswer.isChecked()) {
                        enterAnswer.setText("");
                        enterAnswer.setFocusable(true);
                        enterAnswer.setFocusableInTouchMode(true);
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        enterAnswer.setText(currentAnswer);
                        enterAnswer.setFocusable(false);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                }
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyQuestion.isChecked() && studyAnswer.isChecked()) {
                    if (enterQuestion.getText().toString().equals(currentQuestion)) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if (enterAnswer.getText().toString().equals(currentAnswer)) {
                        answerLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        answerLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if (enterQuestion.getText().toString().equals(currentQuestion) && enterAnswer.getText().toString().equals(currentAnswer)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                    }
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                            answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            answerLinearlayout.setPadding(0, 0, 0, 0);
                        }
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if (studyQuestion.isChecked()) {
                    if (enterQuestion.getText().toString().equals(currentQuestion)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    enterQuestion.setText("");
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                        }
                    };
                    mHandler.postDelayed(mRunnable, RETURN_TIME);
                } else if (studyAnswer.isChecked()) {
                    if (enterAnswer.getText().toString().equals(currentAnswer)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
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
        studyQuestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                correctAnswersCounter = 0;
                correctAnswers.setText(Integer.toString(correctAnswersCounter));
                if ( ! showCurrentEntry.isChecked() ) {
                    if (isChecked) {
                        enterQuestion.setFocusable(true);
                        enterQuestion.setFocusableInTouchMode(true);
                        enterQuestion.setText("");
                        questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        if (studyAnswer.isChecked()) {
                            enterQuestion.setFocusable(false);
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
            }
        });

        studyAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                correctAnswersCounter = 0;
                correctAnswers.setText(Integer.toString(correctAnswersCounter));
                if ( ! showCurrentEntry.isChecked() ) {
                    if (isChecked) {
                        enterAnswer.setFocusable(true);
                        enterAnswer.setFocusableInTouchMode(true);
                        enterAnswer.setText("");
                        answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        if ( studyQuestion.isChecked() ) {
                            enterAnswer.setFocusable(false);
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
            }
        });


        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                /*int position = 0;
*//*                if (fieldEntriesCursor != null) {
                    fieldEntriesCursor.close();
                }*//*
//                fieldEntriesCursor = databaseOperations.getFieldEntries(currentField);
                for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount(); fieldEntriesCursor.moveToNext()) {
                    if (fieldEntriesCursor.getString(1).equals(currentQuestion) && fieldEntriesCursor.getString(2).equals(currentAnswer)) {
                        position = fieldEntriesCursor.getPosition();
                    }
                }
                if (--position >= 0) {
                    fieldEntriesCursor.moveToPosition(position);
                    currentQuestion = fieldEntriesCursor.getString(1);
                    currentAnswer = fieldEntriesCursor.getString(2);
                    *//*viewQuestion.setText(currentQuestion);
                    viewAnswer.setText(currentAnswer);*//*
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
                }*/
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for ( int i = 0; i < wrongItemsList.size(); i++ ) {
                    if ( wrongItemsList.get(i).getQuestion().equals(currentQuestion) &&
                            wrongItemsList.get(i).getAnswer().equals(currentAnswer) ) {
                        if ( i < wrongItemsList.size() - 1 ) {
                            currentQuestion = wrongItemsList.get(i+1).getQuestion();
                            currentAnswer = wrongItemsList.get(i+1).getAnswer();
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
                /*int position = 0;
*//*                if (fieldEntriesCursor != null) {
                    fieldEntriesCursor.close();
                }*//*
//                fieldEntriesCursor = databaseOperations.getFieldEntries(currentField);
                for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount(); fieldEntriesCursor.moveToNext()) {
                    if (fieldEntriesCursor.getString(1).equals(currentQuestion) && fieldEntriesCursor.getString(2).equals(currentAnswer)) {
                        position = fieldEntriesCursor.getPosition();
                    }
                }
                if (++position < fieldEntriesCursor.getCount()) {
                    fieldEntriesCursor.moveToPosition(position);
                    currentQuestion = fieldEntriesCursor.getString(1);
                    currentAnswer = fieldEntriesCursor.getString(2);
                    *//*viewQuestion.setText(currentQuestion);
                    viewAnswer.setText(currentAnswer);*//*
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
                }*/
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
                if (s.toString().length() > 0) {
//                    resultImage.setImageResource(R.drawable.ic_question_marks);
                    questionLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    questionLinearlayout.setPadding(0, 0, 0, 0);
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
                if (s.toString().length() > 0) {
//                    resultImage.setImageResource(R.drawable.ic_question_marks);
                    answerLinearlayout.setBackgroundResource(R.drawable.enter_answer_background);
                    answerLinearlayout.setPadding(0, 0, 0, 0);

                }
            }
        });
        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionBarShown) {
                    getSupportActionBar().hide();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.show);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.hide);
                }
            }
        });
        correctAnswers.setText(Integer.toString(0));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_worng_answers_activity_menu, menu);
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
                bundle.putFloat(QUESTION_LAYOUT,((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT,((LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (enterQuestion.getTextSize()/scaledDensity) );
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.rate_the_app:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                return true;
            case R.id.open_facebook_page:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
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
        saveUserLayoutChanges(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
//        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
//        databaseOperations = new DatabaseOperations(getContentResolver());
//        currentField = savedInstanceState.getString(CURRENT_FIELD);
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestion = savedInstanceState.getString(CURRENT_QUESTION);
        currentAnswer = savedInstanceState.getString(CURRENT_ANSWER);
        correctAnswersCounter = savedInstanceState.getInt(CORRECT_ANSWERS_COUNTER);
        correctAnswers.setText(Integer.toString(correctAnswersCounter));
        restoreUserLayoutChanges(savedInstanceState);
        if ( showCurrentEntry.isChecked() ) {
            enterQuestion.setText(currentQuestion);
            enterAnswer.setText(currentAnswer);
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
}
