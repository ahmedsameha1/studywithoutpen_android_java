package com.ahmedsameha1.peninbin.receivers_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ahmedsameha1.peninbin.utils.Money;
import com.android.vending.billing.IInAppBillingService;

public class NetworkReceiver extends BroadcastReceiver {
    private IInAppBillingService billingService;

    public NetworkReceiver(IInAppBillingService billingService) {
        this.billingService = billingService;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
      if ( isOnline(context) ) {
          Money.checkSubscription(context, billingService);
      }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
