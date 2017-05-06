package com.ahmedsameha1.peninbin.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.LoaderManager;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.OnSwipeTouchListener;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Ahmed on 3/19/16.
 */
public class AddEntryActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        ActivityUtility.ActivityTimers {
    public static final String SCREEN_NAME = "AddEntryActivity";
    public static final String FROM_FIELD_ACTIVITY = "from_field_activity";
    private static final String FIELD_CHOSEN = "field_chosen";
    private static final String SPINNER_POSITION = "spinner_position";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final String CURRENT_FIELD_ID = "current_field_id";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    public static final int GET_FIELDS_LOADER_ID = 0;
    public static final int GET_SPECIAL_TEXT_ID = 1;
    private static final String TEMP_SHARED_STRING = "temp_shared_string";
    private Spinner field_spinner;
    private EditText enter_question;
    private EditText enter_answer;
    private EditText enter_order;
    private TextView showActionbar;
    private SimpleCursorAdapter simple_cursor_adapter;
    private Chronometer timer;
    private long lastPause;
    private DatabaseReadOperations databaseOperations;
    private boolean actionBarShown;
    private boolean fromFieldActivity;
    private Cursor cursor;
    private boolean field_chosen;
    private LinearLayout showResult, questionLinearlayout, container;
    private long current_field_id;
    private int question_data_type;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private Tracker mTracker;
    private String[] symbol, customable_text;
    private int current_field_type;
    private Handler mHandler = new Handler();
    private String temp_shared_string;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        databaseOperations = new DatabaseReadOperations(this);
        fromFieldActivity = getIntent().getBooleanExtra(FROM_FIELD_ACTIVITY, false);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        if (current_field_id == 0) {
            Cursor cursor = databaseOperations.getFieldList();
            if (cursor.moveToFirst()) {
                current_field_id = cursor.getLong(0);
            } else {
                Toast toast = Toast.makeText(AddEntryActivity.this, R.string.please_create_field, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                finish();
                return;
            }
        }
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(this);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        field_spinner = (Spinner) findViewById(R.id.field_spinner);
        final Spinner rank_spinner = (Spinner) findViewById(R.id.rank_spinner);
        enter_question = (EditText) findViewById(R.id.question);
        enter_answer = (EditText) findViewById(R.id.answer);
        enter_order = (EditText) findViewById(R.id.order);
        final TextView add_entry = (TextView) findViewById(R.id.add_entry);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        showResult = (LinearLayout) findViewById(R.id.answer_linearlayout);
        questionLinearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        final CardView show_actionbar_cardview = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView add_entry_cardview = (CardView) findViewById(R.id.add_entry_card_view);
        timer = (Chronometer) findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == GET_FIELDS_LOADER_ID) {
                    return new CursorLoaderAndUpdateDatabase(AddEntryActivity.this,
                            "SELECT " + Contract.COLUMN_ID + ", " + Contract.Field.COLUMN_FIELD_TYPE + ", " + Contract.Field.COLUMN_FIELD_NAME
                                    + " FROM " + Contract.Field.TABLE_NAME
                                    + " ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC",
                            null);
                } else if (id == GET_SPECIAL_TEXT_ID) {
                    return new CursorLoaderAndUpdateDatabase(AddEntryActivity.this, "SELECT ts." + Contract.COLUMN_ID + ", s."
                            + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                            + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                            " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                            + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ?",
                            new String[]{current_field_type + ""});
                }
                return null;
            }

            @Override
            public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
                if (loader.getId() == GET_FIELDS_LOADER_ID) {
                    simple_cursor_adapter.swapCursor(data);
                    cursor = data;
                    setSpinnerPosition(current_field_id);
                } else if (loader.getId() == GET_SPECIAL_TEXT_ID) {
                    if (symbol == null && customable_text == null) {
                        symbol = new String[data.getCount()];
                        customable_text = new String[data.getCount()];
                        for (data.moveToFirst(); data.getPosition() < data.getCount();
                             data.moveToNext()) {
                            symbol[data.getPosition()] = data.getString(1);
                            customable_text[data.getPosition()] = data.getString(2);
                        }

                    }
                }
            }

            @Override
            public void onLoaderReset(android.content.Loader<Cursor> loader) {
                if (loader.getId() == GET_FIELDS_LOADER_ID) {
                    simple_cursor_adapter.swapCursor(null);
                }
            }
        };


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
        setHints();
        getLoaderManager().restartLoader(GET_FIELDS_LOADER_ID, null, loader);
        getLoaderManager().restartLoader(GET_SPECIAL_TEXT_ID, null, loader);
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
        add_entry.setOnClickListener(view -> {
            add_entry_cardview.startAnimation(animAlpha);
            String question = enter_question.getText().toString();
            String answer = enter_answer.getText().toString();
            int order = "".equals(enter_order.getText().toString()) ? -1 : Integer.parseInt(enter_order.getText().toString());
            if (question.length() > 0 && answer.length() > 0) {
                if (fromFieldActivity) {

                    if (!databaseOperations.checkEntryDuplication(question, answer, current_field_id, question_data_type)) {
                        mySQLiteCursorLoader.insertEntry(question_data_type, question, answer,
                                current_field_id, rank_spinner.getSelectedItemPosition(), order);
                    } else {
                        Toast toast = Toast.makeText(AddEntryActivity.this, R.string.already_inserted, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }

                    enter_question.setText("");
                    enter_answer.setText("");
                    enter_order.setText("");
                } else {
                    if (field_chosen) {
                        if (!databaseOperations.checkEntryDuplication(question, answer, current_field_id, question_data_type)) {
                            mySQLiteCursorLoader.insertEntry(question_data_type, question, answer,
                                    current_field_id, rank_spinner.getSelectedItemPosition(), order);
                        } else {
                            Toast toast = Toast.makeText(AddEntryActivity.this, R.string.already_inserted, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }

                        enter_question.setText("");
                        enter_answer.setText("");
                        enter_order.setText("");
                    } else {
                        Toast toast = Toast.makeText(AddEntryActivity.this, R.string.select_field, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                }
                enter_question.requestFocus();
            } else {
                Toast toast = Toast.makeText(AddEntryActivity.this, R.string.both_question_answer, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });

        field_spinner.setOnTouchListener((v, event) -> {
            field_chosen = true;
            return false;
        });
        field_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getSelectedItem();
                current_field_id = c.getLong(0);
                current_field_type = c.getInt(1);
                getLoaderManager().restartLoader(GET_SPECIAL_TEXT_ID, null, loader);// TODO: 5/4/17 test this 
                setHints();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showActionbar.setOnClickListener(view -> {
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
        });
        enter_answer.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                enter_answer.setText("");
                enter_answer.requestFocus();
            }

            @Override
            public void onSwipeRight() {
                if ( temp_shared_string != null ) {
                    EditText editText = (EditText) view;
                    int sel = editText.getSelectionEnd();
                    if ( sel == -1 ) {
                        editText.setText(editText.getText() + temp_shared_string);
                    } else {
                        editText.setText(editText.getText().toString().substring(0,sel)
                                + temp_shared_string + editText.getText().toString().substring(sel));
                    }
                }
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
                if ( temp_shared_string != null ) {
                    EditText editText = (EditText) view;
                    int sel = editText.getSelectionEnd();
                    if ( sel == -1 ) {
                        editText.setText(editText.getText() + temp_shared_string);
                    } else {
                        editText.setText(editText.getText().toString().substring(0,sel)
                                + temp_shared_string + editText.getText().toString().substring(sel));
                    }
                }
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
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = editable.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enter_question.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enter_question.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                            questionLinearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                                questionLinearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
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
                if (symbol != null && customable_text != null) {
                    for (int i = 0; i < symbol.length; i++) {
                        String text = editable.toString();
                        if (customable_text[i] != null && text.contains(customable_text[i])) {
                            enter_answer.setText(text.replaceAll(customable_text[i], symbol[i]));
                            enter_answer.setSelection(text.lastIndexOf(customable_text[i]) + symbol[i].length());
                            questionLinearlayout.setBackgroundResource(R.drawable.move_next_notification_background);
                            questionLinearlayout.setPadding(0, 0, 0, 0);
                            Runnable mRunnable = () -> {
                                questionLinearlayout.setBackgroundResource(R.drawable.show_question_background);
                                questionLinearlayout.setPadding(0, 0, 0, 0);
                            };
                            mHandler.postDelayed(mRunnable, 50);
                        }
                    }
                }
            }
        });
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                temp_shared_string = sharedText;
                if (sharedText != null) {
                    enter_question.setText(sharedText);
                }
            }
        }
    }

    private void setHints() {
        enter_question.setHint(R.string.enter_a_question);
        enter_answer.setHint(R.string.enter_an_answer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.setBase(SystemClock.elapsedRealtime() - lastPause);
        timer.start();
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) + SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        lastPause = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
        super.onPause();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    temp_shared_string = sharedText;
                    if ( enter_question.getText().length() == 0 && enter_question.getText().length() == 0 )  {
                        enter_question.setText(sharedText);
                    } else if ( enter_question.getText().length() == 0 ) {
                        enter_question.setText(sharedText);
                    } else if ( enter_answer.getText().length() == 0 ) {
                        enter_answer.setText(sharedText);
                    }
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
        temp_shared_string = null;
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
        savedInstanceState.putLong(TIMER_LAST_PAUSE, lastPause);
        savedInstanceState.putStringArray(SYMBOL, symbol);
        savedInstanceState.putStringArray(CUSTOMABLE_TEXT, customable_text);
        savedInstanceState.putString(TEMP_SHARED_STRING, temp_shared_string);
        saveUserLayoutChanges(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        current_field_id = savedInstanceState.getLong(CURRENT_FIELD_ID);
        field_chosen = savedInstanceState.getBoolean(FIELD_CHOSEN);
        lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        customable_text = savedInstanceState.getStringArray(CUSTOMABLE_TEXT);
        symbol = savedInstanceState.getStringArray(SYMBOL);
        temp_shared_string = savedInstanceState.getString(TEMP_SHARED_STRING);
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

    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {

    }
}
