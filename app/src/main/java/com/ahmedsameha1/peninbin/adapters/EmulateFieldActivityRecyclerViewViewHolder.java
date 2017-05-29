package com.ahmedsameha1.peninbin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class EmulateFieldActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements CheckBox.OnCheckedChangeListener {
    public TextView question;
    public TextView answer;
    public RadioButton radioButton;
    public String currentQuestion;
    public String currentAnswer;
    public IMyViewHolderClicks mListener;
    public long field_id;
    public long entry_id;

    public EmulateFieldActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        question = (TextView) view.findViewById(R.id.question);
        answer = (TextView) view.findViewById(R.id.answer);
        radioButton = (RadioButton) view.findViewById(R.id.copy);
        radioButton.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mListener.onCheckChanged(entry_id, b);
    }



    public interface IMyViewHolderClicks {
        void onCheckChanged(long entry_id, boolean checked);
    }
}
