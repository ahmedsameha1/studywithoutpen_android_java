package com.ahmedsameha1.studyvocabulary.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.adapters.MainActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabulary.database.Contract;

import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;
import com.ahmedsameha1.studyvocabulary.dialogs.CreateFieldDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ExportFieldsDialogFragment;
import com.ahmedsameha1.studyvocabulary.dialogs.ImportFieldsDialogFragment;
import com.ahmedsameha1.studyvocabulary.fragments.MainActivityFragmentDrawer;
import com.ahmedsameha1.studyvocabulary.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

/**
 * Created by ahmed on 7/26/2015.
 */
public class MainActivity extends AppCompatActivity implements
        MainActivityFragmentDrawer.FragmentDrawerListener,
        DeleteFieldDialogFragment.Contact,
        CreateFieldDialogFragment.Contact {
    private static final String RUN_FIRST_TIME = "run_first_time";
    public static final String RUN_FIRST_TIME_DIALOG = "runFirstTimeDialog";
    public static final String RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS = "runFirstTimeCreateExample_Fields";
    public static final String LAST_USED_FIELD_POSITION = "last_used_field_position";
    public static final String SPINNER_POSITION = "spinnerPosition";
    private static final String CREATE_NEW_FIELD = "create_new_field";
    private static final String QUERY_KEY = "query_key";
    private static final int GET_FIELDS_ID = 0;
    private static final int SEARCH_FIELDS_ID = 1;
    private static final int EXPORT_FIELD = 0;
    private static final int IMPORT_FIELD = 1;
    private static final int REVIEW = 2;
    private static final int FACEBOOK = 3;
    public static final int DROPBOX = 4;
    private Spinner fieldSpinner;
    private Button createFieldButton, fieldChooseOk;
    private EditText enterNewFieldName;
    private TextView orChooseOne;
    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    //    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private Cursor fieldListCursor;
    private MainActivityFragmentDrawer drawerFragment;
    private RecyclerView recyclerView;
    private MainActivityRecyclerViewAdapter mAdapter;
    private String first_field_name;
    private LoaderManager.LoaderCallbacks<Cursor> getFields;
    private long first_field_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); // this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.main_activity_recyclerview);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (MainActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        getFields = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == GET_FIELDS_ID) {
                    /*return new CursorLoader(MainActivity.this, uri, new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE, Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME}, null, null,
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");*/
                    mySQLiteCursorLoader = new MySQLiteCursorLoader(MainActivity.this,
                            "SELECT " + Contract.COLUMN_NAME_ID + ", " + Contract.Field.COLUMN_NAME_FIELD_TYPE + ", " + Contract.Field.COLUMN_NAME_FIELD_NAME
                                    + " FROM " + Contract.Field.TABLE_NAME + "  ORDER BY " + Contract.Field.COLUMN_NAME_USAGE_COUNT + " DESC",
                            null);
                    return mySQLiteCursorLoader;
                } else if (id == SEARCH_FIELDS_ID) {
                    String query = args.getString(QUERY_KEY);
                    /*return new CursorLoader(MainActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_TYPE, Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME},
                            Contract.Old_Field_DBV6_AppV32.COLUMN_NAME_FIELD_NAME + " LIKE ? ",
                            new String[] {"%" + query + "%"},
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");*/
                    mySQLiteCursorLoader = new MySQLiteCursorLoader(MainActivity.this,
                            "SELECT " + Contract.COLUMN_NAME_ID + ", " + Contract.Field.COLUMN_NAME_FIELD_TYPE + ", "
                                    + Contract.Field.COLUMN_NAME_FIELD_NAME
                                    + " FROM " + Contract.Field.TABLE_NAME + " WHERE " + Contract.Field.COLUMN_NAME_FIELD_NAME + " LIKE ? "
                                    + " ORDER BY " + Contract.Field.COLUMN_NAME_USAGE_COUNT + " DESC", new String[]{"%" + query + "%"});
                    return mySQLiteCursorLoader;
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                if (data.moveToFirst()) {
                    first_field_name = data.getString(2);
                    first_field_id = data.getLong(0);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        mAdapter = new MainActivityRecyclerViewAdapter(this, null);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 2;
        int spacing = 3;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        addFab();
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
        deleteUnNeadedSharedPreferences();
    }

    private void deleteUnNeadedSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean(RUN_FIRST_TIME, true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(RUN_FIRST_TIME, false);
            PackageManager manager = getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                for (int versionCode = info.versionCode; versionCode >= 1; versionCode--) {
                    if (sharedPreferences.contains(RUN_FIRST_TIME_DIALOG + versionCode)) {
                        editor.remove(RUN_FIRST_TIME_DIALOG + versionCode);
                    }
                    if (sharedPreferences.contains(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode)) {
                        editor.remove(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (sharedPreferences.contains(LAST_USED_FIELD_POSITION)) {
                editor.remove(LAST_USED_FIELD_POSITION);
            }
            if (sharedPreferences.contains(FieldActivity.CURRENT_FIELD_NAME)) {
                editor.remove(FieldActivity.CURRENT_FIELD_NAME);
            }
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
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
                    handleIntent(newText);
                } else {
                    getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
                }
                return true;
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Thread exportThread = ExportImportDatabase.getExportThread();
        Thread importThread = ExportImportDatabase.getImportThread();
        switch (position) {
            case EXPORT_FIELD:
                if ((exportThread == null || !exportThread.isAlive()) &&
                        (importThread == null || !importThread.isAlive())) {
                    ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                    exportFieldsDialogFragment.show(getSupportFragmentManager(), ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case IMPORT_FIELD:
                if ((exportThread == null || !exportThread.isAlive()) &&
                        (importThread == null || !importThread.isAlive())) {
                    ImportFieldsDialogFragment importFieldsDialogFragment = new ImportFieldsDialogFragment();
                    importFieldsDialogFragment.show(getSupportFragmentManager(), ImportFieldsDialogFragment.IMPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
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
            case DROPBOX:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dropbox.com/sh/qq4rzots8vrb1mi/AAA6loAcxX6edP5bnHflXSFSa?dl=0"));
                startActivity(browserIntent);
                break;
        }
    }

    private void handleIntent(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(QUERY_KEY, query);
        getLoaderManager().restartLoader(SEARCH_FIELDS_ID, bundle, getFields);
    }

    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */




    private void addFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.fab_selector));
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.drawable.entry);
        SubActionButton button1 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(100, 100))
                .setContentView(itemIcon1, new FrameLayout.LayoutParams(50, 50, Gravity.CENTER)).build();
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.drawable.folder);
        SubActionButton button2 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(100, 100))
                .setContentView(itemIcon2, new FrameLayout.LayoutParams(50, 50, Gravity.CENTER)).build();
        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(fab)
                .build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getItemCount() > 0) {
                    if (first_field_name != null) {
                        Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, first_field_id);
                        intent.putExtra(FieldActivity.CURRENT_FIELD_NAME, first_field_name);
                        intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, false);
                        startActivity(intent);
                    }
                } else {
                    CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                    fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                }
                actionMenu.close(false);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                actionMenu.close(true);
            }
        });
    }

    @Override
    public void deleteField(long field_id) {
        mySQLiteCursorLoader.deleteField(field_id);
    }

    @Override
    public void createField(String field_name, int field_type, int question_data_type) {
        mySQLiteCursorLoader.createField(field_name, field_type, question_data_type);
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
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
