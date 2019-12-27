package jaygoo.library.m3u8downloader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * SharedPreferences 的工具类
 * Created by Enoch on 2017/5/8.
 */

public class SpUtils {

    private SpUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, value).apply();

    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void removeString(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void removeBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, String key, long defaultLong) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(key, defaultLong);
    }

    public static void removeLong(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultInt) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultInt);
    }

    public static void removeInt(Context context, String key) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(key).apply();
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
