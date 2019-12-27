package com.video.test.module.swap;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.video.test.R;
import com.video.test.javabean.TabEntityBean;
import com.video.test.ui.base.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Reus
 * 兑换中心页面
 */
@Route(path = "/swap/activity")
public class SwapActivity extends BaseActivity<SwapPresenter> implements SwapContract.View {

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_friend_count)
    TextView mTvCount;
    @BindView(R.id.tabLayout)
    CommonTabLayout mTabLayout;

    private Fragment mFragmentSwapList;
    private Fragment mFragmentSwapHistory;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_swap;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ArrayList<CustomTabEntity> tabEntities = initTabEntities();
        mTabLayout.setTabData(tabEntities);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switchTo(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
    }

    private ArrayList<CustomTabEntity> initTabEntities() {
        ArrayList<CustomTabEntity> list = new ArrayList<>();
        list.add(new TabEntityBean("兑换会员", 0, 0));
        list.add(new TabEntityBean("邀请兑换明细", 0, 0));
        return list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments(savedInstanceState);
    }

    private void initFragments(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (null != savedInstanceState) {
            mFragmentSwapList = getSupportFragmentManager().findFragmentByTag("swapList");
            mFragmentSwapHistory = getSupportFragmentManager().findFragmentByTag("swapHistory");
        } else {

            mFragmentSwapList = new SwapListFragment();
            mFragmentSwapHistory = new SwapHistoryFragment();

            transaction.add(R.id.container, mFragmentSwapList, "swapList");
            transaction.add(R.id.container, mFragmentSwapHistory, "swapHistory");
        }

        transaction.commit();
        switchTo(currentTabPosition);
        mTabLayout.setCurrentTab(currentTabPosition);
    }

    private void switchTo(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                transaction.show(mFragmentSwapList);
                transaction.hide(mFragmentSwapHistory);
                transaction.commitAllowingStateLoss();
                break;
            case 1:
                transaction.hide(mFragmentSwapList);
                transaction.show(mFragmentSwapHistory);
                transaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        mTvTitle.setText(getString(R.string.activity_share_swap));
        mIbBack.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ib_back_toolbar})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void setShareNum(int shareNum) {
        SpannableString num = new SpannableString(getString(R.string.activity_swap_count_title, shareNum));
        ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.swap_text_friend_count));
        num.setSpan(span, 0, num.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCount.setText(num);
    }
}
