package com.ahmedsameha1.studyvocabularyfive;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;

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


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.list_view);
        currentField = getIntent().getStringExtra(StudyActivity.CURRENT_FIELD);
        setSupportActionBar(toolbar);
        simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.study_search_list_view,
                null,
                new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                new int[]{R.id.study_search_list_view_text1, R.id.study_search_list_view_text2}, 0);
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(StudyActivity.CURRENT_FIELD, currentField);
                intent.putExtra(StudyActivity.CURRENT_QUESTION, cursor.getString(1));
                intent.putExtra(StudyActivity.CURRENT_ANSWER, cursor.getString(2));
                intent.putExtra(StudyActivity.ORDER_OF_ENTRIES, 0);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
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
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }
}
