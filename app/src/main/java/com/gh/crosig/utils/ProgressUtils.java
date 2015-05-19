package com.gh.crosig.utils;

import android.app.Activity;
import android.app.ProgressDialog;

import com.gh.crosig.R;

/**
 * Created by henrique on 19/05/15.
 */
public class ProgressUtils {

    public static ProgressDialog newProgressDlg(Activity activity) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setMessage(activity.getString(R.string.progress_dialog));
        return pd;
    }
}
