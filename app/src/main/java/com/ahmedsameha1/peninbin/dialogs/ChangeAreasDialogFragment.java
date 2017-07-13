package com.ahmedsameha1.peninbin.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
/**
 * Created by ahmed on 2/7/2016.
 */
public class ChangeAreasDialogFragment extends DialogFragment{
    public static final String CHANGE_AREAS_DIALOG_FRAGMENT = "change_areas_dialog_fragment";
    private float initialQuestionArea,initialAnsweArea;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        initialQuestionArea = getArguments().getFloat(StudyAPeriodActivity.QUESTION_LAYOUT);
        initialAnsweArea = getArguments().getFloat(StudyAPeriodActivity.ANSWER_LAYOUT);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setTitle(R.string.repartition_the_area)
                .setView(inflater.inflate(R.layout.change_areas_dialog_fragment, null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(dialog -> {
            final ImageButton question_area_help = (ImageButton) alertDialog.findViewById(R.id.question_area_help);
            final ImageButton answer_area_help = (ImageButton) alertDialog.findViewById(R.id.answer_area_help);
            final EditText enterQuestionArea = (EditText) alertDialog.findViewById(R.id.questio_area);
            final EditText enterAnswerArea = (EditText) alertDialog.findViewById(R.id.answer_area);
            enterQuestionArea.setText(String.format("%d", (int) initialQuestionArea));
            enterAnswerArea.setText(String.format("%d", (int) initialAnsweArea));
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                try {
                    if ( enterQuestionArea.getText().toString().length() > 0 && enterAnswerArea.getText().toString().length() > 0 ) {
                        int questionArea = Integer.parseInt(enterQuestionArea.getText().toString());
                        int answerArea = Integer.parseInt(enterAnswerArea.getText().toString());

                            ((ChangeAreas) getActivity()).changeAreas((float) questionArea,(float) answerArea);
                            mySQLiteCursorLoader.updateFieldAreasSize(current_field_id,questionArea,answerArea);
                        ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(true);
                            dismiss();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Utilities")
                                    .setAction(ActivityUtility.getVersionCode(getActivity()) + "change areas size")
                                    .build());
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.enter_question_area_answer_area, Toast.LENGTH_SHORT);
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
            });
            alertDialog.setOnKeyListener((dialog1, keyCode, event) -> {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(true);
                    return false;
                }
                return false;

            });
            enterQuestionArea.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if ( editable.toString().equals(String.valueOf(0))) {
                        enterQuestionArea.setText("");
                    }
                }
            });
            enterAnswerArea.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if ( editable.toString().equals(String.valueOf(0))) {
                        enterAnswerArea.setText("");
                    }
                }
            });
            question_area_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.question_area_help).create().show();
            });
            answer_area_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.answer_area_help).create().show();
            });
            ((ActivityUtility.ActivityTimers) getActivity()).manageTimersDueDialogsShow(false);
        });
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
    public interface ChangeAreas {
        void changeAreas( float questionArea, float answerArea );
    }
}
