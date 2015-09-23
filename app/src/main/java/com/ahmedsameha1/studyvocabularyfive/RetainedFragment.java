package com.ahmedsameha1.studyvocabularyfive;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by ahmed on 8/18/2015.
 */
public class RetainedFragment extends Fragment {
    private String [][] listForTest;
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String[][] getListForTest() {
        return listForTest;
    }

    public void setListForTest(String[][] listForTest) {
        this.listForTest = listForTest;
    }
}
