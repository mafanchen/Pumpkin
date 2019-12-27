package com.video.test.module.notice;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.javabean.NoticeBean;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.LoadingView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/notice/activity")
public class NoticeActivity extends BaseActivity<NoticePresenter> implements NoticeContract.View {
    private static final String TAG = "NoticeActivity";

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.rv_notice_activity)
    RecyclerView mRv;
    @BindView(R.id.swipeRefresh_notice_activity)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    private NoticeAdapter mNoticeAdapter;


    @Override
    protected void initToolBar() {
        if (null != mTvTitle && null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
            mTvTitle.setText(R.string.notice_systemMessage);
        }
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_notice;
    }

    @Override
    protected void initData() {
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                if (mLoadingView != null) {
                    mLoadingView.showContent();
                }
                mPresenter.getSystemNotice();
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });

        initSwipeRefresh();
        mPresenter.getSystemNotice();
    }

    @Override
    protected void setAdapter() {
        if (mRv.getItemDecorationCount() == 0) {
            DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_bg_item_divider_1dp);
            assert drawable != null;
            decoration.setDrawable(drawable);
            mRv.addItemDecoration(decoration);
        }
        mNoticeAdapter = new NoticeAdapter();
        mRv.setAdapter(mNoticeAdapter);
    }


    @Override
    public void setSystemNotice(List<NoticeBean> noticeBeanList) {
        mNoticeAdapter.setData(noticeBeanList);
    }

    @Override
    public void setSwipeRefreshStatus(Boolean status) {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setRefreshing(status);
        }
    }

    private void initSwipeRefresh() {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setColorSchemeColors(Color.parseColor("#ff9900"), Color.parseColor("#aaaaaa"));
            mSwipeRefresh.setEnabled(false);
        }
    }

    @OnClick(R.id.ib_back_toolbar)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.showError();
        }
    }
}
