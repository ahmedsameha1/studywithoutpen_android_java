package com.ahmedsameha1.studyvocabularyfive.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.ahmedsameha1.studyvocabularyfive.Adapters.WrongItemAdapter;
import com.ahmedsameha1.studyvocabularyfive.Database.Contract;
import com.ahmedsameha1.studyvocabularyfive.Database.DataProvider;
import com.ahmedsameha1.studyvocabularyfive.Database.DatabaseOperations;
import com.ahmedsameha1.studyvocabularyfive.R;
import com.ahmedsameha1.studyvocabularyfive.WrongItem;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 9/7/2015.
 */
public class WrongAnswersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String WRONG_ITEM_LIST = "wrongItemList";
    private String currentField;
    private ArrayList<WrongItem> wrongItemList;
    private Cursor cursor;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        listView = (ListView) findViewById(R.id.wrong_answer_list);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getBundleExtra(WRONG_ITEM_LIST);
        currentField = bundle.getString(TestActivity.CURRENT_FIELD);
//        ArrayList<WrongItem> wrongItemList = (ArrayList<WrongItem>) bundle.getSerializable(WRONG_ITEM_LIST);
//        wrongItemList = new ArrayList<WrongItem>();
//        WrongItemAdapter wrongItemAdapter = new WrongItemAdapter(this,wrongItemList);
//        listView.setAdapter(wrongItemAdapter);
        getSupportLoaderManager().initLoader(0,null,this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wrong_answers_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.rate_the_app:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast toast = Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,0,400);
                    toast.show();
                }
                return true;
            case R.id.open_facebook_page:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Study-Without-Pen-App-1503679869949785/"));
                startActivity(browserIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    private ArrayList<WrongItem> readWrongAnswersFromLinkerTable () {
        ArrayList<WrongItem> wrongItemsList = new ArrayList<WrongItem>();
        for ( cursor.moveToFirst(); cursor.getPosition() < cursor.getCount(); cursor.moveToNext() ) {
            String question = cursor.getString(0);
            String answer = cursor.getString(1);
            String wrongAnswers = cursor.getString(2);
            ArrayList<String> wrongAnswersList = new ArrayList<String>();
            StringTokenizer stringTokenizer = new StringTokenizer(wrongAnswers,"\t");
            while ( stringTokenizer.hasMoreTokens() ) {
                wrongAnswersList.add(stringTokenizer.nextToken());
            }
            wrongItemsList.add(new WrongItem(question,answer,wrongAnswersList));
        }
        return wrongItemsList;
    }

    @Override
    public void onSaveInstanceState ( Bundle savedInsatanceState ) {
        savedInsatanceState.putSerializable(WRONG_ITEM_LIST, wrongItemList);
    }
    @Override
    public void onRestoreInstanceState( Bundle savedInstanceState ) {
        wrongItemList = (ArrayList<WrongItem>) savedInstanceState.getSerializable(WRONG_ITEM_LIST);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.withAppendedPath(DataProvider.CONTENT_URI, Contract.Linker.TABLE_NAME);
        return new CursorLoader(this,uri,
                new String[] {Contract.Linker.COLUMN_NAME_QUESTION_LINK,
                        Contract.Linker.COLUMN_NAME_ANSWER_LINK,
                        Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS},
                Contract.Linker.COLUMN_NAME_TEST_WRONG_ANSWERS + " != ? AND " + Contract.Linker.COLUMN_NAME_FIELD_LINK + " = ? ",
                new String[] {"", currentField},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        if ( wrongItemList == null ) {
            wrongItemList = readWrongAnswersFromLinkerTable();
            DatabaseOperations databaseOperations = new DatabaseOperations(getContentResolver());
            databaseOperations.clearLinkerTableWrongAnswers(currentField);
        }
        WrongItemAdapter wrongItemAdapter = new WrongItemAdapter(this, wrongItemList);
        listView.setAdapter(wrongItemAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
