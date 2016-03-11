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
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;

/**
 * Created by ahmed on 7/26/2015.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LAST_USED_FIELD_POSITION = "last_used_field_position";
    public static final String SPINNER_POSITION = "spinnerPosition";
    private Spinner fieldSpinner;
    private Button createFieldButton, fieldChooseOk;
    private EditText enterNewFieldName;
    private TextView orChooseOne;
    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;
//    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private Cursor fieldListCursor;
    String currentChoosenField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); // this Toolbar will act as action bar of the activity
        enterNewFieldName = (EditText) findViewById(R.id.enter_new_field_name);
        createFieldButton = (Button) findViewById(R.id.create_new_field);
        orChooseOne = (TextView) findViewById(R.id.or_choose_one);
        fieldSpinner = (Spinner) findViewById(R.id.fields_spinner);
        fieldChooseOk = (Button) findViewById(R.id.field_choose_ok);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        createFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = enterNewFieldName.getText().toString();
                createNewField(string); // creating a new field with the value which the user have entered

                enterNewFieldName.setText("");
            }
        });

        fieldChooseOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Cursor cursor = (Cursor) fieldSpinner.getSelectedItem(); // getting the selected item data as a Cursor opject
                String s = cursor.getString(1); // getting the name of the field
                cursor.close();*/
                Intent intent = new Intent(MainActivity.this, FieldActivity.class);// Creating Intent object according to open field activity
                // put the name of new field in the intent to get it
                //in the field activity, this to make the created field will be the field that is selected in the field activity when opened
//                intent.putExtra(FieldActivity.INITIAL_FIELD, fieldSpinner.getSelectedItemPosition());
                startActivity(intent);
                // saving the last selected field, according to getting it again when starting
                //the main activity next time, to make this field is the selected field when the activity start
                setLastUsedField(fieldSpinner.getSelectedItemPosition());
            }
        });
        databaseOperations = new DatabaseOperations(getContentResolver());
        simpleCursorAdapterForFieldSpinner = initializeSimpleCursorAdapterForFieldSpinner(); // initializing the adapter of the field spinner using this method
        fieldSpinner.setAdapter(simpleCursorAdapterForFieldSpinner); // setting this adapter to the field spinner
        // prevent the keyboard from opening automatically when the activity start
        getSupportLoaderManager().initLoader(0, null, this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
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
            case R.id.rate_the_app:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                return true;
            case R.id.open_facebook_page:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */
    @Override
    public void onResume() {
        super.onResume();
//        databaseHelper = new DatabaseHelper(this);
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        /*databaseOperations = new DatabaseOperations(getContentResolver());
        simpleCursorAdapterForFieldSpinner = initializeSimpleCursorAdapterForFieldSpinner(); // initializing the adapter of the field spinner using this method
        fieldSpinner.setAdapter(simpleCursorAdapterForFieldSpinner); // setting this adapter to the field spinner*/
        // if there is no fields, hide the part of the fields spinner because the user cannot use it
        /*if ( simpleCursorAdapterForFieldSpinner.getCount() <= 0 ) {
            fieldSpinner.setVisibility(View.INVISIBLE);
            orChooseOne.setVisibility(View.INVISIBLE);
            fieldChooseOk.setVisibility(View.INVISIBLE);
        } else {
            fieldSpinner.setVisibility(View.VISIBLE);
            orChooseOne.setVisibility(View.VISIBLE);
            fieldChooseOk.setVisibility(View.VISIBLE);
        }*/
        setFieldsSpinnerWhenStart(); // set the selected field in the spinner
    }

    /**
     * This method creates a SimpleCursorAdapter to populate the data from cursor to the field spinner
     * @return SimpleCursorAdapter object
     */
    private SimpleCursorAdapter initializeSimpleCursorAdapterForFieldSpinner() {
        String[] fromColumns = {Contract.OldField.COLUMN_NAME_FIELD_NAME}; // the field name column in the cursor
        int[] toViews = {R.id.list_item};   // the view that will hold the data of the field name
//        fieldListCursor = databaseOperations.getFieldList(); // the cursor of the list of the field names
        return new SimpleCursorAdapter(this, R.layout.field_spinner, null, fromColumns, toViews, 0); // creating the adapter then returning it
    }

    public void createNewField(String fieldName) {
        if (fieldName.contains(" ")) {
            Toast toast = Toast.makeText(MainActivity.this, "Spaces is not allowed in field name", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else if (fieldName.equals("")) {
            Toast toast = Toast.makeText(MainActivity.this, "Where is the field name?!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
            Toast toast = Toast.makeText(MainActivity.this, "There is an field with the same name!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,400);
            toast.show();
        } else {
//            databaseOperations.createNewField(fieldName);
            setLastUsedField(fieldListCursor.getCount());
            Intent intent = new Intent(MainActivity.this, FieldActivity.class); // Creating Intent object according to open field activity
//            intent.putExtra(FieldActivity.INITIAL_FIELD, fieldName);
            startActivity(intent);
        }
        if ( fieldSpinner.getVisibility() != View.INVISIBLE) { // if the spinner is visible
            // updating the adapter of the field spinner with the last list of fields
//            fieldListCursor = databaseOperations.getFieldList();
//            simpleCursorAdapterForFieldSpinner.changeCursor(fieldListCursor);
        }
    }

    /**
     * This method sets the selected item in the field spinner when the main activity starts
     * it gets the item form the shared preferences
     */
    public void setFieldsSpinnerWhenStart () {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        // getting the data from the preferences and set the selected item of the field spinner.
        int lastUsedPosition = sharedPreferences.getInt(LAST_USED_FIELD_POSITION, 0);
        // to be sure that the last used position in this spinner is still avilable ( may be the this position has been deleted )
        if ( simpleCursorAdapterForFieldSpinner.getCount() >= (lastUsedPosition + 1) ) {
            fieldSpinner.setSelection(lastUsedPosition);
        }
    }

    /**
     * This method saves the last selected item in the field spinner to the shared preferences
     * @param position the last selected item in the field spinner, to save it.
     */
    public void setLastUsedField ( int position ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); // getting the preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_USED_FIELD_POSITION, position); // saving the last selected item of the field spinner
        editor.commit();
    }
    @Override
    public void onPause() {
        super.onPause();
//        databaseHelper.close(); // releasing resources
    }
    @Override
    public void onDestroy() {
//        fieldListCursor.close();
//        sqLiteDatabase.close();
//        databaseHelper.close(); // releasing resources
        super.onDestroy();
    }
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState ) {
        super.onSaveInstanceState(savedInstanceState);
        setLastUsedField(fieldSpinner.getSelectedItemPosition());
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
        if ( simpleCursorAdapterForFieldSpinner.getCount() <= 0 ) {
            fieldSpinner.setVisibility(View.INVISIBLE);
            orChooseOne.setVisibility(View.INVISIBLE);
            fieldChooseOk.setVisibility(View.INVISIBLE);
        } else {
            fieldSpinner.setVisibility(View.VISIBLE);
            orChooseOne.setVisibility(View.VISIBLE);
            fieldChooseOk.setVisibility(View.VISIBLE);
        }
        setFieldsSpinnerWhenStart(); // set the selected field in the spinner
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapterForFieldSpinner.swapCursor(null);
    }
}
