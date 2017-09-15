package com.ahmedsameha1.peninbin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by ahmed on 9/15/17.
 */

public class RetainedFragment extends Fragment {
    private String[][] listForTestEnhancedTest;
    private String[][] listForTestFullyRandomTest;
    private String[][] listForTestStudyAPeriod;
    private String[][] listForTestQuickTest;
    private String[][] listForTestStudyActivity;
    private String[][] listForTestStudyEntryFragment;

    public String[][] getListForTestEnhancedTest() {
        return listForTestEnhancedTest;
    }

    public void setListForTestEnhancedTest(String[][] listForTestEnhancedTest) {
        this.listForTestEnhancedTest = listForTestEnhancedTest;
    }

    public String[][] getListForTestFullyRandomTest() {
        return listForTestFullyRandomTest;
    }

    public void setListForTestFullyRandomTest(String[][] listForTestFullyRandomTest) {
        this.listForTestFullyRandomTest = listForTestFullyRandomTest;
    }

    public String[][] getListForTestStudyAPeriod() {
        return listForTestStudyAPeriod;
    }

    public void setListForTestStudyAPeriod(String[][] listForTestStudyAPeriod) {
        this.listForTestStudyAPeriod = listForTestStudyAPeriod;
    }

    public String[][] getListForTestQuickTest() {
        return listForTestQuickTest;
    }

    public void setListForTestQuickTest(String[][] listForTestQuickTest) {
        this.listForTestQuickTest = listForTestQuickTest;
    }

    public String[][] getListForTestStudyActivity() {
        return listForTestStudyActivity;
    }

    public void setListForTestStudyActivity(String[][] listForTestStudyActivity) {
        this.listForTestStudyActivity = listForTestStudyActivity;
    }

    public String[][] getListForTestStudyEntryFragment() {
        return listForTestStudyEntryFragment;
    }

    public void setListForTestStudyEntryFragment(String[][] listForTestStudyEntryFragment) {
        this.listForTestStudyEntryFragment = listForTestStudyEntryFragment;
    }

    public String[][] getListForTestTestEntryFragment() {
        return listForTestTestEntryFragment;
    }

    public void setListForTestTestEntryFragment(String[][] listForTestTestEntryFragment) {
        this.listForTestTestEntryFragment = listForTestTestEntryFragment;
    }

    private String[][] listForTestTestEntryFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }
}
