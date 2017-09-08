package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 7/21/2015.
 */
public class AddFieldNoteDialogFragment extends DialogFragment  {
    public static final String ADD_FIELD_NOTE_DIALOG_FRAGMENT = "add_field_note_dialog_fragment";

    private Tracker mTracker;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        setRetainInstance(true);
    }
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
        builder.setView(inflater.inflate(R.layout.add_field_note_dialog_fragment,null))
                .setTitle(R.string.add_field_note)
                .setPositiveButton(R.string.add_field_note, null)
                .setNegativeButton(R.string.cancel, null).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enter_note = (EditText) alertDialog.findViewById(R.id.note);
                Button ok = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String note = enter_note.getText().toString();
                        if (note.length() > 0) {
                            ((Contact) AddFieldNoteDialogFragment.this.getActivity()).addFieldNote(note);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                    .setAction((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(AddFieldNoteDialogFragment.this.getActivity()) + "add field note")
                                    .build());
                            AddFieldNoteDialogFragment.this.dismiss();
                        } else {
                            Toast toast = Toast.makeText(AddFieldNoteDialogFragment.this.getActivity(), R.string.where_is_note, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface Contact {
        public void addFieldNote(String note);
    }
}



