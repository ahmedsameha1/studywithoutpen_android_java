package com.ahmedsameha1.studyvocabulary.activities;

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

import com.ahmedsameha1.studyvocabulary.adapters.WrongAnswersActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseHelper;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.adapters.WrongItem;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

import java.util.ArrayList;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongAnswersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    private long current_field_id;
    private ArrayList<WrongItem> wrongItemList;
    private Cursor cursor;
    private RecyclerView recyclerView;
    private WrongAnswersActivityRecyclerViewAdapter mAdapter;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private DatabaseOperations databaseOperations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this);
        databaseOperations = new DatabaseOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.field_activity_recyclerview);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        current_field_id = bundle.getLong(StudyActivity.CURRENT_FIELD_ID);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
//        ArrayList<WrongItem> wrongItemList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
//        wrongItemList = new ArrayList<WrongItem>();
//        WrongItemAdapter wrongItemAdapter = new WrongItemAdapter(this,wrongItemList);
//        listView.setAdapter(wrongItemAdapter);
        getLoaderManager().initLoader(0,null,this);
    }




    private ArrayList<WrongItem> getWrongItems() {
        ArrayList<WrongItem> wrongItemsList = new ArrayList<WrongItem>();
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            String question = cursor.getString(0);
            String answer = cursor.getString(1);
            ArrayList<String> wrongAnswersList =
                    databaseOperations.getEntryWrongAnswers(question,answer,current_field_id,databaseOperations.getQuestionDataType(current_field_id));
            if ( wrongAnswersList.size() > 0 ) {
                wrongItemsList.add(new WrongItem(question, answer, wrongAnswersList));
            }
        }
        return wrongItemsList;
    }

    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putSerializable(WRONG_ITEM_LIST, wrongItemList);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        wrongItemList = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEM_LIST);
        mAdapter = new WrongAnswersActivityRecyclerViewAdapter(this,wrongItemList);
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MySQLiteCursorLoader(WrongAnswersActivity.this,
                "SELECT " + " s1." + Contract.STring.COLUMN_NAME_VALUE + " q , s2." + Contract.STring.COLUMN_NAME_VALUE
                        + " a " + " FROM " + Contract.STring.TABLE_NAME
                        + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                        + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_QUESTION + " INNER JOIN "
                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_NAME_ID + " = se." + Contract.String_Entry.COLUMN_NAME_ANSWER
                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_NAME_FIELD + " = ? ",
                new String[]{current_field_id + ""});
        /*Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Old_Linker_DBV6_AppV32.TABLE_NAME);
        return new CursorLoader(this,uri,
                new String[] {Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_QUESTION_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_ANSWER_LINK,
                        Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS},
                Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_TEST_WRONG_ANSWERS + " != ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {"", currentField},
                null);*/
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        if ( wrongItemList == null ) {
            wrongItemList = getWrongItems();
            DatabaseOperations databaseOperations = new DatabaseOperations(this);
            mySQLiteCursorLoader.clearThisFieldFromWrongAnswers(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
        }
        mAdapter = new WrongAnswersActivityRecyclerViewAdapter(this,wrongItemList);
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
