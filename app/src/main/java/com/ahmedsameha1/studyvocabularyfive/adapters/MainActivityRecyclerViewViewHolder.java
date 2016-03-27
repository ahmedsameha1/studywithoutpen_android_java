package com.ahmedsameha1.studyvocabularyfive.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class MainActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    public TextView name;
    public ImageView type;
    public String fieldName;
    public LinearLayout actions;
    public int position;
    public IMyViewHolderClicks mListener;
    public MainActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        type = (ImageView) view.findViewById(R.id.type);
        name = (TextView) view.findViewById(R.id.field_name);
        actions = (LinearLayout) view.findViewById(R.id.main_recyclerview_item_actions);
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
    }
    @Override
    public boolean onLongClick(View v) {
        mListener.onLongClick(v, fieldName);
        return true;
    }
    @Override
    public void onClick(View v) {
        mListener.onClick(v, fieldName, position);
    }

    public interface IMyViewHolderClicks {
        void onLongClick(View view, String fieldName);
        void onClick(View view, String fieldName, int position);
    }
}
