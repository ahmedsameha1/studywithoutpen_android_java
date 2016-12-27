package com.ahmedsameha1.peninbin.fragments;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by ahmed on 8/18/2015.
 */
public class RetainedFragment extends Fragment {
    private String [][] listForTest_FullyRandomTestActivity;
    private String [][] listForTest_EnhancedTestActivity;
    private String [][] listForTest_QuickTestActivity;
    private String [][] listForTest_StudyDuringActivity;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String[][] getListForTest_FullyRandomTestActivity() {
        return listForTest_FullyRandomTestActivity;
    }

    public void setListForTest_FullyRandomTestActivity(String[][] listForTest_FullyRandomTestActivity) {
        this.listForTest_FullyRandomTestActivity = listForTest_FullyRandomTestActivity;
    }

    public String[][] getListForTest_EnhancedTestActivity() {
        return listForTest_EnhancedTestActivity;
    }

    public void setListForTest_EnhancedTestActivity(String[][] listForTest_EnhancedTestActivity) {
        this.listForTest_EnhancedTestActivity = listForTest_EnhancedTestActivity;
    }

    public String[][] getListForTest_StudyDuringActivity() {
        return listForTest_StudyDuringActivity;
    }

    public void setListForTest_StudyDuringActivity(String[][] listForTest_StudyDuringActivity) {
        this.listForTest_StudyDuringActivity = listForTest_StudyDuringActivity;
    }
    public String[][] getListForTest_QuickTestActivity() {
        return listForTest_QuickTestActivity;
    }

    public void setListForTest_QuickTestActivity(String[][] listForTest_QuickTestActivity) {
        this.listForTest_QuickTestActivity = listForTest_QuickTestActivity;
    }
}
