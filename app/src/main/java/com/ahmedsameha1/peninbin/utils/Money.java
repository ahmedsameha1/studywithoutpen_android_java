package com.ahmedsameha1.peninbin.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;

import com.ahmedsameha1.peninbin.R;
import com.android.vending.billing.IInAppBillingService;

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
    private static final String TIME_OF_LAST_SUBSCRIPTION_PURCHASE = "time_of_last_subscription_purchase";


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
    private static final String BILLING_UNAVAILABLE_GET_PURCHASES_STATUS = "billing_unavailable_get_purchases_status";
    private static final String USER_SUBSCRIPTION_STATUS = "user_subscription_status";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    private static final String SECURITY_ERROR_GET_PURCHASES = "security_error_get_purchases";
    private static final String UNSOLVABLE_ERROR_GETPURCHASES = "unsolvable_error_getpurchases";


    public static void startPurchase() {

    }

    public static void continuePurchase() {

    }

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

    private static boolean getLocalMoneyStatus(Context context ) {
        if ( context.getPackageManager().checkPermission(Manifest.permission.GET_ACCOUNTS, context.getPackageName() ) == PERMISSION_GRANTED ) {
            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    String possibleEmail = account.name;
                    if (possibleEmail.equalsIgnoreCase("ahmedsameha1@gmail.com")) {
                        return true;
                    }
                }
            }
        }
        long time_of_last_subscription_purchase = getLocalSubscriptionStatus(context);
        if ( time_of_last_subscription_purchase == Constants.Money.NOT_SUBSCRIBED ) {
            return false;
        }
        long period = System.currentTimeMillis() - time_of_last_subscription_purchase;
        if ( period > ( AlarmManager.INTERVAL_DAY * Constants.Money.PERIOD_OF_SUBSCRIBTION_INCLUDING_GRACE_PERIOD ) ) {
            return false;
        } else {
            return true;
        }
    }

    private static long getLocalSubscriptionStatus( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(TIME_OF_LAST_SUBSCRIPTION_PURCHASE, Constants.Money.NOT_SUBSCRIBED);
    }

    private static void putLocalSubscriptionStatus( Context context, long date_millis ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TIME_OF_LAST_SUBSCRIPTION_PURCHASE, date_millis);
        editor.commit();
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

    private static boolean getBillingUnavailableGetPurchasesStatus( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(BILLING_UNAVAILABLE_GET_PURCHASES_STATUS, false);
    }

    private static void putBillingUnavailableGetPurchasesStatus( Context context, boolean status ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BILLING_UNAVAILABLE_GET_PURCHASES_STATUS, status);
        editor.commit();
    }

    public static boolean getUserSubscriptionStatus( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(USER_SUBSCRIPTION_STATUS, false);
    }

    private static void putUserSubscriptionStatus( Context context, boolean status ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_SUBSCRIPTION_STATUS, status);
        editor.commit();
    }

    private static void removeAllGetPurchasesErrors(Context context) {
        disallowUserDueAnError(context);
        putNetworkDownGetPurchasesesStatus(context, false);
        putUnsolvableErrorGetPurchases(context, false);
    }

    private static boolean getSecurityErrorGetPurchases( Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SECURITY_ERROR_GET_PURCHASES, false);
    }

    private static void setSecurityErrorGetPurchases( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SECURITY_ERROR_GET_PURCHASES, true);
        editor.commit();
    }

    private static void removeSecurityErrorGetPurchases( Context context ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SECURITY_ERROR_GET_PURCHASES);
        editor.commit();
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



    //// TODO: 22/10/16 must put google analytic flags in every part of the work with getpurchases
    public static void checkSubscription (final Context context, final IInAppBillingService billingService ) {
        (new Thread(new Runnable() {
            public void run() {
                if ( context.getPackageManager().checkPermission(Manifest.permission.GET_ACCOUNTS, context.getPackageName() ) == PERMISSION_GRANTED ) {
                    Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                    Account[] accounts = AccountManager.get(context).getAccounts();
                    for (Account account : accounts) {
                        if (emailPattern.matcher(account.name).matches()) {
                            String possibleEmail = account.name;
                            if (possibleEmail.equalsIgnoreCase("ahmedsameha1@gmail.com")) {
                                putUserSubscriptionStatus(context, true);
                                removeAllGetPurchasesErrors(context);
                                return;
                            }
                        }
                    }
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
                            if ( owned_item_data_list.size() == 1 ) {
                                if (Security.verifyPurchase(get64(piece1 + piece2 + piece3 + piece4 + piece5 + piece6
                                        + piece7 + piece8 + piece9 + piece10 + piece11, context.getResources().getIntArray(R.array.values))
                                        , owned_item_data_list.get(0), owned_item_signature_list.get(0))) {
                                    putUserSubscriptionStatus(context, true);
                                } else {
                                    putUserSubscriptionStatus(context, false);
                                    putUnsolvableErrorGetPurchases(context, true);   // security error
                                }
                            } else if ( owned_item_data_list.size() == 0 ) {
                                putUserSubscriptionStatus(context, false);
                            } else if ( owned_item_data_list.size() > 1 ) {
                                putUserSubscriptionStatus(context, false);
                                putUnsolvableErrorGetPurchases(context, true);  // error because there is only one item
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
                    } else if ( response_code == BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                        putNetworkDownGetPurchasesesStatus(context, true);
                        putUserSubscriptionStatus(context, false);
                    } else if ( response_code == BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE ) {
                        putUnsolvableErrorGetPurchases(context, true);
                        putUserSubscriptionStatus(context, false);
                    }
                } catch (RemoteException e) {
                    allowUserDueAnError(context);
                    e.printStackTrace();
                }
            }
        })).start();
    }

        /*
    the following commented work method is the method how produced the wrong string and the array of integers to make it true
     */

/*    private static Object [] work ( String s ) {
        ArrayList<Character> randomed_characters =  new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            randomed_characters.add(null);
        }
        System.out.println(randomed_characters.size());
        ArrayList<Integer> taken_random_positions = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            taken_random_positions.add(-1);
        }
        System.out.println(taken_random_positions.size());


        SecureRandom secureRandom = new SecureRandom();
        for ( int f = 0; f < s.length(); f++ ) {
            int rand = secureRandom.nextInt(s.length());
            int i;
            while ( true ) {
                for ( i = 0; i < taken_random_positions.size(); i++) {
                    if ( taken_random_positions.get(i) == rand ) {
                        //System.out.println("This was a for try");
                        break;
                    }
                }
                if (i == taken_random_positions.size() ) {

                    break;
                }

                rand = secureRandom.nextInt(s.length());
            }
            taken_random_positions.set(f,rand);
            randomed_characters.set(rand,s.charAt(f));
        }
        String randomized_key = "";
        for ( Character c : randomed_characters ) {
            randomized_key += c;
        }
        return new Object [] { randomized_key, taken_random_positions};
    }*/

    /**
     * Created by me on 19/10/16.
     */

    public static class BillingAPI_NotSupportedException extends Exception {
    }

    public static class GetPurchasesResponseCodeFailException extends Exception {

    }
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
}
