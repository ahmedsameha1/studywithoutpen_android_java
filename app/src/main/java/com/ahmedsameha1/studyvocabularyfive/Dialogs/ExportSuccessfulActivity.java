package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Database.ExportImportDatabase;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.io.File;

/**
 * Created by ahmed on 1/2/2016.
 */
public class ExportSuccessfulActivity extends Activity {
    public static final String NOTIFICATION_ID = "notification_id";
    private TextView exportFilePlace, filePath;
    private Button openFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_completed);
        int notificationId = getIntent().getIntExtra(NOTIFICATION_ID, ExportImportDatabase.FAILED_EXPORT_NOTIFICATION);
        exportFilePlace = (TextView) findViewById(R.id.export_file_place);
        filePath = (TextView) findViewById(R.id.file_path);
        openFolder = (Button) findViewById(R.id.open_folder);
        switch (notificationId) {
            case ExportImportDatabase.FAILED_EXPORT_NOTIFICATION:
                exportFilePlace.setText(R.string.export_failed);
                openFolder.setVisibility(View.GONE);
                filePath.setVisibility(View.GONE);
                break;
            case ExportImportDatabase.SUCCESFUL_REMOVABLE_STORAGE_NOTIFICATION:
                exportFilePlace.setText(R.string.removable_storage_notification);
                filePath.setText(getString(R.string.file_path,
                        getString(R.string.export_import_file_path,Environment.getExternalStorageDirectory().getAbsolutePath()
                                ,File.separator,ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME)));
                break;
            case ExportImportDatabase.SUCCESFUL_EMULATED_STORAGE_NOTIFICATION:
                exportFilePlace.setText(R.string.emulated_storage_notification);
                filePath.setText(getString(R.string.file_path,
                        getString(R.string.export_import_file_path,Environment.getExternalStorageDirectory().getAbsolutePath()
                                ,File.separator,ExportImportDatabase.EXPORT_IMPORT_DIRECTORY_NAME)));
                break;
        }
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(selectedUri, "resource/folder");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            openFolder.setText(R.string.open_file_manager);
        } else {
            openFolder.setText(R.string.find_file_manager);
        }

        openFolder.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                                              Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                              intent.setDataAndType(selectedUri, "resource/folder");
                                              if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
                                                  startActivity(intent);
                                              } else {
                                                  Uri uri = Uri.parse("market://search?q=file manager");
                                                  Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                                  try {
                                                      startActivity(myAppLinkToMarket);
                                                  } catch (ActivityNotFoundException e) {
                                                      Toast.makeText(ExportSuccessfulActivity.this, "Unable to find market app", Toast.LENGTH_LONG).show();
                                                  }
                                              }
                                          }
                                      }

        );
    }
}
