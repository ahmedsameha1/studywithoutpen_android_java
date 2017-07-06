package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.StudyFragmentsTabAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.SortOptions;

import java.util.LinkedHashMap;

public class NewStudyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String SEARCH_VALUE = "search_value";
    public static final String DATA = "data";
    private static NewStudyActivity newStudyActivity;
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
    private LinkedHashMap<String, String[]> list_of_entries;
    private View mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_study);
        newStudyActivity = this;
        Intent intent = getIntent();
        current_field_id = intent.getLongExtra(StudyActivity.CURRENT_FIELD_ID, 0);
        current_entry_id = intent.getLongExtra(StudyActivity.ENTRY_ID, 0);
        current_question = intent.getStringExtra(StudyActivity.CURRENT_QUESTION);
        current_answer = intent.getStringExtra(StudyActivity.CURRENT_ANSWER);
        search_value = intent.getStringExtra(SEARCH_VALUE);
        search_what = intent.getIntExtra(SearchActivity.SEARCH_WHAT, 0);
        databaseReadOperations = new DatabaseReadOperations(this);
        sortOption = databaseReadOperations.getFieldSort(current_field_id);
        TabLayout tableLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(new StudyFragmentsTabAdapter(getSupportFragmentManager()));
        tableLayout.setupWithViewPager(mViewPager);
        getLoaderManager().restartLoader(0, null, this);
        mLoadingView = findViewById(R.id.loading);
        mViewPager.setVisibility(View.GONE);
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
        list_of_entries = new LinkedHashMap<String, String[]>();
        UIHandler uihandler = new UIHandler();
        new Thread(() -> {
            for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount();
                 cursor.moveToNext()) {
                list_of_entries.put(cursor.getString(0), new String[]{cursor.getString(1), cursor.getString(2)});
            }
            uihandler.sendMessage(Message.obtain());
        }).start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            newStudyActivity.mViewPager.setAdapter(new StudyFragmentsTabAdapter(newStudyActivity.getSupportFragmentManager(), newStudyActivity.list_of_entries));
            ActivityUtility.crossFade(newStudyActivity.mViewPager, newStudyActivity.mLoadingView, newStudyActivity);
        }
    }
}
