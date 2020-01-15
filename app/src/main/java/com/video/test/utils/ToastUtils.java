package com.video.test.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    private ToastUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    public static void showToast(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(resId);
        }
        new Handler(Looper.getMainLooper()).post(() -> mToast.show());
    }

    public static void showToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(text);
        }
        new Handler(Looper.getMainLooper()).post(() -> mToast.show());
    }

    public static void showLongToast(Context context, int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(resId);
        }
        new Handler(Looper.getMainLooper()).post(() -> mToast.show());
    }

    public static void showLongToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(text);
        }
        new Handler(Looper.getMainLooper()).post(() -> mToast.show());
    }


    public static void showSnackBar(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }


    public static void showSnackBar(View view, String text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }


    public static void showSnackBar(View view, int resId, String action, View.OnClickListener l) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction(action, l).show();
    }


    public static void showSnackBar(View view, String text, String action, View.OnClickListener l) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction(action, l).show();
    }
}
