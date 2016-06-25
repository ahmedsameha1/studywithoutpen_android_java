package com.ahmedsameha1.peninbin.secondary_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.MainActivity;
import com.ahmedsameha1.peninbin.activities.NotebookFieldActivity;
import com.ahmedsameha1.peninbin.adapters.NotebookFieldActivityRecyclerViewAdapter;

import java.util.Random;

/**
 * Created by ahmed on 31/05/16.
 */
public class ReminderActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        String notebook_text = getIntent().getStringExtra(NotebookFieldActivityRecyclerViewAdapter.NOTE_TEXT);
        TextView notebook_text_textview = (TextView) findViewById(R.id.notebook_text);
        notebook_text_textview.setText(notebook_text);
    }
}
