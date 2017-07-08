package com.ahmedsameha1.peninbin.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.ahmedsameha1.peninbin.adapters.WrongItem;

import java.util.ArrayList;

/**
 * Created by ahmed on 8/18/2015.
 */
public class RetainedFragment extends Fragment {
    private String [][] listForTest_FullyRandomTestActivity;
    private String [][] listForTest_EnhancedTestActivity;
    private String [][] listForTest_QuickTestActivity;
    private String [][] listForTest_StudyDuringActivity;
    private ArrayList<WrongItem> wrongItemArrayList_ask_again_after_test_activity;
    private String [][] study_activity_entries;

    public String[][] getStudy_activity_entries() {
        return study_activity_entries;
    }

    public void setStudy_activity_entries(String[][] study_activity_entries) {
        this.study_activity_entries = study_activity_entries;
    }

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

    public ArrayList<WrongItem> getWrongItemArrayList_ask_again_after_test_activity() {
        return wrongItemArrayList_ask_again_after_test_activity;
    }

    public void setWrongItemArrayList_ask_again_after_test_activity(ArrayList<WrongItem> wrongItemArrayList_ask_again_after_test_activity) {
        this.wrongItemArrayList_ask_again_after_test_activity = wrongItemArrayList_ask_again_after_test_activity;
    }
}
