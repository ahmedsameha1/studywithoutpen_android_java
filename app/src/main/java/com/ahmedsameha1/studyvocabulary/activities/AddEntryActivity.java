package com.ahmedsameha1.studyvocabulary.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DataProvider;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ChangeTextSizeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
/**
 * Created by Ahmed on 3/19/16.
 */
public class AddEntryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize{
    public static final String FROM_FIELD_ACTIVITY = "from_field_activity";
    private static final String FIELD_CHOSEN = "field_chosen";
    private static final String SPINNER_POSITION = "spinner_position";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private Spinner field_spinner;
    private EditText enter_question;
    private EditText enter_answer;
    private TextView add_entry;
    private TextView add_entry_finish;
    private TextView showActionbar;
    private SimpleCursorAdapter simple_cursor_adapter;
    private DatabaseOperations databaseOperations;
    private String currentField;
    private boolean actionBarShown;
    private boolean fromFieldActivity;
    private int fieldType;
    private Cursor cursor;
    private boolean field_chosen;
    private LinearLayout showResult,questionLinearlayout, container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fromFieldActivity = getIntent().getBooleanExtra(FROM_FIELD_ACTIVITY, true);
        currentField = getIntent().getStringExtra(FieldActivity.CURRENT_FIELD);
        field_spinner = (Spinner) findViewById(R.id.field_spinner);
        if ( fromFieldActivity ) {
            field_spinner.setEnabled(false);
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            if ( screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
                    || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                getSupportActionBar().hide();
            }
        }
        actionBarShown = getSupportActionBar().isShowing();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enter_question = (EditText) findViewById(R.id.question);
        enter_answer = (EditText) findViewById(R.id.answer);
        add_entry = (TextView) findViewById(R.id.add_entry);
        add_entry_finish = (TextView) findViewById(R.id.add_entry_finish);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        databaseOperations = new DatabaseOperations(getContentResolver());
        fieldType = databaseOperations.getFieldType(currentField);
        showResult = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        simple_cursor_adapter = new SimpleCursorAdapter(this,R.layout.field_spinner,null,
                new String [] {Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME}, new int[] {R.id.list_item}, 0);
        field_spinner.setAdapter(simple_cursor_adapter);
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        setHints(fieldType);
        add_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( enter_question.getText().toString().length() > 0 && enter_answer.getText().toString().length() > 0) {
                    if (fromFieldActivity) {
                        switch (fieldType) {
                            case Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE:
                                databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                        enter_answer.getText().toString());
                                break;
                            case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
                                String dateTimeUserFormat = enter_question.getText().toString();
                                StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                                stringTokenizer.nextToken();
                                String dateToSave = stringTokenizer.nextToken() + " "
                                        + stringTokenizer.nextToken() + ":00";
                                databaseOperations.insertFieldEntry(currentField, dateToSave, enter_answer.getText().toString());
                                break;
                        }
                        enter_question.setText("");
                        enter_answer.setText("");
                    } else {
                        if ( field_chosen ) {
                            switch (fieldType) {
                                case Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE:
                                    databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                            enter_answer.getText().toString());
                                    break;
                                case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
                                    String dateTimeUserFormat = enter_question.getText().toString();
                                    StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                                    stringTokenizer.nextToken();
                                    String dateToSave = stringTokenizer.nextToken() + " "
                                            + stringTokenizer.nextToken() + ":00";
                                    databaseOperations.insertFieldEntry(currentField, dateToSave, enter_answer.getText().toString());
                                    break;
                            }
                            enter_question.setText("");
                            enter_answer.setText("");
                        } else {
                            Toast toast = Toast.makeText(AddEntryActivity.this, R.string.select_field, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(AddEntryActivity.this, R.string.both_question_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });
        add_entry_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enter_question.getText().toString().length() > 0 && enter_answer.getText().toString().length() > 0) {
                    if (fromFieldActivity) {
                        switch (fieldType) {
                            case Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE:
                                databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                        enter_answer.getText().toString());
                                break;
                            case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
                                String dateTimeUserFormat = enter_question.getText().toString();
                                StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                                stringTokenizer.nextToken();
                                String dateToSave = stringTokenizer.nextToken() + " "
                                        + stringTokenizer.nextToken() + ":00";
                                databaseOperations.insertFieldEntry(currentField, dateToSave, enter_answer.getText().toString());
                                break;
                        }
                        finish();
                    } else {
                        if (field_chosen) {
                            switch (fieldType) {
                                case Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE:
                                    databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                            enter_answer.getText().toString());
                                    break;
                                case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
                                    String dateTimeUserFormat = enter_question.getText().toString();
                                    StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                                    stringTokenizer.nextToken();
                                    String dateToSave = stringTokenizer.nextToken() + " "
                                            + stringTokenizer.nextToken() + ":00";
                                    databaseOperations.insertFieldEntry(currentField, dateToSave, enter_answer.getText().toString());
                                    break;
                            }
                            finish();
                        } else {
                            Toast toast = Toast.makeText(AddEntryActivity.this, R.string.select_field, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(AddEntryActivity.this, R.string.both_question_answer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });
        field_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                field_chosen = true;
                return false;
            }
        });
        field_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getSelectedItem();
                currentField = c.getString(1);
                fieldType = databaseOperations.getFieldType(currentField);
                setHints(fieldType);
                if ( fieldType == Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE) {
                    enter_question.setText("");
                    enter_question.clearFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        enter_question.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (fieldType) {
                        case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
//                            enterQuestion.clearFocus();
                            enter_answer.requestFocus();
                            Calendar calendar = Calendar.getInstance();
                            new DatePickerDialog(AddEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                    final Calendar calendar = Calendar.getInstance();
                                    new TimePickerDialog(AddEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                            enter_question.setText(new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm", Locale.ENGLISH).format(calendar.getTime()));
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
        enter_question.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enter_question.setText("");
                enter_question.requestFocus();
                return true;
            }
        });
        enter_answer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                enter_answer.setText("");
                enter_answer.requestFocus();
                return true;
            }
        });
        getSupportLoaderManager().restartLoader(0,null,this);
        enter_answer.requestFocus();
    }

    private void setHints(int fieldType ) {
        switch ( fieldType ) {
            case Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE:
                enter_question.setHint(R.string.enter_date_time);
                enter_answer.setHint(R.string.write_whatever);
                break;
            case Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE:
                enter_question.setHint(R.string.enter_a_question);
                enter_answer.setHint(R.string.enter_an_answer);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_entry_activity_menu, menu);
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
                bundle1.putFloat(TEXT_SIZE, (enter_question.getTextSize()/scaledDensity) );
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
        savedInstanceState.putString(FieldActivity.CURRENT_FIELD,currentField);
        savedInstanceState.putBoolean(FIELD_CHOSEN, field_chosen);
        savedInstanceState.putInt(SPINNER_POSITION, field_spinner.getSelectedItemPosition());
        saveUserLayoutChanges(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        currentField = savedInstanceState.getString(FieldActivity.CURRENT_FIELD);
        field_chosen = savedInstanceState.getBoolean(FIELD_CHOSEN);
        restoreUserLayoutChanges(savedInstanceState);
    }
    private void setSpinnerPosition(String fieldName ) {
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
              cursor.moveToNext() ){
            String name = cursor.getString(1);
            if ( name.equals(fieldName) ) {
                field_spinner.setSelection(cursor.getPosition());
                return;
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Field_DBV6_AppV32.TABLE_NAME);
        return new CursorLoader(this,uri, new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME},null,null,
                "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simple_cursor_adapter.swapCursor(data);
        cursor = data;
        setSpinnerPosition(currentField);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simple_cursor_adapter.swapCursor(null);
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
        enter_question.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enter_answer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }
    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, enter_question.getTextSize());
    }
    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT),bundle.getFloat(ANSWER_LAYOUT));
        enter_question.setTextSize(TypedValue.COMPLEX_UNIT_PX,bundle.getFloat(TEXT_SIZE));
        enter_answer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }
}
