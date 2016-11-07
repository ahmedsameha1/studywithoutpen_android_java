package com.ahmedsameha1.peninbin.utils;

import android.content.Context;
import android.database.Cursor;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.ArrayList;

/**
 * Created by me on 07/11/16.
 */

public class MoreThanOneAnswer {

    public static ArrayList<String> getHints(Context context, long current_field_id, int question_data_type, String question, String answer) {
        DatabaseReadOperations databaseReadOperations = new DatabaseReadOperations(context);
        Cursor searchForCursor = databaseReadOperations.searchExactlyFor(current_field_id,
                question_data_type, question);
        if (searchForCursor.getCount() > 1) {
            ArrayList<String> hints = new ArrayList<>();
            ArrayList<String> answers = new ArrayList<>();
            for (searchForCursor.moveToFirst(); searchForCursor.getPosition() < searchForCursor.getCount();
                 searchForCursor.moveToNext()) {
                answers.add(searchForCursor.getString(0));
            }
            if ( isNumberOfLettersDifferent(answers) ) {
                hints.add(context.getString(R.string.number_of_letters)  + " " + Integer.toString(answer.length()));
            }
        } else {
            return null;
        }
    }

    private static boolean isNumberOfLettersDifferent(ArrayList<String> answers) {
        for ( String temp : answers ) {
            for ( String temp2 : answers ) {
                if ( !temp.equals(temp2) && temp.length() == temp2.length() ) {
                    return false;
                }
            }
        }
        return true;
    }
}
