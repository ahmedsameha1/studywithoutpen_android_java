package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.R;
import com.ahmedsameha1.studyvocabularyfive.TestActivity;

/**
 * Created by ahmed on 2/8/2016.
 */
public class TestDialogFragment extends DialogFragment {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_a_test)
                        .setView(inflater.inflate(R.layout.test_dialog_fragment,null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enterTries = (EditText) alertDialog.findViewById(R.id.number_of_tries);
                final EditText enterNumberOfQuestions = (EditText) alertDialog.findViewById(R.id.number_of_questions);
                final EditText enterQuestionPercentage = (EditText) alertDialog.findViewById(R.id.questions_percentage);
                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final ImageButton number_of_tries_help = (ImageButton)alertDialog.findViewById(R.id.number_of_tries_help);
                final ImageButton one_only_of_help = (ImageButton)alertDialog.findViewById(R.id.one_only_of_help);
                final ImageButton enter_percentage_help = (ImageButton)alertDialog.findViewById(R.id.enter_percentage_help);
                final ImageButton number_of_questions_help = (ImageButton)alertDialog.findViewById(R.id.number_of_questions_help);
                enterQuestionPercentage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if ( enterQuestionPercentage.getText().toString().length() >= 1 ) {
                            enterNumberOfQuestions.setEnabled(false);
                        } else {
                            enterNumberOfQuestions.setEnabled(true);
                        }
                    }
                });
                enterNumberOfQuestions.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (enterNumberOfQuestions.getText().toString().length() >= 1) {
                            enterQuestionPercentage.setEnabled(false);
                        } else {
                            enterQuestionPercentage.setEnabled(true);
                        }
                    }
                });
                number_of_tries_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_tries_help).create().show();/*(textView).create().show();*/

                    }
                });
                one_only_of_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_only_one_of_help).create().show();
                    }
                });
                enter_percentage_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_percentage_help).create().show();
                    }
                });
                number_of_questions_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_questions_help).create().show();
                    }
                });
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( enterTries.getText().toString().length() > 0 ) {
                            int trys = Integer.parseInt(enterTries.getText().toString());
                            if ( trys > 0 ) {
                                if ( enterNumberOfQuestions.getText().toString().length() > 0 || enterQuestionPercentage.getText().toString().length() > 0 ) {
                                    if ( enterQuestionPercentage.getText().toString().length() > 0) {
                                        double percentageQuestion = Double.parseDouble(enterQuestionPercentage.getText().toString());
                                        if ( percentageQuestion <= 100 && percentageQuestion > 0 ) {
                                            Intent intent = new Intent(getActivity(),TestActivity.class);
                                            intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER,TestActivity.PERCENTAGE);
                                            intent.putExtra(TestActivity.QUESTION_PERCENTAGE,percentageQuestion);
                                            intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER,trys);
                                            intent.putExtra(TestActivity.CURRENT_FIELD,((FieldActivity) getActivity()).getCurrentField());
                                            intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE,false);
                                            startActivity(intent);
                                            dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(getActivity(), getString(R.string.percentage_between), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP,0,400);
                                            toast.show();
                                        }
                                    } else if ( enterNumberOfQuestions.getText().toString().length() > 0) {
                                        int numberQuestions = Integer.parseInt(enterNumberOfQuestions.getText().toString());
                                        int currentNumberOfEntries = new DatabaseOperations(getActivity().getContentResolver())
                                                .getEntriesNumber(((FieldActivity) getActivity()).getCurrentField());
                                        if ( numberQuestions <= currentNumberOfEntries) {
                                            Intent intent = new Intent(getActivity(),TestActivity.class);
                                            intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER,TestActivity.NUMBER);
                                            intent.putExtra(TestActivity.QUESTION_NUMBER,numberQuestions);
                                            intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER,trys);
                                            intent.putExtra(TestActivity.CURRENT_FIELD,((FieldActivity) getActivity()).getCurrentField());
                                            intent.putExtra(TestActivity.LAST_SESSION_OR_NEW_ONE,false);
                                            startActivity(intent);
                                            dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(getActivity(),
                                                    getString(R.string.number_of_questions_smaller_than,currentNumberOfEntries),Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP,0,400);
                                            toast.show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), R.string.please_enter_percentage_or_number_of_question, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(),R.string.greater_zero,Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(),R.string.enter_tries_number,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
}
