package com.ahmedsameha1.studyvocabularyfive.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.adapters.MainActivityRecyclerViewAdapter;
import com.ahmedsameha1.studyvocabularyfive.database.Contract;
import com.ahmedsameha1.studyvocabularyfive.database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.dialogs.CreateFieldDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.fragments.MainActivityFragmentDrawer;
import com.ahmedsameha1.studyvocabularyfive.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

/**
 * Created by ahmed on 7/26/2015.
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MainActivityFragmentDrawer.FragmentDrawerListener {
    private static final String RUN_FIRST_TIME = "run_first_time";
    public static final String RUN_FIRST_TIME_DIALOG = "runFirstTimeDialog";
    public static final String RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS = "runFirstTimeCreateExample_Fields";
    public static final String LAST_USED_FIELD_POSITION = "last_used_field_position";
    public static final String SPINNER_POSITION = "spinnerPosition";
    private static final String CREATE_NEW_FIELD = "create_new_field";
    private Spinner fieldSpinner;
    private Button createFieldButton, fieldChooseOk;
    private EditText enterNewFieldName;
    private TextView orChooseOne;
    private SimpleCursorAdapter simpleCursorAdapterForFieldSpinner;
//    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private Cursor fieldListCursor;
    private MainActivityFragmentDrawer drawerFragment;
    private RecyclerView recyclerView;
    private MainActivityRecyclerViewAdapter mAdapter;
    private String first_field_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity); // this Toolbar will act as action bar of the activity
        recyclerView = (RecyclerView) findViewById(R.id.main_activity_recyclerview);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);       // setting toolbar as the action bar of the activity
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (MainActivityFragmentDrawer)
        getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        mAdapter = new MainActivityRecyclerViewAdapter(this,null);
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        int spanCount = 2;
        int spacing = 3;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        addFab();
        getSupportLoaderManager().restartLoader(0,null,this);
        {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPreferences.getBoolean(RUN_FIRST_TIME, true)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(RUN_FIRST_TIME, false);
                PackageManager manager = getPackageManager();
                try {
                    PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                    for (int versionCode = info.versionCode; versionCode >= 1; versionCode--) {
                        if ( sharedPreferences.contains(RUN_FIRST_TIME_DIALOG + versionCode) ) {
                            editor.remove(RUN_FIRST_TIME_DIALOG + versionCode);
                        }
                        if ( sharedPreferences.contains(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode)) {
                            editor.remove(RUN_FIRST_TIME_CREATE_EXAMPLE_FIELDS + versionCode);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if ( sharedPreferences.contains(LAST_USED_FIELD_POSITION) ) {
                    editor.remove(LAST_USED_FIELD_POSITION);
                }
                if ( sharedPreferences.contains(FieldActivity.CURRENT_FIELD) ) {
                    editor.remove(FieldActivity.CURRENT_FIELD);
                }
                editor.commit();
            }
        }
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
    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
    /*
    Because the database related objects should close as soon as the activity does not use it, I put all database work on onResume()
    onResume() will be called before the user can react with the activity
    in onPause() I close all database related objects.
     */


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
    private void addFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.fab_selector));
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.drawable.entry);
        SubActionButton button1 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(100,100))
                .setContentView(itemIcon1, new FrameLayout.LayoutParams(50, 50, Gravity.CENTER)).build();
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.drawable.folder);
        SubActionButton button2 = itemBuilder.setLayoutParams(new FrameLayout.LayoutParams(100,100))
                .setContentView(itemIcon2, new FrameLayout.LayoutParams(50, 50,Gravity.CENTER)).build();
        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .attachTo(fab)
                .build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mAdapter.getItemCount() > 0 ) {
                    if ( first_field_name != null ) {
                        Intent intent = new Intent(MainActivity.this, AddEntryActivity.class);
                        intent.putExtra(MainActivity.LAST_USED_FIELD_POSITION, getIntent().getIntExtra(MainActivity.LAST_USED_FIELD_POSITION, 0));
                        intent.putExtra(FieldActivity.CURRENT_FIELD, first_field_name);
                        intent.putExtra(AddEntryActivity.FROM_FIELD_ACTIVITY, false);
                        startActivity(intent);
                    }
                } else {
                    CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                    fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                }
                actionMenu.close(false);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateFieldDialogFragment fragment = new CreateFieldDialogFragment();
                fragment.show(getSupportFragmentManager(), CREATE_NEW_FIELD);
                actionMenu.close(true);
            }
        });
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
//        setLastUsedField(fieldSpinner.getSelectedItemPosition());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Field.TABLE_NAME);
        return new CursorLoader(this,uri, new String[]{"rowid _id",Contract.Field.COLUMN_NAME_FIELD_TYPE,Contract.Field.COLUMN_NAME_FIELD_NAME},null,null,
                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        data.moveToFirst();
        first_field_name = data.getString(2);
        /*simpleCursorAdapterForFieldSpinner.swapCursor(data);
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
        setFieldsSpinnerWhenStart(); // set the selected field in the spinner*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
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
