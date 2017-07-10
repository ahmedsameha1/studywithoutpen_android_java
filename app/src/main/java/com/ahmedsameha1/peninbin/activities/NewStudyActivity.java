package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.StudyFragmentsTabAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TimerDialogFragment;
import com.ahmedsameha1.peninbin.fragments.RetainedFragment;
import com.ahmedsameha1.peninbin.fragments.StudyEntryFragment;
import com.ahmedsameha1.peninbin.fragments.TestEntryFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.SortOptions;

import java.util.List;

public class NewStudyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
     ChangeAreasDialogFragment.ChangeAreas,
        ChangeTextSizeDialogFragment.ChangeTextSize,
        ActivityUtility.ActivityTimers {
    public static final String SCREEN_NAME = "StudyActivity";
    public static final String CURRENT_FIELD_ID = "currentField";
    public static final String ENTRY_ID = "entry_id";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String CORRECT_ANSWERS_COUNTER = "correctAnswerCounter";
    public static final String QUESTION_LAYOUT = "user_defined_question_layout";
    public static final String ANSWER_LAYOUT = "user_defined_answer_layout";
    public static final String TEXT_SIZE = "user_defined_question_text_size";
    private static final int MY_DATA_CHECK_CODE = 1234;
    public static final int SELECT_ENTRY_REQUEST = 1;
    public static final int RETURN_TIME = 1000;
    private static final String CURRENT_ENTRY_ID = "current_entry_id";
    private static final String TIMER_LAST_PAUSE = "timer_last_pause";
    private static final String SYMBOL = "symbol";
    private static final String CUSTOMABLE_TEXT = "customable_text";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int GET_SPECIAL_TEXT_LOADER_ID = 1;
    public static final String SEARCH_VALUE = "search_value";
    public static final String DATA = "data";
    public static final String CURRENT_INDEX = "current_index";
    private long current_field_id;
    private long current_entry_id;
    private String current_question;
    private String current_answer;
    private String search_value;
    private int search_what;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private DatabaseReadOperations databaseReadOperations;
    private SortOptions sortOption;
    private ViewPager mViewPager;
    private String[][] entries;
    private StringBuilder current_index;
    private View mLoadingView;
    private RetainedFragment retainedFragment;

    private int question_area_size, answer_area_size, test_text_size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_study);
        Intent intent = getIntent();
        current_field_id = intent.getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        current_entry_id = intent.getLongExtra(StudyActivity.ENTRY_ID, 0);
        current_question = intent.getStringExtra(StudyActivity.CURRENT_QUESTION);
        current_answer = intent.getStringExtra(StudyActivity.CURRENT_ANSWER);
        search_value = intent.getStringExtra(SEARCH_VALUE);
        search_what = intent.getIntExtra(SearchActivity.SEARCH_WHAT, 0);
        databaseReadOperations = new DatabaseReadOperations(this);
        sortOption = databaseReadOperations.getFieldSort(current_field_id);
        question_area_size = databaseReadOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseReadOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseReadOperations.getFieldTestTextSize(current_field_id);
        TabLayout tableLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(new StudyFragmentsTabAdapter(getSupportFragmentManager()));
        tableLayout.setupWithViewPager(mViewPager);
        mLoadingView = findViewById(R.id.loading);
        mViewPager.setVisibility(View.GONE);
        retainedFragment = (RetainedFragment) getFragmentManager().findFragmentByTag(DATA);
        // if the reference to the fragment was null we create a new fragment object and add it to the activity
        if (retainedFragment == null) {
            retainedFragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(retainedFragment, DATA).commit();
        }
        if ( savedInstanceState != null ) {
            entries = retainedFragment.getStudy_activity_entries();
            current_index = (StringBuilder) savedInstanceState.getSerializable(CURRENT_INDEX);
            mViewPager.setAdapter(new StudyFragmentsTabAdapter(this, entries, current_index, current_field_id));
            changeAreas(question_area_size, answer_area_size);
            ActivityUtility.crossFade(mViewPager,mLoadingView, this);
        } else {
            getLoaderManager().restartLoader(0, null, this);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // ordinary load of field entries
        if (search_value == null) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                            + "ORDER BY " + sortOption.getSortString(),
                    new String[]{current_field_id + ""});
        } else if (search_what == SearchActivity.SEARCH_QUESTION) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  q LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{String.valueOf(current_field_id), search_value});
        } else if (search_what == SearchActivity.SEARCH_ANSWER) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  a LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{String.valueOf(current_field_id), search_value});
        }
        // ordinary search
        else {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  q LIKE ? "
                            + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + " a LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{current_field_id + "", "%" + search_value + "%", current_field_id + "", "%" + search_value + "%"});
        }
        return cursorLoaderAndUpdateDatabase;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        entries = new String[cursor.getCount()][2];
        UIHandler uihandler = new UIHandler(this);
        new Thread(() -> {
            for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                 cursor.moveToNext()) {
                int position = cursor.getPosition();
                entries[position][0] = cursor.getString(1);
                entries[position][1] = cursor.getString(2);
                if ( cursor.getLong(0) == current_entry_id) {
                    current_index = new StringBuilder(String.valueOf(position));
                }
            }
            retainedFragment.setStudy_activity_entries(entries);
            uihandler.sendMessage(Message.obtain());
        }).start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_INDEX, current_index);
    }

    private static class UIHandler extends Handler {
        private NewStudyActivity newStudyActivity;

        public UIHandler( NewStudyActivity newStudyActivity) {
            this.newStudyActivity = newStudyActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            newStudyActivity.mViewPager.setAdapter(new StudyFragmentsTabAdapter
                    (newStudyActivity, newStudyActivity.entries, newStudyActivity.current_index,newStudyActivity.current_field_id));
            newStudyActivity.changeAreas(newStudyActivity.question_area_size, newStudyActivity.answer_area_size);
            ActivityUtility.crossFade(newStudyActivity.mViewPager, newStudyActivity.mLoadingView, newStudyActivity);
        }
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
        question_area_size = databaseReadOperations.getFieldQuestionAreaSize(current_field_id);
        answer_area_size = databaseReadOperations.getFieldAnswerAreaSize(current_field_id);
        test_text_size = databaseReadOperations.getFieldTestTextSize(current_field_id);
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.change_areas:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                bundle.putFloat(QUESTION_LAYOUT, question_area_size);
                bundle.putFloat(ANSWER_LAYOUT, answer_area_size);
                ChangeAreasDialogFragment changeAreasDialogFragment = new ChangeAreasDialogFragment();
                changeAreasDialogFragment.setArguments(bundle);
                changeAreasDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;
            case R.id.change_text_size:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                if (test_text_size == ChangeTextSizeDialogFragment.SAVE_SMALL_SIZE) {
                    bundle1.putFloat(TEXT_SIZE, ChangeTextSizeDialogFragment.SMALL_SIZE);
                } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_NORMAL_SIZE) {
                    bundle1.putFloat(TEXT_SIZE, ChangeTextSizeDialogFragment.NORMAL_SIZE);
                } else if (test_text_size == ChangeTextSizeDialogFragment.SAVE_LARGE_SIZE) {
                    bundle1.putFloat(TEXT_SIZE, ChangeTextSizeDialogFragment.LARGE_SIZE);
                }
                ChangeTextSizeDialogFragment changeTextSizeDialogFragment = new ChangeTextSizeDialogFragment();
                changeTextSizeDialogFragment.setArguments(bundle1);
                changeTextSizeDialogFragment.show(getSupportFragmentManager(), ChangeAreasDialogFragment.CHANGE_AREAS_DIALOG_FRAGMENT);
                return true;

            case R.id.timer:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                            .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putString(TimerDialogFragment.MESSAGE, getString(R.string.app_name) + " " + getString(R.string.study));
                        TimerDialogFragment timerDialogFragment = new TimerDialogFragment();
                        timerDialogFragment.setArguments(bundle4);
                        timerDialogFragment.show(getSupportFragmentManager(), TimerDialogFragment.class.getName());
                    } else {
                        Toast toast = Toast.makeText(this, R.string.no_timer_app, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(this, R.string.kitkat_or_newer, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void changeAreas(float questionArea, float answerArea) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for ( Fragment fragment: fragments ) {
            if ( fragment instanceof StudyEntryFragment ) {
                ((StudyEntryFragment) fragment).changeAreas(questionArea, questionArea);
            }
            if ( fragment instanceof TestEntryFragment ) {
                ((TestEntryFragment) fragment).changeAreas(questionArea, questionArea);
            }
        }
    }
    @Override
    public void changeTextSize(float textSize) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for ( Fragment fragment: fragments ) {
            if ( fragment instanceof StudyEntryFragment ) {
                ((StudyEntryFragment) fragment).changeTextSize(textSize);
            }
            if ( fragment instanceof TestEntryFragment ) {
                ((TestEntryFragment) fragment).changeTextSize(textSize);
            }
        }
    }
    @Override
    public void manageTimersDueDialogsShow(boolean isCounting) {

    }
}
