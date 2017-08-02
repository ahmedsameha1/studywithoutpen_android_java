package com.ahmedsameha1.peninbin.activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.OnSwipeTouchListener;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Ahmed on 3/25/16.
 */
public class EditEntryActivity extends AppCompatActivity implements
        ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        ActivityUtility.ActivityTimers {
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String SCREEN_NAME = "EditEntryActivity";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    public static final String ENTRY_ID = "entry_id";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    private EditText editQuestion;
    private EditText editAnswer;
    private EditText enter_order;
    private TextView showActionbar;
    private DatabaseReadOperations databaseOperations;
    private boolean actionBarShown;
    private TextView save;
    private long current_field_id;
    private String currentQuestion;
    private String currentAnswer;
    private LinearLayout answer_linearlayout, question_linearlayout, container;
    private long entry_id;
    private int question_data_type, rank;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private Tracker mTracker;
    private Chronometer timer;
    private long lastPause;
    private int order;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);
        int question_area_size, answer_area_size, test_text_size, action_bar_shown;
        current_field_id = getIntent().getLongExtra(Constants.CURRENT_FIELD_ID, 0);
        entry_id = getIntent().getLongExtra(ENTRY_ID, 0);
        question_data_type = getIntent().getIntExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE, 0);
        currentQuestion = getIntent().getStringExtra(CURRENT_QUESTION);
        currentAnswer = getIntent().getStringExtra(CURRENT_ANSWER);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        databaseOperations = new DatabaseReadOperations(this);
        rank = databaseOperations.getEntryRank(entry_id);
        order = databaseOperations.getEntryOrder(entry_id);
        action_bar_shown = databaseOperations.getFieldActionBarShown(current_field_id);
        question_area_size = databaseOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseOperations.getFieldTestTextSize(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        showActionbar = (TextView) findViewById(R.id.show_actionbar);
        save = (TextView) findViewById(R.id.save);
        final Spinner rank_spinner = (Spinner) findViewById(R.id.rank_spinner);
        editQuestion = (EditText) findViewById(R.id.question);
        editAnswer = (EditText) findViewById(R.id.answer);
        enter_order = (EditText) findViewById(R.id.order);
        answer_linearlayout = (LinearLayout) findViewById(R.id.answer_linearlayout);
        question_linearlayout = (LinearLayout) findViewById(R.id.question_linearlayout);
        container = (LinearLayout) findViewById(R.id.container);
        final CardView show_actionbar_card_view = (CardView) findViewById(R.id.show_actionbar_card_view);
        final CardView save_card_view = (CardView) findViewById(R.id.save_card_view);
        timer = (Chronometer)findViewById(R.id.chronometer);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        setSupportActionBar(toolbar);
        if (action_bar_shown == Contract.Field.DO_NOT_SHOW_ACTION_BAR) {
            getSupportActionBar().hide();
        }
        actionBarShown = getSupportActionBar().isShowing();
        showActionbar.setText(actionBarShown ? R.string.hide : R.string.show);
        final ArrayAdapter rank_spinner_adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.rank));
        rank_spinner.setAdapter(rank_spinner_adapter);
        rank_spinner_adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        rank_spinner.setSelection(rank);
        editAnswer.setText(currentAnswer);
        editQuestion.setText(currentQuestion);
        enter_order.setText(order == -1 ? "" : String.valueOf(order));
        editAnswer.requestFocus();
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
        showActionbar.setOnClickListener( view ->  {
                show_actionbar_card_view.startAnimation(animAlpha);
                if (actionBarShown) {
                    getSupportActionBar().hide();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.show);
                    cursorLoaderAndUpdateDatabase.updateFieldActionBarShown(current_field_id, Contract.Field.DO_NOT_SHOW_ACTION_BAR);
                } else {
                    getSupportActionBar().show();
                    actionBarShown = (!actionBarShown);
                    showActionbar.setText(R.string.hide);
                    cursorLoaderAndUpdateDatabase.updateFieldActionBarShown(current_field_id, Contract.Field.SHOW_ACTION_BAR);
                }
        });
        save.setOnClickListener( view -> {
            try {
                save_card_view.startAnimation(animAlpha);
                String editedQuestion = editQuestion.getText().toString().trim();
                String editedAnswer = editAnswer.getText().toString().trim();
            int edited_order = "".equals(enter_order.getText().toString()) ? 999999999 : Integer.parseInt(enter_order.getText().toString());

            if (editedQuestion.length() > 0 && editedAnswer.length() > 0) {
                            editQuestion.setEnabled(false);
                            editAnswer.setEnabled(false);
                            if (!editedQuestion.equals(currentQuestion) || !editedAnswer.equals(currentAnswer)
                                    || rank != rank_spinner.getSelectedItemPosition() || edited_order != order ) {
                                if (!editedQuestion.equals(currentQuestion) || !editedAnswer.equals(currentAnswer)) {
                                    if (!databaseOperations.checkEntryDuplication(editedQuestion, editedAnswer, current_field_id, question_data_type)) {
                                        //                                    cursorLoaderAndUpdateDatabase.editEntry(entry_id,question_data_type,editedQuestion,editedAnswer);
                                        Intent intent = new Intent();
                                        intent.putExtra(FieldActivity.EDITED_ENTRY_ID, entry_id);
                                        intent.putExtra(FieldActivity.EDITED_QUESTION, editedQuestion);
                                        intent.putExtra(FieldActivity.EDITED_ANSWER, editedAnswer);
                                        intent.putExtra(FieldActivity.EDITED_RANK, rank_spinner.getSelectedItemPosition());
                                        intent.putExtra(FieldActivity.EDITED_ORDER, edited_order);
                                        setResult(RESULT_OK, intent);
                                    } else {
                                        Toast toast = Toast.makeText(EditEntryActivity.this, R.string.already_inserted, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra(FieldActivity.EDITED_ENTRY_ID, entry_id);
                                    intent.putExtra(FieldActivity.EDITED_QUESTION, editedQuestion);
                                    intent.putExtra(FieldActivity.EDITED_ANSWER, editedAnswer);
                                    intent.putExtra(FieldActivity.EDITED_RANK, rank_spinner.getSelectedItemPosition());
                                    intent.putExtra(FieldActivity.EDITED_ORDER, edited_order);
                                    setResult(RESULT_OK, intent);
                                }
                            }

                    finish();
                } else {
                    Toast toast = Toast.makeText(EditEntryActivity.this, R.string.something_both_question_answer, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            } catch ( NumberFormatException nfe ) {
                Toast toast = Toast.makeText(EditEntryActivity.this, R.string.not_allowed_number, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        editQuestion.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                editQuestion.setText("");
                editQuestion.requestFocus();
            }

            @Override
            public void onSwipeRight() {
                editQuestion.setText("");
                editQuestion.requestFocus();
            }
        });
        editAnswer.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                editAnswer.setText("");
                editAnswer.requestFocus();
            }

            @Override
            public void onSwipeRight() {
                editAnswer.setText("");
                editAnswer.requestFocus();
            }
        });
        editQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && editAnswer.getText().length() > 0) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }
        });
        editAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && editQuestion.getText().length() > 0) {
                    save.setEnabled(true);
                } else {
                    save.setEnabled(false);
                }
            }
        });
    }

    private void setHints(int fieldType) {

                editQuestion.setHint(R.string.enter_a_question);
                editAnswer.setHint(R.string.enter_an_answer);

    }

    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
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
                bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) question_linearlayout.getLayoutParams()).weight);
                bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) answer_linearlayout.getLayoutParams()).weight);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                float scaledDensity = this.getResources().getDisplayMetrics().scaledDensity;
                bundle1.putFloat(TEXT_SIZE, (editQuestion.getTextSize() / scaledDensity));
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.setBase( SystemClock.elapsedRealtime() - lastPause);
        timer.start();
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) +SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        lastPause = SystemClock.elapsedRealtime() - timer.getBase();
        timer.stop();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(TIMER_LAST_PAUSE,lastPause);
        saveUserLayoutChanges(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastPause = savedInstanceState.getLong(TIMER_LAST_PAUSE);
        restoreUserLayoutChanges(savedInstanceState);
    }

    @Override
    public void changeAreas(float questionArea, float answerArea) {
        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) question_linearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) answer_linearlayout.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        question_linearlayout.setLayoutParams(question_params);
        answer_linearlayout.setLayoutParams(answer_params);
    }

    @Override
    public void changeTextSize(float textSize) {
        editQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        editAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private void saveUserLayoutChanges(Bundle bundle) {
        bundle.putFloat(QUESTION_LAYOUT, ((LinearLayout.LayoutParams) question_linearlayout.getLayoutParams()).weight);
        bundle.putFloat(ANSWER_LAYOUT, ((LinearLayout.LayoutParams) answer_linearlayout.getLayoutParams()).weight);
        bundle.putFloat(TEXT_SIZE, editQuestion.getTextSize());
    }

    private void restoreUserLayoutChanges(Bundle bundle) {
        changeAreas(bundle.getFloat(QUESTION_LAYOUT), bundle.getFloat(ANSWER_LAYOUT));
        editQuestion.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
        editAnswer.setTextSize(TypedValue.COMPLEX_UNIT_PX, bundle.getFloat(TEXT_SIZE));
    }

    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {

    }
}
