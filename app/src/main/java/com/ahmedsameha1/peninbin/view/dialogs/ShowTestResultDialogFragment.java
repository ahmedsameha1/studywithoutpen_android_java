package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.view.activities.WrongAnswersActivity;
import com.ahmedsameha1.peninbin.adapters.WrongItem;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ExitActivity;
import com.ahmedsameha1.peninbin.utils.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ahmed on 7/22/2015.
 */
public class ShowTestResultDialogFragment extends DialogFragment {
    public static final String SHOW_TEST_RESULT_DIALOG_FRAGMENT = "showTestResultDialogFragment";
    public static final String TEST_TYPE = "test_type";
    private int wrongAnswersNumber;
    private String message;
    private long current_field_id;
    private int question_data_type;
    ArrayList<WrongItem> wrongItemList;
    private int test_type;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(getActivity());
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        int questionsNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_QUESTIONS);
        wrongAnswersNumber = getArguments().getInt(EnhancedTestActivity.NUMBER_OF_WRONG_ANSWERS);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        test_type = getArguments().getInt(TEST_TYPE);
        double correctAnswersPercent = ((double) (questionsNumber - wrongAnswersNumber)) / questionsNumber;
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(1);
        message = getString(R.string.Your_correct_answers_was) + " " + numberFormat.format(correctAnswersPercent)+ " " +
                getString(R.string.of_questions);
        question_data_type = databaseOperations.getQuestionDataType(current_field_id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setMessage(message)
                .setTitle(R.string.test_result)
                .setPositiveButton(R.string.wrong_answers, null )
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowTestResultDialogFragment.this.getActivity().finish();
                        ExitActivity.exit(ShowTestResultDialogFragment.this.getActivity());
                    }
                })
                .create();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ShowTestResultDialogFragment.this.getActivity().finish();
                    ExitActivity.exit(ShowTestResultDialogFragment.this.getActivity());
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
                        bundle.putLong(Constants.CURRENT_FIELD_ID, current_field_id);
                        bundle.putInt(TEST_TYPE, test_type);
                        bundle.putSerializable(WrongAnswersActivity.WRONG_ITEM_LIST, wrongItemList);
                        bundle.putInt(EnhancedTestActivity.PERMITED_TRIES_NUMBER, ShowTestResultDialogFragment.this.getArguments()
                                .getInt(EnhancedTestActivity.PERMITED_TRIES_NUMBER));
                        Intent intent = new Intent(ShowTestResultDialogFragment.this.getActivity(), WrongAnswersActivity.class);
                        intent.putExtra(WrongAnswersActivity.WRONG_ITEM_LIST, bundle);
                        ShowTestResultDialogFragment.this.startActivity(intent);
                        ShowTestResultDialogFragment.this.getActivity().finish();
                        ShowTestResultDialogFragment.this.dismiss();
                    }
                });
                if (wrongAnswersNumber <= 0) {
                    b.setEnabled(false);
                }
                if (test_type == Constants.TestType.ENHANCED_TEST) {
                    mySQLiteCursorLoader.resetEntriesTestOrderEnhancedTest(current_field_id, question_data_type);
                } else if (test_type == Constants.TestType.FULLY_RANDOM_TEST) {
                    mySQLiteCursorLoader.resetEntriesTestOrderFullyRandomTest(current_field_id, question_data_type);
                }
            }
        });

        return alertDialog;
    }

    public interface Contact {
        public void deleteField();
    }
}
