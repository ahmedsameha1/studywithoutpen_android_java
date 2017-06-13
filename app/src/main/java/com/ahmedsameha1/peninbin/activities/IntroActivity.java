package com.ahmedsameha1.peninbin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.AppIntroSampleSlide;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_fully_random_test));
    }
}
