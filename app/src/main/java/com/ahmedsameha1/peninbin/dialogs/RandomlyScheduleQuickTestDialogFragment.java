package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 9/23/2015.
 */
public class RandomlyScheduleQuickTestDialogFragment extends DialogFragment {
    public static final String RANDOMLY_SCHEDULE_QUICK_TEST = "randomly_schedule_quick_test";
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private DatabaseReadOperations databaseOperations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        databaseOperations = new DatabaseReadOperations(getActivity());
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        setRetainInstance(true);
    }



    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.schedule_random_quick_tests)
                        .setView(inflater.inflate(R.layout.randomly_schedule_quick_test_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(dialog -> {
            final CheckBox enable_schedule = (CheckBox) alertDialog.findViewById(R.id.schedule_random_quick_test);
            final CheckBox enable_exclude_times = (CheckBox) alertDialog.findViewById(R.id.exclude_times);
            final EditText enter_number_of_quick_tests = (EditText) alertDialog.findViewById(R.id.number_of_quick_tests);
            final EditText enter_number_of_questions = (EditText) alertDialog.findViewById(R.id.number_of_questions);
            final EditText enter_number_of_tries = (EditText) alertDialog.findViewById(R.id.number_of_tries);
            final LinearLayout show_schedule_options = (LinearLayout) alertDialog.findViewById(R.id.show_schedule_options);
            final LinearLayout excluded_times = (LinearLayout) alertDialog.findViewById(R.id.excluded_times);
            final LinearLayout show_add_excluded_time_button = (LinearLayout) alertDialog.findViewById(R.id.show_add_excluded_time_button);
            final ImageButton add_excluded_time = (ImageButton) alertDialog.findViewById(R.id.add_excluded_time);
            final ImageButton remove_excluded_time = (ImageButton) alertDialog.findViewById(R.id.remove_excluded_time);
            final LinearLayout excluded_time_1_layout = (LinearLayout) alertDialog.findViewById(R.id.exclude_time_1);
            final LinearLayout excluded_time_2_layout = (LinearLayout) alertDialog.findViewById(R.id.exclude_time_2);
            final LinearLayout excluded_time_3_layout = (LinearLayout) alertDialog.findViewById(R.id.exclude_time_3);
            final LinearLayout excluded_time_4_layout = (LinearLayout) alertDialog.findViewById(R.id.exclude_time_4);
            final LinearLayout excluded_time_5_layout = (LinearLayout) alertDialog.findViewById(R.id.exclude_time_5);
            final EditText enter_excluded_time_1_start = (EditText) alertDialog.findViewById(R.id.excluded_time_1_start);
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_view);
            final LinearLayout control_options = (LinearLayout) alertDialog.findViewById(R.id.control_options);
            enter_excluded_time_1_start.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view113, hourOfDay, minute) -> enter_excluded_time_1_start.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_1_end = (EditText) alertDialog.findViewById(R.id.excluded_time_1_end);
            enter_excluded_time_1_end.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view112, hourOfDay, minute) -> enter_excluded_time_1_end.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_2_start = (EditText) alertDialog.findViewById(R.id.excluded_time_2_start);
            enter_excluded_time_2_start.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view111, hourOfDay, minute) -> enter_excluded_time_2_start.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_2_end = (EditText) alertDialog.findViewById(R.id.excluded_time_2_end);
            enter_excluded_time_2_end.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view110, hourOfDay, minute) -> enter_excluded_time_2_end.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_3_start = (EditText) alertDialog.findViewById(R.id.excluded_time_3_start);
            enter_excluded_time_3_start.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view19, hourOfDay, minute) -> enter_excluded_time_3_start.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_3_end = (EditText) alertDialog.findViewById(R.id.excluded_time_3_end);
            enter_excluded_time_3_end.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view18, hourOfDay, minute) -> enter_excluded_time_3_end.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_4_start = (EditText) alertDialog.findViewById(R.id.excluded_time_4_start);
            enter_excluded_time_4_start.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view17, hourOfDay, minute) -> enter_excluded_time_4_start.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_4_end = (EditText) alertDialog.findViewById(R.id.excluded_time_4_end);
            enter_excluded_time_4_end.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view16, hourOfDay, minute) -> enter_excluded_time_4_end.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_5_start = (EditText) alertDialog.findViewById(R.id.excluded_time_5_start);
            enter_excluded_time_5_start.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view15, hourOfDay, minute) -> enter_excluded_time_5_start.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText enter_excluded_time_5_end = (EditText) alertDialog.findViewById(R.id.excluded_time_5_end);
            enter_excluded_time_5_end.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    new TimePickerDialog(getActivity(), (view14, hourOfDay, minute) -> enter_excluded_time_5_end.setText(hourOfDay + ":" + (minute == 0 ? "00" : Integer.toString(minute))), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
                }
                enter_number_of_questions.requestFocus();
            });
            final EditText[] editTexts = {enter_excluded_time_1_start, enter_excluded_time_1_end,
                    enter_excluded_time_2_start, enter_excluded_time_2_end,
                    enter_excluded_time_3_start, enter_excluded_time_3_end,
                    enter_excluded_time_4_start, enter_excluded_time_4_end,
                    enter_excluded_time_5_start, enter_excluded_time_5_end};

            enable_schedule.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    show_schedule_options.setVisibility(View.VISIBLE);
                } else {
                    enable_exclude_times.setChecked(false);
                    show_schedule_options.setVisibility(View.GONE);
                    enter_number_of_quick_tests.setText("");
                    enter_number_of_tries.setText("");
                    enter_number_of_questions.setText("");
                }
            });
            enable_exclude_times.setOnCheckedChangeListener((compoundButton, checked) -> {
                if (checked) {
                    excluded_times.setVisibility(View.VISIBLE);
                    show_add_excluded_time_button.setVisibility(View.VISIBLE);
                    excluded_time_1_layout.setVisibility(View.VISIBLE);
                } else {
                    excluded_times.setVisibility(View.GONE);
                    show_add_excluded_time_button.setVisibility(View.GONE);
                    excluded_time_1_layout.setVisibility(View.GONE);
                    excluded_time_2_layout.setVisibility(View.GONE);
                    excluded_time_3_layout.setVisibility(View.GONE);
                    excluded_time_4_layout.setVisibility(View.GONE);
                    excluded_time_5_layout.setVisibility(View.GONE);
                    for ( EditText editText: editTexts ) {
                        editText.setText("");
                    }
                }
            });
            add_excluded_time.setOnClickListener(view -> {
                if (excluded_time_2_layout.getVisibility() == View.GONE) {
                    excluded_time_2_layout.setVisibility(View.VISIBLE);
                } else if (excluded_time_3_layout.getVisibility() == View.GONE) {
                    excluded_time_3_layout.setVisibility(View.VISIBLE);
                } else if (excluded_time_4_layout.getVisibility() == View.GONE) {
                    excluded_time_4_layout.setVisibility(View.VISIBLE);
                } else if (excluded_time_5_layout.getVisibility() == View.GONE) {
                    excluded_time_5_layout.setVisibility(View.VISIBLE);
                }
            });
            remove_excluded_time.setOnClickListener(view -> {
                if ( excluded_time_5_layout.getVisibility() == View.VISIBLE ) {
                    excluded_time_5_layout.setVisibility(View.GONE);
                } else if ( excluded_time_4_layout.getVisibility() == View.VISIBLE ) {
                    excluded_time_4_layout.setVisibility(View.GONE);
                } else if ( excluded_time_3_layout.getVisibility() == View.VISIBLE ) {
                    excluded_time_3_layout.setVisibility(View.GONE);
                } else if ( excluded_time_2_layout.getVisibility() == View.VISIBLE ) {
                    excluded_time_2_layout.setVisibility(View.GONE);
                } else {
                    enable_exclude_times.setChecked(false);
                }
            });

            enter_number_of_tries.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string._try);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enter_number_of_tries.setText("");
                    } else {
                        string = string + suffix;
                        enter_number_of_tries.setText(string);
                        enter_number_of_tries.setSelection(string.length() - suffix.length());
                    }
                }
            });
            enter_number_of_questions.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string._question);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enter_number_of_questions.setText("");
                    } else {
                        string = string + suffix;
                        enter_number_of_questions.setText(string);
                        enter_number_of_questions.setSelection(string.length() - suffix.length());
                    }
                }
            });
            enter_number_of_quick_tests.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string.quick_test);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enter_number_of_quick_tests.setText("");
                    } else {
                        string = string + suffix;
                        enter_number_of_quick_tests.setText(string);
                        enter_number_of_quick_tests.setSelection(string.length() - suffix.length());
                    }
                }
            });
            int[] data = databaseOperations.getRandomQuickTestData(current_field_id);
            if (data != null) {
                enable_schedule.setChecked(true);
                enter_number_of_quick_tests.setText(Integer.toString(data[0]));
                enter_number_of_tries.setText(Integer.toString(data[1]));
                enter_number_of_questions.setText(Integer.toString(data[2]));
                String[][] excluded_times_array = databaseOperations.get_excluded_times(current_field_id);
                for (int i = 0; i < excluded_times_array.length; i++) {
                    if (excluded_times_array[i] != null) {
                        if (i == 0) {
                            String starts = excluded_times_array[i][0];
                            String ends = excluded_times_array[i][1];
                            editTexts[0].setText(starts);
                            editTexts[1].setText(ends);
                            enable_exclude_times.setChecked(true);
                        }
                        if (i == 1) {
                            String starts = excluded_times_array[i][0];
                            String ends = excluded_times_array[i][1];
                            editTexts[2].setText(starts);
                            editTexts[3].setText(ends);
                            excluded_time_2_layout.setVisibility(View.VISIBLE);
                        }
                        if (i == 2) {
                            String starts = excluded_times_array[i][0];
                            String ends = excluded_times_array[i][1];
                            editTexts[4].setText(starts);
                            editTexts[5].setText(ends);
                            excluded_time_3_layout.setVisibility(View.VISIBLE);
                        }
                        if (i == 3) {
                            String starts = excluded_times_array[i][0];
                            String ends = excluded_times_array[i][1];
                            editTexts[6].setText(starts);
                            editTexts[7].setText(ends);
                            excluded_time_4_layout.setVisibility(View.VISIBLE);
                        }
                        if (i == 4) {
                            String starts = excluded_times_array[i][0];
                            String ends = excluded_times_array[i][1];
                            editTexts[8].setText(starts);
                            editTexts[9].setText(ends);
                            excluded_time_5_layout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                enable_schedule.setChecked(false);
            }

            Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(view -> {
                try {
                    if ( enable_schedule.isChecked() ) {
                        String trys_suffix = " " + getActivity().getString(R.string._try);
                        String number_of_questions_suffix = " " + getActivity().getString(R.string._question);
                        String number_of_quick_tests_suffix = " " + getActivity().getString(R.string.quick_test);
                        String number_of_tries_string = enter_number_of_tries.getText().toString();
                        String number_of_questions_string = enter_number_of_questions.getText().toString();
                        String number_of_quick_tests_string = enter_number_of_quick_tests.getText().toString();
                        if ( number_of_questions_string.length() > 0
                                && number_of_quick_tests_string.length() > 0) {
                            int number_of_tries;
                            if ( number_of_tries_string.length() > 0 ) {
                                number_of_tries = Integer.parseInt(number_of_tries_string.substring(0,number_of_tries_string.length() - trys_suffix.length()));
                            } else {
                                number_of_tries = 1;
                            }
                            int number_of_questions = Integer.parseInt(number_of_questions_string.substring(0, number_of_questions_string.length() - number_of_questions_suffix.length()));
                            int number_of_quick_tests = Integer.parseInt(number_of_quick_tests_string.substring(0, number_of_quick_tests_string.length() - number_of_quick_tests_suffix.length()));
                            if ( number_of_quick_tests <= 94 ) {
                                if ( enable_exclude_times.isChecked() ) {
                                    if (!(excluded_time_1_layout.getVisibility() == View.VISIBLE &&
                                            (enter_excluded_time_1_start.getText().length() == 0
                                                    || enter_excluded_time_1_end.getText().length() == 0))
                                            || (excluded_time_2_layout.getVisibility() == View.VISIBLE &&
                                            (enter_excluded_time_2_start.getText().length() == 0
                                                    || enter_excluded_time_2_end.getText().length() == 0))
                                            || (excluded_time_3_layout.getVisibility() == View.VISIBLE &&
                                            (enter_excluded_time_3_start.getText().length() == 0
                                                    || enter_excluded_time_3_end.getText().length() == 0))
                                            || (excluded_time_4_layout.getVisibility() == View.VISIBLE &&
                                            (enter_excluded_time_4_start.getText().length() == 0
                                                    || enter_excluded_time_4_end.getText().length() == 0))
                                            || (excluded_time_5_layout.getVisibility() == View.VISIBLE &&
                                            (enter_excluded_time_5_start.getText().length() == 0
                                                    || enter_excluded_time_5_end.getText().length() == 0))) {
                                        boolean start_later_end = true;
                                        Calendar calendar = null;
                                        if (excluded_time_1_layout.getVisibility() == View.VISIBLE) {
                                            long start = -1, end = -1;
                                            String text;
                                            StringTokenizer stringTokenizer;
                                            calendar = Calendar.getInstance(Locale.getDefault());
                                            text = enter_excluded_time_1_start.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            start = calendar.getTimeInMillis();
                                            text = enter_excluded_time_1_end.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            end = calendar.getTimeInMillis();
                                            if (start >= end) {
                                                start_later_end = false;
                                            }
                                        }
                                        if (excluded_time_2_layout.getVisibility() == View.VISIBLE) {
                                            long start = -1, end = -1;
                                            String text;
                                            StringTokenizer stringTokenizer;
                                            calendar = Calendar.getInstance(Locale.getDefault());
                                            text = enter_excluded_time_2_start.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            start = calendar.getTimeInMillis();
                                            text = enter_excluded_time_2_end.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            end = calendar.getTimeInMillis();
                                            if (start >= end) {
                                                start_later_end = false;
                                            }
                                        }
                                        if (excluded_time_3_layout.getVisibility() == View.VISIBLE) {
                                            long start = -1, end = -1;
                                            String text;
                                            StringTokenizer stringTokenizer;
                                            calendar = Calendar.getInstance(Locale.getDefault());
                                            text = enter_excluded_time_3_start.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            start = calendar.getTimeInMillis();
                                            text = enter_excluded_time_3_end.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            end = calendar.getTimeInMillis();
                                            if (start >= end) {
                                                start_later_end = false;
                                            }
                                        }
                                        if (excluded_time_4_layout.getVisibility() == View.VISIBLE) {
                                            long start = -1, end = -1;
                                            String text;
                                            StringTokenizer stringTokenizer;
                                            calendar = Calendar.getInstance(Locale.getDefault());
                                            text = enter_excluded_time_4_start.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            start = calendar.getTimeInMillis();
                                            text = enter_excluded_time_4_end.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            end = calendar.getTimeInMillis();
                                            if (start >= end) {
                                                start_later_end = false;
                                            }
                                        }
                                        if (excluded_time_5_layout.getVisibility() == View.VISIBLE) {
                                            long start = -1, end = -1;
                                            String text;
                                            StringTokenizer stringTokenizer;
                                            calendar = Calendar.getInstance(Locale.getDefault());
                                            text = enter_excluded_time_5_start.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            start = calendar.getTimeInMillis();
                                            text = enter_excluded_time_5_end.getText().toString();
                                            stringTokenizer = new StringTokenizer(text, ":");
                                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringTokenizer.nextToken()));
                                            calendar.set(Calendar.MINUTE, Integer.parseInt(stringTokenizer.nextToken()));
                                            end = calendar.getTimeInMillis();
                                            if (start >= end) {
                                                start_later_end = false;
                                            }
                                        }

                                        if (start_later_end) {
                                            String[][] excluded_times1 = new String[5][];
                                            if (excluded_time_1_layout.getVisibility() == View.VISIBLE) {
                                                String starts = enter_excluded_time_1_start.getText().toString();
                                                String ends = enter_excluded_time_1_end.getText().toString();
                                                excluded_times1[0] = new String[]{starts, ends};

                                            }
                                            if (excluded_time_2_layout.getVisibility() == View.VISIBLE) {
                                                String starts = enter_excluded_time_2_start.getText().toString();
                                                String ends = enter_excluded_time_2_end.getText().toString();
                                                excluded_times1[1] = new String[]{starts, ends};
                                            }
                                            if (excluded_time_3_layout.getVisibility() == View.VISIBLE) {
                                                String starts = enter_excluded_time_3_start.getText().toString();
                                                String ends = enter_excluded_time_3_end.getText().toString();
                                                excluded_times1[2] = new String[]{starts, ends};
                                            }
                                            if (excluded_time_4_layout.getVisibility() == View.VISIBLE) {
                                                String starts = enter_excluded_time_4_start.getText().toString();
                                                String ends = enter_excluded_time_4_end.getText().toString();
                                                excluded_times1[3] = new String[]{starts, ends};
                                            }
                                            if (excluded_time_5_layout.getVisibility() == View.VISIBLE) {
                                                String starts = enter_excluded_time_5_start.getText().toString();
                                                String ends = enter_excluded_time_5_end.getText().toString();
                                                excluded_times1[4] = new String[]{starts, ends};
                                            }
                                                mySQLiteCursorLoader.setRandomQuickTestData(current_field_id, number_of_quick_tests,
                                                        number_of_tries, number_of_questions, excluded_times1);
                                            final Snackbar snackbar = Snackbar
                                                    .make(coordinatorLayout, R.string.take_effect_next_midnight, Snackbar.LENGTH_INDEFINITE);
                                            snackbar.setAction(R.string.ok, view13 -> snackbar.dismiss());
                                            snackbar.show();
                                            dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(getActivity(), R.string.some_start_later_than_end, Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.TOP, 0, 400);
                                            toast.show();
                                        }


                                    } else {
                                        Toast toast = Toast.makeText(getActivity(), R.string.fill_all_fields, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                } else {
                                        mySQLiteCursorLoader.setRandomQuickTestData(current_field_id, number_of_quick_tests,
                                                number_of_tries, number_of_questions, null);
                                    final Snackbar snackbar = Snackbar
                                            .make(coordinatorLayout, R.string.take_effect_next_midnight, Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(R.string.ok, view12 -> snackbar.dismiss());
                                    snackbar.show();
                                    dismiss();
                                }
                            } else {
                                Toast toast = Toast.makeText(getActivity(), R.string.number_tests_lower_95, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity(), R.string.enter_values_of_tries_question_quick_tests, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    } else {
                        mySQLiteCursorLoader.setRandomQuickTestData(current_field_id, 0, 0, 0, null);
                        final Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, R.string.take_effect_next_midnight, Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(R.string.ok, view1 -> snackbar.dismiss());
                        snackbar.show();
                        dismiss();
                    }
                } catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            });
        });
        return alertDialog;
    }
}
