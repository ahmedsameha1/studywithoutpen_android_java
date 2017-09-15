package com.ahmedsameha1.peninbin.secondary_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.AnalyticsApplication;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.AppIntroSampleSlide;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Money;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class IntroActivity extends AppIntro {

    public static final String IMAGE_RESOURCE_ID = "image_resorce_id";
    private static final String INTRO_KEY = "intro";
    private Tracker mTracker;
    private long time;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance(getString(R.string.excel), getString(R.string.excel_memorizing_texts),
                R.drawable.intro_first_image, 0xF8BBD0));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_app));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_main));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_entry));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_study_period));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_fully_random_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_enhanced_test));
        addSlide(AppIntroSampleSlide.newInstance(R.layout.intro_emulation));
        showSkipButton(false);
        time = System.currentTimeMillis();
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
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
            case R.id.intro_main_menu:
                image_resource_id = R.drawable.intro_main_menu;
                break;
            case R.id.study_a_period:
                image_resource_id = R.drawable.intro_study_a_period;
                break;
            case R.id.study_a_period_dialog:
                image_resource_id = R.drawable.intro_study_a_period_dialog;
                break;
            case R.id.enhanced_test:
                image_resource_id = R.drawable.intro_enhanced_test;
                break;
            case R.id.enhanced_test_dialog:
                image_resource_id = R.drawable.intro_enhanced_test_dialog;
                break;
            case R.id.fully_random_test:
                image_resource_id = R.drawable.intro_fully_random_test;
                break;
            case R.id.fully_random_test_dialog:
                image_resource_id = R.drawable.intro_fully_random_test_dialog;
                break;
            case R.id.emulation:
                image_resource_id = R.drawable.intro_emulation;
                break;
            case R.id.emulation_dialog_button:
                image_resource_id = R.drawable.intro_field_properities_emulation_button;
                break;
            case R.id.intro_entry:
                image_resource_id = R.drawable.intro_entry;
                break;
        }
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(IMAGE_RESOURCE_ID, image_resource_id);
        startActivity(intent);
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        Toast.makeText(this, String.valueOf((System.currentTimeMillis() - time) / 1000), Toast.LENGTH_SHORT).show();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Intro")
                .setAction((Money.getMe(this) ? Money.TRACKING_ME : "") + ActivityUtility.getVersionCode(this) + String.valueOf((System.currentTimeMillis() - time) / 1000))
                .build());
        time = System.currentTimeMillis();
    }

    public static void show(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean hasBeenShown = prefs.getBoolean(INTRO_KEY, false);
        if ( ! hasBeenShown ) {
            context.startActivity(new Intent(context, IntroActivity.class));
            editor.putBoolean(INTRO_KEY, true);
            editor.commit();
        }
    }
}
