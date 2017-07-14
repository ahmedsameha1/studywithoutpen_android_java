package com.ahmedsameha1.peninbin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.AppIntroSampleSlide;
import com.ahmedsameha1.peninbin.secondary_activities.ImageActivity;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    public static final String IMAGE_RESOURCE_ID = "image_resorce_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_main));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_study_period));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_enhanced_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_fully_random_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_emulation));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.intro_change_background_color;
    }

    public void introImageClicked( View view ) {
        int id = view.getId();
        int image_resource_id = 0;
        switch (id) {
            case R.id.intro_main:
                image_resource_id = R.drawable.intro_main;
                break;
        }
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(IMAGE_RESOURCE_ID, image_resource_id);
        startActivity(intent);
    }
}
