package com.ahmedsameha1.peninbin.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.MainActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.MainActivityRecyclerViewViewHolder;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseHelper;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.fragments.MainActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.intent_services.CheckSubscriptionIntentService;
import com.ahmedsameha1.peninbin.intent_services.ExportFieldsIntentService;
import com.ahmedsameha1.peninbin.intent_services.ImportSWPFileIntentService;
import com.ahmedsameha1.peninbin.intent_services.ImportTextFileIntentService;
import com.ahmedsameha1.peninbin.receivers_services.NetworkReceiver;
import com.ahmedsameha1.peninbin.receivers_services.SimpleWakefulReceiver;
import com.ahmedsameha1.peninbin.secondary_activities.IntroActivity;
import com.ahmedsameha1.peninbin.secondary_activities.SpeciallyFormattedTextFileHelpActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Money;
import com.ahmedsameha1.peninbin.utils.PayloadString;
import com.ahmedsameha1.peninbin.utils.ShowEula;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.ahmedsameha1.peninbin.view.dialogs.AboutDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.CreateFieldDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.ExportFieldsDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.FieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.ImportFromWhatDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.ImportProvidedFieldDialogFragment;
import com.ahmedsameha1.peninbin.view.dialogs.ScheduleInFieldDialogFragment;
import com.android.vending.billing.IInAppBillingService;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONException;
import org.json.JSONObject;

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
        ExportFieldsDialogFragment.Contact,
        ImportFromWhatDialogFragment.Contact,
        SharedPreferences.OnSharedPreferenceChangeListener {
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
    public static final int IMPORT_PROVIDED_FIELD = 2;
    private static final int SUBSCRIBE = 3;
    public static final int ABOUT = 4;
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final String TO_EXPORT_FIELDS = "to_export_fields";
    private static final String TO_EXPORT_FILE_NAME = "to_export_file_name";
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    private static final String PAY_LOAD_STRING = "pay_load_string";
    private static final String BUY_INTENT = "BUY_INTENT";
    private static final int BUY_FLOW_REQUEST_CODE = 6475;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 16463;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1646574;
    private Handler mHandler = new Handler();
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private MainActivityRecyclerViewAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> getFields;
    private long first_field_id;
    private boolean tts_available = false;
    private Tracker mTracker;
    private String to_export_file_name;
    private ArrayList<String> to_export_fields;
    private IInAppBillingService billingService;
    private ServiceConnection mServiceConn;
    private NetworkReceiver networkReceiver;
    private String pay_load;
    private RecyclerView recyclerView;
    private Parcelable recyclerview_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivityFragmentDrawer drawerFragment;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); // this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.main_activity_recyclerview);
        drawerFragment = (MainActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        try {
            DatabaseHelper.getInstance(this).getWritableDatabase().rawQuery("SELECT _id FROM field", null);
        } catch (android.database.sqlite.SQLiteException e) {
            DatabaseHelper.getInstance(this).close();
//            recreate();
        }
        if (savedInstanceState != null) {
            recyclerview_position = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
        }
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
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 2;
        int spacing = getRecyclerViewSpacing();
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        addFab();
        runFirstTimeAppRunOnly();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                billingService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                billingService = IInAppBillingService.Stub.asInterface(service);
            }
        };
        getFields = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == GET_FIELDS_ID) {
                    /*return new CursorLoader(MainActivity.this, uri, new String[]{"rowid _id", Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE, Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME}, null, null,
                            "datetime(" + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + ") DESC");*/
                    mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(MainActivity.this,
                            "SELECT " + Contract.COLUMN_ID + ", " + Contract.Field.COLUMN_FIELD_NAME + ", " + Contract.Field.COLUMN_COLOR
                                    + " FROM " + Contract.Field.TABLE_NAME + "  ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC",
                            null);
                    return mySQLiteCursorLoader;
                } else if (id == SEARCH_FIELDS_ID) {
                    String query = args.getString(QUERY_KEY);
                    mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(MainActivity.this,
                            "SELECT " + Contract.COLUMN_ID + ", "
                                    + Contract.Field.COLUMN_FIELD_NAME + ", " + Contract.Field.COLUMN_COLOR
                                    + " FROM " + Contract.Field.TABLE_NAME + " WHERE " + Contract.Field.COLUMN_FIELD_NAME + " LIKE ? COLLATE NOCASE "
                                    + " ORDER BY " + Contract.Field.COLUMN_USAGE_COUNT + " DESC", new String[]{"%" + query + "%"});
                    return mySQLiteCursorLoader;
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
                if (recyclerview_position != null) {
                    recyclerView.getLayoutManager()
                            .onRestoreInstanceState(recyclerview_position);
                }
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
        new ShowEula(this).show();
        IntroActivity.show(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        startService(new Intent(getBaseContext(), CheckSubscriptionIntentService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (billingService != null) {
            unbindService(mServiceConn);
        }
        UsageLastTime.updateUsageLastTime(this);
    }

    private void runFirstTimeAppRunOnly() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(RUN_FIRST_TIME, true)) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Intent intent = new Intent(this, SimpleWakefulReceiver.class);
            int request = (int) System.currentTimeMillis();
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent pintent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pintent = PendingIntent.getBroadcast(this, request, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pintent = PendingIntent.getBroadcast(this, request, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);
            Log.i("time of service", calendar.getTimeInMillis() + "");
            Log.i("Time Zone", calendar.getTimeZone().getDisplayName());
            editor.putInt(MID_NIGHT_SERVICE_PENDINGINTENT_REQUEST, request);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts_available = true;
            }
        } else if (requestCode == BUY_FLOW_REQUEST_CODE) {
            int response_code = data.getIntExtra(Money.RESPONSE_CODE, 0);
            String purchaseData = data.getStringExtra(Money.RESPONSE_INAPP_PURCHASE_DATA);
            String dataSignature = data.getStringExtra(Money.RESPONSE_INAPP_SIGNATURE);
            if (resultCode == RESULT_OK && response_code == Money.BILLING_RESPONSE_RESULT_OK) {
                if (purchaseData != null && dataSignature != null) {
                    try {
                        if (Money.Security.verifyPurchase(Money.get64(Money.piece1 + Money.piece2 + Money.piece3 + Money.piece4 + Money.piece5 + Money.piece6
                                        + Money.piece7 + Money.piece8 + Money.piece9 + Money.piece10 + Money.piece11, getResources().getIntArray(R.array.values))
                                , purchaseData, dataSignature)) {
                            JSONObject jo = new JSONObject(purchaseData);
                            String pay_load = jo.getString("developerPayload");
                            String order_id = jo.getString("orderId");
                            if (pay_load.equals(this.pay_load) && new DatabaseReadOperations(this).isOrderIdUnique(order_id)) {
                                Money.putUserSubscriptionStatus(this, true);
                                Money.removeAllGetPurchasesErrors(this);
                                new CursorLoaderAndUpdateDatabase(this).addOrderId(order_id);
                                Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_success), Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory(ANALYTICS_CATEGORY)
                                        .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + "somebody has just completed a subscription")
                                        .build());
                            } else {
                                Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            }
                        } else {
                            Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                            toast1.setGravity(Gravity.TOP, 0, 400);
                            toast1.show();
                        }
                    } catch (JSONException e) {
                        Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                        e.printStackTrace();
                    }
                } else {
                    Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            } else {
                Toast toast1 = Toast.makeText(MainActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.TOP, 0, 400);
                toast1.show();
            }
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

        switch (position) {
            case EXPORT_FIELD:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            if (!ExportImportDatabase.isAnyThreadRunning() && !ImportTextFile.isThreadRunning()
                                    && !Emulation.isAnyThreadRunning()) {
                                new AlertDialog.Builder(this).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                                        exportFieldsDialogFragment.show(getSupportFragmentManager(), ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                                    }
                                }).create().show();
                            } else {
                                Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                        }
                    } else {
                        if (!ExportImportDatabase.isAnyThreadRunning() && !ImportTextFile.isThreadRunning()
                                && !Emulation.isAnyThreadRunning()) {
                            new AlertDialog.Builder(this).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                                    exportFieldsDialogFragment.show(getSupportFragmentManager(), ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                                }
                            }).create().show();
                        } else {
                            Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(this, R.string.external_storage_not_ready, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case IMPORT_FIELD:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY) ||
                        Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (!ExportImportDatabase.isAnyThreadRunning() && !ImportTextFile.isThreadRunning()
                                && !Emulation.isAnyThreadRunning()) {
                            new AlertDialog.Builder(MainActivity.this).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ImportFromWhatDialogFragment importFromWhatDialogFragment = new ImportFromWhatDialogFragment();
                                    importFromWhatDialogFragment.show(getSupportFragmentManager(), importFromWhatDialogFragment.IMPORT_FROM_WHAT_DIALOG_FRAGMENT);
                                }
                            }).create().show();
                        } else {
                            Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
                    }
                } else {
                    Toast toast = Toast.makeText(this, R.string.external_storage_not_ready, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case SUBSCRIBE:
                if (!Money.getUserSubscriptionStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.subscription_description)
                                .setMessage(R.string.subscription1_description)
                                .setPositiveButton(R.string.proceed_now, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pay_load = PayloadString.getString();
                                        try {
                                            Bundle bundle = billingService.getBuyIntent(3, MainActivity.this.getPackageName(), "subscription1", Money.SUBSCRIPTION_TYPE,
                                                    pay_load);
                                            int response_code = bundle.getInt(Money.RESPONSE_CODE);
                                            if (response_code == Money.BILLING_RESPONSE_RESULT_OK) {
                                                PendingIntent pendingIntent = bundle.getParcelable(BUY_INTENT);
                                                MainActivity.this.startIntentSenderForResult(pendingIntent.getIntentSender(), BUY_FLOW_REQUEST_CODE, new Intent(), 0, 0, 0);
                                            } else if (response_code == Money.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                                                Toast toast1 = Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                                                toast1.setGravity(Gravity.TOP, 0, 400);
                                                toast1.show();
                                            } else {
                                                Toast toast1 = Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.purchase_error), Toast.LENGTH_SHORT);
                                                toast1.setGravity(Gravity.TOP, 0, 400);
                                                toast1.show();
                                            }
                                        } catch (IntentSender.SendIntentException | RemoteException | NullPointerException e) {
                                            Toast toast1 = Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.purchase_error), Toast.LENGTH_SHORT);
                                            toast1.setGravity(Gravity.TOP, 0, 400);
                                            toast1.show();
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .create().show();
                    } else {
                        Toast toast = Toast.makeText(this, R.string.need_internet_try_again, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else {
                    if (Money.isInInitialPeriod(this)) {
                        Toast toast = Toast.makeText(this, R.string.intial_period, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(Money.MONEY)
                                .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + "try to subscribe - initial period")
                                .build());
                    } else {
                        Toast toast = Toast.makeText(this, R.string.already_subscribed, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(Money.MONEY)
                                .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + "already subscribed")
                                .build());
                    }
                }
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(Money.MONEY)
                        .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + "click to subscribe")
                        .build());
                break;
            case ABOUT:
                AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
                aboutDialogFragment.show(getSupportFragmentManager(), aboutDialogFragment.getClass().getName());
                break;

            case IMPORT_PROVIDED_FIELD:
                ImportProvidedFieldDialogFragment importProvidedFieldDialogFragment = new ImportProvidedFieldDialogFragment();
                importProvidedFieldDialogFragment.show(getSupportFragmentManager(), ImportProvidedFieldDialogFragment.class.getName());
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
        if (Money.getNetworkDownGetPurchasesesStatus(this)) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            networkReceiver = new NetworkReceiver(billingService);
            registerReceiver(networkReceiver, intentFilter);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        ActivityUtility.closeOpenedDialogs(this);
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
                    intent.putExtra(Constants.CURRENT_FIELD_ID, first_field_id);
                    intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, false);
                    MainActivity.this.startActivity(intent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE, tts_available);
                    CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                    fragment.setArguments(bundle);
                    fragment.show(MainActivity.this.getSupportFragmentManager(), CREATE_NEW_FIELD);
                }
                actionMenu.close(false);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(MainActivity.this) + "add entry from main activity")
                        .build());
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE, tts_available);
                CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(MainActivity.this.getSupportFragmentManager(), CREATE_NEW_FIELD);
                actionMenu.close(true);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(MainActivity.this) + "create field")
                        .build());
            }
        });
    }


    @Override
    public void deleteField(long field_id, MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder) {
        mySQLiteCursorLoader.deleteField(field_id, mHandler, mainActivityRecyclerViewViewHolder);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(ANALYTICS_CATEGORY)
                .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + "delete field")
                .build());
    }

    @Override
    public void createField(String field_name, int question_data_type, int check_type, boolean read_answer, String locale, int color) {
        mySQLiteCursorLoader.createField(field_name, question_data_type, check_type, read_answer, locale, color);
        getLoaderManager().restartLoader(GET_FIELDS_ID, null, getFields);
    }


    @Override
    public void exportFields(ArrayList<String> selectedFields, String file_name) {
        to_export_fields = selectedFields;
        to_export_file_name = file_name;
        // This always works
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.DIR_SELECT;
        properties.root = Environment.getExternalStorageDirectory();
        properties.error_dir = Environment.getExternalStorageDirectory();
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if (files.length > 0) {
                    Intent intent = new Intent(getBaseContext(), ExportFieldsIntentService.class);
                    intent.putExtra(ExportFieldsIntentService.SELECTED_FIELDS, to_export_fields);
                    intent.putExtra(ExportFieldsIntentService.FILE_PATH, files[0]);
                    intent.putExtra(ExportFieldsIntentService.FILE_NAME, to_export_file_name);
                    startService(intent);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(ANALYTICS_CATEGORY)
                            .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(MainActivity.this) + "export field")
                            .build());
                    Toast toast = Toast.makeText(MainActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void importFieldsFromAppFile(DialogFragment dialogFragment) {
        if (Build.VERSION.SDK_INT <= 27) {
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = Environment.getExternalStorageDirectory();
            properties.error_dir = Environment.getExternalStorageDirectory();
            properties.extensions = null;
            final FilePickerDialog dialog14 = new FilePickerDialog(this, properties);
            dialog14.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(final String[] files) {
                    //files is the array of the paths of files selected by the Application User.
                    if (files.length > 0) {
                        AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(MainActivity.this);
                        importUserUsageData
                                .setMessage(R.string.import_user_usage)
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog13, int which) {
                                        Toast toast = Toast.makeText(MainActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        Intent intent = new Intent(MainActivity.this, ImportSWPFileIntentService.class);
                                        intent.putExtra(ImportSWPFileIntentService.FILE_PATH, files[0]);
                                        intent.putExtra(ImportSWPFileIntentService.USER_USAGE, false);
                                        MainActivity.this.startService(intent);
                                        dialogFragment.dismiss();
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "")
                                                        + ActivityUtility.getVersionCode(MainActivity.this) + "import field from app file")
                                                .build());
                                    }
                                })
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog12, int which) {
                                        Toast toast = Toast.makeText(MainActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        Intent intent = new Intent(MainActivity.this, ImportSWPFileIntentService.class);
                                        intent.putExtra(ImportSWPFileIntentService.FILE_PATH, files[0]);
                                        intent.putExtra(ImportSWPFileIntentService.USER_USAGE, true);
                                        MainActivity.this.startService(intent);
                                        dialogFragment.dismiss();
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                                .setAction((Money.getMe(MainActivity.this) ? Money.TRACKING_ME : "")
                                                        + ActivityUtility.getVersionCode(MainActivity.this) + "import field from app file")
                                                .build());
                                    }
                                })
                                .create().show();
                    }
                }
            });
            dialog14.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogFragment.dismiss();
                }
            });
            dialog14.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogFragment.dismiss();
                }
            });
            dialog14.show();
        } else {
            Intent intent = new Intent(MainActivity.this, UseSimpleStorageActivity.class);
            intent.putExtra(UseSimpleStorageActivity.IMPORT_FROM_WHAT, UseSimpleStorageActivity.FROM_APP_FILE);
            startActivity(intent);
        }
    }

    @Override
    public void importFieldsFromTextFile(DialogFragment dialogFragment) {
        if (Build.VERSION.SDK_INT <= 27) {
            Context context = this;
            DialogProperties properties = new DialogProperties();
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.selection_type = DialogConfigs.FILE_SELECT;
            properties.root = Environment.getExternalStorageDirectory();
            properties.error_dir = Environment.getExternalStorageDirectory();
            properties.extensions = null;

            final FilePickerDialog dialog1 = new FilePickerDialog(context, properties);

            dialog1.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    //files is the array of the paths of files selected by the Application User.
                    if (files.length > 0) {
                        Toast toast = Toast.makeText(context, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Intent intent = new Intent(((Activity) context).getBaseContext(), ImportTextFileIntentService.class);
                        intent.putExtra(ImportTextFileIntentService.FILE_PATH, files[0]);
                        context.startService(intent);
                        dialogFragment.dismiss();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                .setAction((Money.getMe(context) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(context) + "import field from text file")
                                .build());
                    }
                }
            });
            dialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogFragment.dismiss();
                }
            });
            dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogFragment.dismiss();
                }
            });
            dialog1.show();
        } else {
            Intent intent = new Intent(this, UseSimpleStorageActivity.class);
            intent.putExtra(UseSimpleStorageActivity.IMPORT_FROM_WHAT, UseSimpleStorageActivity.FROM_TEXT_FILE);
            startActivity(intent);
        }
    }

    @Override
    public void textFileHelp(DialogFragment dialogFragment) {
        startActivity(new Intent(this, SpeciallyFormattedTextFileHelpActivity.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(TO_EXPORT_FIELDS, to_export_fields);
        outState.putString(TO_EXPORT_FILE_NAME, to_export_file_name);
        outState.putString(PAY_LOAD_STRING, pay_load);
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        to_export_fields = savedInstanceState.getStringArrayList(TO_EXPORT_FIELDS);
        to_export_file_name = savedInstanceState.getString(TO_EXPORT_FILE_NAME);
        pay_load = savedInstanceState.getString(PAY_LOAD_STRING);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Money.NETWORK_DOWN_GET_PURCHASES_STATUS)) {
            if (!Money.getNetworkDownGetPurchasesesStatus(this)) {
                if (Money.getUserSubscriptionStatus(this)) {
                    unregisterReceiver(networkReceiver);
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
                }
            }
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!ExportImportDatabase.isAnyThreadRunning() && !ImportTextFile.isThreadRunning()
                        && !Emulation.isAnyThreadRunning()) {
                    new AlertDialog.Builder(this).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ExportFieldsDialogFragment exportFieldsDialogFragment = new ExportFieldsDialogFragment();
                            exportFieldsDialogFragment.show(getSupportFragmentManager(), ExportFieldsDialogFragment.EXPORT_FIELD_DIALOG_FRAGMENT);
                        }
                    }).create().show();
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(MainActivity.this, R.string.denied_permission, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!ExportImportDatabase.isAnyThreadRunning() && !ImportTextFile.isThreadRunning()
                        && !Emulation.isAnyThreadRunning()) {
                    new AlertDialog.Builder(MainActivity.this).setMessage(R.string.intellectual_property).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ImportFromWhatDialogFragment importFromWhatDialogFragment = new ImportFromWhatDialogFragment();
                            importFromWhatDialogFragment.show(getSupportFragmentManager(), importFromWhatDialogFragment.IMPORT_FROM_WHAT_DIALOG_FRAGMENT);
                        }
                    }).create().show();
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(MainActivity.this, R.string.denied_permission, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        }
    }

    private int getRecyclerViewSpacing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return 16;
        } else {
            return 3;
        }
    }
}
