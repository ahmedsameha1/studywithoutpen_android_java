package com.ahmedsameha1.peninbin.intent_services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class IntentServicesUtility {
    public static String createNotificationChannel(Context context){
        final String CHANNEL_ID = "channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);
            context.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
            return CHANNEL_ID;
        }
        return "";
    }
}
