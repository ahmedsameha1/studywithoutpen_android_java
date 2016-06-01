package com.ahmedsameha1.peninbin.secondary_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.MainActivity;

import java.util.Random;

/**
 * Created by ahmed on 31/05/16.
 */
public class RefreshingAppUsageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshing_app_usage);
        TextView refreshing_message_textview = (TextView) findViewById(R.id.refreshing_message);
        Button open = (Button) findViewById(R.id.open);
        String[] refreshing_messages = getResources().getStringArray(R.array.refreshing_messages);
        int random = new Random().nextInt(refreshing_messages.length);
        refreshing_message_textview.setText(refreshing_messages[random]);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RefreshingAppUsageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
