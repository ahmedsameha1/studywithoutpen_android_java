package com.ahmedsameha1.studyvocabulary.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.activities.FieldActivity;
import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;

/**
 * Created by ahmed on 12/20/2015.
 */
public class SortEntriesDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";
    private static final int TEXT = 0;
    public static final int DATE = 1;
    private DatabaseOperations databaseOperations;
    private Context context;
    private EditText enterText;
    private String text;
    private int fieldType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldType = getArguments().getInt(FieldActivity.FIELD_TYPE);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Contact contact = (Contact) getActivity();
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.sort_entries_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button sort_answer_desc = (Button) alertDialog.findViewById(R.id.sort_answer_desc);
                Button sort_answer_asc = (Button) alertDialog.findViewById(R.id.sort_answer_asc);
                Button sort_question_desc = (Button) alertDialog.findViewById(R.id.sort_question_desc);
                Button sort_question_asc = (Button) alertDialog.findViewById(R.id.sort_question_asc);
                Button sort_date_desc = (Button) alertDialog.findViewById(R.id.sort_date_desc);
                Button sort_date_asc = (Button) alertDialog.findViewById(R.id.sort_date_asc);
                Button sort_creation_desc = (Button) alertDialog.findViewById(R.id.sort_creation_desc);
                Button sort_creation_asc = (Button) alertDialog.findViewById(R.id.sort_creation_asc);

                if ( fieldType != Contract.Old_Field_DBV6_AppV32.NOTEBOOK_TYPE ) {
                    sort_question_asc.setVisibility(View.VISIBLE);
                    sort_question_desc.setVisibility(View.VISIBLE);
                } else {
                    sort_date_asc.setVisibility(View.VISIBLE);
                    sort_date_desc.setVisibility(View.VISIBLE);
                }

                sort_answer_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_ANSWER_DESC);
                        dismiss();
                    }
                });
                sort_answer_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_ANSWER_ASC);
                        dismiss();
                    }
                });
                sort_question_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_QUESTION_DESC);
                        dismiss();
                    }
                });
                sort_question_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_QUESTION_ASC);
                        dismiss();
                    }
                });
                sort_date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_DATE_DESC);
                        dismiss();
                    }
                });
                sort_date_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_DATE_ASC);
                        dismiss();
                    }
                });
                sort_creation_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_CREATION_DATE_DESC);
                        dismiss();
                    }
                });
                sort_creation_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(FieldActivity.SORT_CREATION_DATE_ASC);
                        dismiss();
                    }
                });
            }

        });
        return alertDialog;
    }

    public interface Contact {
        public void sort(int sort_type);
    }
}


