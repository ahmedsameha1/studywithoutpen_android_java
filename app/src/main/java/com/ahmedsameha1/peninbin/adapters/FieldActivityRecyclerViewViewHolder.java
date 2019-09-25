package com.ahmedsameha1.peninbin.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class FieldActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener, View.OnClickListener {
    public TextView question;
    public TextView answer;
    public TextView wrongness_level;
    public Button edit;
    public Button delete;
    public ImageView more;
    public String currentQuestion;
    public String currentAnswer;
    public LinearLayout toolbar;
    public IMyViewHolderClicks mListener;
    public long field_id;
    public long entry_id;

    public FieldActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        edit = (Button) view.findViewById(R.id.edit);
        delete = (Button) view.findViewById(R.id.delete);
        more = (ImageView) view.findViewById(R.id.more);
        toolbar = (LinearLayout) view.findViewById(R.id.field_recyclerview_item_actions);
        wrongness_level = (TextView) view.findViewById(R.id.wrongness_level);
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
        mListener.onClick(v,currentQuestion,currentAnswer,entry_id);
    }

    public interface IMyViewHolderClicks {
        void onLongClick(View view);
        void onClick(View view, String currentQuestion, String currentAnswer, long entry_id );
    }
}
