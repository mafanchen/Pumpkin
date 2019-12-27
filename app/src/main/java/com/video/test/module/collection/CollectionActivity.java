package com.video.test.module.collection;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.CollectionListBean;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.CenterDrawableTextView;
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
 */
@Route(path = "/collection/activity")
public class CollectionActivity extends BaseActivity<CollectionPresenter> implements CollectionContract.View {

    private static final String TAG = "CollectionActivity";
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.rv_collection_activity)
    RecyclerView mRvCollection;
    @BindView(R.id.tv_selectAll_collection_activity)
    TextView mTvSelectAll;
    @BindView(R.id.tv_delete_collection_activity)
    TextView mTvDelete;
    @BindView(R.id.group_bottom_collection_activity)
    Group mGroup;
    @BindView(R.id.tv_background_noCollection_collection)
    CenterDrawableTextView mTvNoCollection;
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_editBtn_toolbar)
    TextView mTvEditBtn;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    private int mLimit = 30;
    private int mPage;
    private CollectionAdapter mCollectionAdapter;
    private boolean mIsManager;


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_collection;
    }


    @Override
    protected void initToolBar() {
        if (null != mTvTitle && null != mIbBack) {
            mTvTitle.setText(R.string.activity_collection);
            mIbBack.setVisibility(View.VISIBLE);
        }
        if (mTvEditBtn != null) {
            mTvEditBtn.setVisibility(View.VISIBLE);
            mTvEditBtn.setTextColor(Color.parseColor("#ffad43"));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshLayout.autoRefresh();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                if (mLoadingView != null) {
                    mLoadingView.showContent();
                }
                mPage = 1;
                mPresenter.getUserCollection(mPage, mLimit);
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });
        mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage++;
                mPresenter.getMoreCollection(mPage, mLimit);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                mPresenter.getUserCollection(mPage, mLimit);
                mRefreshLayout.setEnableRefresh(false);
            }
        });
    }

    @Override
    protected void setAdapter() {
        int leftRight = PixelUtils.dp2px(this, 3);
        mCollectionAdapter = new CollectionAdapter();
        mRvCollection.setAdapter(mCollectionAdapter);
        mRvCollection.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.WHITE));
        mRvCollection.addOnScrollListener(new GlideOnScrollListener());
    }

    @OnClick({R.id.tv_delete_collection_activity, R.id.tv_selectAll_collection_activity, R.id.tv_editBtn_toolbar, R.id.ib_back_toolbar})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_delete_collection_activity:
                String selectItem = mCollectionAdapter.getSelectItem();
                LogUtils.d(TAG, "删除按钮被点击了 jsonArray == " + selectItem);
                mPresenter.deleteCollection(selectItem);
                break;
            case R.id.tv_selectAll_collection_activity:
                LogUtils.d(TAG, "全选按钮被点击了");
                mCollectionAdapter.selectAllItem();
                break;
            case R.id.tv_editBtn_toolbar:
                clickSelectBtn(mTvEditBtn);
                break;
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
                break;
        }
    }


    private void clickSelectBtn(TextView textView) {
        if (null != textView && null != mGroup) {
            if (View.VISIBLE == mGroup.getVisibility()) {
                mGroup.setVisibility(View.GONE);
                editBtnClick(mIsManager);
                mIsManager = false;
                mCollectionAdapter.changeGroupVisibility(false);
            } else {
                mGroup.setVisibility(View.VISIBLE);
                editBtnClick(mIsManager);
                mIsManager = true;
                mCollectionAdapter.changeGroupVisibility(true);
            }
        }
    }

    private void editBtnClick(boolean isClick) {
        if (isClick) {
            mTvEditBtn.setText(R.string.activity_history_edit);
            mTvEditBtn.setTextColor(Color.parseColor("#ffad43"));
        } else {
            mTvEditBtn.setText(R.string.activity_history_edit_cancel);
            mTvEditBtn.setTextColor(ContextCompat.getColor(this, R.color.history_font_btn_edit));
        }
    }

    @Override
    public void getDeleteCollectionMessage(String message) {
        ToastUtils.showLongToast(TestApp.getContext(), message);
        // 竖线第一页完成的时候,已经关闭了下拉刷新,故此处需要重新打开下来刷新
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void setUserCollection(List<CollectionListBean> collectionListBeans) {
        if (!collectionListBeans.isEmpty()) {
            mTvNoCollection.setVisibility(View.INVISIBLE);
            mCollectionAdapter.setData(collectionListBeans);
            if (collectionListBeans.size() < 30 && null != mRefreshLayout) {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        } else {
            mTvNoCollection.setVisibility(View.VISIBLE);
            mCollectionAdapter.setData(collectionListBeans);
        }
    }

    @Override
    public void addMoreCollection(List<CollectionListBean> collectionListBeans) {
        if (!collectionListBeans.isEmpty()) {
            mCollectionAdapter.addData(collectionListBeans);
        } else {
            if (null != mRefreshLayout) {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }


    @Override
    public void loadingComplete() {
        if (null != mRefreshLayout) {
            mRefreshLayout.finishLoadMore();
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

    /**
     * 首次刷新成功之后 就关闭下来刷新功能
     * @param page
     */
    @Override
    public void cancelRefreshLayout(int page) {
        if (null != mRefreshLayout && 1 == page) {
            mRefreshLayout.setEnableRefresh(false);
        }
    }

    @Override
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.showError();
        }
    }

}

