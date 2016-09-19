package com.ahmedsameha1.peninbin.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.MainActivityRecyclerViewViewHolder;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.dialogs.AboutDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ImportFromWhatDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.adapters.MainActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;

import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.dialogs.CreateFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ExportFieldsDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.fragments.MainActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.receivers_services.SimpleWakefulReceiver;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by ahmed on 7/26/2015.
 */
public class MainActivity extends AppCompatActivity implements
        MainActivityFragmentDrawer.FragmentDrawerListener,
        DeleteFieldDialogFragment.Contact,
        CreateFieldDialogFragment.Contact,
        ExportFieldsDialogFragment.Contact {
    public static final String MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST = "mid_night_service_pendingintent_request";
    public static final String SCREEN_NAME = "MainActivity";
    private static final String RUN_FIRST_TIME = "run_first_time";
    public static final String RUN_FIRST_TIME_DIALOG = "runFirstTimeDialog";
    public static final String RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS = "runFirstTimeCreateExample_Fields";
    public static final String LAST_USED_FIELD_POSITION = "last_used_field_position";
    private static final String CREATE_NEW_FIELD = "create_new_field";
    private static final String QUERY_KEY = "query_key";
    public static final String ANALYTICS_CATEGORY = "Main Activity Category";
    private static final int GET_FIELDS_ID = 0;
    private static final int SEARCH_FIELDS_ID = 1;
    private static final int EXPORT_FIELD = 0;
    private static final int IMPORT_FIELD = 1;
    public static final int ABOUT = 3;
    private static final int REVIEW = 2;
/*    private static final int FACEBOOK = 3;
    public static final int DROPBOX = 4;*/
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final int DIRECTORY_CODE = 15;
    private static final String TO_EXPORT_FIELDS = "to_export_fields";
    private static final String TO_EXPORT_FILE_NAME = "to_export_file_name";
    private Handler mHandler = new Handler();
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private MainActivityRecyclerViewAdapter mAdapter;


    private LoaderManager.LoaderCallbacks<Cursor> getFields;
    private long first_field_id;
    private boolean tts_available = false;
    private Tracker mTracker;
    private String to_export_file_name;
    private ArrayList<String> to_export_fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityFragmentDrawer drawerFragment;
        RecyclerView recyclerView;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); // this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.main_activity_recyclerview);
        drawerFragment = (MainActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
 /*       try {
            DatabaseHelper.getInstance(this).getWritableDatabase().rawQuery("SELECT _id FROM field",null);
        } catch (android.database.sqlite.SQLiteException e) {
            DatabaseHelper.getInstance(this).close();
            recreate();
        }*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        try {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        mAdapter = new MainActivityRecyclerViewAdapter(this, null);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 2;
        int spacing = 3;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        addFab();
        runFirstTimeAppRunOnly();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        getFields = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == GET_FIELDS_ID) {
                    /*return new CursorLoader(MainActivity.this, uri, new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE, Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME}, null, null,
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");*/
                    mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(MainActivity.this,
                            "SELECT " + Contract.COLUMN_ID + ", " + Contract.Field.COLUMN_FIELD_TYPE + ", " + Contract.Field.COLUMN_FIELD_NAME
                                    + " FROM " + Contract.Field.TABLE_NAME + "  ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC",
                            null);
                    return mySQLiteCursorLoader;
                } else if (id == SEARCH_FIELDS_ID) {
                    String query = args.getString(QUERY_KEY);
                    /*return new CursorLoader(MainActivity.this, uri,
                            new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE, Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME},
                            Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + " LIKE ? ",
                            new String[] {"%" + query + "%"},
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");*/
                    mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(MainActivity.this,
                            "SELECT " + Contract.COLUMN_ID + ", " + Contract.Field.COLUMN_FIELD_TYPE + ", "
                                    + Contract.Field.COLUMN_FIELD_NAME
                                    + " FROM " + Contract.Field.TABLE_NAME + " WHERE " + Contract.Field.COLUMN_FIELD_NAME + " LIKE ? "
                                    + " ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC", new String[]{"%" + query + "%"});
                    return mySQLiteCursorLoader;
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                if (data.moveToFirst()) {
                    first_field_id = data.getLong(0);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        getLoaderManager();
        fff();
    }



    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }
    private void runFirstTimeAppRunOnly() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(RUN_FIRST_TIME, true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND,0);
            Intent intent = new Intent(this, SimpleWakefulReceiver.class);
            int request = (int) System.currentTimeMillis();
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pintent = PendingIntent.getBroadcast(this, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
            Log.i("time of service", calendar.getTimeInMillis()+"");
            Log.i("Time Zone",calendar.getTimeZone().getDisplayName());
            editor.putInt(MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST,request);








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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Check which request we're responding to
         if ( requestCode == MY_DATA_CHECK_CODE ) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts_available = true;
            }
        } else if ( requestCode == ImportFromWhatDialogFragment.TEXT_FILE_CODE && resultCode == Activity.RESULT_OK ) {
             ImportTextFile.startImportThread(this, data.getData().getPath());
             mTracker.send(new HitBuilders.EventBuilder()
                     .setCategory(ANALYTICS_CATEGORY)
                     .setAction("import field from text file")
                     .build());
         } else if ( requestCode == ImportFromWhatDialogFragment.APP_FILE_CODE && resultCode == Activity.RESULT_OK ) {
             AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(this);
             importUserUsageData
                     .setMessage(R.string.import_user_usage)
                     .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             Toast toast = Toast.makeText(MainActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.TOP, 0, 400);
                             toast.show();
                             toast = Toast.makeText(MainActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.TOP, 0, 400);
                             toast.show();
                             ExportImportDatabase.startImportThread(MainActivity.this,data.getData(),false);
                             mTracker.send(new HitBuilders.EventBuilder()
                                     .setCategory(ANALYTICS_CATEGORY)
                                     .setAction("import field from app file")
                                     .build());
                         }
                     })
                     .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             Toast toast = Toast.makeText(MainActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.TOP, 0, 400);
                             toast.show();
                             toast = Toast.makeText(MainActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.TOP, 0, 400);
                             toast.show();
                             ExportImportDatabase.startImportThread(MainActivity.this,data.getData(),true);
                             mTracker.send(new HitBuilders.EventBuilder()
                                     .setCategory(ANALYTICS_CATEGORY)
                                     .setAction("import field from app file")
                                     .build());
                         }
                     })
                     .create().show();
         } else if ( requestCode == DIRECTORY_CODE && resultCode == RESULT_OK && ( !isFinishing()) ) {
             ExportImportDatabase.startExportThread(this, to_export_fields, data.getData().getPath(), to_export_file_name);
             mTracker.send(new HitBuilders.EventBuilder()
                     .setCategory(ANALYTICS_CATEGORY)
                     .setAction("export field")
                     .build());
             Toast toast = Toast.makeText(this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
             toast.setGravity(Gravity.TOP, 0, 400);
             toast.show();
             toast = Toast.makeText(this, R.string.shut_down_warning, Toast.LENGTH_LONG);
             toast.setGravity(Gravity.TOP, 0, 400);
             toast.show();
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
        final Thread app_export_thread = ExportImportDatabase.getExportThread();
        final Thread app_import_thread = ExportImportDatabase.getImportThread();
        final Thread text_import_thread = ImportTextFile.getImportThread();
        switch (position) {
            case EXPORT_FIELD:
                if ((app_export_thread == null || !app_export_thread.isAlive()) &&
                        (app_import_thread == null || !app_import_thread.isAlive()) &&
                        (text_import_thread == null || !text_import_thread.isAlive()) ) {
                    ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                    exportFieldsDialogFragment.show(getSupportFragmentManager(), ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case IMPORT_FIELD:
                if ((app_export_thread == null || !app_export_thread.isAlive()) &&
                        (app_import_thread == null || !app_import_thread.isAlive()) &&
                        (text_import_thread == null || !text_import_thread.isAlive()) ) {
                    ImportFromWhatDialogFragment importFromWhatDialogFragment = new ImportFromWhatDialogFragment();
                    importFromWhatDialogFragment.show(getSupportFragmentManager(), importFromWhatDialogFragment.IMPORT_FROM_WHAT_DIALOG_FRAGMENT);
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
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("open review")
                        .build());
                break;
            case ABOUT:
                AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
                aboutDialogFragment.show(getSupportFragmentManager(),AboutDialogFragment.ABOUT_DIALOG_FRAGMENT);
                break;
/*            case FACEBOOK:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("open facebook page")
                        .build());
                break;
            case DROPBOX:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dropbox.com/sh/qq4rzots8vrb1mi/AAA6loAcxX6edP5bnHflXSFSa?dl=0"));
                startActivity(browserIntent);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("open dropbox page")
                        .build());
                break;*/
        }
    }

    private void handleIntent(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(QUERY_KEY, query);
        getLoaderManager().restartLoader(SEARCH_FIELDS_ID, bundle, getFields);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
    }

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
                        Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, first_field_id);
                        intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, false);
                        startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE,tts_available);
                    CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                    fragment.setArguments(bundle);
                    fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                }
                actionMenu.close(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("add entry from main activity")
                        .build());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE,tts_available);
                CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                actionMenu.close(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("create field")
                        .build());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySQLiteCursorLoader.cleanDatabase();
    }

    @Override
    public void deleteField(long field_id, MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder) {
        mySQLiteCursorLoader.deleteField(field_id,mHandler, mainActivityRecyclerViewViewHolder);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(ANALYTICS_CATEGORY)
                .setAction("delete field")
                .build());
    }

    @Override
    public void createField(String field_name, int field_type, int question_data_type,int check_type, boolean read_answer, String locale) {
        mySQLiteCursorLoader.createField(field_name, field_type, question_data_type,check_type,read_answer,locale);
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
    }
    @Override
    public void createField(String field_name, int field_type, int question_data_type) {
        mySQLiteCursorLoader.createField(field_name, field_type, question_data_type);
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
    }

    @Override
    public void exportFields(ArrayList<String> selectedFields, String file_name) {
        to_export_fields = selectedFields;
        to_export_file_name = file_name;
        // This always works
        Intent i = new Intent(this, FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, DIRECTORY_CODE);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(TO_EXPORT_FIELDS,to_export_fields);
        outState.putString(TO_EXPORT_FILE_NAME,to_export_file_name);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        to_export_fields = savedInstanceState.getStringArrayList(TO_EXPORT_FIELDS);
        to_export_file_name = savedInstanceState.getString(TO_EXPORT_FILE_NAME);
        super.onRestoreInstanceState(savedInstanceState);
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

    private void fff(){
        ArrayList<long[]> data = new DatabaseReadOperations(this).getBackupSchedulingData();
        Log.i("scheduled time number:",data.size()+"");
        for ( long[] session : data ) {
            if ( session[1] == ScheduleInFieldDialogFragment.QUICK_TEST ) {
                Log.i("scheduled time:",session[2]+"");
            }
            }
        Calendar cal = new GregorianCalendar(TimeZone.getDefault());
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        Log.i("Time Zone",cal.getTimeZone().getDisplayName());

    }
}
