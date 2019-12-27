package com.video.test.module.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.umeng.analytics.MobclickAgent;
import com.video.test.TestApp;
import com.wang.avi.AVLoadingIndicatorView;
import com.video.test.R;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.SwitchButton;
import com.video.test.utils.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/setting/activity")
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.switch_setting)
    SwitchButton mSwitchBtn;
    @BindView(R.id.tv_clearSearch_setting)
    TextView mTvClearSearch;
    @BindView(R.id.tv_clearCache_setting)
    TextView mTvClearHistory;
    @BindView(R.id.tv_feedback_setting)
    TextView mTvFeedback;
    @BindView(R.id.tv_about_setting)
    TextView mTvAbout;
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.loadingImage_activity_setting)
    AVLoadingIndicatorView mLoadingImage;
    @BindView(R.id.switch_history_setting)
    SwitchButton mSwitchHistoryBtn;
    @BindView(R.id.switch_push_setting)
    SwitchButton mSwitchPushBtn;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_setting;
    }

    @Override
    protected void initData() {
        mPresenter.getSwitchButtonStatus(mSwitchBtn);
        mPresenter.getSwitchHistoryButtonStatus(mSwitchHistoryBtn);
        mPresenter.getSwitchPushButtonStatus(mSwitchPushBtn);
    }

    @Override
    protected void initView() {
        CheckedChangeListener listener = new CheckedChangeListener();
        mSwitchBtn.setOnCheckedChangeListener(listener);
        mSwitchHistoryBtn.setOnCheckedChangeListener(listener);
        mSwitchPushBtn.setOnCheckedChangeListener(listener);
    }

    @Override
    protected void initToolBar() {
        if (null != mIbBack && null != mTvTitle) {
            mTvTitle.setText(R.string.nav_setting);
            mIbBack.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.tv_clearSearch_setting, R.id.tv_clearCache_setting, R.id.tv_feedback_setting, R.id.tv_about_setting, R.id.ib_back_toolbar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_clearSearch_setting:
                LogUtils.d(TAG, "清理搜索结果被点击了");
                requireStoragePerm();
                mPresenter.showCleanHistoryDialog(this);
                break;
            case R.id.tv_clearCache_setting:
                LogUtils.d(TAG, "清理视频缓存被点击了");
                requireStoragePerm();
                mPresenter.showCleanVideoCacheDialog(this);
                break;
            case R.id.tv_feedback_setting:
                LogUtils.d(TAG, "反馈按钮被点击了");
                MobclickAgent.onEvent(TestApp.getContext(), "setting_click_feedback", "用户反馈");
                ARouter.getInstance().build("/feedback/activity").navigation();
                break;
            case R.id.tv_about_setting:
                LogUtils.d(TAG, "关于按钮被点击了");
                ARouter.getInstance().build("/about/activity").navigation();
                break;
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
                break;

        }
    }

    public void sendNetConnectBoardCast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("enableP2PNetwork");
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void clearVideoCacheSuccess() {

    }

    private class CheckedChangeListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            switch (view.getId()) {
                //流量开关
                case R.id.switch_setting:
                    if (isChecked) {
//                        SpUtils.putBoolean(TestApp.getContext(), "netSwitchButton", true);
//                        sendNetConnectBoardCast();
                        showNetworkConfirmDialog();
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), "netSwitchButton", false);
                        sendNetConnectBoardCast();
                    }
                    break;
                //历史纪录
                case R.id.switch_history_setting:
                    if (isChecked) {
                        SpUtils.putBoolean(TestApp.getContext(), "historySwitchButton", true);
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), "historySwitchButton", false);
                    }
                    break;
                case R.id.switch_push_setting:
                    Context context = TestApp.getContext().getApplicationContext();
                    if (isChecked) {
                        JPushInterface.resumePush(context);
                    } else {
                        JPushInterface.stopPush(context);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private void showNetworkConfirmDialog() {
        new MaterialDialog.Builder(this)
                .contentColor(Color.parseColor("#333333"))
                .content("移动数据网络播放可能会导致流量超额，确认开启？")
                .negativeColor(Color.parseColor("#888888"))
                .onNegative((dialog, which) -> {
                    if (mSwitchBtn != null) {
                        mSwitchBtn.setChecked(false);
                    }
                    dialog.dismiss();
                })
                .negativeText("取消")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive(((dialog, which) -> {
                    SpUtils.putBoolean(TestApp.getContext(), "netSwitchButton", true);
                    sendNetConnectBoardCast();
                    dialog.dismiss();
                }))
                .positiveText("确定")
                .build()
                .show();
    }
}
