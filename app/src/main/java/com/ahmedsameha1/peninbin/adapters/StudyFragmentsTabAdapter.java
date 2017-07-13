package com.ahmedsameha1.peninbin.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.fragments.StudyEntryFragment;
import com.ahmedsameha1.peninbin.fragments.TestEntryFragment;

/**
 * Created by ahmed on 7/6/17.
 */

public class StudyFragmentsTabAdapter extends FragmentPagerAdapter {
    private String[][] entries;
    private StringBuilder current_index;
    private AppCompatActivity appCompatActivity;
    private long current_field_id;
    private String[] symbol, customable_text;

    public StudyFragmentsTabAdapter(AppCompatActivity appCompatActivity, String[][] entries, StringBuilder current_index, long current_field_id,
                                    String[] symbol, String[] customable_text) {
        super(appCompatActivity.getSupportFragmentManager());
        this.appCompatActivity = appCompatActivity;
        this.entries = entries;
        this.current_index = current_index;
        this.current_field_id = current_field_id;
        this.symbol = symbol;
        this.customable_text = customable_text;
    }

    @Override
    public Fragment getItem(int position) {
        if ( position == 0 ) {
            return StudyEntryFragment.newInstance(entries, current_index, current_field_id, symbol, customable_text);
        } else if ( position == 1 ) {
            return TestEntryFragment.newInstance(entries, current_index, current_field_id, symbol, customable_text);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ( position == 0 ) {
            return appCompatActivity.getString(R.string.study);
        } else if (position == 1) {
            return appCompatActivity.getString(R.string.test);
        }
        return "";
    }
}
