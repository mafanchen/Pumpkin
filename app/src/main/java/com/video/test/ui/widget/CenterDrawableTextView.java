package com.video.test.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @author Enoch Created on 2018/7/9.
 */
public class CenterDrawableTextView extends AppCompatTextView {
    public CenterDrawableTextView(Context context) {
        super(context);
    }

    public CenterDrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenterDrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CenterDrawableHelper.preDraw(this,canvas);
        super.onDraw(canvas);
    }
}
