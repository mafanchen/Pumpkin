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
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.BannerBean;
import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.javabean.NotificationBean;
import com.video.test.javabean.base.IPageJumpBean;
import com.video.test.module.videotype.BaseVideoTypeListFragment;
import com.video.test.ui.widget.Banner;
import com.video.test.ui.widget.GlideImageLoader;
import com.video.test.ui.widget.TextSwitcher;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.youth.banner.BannerConfig;

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
    @BindView(R.id.banner_recommend_fragment)
    Banner mBanner;
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
    public void onResume() {
        super.onResume();
        /*广告栏开始自动轮播*/
        if (null != mBanner) {
            mBanner.startAutoPlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*广告栏停止自动轮播*/
        if (null != mBanner) {
            mBanner.stopAutoPlay();
        }
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

    @SuppressWarnings("unchecked")
    @Override
    public void initBanner(List<String> bannerList, List<String> bannerContent, final List<BannerBean> bannerBeanList) {
        if (null != mBanner) {
            mBanner.setTag(bannerBeanList);
            LogUtils.d(TAG, "initBanner banner == " + bannerList.toString());
            mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
            mBanner.setImageLoader(new GlideImageLoader());
            mBanner.setImages(bannerList);
            mBanner.setBannerTitles(bannerContent);
            mBanner.setIndicatorGravity(BannerConfig.RIGHT);
            mBanner.setDelayTime(2500);
            mBanner.setOnBannerListener(position -> {
                LogUtils.d(TAG, "OnBannerClick position == " + position);
                //Banner 埋点
                List<BannerBean> list = (List<BannerBean>) mBanner.getTag();
                if (list != null) {
                    BannerBean bannerBean = list.get(position);
                    MobclickAgent.onEvent(TestApp.getContext(), "click_big_banner", bannerBean.getTargetName());
                    getBannerType(bannerBean);
                }
            });
            mBanner.start();
        } else {
            LogUtils.d(TAG, "initBanner  banner = null");
        }
    }

    /**
     * 获取当前Banner和通知的类型 根据类型的不同执行不同的方法
     */
    private void getBannerType(IPageJumpBean jumpBean) {

        switch (jumpBean.getType()) {
            case AppConstant.BANNER_TYPE_VIDEO:
                LogUtils.d(TAG, "BANNER_TYPE_VIDEO");
                ARouter.getInstance().build("/player/activity").withString("vodId", jumpBean.getVodId()).navigation();
                break;
            case AppConstant.BANNER_TYPE_ROUTER:
                LogUtils.d(TAG, "BANNER_TYPE_ROUTER");
                String path = jumpBean.getAndroidRouter();
                if (TextUtils.isEmpty(path)) {
                    break;
                }
                ARouter.getInstance().build(path).navigation();
                break;
            case AppConstant.BANNER_TYPE_WEBURL:
                LogUtils.d(TAG, "BANNER_TYPE_WEBURL = " + jumpBean.getWebUrl());
                startActivity(IntentUtils.getBrowserIntent(jumpBean.getWebUrl()));
                MobclickAgent.onEvent(TestApp.getContext(), "click_ads_banner", jumpBean.getTargetName());
                break;
            case AppConstant.BANNER_TYPE_TOPIC:
                LogUtils.d(TAG, "BANNER_TYPE_Topic");
                int pid = jumpBean.getTopicRouter().getZt_pid();
                String tag = jumpBean.getTopicRouter().getZt_tag();
                String type = jumpBean.getTopicRouter().getZt_type();

                ARouter.getInstance().build("/topicVideoList/activity")
                        .withInt("pid", pid)
                        .withString("tag", tag)
                        .withString("type", type)
                        .navigation();
                break;
            case AppConstant.BANNER_TYPE_AD:
                if (jumpBean instanceof BannerBean) {
                    String adId = ((BannerBean) jumpBean).getId();
                    mPresenter.addAdInfo(AppConstant.AD_TYPE_BANNER, adId);
                }
                LogUtils.d(TAG, "BANNER_TYPE_WEBURL = " + jumpBean.getWebUrl());
                startActivity(IntentUtils.getBrowserIntent(jumpBean.getWebUrl()));
                MobclickAgent.onEvent(TestApp.getContext(), "click_ads_banner", jumpBean.getTargetName());
                break;
            default:
                break;
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

    @Override
    protected void onRefresh() {
        mPresenter.getHomePageVideoList(getPid());
        mPresenter.getBannerAndNotice();
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
