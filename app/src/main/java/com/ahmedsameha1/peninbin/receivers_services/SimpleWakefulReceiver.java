package com.ahmedsameha1.peninbin.receivers_services;

/**
 * Created by ahmed on 27/05/16.
 */
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SimpleWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("wakeful:","wakeful receiver");
        Intent service = new Intent(context, MidnightService.class);

        startWakefulService(context, service);
    }
}