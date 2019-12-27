package com.video.test.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.video.test.utils.LogUtils;


/**
 * @author Created by enochguo on 02/02/2018.
 */

public class MarqueeTextView extends AppCompatTextView {
    private static final String TAG = "MarqueeTextView";

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //设置单行
        setSingleLine();
        //设置Ellipsize
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //设置跑马灯重复次数
        setMarqueeRepeatLimit(1);
        LogUtils.i(TAG, "marqueeRepeatLimit == " + getMarqueeRepeatLimit());
        //获取焦点
        setFocusable(true);
        //强制获取焦点
        setFocusableInTouchMode(true);
    }


    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }
}
