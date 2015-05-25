package com.gh.crosig.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.gh.crosig.R;


public class VeracityDlg extends DialogFragment {

    private static final String TAG = "UpdateStatusDlg";

    public interface VeracityDlgListener {
        public void onPickVeracity(boolean isVeridic);
    }

    private VeracityDlgListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.evaluate_problem)
                .setItems(R.array.veracity, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Dialog click at which: " + which);
                        listener.onPickVeracity(
                                which == 0 ? true : false
                        );
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (VeracityDlgListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
