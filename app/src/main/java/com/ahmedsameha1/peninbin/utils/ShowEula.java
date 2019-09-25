package com.ahmedsameha1.peninbin.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by me on 30/10/16.
 */

public class ShowEula {
    private String EULA_PREFIX = "eula_";
    private Activity mActivity;

    public ShowEula(Activity context) {
        mActivity = context;
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void show() {
        final PackageInfo versionInfo = getPackageInfo();

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        for ( int i = 0; i < versionInfo.versionCode; i++ ) {
            if ( prefs.contains(EULA_PREFIX + i) ) {
                editor.remove(EULA_PREFIX + i);
            }
        }
        editor.apply();
        if( !hasBeenShown ){

            String title = mActivity.getString(R.string.please_read);

            String message =  ActivityUtility.getTextFileFromRawDirectory(mActivity,R.raw.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int y) {
                            // Mark this version as read.
                            editor.putBoolean(eulaKey, true);
                            for (int i = 0; i < versionInfo.versionCode; i++) {
                                if (prefs.contains(EULA_PREFIX + i)) {
                                    editor.remove(EULA_PREFIX + i);
                                }
                            }
                            editor.commit();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();
                        }
                    });
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
    }
}
