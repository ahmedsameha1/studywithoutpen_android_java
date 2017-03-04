package com.ahmedsameha1.peninbin.dialogs;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.receivers_services.ShowScheduledNotificationsReceiver;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.Calendar;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders;
/**
 * Created by ahmed on 2/8/2016.
 */
public class QuickTestDialogFragment extends DialogFragment {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";
    public static final String SCHEDULE_WHAT = "schedule_what";
    public static final String QUICK_TEST_DIALOG = "quick_test_dialog";
    public static final String YEAR = "year";
    public static final String MONTH_OF_YEAR = "month_of_year";
    public static final String DAY_OF_MONTH = "day_of_month";
    public static final String HOUR_OF_DAY = "hour_of_day";
    public static final String MINUTE = "minute";
    private long current_field_id;
    private int year,monthOfYear,dayOfMonth,hourOfDay,minute;
    private AlarmManager alarmManager;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private Tracker mTracker;
    private String number_suffix;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        year = getArguments().getInt(YEAR);
        monthOfYear = getArguments().getInt(MONTH_OF_YEAR);
        dayOfMonth = getArguments().getInt(DAY_OF_MONTH);
        hourOfDay = getArguments().getInt(HOUR_OF_DAY);
        minute = getArguments().getInt(MINUTE);
        alarmManager = (AlarmManager) getActivity().getSystemService(AppCompatActivity.ALARM_SERVICE);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_a_quick_test)
                        .setView(inflater.inflate(R.layout.quick_test_dialog_fragment, null))
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
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_view);
                ArrayAdapter<String> number_or_percentage_array_adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.number_or_percentage));
                number_or_percentage.setAdapter(number_or_percentage_array_adapter);
                number_or_percentage_array_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                number_or_percentage.setSelection(0);
                number_or_percentage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if ( i == 0 ) {
                            enterNumber.setHint(R.string.enter_number_of_questions);
                        } else {
                            enterNumber.setHint(R.string.enter_percentage);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                controlled_portion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ( position == 0 ) {
                            uncontrolled_portion.setSelection(5);
                        } else if ( position == 1 ) {
                            uncontrolled_portion.setSelection(4);
                        } else if ( position == 2 ) {
                            uncontrolled_portion.setSelection(3);
                        } else if ( position == 3 ) {
                            uncontrolled_portion.setSelection(2);
                        } else if ( position == 4 ) {
                            uncontrolled_portion.setSelection(1);
                        } else if ( position == 5 ) {
                            uncontrolled_portion.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                uncontrolled_portion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ( position == 0 ) {
                            controlled_portion.setSelection(5);
                        } else if ( position == 1 ) {
                            controlled_portion.setSelection(4);
                        } else if ( position == 2 ) {
                            controlled_portion.setSelection(3);
                        } else if ( position == 3 ) {
                            controlled_portion.setSelection(2);
                        } else if ( position == 4 ) {
                            controlled_portion.setSelection(1);
                        } else if ( position == 5 ) {
                            controlled_portion.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                control_test_entries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if ( isChecked ) {
                            control_options.setVisibility(View.VISIBLE);
                        } else {
                            control_options.setVisibility(View.GONE);
                        }
                    }
                });
                rank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if ( position == 0 && wrongness.getSelectedItemPosition() == 0 ) {
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
                        if ( position == 0 && rank.getSelectedItemPosition() == 0 ) {
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
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_tries_help).create().show();/*(textView).create().show();*/

                    }
                });
                percentage_or_number_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
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
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String trys_suffix = " " + getActivity().getString(R.string._try);
                            String try_string = enterTries.getText().toString();
                            String number_string = enterNumber.getText().toString();
                            if ( try_string.length() > 0) {
                                int trys = Integer.parseInt(try_string.substring(0,try_string.length() - trys_suffix.length()));
                                if ( number_string.length() > 0) {
                                        int number = Integer.parseInt(number_string.substring(0, number_string.length() - number_suffix.length()));
                                        if (number_or_percentage.getSelectedItemPosition() == 1) {
                                            if (number <= 100 && number > 0) {
                                                Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
                                                intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                                                intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, number);
                                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                                if ( control_test_entries.isChecked() ) {
                                                    intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, controlled_portion.getSelectedItemPosition());
                                                    intent.putExtra(EnhancedTestActivity.RANK_LEVEL, rank.getSelectedItemPosition());
                                                    intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, wrongness.getSelectedItemPosition());
                                                }
                                                final Calendar calendar_picked_date_time = Calendar.getInstance();
                                                calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                                intent.putExtra(SCHEDULE_WHAT,ScheduleInFieldDialogFragment.QUICK_TEST);
                                                intent.putExtra(ScheduleInFieldDialogFragment.TIME,calendar_picked_date_time.getTimeInMillis());
                                                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                                int request = (int)System.currentTimeMillis();
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                                                if ( control_test_entries.isChecked() ) {
                                                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                                                            ScheduleInFieldDialogFragment.QUICK_TEST,calendar_picked_date_time.getTimeInMillis(),request,
                                                            EnhancedTestActivity.PERCENTAGE,number,trys,controlled_portion.getSelectedItemPosition(),
                                                            wrongness.getSelectedItemPosition(),rank.getSelectedItemPosition());
                                                } else {
                                                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                                                            ScheduleInFieldDialogFragment.QUICK_TEST,calendar_picked_date_time.getTimeInMillis(),request,
                                                            EnhancedTestActivity.PERCENTAGE,number,trys, -1, -1, -1);
                                                }
                                                final Snackbar snackbar = Snackbar
                                                        .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snackbar.dismiss();
                                                    }
                                                });
                                                snackbar.show();
                                                dismiss();
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                                                        .setAction("schedule a random quick test")
                                                        .build());
                                            } else {
                                                Toast toast = Toast.makeText(getActivity(), getString(R.string.percentage_between), Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP, 0, 400);
                                                toast.show();
                                            }
                                        } else {
                                            DatabaseReadOperations databaseOperations = new DatabaseReadOperations(getActivity());
                                            int currentNumberOfEntries =
                                                    databaseOperations.getEntriesNumberForTests(current_field_id,databaseOperations.getQuestionDataType(current_field_id));
                                            if ( number > 0 && number <= currentNumberOfEntries) {
                                                Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
                                                intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                                                intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, number);
                                                intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                                if ( control_test_entries.isChecked() ) {
                                                    intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, controlled_portion.getSelectedItemPosition());
                                                    intent.putExtra(EnhancedTestActivity.RANK_LEVEL, rank.getSelectedItemPosition());
                                                    intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, wrongness.getSelectedItemPosition());
                                                }
                                                final Calendar calendar_picked_date_time = Calendar.getInstance();
                                                calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                                intent.putExtra(SCHEDULE_WHAT,ScheduleInFieldDialogFragment.QUICK_TEST);
                                                intent.putExtra(ScheduleInFieldDialogFragment.TIME,calendar_picked_date_time.getTimeInMillis());
                                                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                                int request = (int)System.currentTimeMillis();
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                                                if ( control_test_entries.isChecked() ) {

                                                } else {
                                                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                                                        ScheduleInFieldDialogFragment.QUICK_TEST,calendar_picked_date_time.getTimeInMillis(),request,
                                                        EnhancedTestActivity.NUMBER,number,trys, -1, -1, -1);
                                                }
                                                final Snackbar snackbar = Snackbar
                                                        .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        snackbar.dismiss();
                                                    }
                                                });
                                                snackbar.show();
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
                    }
                });
            }
        });
        return alertDialog;
    }
}
