package com.ahmedsameha1.peninbin.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.StudyDuringActivity;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
/**
 * Created by ahmed on 2/7/2016.
 */
public class ChangeAreasDialogFragment extends DialogFragment{
    public static final String CHANGE_AREAS_DIALOG_FRAGMENT = "change_areas_dialog_fragment";
    private float initialQuestionArea,initialAnsweArea;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private long current_field_id;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        initialQuestionArea = getArguments().getFloat(StudyDuringActivity.QUESTION_LAYOUT);
        initialAnsweArea = getArguments().getFloat(StudyDuringActivity.ANSWER_LAYOUT);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setTitle(R.string.repartition_the_area)
                .setView(inflater.inflate(R.layout.change_areas_dialog_fragment, null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final ImageButton question_area_help = (ImageButton) alertDialog.findViewById(R.id.question_area_help);
                final ImageButton answer_area_help = (ImageButton) alertDialog.findViewById(R.id.answer_area_help);
                final EditText enterQuestionArea = (EditText) alertDialog.findViewById(R.id.questio_area);
                final EditText enterAnswerArea = (EditText) alertDialog.findViewById(R.id.answer_area);
                enterQuestionArea.setText(String.format("%d", (int) initialQuestionArea));
                enterAnswerArea.setText(String.format("%d", (int) initialAnsweArea));
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if ( enterQuestionArea.getText().toString().length() > 0 && enterAnswerArea.getText().toString().length() > 0 ) {
                                int questionArea = Integer.parseInt(enterQuestionArea.getText().toString());
                                int answerArea = Integer.parseInt(enterAnswerArea.getText().toString());
                                if ( questionArea > 0 && answerArea > 0 ) {
                                    ((ChangeAreas) getActivity()).changeAreas((float) questionArea,(float) answerArea);
                                    mySQLiteCursorLoader.updateFieldAreasSize(current_field_id,questionArea,answerArea);
                                    dismiss();
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Utilities")
                                            .setAction("change areas size")
                                            .build());
                                } else {
                                    Toast toast = Toast.makeText(getActivity(), R.string.greater_than_zero, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
                                }
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
                    }
                });
                question_area_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.question_area_help).create().show();
                    }
                });
                answer_area_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.answer_area_help).create().show();
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface ChangeAreas {
        void changeAreas( float questionArea, float answerArea );
    }
}
