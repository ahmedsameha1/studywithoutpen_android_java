package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.AddFieldNoteDialogFragment;

/**
 * Created by ahmed on 07/08/16.
 */
public class FieldInformationActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AddFieldNoteDialogFragment.Contact {
    private DatabaseReadOperations databaseOperations;
    private TextView number_of_entries;
    private TextView number_of_moderate_or_higher_entries;
    private ListView list_of_notes;
    private SimpleCursorAdapter simpleCursorAdapter;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_information);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,-1);
        databaseOperations = new DatabaseReadOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        number_of_entries = (TextView) findViewById(R.id.number_of_entries);
        number_of_moderate_or_higher_entries = (TextView) findViewById(R.id.moderate_or_higher_entries);
        list_of_notes = (ListView) findViewById(R.id.notes_list_view);
        setSupportActionBar(toolbar);
        number_of_entries.setText(Integer.toString(databaseOperations
                .getRealEntriesNumber(current_field_id,Contract.Field.STRING_DATA_TYPE)));
        number_of_moderate_or_higher_entries.setText(Integer.toString(databaseOperations
                .countModerateOrHigherEntries(current_field_id)));
        simpleCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,null,
                new String[]{Contract.Field_Note.COLUMN_NOTE}, new int[]{android.R.id.text1},0);
        list_of_notes.setAdapter(simpleCursorAdapter);
        list_of_notes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
                new AlertDialog.Builder(FieldInformationActivity.this)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_note)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mySQLiteCursorLoader.deleteFieldNote(l);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
                return true;
            }
        });
        getLoaderManager().initLoader(0,null,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_information_activity_menu, menu);
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
            case R.id.add_field_note:
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                AddFieldNoteDialogFragment addFieldNoteDialogFragment = new AddFieldNoteDialogFragment();
                addFieldNoteDialogFragment.setArguments(bundle);
                addFieldNoteDialogFragment.show(getSupportFragmentManager(),AddFieldNoteDialogFragment.ADD_FIELD_NOTE_DIALOG_FRAGMENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(this,"SELECT " + Contract.COLUMN_ID + ", "
                + Contract.Field_Note.COLUMN_NOTE
                + " FROM " + Contract.Field_Note.TABLE_NAME + " WHERE " + Contract.Field_Note.COLUMN_FIELD + " = ?",
                new String[]{current_field_id + ""});
        return mySQLiteCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.swapCursor(null);
    }

    @Override
    public void addFieldNote(String note) {
        mySQLiteCursorLoader.addFieldNote(current_field_id,note);
    }
}
