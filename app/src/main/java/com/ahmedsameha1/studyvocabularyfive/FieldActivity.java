package com.ahmedsameha1.studyvocabularyfive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class FieldActivity extends AppCompatActivity implements CreateFieldDialogFragment.Contact,
        DeleteFieldDialogFragment.Contact,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INITIAL_FIELD = "intial_field";
    public static final String GET_NEW_FIELD_NAME = "getNewFieldName";
    public static final String GET_CONFIRMATION = "getConfirmation";
    public static final String CURRENT_FIELD = "currentField";
    public static final int FIELD_LIST_LOADER_ID = 0;
    public static final int FIELD_ENTRIES_LOADER_ID = 1;
    private String[] deleteEntryData = {null, null, null};
    private Cursor fieldEntriesCursor;
    private Cursor fieldListCursor;
    private EntriesListFragment.CustomCursorAdapter customCursorAdapterForFieldEntriesSpinner;
    private LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;



    private Spinner fieldsListSpinner;

    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;

    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;

    public String currentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_field_activity);
        fieldsListSpinner = (Spinner) findViewById(R.id.fields_spinner);

        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getContentResolver());
        simpleCursorAdapterForFieldSpinner = initializeSimpleCursorAdapterForFieldSpinner();
        fieldsListSpinner.setAdapter(simpleCursorAdapterForFieldSpinner);
//        fieldsListSpinner.setSelection(getIntent().getExtras().getInt(INITIAL_FIELD));
        getSupportLoaderManager().initLoader(FIELD_LIST_LOADER_ID,null,this);
//

        fieldsListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLastUsedField(getCurrentField());
                setLastUsedField(fieldsListSpinner.getSelectedItemPosition());
                EntriesListFragment entriesListFragment = (EntriesListFragment) FieldActivity.this
                        .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                if ( entriesListFragment != null ) {
                    /*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    customCursorAdapterForFieldEntriesSpinner = entriesListFragment.getCustomCursorAdapterForFieldEntriesListView();
                    customCursorAdapterForFieldEntriesSpinner.changeCursor(fieldEntriesCursor);*/


                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD,getCurrentField());
                    entriesListFragment.getLoaderManager().restartLoader(0,bundle,entriesListFragment.getFieldEntries);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*


//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        */
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                return new CursorLoader(FieldActivity.this,
                        uri,
                        new String[] {"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                        Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                        new String[] {getCurrentField()},
                        null);
            }
            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                fieldEntriesCursor = data;
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                fieldEntriesCursor = null;
            }
        };
    }
    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */

    @Override
    public void onPause() {
        super.onPause();
//        setLastUsedField(fieldsListSpinner.getSelectedItemPosition());
    }
    @Override
    public void onDestroy() {
//        fieldListCursor.close();
/*        if ( fieldEntriesCursor != null ) {
            fieldEntriesCursor.close();
        }*/
//        sqLiteDatabase.close();
        if ( databaseHelper != null ) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.field_activity_menu, menu);
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
            case R.id.add_field:
                CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                fragment.show(getSupportFragmentManager(), GET_NEW_FIELD_NAME);
                return true;
            case R.id.delete_field:
                Bundle bundle = new Bundle();
                bundle.putString(DeleteFieldDialogFragment.CONFIRMATION_MESSAGE, getString(R.string.field_deletion_message) + " ( " + getCurrentField() + " ) ?");
                DeleteFieldDialogFragment fragment1 = new DeleteFieldDialogFragment();
                fragment1.setArguments(bundle);
                fragment1.show(getSupportFragmentManager(), GET_CONFIRMATION);
                return true;
            case R.id.test:
/*                if ( fieldEntriesCursor != null ) {
                    fieldEntriesCursor.close();
                }*/
//                fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                if ( fieldEntriesCursor.getCount() > 0 ) {
                    Intent intent = new Intent(this, TestActivity.class);
                    intent.putExtra(TestActivity.CURRENT_FIELD, getCurrentField());
                    startActivity(intent);
                } else {
                    Toast.makeText(this,getString(R.string.no_entries_in_field),Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.study_during:
                StudyDuringDialogFragment fragment2 = new StudyDuringDialogFragment();
                fragment2.show(getSupportFragmentManager(),fragment2.SHOW_STUDY_DURING_DIALOG_FRAGMENT);
                return true;
            case R.id.rate_the_app:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void createNewField(CreateFieldDialogFragment dialog) {
        EditText enterFieldName = (EditText) dialog.getDialog().findViewById(R.id.enter_field_name);
        String fieldName = enterFieldName.getText().toString();
        if (fieldName.contains(" ")) {
            Toast.makeText(FieldActivity.this, "Spaces is not allowed in field name", Toast.LENGTH_SHORT).show();
        } else if (fieldName.equals("")) {
            Toast.makeText(FieldActivity.this, "Where is the field name?!", Toast.LENGTH_SHORT).show();
        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
            Toast.makeText(FieldActivity.this, "There is an field with the same name!", Toast.LENGTH_SHORT).show();
        } else {
            databaseOperations.CreateNewField(fieldName);
        }
//        fieldListCursor = databaseOperations.getFieldList();
//        simpleCursorAdapterForFieldSpinner.changeCursor(fieldListCursor);
//        fieldsListSpinner.setSelection(fieldListCursor.getCount());
        setLastUsedField(fieldListCursor.getCount());
    }

    private SimpleCursorAdapter initializeSimpleCursorAdapterForFieldSpinner() {
        String[] fromColumns = {Contract.OldField.COLUMN_NAME_FIELD_NAME};
        int[] toViews = {R.id.list_item};
    /*    if ( fieldListCursor != null ) {
            fieldListCursor.close();
        }*/
//        fieldListCursor = databaseOperations.getFieldList();
        return new SimpleCursorAdapter(this, R.layout.field_spinner, null, fromColumns, toViews, 0);
    }


    public Spinner getFieldsListSpinner() {
        return fieldsListSpinner;
    }

    public String getCurrentField() {
        Cursor cursor = (Cursor) fieldsListSpinner.getSelectedItem();
        String string = cursor.getString(1);
        return string;
    }

    public void deleteField() {
        if ( simpleCursorAdapterForFieldSpinner.getCount() <= 1 ) {
            databaseOperations.deleteField(getCurrentField());
            finish();
        } else {
            databaseOperations.deleteField(getCurrentField());
            if ( fieldsListSpinner.getSelectedItemPosition() == (fieldListCursor.getCount() - 1 )) {
                fieldsListSpinner.setSelection((fieldListCursor.getCount() - 2));
            }
        }
//        fieldListCursor = databaseOperations.getFieldList();
//        simpleCursorAdapterForFieldSpinner.changeCursor(fieldListCursor);


    }

    public int getFieldPostion(String fieldName) {
        for (fieldListCursor.moveToFirst(); fieldListCursor.getPosition() < fieldListCursor.getCount(); fieldListCursor.moveToNext()) {
            if (fieldListCursor.getString(1).equals(fieldName)) {
                int n = fieldListCursor.getPosition();
//                fieldListCursor.close();
                return n;
            }
        }
//        fieldListCursor.close();
        return 0;
    }


    public void setLastUsedField ( int position ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.LAST_USED_FIELD_POSITION, position); // saving the last selected item of the field spinner
        editor.commit();
    }
    public void setLastUsedField ( String currentField ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FieldActivity.CURRENT_FIELD, currentField); // saving the last selected item of the field spinner
        editor.commit();
    }
    public void setFieldsSpinner() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        // getting the data from the preferences and set the selected item of the field spinner.
        int lastUsedPosition = sharedPreferences.getInt(MainActivity.LAST_USED_FIELD_POSITION, 0);
        // to be sure that the last used position in this spinner is still avilable ( may be the this position has been deleted )
        if ( simpleCursorAdapterForFieldSpinner.getCount() >= (lastUsedPosition + 1) ) {
            fieldsListSpinner.setSelection(lastUsedPosition);
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        return new CursorLoader(this,uri, new String[]{"rowid _id",Contract.Field.COLUMN_NAME_FIELD_NAME},null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapterForFieldSpinner.swapCursor(data);
        fieldListCursor = data;
        if ( simpleCursorAdapterForFieldSpinner.getCount() <= 0) {
            finish();
        } else {
            setFieldsSpinner();
            getSupportLoaderManager().restartLoader(FIELD_ENTRIES_LOADER_ID, null, getFieldEntries);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapterForFieldSpinner.swapCursor(null);
    }
}
