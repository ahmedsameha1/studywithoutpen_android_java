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
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExportFileDirectlyActivity.this.finish();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder importUserUsageData = new AlertDialog.Builder(ExportFileDirectlyActivity.this);
                        importUserUsageData
                                .setMessage(R.string.import_user_usage)
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog12, int which12) {
                                        Toast toast = Toast.makeText(ExportFileDirectlyActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        toast = Toast.makeText(ExportFileDirectlyActivity.this, R.string.shut_down_warning, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        ExportFileDirectlyActivity.this.finish();
                                        ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this, intent.getData().getPath(), false);
                                    }
                                })
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog1, int which1) {
                                        Toast toast = Toast.makeText(ExportFileDirectlyActivity.this, R.string.will_notify_when_process_complete, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        toast = Toast.makeText(ExportFileDirectlyActivity.this, R.string.shut_down_warning, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        ExportFileDirectlyActivity.this.finish();
                                        ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this, intent.getData().getPath(), true);
                                    }
                                })
                                .create().show();
                    }
                });
                Dialog dialog = ask_import_fields.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        new ShowEula(ExportFileDirectlyActivity.this).show();
                    }
                });
        dialog.show();
    }
}
