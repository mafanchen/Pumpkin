package com.video.test.ui.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.video.test.TestApp;

/**
 * @author Enoch Created on 2018/11/8.
 */
public class GlideOnScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                // 拖动时 加载
                Glide.with(TestApp.getContext()).resumeRequests();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //  自动滑动时 暂停加载
                Glide.with(TestApp.getContext()).pauseRequests();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                // 静止时 加载
                Glide.with(TestApp.getContext()).resumeRequests();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }
}
