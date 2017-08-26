package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
import com.ahmedsameha1.peninbin.adapters.WrongAnswersActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongAnswersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String SCREEN_NAME = "WrongAnswersActivity";
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int PREPARE_WRONG_LIST_LOADER_ID = 1;
    private static WrongAnswersActivity wrongAnswersActivity;
    private long current_field_id;
    private ArrayList<WrongItem> wrongItemList;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private WrongAnswersActivityRecyclerViewAdapter mAdapter;
    private DatabaseReadOperations databaseOperations;
    private int test_type;
    private int question_data_type;
    private Tracker mTracker;
    private int permited_tries_number;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private View mLoadingView;
    private RelativeLayout content;
    private LoaderManager.LoaderCallbacks<ArrayList<WrongItem>> prepare_wrong_list_loader = new LoaderManager.LoaderCallbacks<ArrayList<WrongItem>>() {
        @Override
        public Loader<ArrayList<WrongItem>> onCreateLoader(int i, Bundle bundle) {
            return new WrongAnswersActivity.PrepareWrongList(WrongAnswersActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<WrongItem>> loader, ArrayList<WrongItem> list) {
            wrongItemList = list;
            mAdapter = new WrongAnswersActivityRecyclerViewAdapter(WrongAnswersActivity.this, wrongItemList, current_field_id);
            recyclerView.setAdapter(mAdapter);
            ActivityUtility.crossFade(content, mLoadingView, WrongAnswersActivity.this);
            getLoaderManager().destroyLoader(loader.getId());
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<WrongItem>> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
        wrongAnswersActivity = this;
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        current_field_id = bundle.getLong(Constants.CURRENT_FIELD_ID);
        test_type = bundle.getInt(ShowTestResultDialogFragment.TEST_TYPE);
        permited_tries_number = bundle.getInt(EnhancedTestActivity.PERMITED_TRIES_NUMBER);
        databaseOperations = new DatabaseReadOperations(this);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        content = (RelativeLayout) findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading);
        content.setVisibility(View.GONE);
        if (savedInstanceState != null) {
            wrongItemList = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEM_LIST);
            if ( wrongItemList != null ) {
                mAdapter = new WrongAnswersActivityRecyclerViewAdapter(this, wrongItemList, current_field_id);
                recyclerView.setAdapter(mAdapter);
                ActivityUtility.crossFade(content, mLoadingView, WrongAnswersActivity.this);
            } else {
                if ( getLoaderManager().getLoader(PREPARE_WRONG_LIST_LOADER_ID) != null ) {
                    getLoaderManager().initLoader(PREPARE_WRONG_LIST_LOADER_ID, null, prepare_wrong_list_loader);
                } else {
                    getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, this);
                }
            }
        } else {
            getLoaderManager().initLoader(GET_ENTRIES_LOADER_ID, null, this);
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private ArrayList<WrongItem> getWrongItems() {
        ArrayList<WrongItem> wrongItemsList = new ArrayList<WrongItem>();
        String[][] wrong_answers = new String[1][1];
        if (test_type == Constants.TestType.ENHANCED_TEST) {
            wrong_answers = databaseOperations.getWrongAnswersEnhancedTest(current_field_id, question_data_type);
        } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
            wrong_answers = databaseOperations.getWrongAnswersFullyRandomTest(current_field_id, question_data_type);
        }
        for (cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext()) {
            long entry_id = cursor.getLong(2);
            ArrayList<String> wrong_answers_arrayList = new ArrayList<>();
            for (int i = 0; i < wrong_answers.length; i++) {
                if (entry_id == Long.valueOf(wrong_answers[i][0])) {
                    wrong_answers_arrayList.add(wrong_answers[i][1]);
                }
            }
            if (wrong_answers_arrayList.size() > 0) {
                wrongItemsList.add(new WrongItem(cursor.getLong(2), cursor.getString(0), cursor.getString(1), wrong_answers_arrayList));
            }
        }
        return wrongItemsList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(ActivityUtility.getVersionCode(this) + SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wrong_answers_activity_menu, menu);
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
            case R.id.ask_again:
                if (wrongItemList != null) {
                    String string_for_pausing = null;
                    if (test_type == Constants.TestType.ENHANCED_TEST) {
                        string_for_pausing = AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED + current_field_id;
                    } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                        string_for_pausing = AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM + current_field_id;
                    }
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WrongAnswersActivity.this);
                    if (!sharedPreferences.contains(string_for_pausing) && !Emulation.isAnyThreadRunning()) {
                        if (test_type == Constants.TestType.ENHANCED_TEST) {
                            if (databaseOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, current_field_id)) {
                                new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(WrongAnswersActivity.this, AskAgainAfterTestActivity.class);
                                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                                                intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                                WrongAnswersActivity.this.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST, question_data_type);
                                                cursorLoaderAndUpdateDatabase.
                                                        clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_ENHANCED_TEST,
                                                                current_field_id);
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(WRONG_ITEM_LIST, wrongItemList);
                                                Intent intent = new Intent(WrongAnswersActivity.this, AskAgainAfterTestActivity.class);
                                                intent.putExtra(WRONG_ITEM_LIST, bundle);
                                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, permited_tries_number);
                                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                                                WrongAnswersActivity.this.startActivity(intent);
                                            }
                                        }).create().show();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(WRONG_ITEM_LIST, wrongItemList);
                                Intent intent = new Intent(this, AskAgainAfterTestActivity.class);
                                intent.putExtra(WRONG_ITEM_LIST, bundle);
                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, permited_tries_number);
                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.ENHANCED_TEST);
                                startActivity(intent);
                            }
                        } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                            if (databaseOperations.isThisFieldStudyTillCorrectPaused(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, current_field_id)) {
                                new AlertDialog.Builder(this).setMessage(R.string.continue_last_study_agian)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(WrongAnswersActivity.this, AskAgainAfterTestActivity.class);
                                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                                                intent.putExtra(AskAgainAfterTestActivity.BOOLEAN_CONTINUE, true);
                                                WrongAnswersActivity.this.startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                cursorLoaderAndUpdateDatabase.resetEntriesStudyPeriodOrder(current_field_id, Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST, question_data_type);
                                                cursorLoaderAndUpdateDatabase.
                                                        clearThisFieldFromStudyTillCorrectPausedAndFromStudyTillCorrectRepeatedEntries(Constants.Study_Till_Correct.STUDY_AGAIN_FULLY_RANDOM_TEST,
                                                                current_field_id);
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable(WRONG_ITEM_LIST, wrongItemList);
                                                Intent intent = new Intent(WrongAnswersActivity.this, AskAgainAfterTestActivity.class);
                                                intent.putExtra(WRONG_ITEM_LIST, bundle);
                                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, permited_tries_number);
                                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                                                WrongAnswersActivity.this.startActivity(intent);
                                            }
                                        }).create().show();
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(WRONG_ITEM_LIST, wrongItemList);
                                Intent intent = new Intent(this, AskAgainAfterTestActivity.class);
                                intent.putExtra(WRONG_ITEM_LIST, bundle);
                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, permited_tries_number);
                                intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                intent.putExtra(ShowTestResultDialogFragment.TEST_TYPE, Constants.TestType.FULLY_RANDOM_TEST);
                                startActivity(intent);
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(WrongAnswersActivity.this, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(WrongAnswersActivity.this, R.string.wait_loading, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitActivity.exit(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UsageLastTime.updateUsageLastTime(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInsatanceState) {
        super.onSaveInstanceState(savedInsatanceState);
        savedInsatanceState.putSerializable(WRONG_ITEM_LIST, wrongItemList);
        savedInsatanceState.putParcelable(RECYCLERVIEW_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recyclerView.getLayoutManager()
                .onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLERVIEW_POSITION));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoaderAndUpdateDatabase(WrongAnswersActivity.this,
                "SELECT " + " s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                        + " a , se." + Contract.COLUMN_ID + " FROM " + Contract.STring.TABLE_NAME
                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? ",
                new String[]{current_field_id + ""});
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        getLoaderManager().initLoader(PREPARE_WRONG_LIST_LOADER_ID, null, prepare_wrong_list_loader);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class PrepareWrongList extends AsyncTaskLoader<ArrayList<WrongItem>> {


        public PrepareWrongList(Context context) {
            super(context);
        }

        @Override
        public ArrayList<WrongItem> loadInBackground() {
            return wrongAnswersActivity.getWrongItems();
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
