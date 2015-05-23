package com.gh.crosig.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by henrique on 21/05/15.
 */
public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public static String dateToStr(Date date) {
        if (date == null)
            return null;
        return dateFormat.format(date);
    }
}
