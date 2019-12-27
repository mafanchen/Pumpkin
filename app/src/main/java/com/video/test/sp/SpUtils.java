package com.video.test.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import io.reactivex.annotations.NonNull;


/**
 * SharedPreferences 的工具类
 * Created by Enoch on 2017/5/8.
 */

public class SpUtils {

    private static final String TAG = "SpUtils";

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

    public static void putSerializable(@NonNull Context context, @NonNull String key, @NonNull Serializable objcet) {
        String value = object2String(objcet);
        if (value != null) {
            putString(context, key, value);
        }
    }

    @Nullable
    public static <T extends Serializable> T getSerializable(@NonNull Context context, @NonNull String key) {
        String value = getString(context, key, null);
        if (value == null) {
            return null;
        }
        return string2Object(value);
    }

    @Nullable
    private static String object2String(@NonNull Serializable object) {
        long start = System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String result = byteArrayOutputStream.toString("ISO-8859-1");
            result = URLEncoder.encode(result, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
            Log.d(TAG, "序列化完成:" + result);
            Log.d(TAG, "用时:" + (System.currentTimeMillis() - start));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                byteArrayOutputStream.close();
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T extends Serializable> T string2Object(@NonNull String string) {
        long start = System.currentTimeMillis();
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            string = URLDecoder.decode(string, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.ISO_8859_1));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            byteArrayInputStream.close();
            objectInputStream.close();
            Log.d(TAG, "反序列化用时:" + (System.currentTimeMillis() - start));
            return (T) object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
