package com.ahmedsameha1.studyvocabulary.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Objects;

/**
 * Created by ahmed on 28/04/16.
 */
public class SpinnerWithHintAdapter extends ArrayAdapter<String> {
    public SpinnerWithHintAdapter(Context theContext, int theLayoutResId, List<String> strings) {
        super(theContext, theLayoutResId,  strings);
    }
    public SpinnerWithHintAdapter(Context theContext, int theLayoutResId, String[] strings) {
        super(theContext, theLayoutResId,  strings);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
