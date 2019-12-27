package com.video.test.module.videolist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.video.test.R;
import com.video.test.javabean.VideoBean;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/videoList/activity")
public class VideoListActivity extends BaseActivity<VideoListPresenter> implements VideoListContract.View {
    private static final String TAG = "VideoListActivity";

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_videoList_activity)
    RecyclerView mRv;
    @Autowired
    int pid;
    @Autowired
    String tag;
    @Autowired
    String type;

    private VideoListAdapter mVideoListAdapter;
    private int mPage = 1;
    private int itemsNum = 30;


    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_video_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LogUtils.d(TAG, "getVideoList  pid == " + pid + " tag == " + tag + " type == " + type + " page == " + mPage);
//        /** 默认一次给 100个 数据 */
//        mPresenter.getVideoList(pid, tag, type, mPage++, itemsNum);
//        mRv.refresh();
        mRefreshLayout.autoRefresh();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        super.initView();
        if (null != mRefreshLayout) {
            mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
            mRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
            mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
            mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    LogUtils.d(TAG, "onLoadMore  pid == " + pid + " tag == " + tag + " type == " + type + " page == " + mPage);
                    mPresenter.addVideoList(pid, tag, type, mPage++, itemsNum);
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    mPage = 1;
                    mPresenter.getVideoList(pid, tag, type, mPage++, itemsNum);
                }
            });
        }
    }

    @Override
    protected void setAdapter() {
        int leftRight = PixelUtils.dp2px(this, 3);
        mVideoListAdapter = new VideoListAdapter();
        mRv.setAdapter(mVideoListAdapter);
        mRv.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.WHITE));


    }

    @Override
    public void setVideoList(List<VideoBean> videoBeanList) {
        mVideoListAdapter.setData(videoBeanList);
    }

    @Override
    public void addVideoList(List<VideoBean> videoBeanList) {
        if (videoBeanList.size() < itemsNum) {
            mVideoListAdapter.addData(videoBeanList);
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        } else {
            mVideoListAdapter.addData(videoBeanList);
            mRefreshLayout.finishLoadMore();
        }

    }

    @Override
    public void setPageTitle(String titleName) {
        if (null != mTvTitle && null != mIbBack) {
            mTvTitle.setText(titleName);
            //toolbar 返回按钮
            mIbBack.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.ib_back_toolbar)
    public void onViewClicked() {
        finish();
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

}
