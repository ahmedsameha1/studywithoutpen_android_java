package com.ahmedsameha1.peninbin.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ImportFromWhatDialogFragment extends DialogFragment {
    public static final String IMPORT_FROM_WHAT_DIALOG_FRAGMENT = "import_from_what_dialog_fragment";
    private Context context;
    Contact contact;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contact = (Contact) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.import_from_what_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button app_file = (Button) alertDialog.findViewById(R.id.app_file);
                Button text_file = (Button) alertDialog.findViewById(R.id.text_file);
                ImageButton text_file_help = (ImageButton) alertDialog.findViewById(R.id.text_file_help);
                app_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.importFieldsFromAppFile(ImportFromWhatDialogFragment.this);
                    }
                });
                text_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.importFieldsFromTextFile(ImportFromWhatDialogFragment.this);
                    }
                });
                text_file_help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contact.textFileHelp(ImportFromWhatDialogFragment.this);
                    }
                });
            }
        });
        return alertDialog;
    }

    public interface Contact {
        public void importFieldsFromAppFile(DialogFragment dialogFragment);

        public void importFieldsFromTextFile(DialogFragment dialogFragment);

        public void textFileHelp(DialogFragment dialogFragment);
    }
}


