package com.ahmedsameha1.peninbin.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by me on 15/10/16.
 */

public class Money {

    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String SUBSCRIPTION_TYPE = "subs";
    public static final String piece1 = "QIMfT8qhDGPVlGVxUcEpfGggS0lq";
    public static final String piece2 = "7GtqAIJ4AkurkZT/IdvQPP1mvOKHAra2";
    public static final String piece3 = "AbmkIzdPDU8ciIRiRXbLHibLCLRkh1K";
    public static final String piece4 = "FbA0+ocKREFkl8HaNqSPBAisizjJT";
    public static final String piece5 = "T4PmXAUW5IdR/d8dTOEewA31IAMRIAX5iv";
    public static final String piece6 = "rsBvdFvBn1rGkC2Y8SjHh7OYwcbX+QRsprz+QE";
    public static final String piece7 = "BEW+6Gvb6I1p5tHd64MgQp4PePyVCjKBTXPBzW+Tdj";
    public static final String piece8 = "Q3RlWPEy9hkQgCNIItMxZelgVHPZglgwmMlnA";
    public static final String piece9 = "i7gB1xO8TXLktHtTBO0O3M4dZo2L19vZlvf8+Da";
    public static final String piece10 = "mfIXRAHrgM55pBQtzWV4HNAs8EcccoAGsF9t96SzL";
    public static final String piece11 = "o8lU9xAvjSBrnQ/W1urh2FAV9eOQZTGr90uNJ9gkI";
    private static final String USER_ALLOWED_DUE_AN_ERROR = "user_allowed_due_an_error";
    public static final String NETWORK_DOWN_GET_PURCHASES_STATUS = "network_down_get_purchases_status";
    private static final String USER_SUBSCRIPTION_STATUS = "user_subscription_status";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    private static final String UNSOLVABLE_ERROR_GETPURCHASES = "unsolvable_error_getpurchases";
    public static final String MONEY = "money";


    /**
     * get the real public key
     * @param s the wrong public key
     * @param array the array of positions
     * @return the real public key
     */

    public static String get64(String s, int[] array) {
        String right_string = "";
        for ( int i = 0; i < array.length; i++ ) {
            right_string += s.charAt(array[i]);
        }
        return right_string;
    }


    public static boolean getUserAllowedDueAnError( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(USER_ALLOWED_DUE_AN_ERROR, false);
    }

    private static void allowUserDueAnError( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_ALLOWED_DUE_AN_ERROR, true);
        editor.commit();
    }

    private static void disallowUserDueAnError( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_ALLOWED_DUE_AN_ERROR, false);
        editor.commit();
    }

    public static boolean getNetworkDownGetPurchasesesStatus( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(NETWORK_DOWN_GET_PURCHASES_STATUS, false);
    }

    private static void putNetworkDownGetPurchasesesStatus( Context context, boolean status ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NETWORK_DOWN_GET_PURCHASES_STATUS, status);
        editor.commit();
    }


    public static boolean getUserSubscriptionStatus( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(USER_SUBSCRIPTION_STATUS, false);
    }

    public static void putUserSubscriptionStatus( Context context, boolean status ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_SUBSCRIPTION_STATUS, status);
        editor.commit();
    }

    public static void removeAllGetPurchasesErrors(Context context) {
        disallowUserDueAnError(context);
        putNetworkDownGetPurchasesesStatus(context, false);
        putUnsolvableErrorGetPurchases(context, false);
    }

    private static void putUnsolvableErrorGetPurchases ( Context context , boolean state ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(UNSOLVABLE_ERROR_GETPURCHASES, state);
        editor.commit();
    }

    public static boolean getUnsolvableErrorGetPurchases ( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(UNSOLVABLE_ERROR_GETPURCHASES, false);
    }

    public static void checkSubscription (final Context context, final IInAppBillingService billingService ) {
        AnalyticsApplication application = (AnalyticsApplication) (((Activity) context).getApplication());
        final Tracker mTracker = application.getDefaultTracker();
        (new Thread(() -> {
            if ( context.getPackageManager().checkPermission(Manifest.permission.GET_ACCOUNTS, context.getPackageName() ) == PERMISSION_GRANTED ) {
                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(context).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        String possibleEmail = account.name;
                        if (possibleEmail.equalsIgnoreCase("ahmedmsameha1@gmail.com")) {
                            putUserSubscriptionStatus(context, true);
                            removeAllGetPurchasesErrors(context);
                            return;
                        }
                    }
                }
            }
            // allow the users in the initial time to use the app without subscription
            if ( isInInitialPeriod(context) ) {
                putUserSubscriptionStatus(context, true);
                removeAllGetPurchasesErrors(context);
                return;
            }
            try {
                Bundle items_bundle = billingService.getPurchases(3,context.getPackageName(), SUBSCRIPTION_TYPE, null);
                int response_code = items_bundle.getInt(RESPONSE_CODE);
                if ( response_code == BILLING_RESPONSE_RESULT_OK ) {
                    removeAllGetPurchasesErrors(context);
                    if (!items_bundle.containsKey(RESPONSE_INAPP_ITEM_LIST)
                            || !items_bundle.containsKey(RESPONSE_INAPP_PURCHASE_DATA_LIST)
                            || !items_bundle.containsKey(RESPONSE_INAPP_SIGNATURE_LIST)) {
                        putUserSubscriptionStatus(context, false);
                        putUnsolvableErrorGetPurchases(context, true);  // consedered security error
                    } else {
                        ArrayList<String> owned_item_list = items_bundle.getStringArrayList(
                                RESPONSE_INAPP_ITEM_LIST);
                        ArrayList<String> owned_item_data_list = items_bundle.getStringArrayList(
                                RESPONSE_INAPP_PURCHASE_DATA_LIST);
                        ArrayList<String> owned_item_signature_list = items_bundle.getStringArrayList(
                                RESPONSE_INAPP_SIGNATURE_LIST);
                        if ( owned_item_data_list.size() > 0 ) {
                            if (Security.verifyPurchase(get64(piece1 + piece2 + piece3 + piece4 + piece5 + piece6
                                    + piece7 + piece8 + piece9 + piece10 + piece11, context.getResources().getIntArray(R.array.values))
                                    , owned_item_data_list.get(0), owned_item_signature_list.get(0))) {
                                putUserSubscriptionStatus(context, true);
                            } else {
                                putUserSubscriptionStatus(context, false);
                                putUnsolvableErrorGetPurchases(context, true);   // security error
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory(MONEY)
                                        .setAction(ActivityUtility.getVersionCode(context) + "security error")
                                        .build());
                            }
                        } else {
                            putUserSubscriptionStatus(context, false);
                        }
                    }
                } else if ( response_code == BILLING_RESPONSE_RESULT_USER_CANCELED ||
                        response_code == BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE ||
                        response_code == BILLING_RESPONSE_RESULT_DEVELOPER_ERROR ||
                        response_code == BILLING_RESPONSE_RESULT_ERROR ||
                        response_code == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED ||
                        response_code == BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED ) {
                    allowUserDueAnError(context);
                    putUserSubscriptionStatus(context, false);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(MONEY)
                            .setAction(ActivityUtility.getVersionCode(context) + "allow user due error")
                            .build());
                } else if ( response_code == BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                    putNetworkDownGetPurchasesesStatus(context, true);
                    putUserSubscriptionStatus(context, false);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(MONEY)
                            .setAction(ActivityUtility.getVersionCode(context) + "network error on money")
                            .build());
                } else if ( response_code == BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE ) {
                    putUnsolvableErrorGetPurchases(context, true);
                    putUserSubscriptionStatus(context, false);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(MONEY)
                            .setAction(ActivityUtility.getVersionCode(context) + "billing unavailable error money")
                            .build());
                }
            } catch (RemoteException e) {
                putUserSubscriptionStatus(context, false);
                allowUserDueAnError(context);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(MONEY)
                        .setAction(ActivityUtility.getVersionCode(context) + "allow user due error")
                        .build());
                e.printStackTrace();
            }
        })).start();
    }


    /**
     * Created by me on 19/10/16.
     */
    public static class Security {
        private static final String TAG = "IABUtil/Security";

        private static final String KEY_FACTORY_ALGORITHM = "RSA";
        private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

        /**
         * Verifies that the data was signed with the given signature, and returns
         * the verified purchase. The data is in JSON format and signed
         * with a private key. The data also contains the { PurchaseState}
         * and product ID of the purchase.
         * @param base64PublicKey the base64-encoded public key to use for verifying.
         * @param signedData the signed JSON string (signed, not encrypted)
         * @param signature the signature for the data, signed with the private key
         */
        public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
            if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey) ||
                    TextUtils.isEmpty(signature)) {
                Log.e(TAG, "Purchase verification failed: missing data.");
                return false;
            }

            PublicKey key = Security.generatePublicKey(base64PublicKey);
            return Security.verify(key, signedData, signature);
        }

        /**
         * Generates a PublicKey instance from a string containing the
         * Base64-encoded public key.
         *
         * @param encodedPublicKey Base64-encoded public key
         * @throws IllegalArgumentException if encodedPublicKey is invalid
         */
        public static PublicKey generatePublicKey(String encodedPublicKey) {
            try {
                byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
                KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
                return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "Invalid key specification.");
                throw new IllegalArgumentException(e);
            }
        }

        /**
         * Verifies that the signature from the server matches the computed
         * signature on the data.  Returns true if the data is correctly signed.
         *
         * @param publicKey public key associated with the developer account
         * @param signedData signed data from server
         * @param signature server signature
         * @return true if the data and signature match
         */
        public static boolean verify(PublicKey publicKey, String signedData, String signature) {
            byte[] signatureBytes;
            try {
                signatureBytes = Base64.decode(signature, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Base64 decoding failed.");
                return false;
            }
            try {
                Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
                sig.initVerify(publicKey);
                sig.update(signedData.getBytes());
                if (!sig.verify(signatureBytes)) {
                    Log.e(TAG, "Signature verification failed.");
                    return false;
                }
                return true;
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithmException.");
            } catch (InvalidKeyException e) {
                Log.e(TAG, "Invalid key specification.");
            } catch (SignatureException e) {
                Log.e(TAG, "Signature exception.");
            }
            return false;
        }
    }


    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
    public static boolean isInEgypt(Context context) {
        String country = getUserCountry(context);

        if (country != null && country.equals("eg")) {
            return true;
        }
        return false;
    }

    public static boolean isInInitialPeriod(Context context ) {
        long first_install_time = -1;
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            first_install_time =  pi.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if ( first_install_time >= 0 ) {
            if ( isInEgypt(context) ) {
                return System.currentTimeMillis() <= first_install_time + (AlarmManager.INTERVAL_DAY * 90);
            } else {
                return System.currentTimeMillis() <= first_install_time + (AlarmManager.INTERVAL_DAY * 30);
            }
        } else {
            return false;
        }
    }
}
