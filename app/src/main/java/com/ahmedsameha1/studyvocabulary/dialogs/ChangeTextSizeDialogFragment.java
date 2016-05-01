package com.ahmedsameha1.studyvocabulary.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.StudyActivity;
import com.ahmedsameha1.studyvocabulary.activities.StudyDuringActivity;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

/**
 * Created by ahmed on 2/8/2016.
 */
public class ChangeTextSizeDialogFragment extends DialogFragment {
    public static final String CHANGE_TEXT_SIZE_DIALOG_FRAGMENT = "changeTextSizeDialogFragment";
    public static final int LARGE_SIZE = 20;
    public static final int NORMAL_SIZE = 15;
    public static final int SMALL_SIZE = 10;
    public static final int SAVE_LARGE_SIZE = 2;
    public static final int SAVE_NORMAL_SIZE = 0;
    public static final int SAVE_SMALL_SIZE = 1;
    private int textSize;
    private long current_field_id;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        textSize = (int) getArguments().getFloat(StudyDuringActivity.TEXT_SIZE);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
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
                    public void onClick(View v) {
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.large_size:
                                ((ChangeTextSize)getActivity()).changeTextSize((float)LARGE_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id,SAVE_LARGE_SIZE);
                                break;
                            case R.id.normal_size:
                                ((ChangeTextSize)getActivity()).changeTextSize((float)NORMAL_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id,SAVE_NORMAL_SIZE);
                                break;
                            case R.id.small_size:
                                ((ChangeTextSize)getActivity()).changeTextSize((float) SMALL_SIZE);
                                mySQLiteCursorLoader.updateFieldTestTextSize(current_field_id,SAVE_SMALL_SIZE);
                                break;
                        }
                        dismiss();
                    }
                });
                text_size_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.text_size_help).create().show();
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface ChangeTextSize {
        void changeTextSize(float textSize);
    }
}
