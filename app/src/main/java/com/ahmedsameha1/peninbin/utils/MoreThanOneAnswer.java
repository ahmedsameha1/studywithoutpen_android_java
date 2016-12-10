package com.ahmedsameha1.peninbin.utils;

import android.content.Context;
import android.database.Cursor;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
            searchForCursor.close();
            if ( isNumberOfLettersDifferent(answers, answer.length()) ) {
                hints.add(context.getString(R.string.number_of_letters)  + " " + Integer.toString(answer.length()));
                return hints;
            }
            ArrayList<Integer> number_of_differences = new ArrayList<>(Collections.nCopies(answer.length(), 0));
            for ( int i = (answer.length() -1); i >= 0; i--) {
                for ( String temp : answers ) {
                    if ( !temp.equals(answer) && temp.length() > i ) {
                        if ( temp.charAt(i) != answer.charAt(i) ) {
                            number_of_differences.set(i,number_of_differences.get(i) + 1 );
                        }
                    } else if ( !temp.equals(answer) && temp.length() <= i ) {
                        number_of_differences.set(i,number_of_differences.get(i) + 1 );
                    }
                }
            }
            for (int i = answers.size() - 1; i > 0; i--) {
                if ( number_of_differences.contains(i) ) {
                    ArrayList<Integer> indices = new ArrayList<>(answer.length());
                    for ( int k = 0; k < number_of_differences.size(); k++ ) {
                        if ( number_of_differences.get(k) == i ) {
                            indices.add(k);
                        }
                    }
                    Collections.shuffle(indices);
                    for ( int l = 0; l < indices.size() && l < answers.size() - i; l++ ) {
                        hints.add(context.getString(R.string.letter_number) + " " + ( indices.get(l) + 1) + " : \"" +
                                answer.charAt(indices.get(l)) + "\"");
                    }
                    if ( i != answers.size() -1 ) {
                        hints.add(context.getString(R.string.number_of_letters)  + " " + Integer.toString(answer.length()));
                    }
                    break;
                }
            }
            return hints;
        } else {
            return null;
        }
    }

    private static boolean isNumberOfLettersDifferent(ArrayList<String> answers, int answer_length) {
        int counter = 0;
        for ( String temp : answers ) {
                if ( temp.length() == answer_length ) {
                    counter++;
                }
        }
        return counter == 1;
    }


}
