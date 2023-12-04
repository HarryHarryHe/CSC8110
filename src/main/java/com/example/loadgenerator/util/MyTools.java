package com.example.loadgenerator.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Harry
 * @version 1.0
 * @description: TODO
 * @date 2023/12/04 1:18
 */
public class MyTools {
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** Convert Date type date into String type date according to DATE_PATTERN */
    public static String date2str(Date date) {
        return date2str(date, DATE_PATTERN);
    }

    /** Convert Date to String according to specified format */
    public static String date2str(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
