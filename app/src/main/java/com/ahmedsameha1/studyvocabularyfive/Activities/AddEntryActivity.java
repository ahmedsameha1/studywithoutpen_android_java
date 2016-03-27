package com.ahmedsameha1.studyvocabularyfive.Activities;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;
/**
 * Created by Ahmed on 3/19/16.
 */
public class AddEntryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String FROM_FIELD_ACTIVITY = "from_field_activity";
    private static final String FIELD_CHOSEN = "field_chosen";
    private static final String SPINNER_POSITION = "spinner_position";
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
    private int current_spinner_position;
    private Cursor cursor;
    private boolean field_chosen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fromFieldActivity = getIntent().getBooleanExtra(FROM_FIELD_ACTIVITY, true);
        currentField = getIntent().getStringExtra(FieldActivity.CURRENT_FIELD);
        current_spinner_position = getIntent().getIntExtra(MainActivity.LAST_USED_FIELD_POSITION, 0);
        field_spinner = (Spinner) findViewById(R.id.field_spinner);
        if ( fromFieldActivity ) {
            field_spinner.setEnabled(false);
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
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
        simple_cursor_adapter = new SimpleCursorAdapter(this,R.layout.field_spinner,null,
                new String [] {Contract.Field.COLUMN_NAME_FIELD_NAME}, new int[] {R.id.list_item}, 0);
        field_spinner.setAdapter(simple_cursor_adapter);
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        setHints(fieldType);
        add_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromFieldActivity) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                    enter_answer.getText().toString());
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
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
                            case Contract.Field.DEFAULT_TYPE:
                                databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                        enter_answer.getText().toString());
                                break;
                            case Contract.Field.NOTEBOOK_TYPE:
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
            }
        });
        add_entry_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromFieldActivity) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                    enter_answer.getText().toString());
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
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
                    if ( field_chosen ) {
                        switch (fieldType) {
                            case Contract.Field.DEFAULT_TYPE:
                                databaseOperations.insertFieldEntry(currentField, enter_question.getText().toString(),
                                        enter_answer.getText().toString());
                                break;
                            case Contract.Field.NOTEBOOK_TYPE:
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
                if ( fieldType == Contract.Field.NOTEBOOK_TYPE) {
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
                        case Contract.Field.NOTEBOOK_TYPE:
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return false;
        }
    }
    private void setHints(int fieldType ) {
        switch ( fieldType ) {
            case Contract.Field.NOTEBOOK_TYPE:
                enter_question.setHint(R.string.enter_date_time);
                enter_answer.setHint(R.string.write_whatever);
                break;
            case Contract.Field.DEFAULT_TYPE:
                enter_question.setHint(R.string.enter_a_question);
                enter_answer.setHint(R.string.enter_an_answer);
        }
    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(FIELD_CHOSEN, field_chosen);
        savedInstanceState.putInt(SPINNER_POSITION, field_spinner.getSelectedItemPosition());
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onRestoreInstanceState(savedInstanceState);
        field_chosen = savedInstanceState.getBoolean(FIELD_CHOSEN);
        current_spinner_position = savedInstanceState.getInt(SPINNER_POSITION);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        return new CursorLoader(this,uri, new String[]{"rowid _id",Contract.Field.COLUMN_NAME_FIELD_NAME},null,null,
                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simple_cursor_adapter.swapCursor(data);
        field_spinner.setSelection(current_spinner_position);
        cursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simple_cursor_adapter.swapCursor(null);
    }
}
