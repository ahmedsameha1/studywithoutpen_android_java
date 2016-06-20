package com.ahmedsameha1.peninbin.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.EditEntryActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.StudyDuringActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.DatabaseOperations;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.utils.Expand_Custom_Animation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ahmed on 3/13/2016.
 */
public class NotebookFieldActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<NotebookFieldActivityRecyclerViewViewHolder> {
    public static final String GET_CONFIRMATION = "getConfirmation";
    private Context context;
    private long field_id;
    private int fieldType;
    private int question_data_type;
    private SharedPreferences sharedPreferences;
    private DatabaseOperations databaseOperations;
    private MySQLiteCursorLoader mySQLiteCursorLoader;
    public NotebookFieldActivityRecyclerViewAdapter(Context context, Cursor data, long field_id, int fieldType ) {
        super(context,data);
        this.context = context;
        this.field_id = field_id;
        this.fieldType = fieldType;
        databaseOperations = new DatabaseOperations(context);
        mySQLiteCursorLoader = new MySQLiteCursorLoader(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.question_data_type = databaseOperations.getQuestionDataType(field_id);
    }
    @Override
    public NotebookFieldActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notebook_field_activity_recyclerview_row, parent, false);
        NotebookFieldActivityRecyclerViewViewHolder NotebookFieldActivityRecyclerViewViewHolder =
                new NotebookFieldActivityRecyclerViewViewHolder(itemView, new NotebookFieldActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, ImageView alarmImage ) {
                        if ( alarmImage.getVisibility() == View.GONE ) {
                            alarmImage.setVisibility(View.VISIBLE);
                        } else if ( alarmImage.getVisibility() == View.VISIBLE ){
                            alarmImage.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onLongClick(View view) {
                        View toolbar = view.findViewById(R.id.field_recyclerview_item_actions);
                        Expand_Custom_Animation expandAni = new Expand_Custom_Animation(toolbar, 500);
                        toolbar.startAnimation(expandAni);
                    }
                });
        return NotebookFieldActivityRecyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(NotebookFieldActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        ((LinearLayout.LayoutParams) viewHolder.toolbar.getLayoutParams()).bottomMargin = -50;
        viewHolder.toolbar.setVisibility(View.GONE);
        final long entry_id = data.getLong(0);
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.field_id = field_id;
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEntryActivity.class);
                intent.putExtra(EditEntryActivity.ENTRY_ID, entry_id );
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,field_id);
                intent.putExtra(EditEntryActivity.FIELD_TYPE, fieldType );
                intent.putExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,question_data_type);
                intent.putExtra(StudyActivity.CURRENT_QUESTION,question);
                intent.putExtra(StudyActivity.CURRENT_ANSWER,answer);
                ((Activity) context).startActivityForResult(intent,FieldActivity.EDIT_ENTRY);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( (! sharedPreferences.contains(TestActivity.ACTIVITY_NAME + TestActivity.PAUSING + field_id)) &&
                        (! sharedPreferences.contains(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.PAUSING + field_id))) {
                    if ( ! databaseOperations.checkIfIncludedInPausedOperations(field_id,entry_id) ) {
                        DeleteEntryDialogFragment fragment = new DeleteEntryDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong(DeleteEntryDialogFragment.TO_BE_DELETED_ENTRY_ID,entry_id);
                        bundle.putInt(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,question_data_type);
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = ((FieldActivity) context).getSupportFragmentManager();
                        fragment.show(fragmentManager, DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                    } else {
                        Toast toast = Toast.makeText(context, R.string.complete_or_cancel_last_study_session, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP,0,400);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(context, R.string.wait_until_current_session_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });
        switch (fieldType) {
            case Contract.Field.DEFAULT_TYPE:
                viewHolder.question.setText(question);
                viewHolder.answer.setText(answer);
                break;
            case Contract.Field.NOTEBOOK_TYPE:
                try {
                    Timestamp timestamp = Timestamp.valueOf(question);
                    Date date = new Date(timestamp.getTime());
                    viewHolder.question.setText(new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm").format(date));
                    viewHolder.answer.setText(answer);
                    break;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    String s = question.substring(0,question.length() - 3);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date = null;
                    try {
                        date = dateFormat.parse(s);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    String d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(date) + ":00";
                    mySQLiteCursorLoader.editEntry(entry_id, question_data_type,d,answer,databaseOperations.getEntryRank(entry_id));
                }
        }
    }
}
