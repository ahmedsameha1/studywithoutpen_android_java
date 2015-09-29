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
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by ahmed on 8/1/2015.
 */
public class StudyActivity extends AppCompatActivity {
    public static final String CURRENT_FIELD = "currentField";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String SEARCH_VALUE = "searchValue";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    private String currentField,currentQuestion, currentAnswer;
    private TextView viewQuestion, viewAnswer , correctAnswers;
    private Button checkAndClear;
    private ToggleButton showCurrentEntry;
    private CheckBox studyQuestion, studyAnswer;
    private EditText enterQuestion, enterAnswer, searchFor;
    private ListView searchResults;
    private SimpleCursorAdapter simpleCursorAdapter;
    private ImageButton  up , down, searchForClear;
    private ImageView resultImage;
    private int correctAnswersCounter = 0;
    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private Cursor fieldEntriesCursor, searchForCursor;
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    private LoaderManager.LoaderCallbacks<Cursor> searchFieldEntries;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        currentField = getIntent().getStringExtra(CURRENT_FIELD);
        currentQuestion = getIntent().getStringExtra(CURRENT_QUESTION);
        currentAnswer = getIntent().getStringExtra(CURRENT_ANSWER);
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
        searchFor = (EditText) findViewById(R.id.study_search_entrys);
        searchForClear = (ImageButton) findViewById(R.id.search_for_clear);
        searchResults = (ListView) findViewById(R.id.study_search_list_view);
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
                    if ( studyQuestion.isChecked() ) {
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
        searchFor.addTextChangedListener(new TextWatcher() {
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
                    /*searchForCursor = databaseOperations.searchFor(currentField, value);
                    simpleCursorAdapter = new SimpleCursorAdapter(StudyActivity.this,
                            R.layout.study_search_list_view,
                            searchForCursor,
                            new String[]{currentField + Contract.OldField.COLUMN_NAME_QUESTION_LINK_SUFFIX, currentField + Contract.OldField.COLUMN_NAME_ANSWER_LINK_SUFFIX},
                            new int[]{R.id.study_search_list_view_text1, R.id.study_search_list_view_text2},
                            0);
                    searchResults.setAdapter(simpleCursorAdapter);*/
                } else {
                    simpleCursorAdapter.changeCursor(null);
                }
            }
        });
        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) searchResults.getItemAtPosition(position);
                currentQuestion = cursor.getString(1);
                currentAnswer = cursor.getString(2);
                viewQuestion.setText(currentQuestion);
                viewAnswer.setText(currentAnswer);
                searchFor.setText("");
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
                .addOnGlobalLayoutListener(new OnViewGlobalLayoutListener(searchResults));
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
        searchForClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFor.setText("");
            }
        });
        correctAnswers.setText(Integer.toString(0));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this,
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
        };
        searchFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String searchString = args.getString(StudyActivity.SEARCH_VALUE);
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(StudyActivity.this,
                            uri,
                            new String[] {"rowid _id",
                                    Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                    Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                                    + " OR " +
                                    Contract.Linker.COLUMN_NAME_ANSWER_LINK
                                    + " LIKE ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
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
                R.layout.study_search_list_view,
                null,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                new int[]{R.id.study_search_list_view_text1, R.id.study_search_list_view_text2},
                0);
        searchResults.setAdapter(simpleCursorAdapter);
        getSupportLoaderManager().initLoader(GET_ENTRIES_LOADER_ID,null,getFieldEntries);
    }
    @Override
    public void onResume() {
        super.onResume();
        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getContentResolver());
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
    public void onPause() {
        super.onPause();
//        databaseHelper.close();
    }
    @Override
    public void onDestroy() {
/*        if ( fieldEntriesCursor != null ) {
            fieldEntriesCursor.close();
        }
        if ( searchForCursor != null ) {
            searchForCursor.close()
        };*/
//        sqLiteDatabase.close();
        databaseHelper.close();
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
//        searchFor.setText("");
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_FIELD, currentField);
        savedInstanceState.putString(CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putString(CURRENT_ANSWER, currentAnswer);
        savedInstanceState.putInt(CORRECT_ANSWERS_COUNTER, correctAnswersCounter);

    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getContentResolver());
        currentField = savedInstanceState.getString(CURRENT_FIELD);
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
}
