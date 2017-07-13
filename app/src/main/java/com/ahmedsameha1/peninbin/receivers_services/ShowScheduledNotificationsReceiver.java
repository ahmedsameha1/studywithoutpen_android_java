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
import android.support.v4.app.TaskStackBuilder;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.activities.QuickTestActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.secondary_activities.ShowNotificationDialogsActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.dialogs.ScheduleInFieldDialogFragment;
import com.ahmedsameha1.peninbin.secondary_activities.RefreshingAppUsageActivity;
import com.ahmedsameha1.peninbin.utils.Emulation;

public class ShowScheduledNotificationsReceiver extends BroadcastReceiver {
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    public ShowScheduledNotificationsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
        Uri notification_sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        PendingIntent contentIntent = null;
        int schedule_what = intent.getIntExtra(ScheduleInFieldDialogFragment.SCHEDULE_WHAT,0);
        if ( schedule_what == ScheduleInFieldDialogFragment.QUICK_TEST
                || schedule_what == ScheduleInFieldDialogFragment.STUDY_A_PERIOD
                || schedule_what == ScheduleInFieldDialogFragment.ENHANCED_TEST
                || schedule_what == ScheduleInFieldDialogFragment.FULLY_RANDOM_TEST ) {
            long current_field_id = intent.getLongExtra(Constants.CURRENT_FIELD_ID,0);
            String current_field_name = new DatabaseReadOperations(context).getFieldName(current_field_id);
            if ( !Emulation.isAnyThreadRunning() ) {
                if ( schedule_what == ScheduleInFieldDialogFragment.QUICK_TEST ) {
                    Intent quick_test_intent = new Intent(context, QuickTestActivity.class);
                    if ( intent.hasExtra(EnhancedTestActivity.QUESTION_PERCENTAGE) ) {
                        quick_test_intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.PERCENTAGE);
                        quick_test_intent.putExtra(EnhancedTestActivity.QUESTION_PERCENTAGE, intent.getIntExtra(EnhancedTestActivity.QUESTION_PERCENTAGE,1));
                        quick_test_intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, intent.getIntExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER,1));
                        quick_test_intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                        quick_test_intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, intent.getIntExtra(EnhancedTestActivity.CONTROLLED_PORTION,-1));
                        quick_test_intent.putExtra(EnhancedTestActivity.RANK_LEVEL, intent.getIntExtra(EnhancedTestActivity.RANK_LEVEL,-1));
                        quick_test_intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, intent.getIntExtra(EnhancedTestActivity.WRONGNESS_LEVEL,-1));
    
                    } else {
                        quick_test_intent.putExtra(EnhancedTestActivity.PERCENTAGE_OR_NUMBER, EnhancedTestActivity.NUMBER);
                        quick_test_intent.putExtra(EnhancedTestActivity.QUESTION_NUMBER, intent.getIntExtra(EnhancedTestActivity.QUESTION_NUMBER,1));
                        quick_test_intent.putExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER, intent.getIntExtra(EnhancedTestActivity.PERMITED_TRIES_NUMBER,1));
                        quick_test_intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                        quick_test_intent.putExtra(EnhancedTestActivity.CONTROLLED_PORTION, intent.getIntExtra(EnhancedTestActivity.CONTROLLED_PORTION,-1));
                        quick_test_intent.putExtra(EnhancedTestActivity.RANK_LEVEL, intent.getIntExtra(EnhancedTestActivity.RANK_LEVEL,-1));
                        quick_test_intent.putExtra(EnhancedTestActivity.WRONGNESS_LEVEL, intent.getIntExtra(EnhancedTestActivity.WRONGNESS_LEVEL,-1));
                    }
                    contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher512)
                                    .setContentTitle(context.getString(R.string.quick_test_time))
                                    .setContentText(current_field_name)
                                    .setTicker(context.getString(R.string.quick_test_time))
                                    .setSound(notification_sound)
                                    .setAutoCancel(true);
                } else if ( schedule_what == ScheduleInFieldDialogFragment.ENHANCED_TEST) {
                    Intent quick_test_intent = new Intent(context, ShowNotificationDialogsActivity.class);
                    quick_test_intent.putExtra(ShowNotificationDialogsActivity.DIALOG_TYPE, ShowNotificationDialogsActivity.ENHANCED_TEST_DIALOG);
                    quick_test_intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                    contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher512)
                                    .setContentTitle(context.getString(R.string.enhanced_test_time))
                                    .setContentText(current_field_name)
                                    .setTicker(context.getString(R.string.enhanced_test_time))
                                    .setSound(notification_sound)
                                    .setAutoCancel(true);
                } else if ( schedule_what == ScheduleInFieldDialogFragment.FULLY_RANDOM_TEST) {
                    Intent fully_random_test_intent = new Intent(context, ShowNotificationDialogsActivity.class);
                    fully_random_test_intent.putExtra(ShowNotificationDialogsActivity.DIALOG_TYPE, ShowNotificationDialogsActivity.FULLY_RANDOM_TEST_DIALOG);
                    fully_random_test_intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                    contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), fully_random_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher512)
                                    .setContentTitle(context.getString(R.string.fully_random_test_time))
                                    .setContentText(current_field_name)
                                    .setTicker(context.getString(R.string.fully_random_test_time))
                                    .setSound(notification_sound)
                                    .setAutoCancel(true);
                } else if ( schedule_what == ScheduleInFieldDialogFragment.STUDY_A_PERIOD ) {
                    Intent quick_test_intent = new Intent(context, ShowNotificationDialogsActivity.class);
                    quick_test_intent.putExtra(ShowNotificationDialogsActivity.DIALOG_TYPE, ShowNotificationDialogsActivity.STUDY_PERIOD_DIALOG);
                    quick_test_intent.putExtra(Constants.CURRENT_FIELD_ID, current_field_id);
                    contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), quick_test_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_launcher512)
                                    .setContentTitle(context.getString(R.string.study_period_time))
                                    .setContentText(current_field_name)
                                    .setTicker(context.getString(R.string.study_period_time))
                                    .setSound(notification_sound)
                                    .setAutoCancel(true);
                }

            } else {
                TaskStackBuilder stackBuilder = null;
                Intent notifyIntent = new Intent(context, MainActivity.class);
                notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher512)
                                .setContentTitle(context.getResources().getString(R.string.session_cancelation))
                                .setContentText(context.getResources().getString(R.string.due_emulation_session_cancelation))
                                .setTicker(context.getResources().getString(R.string.due_emulation_session_cancelation))
                                .setAutoCancel(true);

                stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(notifyIntent);
                contentIntent = stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);



            }
            notificationBuilder.setContentIntent(contentIntent);
            Notification notification = notificationBuilder.build();


            // Clear the notification when it is pressed
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            // Send the notification to the system.
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int)System.currentTimeMillis(), notification);
            mySQLiteCursorLoader.deleteScheduledTimeForBackup(current_field_id,schedule_what,intent.getLongExtra(ScheduleInFieldDialogFragment.TIME,0));
        } else if ( schedule_what == ScheduleInFieldDialogFragment.REFRESHING_USAGE_OF_APP ) {
            Intent refreshing_intent = new Intent(context, RefreshingAppUsageActivity.class);
            refreshing_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), refreshing_intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
