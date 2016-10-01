package com.ahmedsameha1.peninbin.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.ArrayList;

/**
 * Created by ahmed on 3/13/2016.
 */
public class CopyMoveEntriesActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<CopyMoveEntriesActivityRecyclerViewViewHolder> {
    private Context context;
    private long field_id;
    private int question_data_type;
    private SharedPreferences sharedPreferences;
    private DatabaseReadOperations databaseOperations;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private ArrayList<Long> list_of_entries;
    public CopyMoveEntriesActivityRecyclerViewAdapter(Context context, Cursor data, ArrayList<Long> list) {
        super(context,data);
        this.context = context;
        list_of_entries = list;
        databaseOperations = new DatabaseReadOperations(context);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        this.question_data_type = databaseOperations.getQuestionDataType(field_id);
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
                });
        return copy_move_entries_activity_recyclerview;
    }

    @Override
    public void onBindViewHolder(CopyMoveEntriesActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        final long entry_id = data.getLong(0);
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.field_id = field_id;
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.question.setText(question);
        viewHolder.answer.setText(answer);
        if ( list_of_entries.contains(entry_id) ) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
    }
}
