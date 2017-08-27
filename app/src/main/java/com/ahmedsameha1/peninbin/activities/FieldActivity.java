package com.ahmedsameha1.peninbin.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.FieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousStudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.AskAboutContinuePreviousTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.CopyFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.CopyMoveDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FieldPropertiesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyAPeriodDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.EnhancedTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FullyRandomTestDialogFragment;
import com.ahmedsameha1.peninbin.fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.receivers_services.NetworkReceiver;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Money;
import com.ahmedsameha1.peninbin.utils.SortOptions;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


public class FieldActivity extends AppCompatActivity implements
        FieldActivityFragmentDrawer.FragmentDrawerListener,
        SortEntriesDialogFragment.Contact,
        DeleteEntryDialogFragment.Contact,
        FieldPropertiesDialogFragment.Contact,
        CopyFieldDialogFragment.CopyField,
        TextToSpeech.OnInitListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        FieldActivityRecyclerViewAdapter.Contact {
    public static final String SCREEN_NAME = "FieldActivity";
    public static final int STUDY_PERIOD = 0;
    public static final int ENHANCED_TEST = 1;
    public static final int FULLY_RANDOM_TEST = 2;
    public static final int SCHEDULE_IN_FIELD = 3;
    public static final int COPY_MOVE = 4;
    public static final int FIELD_PROPERTIES = 5;
    public static final int Field_INFORMATION = 6;
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final String CURRENT_FIELD_NAME = "currentField";
    public static final String CHECK_IF_CONTINUE_STUDY_DURING_SESSION = "checkIfContinueStudyDuringSession";
    public static final String CHECK_IF_CONTINUE_TEST_SESSION = "checkIfContinueTestSession";
    private static final String SORT_DIALOG = "sort_dialog";
    public static final String FIELD_ID = "field_id";
    public static final int EDIT_ENTRY = 0;
    public static final String EDITED_ENTRY_ID = "edited_entry_id";
    public static final String EDITED_QUESTION = "edited_question";
    public static final String EDITED_ANSWER = "edited_answer";
    public static final String EDITED_ORDER = "edited_order";
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final String FIELD_OPTIONS_DIALOG = "field_options_dialog";
    public static final String ANALYTICS_CATEGORY = "Field Activity Category";
    public static final String EDITED_RANK = "edited_rank";
    private static final String PAY_LOAD_STRING = "pay_load_string";
    private static final int BUY_FLOW_REQUEST_CODE = 6475;
    private static final String INITIAL_CHECK_SUPSCRIPTION = "intial_check_supscription";
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    private static final String IS_LOAD_ENTRIES = "is_load_entries";
    private static final String SEARCH_VALUE = "search_value";
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    private long current_field_id;
    private SortOptions sortOption;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private DatabaseReadOperations databaseReadOperations;
    private FieldActivityRecyclerViewAdapter mAdapter;
    private boolean tts_available = false;
    private ArrayList<Locale> localeList = new ArrayList<Locale>();
    private ArrayList<String> locale_display_names = new ArrayList<String>();
    private TextToSpeech tts;
    private boolean is_load_entries = true;
    private String search_value;
    private int question_data_type;
    private Tracker mTracker;
    private IInAppBillingService billingService;
    private ServiceConnection mServiceConn;
    private NetworkReceiver networkReceiver;
    private SharedPreferences sharedPreferences;
    private String pay_load;
    private boolean initialCheckSubscription = true;
    private StringBuilder search_string_highlight = new StringBuilder();
    private View mLoadingView;
    private RelativeLayout content;
    private RecyclerView recyclerView;
    private TextView not_found;
    private Parcelable recyclerview_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        FieldActivityFragmentDrawer drawerFragment;

        if (savedInstanceState != null) {
            current_field_id = savedInstanceState.getLong(FIELD_ID);
            recyclerview_position = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
        } else {
            current_field_id = getIntent().getLongExtra(FIELD_ID, 0);
        }
        databaseReadOperations = new DatabaseReadOperations(this);
        question_data_type = databaseReadOperations.getQuestionDataType(current_field_id);
        sortOption = databaseReadOperations.getFieldSort(current_field_id);
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        not_found = (TextView) findViewById(R.id.not_found);
        ActivityUtility.antiCrossFade(content, mLoadingView, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity); // FieldActivity.this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        drawerFragment = (FieldActivityFragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseReadOperations.getFieldName(current_field_id));
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(FieldActivity.this);
        mAdapter = new FieldActivityRecyclerViewAdapter(FieldActivity.this, null, current_field_id, search_string_highlight);
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
                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, true);
                FieldActivity.this.startActivity(intent);
            }
        });
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                billingService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                billingService = IInAppBillingService.Stub.asInterface(service);
                if (Money.getNetworkDownGetPurchasesesStatus(FieldActivity.this) &&
                        NetworkReceiver.isOnline(FieldActivity.this)) {
                    Money.checkSubscription(FieldActivity.this, billingService);
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
                    sharedPreferences.registerOnSharedPreferenceChangeListener(FieldActivity.this);
                }
            }
        };
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (question_data_type == Contract.Field.STRING_DATA_TYPE) {
                    if (id == SEARCH_ENTRIES_LOADER_ID && search_value != null) {
                        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                        + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + "  q LIKE ? "
                                        + " OR " + " se." + Contract.String_Entry.COLUMN_FIELD + " = ? AND "
                                        + " a LIKE ? COLLATE NOCASE ORDER BY " + sortOption.getSortString(),
                                new String[]{current_field_id + "", "%" + search_value + "%", current_field_id + "", "%" + search_value + "%"});
                        return cursorLoaderAndUpdateDatabase;

                    } else if (id == GET_ENTRIES_LOADER_ID) {
                        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(FieldActivity.this,
                                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                                        + " a, se." + Contract.String_Entry.COLUMN_ASKED_COUNT + ", se." + Contract.String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                                        + " FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                                        + "ORDER BY " + sortOption.getSortString(),
                                new String[]{current_field_id + ""});
                        return cursorLoaderAndUpdateDatabase;

                    }
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
                if (data.getCount() == 0 && search_value != null) {
                    not_found.setText(R.string.not_found);
                    not_found.setVisibility(View.VISIBLE);
                } else {
                    not_found.setText("");
                    not_found.setVisibility(View.GONE);
                }
                ActivityUtility.crossFade(content, mLoadingView, FieldActivity.this);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        };
        getLoaderManager();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
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
                long entry_id = data.getLongExtra(EDITED_ENTRY_ID, 1);
                String question = data.getStringExtra(EDITED_QUESTION);
                String answer = data.getStringExtra(EDITED_ANSWER);
                int rank = data.getIntExtra(EDITED_RANK, 0);
                int order = data.getIntExtra(EDITED_ORDER, 0);
                cursorLoaderAndUpdateDatabase.editEntry(entry_id, question_data_type, question, answer, rank, order);
            }
        } else if (requestCode == MY_DATA_CHECK_CODE) {
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
                                Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_success), Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            } else {
                                Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                                toast1.setGravity(Gravity.TOP, 0, 400);
                                toast1.show();
                            }
                        } else {
                            Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                            toast1.setGravity(Gravity.TOP, 0, 400);
                            toast1.show();
                        }
                    } catch (JSONException e) {
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                        e.printStackTrace();
                    }
                } else {
                    Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            } else {
                Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_LONG);
                toast1.setGravity(Gravity.TOP, 0, 400);
                toast1.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (is_load_entries) {
            getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, getFieldEntries);
            search_string_highlight.replace(0, search_string_highlight.length(), "");
        } else {
            search_string_highlight.replace(0, search_string_highlight.length(), search_value);
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, null, getFieldEntries);
        }
        if (Money.getNetworkDownGetPurchasesesStatus(this)) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            networkReceiver = new NetworkReceiver(billingService);
            registerReceiver(networkReceiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
        getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
        getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
        ActivityUtility.antiCrossFade(content, mLoadingView, FieldActivity.this);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search_value =
                    intent.getStringExtra(SearchManager.QUERY);
            is_load_entries = false;
            getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
            getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
            ActivityUtility.antiCrossFade(content, mLoadingView, FieldActivity.this);
        }
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
        boolean user_eligible = Money.getUserSubscriptionStatus(this);
        switch (position) {
            case ENHANCED_TEST:
                startEnhancedTest();
                break;
            case FULLY_RANDOM_TEST:
                startFullyRandomTest();
                break;
            case STUDY_PERIOD:
                startStudyAPeriod();
                break;
            case FIELD_PROPERTIES:
                String locale_short_name = databaseReadOperations.getFieldLocale(current_field_id);
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
                        .setAction((Money.getMe(this) ? "ME" : "") + ActivityUtility.getVersionCode(this) + "field properties")
                        .build());
                break;
            case SCHEDULE_IN_FIELD:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    ScheduleInFieldDialogFragment scheduleInFieldDialogFragment = new ScheduleInFieldDialogFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                    scheduleInFieldDialogFragment.setArguments(bundle1);
                    scheduleInFieldDialogFragment.show(getSupportFragmentManager(), ScheduleInFieldDialogFragment.SCHEDULE_IN_FIELD);
                } else {
                    Toast toast = Toast.makeText(this, R.string.please_subscribe, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
            case Field_INFORMATION:
                Intent intent = new Intent(this, FieldInformationActivity.class);
                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                startActivity(intent);
                break;

            case COPY_MOVE:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    CopyMoveDialogFragment copyMoveDialogFragment = new CopyMoveDialogFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                    copyMoveDialogFragment.setArguments(bundle2);
                    copyMoveDialogFragment.show(getSupportFragmentManager(), CopyMoveDialogFragment.COPY_MOVE_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(this, R.string.please_subscribe, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(FIELD_ID, current_field_id);
        savedInstanceState.putString(PAY_LOAD_STRING, pay_load);
        savedInstanceState.putBoolean(INITIAL_CHECK_SUPSCRIPTION, initialCheckSubscription);
        if (mAdapter.getCursor() != null) {
            recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        savedInstanceState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);
        savedInstanceState.putBoolean(IS_LOAD_ENTRIES, is_load_entries);
        savedInstanceState.putString(SEARCH_VALUE, search_value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pay_load = savedInstanceState.getString(PAY_LOAD_STRING);
        initialCheckSubscription = savedInstanceState.getBoolean(INITIAL_CHECK_SUPSCRIPTION);
        is_load_entries = savedInstanceState.getBoolean(IS_LOAD_ENTRIES);
        search_value = savedInstanceState.getString(SEARCH_VALUE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; FieldActivity.this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_activity_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (search_value != null) {
            searchItem.expandActionView();
            searchView.setQuery(search_value, false);
        }
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, getFieldEntries);
                        search_string_highlight.replace(0, search_string_highlight.length(), "");
                        recyclerView.setLayoutManager(new LinearLayoutManager(FieldActivity.this));
                        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
                        is_load_entries = true;
                        search_value = null;
                        return true;
                    }
                });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Bundle bundle = new Bundle();

        switch (id) {
            case R.id.sort:
                bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                bundle.putSerializable(SortEntriesDialogFragment.INITIAL_SORT_OPTION, sortOption);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction((Money.getMe(this) ? "ME" : "") + ActivityUtility.getVersionCode(this) + "sort")
                        .build());
                return true;
            case R.id.today_emulation:
                if (!Emulation.isAnyThreadRunning()) {
                        final Intent intent1 = new Intent(this,
                                SearchActivity.class);
                        // add query to the Intent Extras
                        intent1.setAction(Intent.ACTION_SEARCH);
                        intent1.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                        intent1.putExtra(SearchActivity.SEARCH_WHAT, SearchActivity.TODAY_ENTRIES);
                        startActivity(intent1);
                } else {
                    Toast toast = Toast.makeText(this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                    finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void sort(SortOptions sortOption) {
        this.sortOption = sortOption;
        getLoaderManager().destroyLoader(GET_ENTRIES_LOADER_ID);
        getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ActivityUtility.antiCrossFade(content, mLoadingView, FieldActivity.this);
        if (is_load_entries) {
            getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, getFieldEntries);
        } else {
            getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, null, getFieldEntries);
        }
    }

    @Override
    public void updateFieldName(String name) {
        getSupportActionBar().setTitle(name);
    }

    @Override
    public void deleteEntry(long entry_id, int question_data_type) {
        cursorLoaderAndUpdateDatabase.deleteEntry(entry_id, question_data_type);
    }

    @Override
    public void copyField(long field_id) {
        cursorLoaderAndUpdateDatabase.copyField(current_field_id, field_id);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Money.NETWORK_DOWN_GET_PURCHASES_STATUS)) {
            if (!Money.getNetworkDownGetPurchasesesStatus(this)) {
                if (Money.getUserSubscriptionStatus(this)) {
                    unregisterReceiver(networkReceiver);
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
                    if (!initialCheckSubscription) {
                        NotificationCompat.Builder notificationBuilder = null;
                        Intent notifyIntent = null;
                        PendingIntent resultPendingIntent = null;
                        TaskStackBuilder stackBuilder = null;
                        notificationBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.ic_launcher48)
                                        .setContentTitle(this.getResources().getString(R.string.proceed_now))
                                        .setContentText(this.getResources().getString(R.string.have_the_right))
                                        .setTicker(this.getResources().getString(R.string.have_the_right))
                                        .setAutoCancel(true);
                        notifyIntent = new Intent(this, MainActivity.class);
                        stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(notifyIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify((int) System.currentTimeMillis(), notificationBuilder.build());
                    }
                }
            }
        }
    }

    private void startEnhancedTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        final Bundle bundle = new Bundle();
        bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(EnhancedTestActivity.class.getName() + Constants.PAUSING + current_field_id)
                && !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseReadOperations.getEntriesNumberForTests(current_field_id, question_data_type) > 0) {
                if (databaseReadOperations.isThisFieldTestPausedEnhancedTest(current_field_id)) {
                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                    bundle.putInt(AskAboutContinuePreviousTestDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                    fragment3.setArguments(bundle);
                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
                } else if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, current_field_id)) {
                    new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FieldActivity.this, AskAgainAfterTestActivity.class);
                                    intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                                    intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                    FieldActivity.this.startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, question_data_type);
                                    cursorLoaderAndUpdateDatabase.
                                            clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST,
                                                    current_field_id);
                                    EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(FieldActivity.this.getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                                }
                            }).create().show();
                } else {
                    EnhancedTestDialogFragment dialogFragment = new EnhancedTestDialogFragment();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), EnhancedTestDialogFragment.ENHANCED_TEST_DIALOG_FRAGMENT);
                }
            } else {
                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                if (isEmulated) {
                    Toast toast1 = Toast.makeText(FieldActivity.this, R.string.field_emulated, Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            }
        } else {
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    private void startFullyRandomTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        final Bundle bundle = new Bundle();
        bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(FullyRandomTestActivity.class.getName() + Constants.PAUSING + current_field_id)
                && !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseReadOperations.getEntriesNumberForTests(current_field_id, question_data_type) > 0) {
                if (databaseReadOperations.isThisFieldTestPausedFullyRandomTest(current_field_id)) {
                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                    bundle.putInt(AskAboutContinuePreviousTestDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                    fragment3.setArguments(bundle);
                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
                } else if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id)) {
                    new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(FieldActivity.this, AskAgainAfterTestActivity.class);
                                    intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                    intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                                    intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                    FieldActivity.this.startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, question_data_type);
                                    cursorLoaderAndUpdateDatabase.clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id);
                                    FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.show(FieldActivity.this.getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                                }
                            }).create().show();
                } else {
                    FullyRandomTestDialogFragment dialogFragment = new FullyRandomTestDialogFragment();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getSupportFragmentManager(), FullyRandomTestDialogFragment.FULLY_RANDOM_TEST_DIALOG_FRAGMENT);
                }
            } else {
                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                if (isEmulated) {
                    Toast toast1 = Toast.makeText(FieldActivity.this, R.string.field_emulated, Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            }
        } else {
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    private void startStudyAPeriod() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(StudyAPeriodActivity.class.getName() + Constants.PAUSING + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseReadOperations.getEntriesNumberForStudyAPeriod(current_field_id, question_data_type) > 0) {
                if (databaseReadOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_PERIOD, current_field_id)) {
                    AskAboutContinuePreviousStudyAPeriodDialogFragment fragment2 = new AskAboutContinuePreviousStudyAPeriodDialogFragment();
                    fragment2.setArguments(bundle);
                    fragment2.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_STUDY_DURING_SESSION);
                } else {
                    StudyAPeriodDialogFragment fragment2 = new StudyAPeriodDialogFragment();
                    fragment2.setArguments(bundle);
                    fragment2.show(getSupportFragmentManager(), fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                }
            } else {
                Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.no_entries_in_field), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
                if (isEmulated) {
                    Toast toast1 = Toast.makeText(FieldActivity.this, R.string.field_emulated, Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            }

        } else {
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    @Override
    public void substituteQuestion(long entry_id, String text) {
        cursorLoaderAndUpdateDatabase.substituteQuestion(entry_id, question_data_type, text);
    }

    @Override
    public void substituteAnswer(long entry_id, String text) {
        cursorLoaderAndUpdateDatabase.substituteAnswer(entry_id, question_data_type, text);
    }

    @Override
    public void startStudyActivity(long entry_id, String question, String answer) {
        Intent intent = new Intent(this, StudyActivity.class);
        intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
        intent.putExtra(StudyActivity.ENTRY_ID, entry_id);
        if (search_value != null) {
            intent.putExtra(StudyActivity.SEARCH_VALUE, search_value);
        }
        startActivity(intent);
    }
}
