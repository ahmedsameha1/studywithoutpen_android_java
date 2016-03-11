package com.ahmedsameha1.studyvocabularyfive;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseHelper;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.Dialogs.DeleteEntryDialogFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed_000 on 8/23/2015.
 */
public class EntriesListFragment extends Fragment {
    public static final String SEARCH_QUESTION_VALUE = "searchQuestionValue";
    public static final String SEARCH_ANSWER_VALUE = "searchAnswerValue";
    public static final int GET_ENTRIES_LOADER_ID = 0;
    public static final int SEARCH_ENTRIES_LOADER_ID = 1;
    public static final int ORDER_QUESTION_DESC = 2;
    public static final int ORDER_ANSWER_DESC = 3;
    public static final int ORDER_QUESTION_ASC = 4;
    public static final int ORDER_ANSWER_ASC = 5;
    public static final int ORDER_DATE_DESC = 6;
    public static final int ORDER_DATE_ASC = 7;
    public static final int ORDER_ENTRY_DATE_DESC = 8;
    public static final int ORDER_ENTRY_DATE_ASC = 9;
    public static final String FIELD_TYPE = "fieldType";
    protected int fieldType = 0;
    /*    protected EditText searchFor;
        private ImageButton clearSearch;*/
    private EditText searchQuestion, searchAnswer;
    private ImageButton clearSearchQuestion, clearSearchAnswer;
    private ListView fieldEntriesListView;
    private ToggleButton sortQuestion, sortAnswer, sortEntryDate;
    private CustomCursorAdapter customCursorAdapterForFieldEntriesListView;
    protected LoaderManager.LoaderCallbacks<Cursor> getFieldEntries;
    private int currentLoader = GET_ENTRIES_LOADER_ID;

    private DatabaseHelper databaseHelper;
    //    private SQLiteDatabase sqLiteDatabase;
    private DatabaseOperations databaseOperations;
    private String[] deleteEntryData = {null, null, null};
    private Cursor fieldEntriesCursor, searchForCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());
        databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
        if (getActivity() instanceof EntriesListActivity) {
            fieldType = getActivity().getIntent().getIntExtra(FIELD_TYPE, 0);
        }
        getFieldEntries = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if (id == SEARCH_ENTRIES_LOADER_ID) {
                    customCursorAdapterForFieldEntriesListView.changeCursor(null);
                    String currentField = getCurrentField();
                    String searchQuestion = args.getString(EntriesListFragment.SEARCH_QUESTION_VALUE);
                    String searchAnswer = args.getString(EntriesListFragment.SEARCH_ANSWER_VALUE);
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    if (searchQuestion.length() > 0 && searchAnswer.length() > 0) {
                        return new CursorLoader(getActivity(),
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? "
                                        + " AND " +
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK
                                        + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchQuestion + "%", currentField, "%" + searchAnswer + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    } else if (searchQuestion.length() > 0) {
                        return new CursorLoader(getActivity(),
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_QUESTION_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchQuestion + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    } else {
                        return new CursorLoader(getActivity(),
                                uri,
                                new String[]{"rowid _id",
                                        Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                                        Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                                Contract.Linker.COLUMN_NAME_ANSWER_LINK + " LIKE ? AND "
                                        + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                                new String[]{"%" + searchAnswer + "%", currentField},
                                "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                    }
                } else if (id == ORDER_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            "date(" + Contract.Linker.COLUMN_NAME_QUESTION_LINK + ") DESC");

                } else if (id == ORDER_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            "date(" + Contract.Linker.COLUMN_NAME_QUESTION_LINK + ") ASC");
                } else if (id == ORDER_QUESTION_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " DESC");
                } else if (id == ORDER_QUESTION_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " ASC");
                } else if (id == ORDER_ANSWER_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK + " DESC");
                } else if (id == ORDER_ANSWER_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            Contract.Linker.COLUMN_NAME_ANSWER_LINK + " ASC");
                } else if (id == ORDER_ENTRY_DATE_DESC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
                } else if (id == ORDER_ENTRY_DATE_ASC) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") ASC");
                } else {
                    /*String currentField = null;
                    if (args != null) {
                        currentField = args.getString(FieldActivity.CURRENT_FIELD);
                    }
                    currentField = currentField == null ? getCurrentField() : currentField;*/
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    return new CursorLoader(getActivity(), uri,
                            new String[]{"rowid _id", Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK},
                            Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                            new String[]{getCurrentField()},
                            "datetime(" + Contract.COLUMN_NAME_CREATED_AT + ") DESC");
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
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstacneState) {
        View view = inflater.inflate(R.layout.entries_list_fragment, parent, false);
        fieldEntriesListView = (ListView) view.findViewById(R.id.field_entries_list_view);
        searchQuestion = (EditText) view.findViewById(R.id.search_question);
        searchAnswer = (EditText) view.findViewById(R.id.search_answer);
        clearSearchQuestion = (ImageButton) view.findViewById(R.id.clear_search_question);
        clearSearchAnswer = (ImageButton) view.findViewById(R.id.clear_search_answer);
        sortQuestion = (ToggleButton) view.findViewById(R.id.sort_question);
        sortAnswer = (ToggleButton) view.findViewById(R.id.sort_answer);
        sortEntryDate = (ToggleButton) view.findViewById(R.id.sort_entry_date);
        searchQuestion.addTextChangedListener(new TextWatcher() {
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
                    bundle.putString(SEARCH_QUESTION_VALUE, s.toString());
                    bundle.putString(SEARCH_ANSWER_VALUE, searchAnswer.getText().toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                } else {
                    getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
                    customCursorAdapterForFieldEntriesListView.changeCursor(null);
                    if (searchAnswer.getText().toString().length() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString(SEARCH_QUESTION_VALUE, "");
                        bundle.putString(SEARCH_ANSWER_VALUE, searchAnswer.getText().toString());
                        getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    }
                }
            }
        });
        searchAnswer.addTextChangedListener(new TextWatcher() {
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
                    bundle.putString(SEARCH_ANSWER_VALUE, s.toString());
                    bundle.putString(SEARCH_QUESTION_VALUE, searchQuestion.getText().toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                } else {
                    getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
                    customCursorAdapterForFieldEntriesListView.changeCursor(null);
                    if (searchQuestion.getText().toString().length() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString(SEARCH_ANSWER_VALUE, "");
                        bundle.putString(SEARCH_QUESTION_VALUE, searchQuestion.getText().toString());
                        getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    }
                }
            }
        });
        searchQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    switch (fieldType) {
                        case Contract.Field.NOTEBOOK_TYPE:
                            searchAnswer.requestFocus();
                            Calendar calendar = Calendar.getInstance();
                            new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    final Calendar calendar1 = Calendar.getInstance();
                                    calendar1.set(year, monthOfYear, dayOfMonth);
                                    String formated = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar1.getTime());
                                    searchQuestion.setText(formated);
                                }
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                            break;
                    }
                }
            }
        });
        clearSearchQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuestion.setText("");
                getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                if (searchAnswer.getText().toString().length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_QUESTION_VALUE, "");
                    bundle.putString(SEARCH_ANSWER_VALUE, searchAnswer.getText().toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                    getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                }
                /*searchFor.setText("");
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                Bundle bundle = new Bundle();
                bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);*/
            }
        });
        clearSearchAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAnswer.setText("");
                getLoaderManager().destroyLoader(SEARCH_ENTRIES_LOADER_ID);
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                if (searchQuestion.getText().toString().length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_ANSWER_VALUE, "");
                    bundle.putString(SEARCH_QUESTION_VALUE, searchQuestion.getText().toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                    getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                }
                /*searchFor.setText("");
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                Bundle bundle = new Bundle();
                bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);*/
            }
        });
        sortQuestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch (fieldType) {
                        case Contract.Field.NOTEBOOK_TYPE:
                            getLoaderManager().restartLoader(ORDER_DATE_DESC, null, getFieldEntries);
                            currentLoader = ORDER_DATE_DESC;
                            break;
                        case Contract.Field.DEFAULT_TYPE:
                            getLoaderManager().restartLoader(ORDER_QUESTION_DESC, null, getFieldEntries);
                            currentLoader = ORDER_QUESTION_DESC;
                            break;
                    }
                } else {
                    switch (fieldType) {
                        case Contract.Field.NOTEBOOK_TYPE:
                            getLoaderManager().restartLoader(ORDER_DATE_ASC, null, getFieldEntries);
                            currentLoader = ORDER_DATE_ASC;
                            break;
                        case Contract.Field.DEFAULT_TYPE:
                            getLoaderManager().restartLoader(ORDER_QUESTION_ASC, null, getFieldEntries);
                            currentLoader = ORDER_QUESTION_ASC;
                            break;
                    }
                }
            }
        });
        sortAnswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getLoaderManager().restartLoader(ORDER_ANSWER_DESC, null, getFieldEntries);
                    currentLoader = ORDER_ANSWER_DESC;
                } else {
                    getLoaderManager().restartLoader(ORDER_ANSWER_ASC, null, getFieldEntries);
                    currentLoader = ORDER_ANSWER_ASC;
                }
            }
        });
        sortEntryDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getLoaderManager().restartLoader(ORDER_ENTRY_DATE_ASC, null, getFieldEntries);
                    currentLoader = ORDER_ENTRY_DATE_ASC;
                } else {
                    getLoaderManager().restartLoader(ORDER_ENTRY_DATE_DESC, null, getFieldEntries);
                    currentLoader = ORDER_ENTRY_DATE_DESC;
                }
            }
        });
        /*sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    getLoaderManager().restartLoader(ORDER_DESC,null,getFieldEntries);
                }
                else {
                    getLoaderManager().restartLoader(ORDER_ASC,null,getFieldEntries);
                }
            }
        });*/
//        searchFor = (EditText) view.findViewById(R.id.search_for);
//        clearSearch = (ImageButton) view.findViewById(R.id.clear_search);
/*        searchFor.addTextChangedListener(new TextWatcher() {
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
                    bundle.putString(SEARCH_VALUE, s.toString());
                    getLoaderManager().restartLoader(SEARCH_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    *//*searchForCursor = databaseOperations.searchFor(getCurrentField(), s.toString());
                    customCursorAdapterForFieldEntriesListView.
                            changeCursor(searchForCursor);*//*
                } else {
                    customCursorAdapterForFieldEntriesListView.changeCursor(null);
                    Bundle bundle = new Bundle();
                    bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                    getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
                    *//*fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
                    customCursorAdapterForFieldEntriesListView.changeCursor(fieldEntriesCursor);*//*
                }
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFor.setText("");
                customCursorAdapterForFieldEntriesListView.changeCursor(null);
                Bundle bundle = new Bundle();
                bundle.putString(FieldActivity.CURRENT_FIELD, getCurrentField());
                getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, bundle, getFieldEntries);
            }
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        customCursorAdapterForFieldEntriesListView = initializeCustomCursorAdapterForFieldEntriesSpinner();
        fieldEntriesListView.setAdapter(customCursorAdapterForFieldEntriesListView);
        getLoaderManager().restartLoader(GET_ENTRIES_LOADER_ID, null, getFieldEntries);
        searchAnswer.requestFocus();
    }

    private CustomCursorAdapter initializeCustomCursorAdapterForFieldEntriesSpinner() {
    /*    if ( fieldEntriesCursor != null ) {
            fieldEntriesCursor.close();
        }*/
//        fieldEntriesCursor = databaseOperations.getFieldEntries(getCurrentField());
        return new CustomCursorAdapter(getActivity(), null, false);
    }

    public String getCurrentField() {
        if (getActivity() instanceof FieldActivity) {
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
            return sharedPreferences.getString(FieldActivity.CURRENT_FIELD, "");

        } else {
            String string = getActivity().getIntent().getStringExtra(FieldActivity.INITIAL_FIELD);
            return string;
        }
    }

    public void deleteFieldEntry(DeleteEntryDialogFragment dialog) {
//        DatabaseHelper databaseHelper = new DatabaseHelper(this);
//        RadioGroup radioGroup = (RadioGroup) dialog.getDialog().findViewById(R.id.delete_entry_radio_group);
        databaseOperations.deleteFieldEntry(deleteEntryData[1], deleteEntryData[2], deleteEntryData[0]);
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
        public void bindView(View view, Context context, final Cursor cursor) {
            final EditText editQuestion, editAnswer;
            final ImageButton studyEntry, saveEditedEntry, deleteEntry;
            final ToggleButton editEntry;
            editQuestion = (EditText) view.findViewById(R.id.question_field);
            editAnswer = (EditText) view.findViewById(R.id.answer_field);
            studyEntry = (ImageButton) view.findViewById(R.id.study_entry);
            saveEditedEntry = (ImageButton) view.findViewById(R.id.save_edited_entry);
            deleteEntry = (ImageButton) view.findViewById(R.id.delete_entry);
            editEntry = (ToggleButton) view.findViewById(R.id.edit_entry);
            final String question = cursor.getString(1);
            final String answer = cursor.getString(2);
            switch (fieldType) {
                case Contract.Field.DEFAULT_TYPE:
                    editQuestion.setText(question);
                    editAnswer.setText(answer);
                    break;
                case Contract.Field.NOTEBOOK_TYPE:
                    Timestamp timestamp = Timestamp.valueOf(question);
                    Date date = new Date(timestamp.getTime());
                    editQuestion.setText(new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm").format(date));
                    editAnswer.setText(answer);
                    break;
            }

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
                        CustomCursorAdapter.this.notifyDataSetChanged();
                    }
                }
            });
            studyEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( databaseOperations.getFieldType(getCurrentField()) != Contract.Field.NOTEBOOK_TYPE) {
                        switch (fieldType) {
                            case Contract.Field.DEFAULT_TYPE:
                                Intent intent = new Intent(getActivity(), StudyActivity.class);
                                intent.putExtra(StudyActivity.CURRENT_FIELD, getCurrentField());
                                intent.putExtra(StudyActivity.CURRENT_QUESTION, editQuestion.getText().toString());
                                intent.putExtra(StudyActivity.CURRENT_ANSWER, editAnswer.getText().toString());
                                intent.putExtra(StudyActivity.ORDER_OF_ENTRIES,currentLoader);
                                startActivity(intent);
                                break;
                            case Contract.Field.NOTEBOOK_TYPE:
                                Toast toast = Toast.makeText(getActivity(), R.string.notbook_feature_is_not_active, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                                break;
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.study_not_for_notebook,Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                }
            });
            deleteEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                    Cursor studyDuringCursor = getActivity().getContentResolver().query(uri,
                            new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER + " >= ? ",
                            new String[]{editQuestion.getText().toString(), editAnswer.getText().toString(), getCurrentField(), "0"},
                            null);
                    Cursor testCursor = getActivity().getContentResolver().query(uri,
                            new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                            Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? AND "
                                    + Contract.Linker.COLUMN_NAME_TEST_ORDER + " >= ? ",
                            new String[]{editQuestion.getText().toString(), editAnswer.getText().toString(), getCurrentField(), "0"},
                            null);
                    if (studyDuringCursor.getCount() <= 0 && testCursor.getCount() <= 0) {
                        deleteEntryData[0] = getCurrentField();
                        deleteEntryData[1] = editQuestion.getText().toString();
                        deleteEntryData[2] = editAnswer.getText().toString();
                        DeleteEntryDialogFragment fragment = new DeleteEntryDialogFragment();
                        if (getActivity() instanceof FieldActivity) {
                            FragmentManager fragmentManager = ((FieldActivity) getActivity()).getSupportFragmentManager();
                            fragment.show(fragmentManager, DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                        } else {
                            fragment.show(getActivity().getSupportFragmentManager(),
                                    DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.complete_or_cancel_last_study_session, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                    studyDuringCursor.close();
                    testCursor.close();
                }
            });
            saveEditedEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (fieldType) {
                        case Contract.Field.DEFAULT_TYPE:
                            editQuestion.setEnabled(false);
                            editAnswer.setEnabled(false);
                            saveEditedEntry.setEnabled(false);
                            editEntry.setChecked(false);
                            if (!editQuestion.getText().toString().equals(question) || !editAnswer.getText().toString().equals(answer)) {
                                boolean b = databaseOperations.editFieldEntry(getCurrentField(), question, editQuestion.getText().toString(),
                                        answer, editAnswer.getText().toString());
                                if (!b) {
                                    Toast toast = Toast.makeText(getActivity(), R.string.repeated_entry, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
                                }
                            }
                            break;
                        case Contract.Field.NOTEBOOK_TYPE:
                            editQuestion.setEnabled(false);
                            editAnswer.setEnabled(false);
                            saveEditedEntry.setEnabled(false);
                            editEntry.setChecked(false);
                            String dateTimeUserFormat = editQuestion.getText().toString();
                            StringTokenizer stringTokenizer = new StringTokenizer(dateTimeUserFormat, "\n");
                            stringTokenizer.nextToken();
                            String dateToSave = stringTokenizer.nextToken() + " "
                                    + stringTokenizer.nextToken() + ":00";
                            if (!dateToSave.equals(question) || !editAnswer.getText().toString().equals(answer)) {
                                boolean b = databaseOperations.editFieldEntry(getCurrentField(), question, dateToSave,
                                        answer, editAnswer.getText().toString());
                                if (!b) {
                                    Toast toast = Toast.makeText(getActivity(), R.string.repeated_entry, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
                                }
                            }
                            break;
                    }
                }
            });
            editQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        switch (fieldType) {
                            case Contract.Field.NOTEBOOK_TYPE:
                                editAnswer.requestFocus();
//                                editQuestion.clearFocus();
                                Calendar calendar = Calendar.getInstance();
                                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                        final Calendar calendar = Calendar.getInstance();
                                        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                                editQuestion.setText(new SimpleDateFormat("EEEE\nyyyy-MM-dd\nHH:mm", Locale.ENGLISH).format(calendar.getTime()));
//                                                editAnswer.requestFocus();
                                            }
                                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                                break;
                        }
                    }
                }
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = null;
            switch (fieldType) {
                case Contract.Field.DEFAULT_TYPE:
                    view = LayoutInflater.from(context).inflate(R.layout.field_entries_list_view, parent, false);
                    break;
                case Contract.Field.NOTEBOOK_TYPE:
                    view = LayoutInflater.from(context).inflate(R.layout.notebook_field_entries_list_view, parent, false);
                    break;
            }

            return view;

        }
    }

}
