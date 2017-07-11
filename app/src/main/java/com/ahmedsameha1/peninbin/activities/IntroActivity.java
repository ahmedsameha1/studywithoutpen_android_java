package com.ahmedsameha1.peninbin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.AppIntroSampleSlide;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_main));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_study_period));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_enhanced_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_fully_random_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_emulation));
    }
}
