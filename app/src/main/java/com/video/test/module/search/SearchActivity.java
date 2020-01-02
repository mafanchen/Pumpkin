package com.video.test.module.search;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.umeng.analytics.MobclickAgent;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.BeanTopicContentBean;
import com.video.test.javabean.FooterViewBean;
import com.video.test.javabean.SearchNoMoreDataBean;
import com.video.test.javabean.SearchResultVideoBean;
import com.video.test.javabean.SearchSortTypeBean;
import com.video.test.javabean.VideoRecommendBean;
import com.video.test.module.videorecommend.VideoRecommendHorizontalViewBinder;
import com.video.test.ui.adapter.SearchAssociationAdapter;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.listener.SearchViewListener;
import com.video.test.ui.viewbinder.FooterViewBinder;
import com.video.test.ui.widget.BeanSearchBarView;
import com.video.test.ui.widget.CenterDrawableTextView;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.GlideOnScrollListener;
import com.video.test.ui.widget.LoadingView;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/search/activity")
public class SearchActivity extends BaseActivity<SearchPresenter> implements SearchContract.View {

    @BindView(R.id.rv_search)
    RecyclerView mRvSearchResult;
    @BindView(R.id.tb_search_custom)
    BeanSearchBarView mTbSearch;
    @BindView(R.id.swipeRefresh_search)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.tv_background_noResult_search)
    CenterDrawableTextView mTvCacheBackground;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.group_sort)
    Group mGroupSort;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    @BindView(R.id.tv_no_association)
    TextView mTvNoAssociaton;
    @BindView(R.id.rv_association)
    RecyclerView mRvAssociation;

    private SearchAssociationAdapter mAdapterAssociation;
    private MultiTypeAdapter mAdapter;

    @Autowired(name = "searchWord")
    String searchWord;
    //这里由外部点击进入，会传入搜索关键词，此时触发了联想词搜索，这里无需进行联想
    private boolean isSearchImmediately = false;
    private SearchViewBinder mSearchViewBinder;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(searchWord)) {
            mTbSearch.setEditTextContentAndSearchImmediately(searchWord);
            isSearchImmediately = true;
        }
    }

    @Override
    protected void initData() {
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                if (mLoadingView != null) {
                    mLoadingView.showContent();
                }
                mPresenter.getSearchResult(searchWord);
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });

        initSwipeRefresh();
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }

    @Override
    protected void setAdapter() {
        mAdapterAssociation = new SearchAssociationAdapter();
        mAdapterAssociation.setOnItemClickListener(word -> mTbSearch.notifyStartSearching(word));
        if (mRvAssociation.getItemDecorationCount() == 0) {
            android.support.v7.widget.DividerItemDecoration decoration =
                    new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_bg_item_divider_1dp);
            assert drawable != null;
            decoration.setDrawable(drawable);
            mRvAssociation.addItemDecoration(decoration);
        }

        mAdapter = new MultiTypeAdapter();
        mSearchViewBinder = new SearchViewBinder();
        mSearchViewBinder.setOnCollectListener((isCollect, item) -> mPresenter.onCollect(isCollect, item.getVod_id(), item.getCollect_id()));
        mAdapter.register(SearchResultVideoBean.class, mSearchViewBinder);
        mAdapter.register(SearchNoMoreDataBean.class, new SearchNoMoreDataViewBinder());
        mAdapter.register(VideoRecommendBean.class, new VideoRecommendHorizontalViewBinder());
        mAdapter.register(FooterViewBean.class, new FooterViewBinder());
        mAdapter.register(BeanTopicContentBean.class, new SearchTopicViewBinder());

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                List<?> items = mAdapter.getItems();
                Object item = items.get(position);
                if (item instanceof VideoRecommendBean) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        mRvSearchResult.setLayoutManager(manager);
        int width = PixelUtils.dp2px(this, 3f);
        mRvSearchResult.addItemDecoration(new DividerItemDecoration(width, 0, Color.WHITE));
        mRvSearchResult.setAdapter(mAdapter);
        mRvSearchResult.addOnScrollListener(new GlideOnScrollListener());
        mTbSearch.setSearchViewListener(new BeanSearchListener());
    }

    @Override
    public void setSearchResult(Items items, String searchWord) {
        mSearchViewBinder.setSearchWord(searchWord);
        mAdapter.setItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSwipeRefreshStatus(Boolean status) {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setRefreshing(status);
        }
    }

    @Override
    public void initSortRadioGroup(List<SearchSortTypeBean> sortTypeList) {
        if (mRadioGroup.getChildCount() == 0) {
            for (SearchSortTypeBean bean : sortTypeList) {
                mRadioGroup.addView(buildSortButton(bean));
            }
        }
        if (mRadioGroup.getChildCount() != 0) {
            ((RadioButton) mRadioGroup.getChildAt(0)).setChecked(true);
        }
    }

    @Override
    public void addCollectionSuccess(String collectId, String vodId) {
        if (mAdapter == null || mAdapter.getItems().isEmpty()) {
            return;
        }
        for (Object item : mAdapter.getItems()) {
            if (item instanceof SearchResultVideoBean) {
                SearchResultVideoBean bean = (SearchResultVideoBean) item;
                if (TextUtils.equals(bean.getVod_id(), vodId)) {
                    bean.setIs_collect(true);
                    bean.setCollect_id(collectId);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    public void addCollectionError(String vodId) {
        if (mAdapter == null || mAdapter.getItems().isEmpty()) {
            return;
        }
        for (Object item : mAdapter.getItems()) {
            if (item instanceof SearchResultVideoBean) {
                SearchResultVideoBean bean = (SearchResultVideoBean) item;
                if (TextUtils.equals(bean.getVod_id(), vodId)) {
                    bean.setIs_collect(false);
                    bean.setCollect_id(null);
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    public void removeCollected(boolean success, String collectedId) {
        if (mAdapter == null || mAdapter.getItems().isEmpty()) {
            return;
        }
        for (Object item : mAdapter.getItems()) {
            if (item instanceof SearchResultVideoBean) {
                SearchResultVideoBean bean = (SearchResultVideoBean) item;
                if (TextUtils.equals(bean.getCollect_id(), collectedId)) {
                    bean.setIs_collect(!success);
                    if (success) {
                        bean.setCollect_id(null);
                    } else {
                        bean.setCollect_id(collectedId);
                    }
                    mAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Override
    public void hideSortType() {
        mGroupSort.setVisibility(View.GONE);
    }

    @Override
    public void showSortType() {
        mGroupSort.setVisibility(View.VISIBLE);
    }

    private RadioButton buildSortButton(SearchSortTypeBean bean) {
        RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.bean_radio_button_search_sort, mRadioGroup, false);
        radioButton.setTag(bean);
        radioButton.setText(bean.getKey());
        radioButton.setOnClickListener(v -> mPresenter.getSearchResult(bean));
        return radioButton;
    }

    private void initSwipeRefresh() {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setColorSchemeColors(Color.parseColor("#ff9900"), Color.parseColor("#aaaaaa"));
            mSwipeRefresh.setEnabled(false);
        }
    }


    private class BeanSearchListener implements SearchViewListener {

        @Override
        public void onRefreshAutoComplete(String text) {

        }

        @Override
        public void onSearch(String text) {
            if (!TextUtils.isEmpty(text)) {
                searchWord = text;
                LogUtils.i(getClass(), "FFSearchViewListener onSearch == " + text);
                mPresenter.getSearchResult(text);
                MobclickAgent.onEvent(TestApp.getContext(), "search_search_keywords", text);
            } else {
                ToastUtils.showToast(TestApp.getContext(), "请输入您想搜索的内容");
            }
        }

        @Override
        public void onAssociation(String string) {
            mLoadingView.showContent();
            mTvNoAssociaton.setText(getString(R.string.search_association, string));
            //展示联想词列表
            //如果是点击了关键字直接搜索，则无需进行联想词搜索
            if (isSearchImmediately) {
                isSearchImmediately = false;
            } else {
                //这里进行搜索词联想，当搜索词为空，则隐藏联想列表，否则显示联想词列表并且进行联想
                if (TextUtils.isEmpty(string)) {
                    hideAssociationView();
                } else {
                    showAssociationView();
                    mPresenter.getAssociationWord(string);
                }
            }
        }

        @Override
        public void stopAssociation() {
            isSearchImmediately = true;
        }
    }

    private void showAssociationView() {
        mRvAssociation.setVisibility(View.VISIBLE);
        mTvNoAssociaton.setVisibility(View.VISIBLE);
    }

    private void hideAssociationView() {
        mRvAssociation.setVisibility(View.GONE);
        mTvNoAssociaton.setVisibility(View.GONE);
    }

    @Override
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.showError();
        }
    }

    @Override
    public void setAssociationWords(List<String> data) {
        mAdapterAssociation.setData(data);
        mAdapterAssociation.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_no_association)
    public void onViewClick() {
//        这里直接开始搜索，并且隐藏联想界面
        hideAssociationView();
        searchWord = mTbSearch.getInputWord();
        mTbSearch.notifyStartSearching(searchWord);
    }

    @Override
    protected void setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(getResources().getColor(R.color.navigation_background));
            ViewGroup contentView = this.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (null != childView) {
                ViewCompat.setFitsSystemWindows(childView, true);
            }
        }
    }
}
