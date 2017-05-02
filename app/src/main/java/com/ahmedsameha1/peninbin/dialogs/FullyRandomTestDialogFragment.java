package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Money;

/**
 * Created by a on 7/25/16.
 */
public class FullyRandomTestDialogFragment extends DialogFragment {
    public static final String FULLY_RANDOM_TEST_DIALOG_FRAGMENT = "fully_random_test_dialog_fragment";
    private long current_field_id;
    private boolean from_notification;
    private String number_suffix;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION, false);
        new CursorLoaderAndUpdateDatabase(getActivity()).clearThisFieldFromWrongAnswersFullyRandomTest(current_field_id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_a_test)
                        .setView(inflater.inflate(R.layout.fully_random_test_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(dialog -> {
            final EditText enterTries = (EditText) alertDialog.findViewById(R.id.number_of_tries);
            final EditText enterNumber = (EditText) alertDialog.findViewById(R.id.number);
            final Spinner number_or_percentage = (Spinner) alertDialog.findViewById(R.id.number_or_percentage);
//                final ToggleButton percentage_number = (ToggleButton) alertDialog.findViewById(R.id.percentage_number);
            final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
            final ImageButton percentage_or_number_help = (ImageButton) alertDialog.findViewById(R.id.percentage_or_number_help);
            ArrayAdapter<String> number_or_percentage_array_adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.number_or_percentage));
//                final ImageButton enter_number_help = (ImageButton) alertDialog.findViewById(R.id.enter_number_help);
//                final ImageButton number_of_questions_help = (ImageButton) alertDialog.findViewById(R.id.number_of_questions_help);
            number_or_percentage.setAdapter(number_or_percentage_array_adapter);
            number_or_percentage_array_adapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);
            number_or_percentage.setSelection(0);
            number_or_percentage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String number_string = enterNumber.getText().toString();
                    if ( i == 0 ) {
                        enterNumber.setHint(R.string.enter_number_of_questions);
                        if ( number_suffix != null ) {
                            enterNumber.setText(number_string.substring(0, number_string.length() - number_suffix.length()) +
                                    " " + getActivity().getString(R.string._question));
                        }
                    } else {
                        enterNumber.setHint(R.string.enter_percentage);
                        if ( number_suffix != null ) {
                            enterNumber.setText(number_string.substring(0, number_string.length() - number_suffix.length()) +
                                    " %" );
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            number_of_tries_help.setOnClickListener(v -> {
                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.enter_number_of_tries_help).create().show();/*(textView).create().show();*/

            });
            percentage_or_number_help.setOnClickListener(v -> {

                AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                number_of_tries_help_dialog_builder
                        .setMessage(R.string.percentage_or_number_help).create().show();
            });
            enterTries.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    String suffix = " " + getActivity().getString(R.string._try);
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enterTries.setText("");
                    } else {
                        string = string + suffix;
                        enterTries.setText(string);
                        enterTries.setSelection(string.length() - suffix.length());
                    }
                }
            });
            enterNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String string = editable.toString();
                    if ( number_or_percentage.getSelectedItemPosition() == 0) {
                        number_suffix = " " + getActivity().getString(R.string._question);
                    } else {
                        number_suffix = " %";
                    }
                    if ( "".equals(string) || string.matches("0*[1-9]+[0-9]*" + number_suffix) ) {

                    } else if ( string.equals(String.valueOf(0))   || !string.matches("[0-9]+") ) {
                        enterNumber.setText("");
                    } else {
                        string = string + number_suffix;
                        enterNumber.setText(string);
                        enterNumber.setSelection(string.length() - number_suffix.length());
                    }
                }
            });
            positiveButton.setOnClickListener(v -> {
                if (Money.getUserSubscriptionStatus(getActivity()) || Money.getUserAllowedDueAnError(getActivity())) {
                try {
                    String trys_suffix = " " + getActivity().getString(R.string._try);
                    String try_string = enterTries.getText().toString();
                    String number_string = enterNumber.getText().toString();
                    if ( try_string.length() > 0) {
                        int trys = Integer.parseInt(try_string.substring(0,try_string.length() - trys_suffix.length()));
                            if ( number_string.length() > 0) {
                                Double number = Double.parseDouble(number_string.substring(0, number_string.length() - number_suffix.length()));
                                if (number_or_percentage.getSelectedItemPosition() == 1) {
                                    if (number <= 100 && number > 0) {
                                        Intent intent = new Intent(getActivity(), FullyRandomTestActivity.class);
                                        intent.putExtra(FullyRandomTestActivity.PERCENTAGE_OR_NUMBER, FullyRandomTestActivity.PERCENTAGE);
                                        intent.putExtra(FullyRandomTestActivity.QUESTION_PERCENTAGE, number);
                                        intent.putExtra(FullyRandomTestActivity.PERMITED_TRIES_NUMBER, trys);
                                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                        intent.putExtra(FullyRandomTestActivity.LAST_SESSION_OR_NEW_ONE, false);
                                        startActivity(intent);
                                        if ( from_notification ) {
                                            getActivity().finish();
                                        }
                                        dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(getActivity(), getString(R.string.percentage_between), Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                } else {
                                    DatabaseReadOperations databaseOperations = new DatabaseReadOperations(getActivity());
                                    int currentNumberOfEntries =
                                            databaseOperations.getEntriesNumberForTests(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                                    int numberInt = Double.valueOf(number).intValue();
                                    if ( numberInt > 0 && numberInt <= currentNumberOfEntries) {
                                        Intent intent = new Intent(getActivity(), FullyRandomTestActivity.class);
                                        intent.putExtra(FullyRandomTestActivity.PERCENTAGE_OR_NUMBER, FullyRandomTestActivity.NUMBER);
                                        intent.putExtra(FullyRandomTestActivity.QUESTION_NUMBER, numberInt);
                                        intent.putExtra(FullyRandomTestActivity.PERMITED_TRIES_NUMBER, trys);
                                        intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                        intent.putExtra(FullyRandomTestActivity.LAST_SESSION_OR_NEW_ONE, false);
                                        startActivity(intent);
                                        if ( from_notification ) {
                                            getActivity().finish();
                                        }
                                        dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(getActivity(),
                                                getString(R.string.number_of_questions_smaller_than, currentNumberOfEntries), Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                }
                            } else {
                                if ( number_or_percentage.getSelectedItemPosition() == 1 ){
                                    Toast toast = Toast.makeText(getActivity(), R.string.please_enter_percentage, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(getActivity(), R.string.please_enter_number_of_question, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
                                }
                            }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.enter_tries_number, Toast.LENGTH_SHORT);
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
                } else {
                    Toast toast = Toast.makeText(getActivity(), R.string.please_subscribe, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            });
        });
        return alertDialog;
    }
}
