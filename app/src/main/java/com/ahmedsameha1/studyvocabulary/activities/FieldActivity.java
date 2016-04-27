package com.ahmedsameha1.studyvocabulary.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.adapters.FieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;
import com.ahmedsameha1.studyvocabulary.dialogs.AskAboutContinuePreviousStudyDuringDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.SearchDateOrTextDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.StudyDuringDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.TestDialogFragment;
import com.ahmedsameha1.studyvocabulary.fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.studyvocabulary.R;


public class FieldActivity extends AppCompatActivity implements
        FieldActivityFragmentDrawer.FragmentDrawerListener,
        SearchDateOrTextDialogFragment.Contact,
        SortEntriesDialogFragment.Contact,
        DeleteEntryDialogFragment.Contact {
    public static final int SORT = 0;
    public static final int STUDY_PERIOD = 1;
    public static final int TEST = 2;
    private static final int REVIEW = 3;
    private static final int FACEBOOK = 4;
    private static final String QUERY_KEY = "query_key";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final int SORT_QUESTION_DESC = 0;
    public static final int SORT_ANSWER_DESC = 1;
    public static final int SORT_QUESTION_ASC = 2;
    public static final int SORT_ANSWER_ASC = 3;
    public static final int SORT_DATE_DESC = 4;
    public static final int SORT_DATE_ASC = 5;
    public static final int SORT_CREATION_DATE_DESC = 6;
    public static final int SORT_CREATION_DATE_ASC = 7;
    public static final int DEFAULT_SORT = SORT_CREATION_DATE_DESC;
    public static final String INITIAL_FIELD = "intial_field";
    public static final String GET_NEW_FIELD_NAME = "getNewFieldName";

    public static final String CURRENT_FIELD_NAME = "currentField";
    public static final String CHECK_IF_CONTINUE_STUDY_DURING_SESSION = "checkIfContinueStudyDuringSession";
    public static final String CHECK_IF_CONTINUE_TEST_SESSION = "checkIfContinueTestSession";
    public static final String RUN_FIRST_TIME_DIALOG = "runFirstTimeDialog";
    public static final String RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS = "runFirstTimeCreateExample_Fields";
    public static final int FIELD_LIST_LOADER_ID = 0;
    public static final int FIELD_ENTRIES_LOADER_ID = 1;
    private static final String SEARCH_DATE_OR_TEXT = "search_date_or_text";
    private static final String CURRENT_SORT = "currentSort";
    public static final java.lang.String FIELD_TYPE = "field_type";
    private static final String SORT_DIALOG = "sort_dialog";
    public static final String FIELD_ID = "field_id";
    public static final int EDIT_ENTRY = 0;
    public static final String EDITED_ENTRY_ID = "edited_entry_id";
    public static final String EDITED_QUESTION = "edited_question";
    public static final String EDITED_ANSWER = "edited_answer";
    public static final String INSERTED_QUESTION = "inserted_question";
    public static final String INSERTED_ANSWER = "inserted_answer";
    private static final int INSERT_ENTRY = 1;



    private String[] deleteEntryData = {null, null, null};
    private Cursor fieldEntriesCursor;
    private Cursor fieldListCursor;
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    protected int fieldType = 0;
    private long current_field_id;
    private int currentSort;
    public static final String[] SORT_STRINGS = {" q DESC",
            " a DESC",
            " q ASC",
            " a ASC",
            "date( q ) DESC",
            "date( q ) ASC",
            "datetime(" + Contract.CREATION_DATE + ") DESC",
            "datetime(" + Contract.CREATION_DATE + ") ASC"};
    private Spinner fieldsListSpinner;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;
    private FieldActivityFragmentDrawer drawerFragment;
    private DatabaseHelper databaseHelper;
    private DatabaseOperations databaseOperations;
    private RecyclerView recyclerView;
    private FieldActivityRecyclerViewAdapter mAdapter;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        databaseOperations = new DatabaseOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity); // FieldActivity.this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FieldActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(FieldActivity.this);
        current_field_id = getIntent().getLongExtra(FIELD_ID, 0);
        fieldType = databaseOperations.getFieldType(current_field_id);
        mAdapter = new FieldActivityRecyclerViewAdapter(FieldActivity.this, null, current_field_id, fieldType);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FieldActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FieldActivity.this, AddEntryActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, true);
                startActivityForResult(intent,INSERT_ENTRY);
            }
        });

        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if ( databaseOperations.getQuestionDataType(current_field_id) == Contract.Field.STRING_DATA_TYPE ) {
                    int sort = args.getInt(CURRENT_SORT);
                    if (id == SEARCH_ENTRIES_LOADER_ID) {
                        String searchString = args.getString(QUERY_KEY);
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_NAME_ID + ", s1." + Contract.STring.COLUMN_NAME_VALUE + " q , s2." + Contract.STring.COLUMN_NAME_VALUE
                                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_ANSWER
                                        + " WHERE " + " se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? AND "
                                        + "  q LIKE ? "
                                        + " OR " + " se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? AND "
                                        + " a LIKE ? ORDER BY " + SORT_STRINGS[sort],
                                new String[]{ current_field_id + "","%" + searchString + "%", current_field_id + "","%" + searchString + "%"});
                        return mySQLiteCursorLoader;

                    } else if (id == GET_ENTRIES_LOADER_ID) {
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_NAME_ID + ", s1." + Contract.STring.COLUMN_NAME_VALUE + " q , s2." + Contract.STring.COLUMN_NAME_VALUE
                                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? "
                                + "ORDER BY " + SORT_STRINGS[sort] ,
                                new String[]{current_field_id + ""});
                        return mySQLiteCursorLoader;

                    }
                }

                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                fieldEntriesCursor = data;
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        currentSort = savedInstanceState == null ? DEFAULT_SORT : savedInstanceState.getInt(CURRENT_SORT);
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_SORT, currentSort);
        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
    }

    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == EDIT_ENTRY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                long entry_id = data.getLongExtra(EDITED_ENTRY_ID,1);
               String question = data.getStringExtra(EDITED_QUESTION);
                String answer = data.getStringExtra(EDITED_ANSWER);
                mySQLiteCursorLoader.editEntry(entry_id,databaseOperations.getQuestionDataType(current_field_id),question,answer);
            }
        } else if ( requestCode == INSERT_ENTRY ) {
            if ( resultCode == RESULT_OK ) {
                String question = data.getStringExtra(INSERTED_QUESTION);
                String answer = data.getStringExtra(INSERTED_ANSWER);
                mySQLiteCursorLoader.insertEntry(databaseOperations.getQuestionDataType(current_field_id),question,answer,current_field_id);
            }
        }
    }
/*    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_SORT, currentSort);
        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
    }*/

    private void handleIntent(String query) {
        search(query);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        int number_of_entries = databaseOperations.getEntriesNumber(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        switch (position) {
            case TEST:
                if (!sharedPreferences.contains(TestActivity.ACTIVITY_NAME + TestActivity.PAUSING + current_field_id)) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            if (number_of_entries > 0) {
                                if (databaseOperations.isThisFieldTestPaused(current_field_id)) {
                                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                    fragment3.setArguments(bundle);
                                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
                                } else {
                                    TestDialogFragment dialogFragment = new TestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(getSupportFragmentManager(), TestDialogFragment.TEST_DIALOG_FRAGMENT);
                                }
                            } else {
                                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
                            Toast toast = Toast.makeText(FieldActivity.this, R.string.notbook_feature_is_not_active, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            break;
                    }
                } else {
                    Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;

            case STUDY_PERIOD:
                if (!sharedPreferences.contains(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.PAUSING + current_field_id )) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            if (number_of_entries > 0) {
                                if (databaseOperations.isThisFieldStudyPeriodPaused(current_field_id)) {
                                    AskAboutContinuePreviousStudyDuringDialogFragment fragment2 = new AskAboutContinuePreviousStudyDuringDialogFragment();
                                    fragment2.setArguments(bundle);
                                    fragment2.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_STUDY_DURING_SESSION);
                                } else {
                                    StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
                                    fragment2.setArguments(bundle);
                                    fragment2.show(getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                                }
                            } else {
                                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
                            Toast toast = Toast.makeText(FieldActivity.this, R.string.notbook_feature_is_not_active, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                            break;
                    }
                } else {
                    Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_study_during_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case SORT:
                bundle.putInt(FieldActivity.FIELD_TYPE, fieldType);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.setFieldActivityRecyclerViewAdapter(mAdapter);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                break;
            case REVIEW:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(this, "Unable to find market app", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case FACEBOOK:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CURRENT_SORT, currentSort);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; FieldActivity.this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        if (fieldType == Contract.Field.NOTEBOOK_TYPE) {
                            SearchDateOrTextDialogFragment searchDateOrTextDialogFragment = new SearchDateOrTextDialogFragment();
                            searchDateOrTextDialogFragment.show(getSupportFragmentManager(), SEARCH_DATE_OR_TEXT);
                        }
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(CURRENT_SORT, currentSort);
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                        return true;
                    }
                });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")) {
                    if (fieldType != Contract.Field.NOTEBOOK_TYPE) {
                        handleIntent(newText);
                    } else {
                        searchView.clearFocus();
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CURRENT_SORT, currentSort);
                    getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                }
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (fieldType == Contract.Field.NOTEBOOK_TYPE) {
                        searchItem.collapseActionView();
                    }
                }
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }



    @Override
    public void search(String value) {
        Bundle bundle = new Bundle();
        bundle.putString(QUERY_KEY, value);
        bundle.putInt(CURRENT_SORT, currentSort);
        getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
        if (fieldType == Contract.Field.NOTEBOOK_TYPE) {
            searchView.setQuery(value, false);
        }
    }

    @Override
    public void sort(int sort_type) {
        currentSort = sort_type;
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_SORT, currentSort);
        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
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
