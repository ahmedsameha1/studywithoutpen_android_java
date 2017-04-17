package com.ahmedsameha1.peninbin.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EmulateFieldActivity;
import com.ahmedsameha1.peninbin.database.CursorLoaderAndUpdateDatabase;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;

import java.util.ArrayList;

/**
 * Created by ahmed on 3/13/2016.
 */
public class EmulateFieldActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<EmulateFieldActivityRecyclerViewViewHolder> {
    private Context context;
    private long field_id;
    private int question_data_type;
    private SharedPreferences sharedPreferences;
    private DatabaseReadOperations databaseOperations;
    private CursorLoaderAndUpdateDatabase mySQLiteCursorLoader;
    private ArrayList<Long> list_of_entries;
    private ArrayList<Long> starting_entry_id;
    private static CheckBox lastChecked = null;


    public EmulateFieldActivityRecyclerViewAdapter(Context context, Cursor data, ArrayList<Long> starting_entry_id ) {
        super(context,data);
        this.context = context;
        this.starting_entry_id = starting_entry_id;
        list_of_entries = new ArrayList<>();
        databaseOperations = new DatabaseReadOperations(context);
        mySQLiteCursorLoader = new CursorLoaderAndUpdateDatabase(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        this.question_data_type = databaseOperations.getQuestionDataType(field_id);
    }
    @Override
    public EmulateFieldActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.copy_move_entries_activity_recyclerview_row, parent, false);
        EmulateFieldActivityRecyclerViewViewHolder emulateFieldActivityRecyclerViewViewHolder =
                new EmulateFieldActivityRecyclerViewViewHolder(itemView, (entry_id, checked) -> {
                    if ( checked ) {
//                            list_of_entries.add(entry_id);

                        if( lastChecked != null )
                        {
                            lastChecked.setChecked(false);

                        }
                        starting_entry_id.clear();
                            starting_entry_id.add(entry_id);
                            lastChecked = ((CheckBox) itemView.findViewById(R.id.copy));

                        /*if ( list_of_entries.size() > 1 ) {
                            ((CheckBox)itemView.findViewById(R.id.copy)).setChecked(false);
                            list_of_entries.remove(entry_id);
                            starting_entry_id [0] = 0;
                            Toast toast = Toast.makeText(context, R.string.only_one, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }*/
                    } else {
//                            list_of_entries.remove(entry_id);
                        starting_entry_id.clear();
                        lastChecked = null;
                    }
                });
        return emulateFieldActivityRecyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(EmulateFieldActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        final long entry_id = data.getLong(0);
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.field_id = field_id;
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.question.setText(question);
        viewHolder.answer.setText(answer);
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        if ( starting_entry_id.contains( entry_id) ) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(viewHolder);
        if ( viewHolder.checkBox.isChecked())
        {
            lastChecked = viewHolder.checkBox;

        }
    /*    viewHolder.checkBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox)v;


                if(cb.isChecked() )
                {
                    if(lastChecked != null)
                    {
                        lastChecked.setChecked(false);
                    }

                    lastChecked = cb;

                }
                else
                    lastChecked = null;
            }
        });*/
    }
}
