package com.video.test.utils;

import android.content.Context;

/**
 * Created by enochguo on 16/03/2018.
 */

public class PixelUtils {
    private PixelUtils() {
        throw new UnsupportedOperationException("UtilClass can't initialize");
    }

    /**
     * px 转 dp
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * dp 转 px
     */

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }

    /**
     * sp 转 px
     */

    public static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    /**
     * px 转 sp
     *
     */

    public static int px2sp(Context context, float px) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);

    }

}
