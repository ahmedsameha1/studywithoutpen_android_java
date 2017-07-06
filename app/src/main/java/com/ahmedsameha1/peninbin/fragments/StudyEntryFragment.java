package com.ahmedsameha1.peninbin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.NewStudyActivity;

import java.util.LinkedHashMap;

/**
 * Created by ahmed on 7/6/17.
 */

public class StudyEntryFragment extends Fragment {
    LinkedHashMap<String, String[]> data;

    public static StudyEntryFragment newInstance(LinkedHashMap<String, String[]> data) {
        StudyEntryFragment instance = new StudyEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewStudyActivity.DATA, data);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = (LinkedHashMap<String, String[]>) getArguments().getSerializable(NewStudyActivity.DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.study_entry_fragment, container, false);
    }
}
