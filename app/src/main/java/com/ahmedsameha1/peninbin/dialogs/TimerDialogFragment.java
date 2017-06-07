package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 7/21/2015.
 */
public class TimerDialogFragment extends DialogFragment {
    public static final String MESSAGE = "message";
    private Tracker mTracker;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        message = getArguments().getString(MESSAGE);
        mTracker = application.getDefaultTracker();
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setView(inflater.inflate(R.layout.timer_dialog_fragment, null))
                        .setTitle(R.string.timer)
                        .setPositiveButton(R.string.start, null).create();
        alertDialog.setOnShowListener(dialog -> {
            final EditText enter_number_of_minutes = (EditText) alertDialog.findViewById(R.id.number_of_minutes);
            Button start = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            enter_number_of_minutes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string.minute);
                    if ("".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix)) {

                    } else if (string.equals(String.valueOf(0)) || !string.matches("[0-9]+")) {
                        enter_number_of_minutes.setText("");
                    } else {
                        string = string + suffix;
                        enter_number_of_minutes.setText(string);
                        enter_number_of_minutes.setSelection(string.length() - suffix.length());
                    }
                }
            });
            start.setOnClickListener(view -> {
                try {
                    String minute_suffix = " " + getActivity().getString(R.string.minute);
                    String minute_string = enter_number_of_minutes.getText().toString();
                    if (minute_string.length() > 0) {
                        int minutes = Integer.parseInt(minute_string.substring(0, minute_string.length() - minute_suffix.length()));
                        Intent intent = new Intent(AlarmClock.ACTION_SET_TIMER)
                                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                                .putExtra(AlarmClock.EXTRA_LENGTH, minutes * 60)
                                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                        startActivity(intent);
                        ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(true);
                        dismiss();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Utilities")
                                .setAction(ActivityUtility.getVersionCode(getActivity()) + "start timer")
                                .build());
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.enter_number_of_minutes, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } catch (NumberFormatException nfe) {
                    Toast toast = Toast.makeText(getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            });
            alertDialog.setOnKeyListener((dialog1, keyCode, event) -> {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(true);
                    return false;
                }
                return false;

            });
            ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(false);
        });
        return alertDialog;
    }
}



