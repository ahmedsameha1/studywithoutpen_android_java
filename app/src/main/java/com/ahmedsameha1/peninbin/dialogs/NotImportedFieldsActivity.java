package com.ahmedsameha1.peninbin.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

import java.util.ArrayList;

/**
 * Created by ahmed on 1/24/2016.
 */
public class NotImportedFieldsActivity extends Activity {
    public static final String NOTIFICATION_ID = "notification_id";
    private TextView notImportedFieldsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> notImportedFields = getIntent().getStringArrayListExtra(NOTIFICATION_ID);
        String notImportedFieldsNames = "( ";
        for (int i = 0; i < notImportedFields.size(); i++) {
            if (i < notImportedFields.size() - 1) {
                notImportedFieldsNames += notImportedFields.get(i) + ", ";
            } else {
                notImportedFieldsNames += notImportedFields.get(i) + " )";
            }
            setContentView(R.layout.activity_not_imported_fields);
            notImportedFieldsTextView = (TextView) findViewById(R.id.not_imported_fields);
            notImportedFieldsTextView.setText(getString(R.string.not_imported_fields_notification, notImportedFieldsNames));
        }
    }
}
