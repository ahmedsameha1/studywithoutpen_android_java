package com.ahmedsameha1.studyvocabulary.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ShowResultDialogFragment;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.fragments.RetainedFragment;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ahmed on 8/3/2015.
 */
public class TestActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String ACTIVITY_NAME = "TestActivity";
    public static final String CURRENT_FIELD = "currentField";
    public static final String PERCENTAGE_OR_NUMBER = "percentage_or_number";
    public static final String QUESTION_PERCENTAGE = "question_percentage";
    public static final String QUESTION_NUMBER = "question_number";
    public static final int PERCENTAGE = 0;
    public static final int NUMBER = 1;
    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
    public static final int CONTINUE_PREVIOUS_SESSION = 1;
    public static final int RETURN_TIME = 1000;
    public static final String DASH = "-";
    public static final int START_NEW_SESSION = 2;
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
    public static final String LAST_SESSION_OR_NEW_ONE = "lastSessionOrNewOne";
    public static final String BOOLEAN_CONTINUE = "booleanContinue";
    private static final String QUESTION_LAYOUT = "user_defined_question_layout";
    private static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    private static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String PAUSING = "pausing";
    private long current_field_id;
    private String [][] listForTest;
    private ArrayList<String> hints, wrongAnswers;
    private boolean boolean_continue = false, actionBarShown;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
//    private ArrayList<WrongItem> wrongItems;
    private boolean result, testingNow = false;
    private int triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER, permitedTriesNumber, questionsNumber,wrongAnswersCounter , percent_or_number,number;
    private int currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER, hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
    private double percentage;
    private Cursor fieldEntriesCursor, searchForCursor;
    private Toolbar toolbar;
    private EditText enterAnswer;
    private TextView showQuestion, remainingQuestionCounter,showTries, showNumberOfQuestions,check,nextHint,showActionbar;
    private LinearLayout showResult,questionLinearlayout, container;
    private Handler mHandler = new Handler();
    private Runnable mRunnable, pauseRunnable;
    private Thread pauseThread = new Thread(new Runnable() {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                toPause();
            } finally {
                pauseRunnable = new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(ACTIVITY_NAME + PAUSING + current_field_id);
                        editor.commit();
                        Toast toast = Toast.makeText(TestActivity.this, R.string.pause_shutdown, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                };
                mHandler.post(pauseRunnable);
            }
        }
    });

//    private TableRow hintTableRow;
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
        databaseOperations = new DatabaseOperations(this);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        listForTest = retainedFragment.getListForTest_TestActivity();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,0);
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        permitedTriesNumber = getIntent().getIntExtra(PERMITED_TRIES_NUMBER, 1);
        percent_or_number = getIntent().getIntExtra(PERCENTAGE_OR_NUMBER, PERCENTAGE);
        number = getIntent().getIntExtra(QUESTION_NUMBER, 0);
        percentage = getIntent().getDoubleExtra(QUESTION_PERCENTAGE, 0);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        enterAnswer = (EditText) findViewById(R.id.enter_answer);
        showQuestion = (TextView) findViewById(R.id.display_question);
        check = (TextView) findViewById(R.id.check);
//        start = (Button) findViewById(R.id.start);
//        next = (Button) findViewById(R.id.next);
        remainingQuestionCounter = (TextView) findViewById(R.id.remaining_question_counter);


        showTries = (TextView) findViewById(R.id.trys);

        nextHint = (TextView) findViewById(R.id.next_hint);
        showNumberOfQuestions = (TextView) findViewById(R.id.number_of_questions);
//        hintTableRow = (TableRow) findViewById(R.id.hint_table_row);
        showResult = (LinearLayout) findViewById(R.id.result);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        setSupportActionBar(toolbar);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (  screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
                || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show );
        /*start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( fieldEntriesCursor != null && fieldEntriesCursor.getCount() > 0 ) {
                    int j = 0;
                    try {
                        j = Integer.parseInt(numberOfTries.getText().toString());
                    } catch ( NumberFormatException nfe ) {
                        numberOfTries.setText(Integer.toString(1));
                    }
                    if (enterPercent.getText().toString().equals("")
                            || Double.parseDouble(enterPercent.getText().toString()) <= 0 ||
                            Double.parseDouble(enterPercent.getText().toString()) > 100) {
                        Toast.makeText(TestActivity.this, getString(R.string.percentage_between), Toast.LENGTH_LONG).show();
                    } else if (j <= 0) {
                        numberOfTries.setText(Integer.toString(1));
                        Toast.makeText(TestActivity.this, getString(R.string.tries_at_least), Toast.LENGTH_LONG).show();

                    } else {
                        start.setEnabled(false);
                        permitedTriesNumber = Integer.parseInt(numberOfTries.getText().toString());
                        listForTest = getListForTest(Double.parseDouble(enterPercent.getText().toString()));
                        questionsNumber = listForTest.length;
                        showNumberOfQuestions.setText(Integer.toString(questionsNumber));
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
    //                    wrongItems = new ArrayList<WrongItem>();
                    }
                } else {
                    Toast.makeText(TestActivity.this,R.string.wait_for_data_loading,Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enterAnswer.getText().toString().length() > 0) {
                    result = checkAnAnswer();
                    if (triesCounter < permitedTriesNumber && !result) {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
                        showTries.setText(Integer.toString(triesCounter + 1));
                    } else {
                        triesCounter = INITIAL_VALUE_OF_TRIES_COUNTER;
                        currentQuestionCounter++;
                        if (currentQuestionCounter >= questionsNumber) {
                            showQuestion.setText(R.string.test_has_completed);
                            enterAnswer.setText(R.string.test_has_completed);
                            check.setEnabled(false);
//                            start.setEnabled(true);


                            ShowResultDialogFragment fragment = new ShowResultDialogFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
                            bundle.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
                            bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
//                            wrongItems.trimToSize();
//                            bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItems);
                            fragment.setArguments(bundle);
                            fragment.show(getSupportFragmentManager(), ShowResultDialogFragment.SHOW_RESULT_DIALOG_FRAGMENT);
                            currentQuestionCounter = INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER;
                            questionsNumber = 0;
                            wrongAnswersCounter = 0;
                            testingNow = false;
                            enterAnswer.setEnabled(false);
                            showNumberOfQuestions.setText("");
                            remainingQuestionCounter.setText("");
                            showTries.setText("");
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            showResult.setBackgroundResource(R.drawable.enter_answer_background);
                            showResult.setPadding(0,0,0,0);
//                            getSupportLoaderManager().restartLoader(START_NEW_SESSION, null, TestActivity.this);
                            mySQLiteCursorLoader.resetEntriesTestOrder(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                            mySQLiteCursorLoader.clearThisFieldFromTestPaused(current_field_id);
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            showQuestion.scrollTo(0,0);
                            showTries.setText(Integer.toString(triesCounter + 1));
                            ifMoreThanOnce();
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(TestActivity.this, R.string.where_the_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                enterAnswer.setHint(R.string.enter_your_answer);
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
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
            }
        });
        nextHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hints != null) {
                    if (enterAnswer.getText().toString().length() <= 0) {
                        enterAnswer.setHint(hints.get(hintCounter++));
                    } else {
                        Toast toast = Toast.makeText(TestActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                    hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
            }
        });
        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( actionBarShown ) {
                    getSupportActionBar().hide();
                    actionBarShown = (! actionBarShown);
                    showActionbar.setText(R.string.show);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (! actionBarShown);
                    showActionbar.setText(R.string.hide);
                }
            }
        });
        showNumberOfQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(TestActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuestions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        remainingQuestionCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(TestActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        showTries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(TestActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        if ( boolean_continue ) {
            getLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION,null,this);
            int[] array = databaseOperations.getDataFromTestPaused(current_field_id);
            currentQuestionCounter = array[0];
            wrongAnswersCounter = array[1];
            permitedTriesNumber = array[2];
        } else {
            getLoaderManager().initLoader(START_NEW_SESSION,null,this);
        }


        Log.d("fff", "onCreate()");
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
            case R.id.pause:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ACTIVITY_NAME + PAUSING + current_field_id, true);
                editor.commit();
                Toast toast = Toast.makeText(TestActivity.this, R.string.pause_do_not_shutdown, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                pauseThread.start();
                finish();
                return true;
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putFloat(QUESTION_LAYOUT,((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT,((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (showQuestion.getTextSize()/scaledDensity) );
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void toPause() {
        if ( ! databaseOperations.checkEntriesForAnyTestOrder(current_field_id,databaseOperations.getQuestionDataType(current_field_id)) ) {
            mySQLiteCursorLoader.putEntriesTestOrder(current_field_id,
                    databaseOperations.getQuestionDataType(current_field_id),listForTest);
        }
        mySQLiteCursorLoader.clearThisFieldFromTestPaused(current_field_id);
        int[] array = new int[3];
        array[0] = currentQuestionCounter;
        array[1] = wrongAnswersCounter;
        array[2] = permitedTriesNumber;
        mySQLiteCursorLoader.putTestPausedData(current_field_id,array);
    }
/*    private String [][] getListForTest ( double percent ) {
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
    }*/
    private String [][] getListForTest ( double percent ) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][2];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            listOfEntries [fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries [fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
        }
        shuffleArray(listOfEntries);
        for ( int n = 0; n < listForTest.length; n++ ) {
            listForTest [n][0] = listOfEntries [n][0];
            listForTest [n][1] = listOfEntries [n][1];
        }
        return listForTest;
    }
    private String [][] getListForTest ( int numberOfQuestions ) {
        String[][] listForTest = new String[numberOfQuestions][2];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            listOfEntries [fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries [fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
        }
        shuffleArray(listOfEntries);
        for ( int n = 0; n < listForTest.length; n++ ) {
            listForTest [n][0] = listOfEntries [n][0];
            listForTest [n][1] = listOfEntries [n][1];
        }
        return listForTest;
    }
    private void shuffleArray( String [][] array )
    {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = array[index][0];
            String answer = array [index][1];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[i][0] = question;
            array[i][1] = answer;
        }
    }
    private void ifMoreThanOnce() {
        if ( searchForCursor != null ) {
            searchForCursor.close();
        }
        searchForCursor = databaseOperations.searchExactlyFor(current_field_id,
                databaseOperations.getQuestionDataType(current_field_id), listForTest[currentQuestionCounter][0]);
        if ( searchForCursor.getCount() > 1 ) {
            hints = new ArrayList<String>(3);
            hints.add( getString(R.string.first_character) + listForTest[currentQuestionCounter][1].charAt(0));
            hints.add( getString(R.string.number_of_characters) + Integer.toString(listForTest[currentQuestionCounter][1].length()));
            hints.add(getString(R.string.last_character) + listForTest[currentQuestionCounter][1]
                    .charAt(listForTest[currentQuestionCounter][1].length() - 1));
            nextHint.setEnabled(true);
//            hintTableRow.setBackgroundResource(R.color.enabled_color);
        }
        else {
            hints = null;
            nextHint.setEnabled(false);
//            hintTableRow.setBackgroundResource(R.color.default_background_color);
        }
        searchForCursor.close();
    }
    private boolean checkAnAnswer() {
        boolean result = false;
        if ( enterAnswer.getText().toString().equals(listForTest [currentQuestionCounter][1]) ){
            showResult.setBackgroundResource(R.drawable.correct_answer_background);
            showResult.setPadding(0,0,0,0);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
            };
            mHandler.postDelayed(mRunnable, RETURN_TIME);
            result = true;
        }
        else {
            showResult.setBackgroundResource(R.drawable.wrong_answer_background);
            showResult.setPadding(0,0,0,0);
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
            };
            mHandler.postDelayed(mRunnable, RETURN_TIME);
            result = false;
            wrongAnswers.add(enterAnswer.getText().toString());
        }
        enterAnswer.setText("");
        triesCounter++;
        if ( triesCounter == permitedTriesNumber && !result ) {
            wrongAnswersCounter++;
            mySQLiteCursorLoader.putEntryWrongAnswers(listForTest [currentQuestionCounter][0],
                    listForTest[currentQuestionCounter][1],current_field_id,wrongAnswers,databaseOperations.getQuestionDataType(current_field_id));
            wrongAnswers = null;
        }
        return result;
    }
    private String convertWrongAnswersArrayListToString( ArrayList<String> arrayList ) {
        String s = "";
        for ( int i = 0; i < arrayList.size(); i++ ) {
            s += arrayList.get(i);
            if ( i < arrayList.size() - 1) {
                s += "\t";
            }
        }
        return s;
    }
    private String [][] readTestDataFromLinkerTable() {
        String [][] listForTest = new String[fieldEntriesCursor.getCount()][2];
        for ( fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
              fieldEntriesCursor.moveToNext() ) {
            int order = fieldEntriesCursor.getInt(2);
            listForTest [order][0] = fieldEntriesCursor.getString(0);
            listForTest [order][1] = fieldEntriesCursor.getString(1);
        }
        return listForTest;
    }
    @Override
    protected void onStop() {
        super.onStop();
        retainedFragment.setListForTest_TestActivity(listForTest);
//        sqLiteDatabase.close();
    }

    @Override
    public void onDestroy() {
        retainedFragment.setListForTest_TestActivity(listForTest);
//        sqLiteDatabase.close();
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER,currentQuestionCounter);
        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS,wrongAnswersCounter);
        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER,permitedTriesNumber);
//        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInsatanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInsatanceState.putBoolean(RESULT, result);
//        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInsatanceState.putBoolean(NEXT_BUTTON_STATE, check.isEnabled());
        savedInsatanceState.putBoolean(TESTING_NOW, testingNow);
        savedInsatanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
//        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInsatanceState.putInt(HINT_COUNTER, hintCounter);
        savedInsatanceState.putStringArrayList(HINTS, hints);
        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
//        savedInsatanceState.putSerializable(WRONG_ITEMS,wrongItems);
        savedInsatanceState.putBoolean(BOOLEAN_CONTINUE,boolean_continue);
        saveUserLayoutChanges(savedInsatanceState);
       /* if ( ! isFinishing() ) {
            pauseThread.start();
        }*/
        super.onSaveInstanceState(savedInsatanceState);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
//        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
//        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean nextState = savedInstanceState.getBoolean(NEXT_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
//        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
//        wrongItems = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEMS);
        boolean_continue = savedInstanceState.getBoolean(BOOLEAN_CONTINUE);
        restoreUserLayoutChanges(savedInstanceState);
        if ( testingNow ) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            showTries.setText(Integer.toString(triesCounter + 1));
            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            showNumberOfQuestions.setText(Integer.toString(questionsNumber));
//            start.setEnabled(false);
            check.setEnabled(nextState);


            enterAnswer.setEnabled(true);
            ifMoreThanOnce();
        }
        if ( hintCounter > 0 ) {
//            hintIfMoreThanOneAnswer.setText(hints.get(hintCounter - 1));
            if ( enterAnswer.getText().toString().length() <= 0 ) {
                enterAnswer.setHint(hints.get(hintCounter - 1));
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
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( id == CONTINUE_PREVIOUS_SESSION ) {
            return new MySQLiteCursorLoader(TestActivity.this,
                    "SELECT " + " s1." + Contract.STring.COLUMN_NAME_VALUE + " q , s2." + Contract.STring.COLUMN_NAME_VALUE
                            + " a , se." + Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER + " FROM " + Contract.STring.TABLE_NAME
                            + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_NAME_PAUSED_TEST_ORDER + " >= ? ",
                    new String[]{current_field_id + "", "0"});
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,uri,
                    new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK,
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[] {"0", currentField},
                    null);*/
        } else {
            return new MySQLiteCursorLoader(TestActivity.this,
                    "SELECT " + "se." + Contract.COLUMN_NAME_ID + ", s1." + Contract.STring.COLUMN_NAME_VALUE + " q , s2." + Contract.STring.COLUMN_NAME_VALUE
                            + " a " + " FROM " + Contract.STring.TABLE_NAME
                            + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                    new String[]{current_field_id + ""});
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
            return new CursorLoader(this,
                    uri,
                    new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{currentField},
                    null);*/
        }
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fieldEntriesCursor = data;
        if ( boolean_continue ) {
            if ( listForTest == null ) {
                listForTest = readTestDataFromLinkerTable();
                questionsNumber = listForTest.length;
                showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                showTries.setText(Integer.toString(triesCounter + 1));
                showQuestion.setText(listForTest[currentQuestionCounter][0]);
//                start.setEnabled(false);
                check.setEnabled(true);


                enterAnswer.setEnabled(true);
                enterAnswer.requestFocus();
                testingNow = true;
                wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                boolean_continue = false;
                ifMoreThanOnce();
                getLoaderManager().destroyLoader(CONTINUE_PREVIOUS_SESSION);
                getLoaderManager().initLoader(START_NEW_SESSION,null,this);
            }
        } else {
            if ( listForTest == null ) {
                if ( percent_or_number == PERCENTAGE ) {
                    listForTest = getListForTest(percentage);
                } else if ( percent_or_number == NUMBER ) {
                    listForTest = getListForTest(number);
                }
                questionsNumber = listForTest.length;
                showNumberOfQuestions.setText(Integer.toString(questionsNumber));
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                showTries.setText(Integer.toString(triesCounter + 1));
                showQuestion.setText(listForTest[currentQuestionCounter][0]);
                enterAnswer.requestFocus();
                 testingNow = true;
                wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                boolean_continue = false;
                ifMoreThanOnce();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fieldEntriesCursor = null;
    }

}
