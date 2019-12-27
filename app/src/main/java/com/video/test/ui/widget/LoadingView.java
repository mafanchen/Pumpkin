package com.video.test.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.video.test.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单实用的页面状态统一管理 ，加载中、无网络、无数据、出错等状态的随意切换
 */
public class LoadingView extends FrameLayout {
    private int mContentViewResId;
    //    private int mEmptyViewResId = R.layout.base_page_empty;
    private int mErrorViewResId = R.layout.loading_page_error;
//    private int mLoadingViewResId = R.layout.base_page_loading;
//    private int mNoNetworkViewResId = R.layout.base_page_no_net;


    private LayoutInflater mInflater;
    private OnLoadingListener mOnLoadingListener;
    private Map<Integer, View> mResId = new HashMap<>();

    public static LoadingView wrap(Activity activity) {
        return wrap(((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0));
    }

    public static LoadingView wrap(Fragment fragment) {
        return wrap(fragment.getView());
    }

    public static LoadingView wrap(View view) {
        if (view == null) {
            throw new RuntimeException("content view can not be null");
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (view == null) {
            throw new RuntimeException("parent view can not be null");
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int index = parent.indexOfChild(view);
        parent.removeView(view);

        LoadingView loadingView = new LoadingView(view.getContext());
        parent.addView(loadingView, index, lp);
        loadingView.addView(view);
        loadingView.setContentView(view);
        return loadingView;
    }

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BaseLoadingView, defStyleAttr, 0);
//        mEmptyViewResId = a.getResourceId(R.styleable.BaseLoadingView_base_emptyView, config.getEmptyViewResId());
//        mErrorViewResId = a.getResourceId(R.styleable.BaseLoadingView_base_errorView, R.layout.base_page_error);
//        mLoadingViewResId = a.getResourceId(R.styleable.BaseLoadingView_base_loadingView, config.getLoadingViewResId());
//        mNoNetworkViewResId = a.getResourceId(R.styleable.BaseLoadingView_base_noNetworkView, config.getNoNetworkViewResId());
//        a.recycle();
    }

    private void setContentView(View view) {
        mContentViewResId = view.getId();
        mResId.put(mContentViewResId, view);
    }

//    public final void showEmpty() {
//        show(mEmptyViewResId);
//    }

    public final void showError() {
        show(mErrorViewResId);
    }

//    public final void showLoading() {
//        show(mLoadingViewResId);
//    }

//    public final void showNoNetwork() {
//        show(mNoNetworkViewResId);
//    }

    public final void showContent() {
        show(mContentViewResId);
    }

    private void show(int resId) {
        for (View view : mResId.values()) {
            if (view.getId() == mContentViewResId) {
                view.setVisibility(INVISIBLE);
            } else {
                view.setVisibility(GONE);
            }
        }
        layout(resId).setVisibility(VISIBLE);
    }

    private View layout(int resId) {
        if (mResId.containsKey(resId)) {
            return mResId.get(resId);
        }
        View view = mInflater.inflate(resId, this, false);
        view.setVisibility(GONE);
        addView(view);
        mResId.put(resId, view);
        if (resId == mErrorViewResId
//                || resId == mNoNetworkViewResId
//                || resId == mEmptyViewResId
        ) {
            View retry = view.findViewById(R.id.loading_retry);
            if (retry != null) {
                retry.setOnClickListener(v -> {
                    if (mOnLoadingListener != null) {
                        mOnLoadingListener.onRetry();
                    }
                });
            }
            View solve = view.findViewById(R.id.loading_solve);
            if (solve != null) {
                solve.setOnClickListener(v -> {
                    if (mOnLoadingListener != null) {
                        mOnLoadingListener.onSolve();
                    }
                });
            }
        }
        return view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            return;
        }
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }
        View view = getChildAt(0);
        setContentView(view);
//        showLoading();
        showContent();
    }

    /**
     * 设置重试点击事件
     *
     * @param onRetryClickListener 重试点击事件
     */
    public void setOnLoadingListener(OnLoadingListener onRetryClickListener) {
        this.mOnLoadingListener = onRetryClickListener;
    }

    public interface OnLoadingListener {
        void onRetry();

        void onSolve();
    }
}
