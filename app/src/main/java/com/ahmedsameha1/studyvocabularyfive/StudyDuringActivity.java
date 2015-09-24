package com.ahmedsameha1.studyvocabularyfive;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by ahmed_000 on 9/23/2015.
 */
public class StudyDuringActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String CURRENT_FIELD = "currentField";
    public static final String STARTING_TIME = "startingTime";
    private String currentField;
    private long startingTime;
    private Cursor cursor;
    private ArrayList<String> questions, answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentField = getIntent().getStringExtra(CURRENT_FIELD);
        startingTime = getIntent().getLongExtra(STARTING_TIME,0);
    }
    private void prepareListsForStudy() {
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            Timestamp timestamp = Timestamp.valueOf(cursor.getString(2));
            long thisTime = timestamp.getTime();
            if ( thisTime >= startingTime ) {

            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return new CursorLoader(this,
                uri,
                new String [] {Contract.Linker.COLUMN_NAME_QUESTION_LINK, Contract.Linker.COLUMN_NAME_ANSWER_LINK, Contract.COLUMN_NAME_CREATED_AT},
                Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {currentField},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.cursor = null;
    }
}
