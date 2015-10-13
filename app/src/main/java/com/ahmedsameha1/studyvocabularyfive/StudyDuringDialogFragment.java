package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by ahmed on 9/23/2015.
 */
public class StudyDuringDialogFragment extends DialogFragment {
    public static final String SHOW_STUDY_DURING_DIALOG_FRAGMENT = "showStudyDuringDialogFragment";
    public static final int MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;
    public static final int MILLISECONDS_IN_HOUR = 60 * 60 * 1000;
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.enter_the_period)
                        .setView(inflater.inflate(R.layout.study_during_dialog_fragment,null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText enterDay = (EditText) alertDialog.findViewById(R.id.enter_day);
                        EditText enterHour = (EditText) alertDialog.findViewById(R.id.enter_hour);
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
                            Intent intent = new Intent(getActivity(),StudyDuringActivity.class);
                            intent.putExtra(StudyDuringActivity.CURRENT_FIELD,((FieldActivity) getActivity()).getCurrentField());
                            intent.putExtra(StudyDuringActivity.STARTING_TIME,startingTime);
                            intent.putExtra(StudyDuringActivity.LAST_SESSION_OR_NEW_ONE,false);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(),getString(R.string.please_enter_at_least_one_value),Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }
}
