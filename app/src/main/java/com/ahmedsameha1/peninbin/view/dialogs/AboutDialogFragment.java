package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.secondary_activities.IntroActivity;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;

public class AboutDialogFragment extends DialogFragment {
    public static final String ANALYTICS_CATEGORY = "Main Activity Category";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                final Button app_intro = (Button) alertDialog.findViewById(R.id.app_intro);
                final TextView version_textview = (TextView) alertDialog.findViewById(R.id.version);
                final TextView cwac_loaderex_license = (TextView) alertDialog.findViewById(R.id.cwac_loaderex);
                final TextView android_filepicker_license = (TextView) alertDialog.findViewById(R.id.android_filepicker);
                final TextView cursor_recyclerview_adapter = (TextView) alertDialog.findViewById(R.id.cursor_recyclerview_adapter);
                final TextView circular_floating_action_menu_license = (TextView) alertDialog.findViewById(R.id.circular_floating_action_menu);
                final TextView terms_of_use = (TextView) alertDialog.findViewById(R.id.terms_of_use);
                final TextView recyclerview_fast_scroller = (TextView) alertDialog.findViewById(R.id.recyclerview_fast_scroller);
                final TextView color_picker = (TextView) alertDialog.findViewById(R.id.color_picker);
                final TextView flexlayout = (TextView) alertDialog.findViewById(R.id.flexlayout);
                final TextView app_intro_license = (TextView) alertDialog.findViewById(R.id.app_intro_license);
                final TextView simple_storage_license = (TextView) alertDialog.findViewById(R.id.simple_storage_license);
                PackageInfo pInfo = null;
                String version = null;
                try {
                    pInfo = AboutDialogFragment.this.getActivity().getPackageManager().getPackageInfo(AboutDialogFragment.this.getActivity().getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                version_textview.setText(version != null ? version : "");
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                        AboutDialogFragment.this.startActivity(browserIntent);
                        AboutDialogFragment.this.dismiss();
                    }
                });
                app_intro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().startActivity(new Intent(getActivity(), IntroActivity.class));
                    }
                });
                contact_developer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ahmedsameha1@gmail.com"});
                        AboutDialogFragment.this.startActivity(Intent.createChooser(intent, ""));
                    }
                });
                cwac_loaderex_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.mit_license))
                                .create().show();
                    }
                });
                android_filepicker_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
                circular_floating_action_menu_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.mit_license))
                                .create().show();
                    }
                });
                terms_of_use.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.eula))
                                .create().show();
                    }
                });
                cursor_recyclerview_adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
                recyclerview_fast_scroller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
                color_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.mit_license))
                                .create().show();
                    }
                });
                flexlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(AboutDialogFragment.this.getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(AboutDialogFragment.this.getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
                app_intro_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
                simple_storage_license.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(ActivityUtility.getTextFileFromRawDirectory(getActivity(), R.raw.apache_license_v2))
                                .create().show();
                    }
                });
            }
        });
        return alertDialog;
    }

}

