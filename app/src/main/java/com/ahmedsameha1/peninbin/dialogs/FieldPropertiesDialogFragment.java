package com.ahmedsameha1.peninbin.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
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
import com.ahmedsameha1.peninbin.activities.EmulateFieldActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.adapters.SpinnerWithHintAdapter;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Money;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ahmed on 28/04/16.
 */
public class FieldPropertiesDialogFragment extends DialogFragment {
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
    private DatabaseReadOperations databaseReadOperations;
    private String locale_short_name;
    private boolean isReadAnswer;
    private int check_type, field_type;
    private CheckBox read_answer;
    private Spinner select_locale;
    private CursorLoaderAndUpdateDatabase cursorLoaderAndUpdateDatabase;
    private Button positive_button;
    private int index_of_locale;
    private String field_name;
    private Tracker mTracker;
    private boolean emulated;
    private int red = 255, green = 255, blue = 255, field_color, new_color = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        locale_display_names = getArguments().getStringArrayList(LOCALE_DISPLAY_NAMES);
        localeList = (ArrayList<Locale>) getArguments().getSerializable(LOCALE_LIST);
        tts_available = getArguments().getBoolean(TTS_AVAILABLE);
        locale_available = getArguments().getBoolean(LOCALE_AVAILABLE, false);
        index_of_locale = getArguments().getInt(INDEX_OF_LOCALE, -1);
        databaseReadOperations = new DatabaseReadOperations(getActivity());
        cursorLoaderAndUpdateDatabase = new CursorLoaderAndUpdateDatabase(getActivity());
        isReadAnswer = databaseReadOperations.isReadAnswer(current_field_id);
        locale_short_name = databaseReadOperations.getFieldLocale(current_field_id);
        check_type = databaseReadOperations.getFieldCheckType(current_field_id);
        field_name = databaseReadOperations.getFieldName(current_field_id);
        field_color = databaseReadOperations.getFieldColor(current_field_id);
        new_color = field_color;
        red = Color.red(field_color);
        green = Color.green(field_color);
        blue = Color.blue(field_color);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        emulated = databaseReadOperations.isEmulated(current_field_id) > Constants.NOT_EMULATED;
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
                builder.setTitle(R.string.field_properties)
                        .setView(inflater.inflate(R.layout.field_properties_dialog_fragment, null))
                        .setPositiveButton(R.string.ok, null)
                        .setNegativeButton(R.string.cancel, null).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final EditText new_field_name = (EditText) alertDialog.findViewById(R.id.enter_new_field_name);
                final Spinner select_check_type = (Spinner) alertDialog.findViewById(R.id.check_type_spinner);
                read_answer = (CheckBox) alertDialog.findViewById(R.id.read_answer);
                select_locale = (Spinner) alertDialog.findViewById(R.id.locales_spinner);
                Button emulate_field = (Button) alertDialog.findViewById(R.id.emulate_field);
                final Button pick_color = (Button) alertDialog.findViewById(R.id.color);
                positive_button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                new_field_name.setText(field_name);
                read_answer.setChecked(isReadAnswer);
                emulate_field.setText(emulated ? FieldPropertiesDialogFragment.this.getResources().getString(R.string.disable_emulation)
                        : FieldPropertiesDialogFragment.this.getResources().getString(R.string.enable_emulation));
                final ArrayAdapter<String> check_type_adapter = new ArrayAdapter<String>(FieldPropertiesDialogFragment.this.getActivity(),
                        android.R.layout.simple_spinner_item, FieldPropertiesDialogFragment.this.getResources().getStringArray(R.array.check_type));
                check_type_adapter.setDropDownViewResource
                        (android.R.layout.simple_spinner_dropdown_item);
                select_check_type.setAdapter(check_type_adapter);
                select_check_type.setSelection(check_type);
                final SpinnerWithHintAdapter spinnerWithHintAdapter =
                        new SpinnerWithHintAdapter(FieldPropertiesDialogFragment.this.getActivity(), android.R.layout.simple_spinner_item, locale_display_names);


                if (tts_available && locale_available) {
                    spinnerWithHintAdapter.setDropDownViewResource
                            (android.R.layout.simple_spinner_dropdown_item);
                    select_locale.setAdapter(spinnerWithHintAdapter);
                    select_locale.setSelection(index_of_locale);
                    select_locale.setVisibility(View.VISIBLE);
                } else {
                    read_answer.setChecked(false);
                }
                pick_color.setBackgroundColor(field_color);
                read_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            if (!tts_available) {
                                Intent installIntent = new Intent();
                                installIntent.setAction(
                                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                                FieldPropertiesDialogFragment.this.startActivity(installIntent);
                                FieldPropertiesDialogFragment.this.dismiss();
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
                                new_color = color;
                                pick_color.setBackgroundColor(color);
                                Log.d("Pure Hex", Integer.toHexString(color));
                                colorPicker.dismiss();
                            }
                        });
                        colorPicker.show();
                    }
                });
                emulate_field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                                && !ImportTextFile.isThreadRunning()) {
                            if (emulated) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(FieldPropertiesDialogFragment.this.getActivity());
                                builder2.setMessage(FieldPropertiesDialogFragment.this.getString(R.string.sure_disable_emulation))
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog1, int which) {
                                                Emulation.startUnemulateThread(FieldPropertiesDialogFragment.this.getActivity(), current_field_id);
                                                alertDialog.dismiss();
                                                FieldPropertiesDialogFragment.this.dismiss();
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .create().show();

                            } else {
                                if (databaseReadOperations.getRealEntriesNumber(current_field_id,
                                        Contract.Field.STRING_DATA_TYPE) > 0) {
                                    Intent intent = new Intent(FieldPropertiesDialogFragment.this.getActivity(), EmulateFieldActivity.class);
                                    intent.putExtra(FieldActivity.FIELD_ID, current_field_id);
                                    FieldPropertiesDialogFragment.this.getActivity().startActivity(intent);
                                    FieldPropertiesDialogFragment.this.dismiss();
                                } else {
                                    Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.no_entries_in_field, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            }
                        } else {
                            Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.wait_until_current_export_import_emulation_finish, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
                new_field_name.addTextChangedListener(new TextWatcher() {
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
                            new_field_name.setText(limit500);
                            Toast toast = Toast.makeText(getActivity(), R.string.less_500, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
                positive_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (new_field_name.getText().toString().length() > 0) {
                            if (read_answer.isChecked() && select_locale.getSelectedItemPosition() == spinnerWithHintAdapter.getCount()) {
                                Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.select_locale, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            } else if (select_check_type.getSelectedItemPosition() == check_type_adapter.getCount()) {
                                Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.select_a_check_type, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            } else if ((!new_field_name.getText().toString().equals(field_name)) && databaseReadOperations.checkFieldDuplication(new_field_name.getText().toString())) {
                                Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.field_same_name, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            } else {
                                if (!new_field_name.getText().toString().equals(field_name)) {
                                    if (!Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                                            && !ImportTextFile.isThreadRunning()) {
                                        cursorLoaderAndUpdateDatabase.updateFieldName(current_field_id, new_field_name.getText().toString());
                                        ((Contact) FieldPropertiesDialogFragment.this.getActivity()).updateFieldName(new_field_name.getText().toString());
                                    } else {
                                        Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.change_field_name_background_thread, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                    }
                                }
                                cursorLoaderAndUpdateDatabase.updateFieldCheckType(current_field_id, select_check_type.getSelectedItemPosition());
                                cursorLoaderAndUpdateDatabase.updateFieldReadAnswer(current_field_id, read_answer.isChecked());
                                int selected_locale_position = select_locale.getSelectedItemPosition();
                                if (selected_locale_position < spinnerWithHintAdapter.getCount()
                                        && selected_locale_position >= 0) {
                                    Locale locale = localeList.get(selected_locale_position);
                                    locale_short_name = locale.getLanguage() + "," + locale.getCountry();
                                    cursorLoaderAndUpdateDatabase.updateFieldLocale(current_field_id, locale_short_name);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory(FieldActivity.ANALYTICS_CATEGORY)
                                            .setAction((Money.getMe(getActivity()) ? "ME" : "") + ActivityUtility.getVersionCode(FieldPropertiesDialogFragment.this.getActivity()) + "selecting locale")
                                            .build());
                                } else {
                                    cursorLoaderAndUpdateDatabase.updateFieldLocale(current_field_id, null);
                                }
                                if (new_color != field_color) {
                                    cursorLoaderAndUpdateDatabase.updateFieldColor(current_field_id, new_color);
                                }
                                FieldPropertiesDialogFragment.this.dismiss();
                            }
                        } else {
                            Toast toast = Toast.makeText(FieldPropertiesDialogFragment.this.getActivity(), R.string.no_field_name, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    public interface Contact {
        void updateFieldName(String name);
    }
}
