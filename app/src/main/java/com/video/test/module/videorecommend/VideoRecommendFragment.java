package com.video.test.module.videorecommend;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.javabean.NotificationBean;
import com.video.test.module.videotype.BaseVideoTypeListFragment;
import com.video.test.ui.widget.TextSwitcher;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/videoRecommend/fragment")
public class VideoRecommendFragment extends BaseVideoTypeListFragment<VideoRecommendPresenter> implements VideoRecommendContract.View {
    private static final String TAG = "VideoRecommendFragment";
    @BindView(R.id.text_switcher_notice)
    TextSwitcher mSwitcherNotice;
    private ViewSwitcher.ViewFactory textFactory;
    private TextSwitcher.TextBinder<HomePageNoticeBean> textBinder;

    public static VideoRecommendFragment newInstance() {
        VideoRecommendFragment fragment = new VideoRecommendFragment();
        fragment.setPid(AppConstant.VIDEO_LIST_PID_HOT);
        fragment.setAdType(AppConstant.AD_TYPE_RECOMMEND);
        return fragment;
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initViewBeforeLoadData() {
        if (getView() != null) {
            mBanner = getView().findViewById(R.id.banner_recommend_fragment);
            mSwipeRefresh = getView().findViewById(R.id.refresh_recommend_fragment);
            mRvVideoList = getView().findViewById(R.id.rv_recommend_fragment);
            mLoadingView = getView().findViewById(R.id.loadingView);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.bean_fragment_recommend;
    }

    @Override
    public void onDestroyView() {
        mSwitcherNotice.stop();
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initNotice(List<HomePageNoticeBean> noticeList) {
        if (noticeList == null || noticeList.isEmpty()) {
            mSwitcherNotice.stop();
            mSwitcherNotice.setVisibility(View.GONE);
        } else {
            if (textFactory == null) {
                textFactory = () -> {
                    TextView textView = new TextView(mContext);
                    textView.setSingleLine();
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    textView.setTextColor(Color.parseColor("#333333"));
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                    textView.setCompoundDrawablePadding(PixelUtils.dp2px(mContext, 15));
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_VERTICAL;
                    textView.setLayoutParams(params);
                    return textView;
                };
            }
            if (textBinder == null) {
                textBinder = new TextSwitcher.TextBinder<HomePageNoticeBean>() {
                    @Override
                    public void bind(HomePageNoticeBean item, @NotNull TextView textView) {
                        textView.setText(item.getContent());
                        GlideApp.with(VideoRecommendFragment.this)
                                .load(item.getPic())
                                .skipMemoryCache(true)
                                .override(PixelUtils.dp2px(mContext, 75), PixelUtils.dp2px(mContext, 27))
                                .centerCrop()
                                .into(new CustomViewTarget<TextView, Drawable>(textView) {
                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        textView.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                                    }

                                    @Override
                                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                };
            }
            mSwitcherNotice.setFactory(textFactory);
            mSwitcherNotice.setTextBinder(textBinder);
            textBinder.setData(noticeList);
            mSwitcherNotice.start();
        }
    }

    @OnClick(R.id.text_switcher_notice)
    void onViewClick() {
        LogUtils.d(TAG, "OnNoticeClick");
        if (textBinder == null) {
            return;
        }
        HomePageNoticeBean bean = textBinder.getCurrentItem();
        if (bean != null) {
            getBannerType(bean);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onHandleNotification(NotificationBean bean) {
        EventBus.getDefault().removeStickyEvent(bean);
        getBannerType(bean);
    }

    //    private void getUserLoginStatus() {
//        String userLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL);
//    }

}
