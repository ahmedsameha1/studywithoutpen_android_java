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
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ahmed on 28/04/16.
 */
public class FieldOptionsDialogFragment extends DialogFragment implements TextToSpeech.OnInitListener {
    public static final String TEST_DIALOG_FRAGMENT = "test_dialog_fragment";
    public static final String TTS_AVAILABLE = "tts_available";
    private long current_field_id;
    private boolean tts_available;
    private TextToSpeech tts;
    private List<Locale> localeList = new ArrayList<Locale>();
    private List<String> locale_display_names = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        tts_available = getArguments().getBoolean(TTS_AVAILABLE);
        tts = new TextToSpeech(getActivity().getApplicationContext(), this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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
                final CheckBox ignore_case = (CheckBox) alertDialog.findViewById(R.id.ignore_case);
                final CheckBox read_answer = (CheckBox) alertDialog.findViewById(R.id.read_answer);
                final Spinner select_locale = (Spinner) alertDialog.findViewById(R.id.locales_spinner);
                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,locale_display_names);
                select_locale.setAdapter(arrayAdapter);
                select_locale.setSelection(0);
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
                                select_locale.setVisibility(View.VISIBLE);
                            }
                        } else {
                            select_locale.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });
        return alertDialog;
    }

    public void onInit(int status) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            int res = tts.isLanguageAvailable(locale);
            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                localeList.add(locale);
                locale_display_names.add(locale.getDisplayName());
            }
        }
    }
}
