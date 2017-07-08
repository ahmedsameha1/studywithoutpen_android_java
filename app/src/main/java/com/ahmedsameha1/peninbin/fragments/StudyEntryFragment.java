package com.ahmedsameha1.peninbin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.NewStudyActivity;

/**
 * Created by ahmed on 7/6/17.
 */

public class StudyEntryFragment extends Fragment {
    private String[][] entries;
    private StringBuilder current_index;

    public static StudyEntryFragment newInstance(String[][] entries, StringBuilder current_index) {
        StudyEntryFragment instance = new StudyEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewStudyActivity.DATA, entries);
        bundle.putSerializable(NewStudyActivity.CURRENT_INDEX, current_index);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entries = (String[][]) getArguments().getSerializable(NewStudyActivity.DATA);
        current_index = (StringBuilder) getArguments().getSerializable(NewStudyActivity.CURRENT_INDEX);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.study_entry_fragment, container, false);
    }
}
