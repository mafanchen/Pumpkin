package com.video.test.module.beantopic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.video.test.R;
import com.video.test.javabean.BeanTopicBean;
import com.video.test.javabean.FooterViewBean;
import com.video.test.javabean.TabEntityBean;
import com.video.test.ui.base.BaseFragment;
import com.video.test.ui.viewbinder.FooterViewBinder;
import com.video.test.ui.widget.BeanTopicTabLayout;
import com.video.test.ui.widget.GlideOnScrollListener;
import com.video.test.ui.widget.LoadingView;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/beanTopic/fragment")
public class BeanTopicFragment extends BaseFragment<BeanTopicPresenter> implements BeanTopicContract.View {
    private static final String TAG = "BeanTopicFragment";
    @BindView(R.id.rv_beanTopic_fragment)
    RecyclerView mRecycleView;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    @BindView(R.id.tabLayout_topic_toolbar)
    BeanTopicTabLayout mTabLayout;
    private String[] mTabContent = {"最新", "最热"};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private int order = 2;
    private MultiTypeAdapter mAdapter;
    private Items mItems;

    public static Fragment newInstance(String title) {
        BeanTopicFragment beanTopicFragment = new BeanTopicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        beanTopicFragment.setArguments(bundle);
        return beanTopicFragment;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged : " + hidden);
        if (hidden) {

        } else {
            if (mRefreshLayout != null) {
                // 不再每次展示时刷新
                // mRefreshLayout.autoRefresh();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.initWeChatApi();
    }

    @Override
    protected void loadData() {
        mTabEntities.add(new TabEntityBean(mTabContent[0], 0, 0));
        mTabEntities.add(new TabEntityBean(mTabContent[1], 0, 0));

    }

    @Override
    protected void initView() {
        super.initView();
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                if (mLoadingView != null) {
                    mLoadingView.showContent();
                }
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });
        initRefreshLayout();
        initTabLayout();
    }

    private void initTabLayout() {
        mTabLayout.setTabData(mTabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                //position 0 为最新　order 传2 　1 为最热 order 传1
                switch (position) {
                    case 0:
                        order = 2;
                        break;
                    case 1:
                        order = 1;
                        break;
                    default:
                        break;
                }
                Log.d(TAG, "onTabSelect order=" + order);
                mRefreshLayout.autoRefresh();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        //初始化时 默认最热TAG 并且请求最热TAG数据
        mTabLayout.setCurrentTab(0);
        mRefreshLayout.autoRefresh();
    }


    private void initRefreshLayout() {
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setRefreshHeader(new RefreshHeader(mContext));
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mPresenter.getHomepageBeanTopicList(order);
        });
    }

    @Override
    protected void setAdapter() {
        mAdapter = new MultiTypeAdapter();
        mAdapter.register(BeanTopicBean.class, new BeanTopicViewBinder());
        mAdapter.register(FooterViewBean.class, new FooterViewBinder());
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.addOnScrollListener(new GlideOnScrollListener());
        mRecycleView.setAdapter(mAdapter);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.bean_fragment_bean_topic;
    }

    @Override
    public void setHomepageBeanTopicList(List<BeanTopicBean> beanTopicList) {
        if (null == mItems) {
            LogUtils.d(TAG, "setHomepageBeanTopicList mItem not exist");
            mItems = new Items();
            mItems.addAll(beanTopicList);
            mItems.add(new FooterViewBean());
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        } else {
            LogUtils.d(TAG, "setHomepageBeanTopicList mItem not exist");
            mItems.clear();
            mItems.addAll(beanTopicList);
            mItems.add(new FooterViewBean());
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showRefreshLayout() {
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefreshAnimationOnly();
        }
    }

    @Override
    public void hideRefreshLayout(boolean isSuccess) {
        if (null != mRefreshLayout) {
            mRefreshLayout.finishRefresh(isSuccess);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mItems) {
            mItems = null;
            mAdapter = null;
        }
    }

    private void jump2Activity(String activityPath) {
        ARouter.getInstance().build(activityPath).navigation();
    }

 /*   private void showShareDialog() {
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
    }*/


    @OnClick({R.id.iv_search_toolbar})
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_search_toolbar:
                LogUtils.d(TAG, "search btn");
                jump2Activity("/search/activity");
                break;
            default:
                break;
        }
    }

    @Override
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.showError();
        }
    }

}
