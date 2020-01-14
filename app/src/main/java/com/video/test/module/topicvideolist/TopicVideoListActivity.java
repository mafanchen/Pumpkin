package com.video.test.module.topicvideolist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.VideoBean;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.GlideOnScrollListener;
import com.video.test.ui.widget.LoadingView;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 * 专题的详情页面
 */
@Route(path = "/topicVideoList/activity")
public class TopicVideoListActivity extends BaseActivity<TopicVideoListPresenter> implements TopicVideoListContract.View {
    @BindView(R.id.rv_videoList_activity)
    RecyclerView mRv;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.tv_topic_title)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_topic_content)
    TextView mTvContent;
    @BindView(R.id.iv_topic_bg)
    ImageView mIvBg;
    @BindView(R.id.appbar_collect)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitleToolbar;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    @BindView(R.id.tv_topic_videoNum)
    TextView mTopicNum;
    @BindView(R.id.ck_topic_collect)
    CheckBox mCheckBoxCollect;
    @Autowired
    int pid;
    @Autowired
    String tag;
    @Autowired
    String type;

    private TopicVideoListAdapter mVideoListAdapter;

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshLayout.autoRefresh();
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_video_list;
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        super.initView();
        if (mLoadingView != null) {
            mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
                @Override
                public void onRetry() {
                    if (mLoadingView != null) {
                        mLoadingView.showContent();
                    }
                    mPresenter.getVideoList(pid, tag, type);
                }

                @Override
                public void onSolve() {
                    ARouter.getInstance().build("/solve/activity").navigation();
                }
            });
        }
        if (null != mRefreshLayout) {
            mRefreshLayout.setEnableLoadMore(false);
            mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
            mRefreshLayout.setOnRefreshListener(refreshLayout -> mPresenter.getVideoList(pid, tag, type));
        }

        if (null != mAppBarLayout) {
            mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    float ratio = Math.abs((float) verticalOffset / totalScrollRange);
                    if (mTvTitleToolbar != null) {
                        mTvTitleToolbar.setAlpha(ratio);
                    }
                }
            });
        }
    }

    @Override
    protected void setAdapter() {
        int leftRight = PixelUtils.dp2px(this, 3);
        mVideoListAdapter = new TopicVideoListAdapter();
        mRv.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.WHITE));
        mRv.addOnScrollListener(new GlideOnScrollListener());
        mRv.setAdapter(mVideoListAdapter);
    }

    @Override
    public void setVideoList(List<VideoBean> videoBeanList) {
        LogUtils.d("TopicVideoListActivity", "setVideoList");
        mVideoListAdapter.setData(videoBeanList);
        //数据都加载完成之后，再展示收藏按钮，默认隐藏
        mCheckBoxCollect.setVisibility(View.VISIBLE);


    }


    @Override
    public void setPageTitle(String titleName) {
        if (null != mTvTitle) {
            mTvTitle.setText(titleName);
        }
        if (null != mTvTitleToolbar) {
            mTvTitleToolbar.setText(titleName);
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
    public void setPic(String ztPic) {
        if (mIvBg != null && ztPic != null) {
            GlideApp.with(this).load(ztPic).centerCrop().into(mIvBg);
        }
    }

    @Override
    public void setContent(String ztDetail) {
        if (mTvContent != null) {
            mTvContent.setText(ztDetail);
        }
    }


    @Override
    public void setTopicNum(int videoNum) {
        if (null != mTopicNum) {
            mTopicNum.setVisibility(View.VISIBLE);
            mTopicNum.setText(getResources().getString(R.string.topic_video_num, String.valueOf(videoNum)));
        }
    }

    @OnClick({R.id.ib_back_toolbar, R.id.ck_topic_collect})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back_toolbar:
                finish();
                break;
            case R.id.ck_topic_collect:
                //调用接口，请求失败之后，再把状态改回来
                boolean checked = mCheckBoxCollect.isChecked();
                //tag就是专题id
                mPresenter.collect(checked, tag);
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

    @Override
    public void setCollectCheckBoxChecked(boolean checked) {
        if (mCheckBoxCollect != null) {
            mCheckBoxCollect.setChecked(checked);
        }
    }

    @Override
    public void showToast(String s) {
        ToastUtils.showToast(this, s);
    }

}
