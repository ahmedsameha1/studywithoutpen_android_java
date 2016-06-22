package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ahmed on 28/04/16.
 */
public class NotebookFieldPropertiesDialogFragment extends DialogFragment {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";
    public static final String LOCALE_DISPLAY_NAMES = "local_display_names";
    public static final String LOCALE_LIST = "locale_list";
    public static final String TTS_AVAILABLE = "tts_available";
    public static final String LOCALE_AVAILABLE = "locale_available";
    public static final String INDEX_OF_LOCALE = "index_of_locale";
    private long current_field_id;
    private boolean tts_available;
    private boolean locale_available;
    private ArrayList<Locale> localeList = new ArrayList<Locale>();
    private ArrayList<String> locale_display_names = new ArrayList<String>();
    private DatabaseOperations databaseOpertations;
    private String locale_short_name;
    private boolean isReadAnswer;
    private int check_type;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private Button positive_button;
    private int index_of_locale;
    private String field_name;
    private Tracker mTracker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        databaseOpertations = new DatabaseOperations(getActivity());
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
        field_name = databaseOpertations.getFieldName(current_field_id);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.field_properties)
                        .setView(inflater.inflate(R.layout.notebook_field_properties_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText new_field_name = (EditText) alertDialog.findViewById(R.id.enter_new_field_name);
                positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                new_field_name.setText(field_name);



                positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( new_field_name.getText().toString().length() > 0 ) {
                            if ( (! new_field_name.getText().toString().equals(field_name)) && databaseOpertations.checkFieldDuplication(new_field_name.getText().toString())) {
                                Toast toast = Toast.makeText(getActivity(), "There is an field with the same name!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP,0,400);
                                toast.show();
                            } else {
                                if ( ! new_field_name.getText().toString().equals(field_name) ) {
                                    mySQLiteCursorLoader.updateFieldName(current_field_id,new_field_name.getText().toString());
                                    ((Contact) getActivity()).updateFieldName(new_field_name.getText().toString());
                                }
                                dismiss();
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "Where is the field name?!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }
    public interface Contact {
        void updateFieldName(String name) ;
    }
}
