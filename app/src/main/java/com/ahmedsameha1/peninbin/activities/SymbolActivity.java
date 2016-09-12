package com.ahmedsameha1.peninbin.activities;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.AddFieldNoteDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeAreasDialogFragment;
import com.ahmedsameha1.peninbin.dialogs.ChangeTextSizeDialogFragment;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.OnSwipeTouchListener;
import com.ahmedsameha1.peninbin.utils.UsageLastTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Ahmed on 3/19/16.
 */
public class SymbolActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private DatabaseOperations databaseOperations;
    private ListView list_of_symbols;
    private SimpleCursorAdapter simpleCursorAdapter;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private long current_field_id;
    private int current_field_type;
    private CustomCursorAdapter customCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol);
        current_field_id = getIntent().getLongExtra(StudyActivity.CURRENT_FIELD_ID,-1);
        databaseOperations = new DatabaseOperations(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        list_of_symbols = (ListView) findViewById(R.id.list_view_symbol);
        current_field_type = databaseOperations.getFieldType(current_field_id);
        String [] field_type_array = getResources().getStringArray(R.array.field_type);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(field_type_array [current_field_type]);
/*        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.symbol_lishview_row ,null,
                new String[]{Contract.Symbol.COLUMN_VALUE, Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING},
                new int[]{R.id.symbol,R.id.user_customized_string},0);*/
        customCursorAdapter = new CustomCursorAdapter(this, null, false);
        list_of_symbols.setAdapter(customCursorAdapter);
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mySQLiteCursorLoader = new MySQLiteCursorLoader(this,"SELECT ts." + Contract.COLUMN_ID + ", s."
                + Contract.Symbol.COLUMN_VALUE + " , ts." + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING
                + " FROM " + Contract.Field_Type_Symbol.TABLE_NAME + " ts INNER JOIN " + Contract.Symbol.TABLE_NAME +
                " s ON s." + Contract.COLUMN_ID + " = ts." + Contract.Field_Type_Symbol.COLUMN_SYMBOL
                + " WHERE " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " = ? ORDER BY s." + Contract.Symbol.COLUMN_VALUE + " ASC",
                new String[]{ current_field_type + ""});

        return mySQLiteCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        customCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        customCursorAdapter.swapCursor(null);
    }

    public class CustomCursorAdapter extends CursorAdapter {

        public CustomCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final TextView symbol_textview;
            final EditText customable_text_edittext;
            final ImageButton save;
            symbol_textview = (TextView) view.findViewById(R.id.symbol);
            customable_text_edittext = (EditText) view.findViewById(R.id.user_customized_string);
            save = (ImageButton) view.findViewById(R.id.save);
            symbol_textview.setText(cursor.getString(1));
            customable_text_edittext.setText(cursor.getString(2));
            final long symbol_customable_text_id = cursor.getLong(0);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( ! databaseOperations.checkCustomableText(current_field_type,
                            customable_text_edittext.getText().toString() ) ) {
                        mySQLiteCursorLoader.updateCustomableText(symbol_customable_text_id, customable_text_edittext.getText().toString());
                    } else {
                        Toast toast = Toast.makeText(SymbolActivity.this, R.string.text_contained_in_special_text, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                }
            });
            customable_text_edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if ( s.length() <= 1 ) {
                        save.setEnabled(false);
                    } else {
                        save.setEnabled(true);
                    }
                }
            });

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.symbol_lishview_row, parent, false);
            return view;

        }
    }

}
