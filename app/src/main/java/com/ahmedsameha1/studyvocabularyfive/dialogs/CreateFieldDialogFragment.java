package com.ahmedsameha1.studyvocabularyfive.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 7/21/2015.
 */
public class CreateFieldDialogFragment extends DialogFragment {
    EditText enterFieldName;
/*    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }*/
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        final DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getContentResolver());
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
                final Spinner spinner = (Spinner) alertDialog.findViewById(R.id.field_type_spinner);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.field_types));
                spinner.setAdapter(arrayAdapter);
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

//                        CheckBox notebookCheckbox = (CheckBox) alertDialog.findViewById(R.id.notebook_field_checkbox);
//                        int type = notebookCheckbox.isChecked()? Contract.Field.NOTEBOOK_TYPE : Contract.Field.DEFAULT_TYPE;
                        String fieldName = enterFieldName.getText().toString();
                        if (fieldName.equals("")) {
                            Toast toast = Toast.makeText(getActivity(), "Where is the field name?!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if (databaseOperations.checkFieldDuplication(fieldName)) {
                            Toast toast = Toast.makeText(getActivity(), "There is an field with the same name!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else if ( spinner.getSelectedItemPosition() == 0 ) {
                            Toast toast = Toast.makeText(getActivity(), R.string.select_a_field_type, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        } else {
                            int type = spinner.getSelectedItemPosition() - 1;
                            databaseOperations.createNewField(fieldName, type);
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

}



