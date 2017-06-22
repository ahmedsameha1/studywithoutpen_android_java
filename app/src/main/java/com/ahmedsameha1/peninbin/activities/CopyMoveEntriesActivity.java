package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.CopyMoveEntriesActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.dialogs.CopyFieldDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.SortOptions;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
/**
 * Created by ahmed on 22/08/16.
 */
public class CopyMoveEntriesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CopyFieldDialogFragment.CopyEntries,
        CopyFieldDialogFragment.MoveEntries {
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    private CopyMoveEntriesActivityRecyclerViewAdapter mAdapter;
    private ArrayList<Long> list_of_entries = new ArrayList<>();
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;
    private SortOptions sortOption;
    private  RecyclerView recyclerView;
    private Parcelable recyclerview_position;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_move_entries);
        current_field_id = getIntent().getLongExtra(FieldActivity.FIELD_ID, 0);
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(this);
        sortOption = databaseOperations.getFieldSort(current_field_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.copy_move_entries_activity_recyclerview);
        VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(recyclerView);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        if ( savedInstanceState != null ) {
            recyclerview_position = savedInstanceState.getParcelable(RECYCLERVIEW_POSITION);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseOperations.getFieldName(current_field_id));
        mAdapter = new CopyMoveEntriesActivityRecyclerViewAdapter(CopyMoveEntriesActivity.this, null, list_of_entries);
        recyclerView.setLayoutManager(new LinearLayoutManager(CopyMoveEntriesActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        getLoaderManager().restartLoader(0,null,this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.copy_move_entries_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        CopyFieldDialogFragment copyFieldDialogFragment = new CopyFieldDialogFragment();
        Bundle bundle = new Bundle();
        //noinspection SimplifiableIfStatement
        if ( list_of_entries.size() > 0 ) {
            switch (id) {
                case R.id.copy:
                    bundle.putInt(CopyFieldDialogFragment.WHAT_TO_DO, Constants.CopyMove.COPY_ENTRIES);
                    copyFieldDialogFragment.setArguments(bundle);
                    copyFieldDialogFragment.show(getSupportFragmentManager(), CopyFieldDialogFragment.COPY_FIELD_DIALOG_FRAGMENT);
                    return true;
                case R.id.move:
                    bundle.putInt(CopyFieldDialogFragment.WHAT_TO_DO, Constants.CopyMove.MOVE_ENTRIES);
                    copyFieldDialogFragment.setArguments(bundle);
                    copyFieldDialogFragment.show(getSupportFragmentManager(), CopyFieldDialogFragment.COPY_FIELD_DIALOG_FRAGMENT);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            Toast toast = Toast.makeText(CopyMoveEntriesActivity.this, R.string.select_one, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(CopyMoveEntriesActivity.this,
                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                        + "ORDER BY " + sortOption.getSortString(),
                new String[]{current_field_id + ""});
        return mySQLiteCursorLoader;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if ( recyclerview_position != null ) {
            recyclerView.getLayoutManager()
                    .onRestoreInstanceState(recyclerview_position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void copyEntries(long field_id) {
        mySQLiteCursorLoader.copyEntries(list_of_entries,field_id);
    }

    @Override
    public void moveEntries(long field_id) {
        mySQLiteCursorLoader.moveEntries(list_of_entries,field_id);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);
    }

}
