package com.ahmedsameha1.peninbin.view.dialogs;


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
import android.widget.ImageButton;
import android.widget.RadioGroup;

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
 * Created by ahmed on 2/8/2016.
 */
public class ChangeTextSizeDialogFragment extends DialogFragment {
    public static final int LARGE_SIZE = 20;
    public static final int NORMAL_SIZE = 15;
    public static final int SMALL_SIZE = 10;
    public static final int SAVE_LARGE_SIZE = 2;
    public static final int SAVE_NORMAL_SIZE = 0;
    public static final int SAVE_SMALL_SIZE = 1;
    private int textSize;
    private long current_field_id;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        textSize = (int) getArguments().getFloat(StudyAPeriodActivity.TEXT_SIZE);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
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
                builder.setTitle(R.string.select_text_size)
                        .setView(inflater.inflate(R.layout.change_text_size_dialog_fragment,null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton text_size_help = (ImageButton) alertDialog.findViewById(R.id.text_size_help);
                final RadioGroup radioGroup = (RadioGroup) alertDialog.findViewById(R.id.text_size_radio_group);
                switch (textSize) {
                    case LARGE_SIZE:
                        radioGroup.check(R.id.large_size);
                        break;
                    case NORMAL_SIZE:
                        radioGroup.check(R.id.normal_size);
                        break;
                    case SMALL_SIZE:
                        radioGroup.check(R.id.small_size);
                        break;
                }
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.large_size:
                                ((ChangeTextSize) ChangeTextSizeDialogFragment.this.getActivity()).changeTextSize((float) LARGE_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id, SAVE_LARGE_SIZE);
                                break;
                            case R.id.normal_size:
                                ((ChangeTextSize) ChangeTextSizeDialogFragment.this.getActivity()).changeTextSize((float) NORMAL_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id, SAVE_NORMAL_SIZE);
                                break;
                            case R.id.small_size:
                                ((ChangeTextSize) ChangeTextSizeDialogFragment.this.getActivity()).changeTextSize((float) SMALL_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id, SAVE_SMALL_SIZE);
                                break;
                        }
                        ((ActivityUtility.ActivityTimers) ChangeTextSizeDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                        ChangeTextSizeDialogFragment.this.dismiss();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Utilities")
                                .setAction((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(ChangeTextSizeDialogFragment.this.getActivity()) + "change text size")
                                .build());
                    }
                });
                text_size_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(ChangeTextSizeDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.text_size_help).create().show();
                    }
                });
                ((ActivityUtility.ActivityTimers) ChangeTextSizeDialogFragment.this.getActivity()).manageTimersDueDialogsShow(false);
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog1, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            ((ActivityUtility.ActivityTimers) ChangeTextSizeDialogFragment.this.getActivity()).manageTimersDueDialogsShow(true);
                            return false;
                        }
                        return false;

                    }
                });
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
    public interface ChangeTextSize {
        void changeTextSize(float textSize);
    }
}
