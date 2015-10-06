package com.ahmedsameha1.studyvocabularyfive;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by ahmed_000 on 10/6/2015.
 */
public class StudyWrongAnswersActivity extends AppCompatActivity {
    public static final String WRONG_ITEM_LIST = "wrongItemList";
//    public static final String CURRENT_FIELD = "currentField";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String SEARCH_VALUE = "searchValue";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    private String currentQuestion, currentAnswer;
    private TextView viewQuestion, viewAnswer , correctAnswers;
    private Button checkAndClear;
    private ToggleButton showCurrentEntry;
    private CheckBox studyQuestion, studyAnswer;
    private EditText enterQuestion, enterAnswer;
    private SimpleCursorAdapter simpleCursorAdapter;
    private ImageButton  up , down;
    private ImageView resultImage;
    private int correctAnswersCounter = 0;
//    private DatabaseHelper databaseHelper;
    //    private SQLiteDatabase sqLiteDatabase;
//    private DatabaseOperations databaseOperations;
    private ArrayList<WrongItem> wrongItemsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_wrong_answers);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        wrongItemsList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
        currentQuestion = bundle.getString(CURRENT_QUESTION);
        currentAnswer = bundle.getString(CURRENT_ANSWER);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        viewQuestion = (TextView) findViewById(R.id.current_study_question);
        viewAnswer = (TextView) findViewById(R.id.current_study_answer);
        showCurrentEntry = (ToggleButton) findViewById(R.id.show_current_study_entry);
        studyQuestion = (CheckBox) findViewById(R.id.study_question);
        studyAnswer = (CheckBox) findViewById(R.id.study_answer);
        enterQuestion = (EditText) findViewById(R.id.question);
        enterAnswer = ( EditText) findViewById(R.id.answer);
        correctAnswers = ( TextView) findViewById(R.id.correct_answers);
        checkAndClear = (Button) findViewById(R.id.check_and_clear);
        resultImage = (ImageView) findViewById(R.id.result_image);
        up = (ImageButton) findViewById(R.id.up);
        down = (ImageButton) findViewById(R.id.down);
        setSupportActionBar(toolbar);
        enterQuestion.setText(currentQuestion);
        showCurrentEntry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    viewQuestion.setText(currentQuestion);
                    viewAnswer.setText(currentAnswer);
                    enterQuestion.setEnabled(false);
                    enterAnswer.setEnabled(false);
                    checkAndClear.setEnabled(false);
                    enterQuestion.setText("");
                    enterAnswer.setText("");
                } else {
                    viewQuestion.setText("");
                    viewAnswer.setText("");
                    enterQuestion.setEnabled(true);
                    enterAnswer.setEnabled(true);
                    checkAndClear.setEnabled(true);
                    if (studyQuestion.isChecked()) {
                        enterQuestion.setText("");
                    } else {
                        enterQuestion.setText(currentQuestion);
                    }
                    if (studyAnswer.isChecked()) {
                        enterAnswer.setText("");
                    } else {
                        enterAnswer.setText(currentAnswer);
                    }
                }
            }
        });

        studyQuestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                correctAnswersCounter = 0;
                correctAnswers.setText(Integer.toString(correctAnswersCounter));
                if (isChecked) {
                    enterQuestion.setEnabled(true);
                    enterQuestion.setText("");
                } else {
                    if (studyAnswer.isChecked()) {
                        enterQuestion.setEnabled(false);
                        enterQuestion.setText(currentQuestion);
                    } else {
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
                if (isChecked) {
                    enterAnswer.setEnabled(true);
                    enterAnswer.setText("");
                } else {
                    if (studyQuestion.isChecked()) {
                        enterAnswer.setEnabled(false);
                        enterAnswer.setText(currentAnswer);
                    } else {
                        studyAnswer.setChecked(true);
                    }
                }
            }
        });

        checkAndClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyQuestion.isChecked() && studyAnswer.isChecked()) {
                    if (enterQuestion.getText().toString().equals(currentQuestion) && enterAnswer.getText().toString().equals(currentAnswer)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_correct_sign);
                    } else {
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_wrong_sign);
                    }
                } else if (studyQuestion.isChecked()) {
                    if (enterQuestion.getText().toString().equals(currentQuestion)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_correct_sign);
                    } else {
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_wrong_sign);
                    }
                } else if (studyAnswer.isChecked()) {
                    if (enterAnswer.getText().toString().equals(currentAnswer)) {
                        correctAnswersCounter++;
                        correctAnswers.setText(Integer.toString(correctAnswersCounter));
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_correct_sign);
                    } else {
                        if (studyQuestion.isChecked()) {
                            enterQuestion.setText("");
                        } else {
                            enterQuestion.setText(currentQuestion);
                        }
                        if (studyAnswer.isChecked()) {
                            enterAnswer.setText("");
                        } else {
                            enterAnswer.setText(currentAnswer);
                        }
                        resultImage.setImageResource(R.drawable.ic_wrong_sign);
                    }
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for ( int i = 0; i < wrongItemsList.size(); i++ ) {
                    if ( wrongItemsList.get(i).getQuestion().equals(currentQuestion) &&
                            wrongItemsList.get(i).getAnswer().equals(currentAnswer) ) {
                        if ( i >= 1 ) {
                            currentQuestion = wrongItemsList.get(i-1).getQuestion();
                            currentAnswer = wrongItemsList.get(i-1).getAnswer();
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
                            viewQuestion.setText(currentQuestion);
                            viewAnswer.setText(currentAnswer);
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
                            viewQuestion.setText(currentQuestion);
                            viewAnswer.setText(currentAnswer);
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
                    resultImage.setImageResource(R.drawable.ic_question_marks);
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
                    resultImage.setImageResource(R.drawable.ic_question_marks);
                }
            }
        });

        correctAnswers.setText(Integer.toString(0));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
//        searchFor.setText("");
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString(CURRENT_FIELD, currentField);
        savedInstanceState.putString(CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putString(CURRENT_ANSWER, currentAnswer);
        savedInstanceState.putInt(CORRECT_ANSWERS_COUNTER, correctAnswersCounter);
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
        if ( showCurrentEntry.isChecked() ) {
            viewQuestion.setText(currentQuestion);
            viewAnswer.setText(currentAnswer);
        }
    }
}
