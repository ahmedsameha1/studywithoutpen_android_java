package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;
import android.speech.tts.TextToSpeech;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/**
 * Created by ahmed on 7/21/2015.
 */
public class CreateFieldDialogFragment extends DialogFragment implements TextToSpeech.OnInitListener {
    private boolean tts_available;
    private TextToSpeech textToSpeech;
    private List<Locale> localeList = new ArrayList<Locale>();
    private List<String> locale_display_names = new ArrayList<String>();
    private DatabaseReadOperations databaseOperations;
    private String locale_short_name;
    private CheckBox read_answer;
    private Tracker mTracker;
    private int red = 255, green = 255, blue = 255, color = Color.WHITE;
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        tts_available = getArguments().getBoolean(FieldPropertiesDialogFragment.TTS_AVAILABLE);
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), this);
        databaseOperations = new DatabaseReadOperations(getActivity());
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        setRetainInstance(true);
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
                read_answer = (CheckBox) alertDialog.findViewById(R.id.read_answer);
                final Spinner select_locale = (Spinner) alertDialog.findViewById(R.id.locales_spinner);
                final Button pick_color = (Button) alertDialog.findViewById(R.id.color);
                final ArrayAdapter<String> check_type_adapter = new ArrayAdapter<String>(CreateFieldDialogFragment.this.getActivity(),
                        android.R.layout.simple_spinner_item, CreateFieldDialogFragment.this.getResources().getStringArray(R.array.check_type));
                check_type_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                select_check_type.setAdapter(check_type_adapter);
                final SpinnerWithHintAdapter select_locale_adapter =
                        new SpinnerWithHintAdapter(CreateFieldDialogFragment.this.getActivity(), android.R.layout.simple_spinner_item, locale_display_names);
                Button positive_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                read_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!tts_available) {
                                Intent installIntent = new Intent();
                                installIntent.setAction(
                                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                CreateFieldDialogFragment.this.startActivity(installIntent);
                                CreateFieldDialogFragment.this.dismiss();
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
                pick_color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ColorPicker colorPicker = new ColorPicker(getActivity(), red, green, blue);
                        colorPicker.setCallback(new ColorPickerCallback() {
                            @Override
                            public void onColorChosen(@ColorInt int color) {
                                Log.d("color", Integer.toString(color));
                                Log.d("Red", Integer.toString(Color.red(color)));
                                Log.d("Green", Integer.toString(Color.green(color)));
                                Log.d("Blue", Integer.toString(Color.blue(color)));
                                red = Color.red(color);
                                green = Color.green(color);
                                blue = Color.blue(color);
                                CreateFieldDialogFragment.this.color = color;
                                pick_color.setBackgroundColor(color);
                                Log.d("Pure Hex", Integer.toHexString(color));
                                colorPicker.dismiss();
                            }
                        });
                        colorPicker.show();
                    }
                });
                enterFieldName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 500) {
                            String limit500 = editable.toString().substring(0, 500);
                            enterFieldName.setText(limit500);
                            Toast toast = Toast.makeText(getActivity(), R.string.less_500, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
                positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fieldName = enterFieldName.getText().toString();
                        if (fieldName.equals("")) {
                            Toast toast = Toast.makeText(CreateFieldDialogFragment.this.getActivity(), R.string.where_is_field_name, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
                            Toast toast = Toast.makeText(CreateFieldDialogFragment.this.getActivity(), R.string.field_with_same_name, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } else if (read_answer.isChecked() && select_locale.getSelectedItemPosition() == select_locale_adapter.getCount()) {
                            Toast toast = Toast.makeText(CreateFieldDialogFragment.this.getActivity(), R.string.select_locale, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        } else {
                            int check_type = select_check_type.getSelectedItemPosition();
                            if (select_locale.getSelectedItemPosition() < select_locale_adapter.getCount()
                                    && select_locale.getSelectedItemPosition() >= 0) {
                                int i = select_locale.getSelectedItemPosition();
                                Locale locale = localeList.get(i);
                                locale_short_name = locale.getLanguage() + "," + locale.getCountry();
                                ((Contact) CreateFieldDialogFragment.this.getActivity())
                                        .createField(fieldName, Contract.Field.STRING_DATA_TYPE,
                                                check_type, read_answer.isChecked(), locale_short_name, color);
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory(MainActivity.ANALYTICS_CATEGORY)
                                        .setAction(ActivityUtility.getVersionCode(CreateFieldDialogFragment.this.getActivity()) + "selecting locale")
                                        .build());
                            } else {
                                ((Contact) CreateFieldDialogFragment.this.getActivity())
                                        .createField(fieldName, Contract.Field.STRING_DATA_TYPE,
                                                check_type, read_answer.isChecked(), null, color);
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
    void createField(String field_name, int question_data_type, int check_type, boolean read_answer, String locale, int color);
}
    public void onInit(int status) {
        try {
            Locale[] locales = Locale.getAvailableLocales();
            for (Locale locale : locales) {
                if (locale.toString().equals("en_US_POSIX")) continue;
                int res = textToSpeech.isLanguageAvailable(locale);
                if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    localeList.add(locale);
                    locale_display_names.add(locale.getDisplayName());
                }
            }
            locale_display_names.add(getString(R.string.select_locale));
        } catch (NullPointerException e) {
            if ( read_answer != null ) {
                read_answer.setVisibility(View.GONE);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        while (true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                CreateFieldDialogFragment.this.dismiss();
                                Toast toast = Toast.makeText(CreateFieldDialogFragment.this.getActivity(), R.string.error, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                            if (read_answer != null) {
                                read_answer.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                }).start();
            }
        }
    }
}



