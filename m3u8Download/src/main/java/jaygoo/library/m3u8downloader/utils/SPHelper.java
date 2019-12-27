package jaygoo.library.m3u8downloader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Collections;
import java.util.Set;

public class SPHelper {

    private static final String NULL_KEY = "NULL_KEY";
    private static final String TAG_NAME = "M3U8PreferenceHelper";

    private volatile static SharedPreferences sp;


    private static SharedPreferences init(Context context) {
        if (null == sp) {
            synchronized (SPHelper.class) {
                if (null == sp) {
                    // sp = PreferenceManager.getDefaultSharedPreferences(context);
                    sp = context.getSharedPreferences(TAG_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return sp;
    }

    public static void onSetPrefBoolSetting(String Tag, Boolean Value, Context activityContext) {
        if (Tag != null && Value != null && activityContext != null) {
            SharedPreferences settings = activityContext.getSharedPreferences(TAG_NAME, 0);
            sp.edit().putBoolean(Tag, Value).apply();
        }
    }

    private static String checkKeyNonNull(String key) {
        if (key == null) {
            Log.e(NULL_KEY, "Key is null!!!");
            return NULL_KEY;
        }
        return key;
    }


    public static void putBoolean(Context context, @NonNull String key, boolean value) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putBoolean(checkKeyNonNull(key), value).apply();
    }

    public static boolean getBoolean(Context context, @NonNull String key, boolean defValue) {
        if (null == sp) {
            init(context);
        }
        return sp.getBoolean(checkKeyNonNull(key), defValue);
    }

    public static void putInt(Context context, @NonNull String key, int value) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putInt(checkKeyNonNull(key), value).apply();
    }

    public static int getInt(Context context, @NonNull String key, int defValue) {
        if (null == sp) {
            init(context);
        }
        return sp.getInt(checkKeyNonNull(key), defValue);
    }

    public static void putLong(Context context, @NonNull String key, long value) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putLong(checkKeyNonNull(key), value).apply();
    }

    public static long getLong(Context context, @NonNull String key, long defValue) {
        if (null == sp) {
            init(context);
        }
        return sp.getLong(checkKeyNonNull(key), defValue);
    }

    public static void putFloat(Context context, @NonNull String key, float value) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putFloat(checkKeyNonNull(key), value).apply();
    }

    public static float getFloat(Context context, @NonNull String key, float defValue) {
        if (null == sp) {
            init(context);
        }
        return sp.getFloat(checkKeyNonNull(key), defValue);
    }

    public static void putString(Context context, @NonNull String key, @Nullable String value) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putString(checkKeyNonNull(key), value).apply();
    }

    public static String getString(Context context, @NonNull String key, @Nullable String defValue) {
        if (null == sp) {
            init(context);
        }
        return sp.getString(checkKeyNonNull(key), defValue);
    }

    public static void putStringSet(Context context, @NonNull String key, @Nullable Set<String> values) {
        if (null == sp) {
            init(context);
        }
        sp.edit().putStringSet(checkKeyNonNull(key), values).apply();
    }

    public static Set<String> getStringSet(Context context, @NonNull String key, @Nullable Set<String> defValues) {
        if (null == sp) {
            init(context);
        }
        Set<String> result = sp.getStringSet(checkKeyNonNull(key), defValues);
        return result == null ? null : Collections.unmodifiableSet(result);
    }

    public static void increaseCount(Context context, String key) {
        if (null == sp) {
            init(context);
        }
        int count = getInt(context, key, 0);
        putInt(context, key, ++count);
    }

    public static void remove(Context context, String key) {
        if (null == sp) {
            init(context);
        }
        sp.edit().remove(key).apply();
    }

    public static void clearPreference(Context context) {
        if (null == sp) {
            init(context);
        }
        sp.edit().clear().apply();
    }

}
