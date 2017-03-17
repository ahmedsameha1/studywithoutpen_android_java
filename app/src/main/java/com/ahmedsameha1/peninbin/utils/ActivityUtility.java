package com.ahmedsameha1.peninbin.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by me on 07/11/16.
 */

public class ActivityUtility {

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
    public static boolean hasOpenedDialogs(FragmentActivity activity) {
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void closeOpenedDialogs(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    fragmentTransaction.remove(fragment);
                }
            }
        }
        fragmentTransaction.commit();
    }
    public enum FalseString {
        TIME_OUT, DONT_KNOW
    }

    public static void shuffleArray(String[][] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = array[index][0];
            String answer = array[index][1];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[i][0] = question;
            array[i][1] = answer;
        }
    }

    public static void shuffleArray(String[][] strings_array, long[] ids) {
        Random rnd = new Random();
        for (int i = strings_array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = strings_array[index][0];
            String answer = strings_array[index][1];
            long id = ids[index];
            strings_array[index][0] = strings_array[i][0];
            strings_array[index][1] = strings_array[i][1];
            ids[index] = ids[i];
            strings_array[i][0] = question;
            strings_array[i][1] = answer;
            ids[i] = id;
        }
    }

    public static ArrayList prepareControlledAndUncontrolled(Cursor fieldEntriesCursor,int rank, int wrongness_postion) {
        String[][] controlled = new String[fieldEntriesCursor.getCount()][2];
        String[][] uncontrolled = new String[fieldEntriesCursor.getCount()][2];
        long[] uncontrolled_ids = new long[fieldEntriesCursor.getCount()];
        int[] uncontrolled_whether_asked = new int[fieldEntriesCursor.getCount()];
        double wrongness;
        if (wrongness_postion == 0) {
            wrongness = 0;
        } else if (wrongness_postion == 1) {
            wrongness = 0.2;
        } else if (wrongness_postion == 2) {
            wrongness = 0.4;
        } else if (wrongness_postion == 3) {
            wrongness = 0.6;
        } else if (wrongness_postion == 4) {
            wrongness = 0.8;
        } else {
            wrongness = 1;
        }
        int controlled_counter = 0, uncontrolled_counter = 0;
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            double entry_wrongness;
            if (fieldEntriesCursor.getInt(4) > 0) {
                entry_wrongness = (double) fieldEntriesCursor.getInt(5) / fieldEntriesCursor.getInt(4);
            } else {
                entry_wrongness = -1;
            }
            if (fieldEntriesCursor.getInt(3) >= rank && entry_wrongness >= wrongness) {
                controlled[controlled_counter][0] = fieldEntriesCursor.getString(1);
                controlled[controlled_counter][1] = fieldEntriesCursor.getString(2);
                controlled_counter++;
            } else if (fieldEntriesCursor.getInt(3) >= rank && wrongness <= 0) {   // to include the not asked entries
                controlled[controlled_counter][0] = fieldEntriesCursor.getString(1);
                controlled[controlled_counter][1] = fieldEntriesCursor.getString(2);
                controlled_counter++;
            } else {
                uncontrolled[uncontrolled_counter][0] = fieldEntriesCursor.getString(1);
                uncontrolled[uncontrolled_counter][1] = fieldEntriesCursor.getString(2);
                uncontrolled_ids[uncontrolled_counter] = fieldEntriesCursor.getLong(0);
                uncontrolled_whether_asked[uncontrolled_counter] = fieldEntriesCursor.getInt(6);
                uncontrolled_counter++;
            }
        }
        ArrayList arrayList = new ArrayList<>();
        if (controlled_counter > 0) {
            arrayList.add(Arrays.copyOfRange(controlled, 0, controlled_counter));
        } else {
            arrayList.add(null);
        }
        if (uncontrolled_counter > 0) {
            arrayList.add(Arrays.copyOfRange(uncontrolled, 0, uncontrolled_counter));
            arrayList.add(Arrays.copyOfRange(uncontrolled_ids, 0, uncontrolled_counter));
            arrayList.add(Arrays.copyOfRange(uncontrolled_whether_asked, 0, uncontrolled_counter));
        } else {
            arrayList.add(null);
            arrayList.add(null);
            arrayList.add(null);
        }
        return arrayList;
    }
    public interface ActivityTimers {
        void manageTimersDueDialogsShow(boolean isCounting);
    }
}
