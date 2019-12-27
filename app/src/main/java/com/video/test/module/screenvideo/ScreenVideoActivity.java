package com.video.test.module.screenvideo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
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
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.ScreenAreaBean;
import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.ScreenCartoonBean;
import com.video.test.javabean.ScreenMovieBean;
import com.video.test.javabean.ScreenTeleplayBean;
import com.video.test.javabean.ScreenTypeBean;
import com.video.test.javabean.ScreenVarietyBean;
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

@Route(path = "/screenVideo/activity")
public class ScreenVideoActivity extends BaseActivity<ScreenVideoPresenter> implements ScreenVideoContract.View {
    private static final String TAG = "ScreenVideoActivity";

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_screenContent_screenVideo)
    TextView mTvScreenContent;
    @BindView(R.id.appBay_screenVideo_activity)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.rv_area_screenVideo_activity)
    RecyclerView mRvArea;
    @BindView(R.id.rv_type_screenVideo_activity)
    RecyclerView mRvType;
    @BindView(R.id.rv_year_screenVideo_activity)
    RecyclerView mRvYear;
    @BindView(R.id.rv_update_screenVideo_activity)
    RecyclerView mRvSort;
    @BindView(R.id.rv_screenVideo_activity)
    RecyclerView mRvVideos;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    //资源的种类
    @Autowired
    int pid;
    // 资源的类型  电影资源的类型为 电影种类 其他的资源类型为 电影的地区
    @Autowired
    String tag;
    // 资源的类型名称*
    @Autowired
    String tagName;

    String mType;
    String mArea;
    String mYear;
    String mSort;
    String mTypeName;
    String mAreaName;
    String mYearName;
    String mSortName;

    private int mPage = 1;
    private int mItemsNum = 30;
    private ScreenAreaAdapter mScreenAreaAdapter;
    private ScreenYearAdapter mScreenYearAdapter;
    private ScreenTypeAdapter mScreenTypeAdapter;
    private ScreenSortAdapter mScreenSortAdapter;
    private ScreenVideoListAdapter mVideoListAdapter;

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_screen_video;
    }

    @Override
    protected void initToolBar() {
        if (null != mIbBack && null != mTvTitle) {
            mTvTitle.setText(R.string.toolbar_screen);
            mIbBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRefreshLayout.autoRefresh();
    }

    @Override
    protected void initView() {
        super.initView();
        mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                LogUtils.d(TAG, "onLoadMore  pid == " + pid + " tag == " + tag + " tagName == " + tagName + " page == " + mPage);
                mPresenter.moreScreenResult(++mPage, mItemsNum, pid, mArea, tag, mYear, mSort);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadVideoList();
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getScreenTypes();
    }

    @Override
    protected void setAdapter() {
        mScreenAreaAdapter = new ScreenAreaAdapter();
        mScreenYearAdapter = new ScreenYearAdapter();
        mScreenTypeAdapter = new ScreenTypeAdapter();
        mScreenSortAdapter = new ScreenSortAdapter();
        mVideoListAdapter = new ScreenVideoListAdapter();
        int leftRight = PixelUtils.dp2px(this, 3);

        mRvVideos.setAdapter(mVideoListAdapter);
        mRvVideos.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.WHITE));
        setListener();
    }


    @Override
    public void setScreenTypes(ScreenBean screenBean) {
        switch (pid) {
            case AppConstant.VIDEO_TYPE_MOVIE:
                mRvType.setVisibility(View.VISIBLE);
                ScreenMovieBean movieBeans = screenBean.getMovieBeans();
                mScreenAreaAdapter.setData(movieBeans.getScreen_area());
                mScreenSortAdapter.setData(movieBeans.getScreen_play());
                mScreenTypeAdapter.setData(movieBeans.getScreen_type());
                mScreenYearAdapter.setData(movieBeans.getScreen_year());
                mScreenAreaAdapter.setSelectedPosition(0);
                mScreenSortAdapter.setSelectedPosition(0);
                mScreenYearAdapter.setSelectedPosition(0);
                mRvArea.setAdapter(mScreenAreaAdapter);
                mRvYear.setAdapter(mScreenYearAdapter);
                mRvSort.setAdapter(mScreenSortAdapter);
                mRvType.setAdapter(mScreenTypeAdapter);
                if (null != tagName) {
                    List<ScreenTypeBean> screen_type = movieBeans.getScreen_type();
                    for (ScreenTypeBean screenTypeBean : screen_type) {
                        if (screenTypeBean.getType_key().equals(tagName)) {
                            int position = screen_type.indexOf(screenTypeBean);
                            mTypeName = screenTypeBean.getType_key();
                            mScreenTypeAdapter.setSelectedPosition(position);
                        }
                    }
                } else {
                    mScreenTypeAdapter.setSelectedPosition(0);
                }
                break;
            case AppConstant.VIDEO_TYPE_TELEPLAY:
                mRvType.setVisibility(View.GONE);
                ScreenTeleplayBean teleplayBeans = screenBean.getTeleplayBeans();
                mScreenAreaAdapter.setData(teleplayBeans.getScreen_area());
                mScreenSortAdapter.setData(teleplayBeans.getScreen_play());
                mScreenYearAdapter.setData(teleplayBeans.getScreen_year());
                mScreenSortAdapter.setSelectedPosition(0);
                mScreenYearAdapter.setSelectedPosition(0);
                mRvArea.setAdapter(mScreenAreaAdapter);
                mRvYear.setAdapter(mScreenYearAdapter);
                mRvSort.setAdapter(mScreenSortAdapter);
                if (null != tagName) {
                    List<ScreenAreaBean> screen_area = teleplayBeans.getScreen_area();
                    for (ScreenAreaBean screenAreaBean : screen_area) {
                        if (screenAreaBean.getArea_key().equals(tagName)) {
                            int position = screen_area.indexOf(screenAreaBean);
                            mAreaName = screenAreaBean.getArea_key();
                            mScreenAreaAdapter.setSelectedPosition(position);
                        }
                    }
                } else {
                    mScreenAreaAdapter.setSelectedPosition(0);
                }

                break;
            case AppConstant.VIDEO_TYPE_CARTOON:
                mRvType.setVisibility(View.GONE);
                ScreenCartoonBean cartoonBeans = screenBean.getCartoonBeans();
                mScreenAreaAdapter.setData(cartoonBeans.getScreen_area());
                mScreenSortAdapter.setData(cartoonBeans.getScreen_play());
                mScreenYearAdapter.setData(cartoonBeans.getScreen_year());
                mScreenSortAdapter.setSelectedPosition(0);
                mScreenYearAdapter.setSelectedPosition(0);
                mRvArea.setAdapter(mScreenAreaAdapter);
                mRvYear.setAdapter(mScreenYearAdapter);
                mRvSort.setAdapter(mScreenSortAdapter);
                if (null != tagName) {
                    List<ScreenAreaBean> screen_area = cartoonBeans.getScreen_area();
                    for (ScreenAreaBean screenAreaBean : screen_area) {
                        if (screenAreaBean.getArea_key().equals(tagName)) {
                            int position = screen_area.indexOf(screenAreaBean);
                            mAreaName = screenAreaBean.getArea_key();
                            mScreenAreaAdapter.setSelectedPosition(position);
                        }
                    }
                } else {
                    mScreenAreaAdapter.setSelectedPosition(0);
                }
                break;
            case AppConstant.VIDEO_TYPE_VARIETY:
                mRvType.setVisibility(View.GONE);
                ScreenVarietyBean varietyBeans = screenBean.getVarietyBeans();
                mScreenAreaAdapter.setData(varietyBeans.getScreen_area());
                mScreenSortAdapter.setData(varietyBeans.getScreen_play());
                mScreenYearAdapter.setData(varietyBeans.getScreen_year());
                mScreenSortAdapter.setSelectedPosition(0);
                mScreenYearAdapter.setSelectedPosition(0);
                mRvArea.setAdapter(mScreenAreaAdapter);
                mRvYear.setAdapter(mScreenYearAdapter);
                mRvSort.setAdapter(mScreenSortAdapter);

                if (null != tagName) {
                    List<ScreenAreaBean> screen_area = varietyBeans.getScreen_area();
                    for (ScreenAreaBean screenAreaBean : screen_area) {
                        if (screenAreaBean.getArea_key().equals(tagName)) {
                            int position = screen_area.indexOf(screenAreaBean);
                            mAreaName = screenAreaBean.getArea_key();
                            mScreenAreaAdapter.setSelectedPosition(position);
                        }
                    }
                } else {
                    mScreenAreaAdapter.setSelectedPosition(0);
                }
                break;
            default:
                break;
        }
        setListener();
        setPromptTitle(mAreaName, mTypeName, mYearName, mSortName);
    }

    private void setListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float ratio = Math.abs((float) verticalOffset / (totalScrollRange + 20));
                mTvScreenContent.setAlpha(ratio);
            }
        });

        mScreenAreaAdapter.setScreenItemListener((areaKey, areaValue) -> {
            mPage = 1;
            this.mArea = areaValue;
            this.mAreaName = areaKey;
            setPromptTitle(mAreaName, mTypeName, mYearName, mSortName);
            mRefreshLayout.autoRefresh();
        });

        mScreenYearAdapter.setScreenItemListener((yearKey, yearValue) -> {
            mPage = 1;
            this.mYear = yearValue;
            this.mYearName = yearKey;
            setPromptTitle(mAreaName, mTypeName, mYearName, mSortName);
            mRefreshLayout.autoRefresh();
        });

        mScreenTypeAdapter.setScreenItemListener((typeKey, typeValue) -> {
            mPage = 1;
            this.mType = typeValue;
            this.mTypeName = typeKey;
            setPromptTitle(mAreaName, mTypeName, mYearName, mSortName);
            mRefreshLayout.autoRefresh();
        });

        mScreenSortAdapter.setScreenItemListener((sortKey, sortValue) -> {
            mPage = 1;
            this.mSort = sortValue;
            this.mSortName = sortKey;
            setPromptTitle(mAreaName, mTypeName, mYearName, mSortName);
            mRefreshLayout.autoRefresh();
        });
    }

    private void setPromptTitle(String areaName, String typeName, String yearName, String sortName) {
        if (null != mTvScreenContent) {
            StringBuilder stringBuilder = new StringBuilder();
            if (null != areaName) {
                stringBuilder.append(areaName);
            } else {
                stringBuilder.append("全部地区");
            }

            // 只有电影栏目有类型,
            if (AppConstant.VIDEO_TYPE_MOVIE == pid) {
                if (null != typeName) {
                    stringBuilder.append(" ");
                    stringBuilder.append(typeName);
                } else {
                    stringBuilder.append(" ");
                    stringBuilder.append("全部类型");
                }
            }

            if (null != yearName) {
                stringBuilder.append(" ");
                stringBuilder.append(yearName);
            } else {
                stringBuilder.append(" ");
                stringBuilder.append("全部年份");
            }

            if (null != sortName) {
                stringBuilder.append(" ");
                stringBuilder.append(sortName);
            } else {
                stringBuilder.append(" ");
                stringBuilder.append("最高评分");
            }

            mTvScreenContent.setText(stringBuilder);
            MobclickAgent.onEvent(TestApp.getContext(), "screen_video_info", stringBuilder.toString());
        }
    }


    @Override
    public void setVideoList(List<VideoBean> videoBeanList) {
        mVideoListAdapter.setData(videoBeanList);
    }

    @Override
    public void moreVideoList(List<VideoBean> videoBeanList) {
        if (videoBeanList.size() < mItemsNum) {
            mVideoListAdapter.addData(videoBeanList);
            mVideoListAdapter.notifyDataSetChanged();
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        } else {
            mVideoListAdapter.addData(videoBeanList);
            mVideoListAdapter.notifyDataSetChanged();
        }
    }


    @OnClick(R.id.ib_back_toolbar)
    public void onViewClicked() {
        finish();
    }


    //加载数据
    private void loadVideoList() {

        mPage = 1;
        if (AppConstant.VIDEO_TYPE_MOVIE == pid) {
            //电影没有地区  tag 传递过来的实际值就是  mType  项
            if (null == mType) {
                this.mType = tag;
            }
            LogUtils.d(TAG, "loadVideoList : " +
                    "videoPid : " + pid + " mArea : " + mArea + " mType : " + mType + " mYear : " + mYear + " mSort : " + mSort);
            mPresenter.getScreenResult(mPage, mItemsNum, pid, mArea, mType, mYear, mSort);

        } else {
            //其他视频没有类型 tag 传递过来的实际值就是 mArea 项
            if (null == mArea) {
                this.mArea = tag;
            }

            LogUtils.d(TAG, "loadVideoList : " +
                    "videoPid : " + pid + " mArea : " + mArea + " mType : " + mType + " mYear : " + mYear + " mSort : " + mSort);
            mPresenter.getScreenResult(mPage, mItemsNum, pid, mArea, mType, mYear, mSort);

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

}
