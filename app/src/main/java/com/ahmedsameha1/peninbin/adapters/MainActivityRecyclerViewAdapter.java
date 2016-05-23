package com.ahmedsameha1.peninbin.adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmedsameha1.peninbin.activities.FieldActivity;
import com.ahmedsameha1.peninbin.activities.StudyDuringActivity;
import com.ahmedsameha1.peninbin.activities.TestActivity;
import com.ahmedsameha1.peninbin.database.Contract;
import com.ahmedsameha1.peninbin.database.MySQLiteCursorLoader;
import com.ahmedsameha1.peninbin.dialogs.DeleteFieldDialogFragment;
import com.ahmedsameha1.peninbin.R;
import com.ahmedsameha1.peninbin.utils.Expand_Custom_Animation;

/**
 * Created by ahmed on 3/13/2016.
 */
public class MainActivityRecyclerViewAdapter extends
        CursorRecyclerViewAdapter<MainActivityRecyclerViewViewHolder> {
    public static final String GET_CONFIRMATION = "getConfirmation";
    private Context context;
    private Cursor cursor;
    private SharedPreferences sharedPreferences;


    public MainActivityRecyclerViewAdapter(Context context, Cursor data) {
        super(context, data);
        this.context = context;
        this.cursor = data;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public MainActivityRecyclerViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_activity_recyclerview_row, parent, false);
        MainActivityRecyclerViewViewHolder mainActivityRecyclerViewViewHolder =
                new MainActivityRecyclerViewViewHolder(itemView, new MainActivityRecyclerViewViewHolder.IMyViewHolderClicks() {
                    @Override
                    public void onClick(View v, long field_id,boolean isDeleting) {
                        if ( ! isDeleting ) {
                            Intent intent = new Intent(context, FieldActivity.class);
                            intent.putExtra(FieldActivity.FIELD_ID, field_id);
                            context.startActivity(intent);
                            new MySQLiteCursorLoader(context).incrementFieldUsage(field_id);
                        }
                    }

                    @Override
                    public void onLongClick(View view, final long field_id,boolean isDeleting) {
                        if ( ! isDeleting ) {
                            View actions = view.findViewById(R.id.main_recyclerview_item_actions);
                            Expand_Custom_Animation expandAni = new Expand_Custom_Animation(actions, 500);
                            actions.startAnimation(expandAni);
                        }
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
    public void onBindViewHolder(final MainActivityRecyclerViewViewHolder viewHolder, Cursor data) {
        ((LinearLayout.LayoutParams) viewHolder.actions.getLayoutParams()).bottomMargin = -50;
        viewHolder.actions.setVisibility(View.GONE);
        viewHolder.field_id = data.getInt(0);
        final String fieldName = data.getString(2);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( (! sharedPreferences.contains(TestActivity.ACTIVITY_NAME + TestActivity.PAUSING + viewHolder.field_id)) &&
                        (! sharedPreferences.contains(StudyDuringActivity.ACTIVITY_NAME + StudyDuringActivity.PAUSING + viewHolder.field_id)) ) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(DeleteFieldDialogFragment.TO_BE_DELETED_FIELD_ID,viewHolder.field_id);
                    bundle.putString(DeleteFieldDialogFragment.TO_BE_DELETED_FIELD_NAME, fieldName);
                    DeleteFieldDialogFragment fragment = new DeleteFieldDialogFragment();
                    fragment.setArguments(bundle);
                    fragment.setMainActivityRecyclerViewViewHolder(viewHolder);
                    fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), GET_CONFIRMATION);
                } else {
                    Toast toast = Toast.makeText(context, R.string.wait_until_current_session_pausing_finish, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, 400);
                    toast.show();
                }
            }
        });
        if (data.getInt(1) == Contract.Field.DEFAULT_TYPE) {
            viewHolder.type.setImageResource(R.drawable.books1);
        } else if (data.getInt(1) == Contract.Field.NOTEBOOK_TYPE) {
            viewHolder.type.setImageResource(R.drawable.agenda1);
        }
        viewHolder.name.setText(fieldName);
    }
}
