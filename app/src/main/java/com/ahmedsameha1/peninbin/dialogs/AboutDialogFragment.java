package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by ahmed_000 on 10/13/2015.
 */
public class AboutDialogFragment extends DialogFragment {
    public static final String  ABOUT_DIALOG_FRAGMENT = "about_dialog_fragment";
    public static final String ANALYTICS_CATEGORY = "Main Activity Category";



    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setTitle(R.string.about_app)
                .setView(inflater.inflate(R.layout.about_dialog_fragment, null))
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button facebook = (Button) alertDialog.findViewById(R.id.facebook);
                final Button contact_developer = (Button) alertDialog.findViewById(R.id.contact_developer);
                final TextView version_textview = (TextView) alertDialog.findViewById(R.id.version);
                final TextView cwac_loaderex_license = (TextView) alertDialog.findViewById(R.id.cwac_loaderex);
                final TextView android_filepicker_license = (TextView) alertDialog.findViewById(R.id.android_filepicker);
                final TextView circular_floating_action_menu_license = (TextView) alertDialog.findViewById(R.id.circular_floating_action_menu);
                final TextView terms_of_use = (TextView) alertDialog.findViewById(R.id.terms_of_use);
                PackageInfo pInfo = null;
                String version = null;
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                version_textview.setText(version != null ? version : "");
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                        startActivity(browserIntent);
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(ANALYTICS_CATEGORY)
                                .setAction("open facebook page")
                                .build());
                        dismiss();
                    }
                });
                contact_developer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "ahmedsameha1@gmail.com" });
                        startActivity(Intent.createChooser(intent, ""));
                    }
                });
                cwac_loaderex_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       new AlertDialog.Builder(getActivity())
                               .setMessage(R.string.cwac_loaderex_apache_license_version_2_0).create().show();
                    }
                });
                android_filepicker_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.android_filepicker_apache_license_version_2_0).create().show();
                    }
                });
                circular_floating_action_menu_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.circular_floating_action_menu_mit_license).create().show();
                    }
                });
                terms_of_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.eula).create().show();
                    }
                });
            }
        });
        return alertDialog;
    }

}

