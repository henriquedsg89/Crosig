package com.gh.crosig.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.gh.crosig.R;


public class SuggestStatusDlg extends DialogFragment {

    private static final String TAG = "SuggestStatusDlg";

    public interface SuggestStatusDlgListener {
        public void onPickSuggestStatus(String status);
    }

    private SuggestStatusDlgListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.suggest_status_problem)
                .setItems(R.array.problem_status, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Dialog click at which: " + which);
                        listener.onPickSuggestStatus(getResources().getStringArray(R.array.problem_status)[which]);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SuggestStatusDlgListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
