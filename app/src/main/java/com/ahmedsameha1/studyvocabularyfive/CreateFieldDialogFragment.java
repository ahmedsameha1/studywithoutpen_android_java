package com.ahmedsameha1.studyvocabularyfive;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog alertDialog =
        builder.setView(inflater.inflate(R.layout.create_field_dialog_fragment,null))
                .setPositiveButton(R.string.create_field, null)
                .setNegativeButton(R.string.cancel, null).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText enterFieldName = (EditText) alertDialog.findViewById(R.id.enter_field_name);
                        String string = enterFieldName.getText().toString();
                        if ( string.contains(" ") ) {
                            Toast.makeText(getActivity(),getString(R.string.no_space_allowed),Toast.LENGTH_SHORT).show();
                        } else {
                            ((Contact) getActivity()).createNewField(CreateFieldDialogFragment.this);
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
    public interface Contact {
        public void createNewField(CreateFieldDialogFragment dialog);
    }
}
