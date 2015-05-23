package com.gh.crosig.utils;

import android.app.Activity;
import android.graphics.Color;

import com.gh.crosig.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by henrique on 21/05/15.
 */
public class CommonUtils {


    public static float getColor(Activity activity, String status) {
        String[] possiblesStatus = activity.getResources().getStringArray(R.array.problem_status);
        if (possiblesStatus == null || status == null) {
            return BitmapDescriptorFactory.HUE_BLUE;
        } else if (status.equals(possiblesStatus[0])) {
            return BitmapDescriptorFactory.HUE_RED;
        } else if (status.equals(possiblesStatus[1])) {
            return BitmapDescriptorFactory.HUE_BLUE;
        } else {
            return BitmapDescriptorFactory.HUE_GREEN;
        }
    }

    public static int getColorInt(Activity activity, String status) {
        String[] possiblesStatus = activity.getResources().getStringArray(R.array.problem_status);
        if (possiblesStatus == null || status == null) {
            return Color.BLUE;
        } else if (status.equals(possiblesStatus[0])) {
            return Color.RED;
        } else if (status.equals(possiblesStatus[1])) {
            return Color.BLUE;
        } else {
            return Color.GREEN;
        }

    }
}
