package com.ahmedsameha1.peninbin.adapters;



import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.activities.EditEntryActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.SearchActivity;
import com.ahmedsameha1.peninbin.activities.StudyActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Expand_Custom_Animation;

/**
 * Created by ahmed on 3/13/2016.
 */
public class FieldActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<FieldActivityRecyclerViewViewHolder> {
    private Context context;
    private long field_id;
    private int question_data_type;
    private SharedPreferences sharedPreferences;
    private DatabaseReadOperations databaseOperations;
    private StringBuilder search_string;
    public FieldActivityRecyclerViewAdapter(Context context, Cursor data, long field_id, StringBuilder search_string ) {
        super(context,data);
        this.context = context;
        this.field_id = field_id;
        this.search_string = search_string;
        databaseOperations = new DatabaseReadOperations(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.question_data_type = databaseOperations.getQuestionDataType(field_id);
    }
    @Override
    public FieldActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.field_activity_recyclerview_row, parent, false);
        FieldActivityRecyclerViewViewHolder fieldActivityRecyclerViewViewHolder =
                new FieldActivityRecyclerViewViewHolder(itemView, new FieldActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, String currentQuestion, String currentAnswer, long entry_id ) {
                            Intent intent = new Intent(context, StudyActivity.class);
                            intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                            intent.putExtra(StudyActivity.ENTRY_ID,entry_id);
                            intent.putExtra(StudyActivity.CURRENT_QUESTION,currentQuestion);
                            intent.putExtra(StudyActivity.CURRENT_ANSWER,currentAnswer);
                            context.startActivity(intent);
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
        final int asked_counter = data.getInt(3);
        final int wrong_answer_counter = data.getInt(4);
        viewHolder.field_id = field_id;
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.edit.setOnClickListener(v -> {
            if ( !Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                    && !ImportTextFile.isThreadRunning() ) {
                Intent intent = new Intent(context, EditEntryActivity.class);
                intent.putExtra(EditEntryActivity.ENTRY_ID, entry_id );
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID,field_id);
                intent.putExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE,question_data_type);
                intent.putExtra(StudyActivity.CURRENT_QUESTION,question);
                intent.putExtra(StudyActivity.CURRENT_ANSWER,answer);
                ((Activity) context).startActivityForResult(intent,FieldActivity.EDIT_ENTRY);
            } else {
                Toast toast = Toast.makeText(context, R.string.background_thread, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.delete.setOnClickListener(v -> {
            if ( (! sharedPreferences.contains(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.PAUSING + field_id)) &&
                    (! sharedPreferences.contains(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.PAUSING + field_id)) &&
                    (! sharedPreferences.contains(FullyRandomTestActivity.ACTIVITY_NAME + FullyRandomTestActivity.PAUSING + field_id)) &&
                    !Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                    && !ImportTextFile.isThreadRunning() ) {
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
            } else if ( sharedPreferences.contains(EnhancedTestActivity.ACTIVITY_NAME + EnhancedTestActivity.PAUSING + field_id) ||
                     sharedPreferences.contains(StudyAPeriodActivity.ACTIVITY_NAME + StudyAPeriodActivity.PAUSING + field_id) ||
                    sharedPreferences.contains(FullyRandomTestActivity.ACTIVITY_NAME + FullyRandomTestActivity.PAUSING + field_id) ){
                Toast toast = Toast.makeText(context, R.string.wait_until_current_session_pausing_finish, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            } else if ( Emulation.isAnyThreadRunning() || ExportImportDatabase.isAnyThreadRunning()
                    || ImportTextFile.isThreadRunning() ) {
                Toast toast = Toast.makeText(context, R.string.background_thread, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.same_question.setOnClickListener(view -> {
            if ( databaseOperations.moreThanOneAnswerSameQuestion(field_id, question) ) {
                final Intent intent = new Intent(context,
                        SearchActivity.class);
                // add query to the Intent Extras
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, question);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                intent.putExtra(SearchActivity.SEARCH_WHAT, SearchActivity.SEARCH_QUESTION);
                context.startActivity(intent);
            } else {
                Toast toast = Toast.makeText(context, R.string.only_this_with_same_question, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.same_answer.setOnClickListener(view -> {
            if ( databaseOperations.moreThanOneQuestionSameAnswer(field_id, answer) ) {
                final Intent intent = new Intent(context,
                        SearchActivity.class);
                // add query to the Intent Extras
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, answer);
                intent.putExtra(StudyActivity.CURRENT_FIELD_ID, field_id);
                intent.putExtra(SearchActivity.SEARCH_WHAT, SearchActivity.SEARCH_ANSWER);
                context.startActivity(intent);
            } else {
                Toast toast = Toast.makeText(context, R.string.only_this_with_same_answer, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.asked_count.setOnClickListener(view -> {
            Toast toast = Toast.makeText(context,
                    context.getString(R.string.asked, String.valueOf(asked_counter)), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        viewHolder.wrong_answer_count.setOnClickListener(view -> {
            Toast toast = Toast.makeText(context,
                    context.getString(R.string.wrongly_answered, String.valueOf(wrong_answer_counter)), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        Spannable spannable_question = Spannable.Factory.getInstance().newSpannable(question);
        Spannable spannable_answer = Spannable.Factory.getInstance().newSpannable(answer);
        String value = search_string.toString();
        if ( ! "".equals(value) ) {
            if (question.contains(value)) {
                int start = 0;
                while (start < question.length()) {
                    if (question.indexOf(value,start) == start) {
                        int end = start + search_string.length();
                        spannable_question.setSpan(new BackgroundColorSpan(0xFFFFFF00),
                                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = end;
                    } else {
                        start++;
                    }
                }
            }
            if (answer.contains(value)) {
                int start = 0;
                while (start < answer.length()) {
                    if (answer.indexOf(value, start) == start) {
                        int end = start + search_string.length();
                        spannable_answer.setSpan(new BackgroundColorSpan(0xFFFFFF00),
                                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        start = end;
                    } else {
                        start++;
                    }
                }
            }
        }
        viewHolder.question.setText(spannable_question);
        viewHolder.answer.setText(spannable_answer);
        viewHolder.asked_count.setText(String.valueOf(asked_counter));
        viewHolder.wrong_answer_count.setText(String.valueOf(wrong_answer_counter));
    }
}
