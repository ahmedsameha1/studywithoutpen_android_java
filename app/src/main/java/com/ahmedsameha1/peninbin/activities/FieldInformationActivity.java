package com.ahmedsameha1.peninbin.activities;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.AddFieldNoteDialogFragment;

/**
 * Created by ahmed on 07/08/16.
 */
public class FieldInformationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText number_of_entries;
    private ListView list_of_notes;
    private SimpleCursorAdapter simpleCursorAdapter;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private long current_field_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_information);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,-1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        number_of_entries = (EditText) findViewById(R.id.number_of_entries);
        list_of_notes = (ListView) findViewById(R.id.notes_list_view);
        setSupportActionBar(toolbar);
        number_of_entries.setText(Integer.toString((new DatabaseOperations(this)).getEntriesNumber(current_field_id,Contract.Field.STRING_DATA_TYPE)));
        simpleCursorAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,null,
                new String[]{Contract.Field_Note.COLUMN_NOTE}, new int[]{android.R.id.text1},0);
        list_of_notes.setAdapter(simpleCursorAdapter);
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
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this,"SELECT " + Contract.COLUMN_ID + ", "
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
}
