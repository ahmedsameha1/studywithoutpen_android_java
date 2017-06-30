package com.ahmedsameha1.peninbin.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;

import java.util.ArrayList;

/**
 * Created by ahmed on 3/13/2016.
 */
public class CopyMoveEntriesActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<CopyMoveEntriesActivityRecyclerViewViewHolder> {
    private long current_field_id;
    private StringBuilder search_string;
    private ArrayList<Long> list_of_entries;
    public CopyMoveEntriesActivityRecyclerViewAdapter(Context context, Cursor data, ArrayList<Long> list, long field_id, StringBuilder search_string) {
        super(context,data);
        list_of_entries = list;
        this.current_field_id = field_id;
        this.search_string = search_string;
    }
    @Override
    public CopyMoveEntriesActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.copy_move_entries_activity_recyclerview_row, parent, false);
        CopyMoveEntriesActivityRecyclerViewViewHolder copy_move_entries_activity_recyclerview =
                new CopyMoveEntriesActivityRecyclerViewViewHolder(itemView, new CopyMoveEntriesActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onCheckChanged(long entry_id, boolean checked) {
                            if ( checked ) {
                                list_of_entries.add(entry_id);
                            } else {
                                list_of_entries.remove(entry_id);
                            }
                    }

                    @Override
                    public void onClick(CheckBox checkBox) {
                        checkBox.setChecked(!checkBox.isChecked());
                    }
                });
        return copy_move_entries_activity_recyclerview;
    }

    @Override
    public void onBindViewHolder(CopyMoveEntriesActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        final long entry_id = data.getLong(0);
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.question.setText(ActivityUtility.prepare_search_spans(question, search_string));
        viewHolder.answer.setText(ActivityUtility.prepare_search_spans(answer, search_string));
        if ( list_of_entries.contains(entry_id) ) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
    }
}
