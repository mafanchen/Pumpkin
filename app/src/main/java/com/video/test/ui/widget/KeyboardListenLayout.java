package com.video.test.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 用于不被输入法遮挡的布局
 *
 * @author : AhhhhDong
 * @date : 2019/5/16 9:44
 */
public class KeyboardListenLayout extends FrameLayout {

    private OnSizeChangeListener onSizeChangeListener;

    public KeyboardListenLayout(Context context) {
        super(context);
    }

    public KeyboardListenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardListenLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangeListener != null && oldw != 0 && oldh != 0) {
            //监听到现在的高度小于之前的高度，说明被遮挡了
            boolean showKeyboard = h < oldh;
            onSizeChangeListener.onChanged(showKeyboard, h, oldh);
        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }

    public interface OnSizeChangeListener {
        void onChanged(boolean showKeyboard, int h, int oldh);
    }
}
