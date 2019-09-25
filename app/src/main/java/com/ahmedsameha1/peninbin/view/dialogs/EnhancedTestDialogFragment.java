package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Money;

/**
 * Created by ahmed on 2/8/2016.
 */
public class EnhancedTestDialogFragment extends DialogFragment {
    public static final String ENHANCED_TEST_DIALOG_FRAGMENT = "enhanced_test_dialog_fragment";
    private long current_field_id;
    private boolean from_notification;

    private String number_suffix;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        from_notification = getArguments().getBoolean(ShowNotificationDialogsActivity.FROM_NOTIFICATION, false);
        new CursorLoaderAndUpdateDatabase(getActivity()).clearThisFieldFromWrongAnswersEnhancedTest(current_field_id);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_enhanced_test)
                        .setView(inflater.inflate(R.layout.enhanced_test_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enterTries = (EditText) alertDialog.findViewById(R.id.number_of_tries);
                final EditText enterNumber = (EditText) alertDialog.findViewById(R.id.number);
                final Spinner number_or_percentage = (Spinner) alertDialog.findViewById(R.id.number_or_percentage);
                final Spinner controlled_portion = (Spinner) alertDialog.findViewById(R.id.controlled_portion);
                final Spinner uncontrolled_portion = (Spinner) alertDialog.findViewById(R.id.uncontrolled_portion);
                final Spinner rank = (Spinner) alertDialog.findViewById(R.id.spinner_rank);
                final Spinner wrongness = (Spinner) alertDialog.findViewById(R.id.spinner_wrongness_percentage);
                final CheckBox control_test_entries = (CheckBox) alertDialog.findViewById(R.id.control_test_entries);
                final LinearLayout control_options = (LinearLayout) alertDialog.findViewById(R.id.control_options);

                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
                final ImageButton percentage_or_number_help = (ImageButton) alertDialog.findViewById(R.id.percentage_or_number_help);
                ArrayAdapter<String> number_or_percentage_array_adapter = new ArrayAdapter<String>(EnhancedTestDialogFragment.this.getActivity(),
                        android.R.layout.simple_spinner_item, EnhancedTestDialogFragment.this.getResources().getStringArray(R.array.number_or_percentage));
                number_or_percentage.setAdapter(number_or_percentage_array_adapter);
                number_or_percentage_array_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                number_or_percentage.setSelection(0);
                number_or_percentage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String number_string = enterNumber.getText().toString();
                        if (i == 0) {
                            enterNumber.setHint(R.string.enter_number_of_questions);
                            if (number_suffix != null) {
                                enterNumber.setText(number_string.substring(0, number_string.length() - number_suffix.length()) +
                                        " " + getActivity().getString(R.string._question));
                            }
                        } else {
                            enterNumber.setHint(R.string.enter_percentage);
                            if (number_suffix != null) {
                                enterNumber.setText(number_string.substring(0, number_string.length() - number_suffix.length()) +
                                        " %");
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                control_test_entries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            control_options.setVisibility(View.VISIBLE);
                        } else {
                            control_options.setVisibility(View.GONE);
                        }
                    }
                });
                uncontrolled_portion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            controlled_portion.setSelection(5);
                        } else if (position == 1) {
                            controlled_portion.setSelection(4);
                        } else if (position == 2) {
                            controlled_portion.setSelection(3);
                        } else if (position == 3) {
                            controlled_portion.setSelection(2);
                        } else if (position == 4) {
                            controlled_portion.setSelection(1);
                        } else if (position == 5) {
                            controlled_portion.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                controlled_portion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            uncontrolled_portion.setSelection(5);
                        } else if (position == 1) {
                            uncontrolled_portion.setSelection(4);
                        } else if (position == 2) {
                            uncontrolled_portion.setSelection(3);
                        } else if (position == 3) {
                            uncontrolled_portion.setSelection(2);
                        } else if (position == 4) {
                            uncontrolled_portion.setSelection(1);
                        } else if (position == 5) {
                            uncontrolled_portion.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                rank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0 && wrongness.getSelectedItemPosition() == 0) {
                            wrongness.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                wrongness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0 && rank.getSelectedItemPosition() == 0) {
                            rank.setSelection(1);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                number_of_tries_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EnhancedTestDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_tries_help).create().show();/*(textView).create().show();*/

                    }
                });
                percentage_or_number_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(EnhancedTestDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.percentage_or_number_help).create().show();
                    }
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
                        if ("".equals(string) || string.matches("0*[1-9]+[0-9]*" + suffix)) {

                        } else if (string.equals(String.valueOf(0)) || !string.matches("[0-9]+")) {
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
                        if (number_or_percentage.getSelectedItemPosition() == 0) {
                            number_suffix = " " + getActivity().getString(R.string._question);
                        } else {
                            number_suffix = " %";
                        }
                        if ("".equals(string) || string.matches("0*[1-9]+[0-9]*" + number_suffix)) {

                        } else if (string.equals(String.valueOf(0)) || !string.matches("[0-9]+")) {
                            enterNumber.setText("");
                        } else {
                            string = string + number_suffix;
                            enterNumber.setText(string);
                            enterNumber.setSelection(string.length() - number_suffix.length());
                        }
                    }
                });
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Money.getUserSubscriptionStatus(EnhancedTestDialogFragment.this.getActivity()) || Money.getUserAllowedDueAnError(EnhancedTestDialogFragment.this.getActivity()) ) {
                            try {
                                String trys_suffix = " " + EnhancedTestDialogFragment.this.getActivity().getString(R.string._try);
                                String try_string = enterTries.getText().toString();
                                String number_string = enterNumber.getText().toString();
                                int trys;
                                if (try_string.length() > 0) {
                                    trys = Integer.parseInt(try_string.substring(0, try_string.length() - trys_suffix.length()));
                                } else {
                                    trys = 1;
                                }
                                if (number_string.length() > 0) {
                                    Double number = Double.parseDouble(number_string.substring(0, number_string.length() - number_suffix.length()));
                                    if (number_or_percentage.getSelectedItemPosition() == 1) {
                                        if (number <= 100 && number > 0) {
                                            Intent intent = new Intent(EnhancedTestDialogFragment.this.getActivity(), EnhancedTestActivity.class);
                                            intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                                            intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, number);
                                            intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                            intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                            intent.putExtra(EnhancedTestActivity.LAST_SESSION_OR_NEW_ONE, false);
                                            if (control_test_entries.isChecked()) {
                                                intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, controlled_portion.getSelectedItemPosition());
                                                intent.putExtra(EnhancedTestActivity.RANK_LEVEL, rank.getSelectedItemPosition());
                                                intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, wrongness.getSelectedItemPosition());
                                            }
                                            EnhancedTestDialogFragment.this.startActivity(intent);
                                            if (from_notification) {
                                                EnhancedTestDialogFragment.this.getActivity().finish();
                                            }
                                            EnhancedTestDialogFragment.this.dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(), EnhancedTestDialogFragment.this.getString(R.string.percentage_between), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP, 0, 400);
                                            toast.show();
                                        }
                                    } else {
                                        DatabaseReadOperations databaseOperations = new DatabaseReadOperations(EnhancedTestDialogFragment.this.getActivity());
                                        int currentNumberOfEntries =
                                                databaseOperations.getEntriesNumberForTests(current_field_id, databaseOperations.getQuestionDataType(current_field_id));
                                        int numberInt = Double.valueOf(number).intValue();
                                        if (numberInt > 0 && numberInt <= currentNumberOfEntries) {
                                            Intent intent = new Intent(EnhancedTestDialogFragment.this.getActivity(), EnhancedTestActivity.class);
                                            intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                                            intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, numberInt);
                                            intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                            intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                            intent.putExtra(EnhancedTestActivity.LAST_SESSION_OR_NEW_ONE, false);
                                            if (control_test_entries.isChecked()) {
                                                intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, controlled_portion.getSelectedItemPosition());
                                                intent.putExtra(EnhancedTestActivity.RANK_LEVEL, rank.getSelectedItemPosition());
                                                intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, wrongness.getSelectedItemPosition());
                                            }
                                            EnhancedTestDialogFragment.this.startActivity(intent);
                                            if (from_notification) {
                                                EnhancedTestDialogFragment.this.getActivity().finish();
                                            }
                                            EnhancedTestDialogFragment.this.dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(),
                                                    EnhancedTestDialogFragment.this.getString(R.string.number_of_questions_smaller_than, currentNumberOfEntries), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP, 0, 400);
                                            toast.show();
                                        }
                                    }
                                } else {
                                    if (number_or_percentage.getSelectedItemPosition() == 1) {
                                        Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(), R.string.please_enter_percentage, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    } else {
                                        Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(), R.string.please_enter_number_of_question, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                }
                            } catch (NumberFormatException e) {
                                Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(EnhancedTestDialogFragment.this.getActivity(), R.string.please_subscribe, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
                enterNumber.requestFocus();
            }
        });
        return alertDialog;
    }
}
