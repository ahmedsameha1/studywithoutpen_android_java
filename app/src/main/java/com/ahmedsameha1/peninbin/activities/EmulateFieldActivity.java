package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.CopyMoveEntriesActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.EmulateFieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.CopyFieldDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;

import java.util.ArrayList;

/**
 * Created by ahmed on 22/08/16.
 */
public class EmulateFieldActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EMULATING = "emulating field: ";
    private EmulateFieldActivityRecyclerViewAdapter mAdapter;
    private int number_of_entries;
    private EditText enter_number_of_entries;
    private ArrayList<Long> starting_entry_id = new ArrayList<>();
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;
    private DatabaseReadOperations databaseOperations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulate_field);
        current_field_id = getIntent().getLongExtra(FieldActivity.FIELD_ID, 0);
        databaseOperations = new DatabaseReadOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        enter_number_of_entries = (EditText) findViewById(R.id.enter_number_of_entries);
        ImageButton number_of_entries_help = (ImageButton) findViewById(R.id.number_of_entries_help);
        ImageButton starting_entry_help = (ImageButton) findViewById(R.id.starting_entry_help);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.copy_move_entries_activity_recyclerview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(databaseOperations.getFieldName(current_field_id));
        mAdapter = new EmulateFieldActivityRecyclerViewAdapter(EmulateFieldActivity.this, null, starting_entry_id);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EmulateFieldActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 1;
        int spacing = 14;
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(spanCount, spacing, true));
        getLoaderManager().restartLoader(0,null,this);
        enter_number_of_entries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ( s.length() > 0 ) {
                    try {
                        int number = Integer.parseInt(s.toString());
                        if (number <= 0) {
                            enter_number_of_entries.setText("");
                            Toast toast = Toast.makeText(EmulateFieldActivity.this, R.string.greater_zero, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    } catch (NumberFormatException e) {
                        enter_number_of_entries.setText("");
                        Toast toast = Toast.makeText(EmulateFieldActivity.this, R.string.not_allowed_number, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                }
            }
        });
        number_of_entries_help.setOnClickListener( view ->  {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EmulateFieldActivity.this);
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.everyday_entries_help).create().show();
        });
        starting_entry_help.setOnClickListener( view ->  {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EmulateFieldActivity.this);
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.starting_entry_help).create().show();

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.emulate_field_activity_menu, menu);
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
            case R.id.emulate:
                    if ( enter_number_of_entries.getText().length() > 0 ) {
                        number_of_entries = Integer.parseInt(enter_number_of_entries.getText().toString());
                        Toast toast = Toast.makeText(EmulateFieldActivity.this, "This operation may take some time", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        if ( starting_entry_id.size() == 0 ) {
                            Emulation.startEmulateThread(this, current_field_id,number_of_entries);
                        } else {
                            Emulation.startEmulateThread(this, current_field_id, number_of_entries, starting_entry_id.get(0));
                        }
                        ExitActivity.exit(this);
                        finish();
                    } else {
                        Toast toast = Toast.makeText(EmulateFieldActivity.this, R.string.enter_the_number_of__everyday_entries, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(EmulateFieldActivity.this,
                "SELECT " + " se." + Contract.COLUMN_ID + ", s1." + Contract.STring.COLUMN_VALUE + " q , s2." + Contract.STring.COLUMN_VALUE
                        + " a FROM " + Contract.STring.TABLE_NAME + " s1 INNER JOIN " + Contract.String_Entry.TABLE_NAME + " se ON s1."
                        + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_QUESTION + " INNER JOIN "
                        + Contract.STring.TABLE_NAME + " s2 ON s2." + Contract.COLUMN_ID + " = se." + Contract.String_Entry.COLUMN_ANSWER
                        + " WHERE " + "se." + Contract.String_Entry.COLUMN_FIELD + " = ? "
                        + "ORDER BY " + Contract.String_Entry.COLUMN_ORDER_OF_ENTRY + " ASC, "
                        + Contract.String_Entry.COLUMN_RANK + " DESC, " + "datetime(" + Contract.CREATION_DATE + ") ASC",
                new String[]{current_field_id + ""});
        return mySQLiteCursorLoader;
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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
