package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.utils.Money;

import java.util.Date;

/**
 * Created by ahmed on 9/23/2015.
 */
public class StudyDuringDialogFragment extends DialogFragment {
    public static final String SHOW_STUDY_DURING_DIALOG_FRAGMENT = "showStudyDuringDialogFragment";
    public static final int MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    public static final int MILLISECONDS_IN_HOUR = 60 * 60 * 1000;
    private long current_field_id;
    private boolean from_notification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION, false);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_studying_a_period)
                        .setView(inflater.inflate(R.layout.study_during_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(dialog -> {
            final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
            final ImageButton number_of_days_help = (ImageButton) alertDialog.findViewById(R.id.number_of_days_help);
            final ImageButton number_of_hours_help = (ImageButton) alertDialog.findViewById(R.id.number_of_hours_help);
            final EditText enterDay = (EditText) alertDialog.findViewById(R.id.enter_day);
            final EditText enterHour = (EditText) alertDialog.findViewById(R.id.enter_hour);
            final EditText enterTries = (EditText) alertDialog.findViewById(R.id.enter_tries);
            Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            enterTries.addTextChangedListener(new TextWatcher() {
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
                        enterTries.setText("");
                    } else {
                        string = string + suffix;
                        enterTries.setText(string);
                        enterTries.setSelection(string.length() - suffix.length());
                    }
                }
            });
            enterHour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string._hour);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enterHour.setText("");
                    } else {
                        string = string + suffix;
                        enterHour.setText(string);
                        enterHour.setSelection(string.length() - suffix.length());
                    }
                }
            });
            enterDay.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string._day);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enterDay.setText("");
                    } else {
                        string = string + suffix;
                        enterDay.setText(string);
                        enterDay.setSelection(string.length() - suffix.length());
                    }
                }
            });
            positive.setOnClickListener(view -> {
                if (Money.getUserSubscriptionStatus(getActivity()) || Money.getUserAllowedDueAnError(getActivity())) {
                try {
                    String trys_suffix = " " + getActivity().getString(R.string._try);
                    String day_suffix = " " + getActivity().getString(R.string._day);
                    String hour_suffix = " " + getActivity().getString(R.string._hour);
                    String try_string = enterTries.getText().toString();
                    String day_string = enterDay.getText().toString();
                    String hour_string = enterHour.getText().toString();
                    if ( try_string.length() > 0 ) {
                        int trys = Integer.parseInt(try_string.substring(0,try_string.length() - trys_suffix.length()));
                            int day = 0;
                            int hour = 0;
                            if ( day_string.length() > 0 || hour_string.length() > 0 ) {
                                if ( day_string.length() > 0) {
                                    day = Integer.parseInt(day_string.substring(0,day_string.length() - day_suffix.length()));
                                }
                                if ( hour_string.length() > 0) {
                                    hour = Integer.parseInt(hour_string.substring(0,hour_string.length() - hour_suffix.length()));
                                }
                                long duration = ( day * ( (long) MILLISECONDS_IN_DAY) ) + ( hour * ( (long) MILLISECONDS_IN_HOUR) );
                                long nowInMilliseconds = System.currentTimeMillis();
                                long startingTime = nowInMilliseconds - duration;
                                Intent intent = new Intent(getActivity(),StudyAPeriodActivity.class);
                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                intent.putExtra(StudyAPeriodActivity.STARTING_TIME,startingTime);
                                intent.putExtra(StudyAPeriodActivity.LAST_SESSION_OR_NEW_ONE,false);
                                intent.putExtra(StudyAPeriodActivity.NUMBER_OF_TRIES,trys);
                                startActivity(intent);
                                if ( from_notification ) {
                                    getActivity().finish();
                                }
                                dismiss();
                            } else {
                                Toast toast = Toast.makeText(getActivity(),R.string.please_enter_at_least_one_value,Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                            }
                    } else {
                        Toast toast = Toast.makeText(getActivity(),R.string.enter_tries_number,Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                } catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                } else {
                    Toast toast = Toast.makeText(getActivity(), R.string.please_subscribe, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            });
            number_of_tries_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.enter_number_of_tries_help).create().show();
            });
            number_of_days_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.enter_number_of_days_help).create().show();
            });
            number_of_hours_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.enter_number_of_hours_help).create().show();
            });
        });
        return alertDialog;
    }
}
