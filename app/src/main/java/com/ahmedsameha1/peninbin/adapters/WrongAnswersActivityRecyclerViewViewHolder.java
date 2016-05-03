package com.ahmedsameha1.peninbin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class WrongAnswersActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    public IMyViewHolderClicks mListener;
    public TextView showQuestion;
    public TextView showAnswer;
    public Spinner spinner;
    public String question,answer;

    public WrongAnswersActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        showQuestion = (TextView)view.findViewById(R.id.wrong_question);
        showAnswer = (TextView)view.findViewById(R.id.wrong_answer);
        spinner = (Spinner)view.findViewById(R.id.wrong_answer_spinner);
        view.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        mListener.onClick(v, question,answer);
    }

    public interface IMyViewHolderClicks {
        void onClick(View view, String currentQuestion, String currentAnswer);
    }
}
