package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ahmed on 12/20/2015.
 */
public class SearchDateOrTextDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    private static final int TEXT = 0;
    public static final int DATE = 1;
    private DatabaseOperations databaseOperations;
    private Context context;
    private EditText enterText;
    private String text;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.search_date_or_text_dialog_fragment, null))
                .setPositiveButton(R.string.search_hint, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                databaseOperations = new DatabaseOperations(context);
                Button search_date = (Button) alertDialog.findViewById(R.id.search_date);
                enterText = (EditText) alertDialog.findViewById(R.id.search_text);
                Button positiveButton = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( enterText.getText().toString().length() > 0 ) {
                            ( ( Contact) getActivity() ).search(enterText.getText().toString());
                            dismiss();
                        } else {
                            Toast toast = Toast.makeText(getActivity(),R.string.enter_search_text, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        }
                    }
                });
                search_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                final Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(year, monthOfYear, dayOfMonth);
                                String formated = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar1.getTime());
                                ( ( Contact) context ).search(formated);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                        dismiss();
                    }
                });
            }

        });
        return alertDialog;
    }

    public interface Contact {
        public void search(String value);
    }
}


