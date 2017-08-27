package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.CopyMoveEntriesActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.CopyFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Money;
import com.ahmedsameha1.peninbin.utils.SortOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by ahmed on 22/08/16.
 */
public class CopyMoveEntriesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SortEntriesDialogFragment.Contact,
        CopyFieldDialogFragment.CopyEntries,
        CopyFieldDialogFragment.MoveEntries {
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    private static final String SORT_DIALOG = "sort_dialog";
    public static final String ANALYTICS_CATEGORY = "Copy Move Entries Activity Category";
    private static final String IS_LOAD_ENTRIES = "is_load_entries";
    private static final String SEARCH_VALUE = "search_value";
    public static final String FIELD_ID = "field_id";
    private CopyMoveEntriesActivityRecyclerViewAdapter mAdapter;
    private ArrayList<Long> list_of_entries = new ArrayList<>();
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private long current_field_id;
    private SortOptions sortOption;
    private RecyclerView recyclerView;
    private Parcelable recyclerview_position;
    private String search_value;
    private View mLoadingView;
    private RelativeLayout content;
    private boolean is_load_entries = true;
    private StringBuilder search_string_highlight = new StringBuilder();
    private Tracker mTracker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_move_entries);
        if (savedInstanceState != null) {
            current_field_id = savedInstanceState.getLong(FIELD_ID);
            recyclerview_position = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
        } else {
            current_field_id = getIntent().getLongExtra(FIELD_ID, 0);
        }
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(this);
        sortOption = databaseOperations.getFieldSort(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        ActivityUtility.antiCrossFade(content, mLoadingView, this);
        recyclerView = (RecyclerView) findViewById(R.id.copy_move_entries_activity_recyclerview);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseOperations.getFieldName(current_field_id));
        mAdapter = new CopyMoveEntriesActivityRecyclerViewAdapter(CopyMoveEntriesActivity.this, null, list_of_entries, current_field_id, search_string_highlight);
        recyclerView.setLayoutManager(new LinearLayoutManager(CopyMoveEntriesActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        getLoaderManager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; FieldActivity.this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.copy_move_entries_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (search_value != null) {
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
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, CopyMoveEntriesActivity.this);
                        search_string_highlight.replace(0, search_string_highlight.length(), "");
                        recyclerView.setLayoutManager(new LinearLayoutManager(CopyMoveEntriesActivity.this));
                        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
                        list_of_entries.clear();
                        is_load_entries = true;
                        search_value = null;
                        return true;
                    }
                });

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
        CopyFieldDialogFragment copyFieldDialogFragment = new CopyFieldDialogFragment();
        Bundle bundle = new Bundle();
        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.copy:
                if (list_of_entries.size() > 0) {
                    bundle.putInt(CopyFieldDialogFragment.WHAT_TO_DO, Constants.CopyMove.COPY_ENTRIES);
                    copyFieldDialogFragment.setArguments(bundle);
                    copyFieldDialogFragment.show(getSupportFragmentManager(), CopyFieldDialogFragment.COPY_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(CopyMoveEntriesActivity.this, R.string.select_one, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;
            case R.id.move:
                if (list_of_entries.size() > 0) {
                    bundle.putInt(CopyFieldDialogFragment.WHAT_TO_DO, Constants.CopyMove.MOVE_ENTRIES);
                    copyFieldDialogFragment.setArguments(bundle);
                    copyFieldDialogFragment.show(getSupportFragmentManager(), CopyFieldDialogFragment.COPY_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(CopyMoveEntriesActivity.this, R.string.select_one, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;
            case R.id.sort:
                bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                bundle.putSerializable(SortEntriesDialogFragment.INITIAL_SORT_OPTION, sortOption);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction((Money.getMe(this) ? "ME" : "") + ActivityUtility.getVersionCode(this) + "sort")
                        .build());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (is_load_entries) {
            getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, this);
            search_string_highlight.replace(0, search_string_highlight.length(), "");
        } else {
            search_string_highlight.replace(0, search_string_highlight.length(), search_value);
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, null, this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
        getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
        ActivityUtility.antiCrossFade(content, mLoadingView, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == SEARCH_ENTRIES_LOADER_ID && search_value != null) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(CopyMoveEntriesActivity.this,
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
            return cursorLoaderAndUpdateDatabase;

        } else if (id == GET_ENTRIES_LOADER_ID) {
            cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(CopyMoveEntriesActivity.this,
                    "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                            + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                            + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                            + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                            + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                            + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                            + "ORDER BY " + sortOption.getSortString(),
                    new String[]{current_field_id + ""});
            return cursorLoaderAndUpdateDatabase;
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if (recyclerview_position != null) {
            recyclerView.getLayoutManager()
                    .onRestoreInstanceState(recyclerview_position);
        }
        ActivityUtility.crossFade(content, mLoadingView, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        list_of_entries.clear();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search_value =
                    intent.getStringExtra(SearchManager.QUERY);
            is_load_entries = false;
            getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
            getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
            ActivityUtility.antiCrossFade(content, mLoadingView, this);
        }
    }

    @Override
    public void copyEntries(long field_id) {
        cursorLoaderAndUpdateDatabase.copyEntries(list_of_entries, field_id);
    }

    @Override
    public void moveEntries(long field_id) {
        cursorLoaderAndUpdateDatabase.moveEntries(list_of_entries, field_id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(FIELD_ID, current_field_id);
        if ( mAdapter.getCursor() != null ) {
            recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        outState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);
        outState.putBoolean(IS_LOAD_ENTRIES, is_load_entries);
        outState.putString(SEARCH_VALUE, search_value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        is_load_entries = savedInstanceState.getBoolean(IS_LOAD_ENTRIES);
        search_value = savedInstanceState.getString(SEARCH_VALUE);
    }

    @Override
    public void sort(SortOptions sortOption) {
        this.sortOption = sortOption;
        getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
        getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list_of_entries.clear();
        ActivityUtility.antiCrossFade(content, mLoadingView, this);
        if (is_load_entries) {
            getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, null, this);
        }
    }
}
