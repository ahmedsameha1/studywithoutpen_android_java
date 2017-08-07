package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.StudyFragmentsTabAdapter;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.SortOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class StudyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ENTRY_ID = "entry_id";
    public static final String SEARCH_VALUE = "search_value";
    public static final String DATA = "data";
    public static final String CURRENT_INDEX = "current_index";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int PREPARE_WRONG_LIST_LOADER_ID = 2;
    public static final int PREPARE_FIELD_LIST_LOADER_ID = 3;
    public static final int SHOW_LIST_LOADER_ID = 4;
    public static final String SYMBOL = "symbol";
    public static final String CUSTOMABLE_TEXT = "customable_text";
    public static final String STUDY_WHAT = "study_what";
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    public static final int NORMAL = 0;
    public static final int WRONG = 1;
    private static final String LIST_OF_ENTRIES = "list_of_entries";
    private static StudyActivity studyActivity;
    private long current_field_id;
    private long current_entry_id;
    private String search_value;
    private int search_what;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private DatabaseReadOperations databaseReadOperations;
    private SortOptions sortOption;
    private ViewPager mViewPager;
    private String[][] entries;
    private StringBuilder current_index;
    private View mLoadingView;
    private int study_what;
    private ArrayList<WrongItem> wrongItemArrayList;
    private Cursor cursor;
    private LoaderManager.LoaderCallbacks<Object[]> prepare_field_list_loader = new LoaderManager.LoaderCallbacks<Object[]>() {
        @Override
        public Loader<Object[]> onCreateLoader(int i, Bundle bundle) {
            return new StudyActivity.PrepareFieldList(StudyActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<Object[]> loader, Object[] list ) {
            entries = (String[][]) list[0];
            current_index = (StringBuilder) list[1];
            mViewPager.setAdapter(new StudyFragmentsTabAdapter
                    (StudyActivity.this, entries,
                            current_index, current_field_id));
            ActivityUtility.crossFade(mViewPager, mLoadingView, StudyActivity.this);
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<Object[]> loader) {

        }
    };
    private LoaderManager.LoaderCallbacks<Object[]> prepare_wrong_list_loader = new LoaderManager.LoaderCallbacks<Object[]>() {
        @Override
        public Loader<Object[]> onCreateLoader(int i, Bundle bundle) {
            return new StudyActivity.PrepareWrongList(StudyActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<Object[]> loader, Object[] list ) {
            entries = (String[][]) list[0];
            current_index = (StringBuilder) list[1];
            mViewPager.setAdapter(new StudyFragmentsTabAdapter
                    (StudyActivity.this, entries,
                            current_index, current_field_id));
            ActivityUtility.crossFade(mViewPager, mLoadingView, StudyActivity.this);
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<Object[]> loader) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_study);
        studyActivity = this;
        Intent intent = getIntent();
        current_field_id = intent.getLongExtra(Constants.CURRENT_FIELD_ID, 0);
        current_entry_id = intent.getLongExtra(ENTRY_ID, 0);
        search_value = intent.getStringExtra(SEARCH_VALUE);
        search_what = intent.getIntExtra(SearchActivity.SEARCH_WHAT, 0);
        study_what = intent.getIntExtra(STUDY_WHAT, NORMAL);
        databaseReadOperations = new DatabaseReadOperations(this);
        sortOption = databaseReadOperations.getFieldSort(current_field_id);
        TabLayout tableLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.container);
        tableLayout.setupWithViewPager(mViewPager);
        mLoadingView = findViewById(R.id.loading);
        mViewPager.setVisibility(View.GONE);
        if ( savedInstanceState != null) {
            entries = (String[][]) savedInstanceState.getSerializable(LIST_OF_ENTRIES);
            if ( entries != null) {
                current_index = (StringBuilder) savedInstanceState.getSerializable(CURRENT_INDEX);
                mViewPager.setAdapter(new StudyFragmentsTabAdapter(this, entries, current_index, current_field_id));
                ActivityUtility.crossFade(mViewPager,mLoadingView, this);
            } else {
                if ( study_what == NORMAL ) {
                    if ( getLoaderManager().getLoader(PREPARE_FIELD_LIST_LOADER_ID) != null ) {
                        getLoaderManager().initLoader(PREPARE_FIELD_LIST_LOADER_ID, null, prepare_field_list_loader);
                    } else {
                        getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, this);
                    }
                } else if ( study_what == WRONG ) {
                    getLoaderManager().initLoader(PREPARE_WRONG_LIST_LOADER_ID, null, prepare_wrong_list_loader);
                }
            }
        } else {
            if ( study_what == NORMAL ) {
                getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, this);
            } else if ( study_what == WRONG ) {
                wrongItemArrayList = (ArrayList<WrongItem>) intent.getBundleExtra(WRONG_ITEM_LIST).getSerializable(WRONG_ITEM_LIST);
                getLoaderManager().initLoader(PREPARE_WRONG_LIST_LOADER_ID, null, prepare_wrong_list_loader);
            }
        }
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (Fragment fragment: getSupportFragmentManager ().getFragments() ) {
                    ( (Contact) fragment).changeCurrentTab();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if ( id == GET_ENTRIES_LOADER_ID ) {

                if (search_value == null) {
                    // ordinary load of field entries
                    if (search_what != SearchActivity.TODAY_ENTRIES) {
                        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                        + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                                        + "ORDER BY " + sortOption.getSortString(),
                                new String[]{String.valueOf(current_field_id)});
                    } else {
                        if (!Emulation.isAnyThreadRunning()) {
                            Calendar today_calendar = Calendar.getInstance();
                            today_calendar.setTimeInMillis(System.currentTimeMillis());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Emulation.DATE_FORMAT_SQLITE, Locale.US);
                            if ((databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED)) {
                                today_calendar.set(Calendar.HOUR_OF_DAY, 0);
                                today_calendar.set(Calendar.MINUTE, 5);
                                today_calendar.set(Calendar.SECOND, 0);
                                String today_emulation_date_string = simpleDateFormat.format(today_calendar.getTime());
                                cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                                        "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                                + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                                + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                                + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                                + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                                + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.String_Entry.COLUMN_CREATION_EMULATED_DATE + " = ? " + " ORDER BY " + sortOption.getSortString(),
                                        new String[]{String.valueOf(current_field_id), today_emulation_date_string});
                            } else {
                                String end_date_string = simpleDateFormat.format(today_calendar.getTime());
                                today_calendar.set(Calendar.HOUR_OF_DAY, 0);
                                today_calendar.set(Calendar.MINUTE, 0);
                                today_calendar.set(Calendar.SECOND, 0);
                                String start_date_string = simpleDateFormat.format(today_calendar.getTime());
                                cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this,
                                        "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                                + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                                + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                                + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                                + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                                + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND se." + Contract.CREATION_DATE + " >= datetime(?) AND se."
                                                + Contract.CREATION_DATE + " < datetime(?) "
                                                + " ORDER BY " + sortOption.getSortString(),
                                        new String[]{String.valueOf(current_field_id), start_date_string, end_date_string});
                            }
                        } else {
                            Toast toast = Toast.makeText(this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            finish();
                        }
                    }
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if ( loader.getId() == GET_ENTRIES_LOADER_ID ) {
            this.cursor = cursor;
            getLoaderManager().initLoader(PREPARE_FIELD_LIST_LOADER_ID, null, prepare_field_list_loader);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(CURRENT_INDEX, current_index);
        outState.putSerializable(LIST_OF_ENTRIES, entries);
    }

    private static class PrepareWrongList extends AsyncTaskLoader<Object[]> {

        public PrepareWrongList(Context context) {
            super(context);
        }

        @Override
        public Object[] loadInBackground() {
            String[][] entries = new String[studyActivity.wrongItemArrayList.size()][2];
            StringBuilder current_index = new StringBuilder();
            for ( int i = 0; i < studyActivity.wrongItemArrayList.size(); i++ ) {
                entries[i][0] = studyActivity.wrongItemArrayList.get(i).getQuestion();
                entries[i][1] = studyActivity.wrongItemArrayList.get(i).getAnswer();
                if ( studyActivity.wrongItemArrayList.get(i).getEntry_id() == studyActivity.current_entry_id) {
                    current_index = new StringBuilder(String.valueOf(i));
                }
            }
            return new Object[]{entries, current_index};
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
    private static class PrepareFieldList extends AsyncTaskLoader<Object[]> {

        public PrepareFieldList(Context context) {
            super(context);
        }

        @Override
        public Object[] loadInBackground() {
            String[][] entries = new String[studyActivity.cursor.getCount()][2];
            StringBuilder current_index = new StringBuilder();
            for (studyActivity.cursor.moveToFirst(); studyActivity.cursor.getPosition() < studyActivity.cursor.getCount();
                 studyActivity.cursor.moveToNext()) {
                int position = studyActivity.cursor.getPosition();
                entries[position][0] = studyActivity.cursor.getString(1);
                entries[position][1] = studyActivity.cursor.getString(2);
                if (studyActivity.cursor.getLong(0) == studyActivity.current_entry_id) {
                    current_index = new StringBuilder(String.valueOf(position));
                }
            }
            return new Object[]{entries, current_index};
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public interface Contact {
        void changeCurrentTab();
    }
}
