package com.ahmedsameha1.studyvocabularyfive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongAnswersActivity extends AppCompatActivity {
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    private String currentField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        ListView listView = (ListView) findViewById(R.id.wrong_answer_list);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        currentField = bundle.getString(TestActivity.CURRENT_FIELD);
        ArrayList<WrongItem> wrongItemList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
        WrongItemAdapter wrongItemAdapter = new WrongItemAdapter(this,wrongItemList);
        listView.setAdapter(wrongItemAdapter);
    }

    public class WrongItemAdapter extends BaseAdapter {
        private LayoutInflater Inflater;
        private List<WrongItem> wrongItemList;
        public WrongItemAdapter( Context context, List<WrongItem> wrongItemList) {
            Inflater = LayoutInflater.from(context);
            this.wrongItemList = wrongItemList;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView == null) {
                view = Inflater.inflate(R.layout.wrong_item_row, parent, false);
                holder = new ViewHolder();
                holder.showQuestion = (TextView)view.findViewById(R.id.wrong_question);
                holder.showAnswer = (TextView)view.findViewById(R.id.wrong_answer);
                holder.spinner = (Spinner)view.findViewById(R.id.wrong_answer_spinner);
                holder.study = (ImageButton) view.findViewById(R.id.study_entry);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder)view.getTag();
            }

            WrongItem wrongItem = wrongItemList.get(position);
            holder.showQuestion.setText(wrongItem.getQuestion());
            holder.showAnswer.setText(wrongItem.getAnswer());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WrongAnswersActivity.this,
                    R.layout.wrong_item_spinner,R.id.wrong_answer_text,wrongItem.getWrongAnswers());
            holder.spinner.setAdapter(arrayAdapter);
            holder.study.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String question = ((WrongItem) getItem(position)).getQuestion();
                    String answer = ( (WrongItem) getItem(position)).getAnswer();
                    Intent intent = new Intent(WrongAnswersActivity.this, StudyActivity.class);
                    intent.putExtra(StudyActivity.CURRENT_FIELD, currentField);
                    intent.putExtra(StudyActivity.CURRENT_QUESTION, question);
                    intent.putExtra(StudyActivity.CURRENT_ANSWER, answer);
                    startActivity(intent);
                }
            });
            return view;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return wrongItemList.get(position);
        }

        @Override
        public int getCount() {
            return wrongItemList.size();
        }
    }
    static class ViewHolder {
        TextView showQuestion;
        TextView showAnswer;
        Spinner spinner;
        ImageButton study;
    }
}
