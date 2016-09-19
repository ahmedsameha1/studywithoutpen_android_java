package com.ahmedsameha1.peninbin.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.SearchActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 3/2/2016.
 */
public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> ,
        DeleteEntryDialogFragment.Contact{
    public static final String SCREEN_NAME = "SearchActivity";
    public static final int SORT_CREATION_DATE_DESC = 6;
    public static final int DEFAULT_SORT = SORT_CREATION_DATE_DESC;
    public static final int SEARCH_LOADER = 0;
    private static final String QUERY_KEY = "query_key";
    private static final String CURRENT_SORT = "current_sort";
    private static final String CURRENT_FIELD_ID = "current_field_id";
    private SearchActivityRecyclerViewAdapter mAdapter;
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private int currentSort;
    private Tracker mTracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        int fieldType;
        RecyclerView recyclerView;
        if ( savedInstanceState != null ) {
            current_field_id = savedInstanceState.getLong(CURRENT_FIELD_ID);
        } else {
            current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,0);
        }
        currentSort = getIntent().getIntExtra(StudyActivity.ORDER_OF_ENTRIES,DEFAULT_SORT);
        fieldType = new DatabaseReadOperations(this).getFieldType(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.search_activity_recyclerview);
        setSupportActionBar(toolbar);
        mAdapter = new SearchActivityRecyclerViewAdapter(SearchActivity.this,null, current_field_id,fieldType);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
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
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
        mTracker.setScreenName( SCREEN_NAME);
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
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);
            bundle.putInt(CURRENT_SORT, currentSort);
            getLoaderManager().restartLoader(SEARCH_LOADER, bundle, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_activity_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }
    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        super.onSaveInstanceState(savedInsatanceState);
        savedInsatanceState.putLong(CURRENT_FIELD_ID,current_field_id);
    }
/*    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onSaveInstanceState(savedInstanceState);
        if ( savedInstanceState != null && current_field_id == 0 ) {
            current_field_id = savedInstanceState.getLong(CURRENT_FIELD_ID);
        }
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int sort = args.getInt(CURRENT_SORT);
        String searchString = args.getString(QUERY_KEY);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(SearchActivity.this,
                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                        + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                        + "  q LIKE ? "
                        + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                        + " a LIKE ? ORDER BY " + FieldActivity.SORT_STRINGS[sort],
                new String[]{ current_field_id + "","%" + searchString + "%", current_field_id + "","%" + searchString + "%"});
        return mySQLiteCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void deleteEntry(long entry_id, int question_data_type) {
        mySQLiteCursorLoader.deleteEntry(entry_id,question_data_type);
    }


    public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public LinearSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
