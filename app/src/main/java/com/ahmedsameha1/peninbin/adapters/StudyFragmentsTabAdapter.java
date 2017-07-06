package com.ahmedsameha1.peninbin.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ahmedsameha1.peninbin.fragments.StudyEntryFragment;
import com.ahmedsameha1.peninbin.fragments.TestEntryFragment;

import java.util.LinkedHashMap;

/**
 * Created by ahmed on 7/6/17.
 */

public class StudyFragmentsTabAdapter extends FragmentPagerAdapter {
    LinkedHashMap<String, String[]> data;

    public StudyFragmentsTabAdapter(FragmentManager fm, LinkedHashMap<String, String[]> data ) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        if ( position == 0 ) {
            return StudyEntryFragment.newInstance(data);
        } else if ( position == 1 ) {
            return TestEntryFragment.newInstance(data);
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
            return "Study Entry";
        } else if (position == 1) {
            return "Test Entry";
        }
        return "";
    }
}
