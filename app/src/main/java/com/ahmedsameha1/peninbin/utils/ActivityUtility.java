package com.ahmedsameha1.peninbin.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.io.InputStream;
import java.security.SecureRandom;
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
        SecureRandom rnd = new SecureRandom();
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
        SecureRandom rnd = new SecureRandom();
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
        ArrayList<Object[]> controlled_arraylist = new ArrayList<>();
        for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
             fieldEntriesCursor.moveToNext()) {
            double entry_wrongness;
            if (fieldEntriesCursor.getInt(4) > 0) {
                entry_wrongness = (double) fieldEntriesCursor.getInt(5) / fieldEntriesCursor.getInt(4);
            } else {
                entry_wrongness = -1;
            }
            if ( rank > Constants.Rank.LOW && wrongness > 0 ) {
                if (fieldEntriesCursor.getInt(3) >= rank || entry_wrongness >= wrongness) {
                    controlled_arraylist.add(new Object[]{fieldEntriesCursor.getLong(0), fieldEntriesCursor.getString(1),
                            fieldEntriesCursor.getString(2), entry_wrongness, rank, fieldEntriesCursor.getInt(6)});
                    controlled_counter++;
                } else {
                    uncontrolled[uncontrolled_counter][0] = fieldEntriesCursor.getString(1);
                    uncontrolled[uncontrolled_counter][1] = fieldEntriesCursor.getString(2);
                    uncontrolled_ids[uncontrolled_counter] = fieldEntriesCursor.getLong(0);
                    uncontrolled_whether_asked[uncontrolled_counter] = fieldEntriesCursor.getInt(6);
                    uncontrolled_counter++;
                }
            } else {
                if ((fieldEntriesCursor.getInt(3) >= rank && entry_wrongness >= wrongness)
                        || (fieldEntriesCursor.getInt(3) >= rank && wrongness <= 0)) {
                    controlled_arraylist.add(new Object[]{fieldEntriesCursor.getLong(0), fieldEntriesCursor.getString(1),
                            fieldEntriesCursor.getString(2), entry_wrongness, rank, fieldEntriesCursor.getInt(6)});
                    controlled_counter++;
                } else {
                    uncontrolled[uncontrolled_counter][0] = fieldEntriesCursor.getString(1);
                    uncontrolled[uncontrolled_counter][1] = fieldEntriesCursor.getString(2);
                    uncontrolled_ids[uncontrolled_counter] = fieldEntriesCursor.getLong(0);
                    uncontrolled_whether_asked[uncontrolled_counter] = fieldEntriesCursor.getInt(6);
                    uncontrolled_counter++;
                }
            }
        }
        if ( rank >= Constants.Rank.LOW && wrongness_postion > 0 ) {
            Collections.sort(controlled_arraylist, (element1, element2) -> {
                int element1_rank = (int) element1[4];
                double element1_wrongness = (double) element1[3];
                int element2_rank = (int) element2[4];
                double element2_wrongness = (double) element2[3];
                if (element1_wrongness > element2_wrongness) {
                    return 1;
                } else if (element1_wrongness < element2_wrongness) {
                    return -1;
                } else { // wrongness is the same
                    if (element1_rank > element2_rank) {
                        return 1;
                    } else if (element1_rank < element2_rank) {
                        return -1;
                    } else { // wrongness and rank are the same
                            return 0;
                    }
                }
            });
        } else if ( rank > Constants.Rank.LOW && wrongness_postion == 0 ) {
            Collections.sort(controlled_arraylist, (element1, element2) -> {
                int element1_rank = (int) element1[4];
                double element1_wrongness = (double) element1[3];
                int element2_rank = (int) element2[4];
                double element2_wrongness = (double) element2[3];
                if (element1_rank > element2_rank) {
                    return 1;
                } else if (element1_rank < element2_rank) {
                    return -1;
                } else { // rank is the same
                    if (element1_wrongness > element2_wrongness) {
                        return 1;
                    } else if (element1_wrongness < element2_wrongness) {
                        return -1;
                    } else { // wrongness and rank are the same
                            return 0;

                    }
                }
            });
        }

        ArrayList<Object> arrayList = new ArrayList();
        if (controlled_counter > 0) {
            arrayList.add(controlled_arraylist);
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

    public static String[][] prepareListForTest(Cursor fieldEntriesCursor, int numberOfQuestions, int controlled_portion,
                                          int rank_level, int wrongness_level, String date_string,
                                          CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase,
                                          long current_field_id, boolean isEmulated) {
        String[][] listForTest = new String[numberOfQuestions][2];
        long[] list_for_test_ids = new long[numberOfQuestions];
        String[][] list_of_entries_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        String[][] list_of_entries_did_not_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        long[] list_of_entries_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        long[] list_of_entries_did_not_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        if (wrongness_level == -1 && rank_level == -1 && controlled_portion == -1) {
            int asked = 0, didnot_asked = 0;
            for (fieldEntriesCursor.moveToFirst(); fieldEntriesCursor.getPosition() < fieldEntriesCursor.getCount();
                 fieldEntriesCursor.moveToNext()) {
                if (fieldEntriesCursor.getInt(6) == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                    list_of_entries_asked_current_round[asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_asked_current_round[asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_asked_current_round_ids[asked] = fieldEntriesCursor.getLong(0);
                    asked++;
                } else {
                    list_of_entries_did_not_asked_current_round[didnot_asked][0] = fieldEntriesCursor.getString(1);
                    list_of_entries_did_not_asked_current_round[didnot_asked][1] = fieldEntriesCursor.getString(2);
                    list_of_entries_did_not_asked_current_round_ids[didnot_asked] = fieldEntriesCursor.getLong(0);
                    didnot_asked++;
                }
            }
            list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
            list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
            list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
            list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
            if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            } else {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                }
                ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                    listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                    listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                    list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                }
                final long[] final_list_for_test_ids = list_for_test_ids;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                        cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                return listForTest;
            }
        } else {
            int number_of_controlled_questions;
            int number_of_uncontrolled_questions;
            double controlled_percentage;
            if (controlled_portion == 0) {
                controlled_percentage = 0.2;
            } else if (controlled_portion == 1) {
                controlled_percentage = 0.4;
            } else if (controlled_portion == 2) {
                controlled_percentage = 0.5;
            } else if (controlled_portion == 3) {
                controlled_percentage = 0.6;
            } else if (controlled_portion == 4) {
                controlled_percentage = 0.8;
            } else {
                controlled_percentage = 1;
            }
            number_of_controlled_questions = (int) (numberOfQuestions * controlled_percentage);
            number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
            ArrayList<Object> arrayList = ActivityUtility.prepareControlledAndUncontrolled(fieldEntriesCursor,rank_level, wrongness_level);
//            String[][] controlled_entries = (String[][]) arrayList.get(0);
            ArrayList<Object[]> controlled_arraylist = (ArrayList<Object[]>) arrayList.get(0);
            String[][] uncontrolled_entries = (String[][]) arrayList.get(1);
            long[] uncontrolled_entries_ids = (long[]) arrayList.get(2);
            int[] uncontrolled_entries_whether_asked = (int[]) arrayList.get(3);
            if (controlled_arraylist == null) {
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < listForTest.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                        list_for_test_ids[n] = list_of_entries_did_not_asked_current_round_ids[n];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    return listForTest;
                }
            } else if (uncontrolled_entries == null) {
                Collections.shuffle(controlled_arraylist);
                for (int n = 0; n < listForTest.length; n++) {
                    Object[] element = controlled_arraylist.get(n);
                    listForTest[n][0] = (String) element[1];
                    listForTest[n][1] = (String) element[2];
                /*    listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];*/
                }
                return listForTest;
            } else {
                if (number_of_controlled_questions > controlled_arraylist.size()) {
                    number_of_controlled_questions = controlled_arraylist.size();
                    number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
                } else if (number_of_uncontrolled_questions > uncontrolled_entries.length) {
                    number_of_uncontrolled_questions = uncontrolled_entries.length;
                    number_of_controlled_questions = numberOfQuestions - number_of_uncontrolled_questions;
                }
                Collections.shuffle(controlled_arraylist);
                for (int n = 0; n < number_of_controlled_questions; n++) {
                    Object[] element = controlled_arraylist.get(n);
                    listForTest[n][0] = (String) element[1];
                    listForTest[n][1] = (String) element[2];
                   /* listForTest[n][0] = controlled_entries[n][0];
                    listForTest[n][1] = controlled_entries[n][1];*/
                }
                int asked = 0, didnot_asked = 0;
                for (int n = 0; n < uncontrolled_entries.length; n++) {
                    if (uncontrolled_entries_whether_asked[n] == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_asked_current_round[asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_asked_current_round_ids[asked] = uncontrolled_entries_ids[n];
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = uncontrolled_entries[n][0];
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = uncontrolled_entries[n][1];
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = uncontrolled_entries_ids[n];
                        didnot_asked++;
                    }
                }
                for (int n = number_of_controlled_questions; n < controlled_arraylist.size(); n++) {
                    Object[] element = controlled_arraylist.get(n);
                    int whether_asked = (int) element[5];
                    long id = (long) element[0];
                    String question = (String) element[1];
                    String answer = (String) element[2];
                    if ( whether_asked == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
                        list_of_entries_asked_current_round[asked][0] = question;
                        list_of_entries_asked_current_round[asked][1] = answer;
                        list_of_entries_asked_current_round_ids[asked] = id;
                        asked++;
                    } else {
                        list_of_entries_did_not_asked_current_round[didnot_asked][0] = question;
                        list_of_entries_did_not_asked_current_round[didnot_asked][1] = answer;
                        list_of_entries_did_not_asked_current_round_ids[didnot_asked] = id;
                        didnot_asked++;
                    }
                }
                list_of_entries_asked_current_round = Arrays.copyOfRange(list_of_entries_asked_current_round, 0, asked);
                list_of_entries_did_not_asked_current_round = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round, 0, didnot_asked);
                list_of_entries_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_asked_current_round_ids, 0, asked);
                list_of_entries_did_not_asked_current_round_ids = Arrays.copyOfRange(list_of_entries_did_not_asked_current_round_ids, 0, didnot_asked);
                if (number_of_uncontrolled_questions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + number_of_uncontrolled_questions; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_did_not_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                    }
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        list_for_test_ids[i] = list_of_entries_asked_current_round_ids[i];
                    }
                    final long[] final_list_for_test_ids = list_for_test_ids;
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated, date_string);
                            cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(final_list_for_test_ids);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                }
                ActivityUtility.shuffleArray(listForTest);
                return listForTest;
            }
        }
    }

    public static Spannable spanText (String text) {
        int [] colors = {0xFF5A0000,0xFF0000FF,
                0xFFFFFF00,0xFF500850,0xFF00FF00,
                0xFFFF0000,0xFF4B0082};
        SecureRandom secureRandom = new SecureRandom();
        Spannable spanned_text = Spannable.Factory.getInstance().newSpannable(text);
        for ( int i = 0; i < text.length(); i++ ) {
            if ( !text.substring(i, i+1).matches("\\s")) {
                spanned_text.setSpan(new ForegroundColorSpan(colors[secureRandom.nextInt(6)]),
                        i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanned_text.setSpan(new StyleSpan(secureRandom.nextInt(4)),
                        i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanned_text;
    }

    public static String getTextFileFromRawDirectory(Context context, int resource){
        String text = null;
        try {
            InputStream is = context.getResources().openRawResource(resource);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            text = new String(buffer);
        } catch (Exception e) {
            Log.e("error", ""+e.toString());
        }
        return text;
    }
}
