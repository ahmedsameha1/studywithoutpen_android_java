package com.ahmedsameha1.studyvocabularyfive;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by ahmed on 8/18/2015.
 */
public class RetainedFragment extends Fragment {
    private String [][] listForTest_StudyActivity;
    private String [][] listForTest_StudyDuringActivity;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String[][] getListForTest_StudyActivity() {
        return listForTest_StudyActivity;
    }

    public void setListForTest_StudyActivity(String[][] listForTest_StudyActivity) {
        this.listForTest_StudyActivity = listForTest_StudyActivity;
    }

    public String[][] getListForTest_StudyDuringActivity() {
        return listForTest_StudyDuringActivity;
    }

    public void setListForTest_StudyDuringActivity(String[][] listForTest_StudyDuringActivity) {
        this.listForTest_StudyDuringActivity = listForTest_StudyDuringActivity;
    }
}
