package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION,false);
        setRetainInstance(true);
    }
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_studying_a_period)
                        .setView(inflater.inflate(R.layout.study_during_dialog_fragment,null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
                final ImageButton number_of_days_help = (ImageButton) alertDialog.findViewById(R.id.number_of_days_help);
                final ImageButton number_of_hours_help = (ImageButton) alertDialog.findViewById(R.id.number_of_hours_help);
                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            EditText enterDay = (EditText) alertDialog.findViewById(R.id.enter_day);
                            EditText enterHour = (EditText) alertDialog.findViewById(R.id.enter_hour);
                            EditText enterTries = (EditText) alertDialog.findViewById(R.id.enter_tries);
                            if ( enterTries.getText().toString().length() > 0 ) {
                                int trys = Integer.parseInt(enterTries.getText().toString());
                                if ( trys > 0 ) {
                                    int day = 0;
                                    int hour = 0;
                                    if ( enterDay.getText().toString().length() > 0 || enterHour.getText().toString().length() > 0 ) {
                                        if ( enterDay.getText().toString().length() > 0) {
                                            day = Integer.parseInt(enterDay.getText().toString());
                                        }
                                        if ( enterHour.getText().toString().length() > 0) {
                                            hour = Integer.parseInt(enterHour.getText().toString());
                                        }
                                        long duration = ( day * ( (long) MILLISECONDS_IN_DAY) ) + ( hour * ( (long) MILLISECONDS_IN_HOUR) );
                                        Date now = new Date();
                                        //                            long nowInMilliseconds = now.getTime();
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
                                    Toast toast = Toast.makeText(getActivity(),R.string.greater_zero,Toast.LENGTH_SHORT);
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
                    }
                });
                number_of_tries_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_tries_help).create().show();
                    }
                });
                number_of_days_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_days_help).create().show();
                    }
                });
                number_of_hours_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_hours_help).create().show();
                    }
                });
            }
        });
        return alertDialog;
    }
}
