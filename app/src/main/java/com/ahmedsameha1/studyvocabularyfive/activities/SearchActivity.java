package com.ahmedsameha1.studyvocabularyfive.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ahmedsameha1.studyvocabularyfive.adapters.SearchActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabularyfive.database.Contract;
import com.ahmedsameha1.studyvocabularyfive.database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 3/2/2016.
 */
public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String CURRENT_FIELD = "current_field";
    private static final int SEARCH_LOADER = 0;
    private static final String QUERY_KEY = "query_key";
    private ListView listView;
    private String currentField;
    private SimpleCursorAdapter simpleCursorAdapter;
    private RecyclerView recyclerView;
    private SearchActivityRecyclerViewAdapter mAdapter;
    private int fieldType;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.search_activity_recyclerview);
        currentField = getIntent().getStringExtra(StudyActivity.CURRENT_FIELD);
        setSupportActionBar(toolbar);
        fieldType = new DatabaseOperations(getContentResolver()).getFieldType(currentField);
        mAdapter = new SearchActivityRecyclerViewAdapter(SearchActivity.this,null,currentField,fieldType);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
    }

/*    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);
            getSupportLoaderManager().restartLoader(SEARCH_LOADER, bundle, this);
        }
    }
    private void handleIntent(String query) {
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, query);
            getSupportLoaderManager().restartLoader(SEARCH_LOADER, bundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_activity_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleIntent(newText);
                return true;
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }
    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        super.onSaveInstanceState(savedInsatanceState);
        savedInsatanceState.putString(CURRENT_FIELD, currentField);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        super.onSaveInstanceState(savedInstanceState);
        if ( savedInstanceState != null && currentField == null ) {
            currentField = savedInstanceState.getString(CURRENT_FIELD);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchString = args.getString(QUERY_KEY);
        return new CursorLoader(this,Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME),
                new String[]{"rowid _id",
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                        + " OR " +
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK
                        + " LIKE ? AND "
                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[]{"%" + searchString + "%", currentField, "%" + searchString + "%", currentField},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
