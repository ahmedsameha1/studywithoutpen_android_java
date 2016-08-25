package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;

/**
 * Created by a on 7/25/16.
 */
public class CopyFieldDialogFragment extends DialogFragment {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";
    public static final String COPY_FIELD_DIALOG_FRAGMENT = "copy_field_dialog_fragment";
    private long current_field_id;
    private boolean from_notification;
    private DatabaseOperations databaseOperations;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        databaseOperations = new DatabaseOperations(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder
                        .setView(inflater.inflate(R.layout.copy_field_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final RadioButton new_field = (RadioButton) alertDialog.findViewById(R.id.new_field);
                final RadioButton existing_field = (RadioButton) alertDialog.findViewById(R.id.existing_field);
                final EditText enter_new_field_name = (EditText) alertDialog.findViewById(R.id.new_field_name);
                final Spinner existing_fields = (Spinner) alertDialog.findViewById(R.id.existing_fields);
                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,databaseOperations.getFieldList(),
                        new String[]{Contract.Field.COLUMN_FIELD_NAME}, new int[]{android.R.id.text1},0);
                simpleCursorAdapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                existing_fields.setAdapter(simpleCursorAdapter);
                new_field.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if ( checked ) {
                            enter_new_field_name.setVisibility(View.VISIBLE);
                            existing_fields.setVisibility(View.GONE);
                        }
                    }
                });
                existing_field.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if ( checked ) {
                            existing_fields.setVisibility(View.VISIBLE);
                            enter_new_field_name.setVisibility(View.GONE);
                        }
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*try {
                            if (enterTries.getText().toString().length() > 0) {
                                int trys = Integer.parseInt(enterTries.getText().toString());
                                if (trys > 0) {
                                    if ( enterNumber.getText().toString().length() > 0) {
                                        Double number = Double.parseDouble(enterNumber.getText().toString());
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
                                            DatabaseOperations databaseOperations = new DatabaseOperations(getActivity());
                                            int currentNumberOfEntries =
                                                    databaseOperations.getEntriesNumber(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
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
                                    Toast toast = Toast.makeText(getActivity(), R.string.greater_zero, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP,0,400);
                                    toast.show();
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
                        }*/
                    }
                });
            }
        });
        return alertDialog;
    }
}
