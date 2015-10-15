package com.ahmedsameha1.studyvocabularyfive;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by ahmed_000 on 8/23/2015.
 */
public class AddEntryFragment extends Fragment {
    private Button saveEntry, saveEntryAndClearFields,showEntriesButton;
    private ImageButton clearQuestion, clearAnswer;
    private EditText enterQuestion, enterAnswer;
    private LinearLayout showEntriesButtonContainer;
    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private Cursor fieldEntriesCursor;
    private EntriesListFragment.CustomCursorAdapter customCursorAdapterForFieldEntriesSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstacneState ) {
        View view = inflater.inflate(R.layout.add_entry_fragment,parent,false);
        showEntriesButtonContainer = (LinearLayout) view.findViewById(R.id.show_entries_button_container);
        enterQuestion = (EditText) view.findViewById(R.id.enter_question);
        enterAnswer = (EditText) view.findViewById(R.id.enter_answer);
        saveEntry = (Button) view.findViewById(R.id.save_entry);
        saveEntryAndClearFields = (Button) view.findViewById(R.id.save_entry_and_clear_fields);
        clearQuestion = (ImageButton) view.findViewById(R.id.clear_question);
        clearAnswer = (ImageButton) view.findViewById(R.id.clear_answer);
        saveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseOperations.insertFieldEntry(getCurrentField(), enterQuestion.getText().toString(), enterAnswer.getText().toString());
                FieldActivity fieldActivity = (FieldActivity) getActivity();
                EntriesListFragment entriesListFragment = (EntriesListFragment) fieldActivity
                        .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                if ( entriesListFragment != null) {
                    /*Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD,getCurrentField());
                    entriesListFragment.getLoaderManager().restartLoader(0,bundle,entriesListFragment.getFieldEntries);*/
                    /*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    customCursorAdapterForFieldEntriesSpinner = entriesListFragment.getCustomCursorAdapterForFieldEntriesListView();
                    customCursorAdapterForFieldEntriesSpinner.changeCursor(fieldEntriesCursor);*/
                }
            }
        });

        saveEntryAndClearFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseOperations.insertFieldEntry(getCurrentField(), enterQuestion.getText().toString(), enterAnswer.getText().toString());
                enterQuestion.setText("");
                enterAnswer.setText("");
                FieldActivity fieldActivity = (FieldActivity) getActivity();
                EntriesListFragment entriesListFragment = (EntriesListFragment) fieldActivity
                        .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                if ( entriesListFragment != null ) {
                    /*Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD,getCurrentField());
                    entriesListFragment.getLoaderManager().restartLoader(0, bundle, entriesListFragment.getFieldEntries);*/
                    /*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    customCursorAdapterForFieldEntriesSpinner = entriesListFragment.getCustomCursorAdapterForFieldEntriesListView();
                    customCursorAdapterForFieldEntriesSpinner.changeCursor(fieldEntriesCursor);*/
                }
            }
        });

        clearQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterQuestion.setText("");
                enterQuestion.requestFocus();
            }
        });

        clearAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAnswer.setText("");
                enterAnswer.requestFocus();
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Fragment fragment = (EntriesListFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
        if ( fragment == null ) {
            showEntriesButton = new Button(getActivity());
            showEntriesButton.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            showEntriesButton.setText(getActivity().getText(R.string.show_entries));
            showEntriesButtonContainer.addView(showEntriesButton);
            showEntriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),EntriesListActivity.class);
                    intent.putExtra(FieldActivity.INITIAL_FIELD,getCurrentField());
                    startActivity(intent);
                }
            });
        }
    }
    public String getCurrentField() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
        // getting the data from the preferences and set the selected item of the field spinner.

        // to be sure that the last used position in this spinner is still avilable ( may be the this position has been deleted )
                /*if ( simpleCursorAdapterForFieldSpinner.getCount() >= (lastUsedPosition + 1) ) {
                    fieldsListSpinner.setSelection(lastUsedPosition);
                }*/
        return sharedPreferences.getString(FieldActivity.CURRENT_FIELD,"");
    }

    @Override
    public void onDestroy() {
//        sqLiteDatabase.close();
        if ( databaseHelper != null ) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
