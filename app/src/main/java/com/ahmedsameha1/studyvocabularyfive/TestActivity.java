package com.ahmedsameha1.studyvocabularyfive;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ahmed on 8/3/2015.
 */
public class TestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String CURRENT_FIELD = "currentField";
    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
    public static final String LIST_FOR_TEST = "listForTest";
    public static final String CURRENT_QUESTION_COUNTER = "currentQuestionCounter";
    public static final String TRIES_COUNTER = "triesCounter";
    public static final String NEXT_BUTTON_STATE = "newtButtonState";
    public static final String RESULT = "result";
    public static final String PERMITED_TRIES_NUMBER = "permitedTriesNumber";
    public static final String TESTING_NOW = "testingNow";
    public static final String NUMBER_OF_WRONG_ANSWERS = "numberOfWrongAnswers";
    public static final String NUMBER_OF_QUESTIONS = "numberOfQuestions";
    public static final String HINT_COUNTER = "hintCounter";
    public static final String HINTS = "hints";
    public static final String WRONG_ANSWERS = "wrongAnswers";
    public static final String WRONG_ITEMS = "wrongItems";
    private String currentField;
    private String [][] listForTest;
    private ArrayList<String> hints, wrongAnswers;
    private ArrayList<WrongItem> wrongItems;
    private boolean result, testingNow = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber,wrongAnswersCounter;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private Cursor fieldEntriesCursor, searchForCursor;
    private Toolbar toolbar;
    private EditText enterPercent, enterAnswer, numberOfTries;
    private TextView showQuestion, remainingQuestionCounter, hintIfMoreThanOneAnswer,showTries, showNumberOfQuetions;
    private Button start, next;
    private ImageView imageResult;
    private ImageButton nextHint;
    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private RetainedFragment retainedFragment; // to save the list for test array when the activity orientation change
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // getting the reference of the fragment that holds the reference of list for test array
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if ( retainedFragment == null ) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment,LIST_FOR_TEST).commit();
        }
        listForTest = retainedFragment.getListForTest();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        currentField = getIntent().getStringExtra(CURRENT_FIELD);
        enterPercent = (EditText) findViewById(R.id.enter_percent);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        start = (Button) findViewById(R.id.start);
        next = (Button) findViewById(R.id.next);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);
        imageResult = (ImageView) findViewById(R.id.image_result);
        hintIfMoreThanOneAnswer = (TextView) findViewById(R.id.hint_if_there_is_more_than_one_answer);
        showTries = (TextView) findViewById(R.id.trys);
        numberOfTries = (EditText) findViewById(R.id.enter_tries);
        nextHint = (ImageButton) findViewById(R.id.next_hint);
        showNumberOfQuetions = (TextView) findViewById(R.id.number_of_questions);
        setSupportActionBar(toolbar);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterPercent.getText().toString().equals("")
                        || Double.parseDouble(enterPercent.getText().toString()) <= 0 ||
                        Double.parseDouble(enterPercent.getText().toString()) > 100) {
                    Toast.makeText(TestActivity.this, getString(R.string.percentage_between), Toast.LENGTH_LONG).show();
                } else if ( Integer.parseInt(numberOfTries.getText().toString()) <= 0 ){
                    numberOfTries.setText(Integer.toString(1));
                    Toast.makeText(TestActivity.this, getString(R.string.tries_at_least), Toast.LENGTH_LONG).show();

                } else {
                    start.setEnabled(false);
                    permitedTriesNumber = Integer.parseInt(numberOfTries.getText().toString());
                    listForTest = getListForTest(Double.parseDouble(enterPercent.getText().toString()));
                    questionsNumber = listForTest.length;
                    showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                    enterAnswer.setEnabled(true);
                    enterAnswer.requestFocus();
                    next.setEnabled(true);
                    enterPercent.setText("");
                    currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                    triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                    showQuestion.setText(listForTest[currentQuestionCounter][0]);
                    ifMoreThanOnce();
                    showTries.setText(Integer.toString(triesCounter + 1));
                    remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1));
                    testingNow = true;
                    numberOfTries.setEnabled(false);
                    enterPercent.setEnabled(false);
                    wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                    wrongItems = new ArrayList<WrongItem>();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( enterAnswer.getText().toString().length() > 0 ) {
                    result = checkAnAnswer();
                    if ( triesCounter < permitedTriesNumber && !result) {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        showTries.setText(Integer.toString(triesCounter + 1));
                    }
                    else {
                        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                        currentQuestionCounter++;
                        if ( currentQuestionCounter >= questionsNumber) {
                            showQuestion.setText(getString(R.string.end_of_test));
                            next.setEnabled(false);
                            start.setEnabled(true);
                            numberOfTries.setEnabled(true);
                            enterPercent.setEnabled(true);
                            Toast.makeText(TestActivity.this,"Test Completed!",Toast.LENGTH_SHORT).show();
                            ShowResultDialogFragment fragment = new ShowResultDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(NUMBER_OF_QUESTIONS,questionsNumber);
                            bundle.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
                            bundle.putString(CURRENT_FIELD,currentField);
                            wrongItems.trimToSize();
                            bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItems);
                            fragment.setArguments(bundle);
                            fragment.show(getSupportFragmentManager(), ShowResultDialogFragment.SHOW_RESULT_DIALOG_FRAGMENT);
                            currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                            questionsNumber = 0;
                            wrongAnswersCounter = 0;
                            testingNow = false;
                            enterAnswer.setEnabled(false);
                            showNumberOfQuetions.setText("");
                            remainingQuestionCounter.setText("");
                            showTries.setText("");
                            hintIfMoreThanOneAnswer.setText("");
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            imageResult.setImageResource(R.drawable.ic_question_marks);
                        }
                        else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            showTries.setText(Integer.toString(triesCounter + 1));
                            ifMoreThanOnce();
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            hintIfMoreThanOneAnswer.setText("");
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        }
                    }
                } else {
                    Toast.makeText(TestActivity.this,R.string.where_the_answer,Toast.LENGTH_SHORT).show();
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
                    imageResult.setImageResource(R.drawable.ic_question_marks);
                }
            }
        });
        nextHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( hints != null ) {
                    hintIfMoreThanOneAnswer.setText(hints.get(hintCounter++));
                    hintCounter = hintCounter > (hints.size() - 1)  ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
            }
        });
        getSupportLoaderManager().initLoader(0,null,this);
    }
    @Override
    public void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getContentResolver());
//        fieldEntriesCursor = databaseOperations.getFieldEntries(currentField);
    }

    private String [][] getListForTest ( double percent ) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][2];
        for (int i = 0; i < listForTest.length; i++) {
            Arrays.fill(listForTest[i], "");
        }
        int counter = 0;
        while (counter < numberOfQuestions) {
            String question = null;
            String answer = null;
            boolean newPair = false;
            int random = (int) (Math.random() * fieldEntriesCursor.getCount());
            while (! newPair) {
                for (int i = 0; i < listForTest.length; i++) {
                    fieldEntriesCursor.moveToPosition(random);
                    question = fieldEntriesCursor.getString(1);
                    answer = fieldEntriesCursor.getString(2);
                    if ( listForTest[i][0].equals(question) && listForTest[i][1].equals(answer) ) {
                        newPair = false;
                        random = (int) (Math.random() * fieldEntriesCursor.getCount());
                        break;
                    }
                    newPair = true;
                }
            }
            listForTest[counter][0] = question;
            listForTest[counter][1] = answer;
            counter++;
        }
        return listForTest;
    }
    private void ifMoreThanOnce() {
        if ( searchForCursor != null ) {
            searchForCursor.close();
        }
        searchForCursor = databaseOperations.searchFor(currentField, listForTest[currentQuestionCounter][0]);
        if ( searchForCursor.getCount() > 1 ) {
            hints = new ArrayList<String>(3);
            hints.add( getString(R.string.first_character) + listForTest[currentQuestionCounter][1].charAt(0));
            hints.add( getString(R.string.number_of_characters) + Integer.toString(listForTest[currentQuestionCounter][1].length()));
            hints.add(getString(R.string.last_character) + listForTest[currentQuestionCounter][1].charAt(listForTest[currentQuestionCounter][1].length() - 1));
        }
        else {
            hints = null;
        }
        searchForCursor.close();
    }
    private boolean checkAnAnswer() {
        boolean result = false;
        if ( enterAnswer.getText().toString().equals(listForTest [currentQuestionCounter][1]) ){
            imageResult.setImageResource(R.drawable.ic_correct_sign);
            Toast.makeText(TestActivity.this,"Correct",Toast.LENGTH_SHORT).show();
            result = true;
        }
        else {
            imageResult.setImageResource(R.drawable.ic_wrong_sign);
            Toast.makeText(TestActivity.this,"Wrong",Toast.LENGTH_SHORT).show();
            result = false;
            wrongAnswers.add(enterAnswer.getText().toString());
        }
        enterAnswer.setText("");
        triesCounter++;
        if ( triesCounter == permitedTriesNumber && !result ) {
            wrongAnswersCounter++;
            WrongItem wrongItem = new WrongItem(listForTest [currentQuestionCounter][0],listForTest[currentQuestionCounter][1],wrongAnswers);
            wrongItems.add(wrongItem);
            wrongAnswers = null;
        }
        return result;
    }
    @Override
    public void onPause() {
        super.onPause();
//        databaseHelper.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fieldEntriesCursor.close();
        retainedFragment.setListForTest(listForTest);
//        sqLiteDatabase.close();
        databaseHelper.close();
    }

    @Override
    public void onDestroy() {
        fieldEntriesCursor.close();
        retainedFragment.setListForTest(listForTest);
//        sqLiteDatabase.close();
        databaseHelper.close();
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInsatanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInsatanceState.putBoolean(RESULT, result);
        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInsatanceState.putBoolean(NEXT_BUTTON_STATE, next.isEnabled());
        savedInsatanceState.putBoolean(TESTING_NOW, testingNow);
        savedInsatanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInsatanceState.putInt(HINT_COUNTER,hintCounter);
        savedInsatanceState.putStringArrayList(HINTS, hints);
        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
        savedInsatanceState.putSerializable(WRONG_ITEMS,wrongItems);
        super.onSaveInstanceState(savedInsatanceState);
    }
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean nextState = savedInstanceState.getBoolean(NEXT_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
        wrongItems = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEMS);
        if ( testingNow ) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            showTries.setText(Integer.toString(triesCounter + 1));
            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            showNumberOfQuetions.setText(Integer.toString(questionsNumber));
            next.setEnabled(nextState);
            enterPercent.setEnabled(false);
            numberOfTries.setEnabled(false);
            enterAnswer.setEnabled(true);
        }
        if ( hintCounter > 0 ) {
            hintIfMoreThanOneAnswer.setText(hints.get(hintCounter - 1));
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return new CursorLoader(this,
                uri,
                new String[] {"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {currentField},
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fieldEntriesCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fieldEntriesCursor = null;
    }

}
