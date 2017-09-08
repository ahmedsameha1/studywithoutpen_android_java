package com.ahmedsameha1.peninbin.intent_services;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ahmedsameha1.peninbin.utils.Money;
import com.android.vending.billing.IInAppBillingService;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class CheckSubscriptionIntentService extends IntentService {
    private IInAppBillingService billingService;

    public CheckSubscriptionIntentService() {
        super("CheckSubscriptionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ServiceConnection mServiceConn = new ServiceConnection() {
                @Override
                public void onServiceDisconnected(ComponentName name) {
                    billingService = null;
                }

                @Override
                public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                    billingService = IInAppBillingService.Stub.asInterface(service);
                    Log.d("Money", "check started");
                    Money.checkSubscription(CheckSubscriptionIntentService.this, billingService);
                    Log.d("Money", "check finished");
                    unbindService(this);
                    Log.d("Money", "service unbound");
                }
            };
            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }
    }
}
