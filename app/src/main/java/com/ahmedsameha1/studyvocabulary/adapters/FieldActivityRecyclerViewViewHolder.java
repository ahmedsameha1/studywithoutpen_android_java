package com.ahmedsameha1.studyvocabulary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmedsameha1.studyvocabulary.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class FieldActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener, View.OnClickListener {
    public TextView question;
    public TextView answer;
    public Button edit;
    public Button delete;
    public String currentQuestion;
    public String currentAnswer;
    public LinearLayout toolbar;
    public IMyViewHolderClicks mListener;
    public long field_id;

    public FieldActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        edit = (Button) view.findViewById(R.id.edit);
        delete = (Button) view.findViewById(R.id.delete);
        toolbar = (LinearLayout) view.findViewById(R.id.field_recyclerview_item_actions);
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
    }
    @Override
    public boolean onLongClick(View v) {
        mListener.onLongClick(v);
        return true;
    }
    @Override
    public void onClick(View v) {
        mListener.onClick(v,currentQuestion,currentAnswer);
    }

    public interface IMyViewHolderClicks {
        void onLongClick(View view);
        void onClick(View view, String currentQuestion, String currentAnswer );
    }
}
