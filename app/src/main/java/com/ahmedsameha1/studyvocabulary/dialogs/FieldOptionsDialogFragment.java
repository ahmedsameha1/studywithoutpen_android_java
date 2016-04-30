package com.ahmedsameha1.studyvocabulary.dialogs;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.StudyActivity;
import com.ahmedsameha1.studyvocabulary.activities.TestActivity;
import com.ahmedsameha1.studyvocabulary.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.database.MySQLiteCursorLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 28/04/16.
 */
public class FieldOptionsDialogFragment extends DialogFragment {
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
    private CheckBox ignore_case;
    private CheckBox read_answer;
    private Spinner select_locale;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private Button positive_button;
    private int index_of_locale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        locale_display_names = getArguments().getStringArrayList(LOCALE_DISPLAY_NAMES);
        localeList = (ArrayList<Locale>) getArguments().getSerializable(LOCALE_LIST);
        tts_available = getArguments().getBoolean(TTS_AVAILABLE);
        locale_available = getArguments().getBoolean(LOCALE_AVAILABLE,false);
        index_of_locale = getArguments().getInt(INDEX_OF_LOCALE,-1);
        databaseOpertations = new DatabaseOperations(getActivity());
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
        isReadAnswer = databaseOpertations.isReadAnswer(current_field_id);
        locale_short_name = databaseOpertations.getFieldLocale(current_field_id);
        check_type = databaseOpertations.getFieldCheckType(current_field_id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.field_options)
                        .setView(inflater.inflate(R.layout.field_options_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ignore_case = (CheckBox) alertDialog.findViewById(R.id.ignore_case);
                read_answer = (CheckBox) alertDialog.findViewById(R.id.read_answer);
                select_locale = (Spinner) alertDialog.findViewById(R.id.locales_spinner);
                positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                read_answer.setChecked(isReadAnswer);
                ignore_case.setChecked(check_type > Contract.Field.DO_NOT_IGNORE_CASE);
                final SpinnerWithHintAdapter spinnerWithHintAdapter =
                        new SpinnerWithHintAdapter(getActivity(),android.R.layout.simple_spinner_item,locale_display_names);


                    if ( tts_available && locale_available) {
                        spinnerWithHintAdapter.setDropDownViewResource
                                (android.R.layout.simple_spinner_dropdown_item);
                        select_locale.setAdapter(spinnerWithHintAdapter);
                        select_locale.setSelection(index_of_locale);
                        select_locale.setVisibility(View.VISIBLE);
                    } else {
                        read_answer.setChecked(false);
                    }

                read_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!tts_available) {
                                Intent installIntent = new Intent();
                                installIntent.setAction(
                                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                startActivity(installIntent);
                                dismiss();
                            } else {
                                spinnerWithHintAdapter.setDropDownViewResource
                                        (android.R.layout.simple_spinner_dropdown_item);
                                select_locale.setAdapter(spinnerWithHintAdapter);
                                select_locale.setSelection(spinnerWithHintAdapter.getCount());
                                select_locale.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ( read_answer.isChecked() && select_locale.getSelectedItemPosition() == spinnerWithHintAdapter.getCount() ) {
                            Toast toast = Toast.makeText(getActivity(), R.string.select_locale, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else {
                            mySQLiteCursorLoader.updateFieldCheckType(current_field_id,ignore_case.isChecked());
                            mySQLiteCursorLoader.updateFieldReadAnswer(current_field_id,read_answer.isChecked());
                            int selected_locale_position = select_locale.getSelectedItemPosition();
                            if ( selected_locale_position < spinnerWithHintAdapter.getCount()
                                    && selected_locale_position >= 0 ) {
                                Locale locale = localeList.get(selected_locale_position);
                                locale_short_name = locale.getLanguage() + "," + locale.getCountry();
                                mySQLiteCursorLoader.updateFieldLocale(current_field_id,locale_short_name);
                            } else {
                                mySQLiteCursorLoader.updateFieldLocale(current_field_id,null);
                            }
                            dismiss();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

}
