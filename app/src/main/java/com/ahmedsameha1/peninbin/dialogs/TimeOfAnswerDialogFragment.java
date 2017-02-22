package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 12/30/16.
 */

public class TimeOfAnswerDialogFragment extends DialogFragment {
    public static final String TYPE = "type";
    public static final String TIME_OF_ANSWER_DIALOG_FRAGMENT = "time_of_answer_dialog_fragment";
    public static final String CURRENT_TIME_OF_ANSWER = "current_time_of_answer";

    private long current_field_id;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private DatabaseReadOperations databaseReadOperations;
    private int type, current_time_of_answer;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        type = getArguments().getInt(TYPE);
        current_time_of_answer = getArguments().getInt(CURRENT_TIME_OF_ANSWER);
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(getActivity());
        databaseReadOperations = new DatabaseReadOperations(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        setRetainInstance(true);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setTitle(R.string.time_of_answer)
                .setView(inflater.inflate(R.layout.time_of_answer_dialog_fragment, null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton time_of_answer_help_help = (ImageButton) alertDialog.findViewById(R.id.time_of_answer_help);
                final EditText enter_time_of_answer = (EditText) alertDialog.findViewById(R.id.time_of_answer);
                final CheckBox save = (CheckBox) alertDialog.findViewById(R.id.save);
                final CheckBox set_time_of_answer = (CheckBox) alertDialog.findViewById(R.id.set_time_of_answer);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                enter_time_of_answer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String string = editable.toString();
                        String suffix = " " + getActivity().getString(R.string.second);
                        if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                        } else if ( string.equals(String.valueOf(0)) || !string.matches("[0-9]+") ) {
                            enter_time_of_answer.setText("");
                        } else {
                            string = string + suffix;
                            enter_time_of_answer.setText(string);
                            enter_time_of_answer.setSelection(string.length() - suffix.length());
                        }
                    }
                });
                int time;
                if ( current_time_of_answer > 0 ) {
                    set_time_of_answer.setChecked(true);
                    enter_time_of_answer.setText(String.valueOf(current_time_of_answer));
                } else {
                    set_time_of_answer.setChecked(false);
                    enter_time_of_answer.setEnabled(false);
                    enter_time_of_answer.setText("");
                    save.setEnabled(false);
                }
                if ( type == Constants.TimeOfAnswer.TEST ) {
                    time = databaseReadOperations.getTimeOfAnswerTests(current_field_id);
                    if ( time == Constants.TimeOfAnswer.NOT_USED) {
                        save.setChecked(false);
                    } else {
                        save.setChecked(true);
                    }
                } else if ( type == Constants.TimeOfAnswer.STUDY_A_PERIOD ) {
                    time = databaseReadOperations.getTimeOfAnswerStudyAPeriod(current_field_id);
                    if ( time == Constants.TimeOfAnswer.NOT_USED) {
                        save.setChecked(false);
                    } else {
                        save.setChecked(true);
                    }
                }

                set_time_of_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if ( checked ) {
                            enter_time_of_answer.setEnabled(true);
                            enter_time_of_answer.requestFocus();
                            save.setEnabled(true);
                        } else {
                            enter_time_of_answer.setEnabled(false);
                            enter_time_of_answer.setText("");
                            save.setEnabled(false);
                        }
                    }
                });
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if ( set_time_of_answer.isChecked() ) {
                                String second_suffix = " " + getActivity().getString(R.string.second);
                                String time_string = enter_time_of_answer.getText().toString();
                                if ( time_string.length() > 0 ) {
                                    int number = Integer.parseInt(time_string.substring(0, time_string.length() - second_suffix.length()));
                                    if ( number > 0 ) {
                                        if (save.isChecked()) {
                                            cursorLoaderAndUpdateDatabase.setTimeOfAnswer(number, current_field_id, type);
                                        }
                                        ((Contact) getActivity()).setTimeOfAnswer(number);
                                        dismiss();
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Utilities")
                                                .setAction("time of answer")
                                                .build());
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getActivity(), getString(R.string.enter_the_time_of_answer), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            } else {
                                    cursorLoaderAndUpdateDatabase.setTimeOfAnswer(Constants.TimeOfAnswer.NOT_USED, current_field_id, type);
                                ((Contact) getActivity()).setTimeOfAnswer(Constants.TimeOfAnswer.NOT_USED);
                                dismiss();
                            }
                        } catch (NumberFormatException e) {
                            Toast toast = Toast.makeText(getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                time_of_answer_help_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_time_of_answer_help).create().show();
                    }
                });
            }
        });
        return alertDialog;
    }

    public interface Contact {
        void setTimeOfAnswer(int time);
    }
}
