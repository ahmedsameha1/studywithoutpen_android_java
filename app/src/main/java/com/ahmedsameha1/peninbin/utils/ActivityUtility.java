package com.ahmedsameha1.peninbin.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.io.InputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by me on 07/11/16.
 */

public class ActivityUtility {
    public static final int ALL_CONTROLLED = -2;

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
            if (isNumberOfLettersDifferent(answers, answer.length())) {
                hints.add(context.getString(R.string.number_of_letters) + " " + Integer.toString(answer.length()));
                return hints;
            }
            ArrayList<Integer> number_of_differences = new ArrayList<>(Collections.nCopies(answer.length(), 0));
            for (int i = (answer.length() - 1); i >= 0; i--) {
                for (String temp : answers) {
                    if (!temp.equals(answer) && temp.length() > i) {
                        if (temp.charAt(i) != answer.charAt(i)) {
                            number_of_differences.set(i, number_of_differences.get(i) + 1);
                        }
                    } else if (!temp.equals(answer) && temp.length() <= i) {
                        number_of_differences.set(i, number_of_differences.get(i) + 1);
                    }
                }
            }
            for (int i = answers.size() - 1; i > 0; i--) {
                if (number_of_differences.contains(i)) {
                    ArrayList<Integer> indices = new ArrayList<>(answer.length());
                    for (int k = 0; k < number_of_differences.size(); k++) {
                        if (number_of_differences.get(k) == i) {
                            indices.add(k);
                        }
                    }
                    Collections.shuffle(indices);
                    for (int l = 0; l < indices.size() && l < answers.size() - i; l++) {
                        hints.add(context.getString(R.string.letter_number) + " " + (indices.get(l) + 1) + " : \"" +
                                answer.charAt(indices.get(l)) + "\"");
                    }
                    if (i != answers.size() - 1) {
                        hints.add(context.getString(R.string.number_of_letters) + " " + Integer.toString(answer.length()));
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
        for (String temp : answers) {
            if (temp.length() == answer_length) {
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
            String id = array[index][2];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[index][2] = array[i][2];
            array[i][0] = question;
            array[i][1] = answer;
            array[i][2] = id;
        }
    }

    public static void shuffleArray(String[][] array, int end_shuffle_index) {
        SecureRandom rnd = new SecureRandom();
        for (int i = end_shuffle_index; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String question = array[index][0];
            String answer = array[index][1];
            String id = array[index][2];
            array[index][0] = array[i][0];
            array[index][1] = array[i][1];
            array[index][2] = array[i][2];
            array[i][0] = question;
            array[i][1] = answer;
            array[i][2] = id;
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

    public static ArrayList prepareControlledAndUncontrolled(Cursor fieldEntriesCursor, int rank, int wrongness_postion) {
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
                entry_wrongness = Math.floor(((double) fieldEntriesCursor.getInt(5) / fieldEntriesCursor.getInt(4)) * 100) / 100;
            } else {
                entry_wrongness = -1;
            }
            if (rank > Constants.Rank.LOW && wrongness > 0) {
                if (fieldEntriesCursor.getInt(3) >= rank || entry_wrongness >= wrongness) {
                    controlled_arraylist.add(new Object[]{fieldEntriesCursor.getLong(0), fieldEntriesCursor.getString(1),
                            fieldEntriesCursor.getString(2), entry_wrongness, rank, fieldEntriesCursor.getInt(6), fieldEntriesCursor.getInt(4)});
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
                            fieldEntriesCursor.getString(2), entry_wrongness, rank, fieldEntriesCursor.getInt(6), fieldEntriesCursor.getInt(4)});
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
        if (rank >= Constants.Rank.LOW && wrongness_postion > 0) {
            Collections.sort(controlled_arraylist, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] element1, Object[] element2) {
                    int element1_rank = (int) element1[4];
                    double element1_wrongness = (double) element1[3];
                    int element2_rank = (int) element2[4];
                    double element2_wrongness = (double) element2[3];
                    int element1_asked_count = (int) element1[6];
                    int element2_asked_count = (int) element2[6];
                    if (element1_wrongness > element2_wrongness) {
                        return -1;
                    } else if (element1_wrongness < element2_wrongness) {
                        return 1;
                    } else {
                        if (element1_asked_count > element2_asked_count) {
                            return -1;
                        } else if (element1_asked_count < element2_asked_count) {
                            return 1;
                        } else {
                            if (element1_rank > element2_rank) {
                                return -1;
                            } else if (element1_rank < element2_rank) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            });
        } else if (rank > Constants.Rank.LOW && wrongness_postion == 0) {
            Collections.sort(controlled_arraylist, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] element1, Object[] element2) {
                    int element1_rank = (int) element1[4];
                    double element1_wrongness = (double) element1[3];
                    int element2_rank = (int) element2[4];
                    double element2_wrongness = (double) element2[3];
                    int element1_asked_count = (int) element1[6];
                    int element2_asked_count = (int) element2[6];
                    if (element1_rank > element2_rank) {
                        return -1;
                    } else if (element1_rank < element2_rank) {
                        return 1;
                    } else { // rank is the same
                        if (element1_wrongness > element2_wrongness) {
                            return -1;
                        } else if (element1_wrongness < element2_wrongness) {
                            return 1;
                        } else { // wrongness and rank are the same
                            if (element1_asked_count > element2_asked_count) {
                                return -1;
                            } else if (element1_asked_count < element2_asked_count) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
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

    public static Object[] prepareListForTest(CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase, long currentFieldId, boolean isEmulated, Cursor fieldEntriesCursor, int numberOfQuestions,
                                              int controlled_portion, int rank_level, int wrongness_level) {
        final int CONTROLLED_MARKER = 0, POINT_OF_RESET = 1;
        final int NO_CONTROLLED = -1, NO_RESET = -1;
        String[][] listForTest = new String[numberOfQuestions][3];
        String[][] list_of_entries_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        String[][] list_of_entries_did_not_asked_current_round = new String[fieldEntriesCursor.getCount()][2];
        long[] list_of_entries_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        long[] list_of_entries_did_not_asked_current_round_ids = new long[fieldEntriesCursor.getCount()];
        int[] markers = new int[2];
        Object[] result = new Object[2];
        if (wrongness_level == -1 && rank_level == -1 && controlled_portion == -1) {
            markers[CONTROLLED_MARKER] = NO_CONTROLLED; // indicates that there is no controlled entries
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
            if (list_of_entries_did_not_asked_current_round.length == 0) {
                cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(currentFieldId, isEmulated);
            }
            if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < listForTest.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[n]);
                }
                markers[POINT_OF_RESET] = NO_RESET;
                result[0] = listForTest;
                result[1] = markers;
                return result;
            } else {
                ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                    listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                    listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                    listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[n]);
                }
                markers[POINT_OF_RESET] = list_of_entries_did_not_asked_current_round.length - 1; // last index of the old round entries
                ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                    listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                    listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                    listForTest[n][2] = String.valueOf(list_of_entries_asked_current_round_ids[i]);
                }
                result[0] = listForTest;
                result[1] = markers;
                return result;
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
            ArrayList<Object> arrayList = ActivityUtility.prepareControlledAndUncontrolled(fieldEntriesCursor, rank_level, wrongness_level);
            ArrayList<Object[]> controlled_arraylist = (ArrayList<Object[]>) arrayList.get(0);
            String[][] uncontrolled_entries = (String[][]) arrayList.get(1);
            long[] uncontrolled_entries_ids = (long[]) arrayList.get(2);
            int[] uncontrolled_entries_whether_asked = (int[]) arrayList.get(3);
            if (controlled_arraylist == null) {
                markers[CONTROLLED_MARKER] = NO_CONTROLLED; // indicates that there is no controlled entries
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
                if (list_of_entries_did_not_asked_current_round.length == 0) {// All entries is asked, so we need to start a new round
                    cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(currentFieldId, isEmulated);
                }
                if (numberOfQuestions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < listForTest.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[n]);
                    }
                    markers[POINT_OF_RESET] = NO_RESET;
                    result[0] = listForTest;
                    result[1] = markers;
                    return result;
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = 0; n < list_of_entries_did_not_asked_current_round.length; n++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[n][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[n][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[n]);
                    }
                    markers[POINT_OF_RESET] = list_of_entries_did_not_asked_current_round.length - 1; // index of the last entry of the old round
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_asked_current_round_ids[i]);
                    }
                    result[0] = listForTest;
                    result[1] = markers;
                    return result;
                }
            } else if (uncontrolled_entries == null) {
                for (int n = 0; n < listForTest.length; n++) {
                    Object[] element = controlled_arraylist.get(n);
                    listForTest[n][0] = (String) element[1];
                    listForTest[n][1] = (String) element[2];
                    listForTest[n][2] = String.valueOf(element[0]);
                }
                markers[CONTROLLED_MARKER] = numberOfQuestions - 1; // the index of the last controlled entry
                markers[POINT_OF_RESET] = NO_RESET;
                shuffleArray(listForTest);
                result[0] = listForTest;
                result[1] = markers;
                return result;
            } else {
                if (number_of_controlled_questions > controlled_arraylist.size()) {
                    number_of_controlled_questions = controlled_arraylist.size();
                    number_of_uncontrolled_questions = numberOfQuestions - number_of_controlled_questions;
                } else if (number_of_uncontrolled_questions > uncontrolled_entries.length) {
                    number_of_uncontrolled_questions = uncontrolled_entries.length;
                    number_of_controlled_questions = numberOfQuestions - number_of_uncontrolled_questions;
                }
                for (int n = 0; n < number_of_controlled_questions; n++) {
                    Object[] element = controlled_arraylist.get(n);
                    listForTest[n][0] = (String) element[1];
                    listForTest[n][1] = (String) element[2];
                    listForTest[n][2] = String.valueOf(element[0]);
                }
                shuffleArray(listForTest, number_of_controlled_questions - 1);
                markers[CONTROLLED_MARKER] = number_of_controlled_questions - 1; // the index of the last controlled entry
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
                    if (whether_asked == Constants.TestRound.ASKED_AT_CURRENT_ROUND) {
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
                if (list_of_entries_did_not_asked_current_round.length == 0) { // All entries is asked, so we need to start a new round
                    cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(currentFieldId, isEmulated);
                }
                if (number_of_uncontrolled_questions <= list_of_entries_did_not_asked_current_round.length) {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + number_of_uncontrolled_questions; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[i]);
                    }
                    markers[POINT_OF_RESET] = NO_RESET;
                } else {
                    ActivityUtility.shuffleArray(list_of_entries_did_not_asked_current_round, list_of_entries_did_not_asked_current_round_ids);
                    for (int n = number_of_controlled_questions, i = 0; n < number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_did_not_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_did_not_asked_current_round[i][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_did_not_asked_current_round_ids[i]);
                    }
                    markers[POINT_OF_RESET] = number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length - 1; // the index of the last entry from the old round
                    ActivityUtility.shuffleArray(list_of_entries_asked_current_round, list_of_entries_asked_current_round_ids);
                    for (int n = number_of_controlled_questions + list_of_entries_did_not_asked_current_round.length, i = 0; n < listForTest.length; n++, i++) {
                        listForTest[n][0] = list_of_entries_asked_current_round[i][0];
                        listForTest[n][1] = list_of_entries_asked_current_round[i][1];
                        listForTest[n][2] = String.valueOf(list_of_entries_asked_current_round_ids[i]);
                    }
                }
                result[0] = listForTest;
                result[1] = markers;
                return result;
            }
        }
    }

    public static Spannable spanText(String text, TextView textView) {
        int[] colors = {0xFF5A0000, 0xFF0000FF,
                0xFF760E32, 0xFF500850, 0xFF083156,
                0xFFFF0000, 0xFF4B0082};
        int[] aligns = {Gravity.CENTER, Gravity.START, Gravity.END, Gravity.TOP, Gravity.BOTTOM};
        SecureRandom secureRandom = new SecureRandom();
        Spannable spanned_text = Spannable.Factory.getInstance().newSpannable(text);
        for (int i = 0; i < text.length(); i++) {
            if (!text.substring(i, i + 1).matches("\\s")) {
                spanned_text.setSpan(new ForegroundColorSpan(colors[secureRandom.nextInt(7)]),
                        i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setGravity(aligns[secureRandom.nextInt(5)]);
        return spanned_text;
    }

    public static String getTextFileFromRawDirectory(Context context, int resource) {
        String text = null;
        try {
            InputStream is = context.getResources().openRawResource(resource);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1) ;
            text = new String(buffer);
        } catch (Exception e) {
            Log.e("error", "" + e.toString());
        }
        return text;
    }

    public static String getVersionCode(Context context) {
        PackageInfo pi = null;
        String s;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            s = "V: " + pi.versionCode + " - ";
        } catch (PackageManager.NameNotFoundException e) {
            s = "V: problem - ";
            e.printStackTrace();
        }
        return s;
    }

    public static int calculate_time_of_answer(String question, String answer, int reading_letter_second,
                                               int find_answer_time, int typing_letter_second) {
        if (reading_letter_second <= 0 && find_answer_time <= 0 && typing_letter_second <= 0) {
            return 0;
        } else {
            int reading_seconds = question.length() / reading_letter_second;
            reading_seconds = question.length() % reading_letter_second > 0 ? reading_seconds + 1 : reading_seconds;
            int typing_seconds = answer.length() / typing_letter_second;
            typing_seconds = answer.length() % typing_letter_second > 0 ? typing_seconds + 1 : typing_seconds;
            return reading_seconds + find_answer_time + typing_seconds;
        }
    }

    public static void showNewRoundDialog(Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.new_round_dialog)
                .create().show();
    }

    /**
     * This method take care of the round work of the old and new round entries
     * It assume that controlled entries comes first in the array of test then the old round entries then the new round entries
     */
    public static void updateRoundData(long current_field_id, String[][] listForTest,
                                       CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase,
                                       int currentQuestionCounter,
                                       int last_controlled_index, int pointOfReset,
                                       boolean isEmulated) {
        if (currentQuestionCounter > last_controlled_index) {
            if (pointOfReset != currentQuestionCounter) {
                cursorLoaderAndUpdateDatabase.makeAskedCurrentTestRound(Long.parseLong(listForTest[currentQuestionCounter][2]));
            } else {
                cursorLoaderAndUpdateDatabase.restWhetherAskedCurrentTestRound(current_field_id, isEmulated);
            }
        }
    }


    public static void crossFade(View content, final View mLoadingView, Context context) {
        int mShortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        content.setAlpha(0f);
        content.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        content.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    public static void antiCrossFade(View content, View mLoadingView, Context context) {
        int mShortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mLoadingView.setAlpha(0f);
        mLoadingView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mLoadingView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);
    }

    public static Spannable prepare_search_spans(String text, StringBuilder search_value) {
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        if (search_value.length() > 0) {
            String text_lowercase = text.toLowerCase();
            String search_value_lowercase = search_value.toString().toLowerCase();
            int value_lowercase_length = search_value_lowercase.length();
            if (text_lowercase.contains(search_value_lowercase)) {
                int text_lowercase_length = text_lowercase.length();
                if (!text_lowercase.equals(search_value_lowercase)) {
                    if (text_lowercase.matches("[\\p{L}0-9]+")) { // only unicode characters and digits
                        int start = 0;
                        while (start < text_lowercase_length) {
                            if (text_lowercase.indexOf(search_value_lowercase, start) == start) {
                                int end = start + value_lowercase_length;
                                spannable.setSpan(new BackgroundColorSpan(0xFFFFFF00),
                                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                start = end;
                            } else {
                                start++;
                            }
                        }
                    } else {
                        int start = 0;
                        while (start < text_lowercase_length) {
                            if (text_lowercase.indexOf(search_value_lowercase, start) == start) {
                                int end = start + value_lowercase_length;
                                spannable.setSpan(new BackgroundColorSpan(0xFFFFFF00),
                                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                start = end;
                            } else {
                                start++;
                            }
                        }
                        if (text_lowercase.startsWith(search_value_lowercase) &&
                                text_lowercase.substring(value_lowercase_length, value_lowercase_length + 1).matches("[\\s\\p{Punct}]")) {
                            spannable.setSpan(new BackgroundColorSpan(0xFFFF0000),
                                    0, value_lowercase_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (text_lowercase.endsWith(search_value_lowercase) &&
                                text_lowercase.substring(text_lowercase_length - value_lowercase_length - 1,
                                        text_lowercase_length - value_lowercase_length).matches("[\\s\\p{Punct}]")) {
                            spannable.setSpan(new BackgroundColorSpan(0xFFFF0000),
                                    text_lowercase_length - value_lowercase_length, text_lowercase_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        String[] strings = text_lowercase.split("\\Q" + search_value_lowercase + "\\E");
                        if (strings.length > 1) {
                            for (int i = 0, k = 0; i < strings.length - 1; i++) {
                                if (i == 0) {
                                    k += strings[i].length();
                                } else {
                                    k += strings[i].length() + value_lowercase_length;
                                }
                                if (strings[i].matches(".*[\\s\\p{Punct}]+") && strings[i + 1].matches("[\\s\\p{Punct}]+.*")) {
                                    spannable.setSpan(new BackgroundColorSpan(0xFFFF0000),
                                            k, k + value_lowercase_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }
                    }
                } else {
                    spannable.setSpan(new BackgroundColorSpan(0xFFFF0000),
                            0, value_lowercase_length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannable;
    }

    public static String[][] getArrayFromSerializable(Serializable serializable) {
        //   long time = System.currentTimeMillis();
        Object[] objects = (Object[]) serializable;
        String[][] array = null;
        if (objects != null) {
            array = new String[objects.length][];
            for (int i = 0; i < array.length; i++) {
                array[i] = (String[]) objects[i];
            }
        }
//        Log.d("time", System.currentTimeMillis() - time +"");
        return array;
    }
}
