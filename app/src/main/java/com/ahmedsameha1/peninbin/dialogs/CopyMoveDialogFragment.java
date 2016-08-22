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
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.utils.Constants;

/**
 * Created by ahmed on 12/20/2015.
 */
public class CopyMoveDialogFragment extends DialogFragment {

    private DatabaseOperations databaseOperations;
    private Context context;
    private int fieldType;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    private long current_field_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseOperations = new DatabaseOperations(getActivity());
        current_field_id = getArguments().getLong(StudyActivity.CURRENT_FIELD_ID);
        fieldType = databaseOperations.getFieldType(current_field_id);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(getActivity());
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.copy_move_dialog_fragment, null))
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button copy_field = (Button) alertDialog.findViewById(R.id.copy_field);
                Button copy_entries = (Button) alertDialog.findViewById(R.id.copy_entries);
                Button move_entries = (Button) alertDialog.findViewById(R.id.move_entries);

                copy_field.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismiss();
                    }
                });
                copy_entries.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismiss();
                    }
                });
                move_entries.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

            }

        });
        return alertDialog;
    }


}


