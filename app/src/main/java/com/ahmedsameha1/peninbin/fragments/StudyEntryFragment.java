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
    public static final String CURRENT_FIELD_ID = "currentField";
    private String[][] entries;
    private StringBuilder current_index;
    private long current_field_id;

    public static StudyEntryFragment newInstance(String[][] entries, StringBuilder current_index, long current_field_id) {
        StudyEntryFragment instance = new StudyEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NewStudyActivity.DATA, entries);
        bundle.putSerializable(NewStudyActivity.CURRENT_INDEX, current_index);
        bundle.putLong(CURRENT_FIELD_ID, current_field_id);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entries = (String[][]) getArguments().getSerializable(NewStudyActivity.DATA);
        current_index = (StringBuilder) getArguments().getSerializable(NewStudyActivity.CURRENT_INDEX);
        current_field_id = getArguments().getLong(CURRENT_FIELD_ID, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.study_entry_fragment, container, false);
    }

    public void changeAreas(float questionArea, float answerArea) {
/*        LinearLayout.LayoutParams question_params = (LinearLayout.LayoutParams) questionLinearlayout.getLayoutParams();
        LinearLayout.LayoutParams answer_params = (LinearLayout.LayoutParams) answerLinearlayout.getLayoutParams();
        container.setWeightSum(questionArea + answerArea);
        question_params.weight = (float) (questionArea);
        answer_params.weight = (float) (answerArea);
        questionLinearlayout.setLayoutParams(question_params);
        answerLinearlayout.setLayoutParams(answer_params);*/
    }
    public void changeTextSize(float textSize) {
/*        enterQuestion.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        enterAnswer.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);*/
    }
}
