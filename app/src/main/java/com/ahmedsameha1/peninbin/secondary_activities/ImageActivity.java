package com.ahmedsameha1.peninbin.secondary_activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.IntroActivity;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        int image_resource_id = getIntent().getIntExtra(IntroActivity.IMAGE_RESOURCE_ID, 0);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageResource(image_resource_id);
    }
}
