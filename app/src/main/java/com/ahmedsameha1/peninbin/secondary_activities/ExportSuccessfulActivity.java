package com.ahmedsameha1.peninbin.secondary_activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.R;

import java.io.File;

/**
 * Created by ahmed on 1/2/2016.
 */
public class ExportSuccessfulActivity extends Activity {
    public static final String NOTIFICATION_ID = "notification_id";
    private TextView exportFilePlace, filePath;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_completed);
        path = getIntent().getStringExtra(ExportImportDatabase.PATH);
        int notificationId = getIntent().getIntExtra(NOTIFICATION_ID, ExportImportDatabase.FAILED_EXPORT_NOTIFICATION);
        exportFilePlace = (TextView) findViewById(R.id.export_file_place);
        filePath = (TextView) findViewById(R.id.file_path);

        switch (notificationId) {
            case ExportImportDatabase.FAILED_EXPORT_NOTIFICATION:
                exportFilePlace.setText(R.string.export_failed);
                filePath.setVisibility(View.GONE);
                break;
            case ExportImportDatabase.SUCCESFUL_EXPORT_NOTIFICATION:
                exportFilePlace.setText(R.string.exported_file_in);
                filePath.setText(path);
                break;
        }
    }
    //// TODO: 10/07/16 hardly test export import feature 
}
