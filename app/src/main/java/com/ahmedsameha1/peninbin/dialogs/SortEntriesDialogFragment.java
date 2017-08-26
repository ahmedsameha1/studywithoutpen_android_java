package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.utils.SortOptions;

/**
 * Created by ahmed on 12/20/2015.
 */
public class SortEntriesDialogFragment extends DialogFragment {
    public static final String INITIAL_SORT_OPTION = "intial_sort_option";
    private DatabaseReadOperations databaseOperations;
    private Context context;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private long current_field_id;
    private SortOptions sort_option;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseOperations = new DatabaseReadOperations(getActivity());
        current_field_id = getArguments().getLong(Constants.CURRENT_FIELD_ID);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(getActivity());
        sort_option = (SortOptions) getArguments().getSerializable(INITIAL_SORT_OPTION);
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
                Button sort_entry_order_asc = (Button) alertDialog.findViewById(R.id.sort_entry_order_asc);
                Button sort_entry_order_desc = (Button) alertDialog.findViewById(R.id.sort_entry_order_desc);
                Button sort_entry_rank_asc = (Button) alertDialog.findViewById(R.id.sort_rank_asc);
                Button sort_entry_rank_desc = (Button) alertDialog.findViewById(R.id.sort_rank_desc);
                Button sort_entry_wrongness_asc = (Button) alertDialog.findViewById(R.id.sort_wrongness_asc);
                Button sort_entry_wrongness_desc = (Button) alertDialog.findViewById(R.id.sort_wrongness_desc);

                sort_question_asc.setVisibility(View.VISIBLE);
                sort_question_desc.setVisibility(View.VISIBLE);

                switch (sort_option) {
                    case SORT_CREATION_DATE_DESC:
                        sort_creation_desc.setTextColor(Color.RED);
                        break;
                    case SORT_ANSWER_DESC:
                        sort_answer_desc.setTextColor(Color.RED);
                        break;
                    case SORT_QUESTION_ASC:
                        sort_question_asc.setTextColor(Color.RED);
                        break;
                    case SORT_ANSWER_ASC:
                        sort_answer_asc.setTextColor(Color.RED);
                        break;
                    case SORT_DATE_DESC:
                        sort_date_desc.setTextColor(Color.RED);
                        break;
                    case SORT_DATE_ASC:
                        sort_date_asc.setTextColor(Color.RED);
                        break;
                    case SORT_QUESTION_DESC:
                        sort_question_desc.setTextColor(Color.RED);
                        break;
                    case SORT_CREATION_DATE_ASC:
                        sort_creation_asc.setTextColor(Color.RED);
                        break;
                    case SORT_ORDER_ASC:
                        sort_entry_order_asc.setTextColor(Color.RED);
                        break;
                    case SORT_ORDER_DESC:
                        sort_entry_order_desc.setTextColor(Color.RED);
                        break;
                    case SORT_RANK_ASC:
                        sort_entry_rank_asc.setTextColor(Color.RED);
                        break;
                    case SORT_RANK_DESC:
                        sort_entry_rank_desc.setTextColor(Color.RED);
                        break;
                    case SORT_WRONGNESS_ASC:
                        sort_entry_wrongness_asc.setTextColor(Color.RED);
                        break;
                    case SORT_WRONGNESS_DESC:
                        sort_entry_wrongness_desc.setTextColor(Color.RED);
                        break;
                }
                if (databaseOperations.isThereEntryOrder(current_field_id)) {
                    sort_entry_order_asc.setVisibility(View.VISIBLE);
                    sort_entry_order_desc.setVisibility(View.VISIBLE);
                }

                sort_answer_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_ANSWER_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_ANSWER_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_answer_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_ANSWER_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_ANSWER_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_question_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_QUESTION_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_QUESTION_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_question_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_QUESTION_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_QUESTION_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_date_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_DATE_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_DATE_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_date_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_DATE_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_DATE_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_creation_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_CREATION_DATE_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_CREATION_DATE_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_creation_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_CREATION_DATE_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_CREATION_DATE_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_order_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_ORDER_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_ORDER_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_order_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_ORDER_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_ORDER_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_rank_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_RANK_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_RANK_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_rank_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_RANK_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_RANK_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_wrongness_asc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_WRONGNESS_ASC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_WRONGNESS_ASC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
                sort_entry_wrongness_desc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contact.sort(SortOptions.SORT_WRONGNESS_DESC);
                        mySQLiteCursorLoader.updateFieldSort(current_field_id, SortOptions.SORT_WRONGNESS_DESC);
                        SortEntriesDialogFragment.this.dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }

    public interface Contact {
        public void sort(SortOptions sortOption);
    }
}


