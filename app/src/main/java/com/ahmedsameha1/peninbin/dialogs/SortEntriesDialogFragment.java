package com.ahmedsameha1.peninbin.dialogs;

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

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.utils.SortOptions;

/**
 * Created by ahmed on 12/20/2015.
 */
public class SortEntriesDialogFragment extends DialogFragment {
    private DatabaseReadOperations databaseOperations;
    private Context context;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseOperations = new DatabaseReadOperations(getActivity());
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
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
        alertDialog.setOnShowListener(dialog -> {
            Button sort_answer_desc = (Button) alertDialog.findViewById(R.id.sort_answer_desc);
            Button sort_answer_asc = (Button) alertDialog.findViewById(R.id.sort_answer_asc);
            Button sort_question_desc = (Button) alertDialog.findViewById(R.id.sort_question_desc);
            Button sort_question_asc = (Button) alertDialog.findViewById(R.id.sort_question_asc);
            Button sort_date_desc = (Button) alertDialog.findViewById(R.id.sort_date_desc);
            Button sort_date_asc = (Button) alertDialog.findViewById(R.id.sort_date_asc);
            Button sort_creation_desc = (Button) alertDialog.findViewById(R.id.sort_creation_desc);
            Button sort_creation_asc = (Button) alertDialog.findViewById(R.id.sort_creation_asc);
            Button sort_entry_order = (Button) alertDialog.findViewById(R.id.sort_entry_order);

                sort_question_asc.setVisibility(View.VISIBLE);
                sort_question_desc.setVisibility(View.VISIBLE);

            if ( databaseOperations.isThereEntryOrder(current_field_id) ){
                sort_entry_order.setVisibility(View.VISIBLE);
            }

            sort_answer_desc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_ANSWER_DESC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_ANSWER_DESC);
                dismiss();
            });
            sort_answer_asc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_ANSWER_ASC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_ANSWER_ASC);
                dismiss();
            });
            sort_question_desc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_QUESTION_DESC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_QUESTION_DESC);
                dismiss();
            });
            sort_question_asc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_QUESTION_ASC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_QUESTION_ASC);
                dismiss();
            });
            sort_date_desc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_DATE_DESC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_DATE_DESC);
                dismiss();
            });
            sort_date_asc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_DATE_ASC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_DATE_ASC);
                dismiss();
            });
            sort_creation_desc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_CREATION_DATE_DESC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_CREATION_DATE_DESC);
                dismiss();
            });
            sort_creation_asc.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_CREATION_DATE_ASC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_CREATION_DATE_ASC);
                dismiss();
            });
            sort_entry_order.setOnClickListener(v -> {
                contact.sort(SortOptions.SORT_ENTRY_ORDER_ASC);
                mySQLiteCursorLoader.updateFieldSort(current_field_id,SortOptions.SORT_ENTRY_ORDER_ASC);
                dismiss();
            });
        });
        return alertDialog;
    }

    public interface Contact {
        public void sort(SortOptions sortOption);
    }
}


