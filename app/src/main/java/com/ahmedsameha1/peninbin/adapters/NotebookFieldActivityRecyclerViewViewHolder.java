package com.ahmedsameha1.peninbin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class NotebookFieldActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener, View.OnClickListener {
    public TextView question;
    public TextView answer;
    public Button edit;
    public Button delete;
    public String currentQuestion;
    public String currentAnswer;
    public LinearLayout toolbar;
    public ImageView alarmImage;
    public IMyViewHolderClicks mListener;
    public long field_id;
    public long entry_id;
    public Integer request;

    public NotebookFieldActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        alarmImage = (ImageView) view.findViewById(R.id.alarm_image);
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
        mListener.onClick(v, alarmImage,currentQuestion, currentAnswer, entry_id );
    }

    public interface IMyViewHolderClicks {
        void onLongClick(View view);
        void onClick(View view, ImageView alarmImage, String question, String answer, long entry_id);
    }
}
