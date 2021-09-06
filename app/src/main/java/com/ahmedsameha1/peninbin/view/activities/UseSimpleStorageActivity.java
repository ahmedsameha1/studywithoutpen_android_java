package com.ahmedsameha1.peninbin.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.intent_services.ImportSWPFileIntentService;
import com.ahmedsameha1.peninbin.intent_services.ImportTextFileIntentService;
import com.anggrayudi.storage.SimpleStorageHelper;

import kotlin.Unit;

public class UseSimpleStorageActivity extends AppCompatActivity {
    public static final String IMPORT_FROM_WHAT = "import_from_app_file";
    public static final int FROM_APP_FILE = 287;
    public static final int FROM_TEXT_FILE = 61687;
    private SimpleStorageHelper simpleStorageHelper = new SimpleStorageHelper(this);
    private int requestCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCode = getIntent().getIntExtra(IMPORT_FROM_WHAT, 0);
        simpleStorageHelper.setOnFileSelected((requestCode, documentFileList) -> {
            if (documentFileList.size() == 1) {
                if (requestCode == FROM_APP_FILE) {
                    AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(this);
                    importUserUsageData
                            .setMessage(R.string.import_user_usage)
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog13, int which) {
                                    Toast toast = Toast.makeText(UseSimpleStorageActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    Intent intent = new Intent(UseSimpleStorageActivity.this, ImportSWPFileIntentService.class);
                                    intent.putExtra(ImportSWPFileIntentService.URI, documentFileList.get(0).getUri().toString());
                                    intent.putExtra(ImportSWPFileIntentService.USER_USAGE, false);
                                    UseSimpleStorageActivity.this.startService(intent);
                                    finish();
                                }
                            })
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog12, int which) {
                                    Toast toast = Toast.makeText(UseSimpleStorageActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                    Intent intent = new Intent(UseSimpleStorageActivity.this, ImportSWPFileIntentService.class);
                                    intent.putExtra(ImportSWPFileIntentService.URI, documentFileList.get(0).getUri().toString());
                                    intent.putExtra(ImportSWPFileIntentService.USER_USAGE, true);
                                    UseSimpleStorageActivity.this.startService(intent);
                                    finish();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    finish();
                                }
                            })
                            .create().show();
                } else if (requestCode == FROM_TEXT_FILE) {
                        Toast toast = Toast.makeText(this, R.string.will_notify_when_process_complete, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                        Intent intent = new Intent(this, ImportTextFileIntentService.class);
                        intent.putExtra(ImportTextFileIntentService.URI, documentFileList.get(0).getUri().toString());
                        this.startService(intent);
                        finish();
                }
            }
            return Unit.INSTANCE;
        });
        simpleStorageHelper.openFilePicker(requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        simpleStorageHelper.getStorage().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        simpleStorageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        simpleStorageHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        simpleStorageHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}