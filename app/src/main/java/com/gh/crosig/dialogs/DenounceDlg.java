package com.gh.crosig.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gh.crosig.R;


public class DenounceDlg extends DialogFragment {

    private static final String TAG = "DenounceDlg";

    public interface DenounceDlgListener {
        public void onDenounce(String comment);
    }

    private DenounceDlgListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dlgView = getActivity().getLayoutInflater().inflate(R.layout.comment_layout, null);
        builder.setTitle(R.string.denounce_problem)
                .setView(dlgView)
                .setPositiveButton(R.string.denounce_problem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDenounce(((EditText) dlgView.findViewById(
                                R.id.comment_text)).getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DenounceDlg.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DenounceDlgListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
