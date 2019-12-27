package com.video.test.utils;

import android.util.Log;

import com.video.test.BuildConfig;

/**
 * 日志工具类 只有Debug模式生效
 * Created by Enoch on 2017/5/8.
 */

public class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

    public static void i(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

    public static void w(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

    public static void v(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

    public static void d(Class tag, String msg) {
        e(tag.getSimpleName(), msg);
    }

}
