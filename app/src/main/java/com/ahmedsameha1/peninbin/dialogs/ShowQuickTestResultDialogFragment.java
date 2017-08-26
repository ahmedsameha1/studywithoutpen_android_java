package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;

import java.text.NumberFormat;

/**
 * Created by ahmed on 7/22/2015.
 */
public class ShowQuickTestResultDialogFragment extends DialogFragment {
    public static final String SHOW_QUICK_TEST_RESULT_DIALOG_FRAGMENT = "showQuickTestResultDialogFragment";
    private int questionsNumber, wrongAnswersNumber;
    private double correctAnswersPercent;
    private String message;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionsNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_QUESTIONS);
        wrongAnswersNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_WRONG_ANSWERS);
        correctAnswersPercent = ((double) (questionsNumber - wrongAnswersNumber)) /questionsNumber;
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        message = getString(R.string.Your_correct_answers_was) + " " + numberFormat.format(correctAnswersPercent)+ " " +
                getString(R.string.of_questions);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage(message)
                .setTitle(R.string.quick_test_result)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowQuickTestResultDialogFragment.this.getActivity().finish();
                        ExitActivity.exit(ShowQuickTestResultDialogFragment.this.getActivity());
                    }
                })
                .create();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ShowQuickTestResultDialogFragment.this.getActivity().finish();
                    ExitActivity.exit(ShowQuickTestResultDialogFragment.this.getActivity());
                    return false;
                }

                return true;

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

}
