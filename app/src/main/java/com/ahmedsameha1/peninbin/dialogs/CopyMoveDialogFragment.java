package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.CopyMoveEntriesActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by ahmed on 12/20/2015.
 */
public class CopyMoveDialogFragment extends DialogFragment {

    public static final String COPY_MOVE_DIALOG_FRAGMENT = "copy_move_dialog_fragment";
    private long current_field_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.copy_move_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button copy_field = (Button) alertDialog.findViewById(R.id.copy_field);
                Button copy_move_entries = (Button) alertDialog.findViewById(R.id.copy_move_entries);

                copy_field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CopyFieldDialogFragment copyFieldDialogFragment = new CopyFieldDialogFragment();
                        copyFieldDialogFragment.show(getActivity().getSupportFragmentManager(),
                                CopyFieldDialogFragment.COPY_FIELD_DIALOG_FRAGMENT);
                        dismiss();
                    }
                });
                copy_move_entries.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(getActivity(),CopyMoveEntriesActivity.class);
                        intent1.putExtra(FieldActivity.FIELD_ID, current_field_id);
                        startActivity(intent1);
                        dismiss();
                    }
                });


            }

        });
        return alertDialog;
    }


}


