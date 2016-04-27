package com.ahmedsameha1.studyvocabulary.adapters;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabulary.activities.EditEntryActivity;
import com.ahmedsameha1.studyvocabulary.activities.FieldActivity;
import com.ahmedsameha1.studyvocabulary.activities.StudyActivity;
import com.ahmedsameha1.studyvocabulary.activities.StudyDuringActivity;
import com.ahmedsameha1.studyvocabulary.activities.TestActivity;
import com.ahmedsameha1.studyvocabulary.database.Contract;
import com.ahmedsameha1.studyvocabulary.database.DatabaseOperations;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.studyvocabulary.R;
import com.ahmedsameha1.studyvocabulary.dialogs.DeleteFieldDialogFragment;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmed on 3/13/2016.
 */
public class FieldActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<FieldActivityRecyclerViewViewHolder> {
    public static final String GET_CONFIRMATION = "getConfirmation";
    private Context context;
    private long field_id;
    private int fieldType;
    private int sort = FieldActivity.DEFAULT_SORT;
    private SharedPreferences sharedPreferences;
    private DatabaseOperations databaseOperations;
    public FieldActivityRecyclerViewAdapter(Context context, Cursor data, long field_id, int fieldType ) {
        super(context,data);
        this.context = context;
        this.field_id = field_id;
        this.fieldType = fieldType;
        databaseOperations = new DatabaseOperations(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    @Override
    public FieldActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.field_activity_recyclerview_row, parent, false);
        FieldActivityRecyclerViewViewHolder fieldActivityRecyclerViewViewHolder =
                new FieldActivityRecyclerViewViewHolder(itemView, new FieldActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, String currentQuestion, String currentAnswer ) {
                        if ( fieldType != Contract.Field.NOTEBOOK_TYPE) {
                            Intent intent = new Intent(context, StudyActivity.class);
                            intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                            intent.putExtra(StudyActivity.CURRENT_QUESTION,currentQuestion);
                            intent.putExtra(StudyActivity.CURRENT_ANSWER,currentAnswer);
                            intent.putExtra(StudyActivity.ORDER_OF_ENTRIES,sort);
                            context.startActivity(intent);
                        } else {
                            Toast toast = Toast.makeText(context, R.string.study_not_for_notebook,Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        }
                    }
                    @Override
                    public void onLongClick(View view) {
                        View toolbar = view.findViewById(R.id.field_recyclerview_item_actions);
                        Expand_Custom_Animation expandAni = new Expand_Custom_Animation(toolbar, 500);
                        toolbar.startAnimation(expandAni);
                    }
                });
        return fieldActivityRecyclerViewViewHolder;
    }

    @Override
    public void onBindViewHolder(FieldActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        ((LinearLayout.LayoutParams) viewHolder.toolbar.getLayoutParams()).bottomMargin = -50;
        viewHolder.toolbar.setVisibility(View.GONE);
        final long entry_id = data.getLong(0);
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.field_id = field_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEntryActivity.class);
                intent.putExtra(EditEntryActivity.ENTRY_ID, entry_id );
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,field_id);
                intent.putExtra(EditEntryActivity.FIELD_TYPE, fieldType );
                intent.putExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,databaseOperations.getQuestionDataType(field_id));
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
                        bundle.putInt(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,databaseOperations.getQuestionDataType(field_id));
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
                Timestamp timestamp = Timestamp.valueOf(question);
                Date date = new Date(timestamp.getTime());
                viewHolder.question.setText(new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm").format(date));
                viewHolder.answer.setText(answer);
                break;
        }
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
}
