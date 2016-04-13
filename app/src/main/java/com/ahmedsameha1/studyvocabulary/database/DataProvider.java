package com.ahmedsameha1.studyvocabulary.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by ahmed on 9/11/2015.
 */
public class DataProvider extends ContentProvider {
    private SQLiteDatabase sqLiteDatabase;
    private static final String AUTHORITY = "com.ahmedsameha1.studyvocabulary.contentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" );
    @Override
    public boolean onCreate() {
        sqLiteDatabase = DatabaseHelper.getInstance(getContext()).getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = uri.getLastPathSegment();
        Cursor cursor = sqLiteDatabase.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = uri.getLastPathSegment();
        sqLiteDatabase.insert(tableName,null,values);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = uri.getLastPathSegment();
        int d = sqLiteDatabase.delete(tableName,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return d;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = uri.getLastPathSegment();
        sqLiteDatabase.update(tableName,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
