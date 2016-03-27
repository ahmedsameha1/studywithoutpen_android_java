package com.ahmedsameha1.studyvocabularyfive.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Adapters.FieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseHelper;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.Database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.ExportFieldsDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.ImportFieldsDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.Fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.studyvocabularyfive.R;


public class FieldActivity extends AppCompatActivity implements
        DeleteFieldDialogFragment.Contact,
        LoaderManager.LoaderCallbacks<Cursor>,
        FieldActivityFragmentDrawer.FragmentDrawerListener {
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final int ORDER_QUESTION_DESC = 2;
    public static final int ORDER_ANSWER_DESC = 3;
    public static final int ORDER_QUESTION_ASC = 4;
    public static final int ORDER_ANSWER_ASC = 5;
    public static final int ORDER_DATE_DESC = 6;
    public static final int ORDER_DATE_ASC = 7;
    public static final int ORDER_ENTRY_DATE_DESC = 8;
    public static final int ORDER_ENTRY_DATE_ASC = 9;
    public static final String INITIAL_FIELD = "intial_field";
    public static final String GET_NEW_FIELD_NAME = "getNewFieldName";

    public static final String CURRENT_FIELD = "currentField";
    public static final String CHECK_IF_CONTINUE_STUDY_DURING_SESSION = "checkIfContinueStudyDuringSession";
    public static final String CHECK_IF_CONTINUE_TEST_SESSION = "checkIfContinueTestSession";
    public static final String RUN_FIRST_TIME_DIALOG = "runFirstTimeDialog";
    public static final String RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS = "runFirstTimeCreateExample_Fields";
    public static final int FIELD_LIST_LOADER_ID = 0;
    public static final int FIELD_ENTRIES_LOADER_ID = 1;
    private String[] deleteEntryData = {null, null, null};
    private Cursor fieldEntriesCursor;
    private Cursor fieldListCursor;
    private EntriesListFragment.CustomCursorAdapter customCursorAdapterForFieldEntriesSpinner;
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    protected int fieldType = 0;
    private String currentField;


    private Spinner fieldsListSpinner;

    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;
    private FieldActivityFragmentDrawer drawerFragment;
    private DatabaseHelper databaseHelper;
    private DatabaseOperations databaseOperations;
    private RecyclerView recyclerView;
    private FieldActivityRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity); // FieldActivity.this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FieldActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(FieldActivity.this);
        currentField = getIntent().getStringExtra(INITIAL_FIELD);
        fieldType = new DatabaseOperations(getContentResolver()).getFieldType(currentField);
        mAdapter = new FieldActivityRecyclerViewAdapter(FieldActivity.this,null,currentField,fieldType);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FieldActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FieldActivity.this,AddEntryActivity.class);
                intent.putExtra(MainActivity.LAST_USED_FIELD_POSITION,getIntent().getIntExtra(MainActivity.LAST_USED_FIELD_POSITION,0));
                intent.putExtra(CURRENT_FIELD,currentField);
                intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY,true);
                startActivity(intent);
            }
        });
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == SEARCH_ENTRIES_LOADER_ID) {
                    mAdapter.changeCursor(null);
                    String searchQuestion = args.getString(EntriesListFragment.SEARCH_QUESTION_VALUE);
                    String searchAnswer = args.getString(EntriesListFragment.SEARCH_ANSWER_VALUE);
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    if (searchQuestion.length() > 0 && searchAnswer.length() > 0) {
                        return new CursorLoader(FieldActivity.this,
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                                        + " AND " +
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK
                                        + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchQuestion + "%", currentField, "%" + searchAnswer + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    } else if (searchQuestion.length() > 0) {
                        return new CursorLoader(FieldActivity.this,
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchQuestion + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    } else {
                        return new CursorLoader(FieldActivity.this,
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchAnswer + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    }
                } else if (id == ORDER_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "date(" + Contract.Linker.COLUMN_NAME_QUESTION_LINK + ") DESC");

                } else if (id == ORDER_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "date(" + Contract.Linker.COLUMN_NAME_QUESTION_LINK + ") ASC");
                } else if (id == ORDER_QUESTION_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " DESC");
                } else if (id == ORDER_QUESTION_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " ASC");
                } else if (id == ORDER_ANSWER_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK + " DESC");
                } else if (id == ORDER_ANSWER_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK + " ASC");
                } else if (id == ORDER_ENTRY_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                } else if (id == ORDER_ENTRY_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") ASC");
                } else if ( id == GET_ENTRIES_LOADER_ID){
                    /*String currentField = null;
                    if (args != null) {
                        currentField = args.getString(FieldActivity.CURRENT_FIELD);
                    }
                    currentField = currentField == null ? currentField : currentField;*/
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(FieldActivity.this, uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{currentField},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
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
        getSupportLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID,null,getFieldEntries);
        /*setContentView(R.layout.activity_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity);
        fieldsListSpinner = (Spinner) findViewById(R.id.fields_spinner);

        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseHelper = new DatabaseHelper(FieldActivity.this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getContentResolver());
        simpleCursorAdapterForFieldSpinner = initializeSimpleCursorAdapterForFieldSpinner();
        fieldsListSpinner.setAdapter(simpleCursorAdapterForFieldSpinner);
//        fieldsListSpinner.setSelection(getIntent().getExtras().getInt(INITIAL_FIELD));
        getSupportLoaderManager().initLoader(FIELD_LIST_LOADER_ID,null,FieldActivity.this);
//

        fieldsListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLastUsedField(currentField);
                setLastUsedField(fieldsListSpinner.getSelectedItemPosition());
                fieldType = databaseOperations.getFieldType(currentField);
                EntriesListFragment entriesListFragment = (EntriesListFragment) FieldActivity.FieldActivity.this
                        .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                if ( entriesListFragment != null ) {



                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD, currentField);
                    entriesListFragment.getCustomCursorAdapterForFieldEntriesListView()
                            .changeCursor(null);
                    for ( int i = 0; i < 10; i++ ) {
                        entriesListFragment.getLoaderManager().destroyLoader(i);
                    }
                    entriesListFragment.getLoaderManager().restartLoader(EntriesListFragment.GET_ENTRIES_LOADER_ID,
                            bundle, entriesListFragment.getFieldEntries);
                    entriesListFragment.fieldType = databaseOperations.getFieldType(currentField);
                }
                AddEntryFragment addEntryFragment = ((AddEntryFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.add_entry_fragment));
                addEntryFragment.enterAnswer.requestFocus();
                switch ( fieldType ) {
                    case Contract.Field.NOTEBOOK_TYPE:
                        addEntryFragment.enterQuestion.setHint(R.string.enter_date_time);
                        addEntryFragment.enterAnswer.setHint(R.string.write_whatever);
                        break;
                    case Contract.Field.DEFAULT_TYPE:
                        addEntryFragment.enterQuestion.setHint(R.string.enter_a_question);
                        addEntryFragment.enterAnswer.setHint(R.string.enter_an_answer);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*//*




        *//*
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                return new CursorLoader(FieldActivity.FieldActivity.this,
                        uri,
                        new String[] {"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                        Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[] {currentField},
                        null);
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                fieldEntriesCursor = data;
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                fieldEntriesCursor = null;
            }
        };
        FieldActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int versionCode = info.versionCode;
            if ( ! sharedPreferences.contains(RUN_FIRST_TIME_DIALOG + versionCode) ) {
                editor.putBoolean(FieldActivity.RUN_FIRST_TIME_DIALOG + versionCode,true);
                editor.commit();
            }
            if ( ! sharedPreferences.contains(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode) ) {
                editor.putBoolean(FieldActivity.RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode,true);
                editor.commit();
            }
            boolean runFirstTimeDialog = sharedPreferences.getBoolean(RUN_FIRST_TIME_DIALOG + versionCode,false);
            if ( runFirstTimeDialog ) {
                ExampleFieldsDailogFragment exampleFieldsDailogFragment = new ExampleFieldsDailogFragment();
                exampleFieldsDailogFragment.show(getSupportFragmentManager(),"exampleFieldsDialogFragment");
            }
            boolean runFirstTimeCreateExampleFields = sharedPreferences.getBoolean(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode,false);
            if ( runFirstTimeCreateExampleFields ) {
                databaseOperations.createExampleField(FieldActivity.this);
                editor.putBoolean(FieldActivity.RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode, false);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {

        }*/
    }
    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
    @Override
    public void onDestroy() {
//        fieldListCursor.close();
/*        if ( fieldEntriesCursor != null ) {
            fieldEntriesCursor.close();
        }*/
//        sqLiteDatabase.close();
        if ( databaseHelper != null ) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; FieldActivity.this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        Thread exportThread = ExportImportDatabase.getExportThread();
        Thread importThread = ExportImportDatabase.getImportThread();
        switch (id) {
            /*case R.id.test:
                switch (fieldType) {
                    case Contract.Field.DEFAULT_TYPE:
                        if ( fieldEntriesCursor.getCount() > 0 ) {
                            if ( databaseOperations.checkLinkerTest(currentField) ) {
                                AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                                fragment3.show(getSupportFragmentManager(),CHECK_IF_CONTINUE_TEST_SESSION);
                            } else {
                                TestDialogFragment dialogFragment = new TestDialogFragment();
                                dialogFragment.show(getSupportFragmentManager(),TestDialogFragment.TEST_DIALOG_FRAGMENT);
                                *//*Intent intent = new Intent(FieldActivity.this, TestActivity.class);
                                intent.putExtra(TestActivity.CURRENT_FIELD, currentField);
                                intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE,false);
                                startActivity(intent);*//*
                            }
                        } else {
                            Toast toast = Toast.makeText(FieldActivity.this,getString(R.string.no_entries_in_field),Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        }
                        break;
                    case Contract.Field.NOTEBOOK_TYPE:
                        Toast toast = Toast.makeText(FieldActivity.this, R.string.notbook_feature_is_not_active, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                        break;
                }
                return true;*/
            /*case R.id.study_during:
                switch (fieldType) {
                    case Contract.Field.DEFAULT_TYPE:
                        if ( databaseOperations.checkLinkerStudyDuring(currentField) ) {
                            AskAboutContinuePreviousStudyDuringDialogFragment fragment2 = new AskAboutContinuePreviousStudyDuringDialogFragment();
                            fragment2.show(getSupportFragmentManager(),CHECK_IF_CONTINUE_STUDY_DURING_SESSION);
                        } else {
                            StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
                            fragment2.show(getSupportFragmentManager(),fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                        }
                        break;
                    case Contract.Field.NOTEBOOK_TYPE:
                        Toast toast = Toast.makeText(FieldActivity.this, R.string.notbook_feature_is_not_active, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                        break;
                }
                return true;*/
            case R.id.export:
                if ( (exportThread == null || !exportThread.isAlive()) &&
                        (importThread == null || !importThread.isAlive()) ) {
                    ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                    exportFieldsDialogFragment.show(getSupportFragmentManager(),ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(FieldActivity.this,R.string.wait_until_current_export_import_finish,Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                /*if ( ! sharedPreferences.getBoolean(ExportFieldsDialogFragment.EXPORT_THREAD_IS_RUNNING,false) ) {
                    ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                    exportFieldsDialogFragment.show(getSupportFragmentManager(),ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast.makeText(FieldActivity.this,R.string.wait_till_current_export_finish,Toast.LENGTH_SHORT).show();
                }*/
                return true;
            case R.id.import_:
                if ( ( exportThread == null || !exportThread.isAlive() ) &&
                        ( importThread == null || !importThread.isAlive() ) ) {
                    ImportFieldsDialogFragment importFieldsDialogFragment = new ImportFieldsDialogFragment();
                    importFieldsDialogFragment.show(getSupportFragmentManager(),ImportFieldsDialogFragment.IMPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(FieldActivity.this,R.string.wait_until_current_export_import_finish,Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                /*if ( ! sharedPreferences.getBoolean(ImportFieldsDialogFragment.IMPORT_THREAD_IS_RUNNING,false) ) {
                    ImportFieldsDialogFragment importFieldsDialogFragment = new ImportFieldsDialogFragment();
                    importFieldsDialogFragment.show(getSupportFragmentManager(),ImportFieldsDialogFragment.IMPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast.makeText(FieldActivity.this,R.string.wait_till_current_import_finish,Toast.LENGTH_SHORT).show();
                }*/
                return true;
            case R.id.rate_the_app:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(FieldActivity.this, "Unable to find market app", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                return true;
            case R.id.open_facebook_page:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

 /*   public void createNewField(CreateFieldDialogFragment dialog) {
        EditText enterFieldName = (EditText) dialog.getDialog().findViewById(R.id.enter_field_name);
        CheckBox notebookCheckbox = (CheckBox) dialog.getDialog().findViewById(R.id.notebook_field_checkbox);
        int type = notebookCheckbox.isChecked()? Contract.Field.NOTEBOOK_TYPE : Contract.Field.DEFAULT_TYPE;
        String fieldName = enterFieldName.getText().toString();
        if (fieldName.contains(" ")) {
            Toast toast = Toast.makeText(FieldActivity.FieldActivity.this, "Spaces is not allowed in field name", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else if (fieldName.equals("")) {
            Toast toast = Toast.makeText(FieldActivity.FieldActivity.this, "Where is the field name?!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
            Toast toast = Toast.makeText(FieldActivity.FieldActivity.this, "There is an field with the same name!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else {
            databaseOperations.createNewField(fieldName, type);
        }
//        fieldListCursor = databaseOperations.getFieldList();
//        simpleCursorAdapterForFieldSpinner.changeCursor(fieldListCursor);
//        fieldsListSpinner.setSelection(fieldListCursor.getCount());
        setLastUsedField(fieldListCursor.getCount());

        EntriesListFragment entriesListFragment = (EntriesListFragment) getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
        if ( entriesListFragment != null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(entriesListFragment);
            transaction.attach(entriesListFragment);
            transaction.commit();
        }
    }*/

    private SimpleCursorAdapter initializeSimpleCursorAdapterForFieldSpinner() {
        String[] fromColumns = {Contract.Field.COLUMN_NAME_FIELD_NAME};
        int[] toViews = {R.id.list_item};
    /*    if ( fieldListCursor != null ) {
            fieldListCursor.close();
        }*/
//        fieldListCursor = databaseOperations.getFieldList();
        return new SimpleCursorAdapter(FieldActivity.this, R.layout.field_spinner, null, fromColumns, toViews, 0);
    }


    public Spinner getFieldsListSpinner() {
        return fieldsListSpinner;
    }

    public String getCurrentField() {
        Cursor cursor = (Cursor) fieldsListSpinner.getSelectedItem();
        String string = cursor.getString(1);
        return string;
    }

    public void deleteField() {
        if ( simpleCursorAdapterForFieldSpinner.getCount() <= 1 ) {
            databaseOperations.deleteField(currentField);
            finish();
        } else {
            databaseOperations.deleteField(currentField);
            if ( fieldsListSpinner.getSelectedItemPosition() == (fieldListCursor.getCount() - 1 )) {
                fieldsListSpinner.setSelection((fieldListCursor.getCount() - 2));
            }
        }
//        fieldListCursor = databaseOperations.getFieldList();
//        simpleCursorAdapterForFieldSpinner.changeCursor(fieldListCursor);


    }

    public int getFieldPostion(String fieldName) {
        for (fieldListCursor.moveToFirst(); fieldListCursor.getPosition() < fieldListCursor.getCount(); fieldListCursor.moveToNext()) {
            if (fieldListCursor.getString(1).equals(fieldName)) {
                int n = fieldListCursor.getPosition();
//                fieldListCursor.close();
                return n;
            }
        }
//        fieldListCursor.close();
        return 0;
    }


    public void setLastUsedField ( int position ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.LAST_USED_FIELD_POSITION, position); // saving the last selected item of the field spinner
        editor.commit();
    }
    public void setLastUsedField ( String currentField ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FieldActivity.CURRENT_FIELD, currentField); // saving the last selected item of the field spinner
        editor.commit();
    }
    public void setFieldsSpinner() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this); // getting the preferences
        // getting the data from the preferences and set the selected item of the field spinner.
        int lastUsedPosition = sharedPreferences.getInt(MainActivity.LAST_USED_FIELD_POSITION, 0);
        // to be sure that the last used position in FieldActivity.this spinner is still avilable ( may be the FieldActivity.this position has been deleted )
        if ( simpleCursorAdapterForFieldSpinner.getCount() >= (lastUsedPosition + 1) ) {
            fieldsListSpinner.setSelection(lastUsedPosition);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        return new CursorLoader(FieldActivity.this,uri, new String[]{"rowid _id",Contract.Field.COLUMN_NAME_FIELD_NAME},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       /*simpleCursorAdapterForFieldSpinner.swapCursor(data);
        fieldListCursor = data;
        if ( simpleCursorAdapterForFieldSpinner.getCount() <= 0) {
            databaseOperations.createExampleField(FieldActivity.this);
            return;
        }
        setFieldsSpinner();
        getSupportLoaderManager().restartLoader(FIELD_ENTRIES_LOADER_ID, null, getFieldEntries);*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        simpleCursorAdapterForFieldSpinner.swapCursor(null);
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
