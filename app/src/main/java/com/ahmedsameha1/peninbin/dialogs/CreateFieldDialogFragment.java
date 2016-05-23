package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

import com.ahmedsameha1.peninbin.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ahmed on 7/21/2015.
 */
public class CreateFieldDialogFragment extends DialogFragment implements TextToSpeech.OnInitListener {
    private boolean tts_available;
    private TextToSpeech tts;
    private List<Locale> localeList = new ArrayList<Locale>();
    private List<String> locale_display_names = new ArrayList<String>();
    private DatabaseOperations databaseOperations;
    private String locale_short_name;
    private boolean isReadAnswer;
    private int check_type;
    private CheckBox read_answer;
    private Spinner select_locale;
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        tts_available = getArguments().getBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE);
        tts = new TextToSpeech(getActivity().getApplicationContext(), this);
        databaseOperations = new DatabaseOperations(getActivity());
    }
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
        builder.setView(inflater.inflate(R.layout.create_field_dialog_fragment,null))
                .setTitle(R.string.create_field)
                .setPositiveButton(R.string.create_field, null)
                .setNegativeButton(R.string.cancel, null).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final EditText enterFieldName = (EditText) alertDialog.findViewById(R.id.enter_field_name);
                final Spinner select_check_type = (Spinner) alertDialog.findViewById(R.id.check_type_spinner);
                final Spinner select_field_type = (Spinner) alertDialog.findViewById(R.id.field_type_spinner);
                final CheckBox read_answer = (CheckBox) alertDialog.findViewById(R.id.read_answer);
                final Spinner select_locale = (Spinner) alertDialog.findViewById(R.id.locales_spinner);
                final SpinnerWithHintAdapter field_type_adapter = new SpinnerWithHintAdapter(getActivity(),
                        android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.field_type));
                final SpinnerWithHintAdapter check_type_adapter = new SpinnerWithHintAdapter(getActivity(),
                        android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.check_type));
                field_type_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                check_type_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                select_field_type.setAdapter(field_type_adapter);
                select_check_type.setAdapter(check_type_adapter);
                select_field_type.setSelection(field_type_adapter.getCount());
                select_check_type.setSelection(check_type_adapter.getCount());
                final SpinnerWithHintAdapter select_locale_adapter =
                        new SpinnerWithHintAdapter(getActivity(),android.R.layout.simple_spinner_item,locale_display_names);
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
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
                                select_locale_adapter.setDropDownViewResource
                                        (android.R.layout.simple_spinner_dropdown_item);
                                select_locale.setAdapter(select_locale_adapter);
                                select_locale.setSelection(select_locale_adapter.getCount());
                                select_locale.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

//                        CheckBox notebookCheckbox = (CheckBox) alertDialog.findViewById(R.id.notebook_field_checkbox);
//                        int type = notebookCheckbox.isChecked()? Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE : Contract.Old_Field_DBV6_AppV32.DEFAULT_TYPE;
                        String fieldName = enterFieldName.getText().toString();
                        if (fieldName.equals("")) {
                            Toast toast = Toast.makeText(getActivity(), "Where is the field name?!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
                            Toast toast = Toast.makeText(getActivity(), "There is an field with the same name!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if ( select_field_type.getSelectedItemPosition() == field_type_adapter.getCount() ) {
                            Toast toast = Toast.makeText(getActivity(), R.string.select_a_field_type, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if ( select_check_type.getSelectedItemPosition() == check_type_adapter.getCount() ) {
                            Toast toast = Toast.makeText(getActivity(), R.string.select_a_check_type, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if ( read_answer.isChecked() && select_locale.getSelectedItemPosition() == select_locale_adapter.getCount() ) {
                            Toast toast = Toast.makeText(getActivity(), R.string.select_locale, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else {
                            int field_type = select_field_type.getSelectedItemPosition();
                            int check_type = select_check_type.getSelectedItemPosition();
                            if (  select_locale.getSelectedItemPosition() < select_locale_adapter.getCount()
                                    && select_locale.getSelectedItemPosition() >= 0 ) {
                                int i = select_locale.getSelectedItemPosition();
                                Locale locale = localeList.get(i);
                                locale_short_name = locale.getLanguage() + "," + locale.getCountry();
                                ((Contact)getActivity())
                                        .createField(fieldName,field_type, Contract.Field.STRING_DATA_TYPE,
                                                check_type,read_answer.isChecked(),locale_short_name);
                            } else {
                                ((Contact)getActivity())
                                        .createField(fieldName,field_type, Contract.Field.STRING_DATA_TYPE,
                                                check_type,read_answer.isChecked(),null);
                            }
                            alertDialog.dismiss();
                        }


                    }
                });
            }
        });
        return alertDialog;
    }
/*    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState ) {
        View view = inflater.inflate(R.layout.create_field_dialog_fragment,parent,false);
        enterFieldName = (EditText) view.findViewById(R.id.enter_field_name);
        return view;
    }*/
    public interface Contact {
    void createField(String field_name, int field_type, int question_data_type, int check_type, boolean read_answer, String locale);
}
    public void onInit(int status) {
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            if (locale.toString().equals("en_US_POSIX")) continue;
            int res = tts.isLanguageAvailable(locale);
            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                localeList.add(locale);
                locale_display_names.add(locale.getDisplayName());
            }
        }
        locale_display_names.add(getString(R.string.select_locale));
    }
}



