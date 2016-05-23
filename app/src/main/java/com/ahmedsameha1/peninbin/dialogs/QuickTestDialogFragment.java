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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.ScheduleInFieldReceiver;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;

import java.util.Calendar;

/**
 * Created by ahmed on 2/8/2016.
 */
public class QuickTestDialogFragment extends DialogFragment {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";
    public static final String SCHEDULE_WHAT = "schedule_what";
    public static final int QUICK_TEST = 0;
    public static final String QUICK_TEST_DIALOG = "quick_test_dialog";
    public static final String YEAR = "year";
    public static final String MONTH_OF_YEAR = "month_of_year";
    public static final String DAY_OF_MONTH = "day_of_month";
    public static final String HOUR_OF_DAY = "hour_of_day";
    public static final String MINUTE = "minute";
    private long current_field_id;
    private int year,monthOfYear,dayOfMonth,hourOfDay,minute;
    private AlarmManager alarmManager;

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
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.start_a_quick_test)
                        .setView(inflater.inflate(R.layout.test_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enterTries = (EditText) alertDialog.findViewById(R.id.number_of_tries);
                final EditText enterNumber = (EditText) alertDialog.findViewById(R.id.number);
                final ToggleButton percentage_number = (ToggleButton) alertDialog.findViewById(R.id.percentage_number);
                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
                final ImageButton percentage_or_number_help = (ImageButton) alertDialog.findViewById(R.id.percentage_or_number_help);
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_view);
                enterNumber.setHint(R.string.enter_percentage);
                percentage_number.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked) {
                            enterNumber.setHint(R.string.enter_percentage);
                        } else {
                            enterNumber.setHint(R.string.enter_number_of_questions);
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
                percentage_or_number_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.percentage_or_number_help).create().show();
                    }
                });
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (enterTries.getText().toString().length() > 0) {
                                int trys = Integer.parseInt(enterTries.getText().toString());
                                if (trys > 0) {
                                    if ( enterNumber.getText().toString().length() > 0) {
                                        Double number = Double.parseDouble(enterNumber.getText().toString());
                                        if (!percentage_number.isChecked()) {
                                            if (number <= 100 && number > 0) {
                                                Intent intent = new Intent(getActivity(), ScheduleInFieldReceiver.class);
                                                intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER, TestActivity.PERCENTAGE);
                                                intent.putExtra(TestActivity.QUESTION_PERCENTAGE, number);
                                                intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER, trys);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                                final Calendar calendar_picked_date_time = Calendar.getInstance();
                                                calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                                intent.putExtra(SCHEDULE_WHAT,QUICK_TEST);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                                int request = (int)System.currentTimeMillis();
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
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
                                                Intent intent = new Intent(getActivity(), ScheduleInFieldReceiver.class);
                                                intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER, TestActivity.NUMBER);
                                                intent.putExtra(TestActivity.QUESTION_NUMBER, numberInt);
                                                intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER, trys);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                                                final Calendar calendar_picked_date_time = Calendar.getInstance();
                                                calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                                intent.putExtra(SCHEDULE_WHAT,QUICK_TEST);
                                                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                                                int request = (int)System.currentTimeMillis();
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
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
                                        if ( ! percentage_number.isChecked() ){
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
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
}
