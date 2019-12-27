package com.video.test.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 跑马灯
 *
 * @author : AhhhhDong
 * @date : 2019/3/19 11:25
 */
public class AlwaysMarqueeTextView extends AppCompatTextView {

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 原textView中，设置marquee之后，只有在获取判断获取到焦点之后才会滚动，
     * 通过覆写此判断方法，让其一直滚动
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
