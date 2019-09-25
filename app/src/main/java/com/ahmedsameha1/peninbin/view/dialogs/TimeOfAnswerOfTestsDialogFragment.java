package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.view.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.view.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.view.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 12/30/16.
 */

public class TimeOfAnswerOfTestsDialogFragment extends DialogFragment {
    public static final String TIME_OF_ANSWER_DIALOG_FRAGMENT = "time_of_answer_of_tests_dialog_fragment";
    public static final String CURRENT_TIME_OF_ANSWER = "current_time_of_answer";
    public static final String CURRENT_TIME_OF_ANSWER_DO_WHAT = "current_time_of_answer_do_what";
    public static final String TIME_OF_ANSWER_SAVED = "time_of_answer_saved";
    public static final String READING_QUESTION_LETTER_SECOND = "reading_question_letter_second";
    public static final String FIND_ANSWER_TIME = "find_answer_time";
    public static final String TYPING_ANSWER_LETTER_SECOND = "typing_answer_letter_second";

    private long current_field_id;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private int current_time_of_answer_do_what;
    private boolean time_of_answer_saved;
    private Tracker mTracker;
    private int reading_question_letter_second, find_answer_time, typing_answer_letter_second;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        reading_question_letter_second = getArguments().getInt(READING_QUESTION_LETTER_SECOND);
        find_answer_time = getArguments().getInt(FIND_ANSWER_TIME);
        typing_answer_letter_second = getArguments().getInt(TYPING_ANSWER_LETTER_SECOND);
        current_time_of_answer_do_what = getArguments().getInt(CURRENT_TIME_OF_ANSWER_DO_WHAT);
        time_of_answer_saved = getArguments().getBoolean(TIME_OF_ANSWER_SAVED);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        Activity activity = getActivity();
        if (!(activity instanceof EnhancedTestActivity || activity instanceof FullyRandomTestActivity ||
                activity instanceof QuickTestActivity || activity instanceof StudyAPeriodActivity)) {
            setRetainInstance(true);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setTitle(R.string.time_of_answer)
                .setView(inflater.inflate(R.layout.time_of_answer_of_tests_dialog_fragment, null))
                .setPositiveButton(R.string.ok, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton letter_second_reading_question_help = (ImageButton) alertDialog.findViewById(R.id.letter_second_reading_question_help);
                final ImageButton time_of_finding_answer_help = (ImageButton) alertDialog.findViewById(R.id.time_of_finding_answer_help);
                final ImageButton letter_second_typing_answer_help = (ImageButton) alertDialog.findViewById(R.id.letter_second_typing_answer_help);
                final EditText letter_second_reading_question = (EditText) alertDialog.findViewById(R.id.letter_second_reading_question);
                final EditText time_of_finding_answer = (EditText) alertDialog.findViewById(R.id.time_of_finding_answer);
                final EditText letter_second_typing_answer = (EditText) alertDialog.findViewById(R.id.letter_second_typing_answer);
                final CheckBox save = (CheckBox) alertDialog.findViewById(R.id.save);
                final CheckBox set_time_of_answer = (CheckBox) alertDialog.findViewById(R.id.set_time_of_answer);
                final RadioButton notify = (RadioButton) alertDialog.findViewById(R.id.notify);
                final RadioButton move_next = (RadioButton) alertDialog.findViewById(R.id.move_next);
                final RadioGroup do_what = (RadioGroup) alertDialog.findViewById(R.id.do_what);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                letter_second_reading_question.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String entered_string = editable.toString();
                        String prefix = getActivity().getString(R.string.reading_prefix);
                        String reg_string = getActivity().getString(R.string.reg_reading_per_letter_second);
                        if ("".equals(entered_string) || entered_string.matches(reg_string)) {

                        } else if (entered_string.equals(String.valueOf(0)) || !entered_string.matches("[0-9]+")) {
                            letter_second_reading_question.setText("");
                        } else {
                            String formatted_string = getActivity().getString(R.string.format_reading_per_letter_second, entered_string);
                            letter_second_reading_question.setText(formatted_string);
                            letter_second_reading_question.setSelection(prefix.length() + entered_string.length());
                        }
                    }
                });
                time_of_finding_answer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String string = editable.toString();
                        String suffix = getActivity().getString(R.string.find_answer_suffix);
                        if ("".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix)) {

                        } else if (string.equals(String.valueOf(0)) || !string.matches("[0-9]+")) {
                            time_of_finding_answer.setText("");
                        } else {
                            time_of_finding_answer.setText(string + suffix);
                            time_of_finding_answer.setSelection(string.length());
                        }
                    }
                });
                letter_second_typing_answer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String entered_string = editable.toString();
                        String prefix = getActivity().getString(R.string.typing_prefix);
                        String reg_string = getActivity().getString(R.string.reg_typing_per_letter_second);
                        if ("".equals(entered_string) || entered_string.matches(reg_string)) {

                        } else if (entered_string.equals(String.valueOf(0)) || !entered_string.matches("[0-9]+")) {
                            letter_second_typing_answer.setText("");
                        } else {
                            String formatted_string = getActivity().getString(R.string.format_typing_per_letter_second, entered_string);
                            letter_second_typing_answer.setText(formatted_string);
                            letter_second_typing_answer.setSelection(prefix.length() + entered_string.length());
                        }
                    }
                });
                if (reading_question_letter_second > Constants.TimeOfAnswer.NOT_USED && find_answer_time > Constants.TimeOfAnswer.NOT_USED
                        && typing_answer_letter_second > Constants.TimeOfAnswer.NOT_USED) {
                    set_time_of_answer.setChecked(true);
                    letter_second_reading_question.setText(String.valueOf(reading_question_letter_second));
                    time_of_finding_answer.setText(String.valueOf(find_answer_time));
                    letter_second_typing_answer.setText(String.valueOf(typing_answer_letter_second));
                    save.setEnabled(true);
                    if (current_time_of_answer_do_what == Constants.TimeOfAnswer.NOTIFY) {
                        notify.setChecked(true);
                    } else if (current_time_of_answer_do_what == Constants.TimeOfAnswer.MOVE_NEXT) {
                        move_next.setChecked(true);
                    }
                } else {
                    set_time_of_answer.setChecked(false);
                    letter_second_reading_question.setEnabled(false);
                    letter_second_reading_question.setText("");
                    time_of_finding_answer.setEnabled(false);
                    time_of_finding_answer.setText("");
                    letter_second_typing_answer.setEnabled(false);
                    letter_second_typing_answer.setText("");
                    save.setEnabled(false);
                    notify.setEnabled(false);
                    move_next.setEnabled(false);
                }
                set_time_of_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked) {
                            letter_second_reading_question.setEnabled(true);
                            letter_second_reading_question.requestFocus();
                            time_of_finding_answer.setEnabled(true);
                            letter_second_typing_answer.setEnabled(true);
                            save.setEnabled(true);
                            notify.setEnabled(true);
                            move_next.setEnabled(true);
                        } else {
                            letter_second_reading_question.setEnabled(false);
                            letter_second_reading_question.setText("");
                            time_of_finding_answer.setEnabled(false);
                            time_of_finding_answer.setText("");
                            letter_second_typing_answer.setEnabled(false);
                            letter_second_typing_answer.setText("");
                            save.setEnabled(false);
                            notify.setEnabled(false);
                            move_next.setEnabled(false);
                        }
                    }
                });
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            ((ActivityUtility.ActivityTimers) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                            return false;
                        }
                        return false;

                    }
                });
                save.setChecked(time_of_answer_saved);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int do_what_choice = do_what.getCheckedRadioButtonId() == R.id.notify ?
                                    Constants.TimeOfAnswer.NOTIFY : Constants.TimeOfAnswer.MOVE_NEXT;
                            if (set_time_of_answer.isChecked()) {
                                String find_suffix = TimeOfAnswerOfTestsDialogFragment.this.getActivity().getString(R.string.find_answer_suffix);
                                String typing_prefix = TimeOfAnswerOfTestsDialogFragment.this.getActivity().getString(R.string.typing_prefix);
                                String reading_prefix = TimeOfAnswerOfTestsDialogFragment.this.getActivity().getString(R.string.reading_prefix);
                                String letter_per_second_suffix = TimeOfAnswerOfTestsDialogFragment.this.getActivity().getString(R.string.letter_per_second_suffix);
                                String reading = letter_second_reading_question.getText().toString();
                                String find = time_of_finding_answer.getText().toString();
                                String typing = letter_second_typing_answer.getText().toString();
                                if (reading.length() > 0 && find.length() > 0 && typing.length() > 0) {
                                    int reading_number = Integer.parseInt(reading.substring(reading_prefix.length(), reading.indexOf(letter_per_second_suffix)));
                                    int find_number = Integer.parseInt(find.substring(0, find.length() - find_suffix.length()));
                                    int typing_number = Integer.parseInt(typing.substring(typing_prefix.length(), typing.indexOf(letter_per_second_suffix)));
                                    if (reading_number > 0 && find_number > 0 && typing_number > 0) {
                                        ((Contact) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).setTimeOfAnswer(reading_number, find_number, typing_number, do_what_choice, save.isChecked());
                                        if (save.isChecked()) {
                                            cursorLoaderAndUpdateDatabase.setTimeOfAnswerOfTests(current_field_id, reading_number, find_number, typing_number, do_what_choice);
                                        } else {
                                            cursorLoaderAndUpdateDatabase.setTimeOfAnswerOfTests(current_field_id,
                                                    Constants.TimeOfAnswer.NOT_USED, Constants.TimeOfAnswer.NOT_USED, Constants.TimeOfAnswer.NOT_USED, do_what_choice);
                                        }
                                        ((ActivityUtility.ActivityTimers) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                                        TimeOfAnswerOfTestsDialogFragment.this.dismiss();
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Utilities")
                                                .setAction((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(TimeOfAnswerOfTestsDialogFragment.this.getActivity()) + "time of answer")
                                                .build());
                                    }
                                } else {
                                    Toast toast = Toast.makeText(TimeOfAnswerOfTestsDialogFragment.this.getActivity(), TimeOfAnswerOfTestsDialogFragment.this.getString(R.string.missing_data), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            } else {
                                cursorLoaderAndUpdateDatabase.setTimeOfAnswerOfTests(current_field_id,
                                        Constants.TimeOfAnswer.NOT_USED, Constants.TimeOfAnswer.NOT_USED, Constants.TimeOfAnswer.NOT_USED, do_what_choice);
                                ((Contact) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).setTimeOfAnswer(Constants.TimeOfAnswer.NOT_USED,
                                        Constants.TimeOfAnswer.NOT_USED, Constants.TimeOfAnswer.NOT_USED, do_what_choice, save.isChecked());
                                ((ActivityUtility.ActivityTimers) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                                TimeOfAnswerOfTestsDialogFragment.this.dismiss();
                            }
                        } catch (NumberFormatException e) {
                            Toast toast = Toast.makeText(TimeOfAnswerOfTestsDialogFragment.this.getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                letter_second_reading_question_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(TimeOfAnswerOfTestsDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.reading_letter_second_help).create().show();
                    }
                });
                time_of_finding_answer_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(TimeOfAnswerOfTestsDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.finding_answer_help).create().show();
                    }
                });
                letter_second_typing_answer_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(TimeOfAnswerOfTestsDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.typing_letter_second_help).create().show();
                    }
                });
                ((ActivityUtility.ActivityTimers) TimeOfAnswerOfTestsDialogFragment.this.getActivity()).manageTimersDueDialogsShow(false);
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    public interface Contact {
        void setTimeOfAnswer(int reading_letter_second, int find_time, int typing_letter_second, int time_of_answer_do_what, boolean saved);
    }

}
