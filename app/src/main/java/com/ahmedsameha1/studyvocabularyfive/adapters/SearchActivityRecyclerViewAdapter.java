package com.ahmedsameha1.studyvocabularyfive.adapters;



import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.activities.EditEntryActivity;
import com.ahmedsameha1.studyvocabularyfive.activities.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.activities.StudyActivity;
import com.ahmedsameha1.studyvocabularyfive.database.Contract;
import com.ahmedsameha1.studyvocabularyfive.database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ahmed on 3/13/2016.
 */
public class SearchActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<FieldActivityRecyclerViewViewHolder> {
    public static final String GET_CONFIRMATION = "getConfirmation";
    private Context context;
    private String fieldName;
    private int fieldType;
    public SearchActivityRecyclerViewAdapter(Context context, Cursor data, String fieldName, int fieldType) {
        super(context,data);
        this.context = context;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
    @Override
    public FieldActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_activity_recyclerview_row, parent, false);
        FieldActivityRecyclerViewViewHolder fieldActivityRecyclerViewViewHolder =
                new FieldActivityRecyclerViewViewHolder(itemView, new FieldActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, String fieldName, String currentQuestion, String currentAnswer ) {
                        if ( fieldType != Contract.Field.NOTEBOOK_TYPE) {
                            Intent intent = new Intent(context, StudyActivity.class);
                            intent.putExtra(StudyActivity.CURRENT_FIELD, fieldName);
                            intent.putExtra(StudyActivity.CURRENT_QUESTION,currentQuestion);
                            intent.putExtra(StudyActivity.CURRENT_ANSWER,currentAnswer);
                            intent.putExtra(StudyActivity.ORDER_OF_ENTRIES,0);
                            context.startActivity(intent);
                        } else {
                            Toast toast = Toast.makeText(context, R.string.study_not_for_notebook,Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,400);
                            toast.show();
                        }
                    }
                    @Override
                    public void onLongClick(View view, String fieldName) {
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
        final String question = data.getString(1);
        final String answer = data.getString(2);
        viewHolder.fieldName = fieldName;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditEntryActivity.class);
                intent.putExtra(StudyActivity.CURRENT_FIELD, fieldName);
                intent.putExtra(StudyActivity.CURRENT_QUESTION,question);
                intent.putExtra(StudyActivity.CURRENT_ANSWER,answer);
                context.startActivity(intent);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
                Cursor studyDuringCursor = context.getContentResolver().query(uri,
                        new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_STUDY_DURING_ORDER + " >= ? ",
                        new String[]{question, answer, fieldName, "0"},
                        null);
                Cursor testCursor = context.getContentResolver().query(uri,
                        new String[]{Contract.Linker.COLUMN_NAME_QUESTION_LINK},
                        Contract.Linker.COLUMN_NAME_QUESTION_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_ANSWER_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? AND "
                                + Contract.Linker.COLUMN_NAME_TEST_ORDER + " >= ? ",
                        new String[]{question, answer, fieldName, "0"},
                        null);
                if (studyDuringCursor.getCount() <= 0 && testCursor.getCount() <= 0) {
                    DeleteEntryDialogFragment fragment = new DeleteEntryDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(StudyActivity.CURRENT_FIELD,fieldName);
                    bundle.putString(StudyActivity.CURRENT_QUESTION,question);
                    bundle.putString(StudyActivity.CURRENT_ANSWER,answer);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((FieldActivity) context).getSupportFragmentManager();
                    fragment.show(fragmentManager, DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(context, R.string.complete_or_cancel_last_study_session, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                studyDuringCursor.close();
                testCursor.close();
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
}
