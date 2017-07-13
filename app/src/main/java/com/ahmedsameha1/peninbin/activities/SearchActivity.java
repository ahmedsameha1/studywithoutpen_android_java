package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.FieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.SortOptions;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by ahmed on 3/2/2016.
 */
public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DeleteEntryDialogFragment.Contact,
        FieldActivityRecyclerViewAdapter.Contact {
    public static final String ENTRY_ID = "entry_id";
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    public static final String SCREEN_NAME = "SearchActivity";
    public static final int SEARCH_LOADER = 0;
    public static final String EDITED_ENTRY_ID = "edited_entry_id";
    public static final String EDITED_QUESTION = "edited_question";
    public static final String EDITED_ANSWER = "edited_answer";
    public static final String EDITED_RANK = "edited_rank";
    public static final String EDITED_ORDER = "edited_order";
    public static final int EDIT_ENTRY = 0;
    private static final String QUERY_KEY = "query_key";
    private static final String CURRENT_FIELD_ID = "current_field_id";
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    public static final int SEARCH_QUESTION = 1;
    public static final int SEARCH_ANSWER = 2;
    public static final String SEARCH_WHAT = "search_what";
    public static final String SEARCH_VALUE = "search_value";
    public FieldActivityRecyclerViewAdapter mAdapter;
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private SortOptions sortOption;
    private Tracker mTracker;
    private StringBuilder search_string_highlight = new StringBuilder();
    private int question_data_type;
    private SearchView searchView;
    private TextView result_count;
    private int search_what;
    private RecyclerView recyclerView;
    private TextView not_found;
    private View mLoadingView;
    private RelativeLayout content;
    private Parcelable recyclerview_position;
    private String search_value;
    private MenuItem searchItem;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (savedInstanceState != null) {
            current_field_id = savedInstanceState.getLong(CURRENT_FIELD_ID);
            search_what = savedInstanceState.getInt(SEARCH_WHAT);
            recyclerview_position = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
            search_value = savedInstanceState.getString(SEARCH_VALUE);
        } else {
            current_field_id = getIntent().getLongExtra(Constants.CURRENT_FIELD_ID, 0);
            search_what = getIntent().getIntExtra(SEARCH_WHAT, 0);
            search_value = getIntent().getStringExtra(SEARCH_VALUE);
        }
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(this);
        question_data_type = databaseReadOperations.getQuestionDataType(current_field_id);
        sortOption = databaseReadOperations.getFieldSort(current_field_id);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        not_found = (TextView) findViewById(R.id.not_found);
        recyclerView = (RecyclerView) findViewById(R.id.search_activity_recyclerview);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        result_count = (TextView) findViewById(R.id.result_count);
        setSupportActionBar(toolbar);
        mAdapter = new FieldActivityRecyclerViewAdapter(SearchActivity.this, null, current_field_id, search_string_highlight);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        getLoaderManager();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) + SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            search_value = query;
            search_what = intent.getIntExtra(SEARCH_WHAT, 0);
            if (searchView != null && (search_what == SEARCH_QUESTION || search_what == SEARCH_ANSWER)) {
                searchItem.expandActionView();
                searchView.setQuery(search_value, false);
            }
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);
            search_string_highlight.replace(0, search_string_highlight.length(), query);
            getLoaderManager().destroyLoader(SEARCH_LOADER);
            getLoaderManager().restartLoader(SEARCH_LOADER, bundle, this);
            ActivityUtility.antiCrossFade(content, mLoadingView, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_activity_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if ( search_value != null && !"".equals(search_value) ) {
            searchItem.expandActionView();
            searchView.setQuery(search_value, false);
        }
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        finish();
                        return true;
                    }
                });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInsatanceState) {
        super.onSaveInstanceState(savedInsatanceState);
        savedInsatanceState.putLong(CURRENT_FIELD_ID, current_field_id);
        savedInsatanceState.putInt(SEARCH_WHAT, search_what);
        if ( mAdapter.getCursor() != null ) {
            recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        savedInsatanceState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);
        savedInsatanceState.putString(SEARCH_VALUE, search_value);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchString = args.getString(QUERY_KEY);
        if (search_what == SEARCH_ANSWER) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(SearchActivity.this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  a LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{String.valueOf(current_field_id), searchString});
        } else if (search_what == SEARCH_QUESTION) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(SearchActivity.this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  q LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{String.valueOf(current_field_id), searchString});
        } else {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(SearchActivity.this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + "  q LIKE ? "
                            + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                            + " a LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                    new String[]{String.valueOf(current_field_id), "%" + searchString + "%", current_field_id + "", "%" + searchString + "%"});
        }
        return cursorLoaderAndUpdateDatabase;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if ( recyclerview_position != null ) {
            recyclerView.getLayoutManager()
                    .onRestoreInstanceState(recyclerview_position);
        }
        result_count.setText(String.valueOf(data.getCount()));
        if (data.getCount() == 0) {
            not_found.setText(R.string.not_found);
            not_found.setVisibility(View.VISIBLE);
        } else {
            not_found.setText("");
            not_found.setVisibility(View.GONE);
        }
        ActivityUtility.crossFade(content, mLoadingView, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void deleteEntry(long entry_id, int question_data_type) {
        cursorLoaderAndUpdateDatabase.deleteEntry(entry_id, question_data_type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EDIT_ENTRY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                long entry_id = data.getLongExtra(EDITED_ENTRY_ID, 1);
                String question = data.getStringExtra(EDITED_QUESTION);
                String answer = data.getStringExtra(EDITED_ANSWER);
                int rank = data.getIntExtra(EDITED_RANK, 0);
                int order = data.getIntExtra(EDITED_ORDER, 0);
                cursorLoaderAndUpdateDatabase.editEntry(entry_id, question_data_type, question, answer, rank, order);
            }
        }
    }

    @Override
    public void substituteQuestion(long entry_id, String text) {
        cursorLoaderAndUpdateDatabase.substituteQuestion(entry_id, question_data_type, text);
    }

    @Override
    public void substituteAnswer(long entry_id, String text) {
        cursorLoaderAndUpdateDatabase.substituteAnswer(entry_id, question_data_type, text);
    }
    @Override
    public void startStudyActivity(long entry_id, String question, String answer) {
        Intent intent = new Intent(this, NewStudyActivity.class);
        intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
        intent.putExtra(ENTRY_ID, entry_id);
        intent.putExtra(CURRENT_QUESTION, question);
        intent.putExtra(CURRENT_ANSWER, answer);
        if ( search_value.length() > 0 ) {
            intent.putExtra(NewStudyActivity.SEARCH_VALUE, search_value);
            if (search_what == SEARCH_QUESTION) {
                intent.putExtra(SEARCH_WHAT, SEARCH_QUESTION);
            } else if ( search_what == SEARCH_ANSWER ) {
                intent.putExtra(SEARCH_WHAT, SEARCH_ANSWER);
            }
        }
        startActivity(intent);
    }
}
