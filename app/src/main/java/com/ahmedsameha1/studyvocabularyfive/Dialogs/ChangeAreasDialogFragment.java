package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 2/7/2016.
 */
public class ChangeAreasDialogFragment extends DialogFragment{
    public static final String CHANGE_AREAS_DIALOG_FRAGMENT = "change_areas_dialog_fragment";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.change_areas_dialog_fragment, null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enterquestionArea = (EditText) alertDialog.findViewById(R.id.questio_area);
                final EditText enteranswerArea = (EditText) alertDialog.findViewById(R.id.answer_area);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int questionArea = Integer.parseInt(enterquestionArea.getText().toString());
                        int answerArea = Integer.parseInt(enteranswerArea.getText().toString());
                        if ( questionArea > 0 && answerArea > 0 ) {
                            ((ChangeAreas) getActivity()).changeAreas(questionArea,answerArea);
                            dismiss();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), R.string.greater_than_zero, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,0);
                            toast.show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface ChangeAreas {
        void changeAreas( int questionArea, int answerArea );
    }
}
