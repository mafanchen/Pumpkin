package com.video.test.module.profilepic;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.video.test.R;
import com.video.test.javabean.ProfilePicButtonBean;
import com.video.test.javabean.ProfilePictureBean;
import com.video.test.javabean.event.ProfilePicEvent;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.RefreshHeader;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

@Route(path = "/profilePic/activity")
public class ProfilePicActivity extends BaseActivity<ProfilePicPresenter> implements ProfilePicContarct.View {
    private static final String TAG = "ProfilePicActivity";

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.rv_profilePic_activity)
    RecyclerView mRvContainer;
    @BindView(R.id.layout_refresh)
    SmartRefreshLayout mRefreshLayout;
    @Autowired(name = "picUrl")
    String picUrl;
    String mPicId;
    ProfilePicButtonClickListener commitListener = new ProfilePicButtonClickListener() {
        @Override
        public void onClick() {
            mPresenter.updateProfilePic(mPicId);
            LogUtils.d(TAG, "click profile commit ");
        }
    };
    private MultiTypeAdapter mAdapter;
    ProfilePicSelectedListener selectedListener = new ProfilePicSelectedListener() {
        @Override
        public void picSelected(String picId, String picUrl) {
            mPicId = picId;
            mAdapter.notifyDataSetChanged();
            LogUtils.d(TAG, "selected picID : " + picId + " picUrl : " + picUrl);

        }
    };
    private Items mitems;

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_profile_picture;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initToolBar() {
        if (null != mTvTitle) {
            mTvTitle.setText(R.string.profile_title);
        }
        if (null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setAdapter() {
        super.setAdapter();
        initAdapter();
        int leftRight = PixelUtils.dp2px(this, 1);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                List<?> items = mAdapter.getItems();
                Object iterm = items.get(position);
                if (iterm instanceof ProfilePictureBean) {
                    return 1;
                } else {
                    return 4;
                }
            }
        });

        mRvContainer.setLayoutManager(gridLayoutManager);
        mRvContainer.addItemDecoration(new DividerItemDecoration(leftRight, 0, Color.WHITE));
        mRvContainer.setAdapter(mAdapter);
        initRefreshLayout();
    }

    private void initAdapter() {
        if (null == mAdapter) {
            mAdapter = new MultiTypeAdapter();
            mAdapter.register(ProfilePictureBean.class, new ProfilePicViewBinder(picUrl, selectedListener));
            mAdapter.register(ProfilePicButtonBean.class, new ProfilePicButtonViewBinder(commitListener));

        }
    }

    private void initRefreshLayout() {
        mRefreshLayout.setRefreshHeader(new RefreshHeader(this));
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getProfilePics();
            }
        });
        mRefreshLayout.autoRefresh();
    }

    @Override
    public void getProfilePics(List<ProfilePictureBean> pics) {
        if (null != mAdapter) {
            mitems = new Items();
            mitems.addAll(pics);
            addFooter(mitems);
            mAdapter.setItems(mitems);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void updateProfileSuccess(String message) {
        ToastUtils.showToast(this, "设置头像成功");
        EventBus.getDefault().post(new ProfilePicEvent(message));
        onBackPressed();
    }

    @Override
    public void updateProfileFailed(String message) {
        ToastUtils.showToast(this, "设置头像失败");
        onBackPressed();
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

    public void cancelRefreshLayout() {
        if (null != mRefreshLayout) {
            mRefreshLayout.setEnableRefresh(false);
        }
    }

    @OnClick({R.id.ib_back_toolbar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back_toolbar:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    void addFooter(Items items) {
        int size = items.size();
        if (0 != size && !(items.get(size - 1) instanceof ProfilePicButtonBean)) {
            items.add(size, new ProfilePicButtonBean());
        } else {
            LogUtils.d(TAG, "addFooter mitems is empty or have Footer");
        }
    }
}
