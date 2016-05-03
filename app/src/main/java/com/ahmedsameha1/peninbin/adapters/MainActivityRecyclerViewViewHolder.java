package com.ahmedsameha1.peninbin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedsameha1.peninbin.R;

/**
 * Created by Ahmed on 3/17/16.
 */
public class MainActivityRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    public TextView name;
    public ImageView type;
    public long field_id;
    public Button delete;
    public LinearLayout actions;
    public ProgressBar progressBar;
    public boolean isDeleting;
    public IMyViewHolderClicks mListener;
    public MainActivityRecyclerViewViewHolder(View view, IMyViewHolderClicks listener) {
        super(view);
        mListener = listener;
        type = (ImageView) view.findViewById(R.id.type);
        name = (TextView) view.findViewById(R.id.field_name);
        actions = (LinearLayout) view.findViewById(R.id.main_recyclerview_item_actions);
        delete = (Button) view.findViewById(R.id.delete);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
    }
    @Override
    public boolean onLongClick(View v) {
        mListener.onLongClick(v, field_id,isDeleting);
        return true;
    }
    @Override
    public void onClick(View v) {
        mListener.onClick(v, field_id,isDeleting);
    }

    public interface IMyViewHolderClicks {
        void onLongClick(View view, long field_id,boolean isDeleting);
        void onClick(View view, long field_id, boolean isDeleting);
    }
}
