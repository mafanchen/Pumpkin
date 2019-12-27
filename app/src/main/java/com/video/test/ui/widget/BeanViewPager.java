package com.video.test.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Enoch Created on 2018/11/9.
 */
public class BeanViewPager extends ViewPager {
    float x;
    float y;

    public BeanViewPager(@NonNull Context context) {
        super(context);
    }

    public BeanViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                return super.onInterceptHoverEvent(event);
            case MotionEvent.ACTION_MOVE:
                return Math.abs(x - event.getX()) > Math.abs(y - event.getY());
            case MotionEvent.ACTION_UP:
                return super.onInterceptHoverEvent(event);
            default:
                break;
        }
        return super.onInterceptHoverEvent(event);
    }
}
