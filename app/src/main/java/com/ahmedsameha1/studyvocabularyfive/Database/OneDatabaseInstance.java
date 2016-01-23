package com.ahmedsameha1.studyvocabularyfive.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ahmed on 1/23/2016.
 */
public class OneDatabaseInstance {
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static OneDatabaseInstance instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new OneDatabaseInstance();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized OneDatabaseInstance getInstance() {
        if (instance == null) {
            throw new IllegalStateException(OneDatabaseInstance.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}
