package com.ahmedsameha1.studyvocabularyfive.adapterstemp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.ahmedsameha1.studyvocabularyfive.activitiestemp.StudyWrongAnswersActivity;
import com.ahmedsameha1.studyvocabularyfive.WrongItem;
import com.ahmedsameha1.studyvocabularyfive.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmed on 3/12/2016.
 */
public class WrongItemAdapter extends BaseAdapter {
    private LayoutInflater Inflater;
    private List<WrongItem> wrongItemList;
    private Activity activity;
    public WrongItemAdapter( Context context, List<WrongItem> wrongItemList) {
        this.activity = (Activity) context;
        Inflater = LayoutInflater.from(context);
        this.wrongItemList = wrongItemList;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = Inflater.inflate(R.layout.wrong_item_row, parent, false);
            holder = new ViewHolder();
            holder.showQuestion = (TextView)view.findViewById(R.id.wrong_question);
            holder.showAnswer = (TextView)view.findViewById(R.id.wrong_answer);
            holder.spinner = (Spinner)view.findViewById(R.id.wrong_answer_spinner);
            holder.study = (ImageButton) view.findViewById(R.id.study_entry);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        WrongItem wrongItem = wrongItemList.get(position);
        holder.showQuestion.setText(wrongItem.getQuestion());
        holder.showAnswer.setText(wrongItem.getAnswer());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,
                R.layout.wrong_item_spinner,R.id.wrong_answer_text,wrongItem.getWrongAnswers());
        holder.spinner.setAdapter(arrayAdapter);
        holder.study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = ((WrongItem) getItem(position)).getQuestion();
                String answer = ( (WrongItem) getItem(position)).getAnswer();
                Intent intent = new Intent(activity, StudyWrongAnswersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StudyWrongAnswersActivity.CURRENT_QUESTION,question);
                bundle.putString(StudyWrongAnswersActivity.CURRENT_ANSWER, answer);
                bundle.putSerializable(StudyWrongAnswersActivity.WRONG_ITEM_LIST, ((ArrayList<WrongItem>) wrongItemList));
                intent.putExtra(StudyWrongAnswersActivity.WRONG_ITEM_LIST,bundle);
                activity.startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return wrongItemList.get(position);
    }

    @Override
    public int getCount() {
        return wrongItemList.size();
    }

    static class ViewHolder {
        TextView showQuestion;
        TextView showAnswer;
        Spinner spinner;
        ImageButton study;
    }
}
