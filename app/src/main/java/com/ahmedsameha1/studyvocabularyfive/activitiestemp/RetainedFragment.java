package com.ahmedsameha1.studyvocabularyfive.activitiestemp;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by ahmed on 8/18/2015.
 */
public class RetainedFragment extends Fragment {
    private String [][] listForTest_TestActivity;
    private String [][] listForTest_StudyDuringActivity;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String[][] getListForTest_TestActivity() {
        return listForTest_TestActivity;
    }

    public void setListForTest_TestActivity(String[][] listForTest_TestActivity) {
        this.listForTest_TestActivity = listForTest_TestActivity;
    }

    public String[][] getListForTest_StudyDuringActivity() {
        return listForTest_StudyDuringActivity;
    }

    public void setListForTest_StudyDuringActivity(String[][] listForTest_StudyDuringActivity) {
        this.listForTest_StudyDuringActivity = listForTest_StudyDuringActivity;
    }
}
