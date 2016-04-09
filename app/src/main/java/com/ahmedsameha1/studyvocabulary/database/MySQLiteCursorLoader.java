package com.ahmedsameha1.studyvocabulary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.commonsware.cwac.loaderex.AbstractCursorLoader;
import com.commonsware.cwac.loaderex.ContentChangingTask;
import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by Ahmed on 4/9/16.
 */
public class MySQLiteCursorLoader extends AbstractCursorLoader {
    SQLiteOpenHelper db=null;
    String rawQuery=null;
    String[] args=null;


    public MySQLiteCursorLoader(Context context, SQLiteOpenHelper db,
                                String rawQuery, String[] args) {
        super(context);
        this.db=db;
        this.rawQuery=rawQuery;
        this.args=args;
    }

    /**
     * Runs on a worker thread and performs the actual
     * database query to retrieve the Cursor.
     */
    @Override
    protected Cursor buildCursor() {
        return(db.getReadableDatabase().rawQuery(rawQuery, args));
    }

    /**
     * Writes a semi-user-readable roster of contents to
     * supplied output.
     */
    @Override
    public void dump(String prefix, FileDescriptor fd,
                     PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("rawQuery=");
        writer.println(rawQuery);
        writer.print(prefix);
        writer.print("args=");
        writer.println(Arrays.toString(args));
    }

    public void insert(String table, String nullColumnHack,
                       ContentValues values) {
        buildInsertTask(this).execute(db, table, nullColumnHack, values);
    }

    public void update(String table, ContentValues values,
                       String whereClause, String[] whereArgs) {
        buildUpdateTask(this).execute(db, table, values, whereClause,
                whereArgs);
    }

    public void replace(String table, String nullColumnHack,
                        ContentValues values) {
        buildReplaceTask(this).execute(db, table, nullColumnHack, values);
    }

    public void delete(String table, String whereClause,
                       String[] whereArgs) {
        buildDeleteTask(this).execute(db, table, whereClause, whereArgs);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        buildExecSQLTask(this).execute(db, sql, bindArgs);
    }

    protected MyContentChangingTask buildInsertTask(MySQLiteCursorLoader loader) {
        return(new InsertTask(loader));
    }

    protected MyContentChangingTask buildUpdateTask(MySQLiteCursorLoader loader) {
        return(new UpdateTask(loader));
    }

    protected MyContentChangingTask buildReplaceTask(MySQLiteCursorLoader loader) {
        return(new ReplaceTask(loader));
    }

    protected MyContentChangingTask buildDeleteTask(MySQLiteCursorLoader loader) {
        return(new DeleteTask(loader));
    }

    protected ContentChangingTask buildExecSQLTask(MySQLiteCursorLoader loader) {
        return(new ExecSQLTask(loader));
    }

    protected static class InsertTask extends MyContentChangingTask {
        InsertTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Long doInBackground(Object... params) {
            SQLiteOpenHelper db=(SQLiteOpenHelper)params[0];
            String table=(String)params[1];
            String nullColumnHack=(String)params[2];
            ContentValues values=(ContentValues)params[3];

            return db.getWritableDatabase().insertWithOnConflict(table, nullColumnHack, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    protected static class UpdateTask extends MyContentChangingTask {
        UpdateTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Long doInBackground(Object... params) {
            SQLiteOpenHelper db=(SQLiteOpenHelper)params[0];
            String table=(String)params[1];
            ContentValues values=(ContentValues)params[2];
            String where=(String)params[3];
            String[] whereParams=(String[])params[4];

            return Long.valueOf(db.getWritableDatabase()
                    .updateWithOnConflict(table, values, where, whereParams,SQLiteDatabase.CONFLICT_IGNORE));
        }
    }

    protected static class ReplaceTask extends MyContentChangingTask {
        ReplaceTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Long doInBackground(Object... params) {
            SQLiteOpenHelper db=(SQLiteOpenHelper)params[0];
            String table=(String)params[1];
            String nullColumnHack=(String)params[2];
            ContentValues values=(ContentValues)params[3];

            return db.getWritableDatabase().replace(table, nullColumnHack, values);
        }
    }

    protected static class DeleteTask extends MyContentChangingTask {
        DeleteTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Long doInBackground(Object... params) {
            SQLiteOpenHelper db=(SQLiteOpenHelper)params[0];
            String table=(String)params[1];
            String where=(String)params[2];
            String[] whereParams=(String[])params[3];

            return Long.valueOf(db.getWritableDatabase().delete(table, where, whereParams));
        }
    }

    protected static class ExecSQLTask extends ContentChangingTask {
        ExecSQLTask(MySQLiteCursorLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(Object... params) {
            SQLiteOpenHelper db=(SQLiteOpenHelper)params[0];
            String sql=(String)params[1];
            Object[] bindParams=(Object[])params[2];

            db.getWritableDatabase().execSQL(sql, bindParams);

            return(null);
        }
    }
}
