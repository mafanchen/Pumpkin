package com.video.test.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Enoch on 2017/3/10.
 */

public class DateUtils {

    /*Date Pattern*/
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";


    private DateUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    /*Date format util*/
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /*Format DateTime*/
    public static String formatDateTime(Date date) {
        return formatDate(date, DEFAULT_DATETIME_PATTERN);
    }

    /*Formate Dtae*/
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_PATTERN);
    }

    /*Get now DateTime*/
    public static String getDateTime() {
        return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
    }

    /*Get now Date*/
    public static String getDate() {
        return formatDate(new Date(), DEFAULT_DATE_PATTERN);
    }

    /*Get now timestamp (second unit)*/
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getExpiresTime() {
        return getCurrentTimestamp() + 604800;
    }
}
