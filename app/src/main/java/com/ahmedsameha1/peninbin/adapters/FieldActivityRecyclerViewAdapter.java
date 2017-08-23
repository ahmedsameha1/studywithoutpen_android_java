package com.ahmedsameha1.peninbin.adapters;


import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.activities.AskAgainAfterTestActivity;
import com.ahmedsameha1.peninbin.activities.EditEntryActivity;
import com.ahmedsameha1.peninbin.activities.EnhancedTestActivity;
import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.FullyRandomTestActivity;
import com.ahmedsameha1.peninbin.activities.SearchActivity;
import com.ahmedsameha1.peninbin.activities.StudyAPeriodActivity;
import com.ahmedsameha1.peninbin.utils.Constants;
import com.ahmedsameha1.peninbin.database.DatabaseReadOperations;
import com.ahmedsameha1.peninbin.database.ExportImportDatabase;
import com.ahmedsameha1.peninbin.database.ImportTextFile;
import com.ahmedsameha1.peninbin.dialogs.DeleteEntryDialogFragment;
import com.ahmedsameha1.peninbin.utils.ActivityUtility;
import com.ahmedsameha1.peninbin.utils.Emulation;
import com.ahmedsameha1.peninbin.utils.Expand_Custom_Animation;

import java.util.Locale;

/**
 * Created by ahmed on 3/13/2016.
 */
public class FieldActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<FieldActivityRecyclerViewViewHolder> {
    public static final String CURRENT_QUESTION = "currentQuestion";
    public static final String CURRENT_ANSWER = "currentAnswer";
    private Context context;
    private long field_id;
    private int question_data_type;
    private SharedPreferences sharedPreferences;
    private DatabaseReadOperations databaseOperations;
    private StringBuilder search_string;


    public FieldActivityRecyclerViewAdapter(Context context, Cursor data, long field_id, StringBuilder search_string) {
        super(context, data);
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
        return new FieldActivityRecyclerViewViewHolder(itemView, new FieldActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
            @Override
            public void onClick(View v, String currentQuestion, String currentAnswer, long entry_id) {
                ((Contact) context).startStudyActivity(entry_id, currentQuestion, currentAnswer);
            }

            @Override
            public void onLongClick(View view) {
                View toolbar = view.findViewById(R.id.field_recyclerview_item_actions);
                Expand_Custom_Animation expandAni = new Expand_Custom_Animation(toolbar, 500);
                toolbar.startAnimation(expandAni);
            }
        });
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
        double wrongness_level = asked_counter > 0 ? (((double) wrong_answer_counter / asked_counter) * 100) : 0;
        viewHolder.field_id = field_id;
        viewHolder.entry_id = entry_id;
        viewHolder.currentQuestion = question;
        viewHolder.currentAnswer = answer;
        viewHolder.edit.setOnClickListener(v -> {
            if (!Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                    && !ImportTextFile.isThreadRunning()) {
                Intent intent = new Intent(context, EditEntryActivity.class);
                intent.putExtra(EditEntryActivity.ENTRY_ID, entry_id);
                intent.putExtra(Constants.CURRENT_FIELD_ID, field_id);
                intent.putExtra(DeleteEntryDialogFragment.QUESTION_DATA_TYPE, question_data_type);
                intent.putExtra(CURRENT_QUESTION, question);
                intent.putExtra(CURRENT_ANSWER, answer);
                ((Activity) context).startActivityForResult(intent, FieldActivity.EDIT_ENTRY);
            } else {
                Toast toast = Toast.makeText(context, R.string.background_thread, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.delete.setOnClickListener(v -> {
            if (!sharedPreferences.contains(EnhancedTestActivity.class.getName() + Constants.PAUSING + field_id) &&
                    !sharedPreferences.contains(StudyAPeriodActivity.class.getName() + Constants.PAUSING + field_id) &&
                    !sharedPreferences.contains(FullyRandomTestActivity.class.getName() + Constants.PAUSING + field_id) &&
                    !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED + field_id) &&
                    !sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM + field_id) &&
                    !Emulation.isAnyThreadRunning() && !ExportImportDatabase.isAnyThreadRunning()
                    && !ImportTextFile.isThreadRunning()) {
                if (!databaseOperations.checkIfIncludedInPausedOperations(field_id, entry_id)) {
                    DeleteEntryDialogFragment fragment = new DeleteEntryDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(DeleteEntryDialogFragment.TO_BE_DELETED_ENTRY_ID, entry_id);
                    bundle.putInt(DeleteEntryDialogFragment.QUESTION_DATA_TYPE, question_data_type);
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragment.show(fragmentManager, DeleteEntryDialogFragment.DELETE_ENTRY_DIALOG_FRAGMENT);
                } else {
                    Toast toast = Toast.makeText(context, R.string.complete_or_cancel_last_study_session, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            } else if (sharedPreferences.contains(EnhancedTestActivity.class.getName() + Constants.PAUSING + field_id) ||
                    sharedPreferences.contains(StudyAPeriodActivity.class.getName() + Constants.PAUSING + field_id) ||
                    sharedPreferences.contains(FullyRandomTestActivity.class.getName() + Constants.PAUSING + field_id) ||
                    sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_ENHANCED + field_id) ||
                    sharedPreferences.contains(AskAgainAfterTestActivity.class.getName() + AskAgainAfterTestActivity.PAUSING_FULLY_RANDOM + field_id)) {
                Toast toast = Toast.makeText(context, R.string.wait_some_background_operation, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            } else if (Emulation.isAnyThreadRunning() || ExportImportDatabase.isAnyThreadRunning()
                    || ImportTextFile.isThreadRunning()) {
                Toast toast = Toast.makeText(context, R.string.background_thread, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 400);
                toast.show();
            }
        });
        viewHolder.wrongness_level.setOnClickListener(view -> {
            Toast toast = Toast.makeText(context,
                    context.getString(R.string.wrongness_level, wrongness_level), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 400);
            toast.show();
        });
        viewHolder.more.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, view);
            ((Activity) context).getMenuInflater().inflate(R.menu.same_search_menu, popup.getMenu());
            ClipboardManager mclipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = mclipboard.getPrimaryClip();
            if (clipData == null || clipData.getItemCount() <= 0) {
                popup.getMenu().findItem(R.id.substitute_question).setEnabled(false);
                popup.getMenu().findItem(R.id.substitute_answer).setEnabled(false);
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.same_question) {
                    if (databaseOperations.moreThanOneAnswerSameQuestion(field_id, question)) {
                        final Intent intent = new Intent(context,
                                SearchActivity.class);
                        // add query to the Intent Extras
                        intent.setAction(Intent.ACTION_SEARCH);
                        intent.putExtra(SearchManager.QUERY, question);
                        intent.putExtra(SearchActivity.SEARCH_VALUE, question);
                        intent.putExtra(Constants.CURRENT_FIELD_ID, field_id);
                        intent.putExtra(SearchActivity.SEARCH_WHAT, SearchActivity.SEARCH_QUESTION);
                        context.startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(context, R.string.only_this_with_same_question, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else if (item.getItemId() == R.id.same_answer) {
                    if (databaseOperations.moreThanOneQuestionSameAnswer(field_id, answer)) {
                        final Intent intent = new Intent(context,
                                SearchActivity.class);
                        // add query to the Intent Extras
                        intent.setAction(Intent.ACTION_SEARCH);
                        intent.putExtra(SearchManager.QUERY, answer);
                        intent.putExtra(SearchActivity.SEARCH_VALUE, answer);
                        intent.putExtra(Constants.CURRENT_FIELD_ID, field_id);
                        intent.putExtra(SearchActivity.SEARCH_WHAT, SearchActivity.SEARCH_ANSWER);
                        context.startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(context, R.string.only_this_with_same_answer, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 400);
                        toast.show();
                    }
                } else if (item.getItemId() == R.id.asked_count) {
                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.asked, String.valueOf(asked_counter)), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                } else if (item.getItemId() == R.id.copy_question) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getString(R.string._question), question);
                    clipboard.setPrimaryClip(clip);
                } else if (item.getItemId() == R.id.copy_answer) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.answer), answer);
                    clipboard.setPrimaryClip(clip);
                } else if (item.getItemId() == R.id.substitute_question) {
                    ClipData.Item item_ = clipData.getItemAt(0);
                    if (item_ != null && item_.coerceToText(context).toString().trim().length() > 0) {
                        String text = item_.coerceToText(context).toString().trim();
                        if (!text.equals(question)) {
                            if (text.length() <= 500) {
                                if (!databaseOperations.checkEntryDuplication(text, answer, field_id, question_data_type)) {
                                    new AlertDialog.Builder(context)
                                            .setTitle(R.string.question_will)
                                            .setMessage(text)
                                            .setPositiveButton(R.string.ok, (d, i) -> ((Contact) context).substituteQuestion(entry_id, text))
                                            .create().show();
                                } else {
                                    Toast toast = Toast.makeText(context, R.string.already_inserted, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(context, R.string.less_500, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(context, R.string.same_text, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                } else if (item.getItemId() == R.id.substitute_answer) {
                    ClipData.Item item_ = clipData.getItemAt(0);
                    if (item_ != null && item_.getText() != null && item_.getText().toString().trim().length() > 0) {
                        String text = item_.getText().toString().trim();
                        if (!text.equals(answer)) {
                            if (text.length() <= 500) {
                                if (!databaseOperations.checkEntryDuplication(question, text, field_id, question_data_type)) {
                                    new AlertDialog.Builder(context)
                                            .setTitle(R.string.answer_will)
                                            .setMessage(text)
                                            .setPositiveButton(R.string.ok, (d, i) -> ((Contact) context).substituteAnswer(entry_id, text))
                                            .create().show();
                                } else {
                                    Toast toast = Toast.makeText(context, R.string.already_inserted, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP, 0, 400);
                                    toast.show();
                                }
                            } else {
                                Toast toast = Toast.makeText(context, R.string.less_500, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, 0, 400);
                                toast.show();
                            }
                        } else {
                            Toast toast = Toast.makeText(context, R.string.same_text, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 0, 400);
                            toast.show();
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.wrongly_answered, String.valueOf(wrong_answer_counter)), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
                return true;
            });
            popup.show();
        });
        viewHolder.question.setText(ActivityUtility.prepare_search_spans(question, search_string));
        viewHolder.answer.setText(ActivityUtility.prepare_search_spans(answer, search_string));
        viewHolder.wrongness_level.setText(String.format(Locale.getDefault(), "%.0f%%", wrongness_level));
    }


    public interface Contact {
        void substituteQuestion(long entry_id, String text);

        void substituteAnswer(long entry_id, String text);

        void startStudyActivity(long entry_id, String question, String answer);
    }
}
