package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.NotebookFieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousStudyDuringDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.NotebookFieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SearchDateOrTextDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyDuringDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TestDialogFragment;
import com.ahmedsameha1.peninbin.fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;


public class NotebookFieldActivity extends AppCompatActivity implements
        SearchDateOrTextDialogFragment.Contact,
        SortEntriesDialogFragment.Contact,
        DeleteEntryDialogFragment.Contact,
        NotebookFieldPropertiesDialogFragment.Contact {
    public static final String SCREEN_NAME = "FieldActivity";
    public static final int SORT = 0;
    public static final int STUDY_PERIOD = 1;
    public static final int TEST = 2;
    public static final int FIELD_PROPERTIES = 4;


    public static final int SCHEDULE_IN_FIELD = 3;
    private static final String QUERY_KEY = "query_key";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final int SORT_CREATION_DATE_DESC = 0;
    public static final int SORT_ANSWER_DESC = 1;
    public static final int SORT_QUESTION_ASC = 2;
    public static final int SORT_ANSWER_ASC = 3;
    public static final int SORT_DATE_DESC = 4;
    public static final int SORT_DATE_ASC = 5;
    public static final int SORT_QUESTION_DESC = 6;
    public static final int SORT_CREATION_DATE_ASC = 7;
    public static final String CURRENT_FIELD_NAME = "currentField";
    public static final String CHECK_IF_CONTINUE_STUDY_DURING_SESSION = "checkIfContinueStudyDuringSession";
    public static final String CHECK_IF_CONTINUE_TEST_SESSION = "checkIfContinueTestSession";
    private static final String SEARCH_DATE_OR_TEXT = "search_date_or_text";
    private static final String CURRENT_SORT = "currentSort";
    public static final String FIELD_TYPE = "field_type";
    private static final String SORT_DIALOG = "sort_dialog";
    public static final String FIELD_ID = "field_id";
    public static final int EDIT_ENTRY = 0;
    public static final String EDITED_ENTRY_ID = "edited_entry_id";
    public static final String EDITED_QUESTION = "edited_question";
    public static final String EDITED_ANSWER = "edited_answer";
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final String FIELD_OPTIONS_DIALOG = "field_options_dialog";
    public static final String ANALYTICS_CATEGORY = "Field Activity Category";
    public static final String EDITED_RANK = "edited_rank";
    private static final String NOTEBOOK_FIELD_PROPERTIES = "notebook_field_properties";
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    private long current_field_id;
    private int currentSort;
    public static final String[] SORT_STRINGS = {"datetime(" + Contract.CREATION_DATE + ") DESC",
            " a DESC",
            " q ASC",
            " a ASC",
            "date( q ) DESC",
            "date( q ) ASC",
            " q DESC",
            "datetime(" + Contract.CREATION_DATE + ") ASC"};
    private Spinner fieldsListSpinner;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private DatabaseOperations databaseOperations;
    private NotebookFieldActivityRecyclerViewAdapter mAdapter;
    private SearchView searchView;
    private MenuItem searchItem;
    private ArrayList<Locale> localeList = new ArrayList<Locale>();
    private ArrayList<String> locale_display_names = new ArrayList<String>();
    private TextToSpeech tts;
    private boolean load_or_search = true;
    private String search_value;
    private int question_data_type;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebook_activity_field);
        RecyclerView recyclerView;
        if (savedInstanceState != null) {
            current_field_id = savedInstanceState.getLong(FIELD_ID);
        } else {
            current_field_id = getIntent().getLongExtra(FIELD_ID, 0);
        }
        databaseOperations = new DatabaseOperations(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        currentSort = databaseOperations.getFieldSort(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity); // FieldActivity.this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseOperations.getFieldName(current_field_id));
        mAdapter = new NotebookFieldActivityRecyclerViewAdapter(NotebookFieldActivity.this, null, current_field_id);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotebookFieldActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotebookFieldActivity.this, AddNotebookEntryActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, true);
                startActivity(intent);
            }
        });
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    int sort = args.getInt(CURRENT_SORT);
                    if (id == SEARCH_ENTRIES_LOADER_ID) {
                        String searchString = args.getString(QUERY_KEY);
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(NotebookFieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                                        + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + "  q LIKE ? "
                                        + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + " a LIKE ? ORDER BY " + SORT_STRINGS[sort],
                                new String[]{current_field_id + "", "%" + searchString + "%", current_field_id + "", "%" + searchString + "%"});
                        return mySQLiteCursorLoader;

                    } else if (id == GET_ENTRIES_LOADER_ID) {
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(NotebookFieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a , se." + Contract.String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST
                                        + " FROM "  + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                                        + "ORDER BY " + SORT_STRINGS[sort],
                                new String[]{current_field_id + ""});
                        return mySQLiteCursorLoader;

                    }
                }

                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        getLoaderManager();
    }

/*    @Override
    protected void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
        getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
    }*/

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
                long entry_id = data.getLongExtra(EDITED_ENTRY_ID, 1);
                String question = data.getStringExtra(EDITED_QUESTION);
                String answer = data.getStringExtra(EDITED_ANSWER);
                mySQLiteCursorLoader.editEntry(entry_id, question_data_type, question, answer);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (load_or_search) {
            Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_SORT, currentSort);
            getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, search_value);
            bundle.putInt(CURRENT_SORT, currentSort);
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);

                searchView.setQuery(search_value, false);

        }
        mTracker.setScreenName(SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }


    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search_value =
                    intent.getStringExtra(SearchManager.QUERY);
            load_or_search = false;
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CURRENT_SORT, currentSort);
        savedInstanceState.putLong(FIELD_ID, current_field_id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; FieldActivity.this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notebook_field_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                            SearchDateOrTextDialogFragment searchDateOrTextDialogFragment = new SearchDateOrTextDialogFragment();
                            searchDateOrTextDialogFragment.show(getSupportFragmentManager(), SEARCH_DATE_OR_TEXT);

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
/*        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });*/
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                        searchItem.collapseActionView();

                }
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

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.sort:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("sort")
                        .build());
                return true;
            case R.id.update_field_name:
                Bundle bundle1 = new Bundle();
                bundle1.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                NotebookFieldPropertiesDialogFragment notebookFieldPropertiesDialogFragment = new NotebookFieldPropertiesDialogFragment();
                notebookFieldPropertiesDialogFragment.setArguments(bundle1);
                notebookFieldPropertiesDialogFragment.show(getSupportFragmentManager(), NOTEBOOK_FIELD_PROPERTIES);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("sort")
                        .build());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void search(String value) {
        if ( value != null) {
            Bundle bundle = new Bundle();
            bundle.putString(QUERY_KEY, value);
            bundle.putInt(CURRENT_SORT, currentSort);
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);

            searchView.setQuery(value, false);
            searchView.clearFocus();
        } else {
            searchItem.collapseActionView();
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
    public void updateFieldName( String name) {
        getSupportActionBar().setTitle(name);
    }
    @Override
    public void deleteEntry(long entry_id, int question_data_type) {
        mySQLiteCursorLoader.deleteEntry(entry_id, question_data_type);
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
