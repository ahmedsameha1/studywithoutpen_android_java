package com.ahmedsameha1.peninbin.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.StudyWrongAnswersActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmed on 3/13/2016.
 */
public class WrongAnswersActivityRecyclerViewAdapter extends
        RecyclerView.Adapter<WrongAnswersActivityRecyclerViewViewHolder> {
    private Context context;
    private List<WrongItem> wrongItemList;
    private long field_id;

    public WrongAnswersActivityRecyclerViewAdapter(Context context, List<WrongItem> wrongItemList, long field_id) {
        this.wrongItemList = wrongItemList;
        this.context = context;
        this.field_id = field_id;
    }
    @Override
    public WrongAnswersActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wrong_item_row, parent, false);
        WrongAnswersActivityRecyclerViewViewHolder wrongAnswersActivityRecyclerViewViewHolder =
                new WrongAnswersActivityRecyclerViewViewHolder(itemView, (v, currentQuestion, currentAnswer) -> {
                    Intent intent = new Intent(context, StudyWrongAnswersActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(StudyActivity.CURRENT_FIELD_ID,field_id);
                    bundle.putString(StudyWrongAnswersActivity.CURRENT_QUESTION,currentQuestion);
                    bundle.putString(StudyWrongAnswersActivity.CURRENT_ANSWER, currentAnswer);
                    bundle.putSerializable(StudyWrongAnswersActivity.WRONG_ITEM_LIST, ((ArrayList<WrongItem>) wrongItemList));
                    intent.putExtra(StudyWrongAnswersActivity.WRONG_ITEM_LIST,bundle);
                    context.startActivity(intent);
                });
        return wrongAnswersActivityRecyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(WrongAnswersActivityRecyclerViewViewHolder viewHolder, int position) {
        WrongItem wrongItem = wrongItemList.get(position);
        viewHolder.question = wrongItem.getQuestion();
        viewHolder.answer = wrongItem.getAnswer();
        viewHolder.showQuestion.setText(wrongItem.getQuestion());
        viewHolder.showAnswer.setText(wrongItem.getAnswer());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                R.layout.wrong_item_spinner,R.id.wrong_answer_text,wrongItem.getWrongAnswers());
        viewHolder.spinner.setAdapter(arrayAdapter);
    }
    @Override
    public int getItemCount() {
        return wrongItemList.size();
    }
}
