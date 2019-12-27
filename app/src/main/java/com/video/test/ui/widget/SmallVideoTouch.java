package com.video.test.ui.widget;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;


public class SmallVideoTouch implements View.OnTouchListener {

    private int mDownX, mDownY;
    private int mMarginLeft, mMarginTop;
    private int _xDelta, _yDelta;
    private GSYBaseVideoPlayer mGsyBaseVideoPlayer;


    public SmallVideoTouch(GSYBaseVideoPlayer gsyBaseVideoPlayer, int marginLeft, int marginTop) {
        super();
        mMarginLeft = marginLeft;
        mMarginTop = marginTop;
        mGsyBaseVideoPlayer = gsyBaseVideoPlayer;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = X;
                mDownY = Y;

                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) mGsyBaseVideoPlayer
                        .getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;

                break;
            case MotionEvent.ACTION_UP:
                return !(Math.abs(mDownY - Y) < 5 && Math.abs(mDownX - X) < 5);
            case MotionEvent.ACTION_MOVE:
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mGsyBaseVideoPlayer
                        .getLayoutParams();

                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;

                if (layoutParams.leftMargin >= mMarginLeft) {
                    layoutParams.leftMargin = mMarginLeft;
                }

                if (layoutParams.topMargin >= mMarginTop) {
                    layoutParams.topMargin = mMarginTop;
                }

                if (layoutParams.leftMargin <= 0) {
                    layoutParams.leftMargin = 0;
                }

                if (layoutParams.topMargin <= 0) {
                    layoutParams.topMargin = 0;
                }

                mGsyBaseVideoPlayer.setLayoutParams(layoutParams);
                default:
                    break;
        }
        return false;
    }

}