package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;
import com.ahmedsameha1.studyvocabularyfive.TestActivity;
import com.ahmedsameha1.studyvocabularyfive.WrongAnswersActivity;
import com.ahmedsameha1.studyvocabularyfive.WrongItem;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ahmed on 7/22/2015.
 */
public class ShowResultDialogFragment extends DialogFragment {
    public static final String SHOW_RESULT_DIALOG_FRAGMENT = "showResultDialogFragment";
    private int questionsNumber, wrongAnswersNumber;
    private double correctAnswersPercent;
    private String message;
    private String currentField;
    ArrayList<WrongItem> wrongItemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        wrongItemList = (ArrayList<WrongItem>) getArguments()
                .getSerializable(WrongAnswersActivity.WRONG_ITEM_LIST);*/
        questionsNumber = getArguments().getInt(TestActivity.NUMBER_OF_QUESTIONS);
        wrongAnswersNumber = getArguments().getInt(TestActivity.NUMBER_OF_WRONG_ANSWERS);
        currentField = getArguments().getString(TestActivity.CURRENT_FIELD);
        correctAnswersPercent = ((double) (questionsNumber - wrongAnswersNumber)) /questionsNumber;
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        message = getString(R.string.Your_correct_answers_was) + " " + numberFormat.format(correctAnswersPercent)+ " " +
                getString(R.string.of_questions);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage(message)
                .setTitle(R.string.test_result)
                .setPositiveButton(R.string.wrong_answers, null /*new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItemList);
                        Intent intent = new Intent(getActivity(),WrongAnswersActivity.class);
                        intent.putExtra(WrongAnswersActivity.WRONG_ITEM_LIST,bundle);
                        startActivity(intent);
                    }
                }*/)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                        databaseOperations.clearLinkerTableWrongAnswers(currentField);
                        getActivity().finish();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString(TestActivity.CURRENT_FIELD,currentField);
                        bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItemList);
                        Intent intent = new Intent(getActivity(),WrongAnswersActivity.class);
                        intent.putExtra(WrongAnswersActivity.WRONG_ITEM_LIST,bundle);
                        startActivity(intent);
                        getActivity().finish();
                        dismiss();
                    }
                });
                if ( wrongAnswersNumber <= 0 ) {
                    b.setEnabled(false);
                }
            }
        });

        return alertDialog;
    }
    public interface Contact {
        public void deleteField();
    }
}
