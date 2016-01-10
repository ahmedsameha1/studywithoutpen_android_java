package com.ahmedsameha1.studyvocabularyfive.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.ahmedsameha1.studyvocabularyfive.EntriesListActivity;
import com.ahmedsameha1.studyvocabularyfive.EntriesListFragment;
import com.ahmedsameha1.studyvocabularyfive.FieldActivity;
import com.ahmedsameha1.studyvocabularyfive.R;

/**
 * Created by ahmed on 7/27/2015.
 */
public class DeleteEntryDialogFragment extends DialogFragment {
    public static final String DELETE_ENTRY_DIALOG_FRAGMENT = "deleteEntryDialogFragment";
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.delete_entry)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getActivity() instanceof FieldActivity) {
                                    FieldActivity fieldActivity = (FieldActivity) getActivity();
                                    EntriesListFragment entriesListFragment = (EntriesListFragment) fieldActivity
                                            .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                                    entriesListFragment.deleteFieldEntry(DeleteEntryDialogFragment.this);
                                } else {
                                    EntriesListActivity entriesListActivity = (EntriesListActivity) getActivity();
                                    EntriesListFragment entriesListFragment = (EntriesListFragment) entriesListActivity
                                            .getSupportFragmentManager().findFragmentById(R.id.entries_list_fragment);
                                    entriesListFragment.deleteFieldEntry(DeleteEntryDialogFragment.this);
                                }
//                        ((Contact) getActivity()).deleteFieldEntry(DeleteEntryDialogFragment.this);
                            }
                        }

                )
                            .

                    setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }

                    );
                    return builder.create();
                }
        public interface Contact {
        public void deleteFieldEntry(DeleteEntryDialogFragment dialog);
    }
}
