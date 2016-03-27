package com.ahmedsameha1.studyvocabularyfive.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;

import com.ahmedsameha1.studyvocabularyfive.R;

import java.util.List;

/**
 * Created by ahmed on 3/11/2016.
 */


/**
 * Created by Ravi Tamada on 12-03-2015.
 */
public class MainActivityNavigationDrawerAdapter extends RecyclerView.Adapter<MainActivityNavigationDrawerAdapter.MyViewHolder> {
    List<String> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public MainActivityNavigationDrawerAdapter(Context context, List<String> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String current = data.get(position);
        holder.title.setText(current);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}

