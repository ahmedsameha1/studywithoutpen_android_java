package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by ahmed on 12/20/2015.
 */
public class ExportFieldsDialogFragment extends DialogFragment {
    public static final String EXPORT_FIELD_DIALOG_FRAGMENT = "exportFieldDialogFragment";

    ListView listView;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog = builder.setView(inflater.inflate(R.layout.export_fields_dialog_fragment,null))
                .setPositiveButton(R.string.export_fields,null)
                .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
                listView = (ListView) alertDialog.findViewById(R.id.export_fields_list_view);
                SimpleCursorAdapter simpleCursorAdapter =
                        new SimpleCursorAdapter(getActivity(),
                                android.R.layout.simple_list_item_multiple_choice,
                                databaseOperations.getFieldList(),
                                new String[] {Contract.Field.COLUMN_NAME_FIELD_NAME},
                                new int[] {android.R.id.text1},0);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(simpleCursorAdapter);
            }
        });
        return alertDialog;
    }
}
