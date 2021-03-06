package com.video.test.module.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.event.HotSearchWordRetryEvent;
import com.video.test.module.videotype.BaseVideoTypeListFragment;
import com.video.test.sp.SpUtils;
import com.video.test.ui.adapter.BeanViewStatePagerAdapter;
import com.video.test.ui.base.BaseFragment;
import com.video.test.ui.widget.BeanViewPager;
import com.video.test.ui.widget.ShareDialogFragment;
import com.video.test.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/video/fragment")
public class VideoFragment extends BaseFragment<VideoPresenter> implements VideoContract.View {
    private static final String TAG = "VideoFragment";
    @BindView(R.id.vp_video_fragment)
    BeanViewPager mViewPager;
    @BindView(R.id.tabLayout_video_fragment)
    TabLayout mTabLayout;
    @BindView(R.id.layout_history_window)
    LinearLayout mLayoutHistory;
    @BindView(R.id.iv_history_close)
    ImageView mIvHistoryClose;
    @BindView(R.id.tv_history_name)
    TextView mTvHistoryName;
    @BindView(R.id.tv_history_time)
    TextView mTvHistoryTime;
    @BindView(R.id.tv_history_jump)
    TextView mTvHistoryJump;
    @BindView(R.id.tv_searchBtn_toolbar_homepage)
    TextView mTvSearch;
    @BindView(R.id.iv_screen)
    ImageView mIvScreen;
    @BindView(R.id.iv_history_toolbar_homepage)
    ImageView mIvHistory;
    @BindView(R.id.iv_share_toolbar_homepage)
    ImageView mIvShare;
    private int currentPid = 0;
    private String[] mTabTitles = {"", "热门", "电影", "电视剧", "综艺", "动漫"};
    private BeanViewStatePagerAdapter mBeanViewPagerAdapter;

    public static Fragment newInstance(String title) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }


    @Override
    protected void loadData() {
        mPresenter.getHotSearchWord();
        mPresenter.getHistory();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.bean_fragment_video;
    }

    private void initTabLayout() {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i, tab.view));
                tab.setTag(i);
            }
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() instanceof Integer) {
                    int position = (int) tab.getTag();
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        if (position != 0) {
                            TextView tvTitle = customView.findViewById(R.id.tv_title);
                            tvTitle.setTextColor(ContextCompat.getColor(tvTitle.getContext(), R.color.homepage_font_chose));
                        } else {
                            ImageView ivTitle = customView.findViewById(R.id.iv_title);
                            ivTitle.setImageResource(R.drawable.pic_2019_nav_selected);
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getTag() instanceof Integer) {
                    int position = (int) tab.getTag();
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        if (position != 0) {
                            TextView tvTitle = customView.findViewById(R.id.tv_title);
                            tvTitle.setTextColor(ContextCompat.getColor(tvTitle.getContext(), R.color.homepage_font_no_chose));
                        } else {
                            ImageView ivTitle = customView.findViewById(R.id.iv_title);
                            ivTitle.setImageResource(R.drawable.pic_2019_nav_default);
                        }
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private View getTabView(int position, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (position == 0) {
            View view = inflater.inflate(R.layout.layout_tab_image, parent, false);
            ImageView ivTitle = view.findViewById(R.id.iv_title);
            ivTitle.setImageResource(R.drawable.pic_2019_nav_selected);
            return view;
        } else {
            View view = inflater.inflate(R.layout.layout_tab_text, parent, false);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            tvTitle.setText(mTabTitles[position]);
            return view;
        }
    }

    @Override
    protected void setAdapter() {
        ArrayList<Fragment> fragments = mPresenter.initFragmentList();
        mBeanViewPagerAdapter = new BeanViewStatePagerAdapter(getChildFragmentManager());
        mBeanViewPagerAdapter.setItems(fragments, mTabTitles);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mBeanViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPid = ((BaseVideoTypeListFragment) fragments.get(i)).getPid();
                mPresenter.onChangeHotSearchWord(String.valueOf(((BaseVideoTypeListFragment) mBeanViewPagerAdapter.getItem(i)).getPid()));
                if (i <= 1) {
                    mIvScreen.setVisibility(View.GONE);
                    mIvShare.setVisibility(View.VISIBLE);
                    mIvHistory.setVisibility(View.VISIBLE);
                } else {
                    mIvScreen.setVisibility(View.VISIBLE);
                    mIvShare.setVisibility(View.INVISIBLE);
                    mIvHistory.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        initTabLayout();
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mPresenter.initWeChatApi();
    }


    @Override
    public void setScreenTypes(ScreenBean screenBean) {

    }


    private void jump2Activity(String activityPath) {
        ARouter.getInstance().build(activityPath).navigation();
    }


    private void showShareDialog() {
        ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance();
        String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
        shareDialogFragment.setShareItemClickListener(new ShareDialogFragment.ShareItemClickListener() {
            @Override
            public void onShareItemClick(int position) {
                switch (position) {
                    case AppConstant.WX_SCENE_SESSION:
                        LogUtils.d(TAG, "WX_SCENE_SESSION");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_wechat", "详情页微信分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_SESSION);
                        break;
                    case AppConstant.WX_SCENE_TIMELINE:
                        LogUtils.d(TAG, "WX_SCENE_TIMELINE");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_circle", "详情页朋友圈分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_TIMELINE);
                        break;
                    default:
                        break;
                }
            }
        });
        shareDialogFragment.show(getChildFragmentManager(), "dialog");
    }


    @OnClick({R.id.tv_searchBtn_toolbar_homepage, R.id.iv_history_toolbar_homepage, R.id.iv_share_toolbar_homepage, R.id.iv_search, R.id.iv_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_search:
                //传入热门搜索词进行搜索
                ARouter.getInstance().build("/search/activity")
                        .withString("searchWord", mTvSearch.getText().toString().trim())
                        .navigation();
                break;
            case R.id.tv_searchBtn_toolbar_homepage:
                //跳转到搜索页面
                jump2Activity("/search/activity");
                break;
            case R.id.iv_history_toolbar_homepage:
                LogUtils.d(TAG, "history btn");
                jump2Activity("/history/activity");
                break;
            case R.id.iv_share_toolbar_homepage:
                LogUtils.d(TAG, "share btn");
                showShareDialog();
                break;
            case R.id.iv_screen:
                ARouter.getInstance().build("/screenVideo/activity")
                        .withInt("pid", currentPid)
//                        .withString("tag", videoTitleBean.getTag())
//                        .withString("tagName", videoTitleBean.getType())
                        .navigation();
            default:
                break;
        }
    }

    @Override
    public void showHistoryLayout() {
        mLayoutHistory.setVisibility(View.VISIBLE);
    }

    @Override
    public void initHistoryLayout(String name, String time, String vodId) {
        mIvHistoryClose.setOnClickListener(view -> hideHistoryLayout());
        mTvHistoryName.setText(name);
        mTvHistoryTime.setText(time);
        mTvHistoryJump.setOnClickListener(view -> ARouter.getInstance().build("/player/activity").withString("vodId", vodId).navigation());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void hideHistoryLayout() {
        mLayoutHistory.setVisibility(View.GONE);
    }

    @Override
    public void setHotSearchWord(String searchWord) {
        mTvSearch.setText(searchWord);
    }

    @Override
    public Fragment getCurrentFragment() {
        int i = mViewPager.getCurrentItem();
        if (i == -1 || mBeanViewPagerAdapter == null) {
            return null;
        }
        return mBeanViewPagerAdapter.getItem(i);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHotSearchWordRetry(HotSearchWordRetryEvent event) {
        mPresenter.getHotSearchWord();
    }

}


