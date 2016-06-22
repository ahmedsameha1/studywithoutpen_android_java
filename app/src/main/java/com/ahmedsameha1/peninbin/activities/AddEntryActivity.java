package com.ahmedsameha1.peninbin.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.OnSwipeTouchListener;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
        ChangeTextSizeDialogFragment.ChangeTextSize {
    public static final String SCREEN_NAME = "AddEntryActivity";
    public static final String FROM_FIELD_ACTIVITY = "from_field_activity";
    private static final String FIELD_CHOSEN = "field_chosen";
    private static final String SPINNER_POSITION = "spinner_position";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final String CURRENT_FIELD_ID = "current_field_id";
    private Spinner field_spinner;
    private EditText enter_question;
    private EditText enter_answer;
    private TextView showActionbar;
    private SimpleCursorAdapter simple_cursor_adapter;


    private DatabaseOperations databaseOperations;
    private boolean actionBarShown;
    private boolean fromFieldActivity;
    private int fieldType;
    private Cursor cursor;
    private boolean field_chosen;
    private LinearLayout showResult, questionLinearlayout, container;
    private long current_field_id;
    private int question_data_type;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        fromFieldActivity = getIntent().getBooleanExtra(FROM_FIELD_ACTIVITY, true);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        databaseOperations = new DatabaseOperations(this);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        fieldType = databaseOperations.getFieldType(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        field_spinner = (Spinner) findViewById(R.id.field_spinner);
        final Spinner rank_spinner = (Spinner) findViewById(R.id.rank_spinner);
        enter_question = (EditText) findViewById(R.id.question);
        enter_answer = (EditText) findViewById(R.id.answer);
        final TextView add_entry = (TextView) findViewById(R.id.add_entry);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        showResult = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        final CardView show_actionbar_cardview = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView add_entry_cardview = (CardView) findViewById(R.id.add_entry_card_view);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        simple_cursor_adapter = new SimpleCursorAdapter(this, R.layout.field_spinner, null,
                new String[]{Contract.Field.COLUMN_FIELD_NAME}, new int[]{R.id.list_item}, 0);
        final ArrayAdapter rank_spinner_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.rank));
        field_spinner.setAdapter(simple_cursor_adapter);
        rank_spinner.setAdapter(rank_spinner_adapter);
        rank_spinner_adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        rank_spinner.setSelection(Constants.Rank.LOW);
        setHints(fieldType);
        getLoaderManager().restartLoader(0, null, this);
        enter_answer.requestFocus();
        changeAreas(question_area_size, answer_area_size);
        if (test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.SMALL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.NORMAL_SIZE);
        } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE) {
            changeTextSize(ChangeTextSizeDialogFragment.LARGE_SIZE);
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        add_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_entry_cardview.startAnimation(animAlpha);
                String question = enter_question.getText().toString();
                String answer = enter_answer.getText().toString();
                if (question.length() > 0 && answer.length() > 0) {
                    if (fromFieldActivity) {

                        if (!databaseOperations.checkEntryDuplication(question, answer, current_field_id, question_data_type)) {
                            mySQLiteCursorLoader.insertEntry(question_data_type, question, answer,
                                    current_field_id, rank_spinner.getSelectedItemPosition());
                        } else {
                            Toast toast = Toast.makeText(AddEntryActivity.this, R.string.already_inserted, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }

                        enter_question.setText("");
                        enter_answer.setText("");
                    } else {
                        if (field_chosen) {
                            if (!databaseOperations.checkEntryDuplication(question, answer, current_field_id, question_data_type)) {
                                mySQLiteCursorLoader.insertEntry(question_data_type, question, answer,
                                        current_field_id, rank_spinner.getSelectedItemPosition());
                            } else {
                                Toast toast = Toast.makeText(AddEntryActivity.this, R.string.already_inserted, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
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
                current_field_id = c.getLong(0);
                fieldType = c.getInt(1);
                setHints(fieldType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showActionbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_actionbar_cardview.startAnimation(animAlpha);
                if (actionBarShown) {
                    getSupportActionBar().hide();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.show);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id, Contract.Field.DO_NOT_SHOW_ACTION_BAR);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.hide);
                    mySQLiteCursorLoader.updateFieldActionBarShown(current_field_id, Contract.Field.SHOW_ACTION_BAR);
                }
            }
        });
/*        enter_question.setOnLongClickListener(new View.OnLongClickListener() {
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
        });*/
        enter_answer.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                enter_answer.setText("");
                enter_answer.requestFocus();
            }

            @Override
            public void onSwipeRight() {
                enter_answer.setText("");
                enter_answer.requestFocus();
            }
        });
        enter_question.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                enter_question.setText("");
                enter_question.requestFocus();
            }

            @Override
            public void onSwipeRight() {
                enter_question.setText("");
                enter_question.requestFocus();
            }
        });

        enter_question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && enter_answer.getText().length() > 0) {
                    add_entry.setEnabled(true);
                } else {
                    add_entry.setEnabled(false);
                }
            }
        });
        enter_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && enter_question.getText().length() > 0) {
                    add_entry.setEnabled(true);
                } else {
                    add_entry.setEnabled(false);
                }
            }
        });

    }

    private void setHints(int fieldType) {
        enter_question.setHint(R.string.enter_a_question);
        enter_answer.setHint(R.string.enter_an_answer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
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
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) showResult.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (enter_question.getTextSize() / scaledDensity));
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(CURRENT_FIELD_ID, current_field_id);
        savedInstanceState.putBoolean(FIELD_CHOSEN, field_chosen);
        savedInstanceState.putInt(SPINNER_POSITION, field_spinner.getSelectedItemPosition());
        saveUserLayoutChanges(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        current_field_id = savedInstanceState.getLong(CURRENT_FIELD_ID);
        field_chosen = savedInstanceState.getBoolean(FIELD_CHOSEN);
        restoreUserLayoutChanges(savedInstanceState);
    }

    private void setSpinnerPosition(long field_id) {
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
             cursor.moveToNext()) {
            long id = cursor.getLong(0);
            if (id == field_id) {
                field_spinner.setSelection(cursor.getPosition());
                return;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MySQLiteCursorLoader(AddEntryActivity.this,
                "SELECT " + Contract.COLUMN_ID + ", " + Contract.Field.COLUMN_FIELD_TYPE + ", " + Contract.Field.COLUMN_FIELD_NAME
                        + " FROM " + Contract.Field.TABLE_NAME
                        + " WHERE " + Contract.Field.COLUMN_FIELD_TYPE + " != ? "
                        + "  ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC",
                new String[]{Constants.FieldTypes.NOTEBOOK_TYPE + ""});
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simple_cursor_adapter.swapCursor(data);
        cursor = data;
        setSpinnerPosition(current_field_id);
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
        changeAreas(bundle.getFloat(QUESTION_LAYOUT), bundle.getFloat(ANSWER_LAYOUT));
        enter_question.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
        enter_answer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }
}
