package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ahmed on 9/23/2015.
 */
public class StudyEntriesFromDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setView(inflater.inflate(R.layout.study_entries_from_dialog_fragment,null))
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
                        if ( enterDay.length() > 0 && enterHour.length() > 0 ) {
                            int day = Integer.parseInt(enterDay.getText().toString());
                            int hour = Integer.parseInt(enterHour.getText().toString());
                            if ( day <= 0 && hour <= 0)
                        } else {
                            Toast.makeText(getActivity(),)
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
}
