package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.view.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.view.activities.FieldActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.receivers_services.ShowScheduledNotificationsReceiver;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

/**
 * Created by ahmed on 2/8/2016.
 */
public class QuickTestDialogFragment extends DialogFragment {
    public static final String SCHEDULE_WHAT = "schedule_what";
    public static final String QUICK_TEST_DIALOG = "quick_test_dialog";
    public static final String YEAR = "year";
    public static final String MONTH_OF_YEAR = "month_of_year";
    public static final String DAY_OF_MONTH = "day_of_month";
    public static final String HOUR_OF_DAY = "hour_of_day";
    public static final String MINUTE = "minute";
    private long current_field_id;
    private int year, monthOfYear, dayOfMonth, hourOfDay, minute;
    private AlarmManager alarmManager;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private Tracker mTracker;
    private String number_suffix;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
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
                ;
                final Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                final ImageButton number_of_tries_help = (ImageButton) alertDialog.findViewById(R.id.number_of_tries_help);
                final ImageButton percentage_or_number_help = (ImageButton) alertDialog.findViewById(R.id.percentage_or_number_help);
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) QuickTestDialogFragment.this.getActivity().findViewById(R.id.root_view);
                ArrayAdapter<String> number_or_percentage_array_adapter = new ArrayAdapter<String>(QuickTestDialogFragment.this.getActivity(),
                        android.R.layout.simple_spinner_item, QuickTestDialogFragment.this.getResources().getStringArray(R.array.number_or_percentage));
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


                number_of_tries_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(QuickTestDialogFragment.this.getActivity());
                        number_of_tries_help_dialog_builder
                                .setMessage(R.string.enter_number_of_tries_help).create().show();/*(textView).create().show();*/

                    }
                });
                percentage_or_number_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder number_of_tries_help_dialog_builder = new AlertDialog.Builder(QuickTestDialogFragment.this.getActivity());
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
                        try {
                            String trys_suffix = " " + QuickTestDialogFragment.this.getActivity().getString(R.string._try);
                            String try_string = enterTries.getText().toString();
                            String number_string = enterNumber.getText().toString();
                            int trys;
                            if (try_string.length() > 0) {
                                trys = Integer.parseInt(try_string.substring(0, try_string.length() - trys_suffix.length()));
                            } else {
                                trys = 1;
                            }
                            if (number_string.length() > 0) {
                                int number = Integer.parseInt(number_string.substring(0, number_string.length() - number_suffix.length()));
                                if (number_or_percentage.getSelectedItemPosition() == 1) {
                                    if (number <= 100 && number > 0) {
                                        Intent intent = new Intent(QuickTestDialogFragment.this.getActivity(), ShowScheduledNotificationsReceiver.class);
                                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                                        intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, number);
                                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                        intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                        final Calendar calendar_picked_date_time = Calendar.getInstance();
                                        calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                        intent.putExtra(SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, calendar_picked_date_time.getTimeInMillis());
                                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                        int request = (int) System.currentTimeMillis();
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(QuickTestDialogFragment.this.getActivity(), request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);

                                        mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                                                ScheduleInFieldDialogFragment.QUICK_TEST, calendar_picked_date_time.getTimeInMillis(), request,
                                                EnhancedTestActivity.PERCENTAGE, number, trys);
                                        final Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_INDEFINITE);
                                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();
                                        QuickTestDialogFragment.this.dismiss();
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                                                .setAction((Money.getMe(getActivity()) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(QuickTestDialogFragment.this.getActivity()) + "schedule a random quick test")
                                                .build());
                                    } else {
                                        Toast toast = Toast.makeText(QuickTestDialogFragment.this.getActivity(), QuickTestDialogFragment.this.getString(R.string.percentage_between), Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                } else {
                                    DatabaseReadOperations databaseOperations = new DatabaseReadOperations(QuickTestDialogFragment.this.getActivity());
                                    int currentNumberOfEntries =
                                            databaseOperations.getEntriesNumberForTests(current_field_id, databaseOperations.getQuestionDataType(current_field_id));
                                    if (number > 0 && number <= currentNumberOfEntries) {
                                        Intent intent = new Intent(QuickTestDialogFragment.this.getActivity(), ShowScheduledNotificationsReceiver.class);
                                        intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                                        intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, number);
                                        intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, trys);
                                        intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                                        final Calendar calendar_picked_date_time = Calendar.getInstance();
                                        calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                                        intent.putExtra(SCHEDULE_WHAT, ScheduleInFieldDialogFragment.QUICK_TEST);
                                        intent.putExtra(ScheduleInFieldDialogFragment.TIME, calendar_picked_date_time.getTimeInMillis());
                                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                        int request = (int) System.currentTimeMillis();
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(QuickTestDialogFragment.this.getActivity(), request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                                        mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                                                ScheduleInFieldDialogFragment.QUICK_TEST, calendar_picked_date_time.getTimeInMillis(), request,
                                                EnhancedTestActivity.NUMBER, number, trys);
                                        final Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_INDEFINITE);
                                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();
                                        QuickTestDialogFragment.this.dismiss();
                                    } else {
                                        Toast toast = Toast.makeText(QuickTestDialogFragment.this.getActivity(),
                                                QuickTestDialogFragment.this.getString(R.string.number_of_questions_smaller_than, currentNumberOfEntries), Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                }
                            } else {
                                if (number_or_percentage.getSelectedItemPosition() == 1) {
                                    Toast toast = Toast.makeText(QuickTestDialogFragment.this.getActivity(), R.string.please_enter_percentage, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(QuickTestDialogFragment.this.getActivity(), R.string.please_enter_number_of_question, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            }
                        } catch (NumberFormatException e) {
                            Toast toast = Toast.makeText(QuickTestDialogFragment.this.getActivity(), R.string.not_allowed_number, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                enterNumber.requestFocus();
            }
        });
        return alertDialog;
    }
}
