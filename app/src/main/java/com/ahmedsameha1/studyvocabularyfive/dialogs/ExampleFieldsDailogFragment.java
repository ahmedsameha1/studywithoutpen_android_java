package com.ahmedsameha1.studyvocabularyfive.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.ahmedsameha1.studyvocabularyfive.activities.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed_000 on 11/8/2015.
 */
public class ExampleFieldsDailogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setView(inflater.inflate(R.layout.example_fields_dialog_fragment,null))
                        .setTitle(R.string.example_fields_dialog_title)
                .setPositiveButton(R.string.ok,null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                final CheckBox checkBox = (CheckBox) alertDialog.findViewById(R.id.dont_show_again);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( checkBox.isChecked() ) {
                            PackageManager manager = getActivity().getPackageManager();
                            try {
                                PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                                int versionCode = info.versionCode;
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()); // getting the preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(FieldActivity.RUN_FIRST_TIME_DIALOG + versionCode,false);
                                editor.commit();
                            } catch (PackageManager.NameNotFoundException e) {

                            }
                        }
                        dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }
/*
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setView(inflater.inflate(R.layout.create_field_dialog_fragment,null))
                        .setPositiveButton(R.string.create_field, null)
                        .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText enterFieldName = (EditText) alertDialog.findViewById(R.id.enter_field_name);
                        String string = enterFieldName.getText().toString();
                        if ( string.contains(" ") ) {
                            Toast.makeText(getActivity(), getString(R.string.no_space_allowed), Toast.LENGTH_SHORT).show();
                        } else {
                            ((Contact) getActivity()).createNewField(CreateFieldDialogFragment.this);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    *//*    @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState ) {
            View view = inflater.inflate(R.layout.create_field_dialog_fragment,parent,false);
            enterFieldName = (EditText) view.findViewById(R.id.enter_field_name);
            return view;
        }*//*
    public interface Contact {
        public void createNewField(CreateFieldDialogFragment dialog);
    }*/
}
