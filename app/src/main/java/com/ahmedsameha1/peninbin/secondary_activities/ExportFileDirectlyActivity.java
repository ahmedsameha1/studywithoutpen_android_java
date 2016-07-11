package com.ahmedsameha1.peninbin.secondary_activities;

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
                        finish();
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
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        finish();
                                        ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this,intent.getData(),false);
                                    }
                                })
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.will_notify_when_process_complete,Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        toast = Toast.makeText(ExportFileDirectlyActivity.this,R.string.shut_down_warning,Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.TOP, 0, 400);
                                        toast.show();
                                        finish();
                                        ExportImportDatabase.startImportThread(ExportFileDirectlyActivity.this,intent.getData(),true);
                                    }
                                })
                                .create().show();
                    }
                })
                .create().show();
    }
}
