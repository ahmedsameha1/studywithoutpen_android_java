package com.ahmedsameha1.peninbin.activities;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.adapters.WrongAnswersActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.dialogs.ShowTestResultDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongAnswersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String SCREEN_NAME = "WrongAnswersActivity";
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    private long current_field_id;
    private ArrayList<WrongItem> wrongItemList;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private WrongAnswersActivityRecyclerViewAdapter mAdapter;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private DatabaseReadOperations databaseOperations;
    private int test_type;
    private int question_data_type;
    private Tracker mTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        current_field_id = bundle.getLong(StudyActivity.CURRENT_FIELD_ID);
        test_type = bundle.getInt(ShowTestResultDialogFragment.TEST_TYPE);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(this);
        databaseOperations = new DatabaseReadOperations(this);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        getLoaderManager().initLoader(0,null,this);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }
    private ArrayList<WrongItem> getWrongItems() {
        ArrayList<WrongItem> wrongItemsList = new ArrayList<WrongItem>();
        String[][] wrong_answers = new String[1][1];
        if ( test_type == Constants.TestType.ENHANCED_TEST ) {
            wrong_answers = databaseOperations.getWrongAnswersEnhancedTest(current_field_id,question_data_type);
        } else if ( test_type == Constants.TestType.FULLY_RANDOM_TEST ) {
            wrong_answers = databaseOperations.getWrongAnswersFullyRandomTest(current_field_id,question_data_type);
        }
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            long entry_id = cursor.getLong(2);
            ArrayList<String> wrong_answers_arrayList = new ArrayList<>();
            for ( int i = 0; i < wrong_answers.length; i++ ) {
                if ( entry_id == Long.valueOf(wrong_answers[i][0]) ) {
                    wrong_answers_arrayList.add(wrong_answers[i][1]);
                }
            }
            if ( wrong_answers_arrayList.size() > 0 ) {
                wrongItemsList.add(new WrongItem(cursor.getLong(2),cursor.getString(0), cursor.getString(1), wrong_answers_arrayList));
            }
        }
        return wrongItemsList;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(SCREEN_NAME);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putSerializable(WRONG_ITEM_LIST, wrongItemList);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        wrongItemList = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEM_LIST);
        mAdapter = new WrongAnswersActivityRecyclerViewAdapter(this,wrongItemList,current_field_id);
        recyclerView.setAdapter(mAdapter);
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
        /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        return new CursorLoader(this,uri,
                new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS + " != ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                new String[] {"", currentField},
                null);*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        if ( wrongItemList == null ) {
            wrongItemList = getWrongItems();
        }
        mAdapter = new WrongAnswersActivityRecyclerViewAdapter(this,wrongItemList,current_field_id);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
