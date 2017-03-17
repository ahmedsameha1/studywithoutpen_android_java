package com.ahmedsameha1.peninbin.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by ahmed on 9/7/16.
 */
public class SpecialText {
    public static void addSpecialTextToDBV11(SQLiteDatabase sqLiteDatabase ) {// TODO: 3/11/17 must reflect the new field type numbers in the old databases 
        String [] special_text1 = { "[after n]","[after v]","[always + adv]","[always + prep]","[always + adv/prep]",
                "[as form of address]","[before n]","[C]","[I]","[+ infinitive without to]","[L]","[M]","[not continuous]",
                "[+ obj + adj]","[+ obj + n]","[+ obj + n/adj]","[+ obj + to infinitive]","[+ obj + infinitive without to]",
                "[+ obj + v-ing]","past participle","past simple","plural","present participle","[+ question word]","[R]","[S]",
                "[+ that]","[U]","[usually passive]","[usually pl]","[usually sing]","adjective","adverb","auxiliary verb",
                "comparative","conjunction","determiner","exclamation","group noun","modal verb","noun","name","phrasal verb",
                "plural noun","predeterminer","prefix","preposition","pronoun","short form","suffix","superlative","verb",
                "ABBREVIATION","APPROVING","AUS","CHILD'S WORD","DATED","DISAPPROVING","FEMALE","FIGURATIVE","FORMAL",
                "HUMOROUS","INFORMAL","IRISH ENGLISH","LEGAL","LITERARY","MALE","NORTHERN ENGLISH","NOT STANDARD","OFFENSIVE",
                "OLD USE","POLITE WORD/PHRASE","SAYING","SCOTTISH ENGLISH","SLANG","SPECIALIZED","TRADEMARK","UK","US"};
        for ( String temp: special_text1 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Symbol.COLUMN_VALUE, temp);
            long symbol_id = sqLiteDatabase.insert(Contract.Symbol.TABLE_NAME, null, contentValues);
            contentValues = new ContentValues();
            contentValues.put(Contract.Field_Type_Symbol.COLUMN_SYMBOL, symbol_id);
            contentValues.put(Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE, Constants.FieldTypes.ENGLISH_DICTIONARY_TYPE);
            sqLiteDatabase.insert(Contract.Field_Type_Symbol.TABLE_NAME,null, contentValues);
        }
        char [] chars = {1619, 1648, 1622, 1620, 1621, 1750, 1752, 1753, 1754, 1751, 1755, 1756, 1758};
        String [] special_text2 = { "َ","ً","ُ","ٌ","ِ",
                "ٍ","ّ","ـ" , "ْ", Character.valueOf(chars[0]).toString(),
                Character.valueOf(chars[1]).toString(),Character.valueOf(chars[2]).toString(),
                Character.valueOf(chars[3]).toString(),Character.valueOf(chars[4]).toString(),
                Character.valueOf(chars[5]).toString(),Character.valueOf(chars[6]).toString(),
                Character.valueOf(chars[7]).toString(),Character.valueOf(chars[8]).toString(),
                Character.valueOf(chars[9]).toString(),Character.valueOf(chars[10]).toString(),
                Character.valueOf(chars[11]).toString(),Character.valueOf(chars[12]).toString()};
        for ( String temp: special_text2 ) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.Symbol.COLUMN_VALUE, temp);
            long symbol_id = sqLiteDatabase.insert(Contract.Symbol.TABLE_NAME, null, contentValues);
            contentValues = new ContentValues();
            contentValues.put(Contract.Field_Type_Symbol.COLUMN_SYMBOL, symbol_id);
            contentValues.put(Contract.Field_Type_Symbol.COLUMN_FIELD_TYPE, Constants.FieldTypes.KORAN_TYPE);
            sqLiteDatabase.insert(Contract.Field_Type_Symbol.TABLE_NAME,null, contentValues);
        }
    }
}
