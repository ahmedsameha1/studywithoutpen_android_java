package com.ahmedsameha1.peninbin.dialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.WrongAnswersActivity;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ahmed on 7/22/2015.
 */
public class ShowTestResultDialogFragment extends DialogFragment {
    public static final String SHOW_TEST_RESULT_DIALOG_FRAGMENT = "showTestResultDialogFragment";
    private int questionsNumber, wrongAnswersNumber;
    private double correctAnswersPercent;
    private String message;
    private long current_field_id;
    private int question_data_type;
    ArrayList<WrongItem> wrongItemList;
    private DatabaseOperations databaseOperations;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseOperations = new DatabaseOperations(getActivity());
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
/*        wrongItemList = (ArrayList<WrongItem>) getArguments()
                .getSerializable(WrongAnswersActivity.WRONG_ITEM_LIST);*/
        questionsNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_QUESTIONS);
        wrongAnswersNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_WRONG_ANSWERS);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        correctAnswersPercent = ((double) (questionsNumber - wrongAnswersNumber)) /questionsNumber;
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        message = getString(R.string.Your_correct_answers_was) + " " + numberFormat.format(correctAnswersPercent)+ " " +
                getString(R.string.of_questions);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
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
                        mySQLiteCursorLoader.clearThisFieldFromWrongAnswers(current_field_id,question_data_type);
                        getActivity().finish();
                    }
                })
                .create();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mySQLiteCursorLoader.clearThisFieldFromWrongAnswers(current_field_id,question_data_type);
                        getActivity().finish();
                        return false;
                    }

                return true;

            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
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
                mySQLiteCursorLoader.resetEntriesTestOrder(current_field_id,question_data_type);
            }
        });

        return alertDialog;
    }

    public interface Contact {
        public void deleteField();
    }
}
