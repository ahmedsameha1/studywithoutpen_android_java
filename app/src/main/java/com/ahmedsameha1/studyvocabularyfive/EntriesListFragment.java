package com.ahmedsameha1.studyvocabularyfive;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ToggleButton;

/**
 * Created by ahmed_000 on 8/23/2015.
 */
public class EntriesListFragment extends Fragment {
    public static final String SEARCH_VALUE = "searchValue";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    private EditText searchFor;
    private ImageButton clearSearch;
    private ListView fieldEntriesListView;
    private CustomCursorAdapter customCursorAdapterForFieldEntriesListView;
    protected LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;



    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private String[] deleteEntryData = {null, null, null};
    private Cursor fieldEntriesCursor, searchForCursor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
//        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if ( id == 1) {
                    String currentField = getCurrentField();
                    String searchString = args.getString(EntriesListFragment.SEARCH_VALUE);
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(),
                            uri,
                            new String[] {"rowid _id",
                                    Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                    Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                                    + " OR " +
                                    Contract.Linker.COLUMN_NAME_ANSWER_LINK
                                    + " LIKE ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[] {searchString + "%",currentField,searchString + "%",currentField},
                            null);
                } else {
                    String currentField = null;
                    if ( args != null ) {
                        currentField = args.getString(FieldActivity.CURRENT_FIELD);
                    }
                    currentField = currentField == null ? getCurrentField() : currentField;
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(),uri,
                            new String[] {"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[] {currentField},
                            null);
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                customCursorAdapterForFieldEntriesListView.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                customCursorAdapterForFieldEntriesListView.swapCursor(null);
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        /*databaseHelper = new DatabaseHelper(getActivity());
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        databaseOperations = new DatabaseOperations(sqLiteDatabase);*/
        /*customCursorAdapterForFieldEntriesListView = initializeCustomCursorAdapterForFieldEntriesSpinner();
        fieldEntriesListView.setAdapter(customCursorAdapterForFieldEntriesListView);*/
    }

    @Override
    public void onDestroy() {
//        sqLiteDatabase.close();
        databaseHelper.close();
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstacneState ) {
        View view = inflater.inflate(R.layout.entries_list_fragment,parent,false);
        fieldEntriesListView = (ListView) view.findViewById(R.id.field_entries_list_view);
        searchFor = (EditText) view.findViewById(R.id.search_for);
        clearSearch = (ImageButton) view.findViewById(R.id.clear_search);
        searchFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_VALUE,s.toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID,bundle, getFieldEntries);
                    /*searchForCursor = databaseOperations.searchFor(getCurrentField(), s.toString());
                    customCursorAdapterForFieldEntriesListView.
                            changeCursor(searchForCursor);*/
                } else {
                    customCursorAdapterForFieldEntriesListView.changeCursor(null);
                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD,getCurrentField());
                    getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    /*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    customCursorAdapterForFieldEntriesListView.changeCursor(fieldEntriesCursor);*/
                }
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFor.setText("");
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                Bundle bundle = new Bundle();
                bundle.putString(FieldActivity.CURRENT_FIELD,getCurrentField());
                getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        customCursorAdapterForFieldEntriesListView = initializeCustomCursorAdapterForFieldEntriesSpinner();
        fieldEntriesListView.setAdapter(customCursorAdapterForFieldEntriesListView);
        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, getFieldEntries);
    }

    private CustomCursorAdapter initializeCustomCursorAdapterForFieldEntriesSpinner() {
    /*    if ( fieldEntriesCursor != null ) {
            fieldEntriesCursor.close();
        }*/
//        fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
        return new CustomCursorAdapter(getActivity(), null, false);
    }
    public String getCurrentField() {
        if ( getActivity() instanceof FieldActivity ) {
            /*Spinner fieldsListSpinner = ( (FieldActivity) getActivity() ).getFieldsListSpinner();
            Cursor cursor = (Cursor) fieldsListSpinner.getSelectedItem();
            String string = cursor.getString(1);*/
            /*String string = ( (FieldActivity) getActivity() ).currentField;
            return string;*/
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                // getting the data from the preferences and set the selected item of the field spinner.
                int lastUsedPosition = sharedPreferences.getInt(MainActivity.LAST_USED_FIELD_POSITION, 0);
                // to be sure that the last used position in this spinner is still avilable ( may be the this position has been deleted )
                /*if ( simpleCursorAdapterForFieldSpinner.getCount() >= (lastUsedPosition + 1) ) {
                    fieldsListSpinner.setSelection(lastUsedPosition);
                }*/
            return sharedPreferences.getString(FieldActivity.CURRENT_FIELD,"");

        } else {
            String string = getActivity().getIntent().getStringExtra(FieldActivity.INITIAL_FIELD);
            return string;
        }
    }
    public void deleteFieldEntry(DeleteEntryDialogFragment dialog) {
//        DatabaseHelper databaseHelper = new DatabaseHelper(this);
//        RadioGroup radioGroup = (RadioGroup) dialog.getDialog().findViewById(R.id.delete_entry_radio_group);
        databaseOperations.deleteFieldEntry(deleteEntryData[1], deleteEntryData[2],deleteEntryData[0]);
        /*switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_delete_only_this_entry:

                break;
            *//*case R.id.radio_delete_this_question:
                databaseOperations.deleteThisFieldQuestion(deleteEntryData[0], deleteEntryData[1]);
                break;
            case R.id.radio_delete_this_answer:
                databaseOperations.deleteThisFieldAnswer(deleteEntryData[0], deleteEntryData[2]);
                break;
            case R.id.radio_delete_this_entry_and_its_root:
                databaseOperations.deleteThisEntryAndItsRoots(deleteEntryData[0], deleteEntryData[1], deleteEntryData[2]);
                break;*//*
        }*/
/*        fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
        customCursorAdapterForFieldEntriesListView.changeCursor(fieldEntriesCursor);*/
        deleteEntryData[0] = deleteEntryData[1] = deleteEntryData[2] = null;
    }
    public CustomCursorAdapter getCustomCursorAdapterForFieldEntriesListView() {
        return customCursorAdapterForFieldEntriesListView;
    }
    public class CustomCursorAdapter extends CursorAdapter {

        public CustomCursorAdapter(Context context, Cursor cursor, boolean autoRequery) {
            super(context, cursor, autoRequery);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final EditText editQuestion, editAnswer;
            final ImageButton studyEntry, saveEditedEntry, deleteEntry;
            final ToggleButton editEntry;
            editQuestion = (EditText) view.findViewById(R.id.question_field);
            editAnswer = (EditText) view.findViewById(R.id.answer_field);
            studyEntry = (ImageButton) view.findViewById(R.id.study_entry);
            saveEditedEntry = (ImageButton) view.findViewById(R.id.save_edited_entry);
            deleteEntry = (ImageButton) view.findViewById(R.id.delete_entry);
            editEntry = (ToggleButton) view.findViewById(R.id.edit_entry);
            editQuestion.setText(cursor.getString(1));
            editAnswer.setText(cursor.getString(2));
            final String question = cursor.getString(1);
            final String answer = cursor.getString(2);
            editEntry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        editQuestion.setEnabled(true);
                        editAnswer.setEnabled(true);
                        saveEditedEntry.setEnabled(true);
                        studyEntry.setEnabled(false);

                    } else {
                        editQuestion.setEnabled(false);
                        editAnswer.setEnabled(false);
                        saveEditedEntry.setEnabled(false);
                        studyEntry.setEnabled(true);
                    }
                }
            });
            studyEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StudyActivity.class);
                    intent.putExtra(StudyActivity.CURRENT_FIELD, getCurrentField());
                    intent.putExtra(StudyActivity.CURRENT_QUESTION, editQuestion.getText().toString());
                    intent.putExtra(StudyActivity.CURRENT_ANSWER, editAnswer.getText().toString());
                    startActivity(intent);
                }
            });
            deleteEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEntryData[0] = getCurrentField();
                    deleteEntryData[1] = editQuestion.getText().toString();
                    deleteEntryData[2] = editAnswer.getText().toString();
                    DeleteEntryDialogFragment fragment = new DeleteEntryDialogFragment();
                    if ( getActivity() instanceof FieldActivity ) {
                        FragmentManager fragmentManager = ((FieldActivity) getActivity()).getSupportFragmentManager();
                        fragment.show(fragmentManager, DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                    } else {
                        fragment.show(getActivity().getSupportFragmentManager(),
                                DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                    }
                }
            });
            saveEditedEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editQuestion.setEnabled(false);
                    editAnswer.setEnabled(false);
                    saveEditedEntry.setEnabled(false);
                    editEntry.setChecked(false);
//                    DatabaseHelper databaseHelper = new DatabaseHelper(FieldActivity.this);
                    if (!editQuestion.getText().toString().equals(question) || !editAnswer.getText().toString().equals(answer)) {
                        databaseOperations.editFieldEntry(getCurrentField(), question, editQuestion.getText().toString(),
                                answer, editAnswer.getText().toString());
                    }
                    /*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    CustomCursorAdapter.this.changeCursor(fieldEntriesCursor);*/
                    searchFor.setText("");
                }
            });
          /*  editQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ((v == editQuestion) && !hasFocus)
                        editQuestionLostFocusTimestamp = System.currentTimeMillis();
                }
            });
            editAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if ((v == editAnswer) && !hasFocus)
                        editAnswerLostFocusTimestamp = System.currentTimeMillis();
                }
            });*/
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.field_entries_list_view, parent, false);
            return view;

        }
    }

}
