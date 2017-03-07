package com.ahmedsameha1.peninbin.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
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
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.FieldActivityRecyclerViewAdapter;
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
import com.ahmedsameha1.peninbin.dialogs.SearchDateOrTextDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.SortEntriesDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.StudyDuringDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.EnhancedTestDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.FullyRandomTestDialogFragment;
import com.ahmedsameha1.peninbin.fragments.FieldActivityFragmentDrawer;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.receivers_services.NetworkReceiver;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Money;
import com.ahmedsameha1.peninbin.utils.PayloadString;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;


public class FieldActivity extends AppCompatActivity implements
        FieldActivityFragmentDrawer.FragmentDrawerListener,
        SearchDateOrTextDialogFragment.Contact,
        SortEntriesDialogFragment.Contact,
        DeleteEntryDialogFragment.Contact,
        FieldPropertiesDialogFragment.Contact,
        CopyFieldDialogFragment.CopyField,
        TextToSpeech.OnInitListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SCREEN_NAME = "FieldActivity";
    public static final int STUDY_PERIOD = 0;
    public static final int ENHANCED_TEST = 1;
    public static final int FULLY_RANDOM_TEST = 2;
    public static final int SCHEDULE_IN_FIELD = 3;
    public static final int COPY_MOVE = 4;
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
    public static final int SORT_ENTRY_ORDER_ASC = 8;
    public static final String CURRENT_FIELD_NAME = "currentField";
    public static final String CHECK_IF_CONTINUE_STUDY_DURING_SESSION = "checkIfContinueStudyDuringSession";
    public static final String CHECK_IF_CONTINUE_TEST_SESSION = "checkIfContinueTestSession";
    private static final String CURRENT_SORT = "currentSort";
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
    private static final String PAY_LOAD_STRING = "pay_load_string";
    private static final String BUY_INTENT = "BUY_INTENT";
    private static final int BUY_FLOW_REQUEST_CODE = 6475;
    private static final String INITIAL_CHECK_SUPSCRIPTION = "intial_check_supscription";
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
            "datetime(" + Contract.CREATION_DATE + ") ASC",
            Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, " + "datetime(" + Contract.CREATION_DATE + ") ASC"};
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private DatabaseReadOperations databaseOperations;
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
    private IInAppBillingService billingService;
    private ServiceConnection mServiceConn;
    private NetworkReceiver networkReceiver;
    private SharedPreferences sharedPreferences;
    private String pay_load;
    private boolean initialCheckSubscription = true;


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
        databaseOperations = new DatabaseReadOperations(this);
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
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                billingService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                billingService = IInAppBillingService.Stub.asInterface(service);
                if ( Money.getNetworkDownGetPurchasesesStatus(FieldActivity.this) &&
                        NetworkReceiver.isOnline(FieldActivity.this) ) {
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
                    int sort = args.getInt(CURRENT_SORT);
                    if (id == SEARCH_ENTRIES_LOADER_ID) {
                        String searchString = args.getString(QUERY_KEY);
                        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(FieldActivity.this,
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
                        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(FieldActivity.this,
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

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkReceiver);
        } catch ( IllegalArgumentException e ) {
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
                mySQLiteCursorLoader.editEntry(entry_id, question_data_type, question, answer, rank);
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
                            if ( pay_load.equals(this.pay_load) && new DatabaseReadOperations(this).isOrderIdUnique(order_id) ) {
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
        if ( mServiceConn != null ) {
            unbindService(mServiceConn);
        }
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
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        boolean user_eligible = Money.getUserSubscriptionStatus(this);
        user_eligible = true; // TODO: 2/13/17 must comment this line before publish 
        switch (position) {
            case ENHANCED_TEST:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    startEnhancedTest();
                } else if (Money.getNetworkDownGetPurchasesesStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    } else {
                        initialCheckSubscription = false;
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.need_internet), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.notified_if_eligible), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                    }
                } else if (Money.getUnsolvableErrorGetPurchases(this)) {
                    Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else {
                    startPurchaseFlow();
                }
                break;
            case FULLY_RANDOM_TEST:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    startFulllyRandomTest();
                } else if (Money.getNetworkDownGetPurchasesesStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    } else {
                        initialCheckSubscription = false;
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.need_internet), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.notified_if_eligible), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                    }
                } else if (Money.getUnsolvableErrorGetPurchases(this)) {
                    Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else {
                    startPurchaseFlow();
                }
                break;
            case STUDY_PERIOD:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                   startStudyAPeriod();
                } else if (Money.getNetworkDownGetPurchasesesStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    } else {
                        initialCheckSubscription = false;
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.need_internet), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.notified_if_eligible), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                    }
                } else if (Money.getUnsolvableErrorGetPurchases(this)) {
                    Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else {
                    startPurchaseFlow();
                }
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
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    ScheduleInFieldDialogFragment scheduleInFieldDialogFragment = new ScheduleInFieldDialogFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    scheduleInFieldDialogFragment.setArguments(bundle1);
                    scheduleInFieldDialogFragment.show(getSupportFragmentManager(), ScheduleInFieldDialogFragment.SCHEDULE_IN_FIELD);
                } else if (Money.getNetworkDownGetPurchasesesStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    } else {
                        initialCheckSubscription = false;
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.need_internet), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.notified_if_eligible), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                    }
                } else if (Money.getUnsolvableErrorGetPurchases(this)) {
                    Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else {
                    startPurchaseFlow();
                }
                break;
            case Field_INFORMATION:
                Intent intent = new Intent(this, FieldInformationActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                startActivity(intent);
                break;

            case COPY_MOVE:
                if (user_eligible || Money.getUserAllowedDueAnError(this)) {
                    CopyMoveDialogFragment copyMoveDialogFragment = new CopyMoveDialogFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    copyMoveDialogFragment.setArguments(bundle2);
                    copyMoveDialogFragment.show(getSupportFragmentManager(), CopyMoveDialogFragment.COPY_MOVE_DIALOG_FRAGMENT);
                } else if (Money.getNetworkDownGetPurchasesesStatus(this)) {
                    if (NetworkReceiver.isOnline(this)) {
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    } else {
                        initialCheckSubscription = false;
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                        Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.need_internet), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.notified_if_eligible), Toast.LENGTH_SHORT);
                        toast1.setGravity(Gravity.TOP, 0, 400);
                        toast1.show();
                    }
                } else if (Money.getUnsolvableErrorGetPurchases(this)) {
                    Toast toast = Toast.makeText(FieldActivity.this, getString(R.string.error), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else {
                    startPurchaseFlow();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CURRENT_SORT, currentSort);
        savedInstanceState.putLong(FIELD_ID, current_field_id);
        savedInstanceState.putString(PAY_LOAD_STRING, pay_load);
        savedInstanceState.putBoolean(INITIAL_CHECK_SUPSCRIPTION, initialCheckSubscription);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        pay_load = savedInstanceState.getString(PAY_LOAD_STRING);
        initialCheckSubscription = savedInstanceState.getBoolean(INITIAL_CHECK_SUPSCRIPTION);
        super.onRestoreInstanceState(savedInstanceState);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Bundle bundle = new Bundle();

        switch (id) {
            case R.id.sort:
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                SortEntriesDialogFragment sortEntriesDialogFragment = new SortEntriesDialogFragment();
                sortEntriesDialogFragment.setArguments(bundle);
                sortEntriesDialogFragment.show(getSupportFragmentManager(), SORT_DIALOG);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction("sort")
                        .build());
                return true;
            case R.id.text_shortcuts:
                Intent intent = new Intent(this, SymbolActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startPurchaseFlow() {
        if (NetworkReceiver.isOnline(this)) {
            pay_load = PayloadString.getString();
            try {
                Bundle bundle = billingService.getBuyIntent(3, this.getPackageName(), "subscription1", Money.SUBSCRIPTION_TYPE,
                        pay_load);
                int response_code = bundle.getInt(Money.RESPONSE_CODE);
                if (response_code == Money.BILLING_RESPONSE_RESULT_OK) {
                    PendingIntent pendingIntent = bundle.getParcelable(BUY_INTENT);
                    startIntentSenderForResult(pendingIntent.getIntentSender(), BUY_FLOW_REQUEST_CODE, new Intent(), 0, 0, 0);
                } else if (response_code == Money.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                    Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.cannot_reach_service), Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                } else {
                    Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            } catch (IntentSender.SendIntentException | RemoteException | NullPointerException e) {
                Toast toast1 = Toast.makeText(FieldActivity.this, getString(R.string.purchase_error), Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.TOP, 0, 400);
                toast1.show();
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(this, R.string.need_internet_try_again, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
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
    public void updateFieldName(String name) {
        getSupportActionBar().setTitle(name);
    }

    @Override
    public void deleteEntry(long entry_id, int question_data_type) {
        mySQLiteCursorLoader.deleteEntry(entry_id, question_data_type);
    }

    @Override
    public void copyField(long field_id) {
        mySQLiteCursorLoader.copyField(current_field_id, field_id);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Money.NETWORK_DOWN_GET_PURCHASES_STATUS)) {
            if (!Money.getNetworkDownGetPurchasesesStatus(this)) {
                if (Money.getUserSubscriptionStatus(this)) {
                    unregisterReceiver(networkReceiver);
                    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
                    if ( !initialCheckSubscription ) {
                        NotificationCompat.Builder notificationBuilder = null;
                        Intent notifyIntent = null;
                        PendingIntent resultPendingIntent = null;
                        TaskStackBuilder stackBuilder = null;
                        notificationBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.ic_launcher512)
                                        .setContentTitle(this.getResources().getString(R.string.proceed_now))
                                        .setContentText(this.getResources().getString(R.string.have_the_right))
                                        .setTicker(this.getResources().getString(R.string.have_the_right))
                                        .setAutoCancel(true);
                        notifyIntent = new Intent(this, MainActivity.class);
                        stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(notifyIntent);
                        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        notificationBuilder.setContentIntent(resultPendingIntent);
                        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify((int) System.currentTimeMillis(), notificationBuilder.build());
                    }
                }
            }
        }
    }

    private void startEnhancedTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.PAUSING + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseOperations.getEntriesNumberForTests(current_field_id, question_data_type) > 0) {
                if (databaseOperations.isThisFieldTestPausedEnhancedTest(current_field_id)) {
                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                    bundle.putInt(AskAboutContinuePreviousTestDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                    fragment3.setArguments(bundle);
                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
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
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    private void startFulllyRandomTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(FullyRandomTestActivity.ACTIVITY_NAME + FullyRandomTestActivity.PAUSING + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseOperations.getEntriesNumberForTests(current_field_id, question_data_type) > 0) {
                if (databaseOperations.isThisFieldTestPausedFullyRandomTest(current_field_id)) {
                    AskAboutContinuePreviousTestDialogFragment fragment3 = new AskAboutContinuePreviousTestDialogFragment();
                    bundle.putInt(AskAboutContinuePreviousTestDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                    fragment3.setArguments(bundle);
                    fragment3.show(getSupportFragmentManager(), CHECK_IF_CONTINUE_TEST_SESSION);
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
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_test_pausing_finish, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
    }

    private void startStudyAPeriod() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FieldActivity.this);
        Bundle bundle = new Bundle();
        bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
        boolean isEmulated = databaseOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        if (!sharedPreferences.contains(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.PAUSING + current_field_id)
                && !Emulation.isAnyThreadRunning()) {
            if (databaseOperations.getEntriesNumberForStudyAPeriod(current_field_id, question_data_type) > 0) {
                if (databaseOperations.isThisFieldStudyPeriodPaused(current_field_id)) {
                    AskAboutContinuePreviousStudyAPeriodDialogFragment fragment2 = new AskAboutContinuePreviousStudyAPeriodDialogFragment();
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
                if (isEmulated) {
                    Toast toast1 = Toast.makeText(FieldActivity.this, R.string.field_emulated, Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.TOP, 0, 400);
                    toast1.show();
                }
            }

        } else {
            Toast toast = Toast.makeText(FieldActivity.this, R.string.wait_until_study_during_pausing_finish, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
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
