package com.ahmedsameha1.studyvocabulary.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteEntryDialogFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Ahmed on 3/25/16.
 */
public class EditEntryActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String FIELD_TYPE = "field_type";
    public static final String ENTRY_ID = "entry_id";
    private Spinner field_spinner;
    private EditText editQuestion;
    private EditText editAnswer;
    private TextView add_entry;
    private TextView add_entry_finish;
    private TextView showActionbar;
    private SimpleCursorAdapter simple_cursor_adapter;
    private DatabaseOperations databaseOperations;
    private boolean actionBarShown;
    private boolean fromFieldActivity;
    private int fieldType;
    private int current_spinner_position;
    private Cursor cursor;
    private boolean field_chosen;
    private TextView save;
    private long current_field_id;
    private String currentQuestion;
    private String currentAnswer;
    private LinearLayout showResult,questionLinearlayout, container;
    private long entry_id;
    private int question_data_type;
    private MySQLiteCursorLoader mySQLiteCursorLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,0);
        fieldType = getIntent().getIntExtra(FIELD_TYPE,0);
        entry_id = getIntent().getLongExtra(ENTRY_ID,0);
        question_data_type = getIntent().getIntExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,0);
        currentQuestion = getIntent().getStringExtra(StudyActivity.CURRENT_QUESTION);
        currentAnswer = getIntent().getStringExtra(StudyActivity.CURRENT_ANSWER);
        databaseOperations = new DatabaseOperations(this);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        save = (TextView) findViewById(R.id.save);
        editQuestion = (EditText) findViewById(R.id.question);
        editAnswer = (EditText) findViewById(R.id.answer);
        showResult = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            if ( screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
                    || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                getSupportActionBar().hide();
            }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        editAnswer.setText(currentAnswer);
        switch (fieldType) {
            case Contract.Field.DEFAULT_TYPE:
                editQuestion.setText(currentQuestion);
                break;
            case Contract.Field.NOTEBOOK_TYPE:
                Timestamp timestamp = Timestamp.valueOf(currentQuestion);
                Date date = new Date(timestamp.getTime());
                editQuestion.setText(new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm").format(date));
                break;
        }
        setHints(fieldType);
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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedQuestion = editQuestion.getText().toString();
                String editedAnswer = editAnswer.getText().toString();
                if ( editedQuestion.length() > 0 && editedAnswer.length() > 0 ) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            editQuestion.setEnabled(false);
                            editAnswer.setEnabled(false);
                            if (!editedQuestion.equals(currentQuestion) || !editedAnswer.equals(currentAnswer)) {
                                if ( ! databaseOperations.checkEntryDuplication(editedQuestion,editedAnswer,current_field_id,question_data_type)) {
//                                    mySQLiteCursorLoader.editEntry(entry_id,question_data_type,editedQuestion,editedAnswer);
                                    Intent intent = new Intent();
                                    intent.putExtra(FieldActivity.EDITED_ENTRY_ID,entry_id);
                                    intent.putExtra(FieldActivity.EDITED_QUESTION,editedQuestion);
                                    intent.putExtra(FieldActivity.EDITED_ANSWER,editedAnswer);
                                    setResult(RESULT_OK,intent);
                                } else {
                                Toast toast = Toast.makeText(EditEntryActivity.this, R.string.repeated_entry, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                }
                            }
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
                            editQuestion.setEnabled(false);
                            editAnswer.setEnabled(false);
                            String dateTimeUserFormat = editedQuestion;
                            StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                            stringTokenizer.nextToken();
                            String dateToSave = stringTokenizer.nextToken() + " "
                                    + stringTokenizer.nextToken() + ":00";
                            if (!dateToSave.equals(currentQuestion) || !editedAnswer.equals(currentAnswer)) {
                                if ( ! databaseOperations.checkEntryDuplication(dateToSave,editedAnswer,current_field_id,question_data_type)) {
//                                 mySQLiteCursorLoader.editEntry(entry_id,question_data_type,dateToSave,editedAnswer);
                                    Intent intent = new Intent();
                                    intent.putExtra(FieldActivity.EDITED_ENTRY_ID,entry_id);
                                    intent.putExtra(FieldActivity.EDITED_QUESTION,dateToSave);
                                    intent.putExtra(FieldActivity.EDITED_ANSWER,editedAnswer);
                                    setResult(RESULT_OK,intent);
                                } else {
                                    Toast toast = Toast.makeText(EditEntryActivity.this, R.string.repeated_entry, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            }
                            break;
                    }
                    finish();
                } else {
                    Toast toast = Toast.makeText(EditEntryActivity.this, R.string.something_both_question_answer, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }

            }
        });
        editQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (fieldType) {
                        case Contract.Field.NOTEBOOK_TYPE:
//                            enterQuestion.clearFocus();
                            editAnswer.requestFocus();
                            Calendar calendar = Calendar.getInstance();
                            new DatePickerDialog(EditEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                    final Calendar calendar = Calendar.getInstance();
                                    new TimePickerDialog(EditEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                            editQuestion.setText(new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm", Locale.ENGLISH).format(calendar.getTime()));
//                                            enterAnswer.requestFocus();
                                        }
                                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                                }
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                            break;
                    }
                }
            }
        });
        editQuestion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editQuestion.setText("");
                editQuestion.requestFocus();
                return true;
            }
        });
        editAnswer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editAnswer.setText("");
                editAnswer.requestFocus();
                return true;
            }
        });
        editAnswer.requestFocus();
    }
    private void setHints(int fieldType ) {
        switch ( fieldType ) {
            case Contract.Field.NOTEBOOK_TYPE:
                editQuestion.setHint(R.string.enter_date_time);
                editAnswer.setHint(R.string.write_whatever);
                break;
            case Contract.Field.DEFAULT_TYPE:
                editQuestion.setHint(R.string.enter_a_question);
                editAnswer.setHint(R.string.enter_an_answer);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_entry_activity_menu, menu);
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
                bundle.putFloat(ANSWER_LAYOUT,((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(),ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (editQuestion.getTextSize()/scaledDensity) );
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
        super.onSaveInstanceState(savedInstanceState);
        saveUserLayoutChanges(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreUserLayoutChanges(savedInstanceState);
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
        editQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        editAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, editQuestion.getTextSize());
    }
    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT),bundle.getFloat(ANSWER_LAYOUT));
        editQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
        editAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }
}
