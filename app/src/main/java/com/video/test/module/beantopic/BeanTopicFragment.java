package com.video.test.module.beantopic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.BeanTopicBean;
import com.video.test.javabean.FooterViewBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseFragment;
import com.video.test.ui.viewbinder.FooterViewBinder;
import com.video.test.ui.widget.GlideOnScrollListener;
import com.video.test.ui.widget.LoadingView;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.ui.widget.ShareDialogFragment;
import com.video.test.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
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
    private MultiTypeAdapter mAdapter;
    private Items mItems;
    private DividerItemDecoration mDecoration;


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
                mRefreshLayout.autoRefresh();
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
                mPresenter.getHomepageBeanTopicList();
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });
        if (getView() != null) {
            ((TextView) getView().findViewById(R.id.tv_searchBtn_toolbar_homepage)).setText(R.string.searchBar_resources_name);
        }
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setRefreshHeader(new RefreshHeader(mContext));
        mRefreshLayout.setOnRefreshListener(refreshLayout -> mPresenter.getHomepageBeanTopicList());
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


    @OnClick({R.id.layout_search, R.id.iv_history_toolbar_homepage, R.id.iv_share_toolbar_homepage})
    void click(View view) {
        switch (view.getId()) {
            case R.id.layout_search:
                LogUtils.d(TAG, "search btn");
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
