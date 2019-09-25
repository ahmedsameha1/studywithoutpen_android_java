package com.ahmedsameha1.peninbin.view.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.adapters.EmulateFieldActivityRecyclerViewAdapter;
import com.ahmedsameha1.peninbin.adapters.LinearSpacingItemDecoration;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.intent_services.EnableEmulationIntentService;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by ahmed on 22/08/16.
 */
public class EmulateFieldActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String RECYCLERVIEW_POSITION = "recyclerview_position";
    private EmulateFieldActivityRecyclerViewAdapter mAdapter;
    private EditText enter_number_of_entries;
    private ArrayList<Long> starting_entry_id = new ArrayList<>();
    private long current_field_id;
    private RecyclerView recyclerView;
    private Parcelable recyclerview_position;
    private boolean[] week_days_case = {true,true,true,true,true,true,true};
    private Button[] week_days = new Button[7];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulate_field);
        current_field_id = getIntent().getLongExtra(FieldActivity.FIELD_ID, 0);
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        enter_number_of_entries = (EditText) findViewById(R.id.enter_number_of_entries);
        ImageButton number_of_entries_help = (ImageButton) findViewById(R.id.number_of_entries_help);
        ImageButton starting_entry_help = (ImageButton) findViewById(R.id.starting_entry_help);
        ImageButton exclude_days_help = (ImageButton) findViewById(R.id.what_days_help);
        prepareWeekDayArray();
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
        mAdapter = new EmulateFieldActivityRecyclerViewAdapter(EmulateFieldActivity.this, null, starting_entry_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(EmulateFieldActivity.this));
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
        number_of_entries_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EmulateFieldActivity.this);
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.everyday_entries_help).create().show();
            }
        });
        starting_entry_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EmulateFieldActivity.this);
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.starting_entry_help).create().show();

            }
        });
        exclude_days_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmulateFieldActivity.this);
                builder.setMessage(R.string.exclude_days_help).create().show();
            }
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
                        int number_of_entries = Integer.parseInt(enter_number_of_entries.getText().toString());
                        Toast toast = Toast.makeText(EmulateFieldActivity.this, "This operation may take some time", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Intent intent = new Intent(EmulateFieldActivity.this.getBaseContext(), EnableEmulationIntentService.class);
                        intent.putExtra(EnableEmulationIntentService.FIELD_ID, current_field_id);
                        intent.putExtra(EnableEmulationIntentService.NUMBER_OF_ENTRIES, number_of_entries);
                        intent.putExtra(EnableEmulationIntentService.WEEK_DAYS_CASE, week_days_case);
                        if ( starting_entry_id.size() != 0 ) {
                            intent.putExtra(EnableEmulationIntentService.STARTING_ENTRY, starting_entry_id.get(0));
                        }
                        startService(intent);
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
        CursorLoaderAndUpdateDatabase mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(EmulateFieldActivity.this,
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recyclerview_position = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLERVIEW_POSITION, recyclerview_position);    }

    private void prepareWeekDayArray() {
        for ( int i = 0; i < week_days.length; i++ ) {
            switch (i) {
                case 1:
                    week_days[0] = (Button) findViewById(R.id.sunday);
                    break;
                case 2:
                    week_days[1] = (Button) findViewById(R.id.monday);
                    break;
                case 3:
                    week_days[2] = (Button) findViewById(R.id.tuesday);
                    break;
                case 4:
                    week_days[3] = (Button) findViewById(R.id.wednesday);
                    break;
                case 5:
                    week_days[4] = (Button) findViewById(R.id.thursday);
                    break;
                case 6:
                    week_days[5] = (Button) findViewById(R.id.friday);
                    break;
                case 0:
                    week_days[6] = (Button) findViewById(R.id.saturday);
                    break;
            }
        }
        for ( int i = 0; i < week_days.length; i++ ) {
            final int n = i;
            week_days[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    week_days_case[n] = !week_days_case[n];
                    boolean all_days_not = true;
                    for ( int k = 0; k < week_days_case.length; k++ ) {
                        if ( week_days_case[k] ) {
                            all_days_not = false;
                            break;
                        }
                    }
                    if ( all_days_not ) {
                        week_days_case[n] = !week_days_case[n];
                    }
                    if (week_days_case[n]) {
                        week_days[n].setBackgroundColor(0xff00ff00);
                    } else {
                        week_days[n].setBackgroundColor(0xffff0000);
                    }
                }
            });
        }
    }

}
