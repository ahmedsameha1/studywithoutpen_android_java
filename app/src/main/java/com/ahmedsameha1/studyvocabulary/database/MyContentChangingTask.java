package com.ahmedsameha1.studyvocabulary.database;

import android.os.AsyncTask;
import android.content.Loader;
import android.os.AsyncTask;
/**
 * Created by Ahmed on 4/9/16.
 */
public abstract class MyContentChangingTask extends AsyncTask<Object, Void, Long> {
    private Loader<?> loader=null;

    public MyContentChangingTask(Loader<?> loader) {
        this.loader=loader;
    }

    @Override
    protected void onPostExecute(Long param) {
        loader.onContentChanged();
    }
}
