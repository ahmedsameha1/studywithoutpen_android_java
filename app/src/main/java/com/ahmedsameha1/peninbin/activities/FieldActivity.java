package com.ahmedsameha1.peninbin.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.Loader;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.FieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousStudyDuringDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SearchDateOrTextDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyDuringDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.EnhancedTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.TestDialogFragment;
import com.ahmedsameha1.peninbin.fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;


public class FieldActivity extends AppCompatActivity implements
        FieldActivityFragmentDrawer.FragmentDrawerListener,
        SearchDateOrTextDialogFragment.Contact,
        SortEntriesDialogFragment.Contact,
        DeleteEntryDialogFragment.Contact,
        FieldPropertiesDialogFragment.Contact,
        TextToSpeech.OnInitListener {
    public static final String SCREEN_NAME = "FieldActivity";
    public static final int SORT = 0;
    public static final int STUDY_PERIOD = 1;
    public static final int ENHANCED_TEST = 2;
    public static final int TEST = 3;
    public static final int SCHEDULE_IN_FIELD = 4;
    public static final int FIELD_PROPERTIES = 5;
    public static final int Field_INFORMATION = 6;



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
    public static final java.lang.String FIELD_TYPE = "field_type";
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
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    protected int fieldType = 0;
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
    private FieldActivityRecyclerViewAdapter mAdapter;
    private SearchView searchView;
    private MenuItem searchItem;
    private boolean tts_available = false;
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
        setContentView(R.layout.activity_field);
        FieldActivityFragmentDrawer drawerFragment;
        RecyclerView recyclerView;
        if (savedInstanceState != null) {
            current_field_id = savedInstanceState.getLong(FIELD_ID);
        } else {
            current_field_id = getIntent().getLongExtra(FIELD_ID, 0);
        }
        databaseOperations = new DatabaseOperations(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        fieldType = databaseOperations.getFieldType(current_field_id);
        currentSort = databaseOperations.getFieldSort(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity); // FieldActivity.this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerFragment = (FieldActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseOperations.getFieldName(current_field_id));
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(FieldActivity.this);
        mAdapter = new FieldActivityRecyclerViewAdapter(FieldActivity.this, null, current_field_id, fieldType);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FieldActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        tts = new TextToSpeech(getApplicationContext(), this);
        try {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FieldActivity.this, AddEntryActivity.class);
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
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + "  q LIKE ? "
                                        + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + " a LIKE ? ORDER BY " + SORT_STRINGS[sort],
                                new String[]{current_field_id + "", "%" + searchString + "%", current_field_id + "", "%" + searchString + "%"});
                        return mySQLiteCursorLoader;

                    } else if (id == GET_ENTRIES_LOADER_ID) {
                        mySQLiteCursorLoader = new MySQLiteCursorLoader(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
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
                int rank = data.getIntExtra(EDITED_RANK,0);
                mySQLiteCursorLoader.editEntry(entry_id, question_data_type, question, answer,rank);
            }
        } else if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts_available = true;
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
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }
    @Override
    public void onInit(int status) {
        try {
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale locale : locales) {
                if (locale.toString().equals("en_US_POSIX")) continue;
                int res = tts.isLanguageAvailable(locale);
                if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    localeList.add(locale);
                    locale_display_names.add(locale.getDisplayName());
                }
            }
            locale_display_names.add(getString(R.string.select_locale));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onDrawerItemSelected(View view, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        int number_of_entries = databaseOperations.getEntriesNumber(current_field_id, question_data_type);
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        switch (position) {
            case ENHANCED_TEST:
                if (!sharedPreferences.contains(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.PAUSING + current_field_id)) {

                            if (number_of_entries > 0) {
                                if (databaseOperations.isThisFieldTestPaused(current_field_id)) {
                                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                    fragment3.setArguments(bundle);
                                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
                                } else {
                                    EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(getSupportFragmentManager(), EnhancedTestDialogFragment.TEST_DIALOG_FRAGMENT);
                                }
                            } else {
                                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                } else {
                    Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case TEST:
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
            case STUDY_PERIOD:
                if (!sharedPreferences.contains(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.PAUSING + current_field_id)) {
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

                } else {
                    Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_study_during_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case SORT:
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("sort")
                        .build());
                break;
            case FIELD_PROPERTIES:
                String locale_short_name = databaseOperations.getFieldLocale(current_field_id);
                if (locale_short_name != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(locale_short_name, ",");
                    String lang = stringTokenizer.nextToken();
                    String country = stringTokenizer.nextToken();
                    Locale locale = new Locale(lang, country);
                    boolean locale_available = (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_COUNTRY_AVAILABLE);
                    bundle.putBoolean(FieldPropertiesDialogFragment.LOCALE_AVAILABLE, locale_available);
                    if (tts_available && locale_available) {
                        bundle.putInt(FieldPropertiesDialogFragment.INDEX_OF_LOCALE, localeList.indexOf(locale));
                    }
                }
                bundle.putBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE, tts_available);
                bundle.putStringArrayList(FieldPropertiesDialogFragment.LOCALE_DISPLAY_NAMES, locale_display_names);
                bundle.putSerializable(FieldPropertiesDialogFragment.LOCALE_LIST, localeList);
                FieldPropertiesDialogFragment fieldPropertiesDialogFragment = new FieldPropertiesDialogFragment();
                fieldPropertiesDialogFragment.setArguments(bundle);
                fieldPropertiesDialogFragment.show(getSupportFragmentManager(), FIELD_OPTIONS_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("field properties")
                        .build());
                break;
            case SCHEDULE_IN_FIELD:

                    ScheduleInFieldDialogFragment scheduleInFieldDialogFragment = new ScheduleInFieldDialogFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                    scheduleInFieldDialogFragment.setArguments(bundle1);
                    scheduleInFieldDialogFragment.show(getSupportFragmentManager(),ScheduleInFieldDialogFragment.SCHEDULE_IN_FIELD);

                /*Calendar calendar_current_date_time = Calendar.getInstance();
                final Calendar calendar_picked_date_time = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        new TimePickerDialog(FieldActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(FieldActivity.this, ShowScheduledNotificationsReceiver.class);
                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                int request = (int)System.currentTimeMillis();
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(FieldActivity.this,request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                            }
                        }, calendar_picked_date_time.get(Calendar.HOUR_OF_DAY), calendar_picked_date_time.get(Calendar.MINUTE), false).show();
                    }
                }, calendar_current_date_time.get(Calendar.YEAR), calendar_current_date_time.get(Calendar.MONTH),
                        calendar_current_date_time.get(Calendar.DAY_OF_MONTH)).show();*/
                break;
            case Field_INFORMATION:
                Intent intent = new Intent(this,FieldInformationActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                startActivity(intent);
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
        getMenuInflater().inflate(R.menu.field_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(CURRENT_SORT, currentSort);
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                        load_or_search = true;
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
