package com.ahmedsameha1.peninbin.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

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
import com.ahmedsameha1.peninbin.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ahmed on 7/4/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private final static String TAG = "DatabaseHelper";
    public static final int DATABASE_VERSION = 10;
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
            + String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND + " INTEGER DEFAULT " + Constants.TestRound.ASKED_AT_CURRENT_ROUND + ", "
            + String_Entry.COLUMN_ORDER_OF_ENTRY + " INTEGER DEFAULT NULL, "
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
            + Not_String_Entry.COLUMN_REMIND_AT + " INTEGER DEFAULT NULL, "
            + Not_String_Entry.COLUMN_REMIND_AT_PENDINGINTENT_REQUEST + " INTEGER DEFAULT NULL, "
            + Not_String_Entry.COLUMN_WHETHER_ASKED_AT_CURRENT_TEST_ROUND + " INTEGER DEFAULT " + Constants.TestRound.ASKED_AT_CURRENT_ROUND + ", "
            + Not_String_Entry.COLUMN_ORDER_OF_ENTRY + " INTEGER DEFAULT NULL, "
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

    public static final String CREATE_FIELD_NOTE_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Field_Note.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Contract.Field_Note.COLUMN_FIELD + " INTEGER NOT NULL, "
            + Contract.Field_Note.COLUMN_NOTE + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + Contract.Field_Note.COLUMN_FIELD + ") REFERENCES " + Field.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + " UNIQUE ( " + Contract.Field_Note.COLUMN_FIELD + ", "
            + Contract.Field_Note.COLUMN_NOTE + " )"
            + " );";

    public static final String CREATE_FIELD_TYPE_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Field_Type.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + Contract.Field_Type.COLUMN_TYPE_NAME + " TEXT NOT NULL, "
            + " UNIQUE ( " + Contract.Field_Type.COLUMN_TYPE_NAME + " )"
            + " );";

    public static final String CREATE_SYMBOL_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Symbol.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Contract.Symbol.COLUMN_VALUE + " TEXT NOT NULL, "
            + " UNIQUE ( " + Contract.Symbol.COLUMN_VALUE + " )"
            + " );";

    public static final String CREATE_FIELD_TYPE_SYMBOL_TABLE = "CREATE TABLE IF NOT EXISTS " + Contract.Field_Type_Symbol.TABLE_NAME
            + " ( "
            + Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + " INTEGER NOT NULL, "
            + Contract.Field_Type_Symbol.COLUMN_SYMBOL + " INTEGER NOT NULL, "
            + Contract.Field_Type_Symbol.COLUMN_USER_CUSTOMIZED_STRING + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + ") REFERENCES " + Contract.Field_Type.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + "FOREIGN KEY(" + Contract.Field_Type_Symbol.COLUMN_SYMBOL + ") REFERENCES " + Contract.Symbol.TABLE_NAME + "(" + Contract.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, "
            + " UNIQUE ( " + Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE + ", "
            + Contract.Field_Type_Symbol.COLUMN_SYMBOL + " )"
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
        sqLiteDatabase.execSQL(CREATE_FIELD_NOTE_TABLE);
        sqLiteDatabase.execSQL(CREATE_FIELD_TYPE_TABLE);
        sqLiteDatabase.execSQL(CREATE_SYMBOL_TABLE);
        sqLiteDatabase.execSQL(CREATE_FIELD_TYPE_SYMBOL_TABLE);
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
            ModifyDatabaseSchema.fieldTableMigration(sqLiteDatabase);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_QUESTION_TABLE);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_ANSWER_TABLE);
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_LINKER_TABLE);
            ModifyDatabaseSchema.questionMigration(sqLiteDatabase);
            ModifyDatabaseSchema.answerMigration(sqLiteDatabase);
            ModifyDatabaseSchema.linkerMigration(sqLiteDatabase);
            ModifyDatabaseSchema.tablesDeletion(sqLiteDatabase);
        }
        if ( oldVersion < 3 ) {
            ModifyDatabaseSchema.onUpgradeForFieldTableV3(sqLiteDatabase);
            ModifyDatabaseSchema.onUpgradeForQuestionTableV3(sqLiteDatabase);
            ModifyDatabaseSchema.onUpgradeForAnswerTableV3(sqLiteDatabase);
            ModifyDatabaseSchema.onUpgradeForLinkerTableV3(sqLiteDatabase);
        }
        if ( oldVersion < 4 ) {
            sqLiteDatabase.execSQL(DBV6_APPV32_CREATE_STUDY_DURING_TEMP_TABLE);
        }
        if ( oldVersion < 5 ) {
            ModifyDatabaseSchema.onUpgradeForFieldTableV5(sqLiteDatabase);
            ModifyDatabaseSchema.onUpgradeForLinkerTableV5(sqLiteDatabase);
            ModifyDatabaseSchema.migrateDataFromStudyDuringTempTableToLinkerStudyDuringFlagColumnV5(sqLiteDatabase);
        }
        if ( oldVersion < 6 ) {
            ModifyDatabaseSchema.onUpgradeForFieldTableV6(sqLiteDatabase);
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
            ModifyDatabaseSchema.onUpgradeDatabaseToDBV7AppV33(context,sqLiteDatabase);
        }
        if ( oldVersion < 8 ) {
            ModifyDatabaseSchema.onUpgradeDatabaseToDBV8AppV38(sqLiteDatabase);
        }
        if ( oldVersion < 9 ) {
            ModifyDatabaseSchema.onUpgradeDatabaseToDBV9AppV40(sqLiteDatabase);
        }
        if ( oldVersion < 10 ) {
            ModifyDatabaseSchema.onUpgradeDatabaseToDBV10AppV43(sqLiteDatabase);
        }
    }
    














}
