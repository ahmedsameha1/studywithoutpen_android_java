package com.ahmedsameha1.peninbin.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed on 2/8/2016.
 */
public class ObfuscateQuestionDialogFragment extends DialogFragment {
    public static final String OBFUSCATE_QUESTION_DIALOG_FRAGMENT = "obfuscate_question_dialog_fragment";
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private DatabaseReadOperations databaseReadOperations;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        databaseReadOperations = new DatabaseReadOperations(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        Activity activity = getActivity();
        if ( !(activity instanceof EnhancedTestActivity || activity instanceof FullyRandomTestActivity ||
                activity instanceof QuickTestActivity || activity instanceof StudyAPeriodActivity) ) {
            setRetainInstance(true);
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog alertDialog =
                builder.setTitle(R.string.obfuscate_question)
                        .setView(inflater.inflate(R.layout.obfuscate_question_dialog_fragment,null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton obfuscate_question_help = (ImageButton) alertDialog.findViewById(R.id.obfuscate_question_help);
                final CheckBox radioButton = (CheckBox) alertDialog.findViewById(R.id.obfuscate_question);
                radioButton.setChecked(databaseReadOperations.isObfuscateQuestion(current_field_id));
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mySQLiteCursorLoader.setObfuscateQuestion(current_field_id, radioButton.isChecked());
                        ((ObfuscateText) ObfuscateQuestionDialogFragment.this.getActivity()).obfuscateText(radioButton.isChecked());
                        ((ActivityUtility.ActivityTimers) ObfuscateQuestionDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                        ObfuscateQuestionDialogFragment.this.dismiss();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Utilities")
                                .setAction((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(ObfuscateQuestionDialogFragment.this.getActivity()) + "obfuscate text")
                                .build());
                    }
                });
                obfuscate_question_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(ObfuscateQuestionDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.obfuscate_question_help).create().show();
                    }
                });
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {

                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            ((ActivityUtility.ActivityTimers) ObfuscateQuestionDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                            return false;
                        }
                        return false;

                    }
                });
                ((ActivityUtility.ActivityTimers) ObfuscateQuestionDialogFragment.this.getActivity()).manageTimersDueDialogsShow(false);
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
    public interface ObfuscateText {
        void obfuscateText(boolean obfuscate);
    }
}
