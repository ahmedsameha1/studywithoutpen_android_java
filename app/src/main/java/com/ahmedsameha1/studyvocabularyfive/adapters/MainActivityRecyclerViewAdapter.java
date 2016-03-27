package com.ahmedsameha1.studyvocabularyfive.adapters;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ahmedsameha1.studyvocabularyfive.activities.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.activities.MainActivity;
import com.ahmedsameha1.studyvocabularyfive.database.Contract;
import com.ahmedsameha1.studyvocabularyfive.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 3/13/2016.
 */
public class MainActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<MainActivityRecyclerViewViewHolder> {
    public static final String GET_CONFIRMATION = "getConfirmation";
    private Context context;
    private Cursor cursor;

    public MainActivityRecyclerViewAdapter(Context context, Cursor data) {
        super(context, data);
        this.context = context;
        this.cursor = data;
    }

    @Override
    public MainActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_activity_recyclerview_row, parent, false);
        MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder =
                new MainActivityRecyclerViewViewHolder(itemView, new MainActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, String fieldName, int position) {
                        Intent intent = new Intent(context, FieldActivity.class);
                        intent.putExtra(FieldActivity.INITIAL_FIELD, fieldName);
                        intent.putExtra(MainActivity.LAST_USED_FIELD_POSITION, position);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, final String fieldName) {
                        View actions = view.findViewById(R.id.main_recyclerview_item_actions);
                        Expand_Custom_Animation expandAni = new Expand_Custom_Animation(actions, 500);
                        actions.startAnimation(expandAni);
                        Button delete = (Button) view.findViewById(R.id.delete);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString(DeleteFieldDialogFragment.TO_BE_DELETED_FIELD_NAME, fieldName);
                                DeleteFieldDialogFragment fragment = new DeleteFieldDialogFragment();
                                fragment.setArguments(bundle);
                                fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), GET_CONFIRMATION);
                            }
                        });

                    }
                });
        return mainActivityRecyclerViewViewHolder;
    }

    /*  public class ViewHolder extends RecyclerView.ViewHolder {
          public TextView name;
          public ImageView type;
          public ViewHolder(View view) {
              super(view);
              type = (ImageView) view.findViewById(R.id.type);
              name = (TextView) view.findViewById(R.id.field_name);
          }
      }*/
    @Override
    public void onBindViewHolder(MainActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        ((LinearLayout.LayoutParams) viewHolder.actions.getLayoutParams()).bottomMargin = -50;
        viewHolder.actions.setVisibility(View.GONE);
        String fieldName = data.getString(2);
        viewHolder.position = data.getPosition();
        viewHolder.fieldName = fieldName;
        if (data.getInt(1) == Contract.Field.DEFAULT_TYPE) {
            viewHolder.type.setImageResource(R.drawable.books1);
        } else if (data.getInt(1) == Contract.Field.NOTEBOOK_TYPE) {
            viewHolder.type.setImageResource(R.drawable.agenda1);
        }
        viewHolder.name.setText(fieldName);
    }
}
