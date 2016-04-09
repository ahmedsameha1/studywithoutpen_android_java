package com.ahmedsameha1.studyvocabulary.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DataProvider;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.studyvocabulary.R;

/**
 * Created by ahmed on 8/1/2015.
 */
public class StudyActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String CURRENT_FIELD = "currentField";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String SEARCH_VALUE = "searchValue";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final int ORDER_QUESTION_DESC = 2;
    public static final int ORDER_ANSWER_DESC = 3;
    public static final int ORDER_QUESTION_ASC = 4;
    public static final int ORDER_ANSWER_ASC = 5;
    public static final int ORDER_DATE_DESC = 6;
    public static final int ORDER_DATE_ASC = 7;
    public static final int ORDER_ENTRY_DATE_DESC = 8;
    public static final int ORDER_ENTRY_DATE_ASC = 9;
    public static final int SELECT_ENTRY_REQUEST = 1;
    public static final int RETURN_TIME = 1000;
    public static final String ORDER_OF_ENTRIES = "order_of_entries";
    private boolean actionBarShown;
    private String currentField,currentQuestion, currentAnswer;
    private TextView correctAnswers, check,showActionbar;
    private CheckBox studyQuestion, studyAnswer;
    private ToggleButton showCurrentEntry;
    private EditText enterQuestion, enterAnswer;
    private ImageButton  up , down;
    private int correctAnswersCounter = 0;

    private LinearLayout answerLinearlayout,questionLinearlayout, container;

    private Cursor fieldEntriesCursor;
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;

    private int currentLoader;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        currentField = getIntent().getStringExtra(CURRENT_FIELD);
        currentQuestion = getIntent().getStringExtra(CURRENT_QUESTION);
        currentAnswer = getIntent().getStringExtra(CURRENT_ANSWER);
        currentLoader = getIntent().getIntExtra(ORDER_OF_ENTRIES,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        showCurrentEntry = (ToggleButton) findViewById(R.id.show_current_study_entry);
        studyQuestion = (CheckBox) findViewById(R.id.study_question);
        studyAnswer = (CheckBox) findViewById(R.id.study_answer);
        enterQuestion = (EditText) findViewById(R.id.question);
        enterAnswer = ( EditText) findViewById(R.id.answer);
        correctAnswers = ( TextView) findViewById(R.id.correct_answers);
        check = (TextView) findViewById(R.id.check);
        answerLinearlayout = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
//        searchFor = (EditText) findViewById(R.id.study_search_entrys);
//        searchForClear = (ImageButton) findViewById(R.id.search_for_clear);
        up = (ImageButton) findViewById(R.id.up);
        down = (ImageButton) findViewById(R.id.down);
        setSupportActionBar(toolbar);
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (  screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
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
                    questionLinearlayout.setPadding(0,0,0,0);
                    answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                    answerLinearlayout.setPadding(0,0,0,0);
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

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studyQuestion.isChecked() && studyAnswer.isChecked()) {
                    if ( enterQuestion.getText().toString().equals(currentQuestion) ) {
                        questionLinearlayout.setBackgroundResource(R.drawable.correct_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    } else {
                        questionLinearlayout.setBackgroundResource(R.drawable.wrong_answer_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                    }
                    if ( enterAnswer.getText().toString().equals(currentAnswer) ) {
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
/*        searchFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if (value.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(StudyActivity.SEARCH_VALUE,value);
                    getSupportLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID,bundle,searchFieldEntries);
                    *//*searchForCursor = databaseOperations.searchFor(currentField, value);
                    simpleCursorAdapter = new SimpleCursorAdapter(StudyActivity.this,
                            R.layout.search_row,
                            searchForCursor,
                            new String[]{currentField + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX, currentField + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX},
                            new int[]{R.id.study_search_list_view_text1, R.id.study_search_list_view_text2},
                            0);
                    searchResults.setAdapter(simpleCursorAdapter);*//*
                } else {
                    simpleCursorAdapter.changeCursor(null);
                }
            }
        });*/
        /*searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) searchResults.getItemAtPosition(position);
                currentQuestion = cursor.getString(1);
                currentAnswer = cursor.getString(2);
                viewQuestion.setText(currentQuestion);
                viewAnswer.setText(currentAnswer);
//                searchFor.setText("");
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
        });
        searchResults.getViewTreeObserver()
                .addOnGlobalLayoutListener(new OnViewGlobalLayoutListener(searchResults));*/
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0;
/*                if (fieldEntriesCursor != null) {
                    fieldEntriesCursor.close();
                }*/
//                fieldEntriesCursor = databaseOperations.getFieldEntries(currentField);
                for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount(); fieldEntriesCursor.moveToNext()) {
                    if (fieldEntriesCursor.getString(1).equals(currentQuestion) && fieldEntriesCursor.getString(2).equals(currentAnswer)) {
                        position = fieldEntriesCursor.getPosition();
                        break;
                    }
                }
                if (--position >= 0) {
                    fieldEntriesCursor.moveToPosition(position);
                    currentQuestion = fieldEntriesCursor.getString(1);
                    currentAnswer = fieldEntriesCursor.getString(2);
                    /*viewQuestion.setText(currentQuestion);
                    viewAnswer.setText(currentAnswer);*/
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
                    if (showCurrentEntry.isChecked()) {
                        enterQuestion.setText(currentQuestion);
                        enterAnswer.setText(currentAnswer);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
                }
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = 0;
/*                if (fieldEntriesCursor != null) {
                    fieldEntriesCursor.close();
                }*/
//                fieldEntriesCursor = databaseOperations.getFieldEntries(currentField);
                for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount(); fieldEntriesCursor.moveToNext()) {
                    if (fieldEntriesCursor.getString(1).equals(currentQuestion) && fieldEntriesCursor.getString(2).equals(currentAnswer)) {
                        position = fieldEntriesCursor.getPosition();
                        break;
                    }
                }
                if (++position < fieldEntriesCursor.getCount()) {
                    fieldEntriesCursor.moveToPosition(position);
                    currentQuestion = fieldEntriesCursor.getString(1);
                    currentAnswer = fieldEntriesCursor.getString(2);
                    /*viewQuestion.setText(currentQuestion);
                    viewAnswer.setText(currentAnswer);*/
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
                    if (showCurrentEntry.isChecked()) {
                        enterQuestion.setText(currentQuestion);
                        enterAnswer.setText(currentAnswer);
                        questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        questionLinearlayout.setPadding(0, 0, 0, 0);
                        answerLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                        answerLinearlayout.setPadding(0, 0, 0, 0);
                    }
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


 /*       getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this,
                            uri,
                            new String[] {"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
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
        };*/
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                 if (id == ORDER_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "date(" + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + ") DESC");

                } else if (id == ORDER_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "date(" + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + ") ASC");
                } else if (id == ORDER_QUESTION_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " DESC");
                } else if (id == ORDER_QUESTION_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " ASC");
                } else if (id == ORDER_ANSWER_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " DESC");
                } else if (id == ORDER_ANSWER_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK + " ASC");
                } else if (id == ORDER_ENTRY_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");
                } else if (id == ORDER_ENTRY_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") ASC");
                } else {
                    /*String currentField = null;
                    if (args != null) {
                        currentField = args.getString(FieldActivity.CURRENT_FIELD);
                    }
                    currentField = currentField == null ? getCurrentField() : currentField;*/
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                fieldEntriesCursor = data;
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                fieldEntriesCursor = null;
            }
        };
        /*searchFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String searchString = args.getString(StudyActivity.SEARCH_VALUE);
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this,
                            uri,
                            new String[] {"rowid _id",
                                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                    + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? "
                                    + " OR " +
                                    Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK
                                    + " LIKE ? AND "
                                    + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[] {"%" + searchString + "%",currentField,"%" + searchString + "%",currentField},
                            null);
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                simpleCursorAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                simpleCursorAdapter.swapCursor(null);
            }
        };
        simpleCursorAdapter = new SimpleCursorAdapter(StudyActivity.this,
                R.layout.search_row,
                null,
                new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK, Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK},
                new int[]{R.id.study_search_list_view_text1, R.id.study_search_list_view_text2},
                0);*/
//        searchResults.setAdapter(simpleCursorAdapter);
        correctAnswers.setText(Integer.toString(0));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportLoaderManager().initLoader(currentLoader, null, getFieldEntries);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.study_activity_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        //check if search intent
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // manually launch the real search activity
            final Intent searchIntent = new Intent(this,
                    SearchActivity.class);
            // add query to the Intent Extras
            searchIntent.setAction(Intent.ACTION_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, query);
            searchIntent.putExtra(CURRENT_FIELD, currentField);
            startActivityForResult(searchIntent, SELECT_ENTRY_REQUEST);
            /*intent.putExtra(CURRENT_FIELD, currentField);
            startActivityForResult(intent,SELECT_ENTRY_REQUEST);*/
        } else {
            super.startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_ENTRY_REQUEST) {
            if (resultCode == RESULT_OK) {
                currentField = data.getStringExtra(CURRENT_FIELD);
                currentQuestion = data.getStringExtra(CURRENT_QUESTION);
                currentAnswer = data.getStringExtra(CURRENT_ANSWER);
                currentLoader = data.getIntExtra(ORDER_OF_ENTRIES, 0);
                enterQuestion.setText(currentQuestion);
                correctAnswers.setText(Integer.toString(0));
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                getSupportLoaderManager().restartLoader(currentLoader,null,getFieldEntries);
            }
        }
    }


/*    public int getEntryPostion (String fieldName, String question ,String answer ) {
        Cursor cursor = new DatabaseHelper(this).getFieldEntries(currentField);
        for ( cursor.moveToFirst() ; cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            if ( cursor.getString(1).equals(question) && cursor.getString(2).equals(answer)) {
                return cursor.getPosition();
            }
        }
        return 0;
    }*/

    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
//        searchFor.setText("");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_FIELD, currentField);
        savedInstanceState.putString(CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putString(CURRENT_ANSWER, currentAnswer);
        savedInstanceState.putInt(CORRECT_ANSWERS_COUNTER, correctAnswersCounter);
        saveUserLayoutChanges(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        currentField = savedInstanceState.getString(CURRENT_FIELD);
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
    private static class OnViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private final static int maxHeight = 75;
        private View view;

        public OnViewGlobalLayoutListener(View view) {
            this.view = view;
        }

        @Override
        public void onGlobalLayout() {
            if (view.getHeight() > maxHeight)
                view.getLayoutParams().height = maxHeight;
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
