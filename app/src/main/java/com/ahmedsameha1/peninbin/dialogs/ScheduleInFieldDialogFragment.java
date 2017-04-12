package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.receivers_services.ShowScheduledNotificationsReceiver;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;

import java.util.Calendar;
import com.google.android.gms.analytics.Tracker;
/**
 * Created by ahmed on 12/20/2015.
 */
public class ScheduleInFieldDialogFragment extends DialogFragment {
    public static final int QUICK_TEST = 0;
    public static final int ENHANCED_TEST = 1;
    public static final int STUDY_A_PERIOD = 2;
    public static final int REFRESHING_USAGE_OF_APP = 3;
    public static final String TIME = "time";
    public static final String SCHEDULE_IN_FIELD = "schedule_in_field";
    public static final String SCHEDULE_WHAT = "schedule_what";
    public static final int FULLY_RANDOM_TEST = 5;

    private Context context;
    private long current_field_id;
    private AlarmManager alarmManager;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        alarmManager = (AlarmManager) getActivity().getSystemService(AppCompatActivity.ALARM_SERVICE);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.schedule_in_field_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(dialog -> {
            Button schedule_quick_test = (Button) alertDialog.findViewById(R.id.schedule_quick_test);
            Button schedule_enhanced_test = (Button) alertDialog.findViewById(R.id.schedule_enhanced_test);
            Button schedule_fully_random_test = (Button) alertDialog.findViewById(R.id.schedule_fully_random_test);
            Button schedule_study_period = (Button) alertDialog.findViewById(R.id.schedule_study_period);
            Button randomly_schedule_quick_test = (Button) alertDialog.findViewById(R.id.schedule_random_quick_test);
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_view);
            schedule_quick_test.setOnClickListener(view -> {
                final Calendar calendar_current_date_time = Calendar.getInstance();
                final Calendar calendar_picked_date_time = Calendar.getInstance();
                new DatePickerDialog(getActivity(), (view111, year, monthOfYear, dayOfMonth) -> new TimePickerDialog(getActivity(), (view110, hourOfDay, minute) -> {
                    Bundle bundle = new Bundle();
                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    bundle.putInt(QuickTestDialogFragment.YEAR,year);
                    bundle.putInt(QuickTestDialogFragment.MONTH_OF_YEAR,monthOfYear);
                    bundle.putInt(QuickTestDialogFragment.DAY_OF_MONTH,dayOfMonth);
                    bundle.putInt(QuickTestDialogFragment.HOUR_OF_DAY,hourOfDay);
                    bundle.putInt(QuickTestDialogFragment.MINUTE,minute);
                    QuickTestDialogFragment quickTestDialogFragment = new QuickTestDialogFragment();
                    quickTestDialogFragment.setArguments(bundle);
                    quickTestDialogFragment.show(getActivity().getSupportFragmentManager(), QuickTestDialogFragment.QUICK_TEST_DIALOG);
                    ScheduleInFieldDialogFragment.this.dismiss();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                            .setAction(ActivityUtility.getVersionCode(getActivity()) + "schedule a quick test")
                            .build());
                    /*Log.i("time Picker",hourOfDay +" " + minute);
                    calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                    Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
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
                    ScheduleInFieldDialogFragment.this.dismiss();*/
                }, calendar_current_date_time.get(Calendar.HOUR_OF_DAY), calendar_current_date_time.get(Calendar.MINUTE), false).show(), calendar_current_date_time.get(Calendar.YEAR), calendar_current_date_time.get(Calendar.MONTH),
                        calendar_current_date_time.get(Calendar.DAY_OF_MONTH)).show();
            });
            schedule_enhanced_test.setOnClickListener(view -> {
                Calendar calendar_current_date_time = Calendar.getInstance();
                final Calendar calendar_picked_date_time = Calendar.getInstance();
                new DatePickerDialog(getActivity(), (view19, year, monthOfYear, dayOfMonth) -> new TimePickerDialog(getActivity(), (view18, hourOfDay, minute) -> {
                    calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                    Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
                    intent.putExtra(SCHEDULE_WHAT, ENHANCED_TEST);
                    intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                    intent.putExtra(ScheduleInFieldDialogFragment.TIME,calendar_picked_date_time.getTimeInMillis());
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    int request = (int)System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                            ScheduleInFieldDialogFragment.ENHANCED_TEST,calendar_picked_date_time.getTimeInMillis(),request);
                    final Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.ok, view17 -> snackbar.dismiss());
                    snackbar.show();
                    ScheduleInFieldDialogFragment.this.dismiss();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                            .setAction(ActivityUtility.getVersionCode(getActivity()) + "schedule an enhanced test")
                            .build());
                }, calendar_picked_date_time.get(Calendar.HOUR_OF_DAY), calendar_picked_date_time.get(Calendar.MINUTE), false).show(), calendar_current_date_time.get(Calendar.YEAR), calendar_current_date_time.get(Calendar.MONTH),
                        calendar_current_date_time.get(Calendar.DAY_OF_MONTH)).show();
            });
            schedule_fully_random_test.setOnClickListener(view -> {
                Calendar calendar_current_date_time = Calendar.getInstance();
                final Calendar calendar_picked_date_time = Calendar.getInstance();
                new DatePickerDialog(getActivity(), (view16, year, monthOfYear, dayOfMonth) -> new TimePickerDialog(getActivity(), (view15, hourOfDay, minute) -> {
                    calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                    Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
                    intent.putExtra(SCHEDULE_WHAT, FULLY_RANDOM_TEST);
                    intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                    intent.putExtra(ScheduleInFieldDialogFragment.TIME,calendar_picked_date_time.getTimeInMillis());
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    int request = (int)System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                            ScheduleInFieldDialogFragment.FULLY_RANDOM_TEST,calendar_picked_date_time.getTimeInMillis(),request);
                    final Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.ok, view14 -> snackbar.dismiss());
                    snackbar.show();
                    ScheduleInFieldDialogFragment.this.dismiss();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                            .setAction(ActivityUtility.getVersionCode(getActivity()) + "schedule a fully random test")
                            .build());
                }, calendar_picked_date_time.get(Calendar.HOUR_OF_DAY), calendar_picked_date_time.get(Calendar.MINUTE), false).show(), calendar_current_date_time.get(Calendar.YEAR), calendar_current_date_time.get(Calendar.MONTH),
                        calendar_current_date_time.get(Calendar.DAY_OF_MONTH)).show();
            });
            schedule_study_period.setOnClickListener(view -> {
                Calendar calendar_current_date_time = Calendar.getInstance();
                final Calendar calendar_picked_date_time = Calendar.getInstance();
                new DatePickerDialog(getActivity(), (view13, year, monthOfYear, dayOfMonth) -> new TimePickerDialog(getActivity(), (view12, hourOfDay, minute) -> {
                    calendar_picked_date_time.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                    Intent intent = new Intent(getActivity(), ShowScheduledNotificationsReceiver.class);
                    intent.putExtra(SCHEDULE_WHAT,STUDY_A_PERIOD);
                    intent.putExtra(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                    intent.putExtra(ScheduleInFieldDialogFragment.TIME,calendar_picked_date_time.getTimeInMillis());
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    int request = (int)System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),request , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC, calendar_picked_date_time.getTimeInMillis(), pendingIntent);
                    mySQLiteCursorLoader.addScheduledTimeForBackup(current_field_id,
                            ScheduleInFieldDialogFragment.STUDY_A_PERIOD,calendar_picked_date_time.getTimeInMillis(),request);
                    final Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, R.string.scheduled, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.ok, view1 -> snackbar.dismiss());
                    snackbar.show();
                    ScheduleInFieldDialogFragment.this.dismiss();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                            .setAction(ActivityUtility.getVersionCode(getActivity()) + "schedule a study a period")
                            .build());
                }, calendar_picked_date_time.get(Calendar.HOUR_OF_DAY), calendar_picked_date_time.get(Calendar.MINUTE), false).show(), calendar_current_date_time.get(Calendar.YEAR), calendar_current_date_time.get(Calendar.MONTH),
                        calendar_current_date_time.get(Calendar.DAY_OF_MONTH)).show();
            });
            randomly_schedule_quick_test.setOnClickListener(view -> {
                RandomlyScheduleQuickTestDialogFragment randomlyScheduleQuickTestDialogFragment = new RandomlyScheduleQuickTestDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putLong(StudyActivity.CURRENT_FIELD_ID,current_field_id);
                randomlyScheduleQuickTestDialogFragment.setArguments(bundle);
                randomlyScheduleQuickTestDialogFragment.show(getActivity().getSupportFragmentManager(),
                        RandomlyScheduleQuickTestDialogFragment.RANDOMLY_SCHEDULE_QUICK_TEST);
                dismiss();
            });
        });
        return alertDialog;
    }

}


