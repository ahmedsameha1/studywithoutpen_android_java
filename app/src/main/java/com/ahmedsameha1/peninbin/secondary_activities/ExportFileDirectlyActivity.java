package com.ahmedsameha1.peninbin.secondary_activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.utils.ShowEula;

/**
 * Created by me on 11/07/16.
 */
public class ExportFileDirectlyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        AlertDialog.Builder ask_import_fields = new AlertDialog.Builder(this);
        ask_import_fields
                .setMessage(getString(R.string.ask_import_fields))
                .setNegativeButton("NO", (dialog, which) -> finish())
                .setPositiveButton("YES", (dialog, which) -> {
                    AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(ExportFileDirectlyActivity.this);
                    importUserUsageData
                            .setMessage(R.string.import_user_usage)
                            .setNegativeButton("NO", (dialog12, which12) -> {
                                Toast toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                finish();
                                ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this,intent.getData().getPath(),false);
                            })
                            .setPositiveButton("YES", (dialog1, which1) -> {
                                Toast toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                                finish();
                                ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this,intent.getData().getPath(),true);
                            })
                            .create().show();
                });
                Dialog dialog = ask_import_fields.create();
                dialog.setOnShowListener(dialogInterface -> new ShowEula(ExportFileDirectlyActivity.this).show());
        dialog.show();
    }
}
