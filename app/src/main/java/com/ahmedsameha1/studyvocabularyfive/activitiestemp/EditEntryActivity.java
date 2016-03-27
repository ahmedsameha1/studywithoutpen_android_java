package com.ahmedsameha1.studyvocabularyfive.activitiestemp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.databasetemp.Contract;
import com.ahmedsameha1.studyvocabularyfive.databasetemp.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Ahmed on 3/25/16.
 */
public class EditEntryActivity extends AppCompatActivity {
    private Spinner field_spinner;
    private EditText editQuestion;
    private EditText editAnswer;
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
    private TextView save;
    private String currentQuestion;
    private String currentAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentField = getIntent().getStringExtra(FieldActivity.CURRENT_FIELD);
        currentQuestion = getIntent().getStringExtra(StudyActivity.CURRENT_QUESTION);
        currentAnswer = getIntent().getStringExtra(StudyActivity.CURRENT_ANSWER);
        databaseOperations = new DatabaseOperations(getContentResolver());
        fieldType = databaseOperations.getFieldType(currentField);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        save = (TextView) findViewById(R.id.save);
        editQuestion = (EditText) findViewById(R.id.question);
        editAnswer = (EditText) findViewById(R.id.answer);
            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;
            if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL
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
                switch (fieldType) {
                    case Contract.Field.DEFAULT_TYPE:
                        editQuestion.setEnabled(false);
                        editAnswer.setEnabled(false);
                        if (!editQuestion.getText().toString().equals(currentQuestion) || !editAnswer.getText().toString().equals(currentAnswer)) {
                            boolean b = databaseOperations.editFieldEntry(currentField, currentQuestion, editQuestion.getText().toString(),
                                    currentAnswer, editAnswer.getText().toString());
                            if (!b) {
                                Toast toast = Toast.makeText(EditEntryActivity.this, R.string.repeated_entry, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        }
                        break;
                    case Contract.Field.NOTEBOOK_TYPE:
                        editQuestion.setEnabled(false);
                        editAnswer.setEnabled(false);
                        String dateTimeUserFormat = editQuestion.getText().toString();
                        StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                        stringTokenizer.nextToken();
                        String dateToSave = stringTokenizer.nextToken() + " "
                                + stringTokenizer.nextToken() + ":00";
                        if (!dateToSave.equals(currentQuestion) || !editAnswer.getText().toString().equals(currentAnswer)) {
                            boolean b = databaseOperations.editFieldEntry(currentField, currentQuestion, dateToSave,
                                    currentAnswer, editAnswer.getText().toString());
                            if (!b) {
                                Toast toast = Toast.makeText(EditEntryActivity.this, R.string.repeated_entry, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        }
                        break;
                }
                finish();
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
}
