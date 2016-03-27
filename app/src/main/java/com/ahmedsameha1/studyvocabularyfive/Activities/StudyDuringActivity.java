package com.ahmedsameha1.studyvocabularyfive.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by ahmed_000 on 9/23/2015.
 */
public class StudyDuringActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> ,
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String ACTIVITY_NAME = "StudyDuringActivity";
    public static final String CURRENT_FIELD = "currentField";
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
    public static final int PORTRAIT = 0;
    public static final int LANDSCAPE = 1;
    private String currentField;
    private long startingTime;
    boolean boolean_continue, actionBarShown;
    private Cursor cursor;
    private ArrayList<String> questions = new ArrayList<String>(), answers = new ArrayList<String>();
    private ArrayList<Integer> repeatedEntries = new ArrayList<Integer>();


    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////


    public static final int INITIAL_VALUE_OF_CURRENT_QUESTION_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_TRIES_COUNTER = 0;
    public static final int INITIAL_VALUE_OF_HINT_COUNTER = 0;
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
    private TextView showQuestion, remainingQuestionCounter, showTries, showNumberOfQuetions,showActionbar;
    private TextView nextHint, check, _continue;

//    private ProgressBar progressBar;

    private LinearLayout showResult,questionLinearlayout, container;
    //    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private RetainedFragment retainedFragment; // to save the list for test array when the activity orientation change
    private Handler mHandler = new Handler();
    private Runnable mRunnable;


    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_during);
        currentField = getIntent().getStringExtra(CURRENT_FIELD);
        startingTime = getIntent().getLongExtra(STARTING_TIME, 0);
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(LIST_FOR_TEST);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, LIST_FOR_TEST).commit();
        }
        /*listForTest = retainedFragment.getListForTest_StudyDuringActivity();
        if ( listForTest == null ) {
            listForTest = prepareListForStudyFirstTime();
        }*/
//        questionsNumber = listForTest.length;
///////////////////////////////////////////////////////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        showResult = (LinearLayout) findViewById(R.id.result);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
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
        setSupportActionBar(toolbar);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if ( screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
                || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL ) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show );
        showQuestion.setMovementMethod(new ScrollingMovementMethod());
        //////////////////////////////////////////////////////////////////////////////////////////////////////
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testingNow = true;
                if (enterAnswer.getText().toString().length() > 0) {

                    result = checkAnAnswer();
                    if (triesCounter < permitedTriesNumber && !result) {
                        showQuestion.setText(listForTest[currentQuestionCounter][0]);
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
                                showNumberOfQuetions.setText(Integer.toString(questionsNumber));
                                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                                ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                            } else {
                                databaseOperations.clearStudyDuringDataFromLinkerTable(currentField);
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudyDuringActivity.this); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                /*editor.putString(STRING_REPRESENTING_REPEATED_ENTRIES, null);
                                editor.putInt(CURRENT_QUESTION_COUNTER, 0);*/
                                editor.remove(ACTIVITY_NAME + DASH + STRING_REPRESENTING_REPEATED_ENTRIES
                                        + DASH + currentField);
                                editor.remove(ACTIVITY_NAME + DASH + CURRENT_QUESTION_COUNTER
                                        + DASH + currentField);
                                editor.remove(ACTIVITY_NAME + DASH + NUMBER_OF_TRIES + DASH + currentField);
                                editor.commit();
                                finish();
                            }
//                            testingNow = false;
//                            enterAnswer.setEnabled(false);
//                            showNumberOfQuetions.setText("");
//                            remainingQuestionCounter.setText("");
//                            showTries.setText("");
//
//                            start.setEnabled(true);
//                            numberOfTries.setEnabled(true);

//                            Toast.makeText(TestActivity.this, "Test Completed!", Toast.LENGTH_SHORT).show();
                        } else {
                            showQuestion.setText(listForTest[currentQuestionCounter][0]);
                            showTries.setText(Integer.toString(triesCounter + 1));
                            ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
                            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
                            enterAnswer.setHint(R.string.enter_your_answer);
                            hintCounter = INITIAL_VALUE_OF_HINT_COUNTER;
//                            wrongAnswers = new ArrayList<String>(permitedTriesNumber);
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(StudyDuringActivity.this, R.string.where_the_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                enterAnswer.setHint(R.string.enter_your_answer);
            }
        });
        nextHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hints != null) {
                    if ( enterAnswer.getText().toString().length() <= 0 ) {
                        enterAnswer.setHint(hints.get(hintCounter++));
                    } else {
                        Toast toast = Toast.makeText(StudyDuringActivity.this, hints.get(hintCounter++), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                    hintCounter = hintCounter > (hints.size() - 1) ? INITIAL_VALUE_OF_HINT_COUNTER : hintCounter;
                }
            }
        });
       /* showQuestion.setText(listForTest [currentQuestionCounter][0]);
        ifMoreThanOnce(listForTest [currentQuestionCounter][0],listForTest [currentQuestionCounter][1]);
   */
        /*numberOfTries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numberOfTries.getText().toString().length() > 0) {
                    permitedTriesNumber = Integer.parseInt(numberOfTries.getText().toString());
                } else if ( numberOfTries.getText().toString().length() <= 0){
                    Toast.makeText(StudyDuringActivity.this,R.string.enter_tries_number,Toast.LENGTH_SHORT).show();
                } else if ( Integer.parseInt(numberOfTries.getText().toString()) <= 0 ) {
                    numberOfTries.setText(Integer.toString(1));
                    Toast.makeText(StudyDuringActivity.this, getString(R.string.tries_at_least), Toast.LENGTH_LONG).show();
                }
            }
        });*/
        enterAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0 && permitedTriesNumber != 1) {
//                    check.setBackgroundResource(R.drawable.ic_question_marks);
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
                }
            }
        });
        _continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check.setEnabled(true);
                enterAnswer.setText("");
                _continue.setEnabled(false);
                enterAnswer.setEnabled(true);
//                enterAnswer.requestFocus();
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(enterAnswer.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                enterAnswer.requestFocus();
                enterAnswer.setHint(R.string.enter_your_answer);
                if ( permitedTriesNumber == 1 ) {
//                    check.setBackgroundResource(R.drawable.ic_question_marks);
                    showResult.setBackgroundResource(R.drawable.enter_answer_background);
                    showResult.setPadding(0, 0, 0, 0);
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
        showNumberOfQuetions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.number_of_questions, showNumberOfQuetions.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        remainingQuestionCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.remaining_questions, remainingQuestionCounter.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        showTries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(StudyDuringActivity.this,
                        getString(R.string.try_number, showTries.getText().toString()), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        permitedTriesNumber = 3;
        showTries.setText(Integer.toString(triesCounter + 1));
        databaseOperations = new DatabaseOperations(getContentResolver());
        boolean_continue = getIntent().getBooleanExtra(LAST_SESSION_OR_NEW_ONE, false);
        if (boolean_continue) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String string = sharedPreferences.getString(ACTIVITY_NAME + DASH + STRING_REPRESENTING_REPEATED_ENTRIES
                    + DASH + currentField, null);
            currentQuestionCounter = sharedPreferences.getInt(ACTIVITY_NAME + DASH + CURRENT_QUESTION_COUNTER
                    + DASH + currentField, 0);
            repeatedEntries = convertStringToRepeatedEntriesArrayList(string);
            permitedTriesNumber = sharedPreferences.getInt(ACTIVITY_NAME + DASH + NUMBER_OF_TRIES + DASH + currentField, 0);
            getSupportLoaderManager().initLoader(CONTINUE_PREVIOUS_SESSION, null, this);
        } else {
            permitedTriesNumber = getIntent().getExtras().getInt(NUMBER_OF_TRIES);
            getSupportLoaderManager().initLoader(START_NEW_SESSION, null, this);
        }

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
            case R.id.stop:
                toStop();
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
            case R.id.open_facebook_page:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
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

    private void toStop() {
        final DatabaseOperations databaseOperations = new DatabaseOperations(getContentResolver());
//                progressBar.setVisibility(View.VISIBLE);
        databaseOperations.clearStudyDuringDataFromLinkerTable(currentField);
        databaseOperations.writeDataToLinkerTableStudyDuringColumns(listForTest, currentField);

//                databaseOperations.clearStudyDuringTempTable(currentField);
//                databaseOperations.writeToStudyDuringTempTable(listForTest,currentField);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACTIVITY_NAME + DASH + STRING_REPRESENTING_REPEATED_ENTRIES
                + DASH + currentField, convertRepeatedEntriesArrayListToString());
        editor.putInt(ACTIVITY_NAME + DASH + CURRENT_QUESTION_COUNTER
                + DASH + currentField, currentQuestionCounter);
        editor.putInt(ACTIVITY_NAME + DASH + NUMBER_OF_TRIES + DASH + currentField, permitedTriesNumber);
        editor.commit();
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
        searchForCursor = databaseOperations.searchExactlyFor(currentField, currentQuestion);
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

    private boolean checkAnAnswer() {
        boolean result = false;
        if (enterAnswer.getText().toString().equals(listForTest[currentQuestionCounter][1])) {
//            check.setBackgroundResource(R.drawable.ic_correct_sign);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ACTIVITY_NAME + DASH + CURRENT_QUESTION_COUNTER + DASH + currentField, currentQuestionCounter);
        editor.commit();
//        savedInsatanceState.putInt(CURRENT_QUESTION_COUNTER, currentQuestionCounter);
        savedInstanceState.putInt(TRIES_COUNTER, triesCounter);
        savedInstanceState.putBoolean(RESULT, result);
//        savedInsatanceState.putInt(PERMITED_TRIES_NUMBER, permitedTriesNumber);
        savedInstanceState.putBoolean(CHECK_BUTTON_STATE, check.isEnabled());
        savedInstanceState.putBoolean(_CONTINUE_BUTTON_STATE, _continue.isEnabled());
        savedInstanceState.putBoolean(TESTING_NOW, testingNow);
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS, questionsNumber);
//        savedInsatanceState.putInt(NUMBER_OF_WRONG_ANSWERS, wrongAnswersCounter);
        savedInstanceState.putInt(HINT_COUNTER, hintCounter);
        savedInstanceState.putStringArrayList(HINTS, hints);
        savedInstanceState.putIntegerArrayList(REPEATED_ENTRIES, repeatedEntries);
        saveUserLayoutChanges(savedInstanceState);
        if ( ! isFinishing() ) {
            toStop();
        }
//        savedInsatanceState.putStringArrayList(WRONG_ANSWERS, wrongAnswers);
//        savedInsatanceState.putSerializable(WRONG_ITEMS,wrongItems);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentQuestionCounter = sharedPreferences.getInt(ACTIVITY_NAME + DASH + CURRENT_QUESTION_COUNTER
                + DASH + currentField, 0);
//        currentQuestionCounter = savedInstanceState.getInt(CURRENT_QUESTION_COUNTER);
        triesCounter = savedInstanceState.getInt(TRIES_COUNTER);
        result = savedInstanceState.getBoolean(RESULT);
//        permitedTriesNumber = savedInstanceState.getInt(PERMITED_TRIES_NUMBER);
        testingNow = savedInstanceState.getBoolean(TESTING_NOW);
        boolean checkState = savedInstanceState.getBoolean(CHECK_BUTTON_STATE);
        boolean _continueState = savedInstanceState.getBoolean(_CONTINUE_BUTTON_STATE);
        questionsNumber = savedInstanceState.getInt(NUMBER_OF_QUESTIONS);
//        wrongAnswersCounter = savedInstanceState.getInt(NUMBER_OF_WRONG_ANSWERS);
        hints = savedInstanceState.getStringArrayList(HINTS);
        hintCounter = savedInstanceState.getInt(HINT_COUNTER);
        repeatedEntries = savedInstanceState.getIntegerArrayList(REPEATED_ENTRIES);
        restoreUserLayoutChanges(savedInstanceState);
//        wrongAnswers = savedInstanceState.getStringArrayList(WRONG_ANSWERS);
//        wrongItems = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEMS);
        if (testingNow) {
            showQuestion.setText(listForTest[currentQuestionCounter][0]);
            showTries.setText(Integer.toString(triesCounter + 1));
            remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            showNumberOfQuetions.setText(Integer.toString(questionsNumber));
            check.setEnabled(checkState);
            enterAnswer.setEnabled(checkState);
            _continue.setEnabled(_continueState);
//            enterPercent.setEnabled(false);

            enterAnswer.setEnabled(true);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == CONTINUE_PREVIOUS_SESSION) {
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
            return new CursorLoader(this, uri,
                    new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK,
                            Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER},
                    Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER + " >= ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{"0", currentField},
                    null);
            /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Study_During_Temp.TABLE_NAME);
            return new CursorLoader(this,uri,
                    new String[] {Contract.Study_During_Temp.COLUMN_NAME_QUESTION, Contract.Study_During_Temp.COLUMN_NAME_ANSWER},
                    Contract.Study_During_Temp.COLUMN_NAME_FIELD + " = ?",
                    new String[] {currentField},
                    null);*/
        } else {
            Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
            return new CursorLoader(this,
                    uri,
                    new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK, Contract.COLUMN_NAME_CREATED_AT},
                    Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                    new String[]{currentField},
                    null);
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
            ifMoreThanOnce(listForTest[currentQuestionCounter][0], listForTest[currentQuestionCounter][1]);
            showNumberOfQuetions.setText(Integer.toString(questionsNumber));
            if (boolean_continue) {
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1 - currentQuestionCounter));
            } else {
                remainingQuestionCounter.setText(Integer.toString(questionsNumber - 1));
            }

        } else if (questionsNumber <= 0 && loader.getId() == START_NEW_SESSION) {
            Toast toast = Toast.makeText(this, getString(R.string.no_entries_in_this_time_range), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.cursor = null;
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
        changeAreas(bundle.getFloat(QUESTION_LAYOUT),bundle.getFloat(ANSWER_LAYOUT));
        showQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX,bundle.getFloat(TEXT_SIZE));
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX,bundle.getFloat(TEXT_SIZE));
    }
}
