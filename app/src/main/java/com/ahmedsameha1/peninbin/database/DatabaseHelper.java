package com.ahmedsameha1.peninbin.database;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.ahmedsameha1.peninbin.activities.StudyDuringActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.Contract.OldField;

import android.preference.PreferenceManager;
import android.util.Log;
import com.ahmedsameha1.peninbin.database.Contract.Field;


import com.ahmedsameha1.peninbin.database.Contract.STring;
import com.ahmedsameha1.peninbin.database.Contract.String_Entry;
import com.ahmedsameha1.peninbin.database.Contract.Not_String;
import com.ahmedsameha1.peninbin.database.Contract.Not_String_Entry;
import com.ahmedsameha1.peninbin.database.Contract.Test_Wrong_Answer_String;
import com.ahmedsameha1.peninbin.database.Contract.Test_Wrong_Answer_Not_String;
import com.ahmedsameha1.peninbin.database.Contract.Test_Paused;
import com.ahmedsameha1.peninbin.database.Contract.Study_Period_Repeated_Entry;
import com.ahmedsameha1.peninbin.database.Contract.Study_Period_Paused;
import com.ahmedsameha1.peninbin.database.Contract.Alternative_Answer_String;
import com.ahmedsameha1.peninbin.database.Contract.Alternative_Answer_Not_String;
import com.ahmedsameha1.peninbin.database.Contract.Old_Question_DBV6_AppV32;
import com.ahmedsameha1.peninbin.database.Contract.Old_Answer_DBV6_AppV32;
import com.ahmedsameha1.peninbin.database.Contract.Old_Linker_DBV6_AppV32;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 7/4/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "data";
    private Context context;
    private static DatabaseHelper sInstance;
    private String app_database_path;

    public static final String CREATE_FIELD_TABLE = "CREATE TABLE IF NOT EXISTS " + Field.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + Field.COLUMN_FIELD_NAME + " TEXT UNIQUE NOT NULL, "
            + Field.COLUMN_FIELD_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_LOCALE + " TEXT DEFAULT NULL, "
            + Field.COLUMN_CHECK_TYPE + " INTEGER NOT NULL DEFAULT " + Field.DO_NOT_IGNORE_CASE + ", "
            + Field.COLUMN_SORT_BY + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_LIST_TEXT_SIZE + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_QUESTION_AREA_SIZE + " INTEGER NOT NULL DEFAULT 1, "
            + Field.COLUMN_ANSWER_AREA_SIZE + " INTEGER NOT NULL DEFAULT 1, "
            + Field.COLUMN_TEST_TEXT_SIZE + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_ACTION_BAR_SHOWN + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_READ_ANSWER + " INTEGER NOT NULL DEFAULT " + Field.DO_NOT_READ_ANSWER + ", "
            + Field.COLUMN_USAGE_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_CREATION_EMULATION_NUMBER + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS + " INTEGER NOT NULL DEFAULT " + Field.ZERO_RANDOM_QUICK_TESTS + ", "
            + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS + " INTEGER NOT NULL DEFAULT 0, "
            + Field.COLUMN_RANDOM_QUICK_TEST_CONTROLLED_PORTION + " INTEGER NOT NULL DEFAULT -1, "
            + Field.COLUMN_RANDOM_QUICK_TEST_WRONGNESS_LEVEL + " INTEGER NOT NULL DEFAULT -1, "
            + Field.COLUMN_RANDOM_QUICK_TEST_RANK_LEVEL + " INTEGER NOT NULL DEFAULT -1, "
            + Contract.CREATION_DATE + " DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME'))"
            + " );";
/*    public static final String CREATE_FIELD_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " + Field_Type.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Field_Type.COLUMN_NAME_FIELD_TYPE_NAME + " TEXT UNIQUE NOT NULL"
            + " );";*/
/*    public static final String CREATE_LOCALE_TABLE = "CREATE TABLE IF NOT EXISTS " + Locale.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Locale.COLUMN_NAME_LOCALE_NAME + " TEXT UNIQUE NOT NULL"
            + " );";*/
    public static final String CREATE_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + STring.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + STring.COLUMN_VALUE + " TEXT UNIQUE NOT NULL"
            + " );";
    public static final String CREATE_NOT_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + Not_String.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Not_String.COLUMN_VALUE + " BLOB NOT NULL"
            + " );";
    public static final String CREATE_STRING_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS " + String_Entry.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + String_Entry.COLUMN_QUESTION + " INTEGER NOT NULL, "
            + String_Entry.COLUMN_ANSWER + " INTEGER NOT NULL, "
            + String_Entry.COLUMN_FIELD + " INTEGER NOT NULL, "
            + String_Entry.COLUMN_PAUSED_TEST_ORDER + " INTEGER NOT NULL DEFAULT -1, "
            + String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " INTEGER NOT NULL DEFAULT -1, "
            + String_Entry.COLUMN_ASKED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + String_Entry.COLUMN_RANK + " INTEGER NOT NULL DEFAULT 0, "
            + String_Entry.COLUMN_CREATION_EMULATED_DATE + " DATETIME DEFAULT NULL, "
            + String_Entry.COLUMN_REMIND_AT + " INTEGER DEFAULT NULL, "
            + String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " INTEGER DEFAULT NULL, "
            + Contract.CREATION_DATE + " DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), "
            + "FOREIGN KEY(" + String_Entry.COLUMN_QUESTION + ") REFERENCES " + STring.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + "FOREIGN KEY(" + String_Entry.COLUMN_ANSWER + ") REFERENCES " + STring.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + "FOREIGN KEY(" + String_Entry.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + " UNIQUE ( " + String_Entry.COLUMN_QUESTION + ", "
            + String_Entry.COLUMN_ANSWER + ", "
            + String_Entry.COLUMN_FIELD + " )"
            + " );";
    public static final String CREATE_NOT_STRING_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS " + Not_String_Entry.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + Not_String_Entry.COLUMN_QUESTION + " INTEGER NOT NULL, "
            + Not_String_Entry.COLUMN_ANSWER + " INTEGER NOT NULL, "
            + Not_String_Entry.COLUMN_FIELD + " INTEGER NOT NULL, "
            + Not_String_Entry.COLUMN_PAUSED_TEST_ORDER + " INTEGER NOT NULL DEFAULT -1, "
            + Not_String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER + " INTEGER NOT NULL DEFAULT -1, "
            + Not_String_Entry.COLUMN_ASKED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + Not_String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + Not_String_Entry.COLUMN_RANK + " INTEGER NOT NULL DEFAULT 0, "
            + Not_String_Entry.COLUMN_CREATION_EMULATED_DATE + " DATETIME DEFAULT NULL, "
            + Contract.CREATION_DATE + " DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), "
            + "FOREIGN KEY(" + Not_String_Entry.COLUMN_QUESTION + ") REFERENCES " + Not_String.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + "FOREIGN KEY(" + Not_String_Entry.COLUMN_ANSWER + ") REFERENCES " + STring.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + "FOREIGN KEY(" + Not_String_Entry.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + " UNIQUE ( " + Not_String_Entry.COLUMN_QUESTION + ", "
            + Not_String_Entry.COLUMN_ANSWER + ", "
            + Not_String_Entry.COLUMN_FIELD + " )"
            + " );";
    public static final String CREATE_TEST_WRONG_ANSWER_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + Test_Wrong_Answer_String.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Test_Wrong_Answer_String.COLUMN_ENTRY + " INTEGER NOT NULL, "
            + Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + Test_Wrong_Answer_String.COLUMN_ENTRY + ") REFERENCES " + String_Entry.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    public static final String CREATE_TEST_WRONG_ANSWER_NOT_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + Test_Wrong_Answer_Not_String.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Test_Wrong_Answer_Not_String.COLUMN_ENTRY + " INTEGER NOT NULL, "
            + Test_Wrong_Answer_Not_String.COLUMN_WRONG_ANSWER + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + Test_Wrong_Answer_Not_String.COLUMN_ENTRY + ") REFERENCES " + Not_String_Entry.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    public static final String CREATE_TEST_PAUSED_TABLE = "CREATE TABLE IF NOT EXISTS " + Test_Paused.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Test_Paused.COLUMN_FIELD + " INTEGER UNIQUE NOT NULL, "
            + Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER + " INTEGER NOT NULL, "
            + Test_Paused.COLUMN_WRONG_ANSWERS_COUNT + " INTEGER NOT NULL, "
            + Test_Paused.COLUMN_TRIES_NUMBER + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + Test_Paused.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    public static final String CREATE_STUDY_PERIOD_REPEATED_ENTRY_TABLE = "CREATE TABLE IF NOT EXISTS " + Study_Period_Repeated_Entry.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Study_Period_Repeated_Entry.COLUMN_FIELD + " INTEGER NOT NULL, "
            + Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + Study_Period_Repeated_Entry.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + " UNIQUE ( " + Study_Period_Repeated_Entry.COLUMN_FIELD + ", "
            + Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY + " )"
            + " );";
    public static final String CREATE_STUDY_PERIOD_PAUSED_TABLE = "CREATE TABLE IF NOT EXISTS " + Study_Period_Paused.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Study_Period_Paused.COLUMN_FIELD + " INTEGER UNIQUE NOT NULL, "
            + Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER + " INTEGER NOT NULL, "
            + Study_Period_Paused.COLUMN_TRIES_NUMBER + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + Study_Period_Paused.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    public static final String CREATE_ALTERNATIVE_ANSWER_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + Alternative_Answer_String.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Alternative_Answer_String.COLUMN_ENTRY + " INTEGER NOT NULL, "
            + Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER + " INTEGER NOT NULL, "
            + Alternative_Answer_String.COLUMN_ANSWERED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + "FOREIGN KEY(" + Alternative_Answer_String.COLUMN_ENTRY + ") REFERENCES " + String_Entry.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + "FOREIGN KEY(" + Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER + ") REFERENCES " + STring.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + " UNIQUE ( " + Alternative_Answer_String.COLUMN_ENTRY + ", "
            + Alternative_Answer_String.COLUMN_ALTERNATIVE_ANSWER + " )"
            + " );";
    public static final String CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE = "CREATE TABLE IF NOT EXISTS " + Alternative_Answer_Not_String.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Alternative_Answer_Not_String.COLUMN_ENTRY + " INTEGER NOT NULL, "
            + Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER + " INTEGER NOT NULL, "
            + Alternative_Answer_Not_String.COLUMN_ANSWERED_COUNT + " INTEGER NOT NULL DEFAULT 0, "
            + "FOREIGN KEY(" + Alternative_Answer_Not_String.COLUMN_ENTRY + ") REFERENCES " + Not_String_Entry.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + "FOREIGN KEY(" + Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER + ") REFERENCES " + STring.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE NO ACTION, "
            + " UNIQUE ( " + Alternative_Answer_Not_String.COLUMN_ENTRY + ", "
            + Alternative_Answer_Not_String.COLUMN_ALTERNATIVE_ANSWER + " )"
            + " );";
    public static final String CREATE_EXCLUDED_TIME_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Excluded_Time.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Contract.Excluded_Time.COLUMN_FIELD + " INTEGER NOT NULL, "
            + Contract.Excluded_Time.COLUMN_STARTS + " TEXT NOT NULL, "
            + Contract.Excluded_Time.COLUMN_ENDS + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + Contract.Excluded_Time.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    public static final String CREATE_SCHEDULED_TIME_BACKUP_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Scheduled_Time_Backup.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_FIELD + " INTEGER NOT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE + " INTEGER NOT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_TIME + " INTEGER NOT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST + " INTEGER NOT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE + " INTEGER DEFAULT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER + " INTEGER DEFAULT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES + " INTEGER DEFAULT NULL, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_CONTROLLED_PORTION + " INTEGER DEFAULT -1, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_WRONGNESS_LEVEL + " INTEGER DEFAULT -1, "
            + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_RANK_LEVEL + " INTEGER DEFAULT -1, "
            + "FOREIGN KEY(" + Contract.Scheduled_Time_Backup.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE "
            + " );";
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*public static final String CREATE_FIELD_TABLE = "CREATE TABLE " + OldField.TABLE_NAME +
            " ( " + OldField._ID + " INTEGER PRIMARY KEY , "
            +  OldField.COLUMN_FIELD_NAME + " TEXT , " +
             OldField.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP );";*/
    public static final String DBV6_APPV32_CREATE_FIELD_TABLE = "CREATE TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME
            + " ( "
            + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + " TEXT PRIMARY KEY NOT NULL, "
            + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE + " INTEGER NOT NULL DEFAULT 0 , "
            + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";

    /*public static final String CREATE_FIELD_TABLE = "CREATE TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME
            + " ( "
            + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + " TEXT PRIMARY KEY, "
            + Contract.Old_COLUMN_NAME_CREATED_AT
            _DBV6_AppV32 + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";*/

    public static final String DBV6_APPV32_CREATE_QUESTION_TABLE = "CREATE TABLE " + Old_Question_DBV6_AppV32.TABLE_NAME
            + " ( " + Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY
            + " TEXT PRIMARY KEY NOT NULL, "
            + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";

    public static final String DBV6_APPV32_CREATE_ANSWER_TABLE = "CREATE TABLE " + Old_Answer_DBV6_AppV32.TABLE_NAME
            + " ( " + Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY
            + " TEXT PRIMARY KEY NOT NULL, "
            + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')) );";

    public static final String DBV6_APPV32_CREATE_LINKER_TABLE = "CREATE TABLE " + Old_Linker_DBV6_AppV32.TABLE_NAME
            + " ( " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " TEXT NOT NULL, "
            + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " TEXT NOT NULL, "
            + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " TEXT NOT NULL, "
            + Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER + " INTEGER NOT NULL DEFAULT -1 , "
            + Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " INTEGER NOT NULL DEFAULT -1 , "
            + Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS + " TEXT NOT NULL DEFAULT '' , "
            + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'LOCALTIME')), "
            + " FOREIGN KEY " + " (" + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + ")" + " REFERENCES "
            + Old_Question_DBV6_AppV32.TABLE_NAME + "(" + Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " FOREIGN KEY " + " (" + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ")" + " REFERENCES "
            + Old_Answer_DBV6_AppV32.TABLE_NAME + "(" + Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " FOREIGN KEY " + " (" + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ")" + " REFERENCES "
            + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + "(" + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
            + " PRIMARY KEY ( " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + ","
            + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ","
            + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ")" + ");";


    public static final String DBV6_APPV32_CREATE_STUDY_DURING_TEMP_TABLE = "CREATE TABLE " + Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME
            + " ( " + Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_QUESTION + " TEXT NOT NULL, "
            + Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_ANSWER + " TEXT NOT NULL, "
            + Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD + " TEXT NOT NULL );";

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        app_database_path = context.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
    }
    @Override
    public void onCreate( SQLiteDatabase sqLiteDatabase ) {
        /*boolean dbExist = checkDataBase();
        if(dbExist) {
            Log.d(TAG, "Database exists.");
            deleteDb();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }*/
        sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);//
        sqLiteDatabase.execSQL(CREATE_STRING_TABLE);//
        sqLiteDatabase.execSQL(CREATE_NOT_STRING_TABLE);//
        sqLiteDatabase.execSQL(CREATE_STRING_ENTRY_TABLE);//
        sqLiteDatabase.execSQL(CREATE_NOT_STRING_ENTRY_TABLE);//
        sqLiteDatabase.execSQL(CREATE_TEST_WRONG_ANSWER_STRING_TABLE);
        sqLiteDatabase.execSQL(CREATE_TEST_WRONG_ANSWER_NOT_STRING_TABLE);
        sqLiteDatabase.execSQL(CREATE_TEST_PAUSED_TABLE);
        sqLiteDatabase.execSQL(CREATE_STUDY_PERIOD_REPEATED_ENTRY_TABLE);
        sqLiteDatabase.execSQL(CREATE_STUDY_PERIOD_PAUSED_TABLE);
        sqLiteDatabase.execSQL(CREATE_ALTERNATIVE_ANSWER_STRING_TABLE);//
        sqLiteDatabase.execSQL(CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE);//
        sqLiteDatabase.execSQL(CREATE_EXCLUDED_TIME_TABLE);
        sqLiteDatabase.execSQL(CREATE_SCHEDULED_TIME_BACKUP_TABLE);
        Log.d("from APP to Ahmed", "table have been written");
    }
    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(app_database_path);
            checkDB = file.exists();
        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }
    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(app_database_path);
        InputStream is = context.getAssets().open("sqlite" + File.separator + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }
    public void deleteDb() {
        File file = new File(app_database_path);
        if(file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("from Peninbin", "onUpgrade");
        if ( oldVersion < 2 ) {
            fieldTableMigration(sqLiteDatabase);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_QUESTION_TABLE);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_ANSWER_TABLE);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_LINKER_TABLE);
            questionMigration(sqLiteDatabase);
            answerMigration(sqLiteDatabase);
            linkerMigration(sqLiteDatabase);
            tablesDeletion(sqLiteDatabase);
        }
        if ( oldVersion < 3 ) {
            onUpgradeForFieldTableV3(sqLiteDatabase);
            onUpgradeForQuestionTableV3(sqLiteDatabase);
            onUpgradeForAnswerTableV3(sqLiteDatabase);
            onUpgradeForLinkerTableV3(sqLiteDatabase);
        }
        if ( oldVersion < 4 ) {
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_STUDY_DURING_TEMP_TABLE);
        }
        if ( oldVersion < 5 ) {
            onUpgradeForFieldTableV5(sqLiteDatabase);
            onUpgradeForLinkerTableV5(sqLiteDatabase);
            migrateDataFromStudyDuringTempTableToLinkerStudyDuringFlagColumnV5(sqLiteDatabase);
        }
        if ( oldVersion < 6 ) {
            onUpgradeForFieldTableV6(sqLiteDatabase);
        }
        if ( oldVersion < 7 ) {
            sqLiteDatabase.execSQL(CREATE_STRING_TABLE);
            sqLiteDatabase.execSQL(CREATE_NOT_STRING_TABLE);
            sqLiteDatabase.execSQL(CREATE_STRING_ENTRY_TABLE);
            sqLiteDatabase.execSQL(CREATE_NOT_STRING_ENTRY_TABLE);
            sqLiteDatabase.execSQL(CREATE_TEST_WRONG_ANSWER_STRING_TABLE);
            sqLiteDatabase.execSQL(CREATE_TEST_WRONG_ANSWER_NOT_STRING_TABLE);
            sqLiteDatabase.execSQL(CREATE_TEST_PAUSED_TABLE);
            sqLiteDatabase.execSQL(CREATE_STUDY_PERIOD_REPEATED_ENTRY_TABLE);
            sqLiteDatabase.execSQL(CREATE_STUDY_PERIOD_PAUSED_TABLE);
            sqLiteDatabase.execSQL(CREATE_ALTERNATIVE_ANSWER_STRING_TABLE);
            sqLiteDatabase.execSQL(CREATE_ALTERNATIVE_ANSWER_NOT_STRING_TABLE);
            onUpgradeDatabaseToDBV7AppV33(sqLiteDatabase);
        }
        if ( oldVersion < 8 ) {
            onUpgradeDatabaseToDBV8AppV38(sqLiteDatabase);
        }
        if ( oldVersion < 9 ) {
            onUpgradeDatabaseToDBV9AppV40(sqLiteDatabase);
        }
    }
    


    public Cursor getFieldList() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String [] string = {"rowid _id", OldField.COLUMN_FIELD_NAME};
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string,null,null,null,null,null);
        return cursor;
    }
    public Cursor getFieldList( SQLiteDatabase sqLiteDatabase ) {
        String [] string = {"rowid _id", OldField.COLUMN_FIELD_NAME};
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string,null,null,null,null,null);
        return cursor;
    }

    public void CreateNewField( String fieldName ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String tableQuestion = "CREATE TABLE " + fieldName + OldField.TABLE_QUESTION_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_QUESTION_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + OldField.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableAnswer = "CREATE TABLE " + fieldName + OldField.TABLE_ANSWER_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_ANSWER_SUFFIX + " "
                + "TEXT PRIMARY KEY NOT NULL, "
                + OldField.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        String tableLink = "CREATE TABLE " + fieldName + OldField.TABLE_LINK_SUFFIX + " "
                + " ( " + OldField._ID + " INTEGER , " + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " "
                + " TEXT " + ","
                + " " + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " "
                + " TEXT " + ", "
                + OldField.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP ,"
                +  " FOREIGN KEY " + " (" + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + OldField.TABLE_QUESTION_SUFFIX +
                "(" + fieldName + OldField.COLUMN_QUESTION_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                +  " FOREIGN KEY " + " (" + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + ")" + " REFERENCES "
                + fieldName + OldField.TABLE_ANSWER_SUFFIX +
                "(" + fieldName + OldField.COLUMN_ANSWER_SUFFIX + ") ON DELETE CASCADE ON UPDATE CASCADE" + ","
                + " PRIMARY KEY ( " + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX +
                "," + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + ")"
                + ");";
        sqLiteDatabase.execSQL(tableQuestion);
        sqLiteDatabase.execSQL(tableAnswer);
        sqLiteDatabase.execSQL(tableLink);
        ContentValues contentValues = new ContentValues();
        contentValues.put(OldField.COLUMN_FIELD_NAME, fieldName);
        sqLiteDatabase.insert(OldField.TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
        Log.d("from APP to Ahmed", "an field has been created");
    }
    public boolean checkFieldDuplication(String fieldName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String [] string = {"rowid _id", OldField.COLUMN_FIELD_NAME};
        String selectionArgs = OldField.COLUMN_FIELD_NAME + " = ? ";
        Cursor cursor = sqLiteDatabase.query(OldField.TABLE_NAME, string, selectionArgs, new String [] {fieldName}, null, null, null);
        boolean temp = cursor.getCount() == 0 ? false : true;
        sqLiteDatabase.close();
        return temp;
    }
    public void insertQuestion ( String fieldName, String question ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_QUESTION_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_QUESTION_SUFFIX},
                fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ? ",
                new String[] { question },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_QUESTION_SUFFIX,question);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_QUESTION_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertAnswer ( String fieldName, String answer ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_ANSWER_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_ANSWER_SUFFIX},
                fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ? ",
                new String[] { answer },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_ANSWER_SUFFIX,answer);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_ANSWER_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertQuestionAnswerLink ( String fieldName, String question, String answer ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_LINK_SUFFIX,
                new String[] { fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX,fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX},
                fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " = ? AND " + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " = ?",
                new String[] { question, answer },
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX, question);
            contentValues.put(fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX, answer);
            sqLiteDatabase.insert(fieldName + OldField.TABLE_LINK_SUFFIX, null, contentValues);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    public void insertFieldEntry(String fieldName, String question, String answer) {
        insertQuestion(fieldName, question);
        insertAnswer(fieldName, answer);
        insertQuestionAnswerLink(fieldName, question, answer);
    }
    public void deleteField(String fieldName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String linkTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_LINK_SUFFIX + ";";
        String questionTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_QUESTION_SUFFIX + ";";
        String answerTable = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_ANSWER_SUFFIX + ";";
        sqLiteDatabase.execSQL(linkTable);
        sqLiteDatabase.execSQL(questionTable);
        sqLiteDatabase.execSQL(answerTable);
        sqLiteDatabase.delete(OldField.TABLE_NAME, OldField.COLUMN_FIELD_NAME + " = '" + fieldName + "';", null);
        sqLiteDatabase.close();
        Log.d("from APP to Ahmed", "an field has been deleted");
    }

    public Cursor getFieldEntries(String fieldName) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + fieldName + OldField.TABLE_LINK_SUFFIX + "._id As" +
                " _id, " + fieldName + OldField.COLUMN_QUESTION_SUFFIX + "," +
                fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX +
                " From " + fieldName + OldField.TABLE_QUESTION_SUFFIX +
                " INNER JOIN " + fieldName + OldField.TABLE_LINK_SUFFIX + " ON " +
                fieldName + OldField.COLUMN_QUESTION_SUFFIX +
                " = " + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + "", null);
        return cursor;
    }
    private void editQuestion( String fieldName, String currentQuestion, String newQuestion ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_QUESTION_SUFFIX,
                new String[]{fieldName + OldField.COLUMN_QUESTION_SUFFIX},
                fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ?",
                new String[]{newQuestion},
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_QUESTION_SUFFIX, newQuestion);
            sqLiteDatabase.update(fieldName + OldField.TABLE_QUESTION_SUFFIX, contentValues,
                    fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ?", new String[]{currentQuestion});
        }
        else {
            sqLiteDatabase.delete(fieldName + OldField.TABLE_QUESTION_SUFFIX, fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ?",
                    new String[]{currentQuestion});
        }
        cursor.close();
        sqLiteDatabase.close();
    }
    private void editAnswer( String fieldName, String currentAnswer, String newAnswer ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_ANSWER_SUFFIX,
                new String[]{fieldName + OldField.COLUMN_ANSWER_SUFFIX},
                fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ?",
                new String[]{newAnswer},
                null,
                null,
                null);
        if ( cursor.getCount() <= 0 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(fieldName + OldField.COLUMN_ANSWER_SUFFIX, newAnswer);
            sqLiteDatabase.update(fieldName + OldField.TABLE_ANSWER_SUFFIX, contentValues,
                    fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ?", new String[]{currentAnswer});
        }
        else {
            sqLiteDatabase.delete(fieldName + OldField.TABLE_ANSWER_SUFFIX,fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ?",
                    new String[]{currentAnswer});
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    public void editFieldEntry(String fieldName, String currentQuestion, String newQuestion, String currentAnswer, String newAnswer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        if ( !currentQuestion.equals(newQuestion) ) {
            Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_LINK_SUFFIX,
                    new String[]{fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX},
                    fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " = ?",
                    new String[]{currentQuestion},
                    null,
                    null,
                    null);
            if (cursor.getCount() > 1) {
                insertQuestion(fieldName, newQuestion);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editQuestion(fieldName,currentQuestion,newQuestion);
            }
            cursor.close();
        }
        if ( !currentAnswer.equals(newAnswer) ) {
            Cursor cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_LINK_SUFFIX,
                    new String[]{ fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX},
                    fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " = ?",
                    new String[]{currentAnswer},
                    null,
                    null,
                    null);
            if (cursor.getCount() > 1) {
                insertQuestion(fieldName, newAnswer);
                deleteOnlyFieldEntry(fieldName,currentQuestion,currentAnswer);
            } else {
                editAnswer(fieldName,currentAnswer,newAnswer);
            }
            cursor.close();
        }
        if ( !currentQuestion.equals(newQuestion) && !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName, newQuestion, newAnswer);
        }
        else if ( !currentQuestion.equals(newQuestion) ) {
            insertQuestionAnswerLink(fieldName,newQuestion,currentAnswer);
        }
        else if ( !currentAnswer.equals(newAnswer) ) {
            insertQuestionAnswerLink(fieldName,currentQuestion,newAnswer);
        }
        sqLiteDatabase.close();
    }


    public void deleteOnlyFieldEntry(String fieldName, String question, String answer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " = ? " +  " AND " +
                fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " = ? " ;
        sqLiteDatabase.delete(fieldName + OldField.TABLE_LINK_SUFFIX, s, new String[]{question, answer});
        sqLiteDatabase.close();
    }
    public void deleteThisFieldQuestion(String fieldName, String question) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_QUESTION_SUFFIX, s, new String[]{question});
        sqLiteDatabase.close();
    }
    public void deleteThisFieldAnswer(String fieldName, String answer) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_ANSWER_SUFFIX,s,new String[] {answer});
        sqLiteDatabase.close();
    }
    public void deleteThisEntryAndItsRoots(String fieldName, String question, String answer ) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String s = fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_QUESTION_SUFFIX,s,new String[] {question});
        s = fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = ?";
        sqLiteDatabase.delete(fieldName + OldField.TABLE_ANSWER_SUFFIX,s,new String[] {answer});
        sqLiteDatabase.close();
    }
    public Cursor searchFor ( String fieldName, String searchString ) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String [] columns = { "rowid _id" , fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX, fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX};
        String selections = fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " LIKE ? "  + " OR " +
                fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " LIKE ?";
        String [] selectionArgs = {searchString + "%",searchString + "%"};
        return sqLiteDatabase.query(fieldName + OldField.TABLE_LINK_SUFFIX,columns,selections,selectionArgs ,null,null,null);
    }
    private void questionMigration ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_QUESTION_SUFFIX,
                    new String[] { fieldName + OldField.COLUMN_QUESTION_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                String s = cursor.getString(0);
                boolean equals = false;
                for ( String question : mainArrayList ) {
                    if ( question.equals(s) ){
                        equals = true;
                        break;
                    }
                }
                if ( !equals ) {
                    mainArrayList.add(s);
                    s = s.replace("'","''");
                    String statement = "INSERT INTO " + Old_Question_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY
                            + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                            + "SELECT " + fieldName + OldField.COLUMN_QUESTION_SUFFIX + "," + OldField.COLUMN_CREATED_AT
                            + " FROM " + fieldName + OldField.TABLE_QUESTION_SUFFIX
                            + " WHERE " + fieldName + OldField.COLUMN_QUESTION_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "questions has been transfered");
/*        ContentValues contentValues = new ContentValues();
        for ( String question : mainArrayList ) {
            contentValues.put(Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY,question);
        }
        sqLiteDatabase.insert(Old_Question_DBV6_AppV32.TABLE_NAME,null,contentValues);*/
    }
    private void answerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        cursor.close();
        ArrayList<String> mainArrayList = new ArrayList<String>();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query(fieldName + OldField.TABLE_ANSWER_SUFFIX, new String[] { fieldName + OldField.COLUMN_ANSWER_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                String s = cursor.getString(0);
                boolean equals = false;
                for ( String answer : mainArrayList ) {
                    if ( answer.equals(s) ){
                        equals = true;
                        break;
                    }
                }
                if ( !equals ) {
                    mainArrayList.add(s);
                    s = s.replace("'","''");
                    String statement = "INSERT INTO " + Old_Answer_DBV6_AppV32.TABLE_NAME + " ( " + Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY
                            + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                            + "SELECT " + fieldName + OldField.COLUMN_ANSWER_SUFFIX + "," + OldField.COLUMN_CREATED_AT
                            + " FROM " + fieldName + OldField.TABLE_ANSWER_SUFFIX
                            + " WHERE " + fieldName + OldField.COLUMN_ANSWER_SUFFIX + " = " + "'" + s + "'" + ";";
                    sqLiteDatabase.execSQL(statement);
//                    mainArrayList.add(s);
                }
            }
            cursor.close();
        }
        Log.d("From Pen in Bin", "answers has been transfered");
        /*ContentValues contentValues = new ContentValues();
        for ( String answer : mainArrayList ) {
            contentValues.put(Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY,answer);
        }
        sqLiteDatabase.insert(Old_Answer_DBV6_AppV32.TABLE_NAME,null,contentValues);*/
    }
    private void linkerMigration(SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        String question, answer;
        cursor.close();
        for ( String fieldName : fieldList ) {
            cursor = sqLiteDatabase.query( fieldName + OldField.TABLE_LINK_SUFFIX,
                    new String[] {fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX, fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX},
                    null,null,null,null,null);
            for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
                question = cursor.getString(0);
                answer = cursor.getString(1);
                question = question.replace("'", "''");
                answer = answer.replace("'", "''");
                String statement = "INSERT INTO " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                        + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                        + "SELECT " + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + ","
                        + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + "," + OldField.COLUMN_CREATED_AT
                        + " FROM " + fieldName + OldField.TABLE_LINK_SUFFIX
                        + " WHERE " + fieldName + OldField.COLUMN_QUESTION_LINK_SUFFIX + " = " + "'" + question + "'" + " AND "
                        + fieldName + OldField.COLUMN_ANSWER_LINK_SUFFIX + " = " + "'" + answer + "'" + ";";
                sqLiteDatabase.execSQL(statement);
                /*ContentValues contentValues = new ContentValues();
                contentValues.put(Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK,fieldName);
                int rows = 0;
                int r = 0;
                Cursor c = sqLiteDatabase.query(Old_Linker_DBV6_AppV32.TABLE_NAME,null,Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL",
                        new String[] {question,answer},null,null,null);
                r = c.getCount();
              *//*  rows = sqLiteDatabase.update(Old_Linker_DBV6_AppV32.TABLE_NAME,contentValues, Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                        + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL",
                        new String[] {question,answer});*//*
                rows = sqLiteDatabase.update(Old_Linker_DBV6_AppV32.TABLE_NAME,contentValues, Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                                + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                        new String[] {question,answer,""});*/
                String update = "UPDATE " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " SET " + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = " + "'" + fieldName + "'"
                        + " WHERE " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " = " + "'" + question + "'" + " AND "
                        + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = " + "'" + answer + "'" + " AND "
                        + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " IS NULL;";
                sqLiteDatabase.execSQL(update);
            }
        }
        Log.d("From Pen in Bin", "links has been transfered");
    }
    private void fieldTableMigration(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable" +
                " ( "
                +  OldField.COLUMN_FIELD_NAME + " TEXT , " +
                OldField.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + OldField.COLUMN_FIELD_NAME
                + "," + OldField.COLUMN_CREATED_AT + " ) "
                + "SELECT " + OldField.COLUMN_FIELD_NAME + ","
                + OldField.COLUMN_CREATED_AT
                + " FROM " + OldField.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + OldField.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_FIELD_TABLE);
        copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + OldField.COLUMN_FIELD_NAME + ","
                + OldField.COLUMN_CREATED_AT
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void tablesDeletion ( SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = getFieldList(sqLiteDatabase);
        ArrayList<String> fieldList = new ArrayList<String>(cursor.getCount());
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            fieldList.add(cursor.getString(1));
        }
        for ( String fieldName: fieldList ) {
            String dropLink = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_LINK_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropLink);
            String dropAnswer = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_ANSWER_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropAnswer);
            String dropQuestion = "DROP TABLE IF EXISTS " + fieldName + OldField.TABLE_QUESTION_SUFFIX + ";";
            sqLiteDatabase.execSQL(dropQuestion);
        }
        Log.d("from peninbin", "tables deleted");
    }
    private void onUpgradeForFieldTableV3(SQLiteDatabase sqLiteDatabase) {
        String tempTable = "CREATE TABLE " + "tempTable"
                + " ( "
                + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + " TEXT PRIMARY KEY, "
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " DATETIME DEFAULT CURRENT_TIMESTAMP );";
        sqLiteDatabase.execSQL(tempTable);
        String copy =  "INSERT INTO " + "tempTable" + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_FIELD_TABLE);
        copy =  "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForQuestionTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Old_Question_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_QUESTION_TABLE);
        String copy =  "INSERT INTO " + Old_Question_DBV6_AppV32.TABLE_NAME + " ( " + Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForAnswerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Old_Answer_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_ANSWER_TABLE);
        String copy =  "INSERT INTO " + Old_Answer_DBV6_AppV32.TABLE_NAME + " ( " + Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForLinkerTableV3(SQLiteDatabase sqLiteDatabase) {
        String rename = "ALTER TABLE " + Old_Linker_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_LINKER_TABLE);
        String copy =  "INSERT INTO " + Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ","
                + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + ","
                + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + ","
                + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                +  ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForFieldTableV5 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_FIELD_TABLE);
        String copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForLinkerTableV5 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Linker_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_LINKER_TABLE);
        String copy = "INSERT INTO " + Old_Linker_DBV6_AppV32.TABLE_NAME + " ( " + Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + " , " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                + " , " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK
                + "," + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK
                + "," + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }
/*    private void migrateDataFromStudyDuringTempTableToLinkerStudyDuringFlagColumnV5 ( SQLiteDatabase sqLiteDatabase ) {
        Cursor studyDuringTempCursor = sqLiteDatabase.query(Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME,
                new String[] {Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_QUESTION,
                        Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_ANSWER,
                        Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD},
                null,null,null,null,null);
        for ( studyDuringTempCursor.moveToFirst(); studyDuringTempCursor.getPosition() < studyDuringTempCursor.getCount();
              studyDuringTempCursor.moveToNext() ) {
            String question = studyDuringTempCursor.getString(0);
            String answer = studyDuringTempCursor.getString(1);
            String fieldName = studyDuringTempCursor.getString(2);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,studyDuringTempCursor.getPosition());
            sqLiteDatabase.update(Old_Linker_DBV6_AppV32.TABLE_NAME, contentValues,
                    Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " = ? AND " + Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND "
                            + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                    new String[]{question, answer, fieldName});
        }
        String drop = "DROP TABLE IF EXISTS " + Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
    }*/
    public void migrateDataFromStudyDuringTempTableToLinkerStudyDuringFlagColumnV5 ( SQLiteDatabase sqLiteDatabase ) {
        Cursor fieldCursor = sqLiteDatabase.query(Contract.Old_Field_DBV6_AppV32.TABLE_NAME,
                new String[]{Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME},
                null, null, null, null, null);
        for ( fieldCursor.moveToFirst(); fieldCursor.getPosition() < fieldCursor.getCount();
              fieldCursor.moveToNext() ){
            Cursor studyDuringTempCursor = sqLiteDatabase.query(Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME,
                    new String[] {Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_QUESTION,
                            Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_ANSWER,
                            Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD},
                    Contract.Old_Study_During_Temp_DBV6_AppV32.COLUMN_FIELD + " = ? ",
                    new String[] {fieldCursor.getString(0)},
                    null,null,null);
            for ( studyDuringTempCursor.moveToFirst(); studyDuringTempCursor.getPosition() < studyDuringTempCursor.getCount();
                  studyDuringTempCursor.moveToNext() ) {
                String question = studyDuringTempCursor.getString(0);
                String answer = studyDuringTempCursor.getString(1);
                String fieldName = studyDuringTempCursor.getString(2);
                ContentValues contentValues = new ContentValues();
                contentValues.put(Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,studyDuringTempCursor.getPosition());
                sqLiteDatabase.update(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, contentValues,
                        Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK + " = ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK + " = ? AND "
                                + Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                        new String[]{question, answer, fieldName});
            }
        }
        String drop = "DROP TABLE IF EXISTS " + Contract.Old_Study_During_Temp_DBV6_AppV32.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(drop);
    }
    private void onUpgradeForFieldTableV6 ( SQLiteDatabase sqLiteDatabase ) {
        String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
        sqLiteDatabase.execSQL(rename);
        sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_FIELD_TABLE);
        String copy = "INSERT INTO " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " ( " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + " , " + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32 + " ) "
                + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                + " FROM " + "tempTable"
                + ";";
        sqLiteDatabase.execSQL(copy);
        String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
        sqLiteDatabase.execSQL(drop);
    }

    private void onUpgradeDatabaseToDBV7AppV33( SQLiteDatabase sqLiteDatabase ) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Old_Field_DBV6_AppV32.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Field.TABLE_NAME + " ( " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_FIELD_TYPE
                    + " , " + Contract.CREATION_DATE + " ) "
                    + "SELECT " + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_NAME
                    + "," + Contract.Old_Field_DBV6_AppV32.COLUMN_FIELD_TYPE
                    + "," + Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);  // At this point the data of the old field table has been copied to the new field table
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            /////////////////////////////
            Cursor questionCursor = sqLiteDatabase.query(Old_Question_DBV6_AppV32.TABLE_NAME,
                    new String[]{Old_Question_DBV6_AppV32.COLUMN_QUESTION_ENTRY},
                    null,null,null,null,null);
            for ( questionCursor.moveToFirst(); questionCursor.getPosition() < questionCursor.getCount();
                  questionCursor.moveToNext() ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(STring.COLUMN_VALUE,questionCursor.getString(0));
                sqLiteDatabase.insert(STring.TABLE_NAME,null,contentValues);
            }
            questionCursor.close();
            Cursor answerCursor = sqLiteDatabase.query(Old_Answer_DBV6_AppV32.TABLE_NAME,
                    new String[]{Old_Answer_DBV6_AppV32.COLUMN_ANSWER_ENTRY},
                    null,null,null,null,null);
            for ( answerCursor.moveToFirst(); answerCursor.getPosition() < answerCursor.getCount();
                  answerCursor.moveToNext() ) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(STring.COLUMN_VALUE,answerCursor.getString(0));
                sqLiteDatabase.insert(STring.TABLE_NAME,null,contentValues);
            }
            answerCursor.close();
            // at this point the needed data on the old question and old answer tables has been copied to the new STring table.
            drop = "DROP TABLE IF EXISTS " + Old_Question_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            drop = "DROP TABLE IF EXISTS " + Old_Answer_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            /////////////////////////////////
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Cursor linkerCursor = sqLiteDatabase.query(Old_Linker_DBV6_AppV32.TABLE_NAME,
                    new String[]{Old_Linker_DBV6_AppV32.COLUMN_QUESTION_LINK, Old_Linker_DBV6_AppV32.COLUMN_ANSWER_LINK,
                    Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK,Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER,Old_Linker_DBV6_AppV32.COLUMN_STUDY_DURING_ORDER,
                    Old_Linker_DBV6_AppV32.COLUMN_TEST_WRONG_ANSWERS,Contract.Old_COLUMN_NAME_CREATED_AT_DBV6_AppV32},
                    null,null,null,null,null,null);
            for ( linkerCursor.moveToFirst(); linkerCursor.getPosition() < linkerCursor.getCount();
                  linkerCursor.moveToNext() ) {
                String field = linkerCursor.getString(2);
                Cursor question_id_cursor = sqLiteDatabase.query(STring.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(0)},null,null,null,null);
                question_id_cursor.moveToFirst();
                int question_id = question_id_cursor.getInt(0);
                Cursor answer_id_cursor = sqLiteDatabase.query(STring.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        STring.COLUMN_VALUE + " = ? ",
                        new String[]{linkerCursor.getString(1)},null,null,null,null);
                answer_id_cursor.moveToFirst();
                int answer_id = answer_id_cursor.getInt(0);
                Cursor field_id_cursor = sqLiteDatabase.query(Field.TABLE_NAME,new String[] { Contract.COLUMN_ID},
                        Field.COLUMN_FIELD_NAME + " = ? ",
                        new String[]{field},null,null,null,null);
                field_id_cursor.moveToFirst();
                int field_id = field_id_cursor.getInt(0);
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(String_Entry.COLUMN_QUESTION,question_id);
                contentValues.put(String_Entry.COLUMN_ANSWER,answer_id);
                contentValues.put(String_Entry.COLUMN_FIELD,field_id);
                contentValues.put(String_Entry.COLUMN_PAUSED_TEST_ORDER,linkerCursor.getInt(3));
                contentValues.put(String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER,linkerCursor.getInt(4));
                contentValues.put(Contract.CREATION_DATE,linkerCursor.getString(6));
                long entry_id = sqLiteDatabase.insert(String_Entry.TABLE_NAME,null,contentValues);
                if ( linkerCursor.getString(5).length() > 0 ) {
                    StringTokenizer stringTokenizer = new StringTokenizer(linkerCursor.getString(5),"\t");
                    while ( stringTokenizer.hasMoreTokens() ) {
                        contentValues = new ContentValues();
                        contentValues.put(Test_Wrong_Answer_String.COLUMN_ENTRY,entry_id);
                        contentValues.put(Test_Wrong_Answer_String.COLUMN_WRONG_ANSWER,stringTokenizer.nextToken());
                        sqLiteDatabase.insert(Test_Wrong_Answer_String.TABLE_NAME,null,contentValues);
                    }
                }
                Cursor test_paused_cursor = sqLiteDatabase.query(Test_Paused.TABLE_NAME,new String[]{Test_Paused.COLUMN_FIELD},
                        Test_Paused.COLUMN_FIELD + " = ? ",
                        new String[]{field_id + ""},null,null,null);
                if ( test_paused_cursor.getCount() <= 0 ) {
                    Cursor cursor = sqLiteDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                            new String[]{"0", field},null,null,null);
                    if ( cursor.getCount() > 0 ) {
                        int currentQuestionCounter = sharedPreferences.getInt(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.CURRENT_QUESTION_COUNTER
                                + TestActivity.DASH + field,0);
                        int wrongAnswersCounter = sharedPreferences.getInt(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.NUMBER_OF_WRONG_ANSWERS
                                + TestActivity.DASH + field,0);
                        int permitedTriesNumber = sharedPreferences.getInt(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.PERMITED_TRIES_NUMBER
                                + TestActivity.DASH + field,0);
                        contentValues = new ContentValues();
                        contentValues.put(Test_Paused.COLUMN_FIELD,field_id);
                        contentValues.put(Test_Paused.COLUMN_CURRENT_QUESTION_COUNTER,currentQuestionCounter);
                        contentValues.put(Test_Paused.COLUMN_WRONG_ANSWERS_COUNT,wrongAnswersCounter);
                        contentValues.put(Test_Paused.COLUMN_TRIES_NUMBER,permitedTriesNumber);
                        sqLiteDatabase.insert(Test_Paused.TABLE_NAME,null,contentValues);
                        editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.CURRENT_QUESTION_COUNTER
                                + TestActivity.DASH + field);
                        editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.NUMBER_OF_WRONG_ANSWERS
                                + TestActivity.DASH + field);
                        editor.remove(TestActivity.ACTIVITY_NAME + TestActivity.DASH + TestActivity.PERMITED_TRIES_NUMBER
                                + TestActivity.DASH + field);
                    }
                    cursor.close();
                }
                Cursor study_period_paused_cursor = sqLiteDatabase.query(Study_Period_Paused.TABLE_NAME,new String[]{Study_Period_Paused.COLUMN_FIELD},
                        Study_Period_Paused.COLUMN_FIELD + " = ? ",
                        new String[]{field_id + ""},null,null,null);
                if ( study_period_paused_cursor.getCount() <= 0 ) {
                    Cursor cursor = sqLiteDatabase.query(Contract.Old_Linker_DBV6_AppV32.TABLE_NAME, new String[]{Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER},
                            Contract.Old_Linker_DBV6_AppV32.COLUMN_TEST_ORDER + " >= ? AND " + Contract.Old_Linker_DBV6_AppV32.COLUMN_FIELD_LINK + " = ? ",
                            new String[]{"0", field},null,null,null);
                    if ( cursor.getCount() > 0 ) {
                        String string = sharedPreferences.getString(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                + StudyDuringActivity.DASH + field, null);
                        if ( string != null ) {
                            StringTokenizer stringTokenizer = new StringTokenizer(string, "-");
                            while (stringTokenizer.hasMoreTokens()) {
                                contentValues = new ContentValues();
                                contentValues.put(Study_Period_Repeated_Entry.COLUMN_FIELD,field_id);
                                contentValues.put(Study_Period_Repeated_Entry.COLUMN_REPEATED_ENTRY,Integer.parseInt(stringTokenizer.nextToken()));
                                sqLiteDatabase.insert(Study_Period_Repeated_Entry.TABLE_NAME,null,contentValues);
                            }
                        }
                        int currentQuestionCounter = sharedPreferences.getInt(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                + StudyDuringActivity.DASH + field, 0);
                        int permitedTriesNumber = sharedPreferences.getInt(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.NUMBER_OF_TRIES + StudyDuringActivity.DASH + field, 0);
                        contentValues = new ContentValues();
                        contentValues.put(Study_Period_Paused.COLUMN_FIELD,field_id);
                        contentValues.put(Study_Period_Paused.COLUMN_CURRENT_QUESTION_COUNTER,currentQuestionCounter);
                        contentValues.put(Study_Period_Paused.COLUMN_TRIES_NUMBER,permitedTriesNumber);
                        sqLiteDatabase.insert(Study_Period_Paused.TABLE_NAME,null,contentValues);
                        editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.STRING_REPRESENTING_REPEATED_ENTRIES
                                + StudyDuringActivity.DASH + field);
                        editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.CURRENT_QUESTION_COUNTER
                                + StudyDuringActivity.DASH + field);
                        editor.remove(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.DASH +
                                StudyDuringActivity.NUMBER_OF_TRIES + StudyDuringActivity.DASH + field);
                    }
                    cursor.close();
                }
                question_id_cursor.close();
                answer_id_cursor.close();
                field_id_cursor.close();
                test_paused_cursor.close();
                study_period_paused_cursor.close();
            }
            editor.apply();
            linkerCursor.close();
            drop = "DROP TABLE IF EXISTS " + Old_Linker_DBV6_AppV32.TABLE_NAME + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    private void onUpgradeDatabaseToDBV8AppV38(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Field.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_FIELD_TYPE
                    + " , " + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Field.COLUMN_LOCALE
                    + " , " + Field.COLUMN_CHECK_TYPE
                    + " , " + Field.COLUMN_SORT_BY
                    + " , " + Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Field.COLUMN_READ_ANSWER
                    + " , " + Field.COLUMN_USAGE_COUNT
                    + " , " + Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_FIELD_TYPE
                    + " , " + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Field.COLUMN_LOCALE
                    + " , " + Field.COLUMN_CHECK_TYPE
                    + " , " + Field.COLUMN_SORT_BY
                    + " , " + Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Field.COLUMN_READ_ANSWER
                    + " , " + Field.COLUMN_USAGE_COUNT
                    + " , " + Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.String_Entry.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(CREATE_STRING_ENTRY_TABLE);
            copy = "INSERT INTO " + String_Entry.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + String_Entry.COLUMN_QUESTION
                    + " , " + String_Entry.COLUMN_ANSWER
                    + " , " + String_Entry.COLUMN_FIELD
                    + " , " + String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + String_Entry.COLUMN_ASKED_COUNT
                    + " , " + String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + String_Entry.COLUMN_RANK
                    + " , " + String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + String_Entry.COLUMN_QUESTION
                    + " , " + String_Entry.COLUMN_ANSWER
                    + " , " + String_Entry.COLUMN_FIELD
                    + " , " + String_Entry.COLUMN_PAUSED_TEST_ORDER
                    + " , " + String_Entry.COLUMN_PAUSED_STUDY_PERIOD_ORDER
                    + " , " + String_Entry.COLUMN_ASKED_COUNT
                    + " , " + String_Entry.COLUMN_WRONGLY_ANSWERED_COUNT
                    + " , " + String_Entry.COLUMN_RANK
                    + " , " + String_Entry.COLUMN_CREATION_EMULATED_DATE
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.execSQL(CREATE_EXCLUDED_TIME_TABLE);
            sqLiteDatabase.execSQL(CREATE_SCHEDULED_TIME_BACKUP_TABLE);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
    private void onUpgradeDatabaseToDBV9AppV40(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try {
            String rename = "ALTER TABLE " + Contract.Field.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(CREATE_FIELD_TABLE);
            String copy = "INSERT INTO " + Field.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_FIELD_TYPE
                    + " , " + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Field.COLUMN_LOCALE
                    + " , " + Field.COLUMN_CHECK_TYPE
                    + " , " + Field.COLUMN_SORT_BY
                    + " , " + Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Field.COLUMN_READ_ANSWER
                    + " , " + Field.COLUMN_USAGE_COUNT
                    + " , " + Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.CREATION_DATE
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Field.COLUMN_FIELD_NAME
                    + " , " + Field.COLUMN_FIELD_TYPE
                    + " , " + Field.COLUMN_ENTRY_QUESTION_COLUMN_DATA_TYPE
                    + " , " + Field.COLUMN_LOCALE
                    + " , " + Field.COLUMN_CHECK_TYPE
                    + " , " + Field.COLUMN_SORT_BY
                    + " , " + Field.COLUMN_LIST_TEXT_SIZE
                    + " , " + Field.COLUMN_QUESTION_AREA_SIZE
                    + " , " + Field.COLUMN_ANSWER_AREA_SIZE
                    + " , " + Field.COLUMN_TEST_TEXT_SIZE
                    + " , " + Field.COLUMN_ACTION_BAR_SHOWN
                    + " , " + Field.COLUMN_READ_ANSWER
                    + " , " + Field.COLUMN_USAGE_COUNT
                    + " , " + Field.COLUMN_CREATION_EMULATION_NUMBER
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TESTS
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_TRIES
                    + " , " + Field.COLUMN_RANDOM_QUICK_TEST_NUMBER_OF_QUESTIONS
                    + " , " + Contract.CREATION_DATE
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            String drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            rename = "ALTER TABLE " + Contract.Scheduled_Time_Backup.TABLE_NAME + " RENAME TO " + "tempTable";
            sqLiteDatabase.execSQL(rename);
            sqLiteDatabase.execSQL(CREATE_SCHEDULED_TIME_BACKUP_TABLE);
            copy = "INSERT INTO " + Contract.Scheduled_Time_Backup.TABLE_NAME + " ( " + Contract.COLUMN_ID
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_FIELD
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_TIME
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES
                    + " ) "
                    + "SELECT " + Contract.COLUMN_ID
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_FIELD
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_SESSION_TYPE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_TIME
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_PENDINGINTENT_REQUEST
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER_OR_PERCENTAGE
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_NUMBER
                    + " , " + Contract.Scheduled_Time_Backup.COLUMN_NOT_RANDOM_QUICK_TEST_TRIES
                    + " FROM " + "tempTable"
                    + ";";
            sqLiteDatabase.execSQL(copy);
            drop = "DROP TABLE IF EXISTS " + "tempTable" + ";";
            sqLiteDatabase.execSQL(drop);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
