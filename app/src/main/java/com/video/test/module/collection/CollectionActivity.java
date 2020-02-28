package com.video.test.module.collection;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.video.test.R;
import com.video.test.javabean.TabEntityBean;
import com.video.test.javabean.base.ISelectableBean;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.CenterDrawableTextView;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.GlideOnScrollListener;
import com.video.test.ui.widget.LoadingView;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.ToastUtils;

import java.util.ArrayList;
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
    @BindView(R.id.tabLayout_collection)
    CommonTabLayout mTabLayout;
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
            mTvEditBtn.setTextColor(Color.parseColor("#38dca1"));
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.autoRefresh();
    }

    @Override
    protected void initView() {
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                if (mLoadingView != null) {
                    mLoadingView.showContent();
                }
                mPresenter.getUserCollection();
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
        mRefreshLayout.setOnRefreshListener(refreshLayout -> mPresenter.getUserCollection());
        initTabLayout();
    }

    private void initTabLayout() {
        String[] tabContent = {"电影", "电视剧", "综艺", "动漫", "专题"};
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        for (String s : tabContent) {
            tabEntities.add(new TabEntityBean(s, 0, 0));
        }
        mTabLayout.setTabData(tabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mPresenter.changePage(position + 1);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        //初始化时 默认最热TAG 并且请求最热TAG数据
        mTabLayout.setCurrentTab(0);
    }


    @Override
    protected void setAdapter() {
        int leftRight = PixelUtils.dp2px(this, 3);
        mCollectionAdapter = new CollectionAdapter();
        mRvCollection.setAdapter(mCollectionAdapter);
        mRvCollection.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.WHITE));
        mRvCollection.setLayoutManager(new GridLayoutManager(this, 3));
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
            mTvEditBtn.setTextColor(Color.parseColor("#38dca1"));
        } else {
            mTvEditBtn.setText(R.string.activity_history_edit_cancel);
            mTvEditBtn.setTextColor(ContextCompat.getColor(this, R.color.history_font_btn_edit));
        }
    }

    @Override
    public void getDeleteCollectionMessage(String message) {
        ToastUtils.showLongToast(message);
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void setUserCollection(List<ISelectableBean> collectionListBeans) {
        if (!collectionListBeans.isEmpty()) {
            mTvNoCollection.setVisibility(View.INVISIBLE);
            mCollectionAdapter.setData(collectionListBeans);
        } else {
            mTvNoCollection.setVisibility(View.VISIBLE);
            mCollectionAdapter.setData(collectionListBeans);
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
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.showError();
        }
    }

    @Override
    public void setItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (mRvCollection != null) {
            if (mRvCollection.getItemDecorationCount() > 0) {
                mRvCollection.removeItemDecorationAt(0);
            }
            mRvCollection.addItemDecoration(decoration);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        if (mRvCollection != null) {
            mRvCollection.setLayoutManager(manager);
        }
    }

}

