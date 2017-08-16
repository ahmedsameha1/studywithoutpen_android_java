package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AboutDialogFragment extends DialogFragment {
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
        alertDialog.setOnShowListener(dialog -> {
            final Button facebook = (Button) alertDialog.findViewById(R.id.facebook);
            final Button contact_developer = (Button) alertDialog.findViewById(R.id.contact_developer);
            final TextView version_textview = (TextView) alertDialog.findViewById(R.id.version);
            final TextView cwac_loaderex_license = (TextView) alertDialog.findViewById(R.id.cwac_loaderex);
            final TextView android_filepicker_license = (TextView) alertDialog.findViewById(R.id.android_filepicker);
            final TextView cursor_recyclerview_adapter = (TextView) alertDialog.findViewById(R.id.cursor_recyclerview_adapter);
            final TextView circular_floating_action_menu_license = (TextView) alertDialog.findViewById(R.id.circular_floating_action_menu);
            final TextView terms_of_use = (TextView) alertDialog.findViewById(R.id.terms_of_use);
            final TextView recyclerview_fast_scroller = (TextView) alertDialog.findViewById(R.id.recyclerview_fast_scroller);
            final TextView color_picker = (TextView) alertDialog.findViewById(R.id.color_picker);
            final TextView flexlayout = (TextView) alertDialog.findViewById(R.id.flexlayout);
            PackageInfo pInfo = null;
            String version = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            version_textview.setText(version != null ? version : "");
            facebook.setOnClickListener(view -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(ANALYTICS_CATEGORY)
                        .setAction(ActivityUtility.getVersionCode(getActivity()) + "open facebook page")
                        .build());
                dismiss();
            });
            contact_developer.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "ahmedsameha1@gmail.com" });
                startActivity(Intent.createChooser(intent, ""));
            });
            cwac_loaderex_license.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.mit_license))
                    .create().show());
            android_filepicker_license.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.apache_license_v2))
                    .create().show());
            circular_floating_action_menu_license.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.mit_license))
                    .create().show());
            terms_of_use.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.eula))
                    .create().show());
            cursor_recyclerview_adapter.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.apache_license_v2))
                    .create().show());
            recyclerview_fast_scroller.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.apache_license_v2))
                    .create().show());
            color_picker.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.mit_license))
                    .create().show());
            flexlayout.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                    .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(),R.raw.apache_license_v2))
                    .create().show());
        });
        return alertDialog;
    }

}

