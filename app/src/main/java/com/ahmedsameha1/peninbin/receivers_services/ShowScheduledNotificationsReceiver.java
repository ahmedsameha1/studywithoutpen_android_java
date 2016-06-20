package com.ahmedsameha1.peninbin.receivers_services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.RefreshingAppUsageActivity;

public class ShowScheduledNotificationsReceiver extends BroadcastReceiver {
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    public ShowScheduledNotificationsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mySQLiteCursorLoader = new MySQLiteCursorLoader(context);
        Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        PendingIntent contentIntent = null;
        int schedule_what = intent.getIntExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,0);
        if ( schedule_what != ScheduleInFieldDialogFragment.REFRESHING_USAGE_OF_APP ) {
            long current_field_id = intent.getLongExtra(StudyActivity.CURRENT_FIELD_ID,0);
            String current_field_name = new DatabaseOperations(context).getFieldName(current_field_id);
            if ( schedule_what == ScheduleInFieldDialogFragment.QUICK_TEST ) {
                Intent quick_test_intent = new Intent(context, QuickTestActivity.class);
                if ( intent.hasExtra(TestActivity.QUESTION_PERCENTAGE) ) {
                    quick_test_intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER, TestActivity.PERCENTAGE);
                    quick_test_intent.putExtra(TestActivity.QUESTION_PERCENTAGE, intent.getIntExtra(TestActivity.QUESTION_PERCENTAGE,1));
                    quick_test_intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER, intent.getIntExtra(TestActivity.PERMITED_TRIES_NUMBER,1));
                    quick_test_intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    quick_test_intent.putExtra(TestActivity.CONTROLLED_PORTION, intent.getIntExtra(TestActivity.CONTROLLED_PORTION,-1));
                    quick_test_intent.putExtra(TestActivity.RANK_LEVEL, intent.getIntExtra(TestActivity.RANK_LEVEL,-1));
                    quick_test_intent.putExtra(TestActivity.WRONGNESS_LEVEL, intent.getIntExtra(TestActivity.WRONGNESS_LEVEL,-1));

                } else {
                    quick_test_intent.putExtra(TestActivity.PERCENTAGE_OR_NUMBER, TestActivity.NUMBER);
                    quick_test_intent.putExtra(TestActivity.QUESTION_NUMBER, intent.getIntExtra(TestActivity.QUESTION_NUMBER,1));
                    quick_test_intent.putExtra(TestActivity.PERMITED_TRIES_NUMBER, intent.getIntExtra(TestActivity.PERMITED_TRIES_NUMBER,1));
                    quick_test_intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                    quick_test_intent.putExtra(TestActivity.CONTROLLED_PORTION, intent.getIntExtra(TestActivity.CONTROLLED_PORTION,-1));
                    quick_test_intent.putExtra(TestActivity.RANK_LEVEL, intent.getIntExtra(TestActivity.RANK_LEVEL,-1));
                    quick_test_intent.putExtra(TestActivity.WRONGNESS_LEVEL, intent.getIntExtra(TestActivity.WRONGNESS_LEVEL,-1));
                }
                contentIntent = PendingIntent.getActivity(context, 0, quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getString(R.string.quick_test_time))
                                .setContentText(current_field_name)
                                .setTicker(context.getString(R.string.quick_test_time))
                                .setSound(notification_sound)
                                .setAutoCancel(true);
            } else if ( schedule_what == ScheduleInFieldDialogFragment.TEST ) {
                Intent quick_test_intent = new Intent(context, ShowNotificationDialogsActivity.class);
                quick_test_intent.putExtra(ShowNotificationDialogsActivity.DIALOG_TYPE, ShowNotificationDialogsActivity.TEST_DIALOG);
                quick_test_intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                contentIntent = PendingIntent.getActivity(context, 0, quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getString(R.string.test_time))
                                .setContentText(current_field_name)
                                .setTicker(context.getString(R.string.test_time))
                                .setSound(notification_sound)
                                .setAutoCancel(true);
            } else if ( schedule_what == ScheduleInFieldDialogFragment.STUDY_A_PERIOD ) {
                Intent quick_test_intent = new Intent(context, ShowNotificationDialogsActivity.class);
                quick_test_intent.putExtra(ShowNotificationDialogsActivity.DIALOG_TYPE, ShowNotificationDialogsActivity.STUDY_PERIOD_DIALOG);
                quick_test_intent.putExtra(StudyActivity.CURRENT_FIELD_ID, current_field_id);
                contentIntent = PendingIntent.getActivity(context, 0, quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder =
                        new android.support.v4.app.NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getString(R.string.study_period_time))
                                .setContentText(current_field_name)
                                .setTicker(context.getString(R.string.study_period_time))
                                .setSound(notification_sound)
                                .setAutoCancel(true);
            }
            notificationBuilder.setContentIntent(contentIntent);
            Notification notification = notificationBuilder.build();


            // Clear the notification when it is pressed
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            // Send the notification to the system.
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify((int)System.currentTimeMillis(), notification);
            mySQLiteCursorLoader.deleteScheduledTimeForBackup(current_field_id,schedule_what,intent.getLongExtra(ScheduleInFieldDialogFragment.TIME,0));
        } else {
            Intent refreshing_intent = new Intent(context, RefreshingAppUsageActivity.class);
            refreshing_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            contentIntent = PendingIntent.getActivity(context, 0, refreshing_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder =
                    new android.support.v4.app.NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher512)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.studying_is_everyday_task))
                            .setTicker(context.getString(R.string.studying_is_everyday_task))
                            .setSound(notification_sound)
                            .setAutoCancel(true);
            notificationBuilder.setContentIntent(contentIntent);
            Notification notification = notificationBuilder.build();


            // Clear the notification when it is pressed
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            // Send the notification to the system.
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify((int)System.currentTimeMillis(), notification);
        }
    }
}
