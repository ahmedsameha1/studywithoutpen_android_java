package com.ahmedsameha1.peninbin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class CopyMoveEntriesActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnCheckedChangeListener {
    public TextView question;
    public TextView answer;
    public CheckBox checkBox;
    public String currentQuestion;
    public String currentAnswer;
    public IMyViewHolderClicks mListener;
    public long field_id;
    public long entry_id;

    public CopyMoveEntriesActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        checkBox = (CheckBox) view.findViewById(R.id.copy);
        checkBox.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mListener.onCheckChanged(entry_id, b);
    }



    public interface IMyViewHolderClicks {
        void onCheckChanged( long entry_id , boolean checked);
    }
}
