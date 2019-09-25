package com.ahmedsameha1.peninbin.view.activities;

import android.app.LoaderManager;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;

/**
 * Created by ahmed on 5/12/17.
 */

public abstract class FullyRandomTest extends AppCompatActivity {
    Cursor fieldEntriesCursor;
    DatabaseReadOperations databaseOperations;
    CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    long current_field_id;
    String[][] listForTest;
    LoaderManager.LoaderCallbacks<Cursor> loader;

    String[][] getListForTest(double percent) {
        int numberOfQuestions = (int) ((percent / 100) * fieldEntriesCursor.getCount()) > 0 ? (int) ((percent / 100) * fieldEntriesCursor.getCount()) : 1;
        String[][] listForTest = new String[numberOfQuestions][3];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][3];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            listOfEntries[fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries[fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
            listOfEntries[fieldEntriesCursor.getPosition()][2] = String.valueOf(fieldEntriesCursor.getLong(0));
        }
        ActivityUtility.shuffleArray(listOfEntries);
        for (int n = 0; n < listForTest.length; n++) {
            listForTest[n][0] = listOfEntries[n][0];
            listForTest[n][1] = listOfEntries[n][1];
            listForTest[n][2] = listOfEntries[n][2];
        }
        return listForTest;
    }

    String[][] getListForTest(int numberOfQuestions) {
        String[][] listForTest = new String[numberOfQuestions][3];
        String[][] listOfEntries = new String[fieldEntriesCursor.getCount()][3];
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            listOfEntries[fieldEntriesCursor.getPosition()][0] = fieldEntriesCursor.getString(1);
            listOfEntries[fieldEntriesCursor.getPosition()][1] = fieldEntriesCursor.getString(2);
            listOfEntries[fieldEntriesCursor.getPosition()][2] = String.valueOf(fieldEntriesCursor.getLong(0));
        }
        ActivityUtility.shuffleArray(listOfEntries);
        for (int n = 0; n < listForTest.length; n++) {
            listForTest[n][0] = listOfEntries[n][0];
            listForTest[n][1] = listOfEntries[n][1];
            listForTest[n][2] = listOfEntries[n][2];
        }
        return listForTest;
    }
    abstract void DoWhatYouHaveToDoAfterTimeOfAnswerElapsed();
}
