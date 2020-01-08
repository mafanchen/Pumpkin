package com.video.test.module.setting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.SwitchButton;
import com.video.test.utils.LogUtils;
import com.video.test.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/setting/activity")
public class SettingActivity extends BaseActivity<SettingPresenter> implements SettingContract.View {
    private static final String TAG = "SettingActivity";

    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.switch_mobileNet_play_setting)
    SwitchButton mSwitchMobilePlay;
    @BindView(R.id.switch_history_setting)
    SwitchButton mSwitchHistoryBtn;
    @BindView(R.id.switch_push_setting)
    SwitchButton mSwitchPushBtn;
    @BindView(R.id.switch_autoPlayer_setting)
    SwitchButton mSwitchAutoPlay;
    @BindView(R.id.switch_mobileNet_down_setting)
    SwitchButton mSwitchMobileDown;
    @BindView(R.id.tv_cache_space_setting)
    TextView mTvCacheSpace;
    private MaterialDialog mProgressDialog;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_setting;
    }

    @Override
    protected void initData() {
        mPresenter.getSwitchMobilePlayStatus(mSwitchMobilePlay);
        mPresenter.getSwitchHomepageHistoryStatus(mSwitchHistoryBtn);
        mPresenter.getSwitchPushNoticeStatus(mSwitchPushBtn);
        mPresenter.getSwitchMobileDownStatus(mSwitchMobileDown);
        mPresenter.getSwitchAutoPlayStatus(mSwitchAutoPlay);
        mPresenter.getCacheSize();
    }

    @Override
    protected void initView() {
        CheckedChangeListener listener = new CheckedChangeListener();
        mSwitchMobilePlay.setOnCheckedChangeListener(listener);
        mSwitchHistoryBtn.setOnCheckedChangeListener(listener);
        mSwitchPushBtn.setOnCheckedChangeListener(listener);
        mSwitchAutoPlay.setOnCheckedChangeListener(listener);
        mSwitchMobileDown.setOnCheckedChangeListener(listener);
    }


    @Override
    protected void initToolBar() {
        if (null != mIbBack && null != mTvTitle) {
            mTvTitle.setText(R.string.nav_setting);
            mIbBack.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.tv_clearSearch_setting, R.id.tv_clearCache_setting, R.id.tv_clearLocal_setting, R.id.ib_back_toolbar})
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
            case R.id.tv_clearLocal_setting:
                LogUtils.d(TAG, "清空本地缓存被点击了");
                requireStoragePerm();
                mPresenter.removeLocalCache();
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

    @Override
    public void showLoadingDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .content("正在清理缓存")
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setCacheSize(String size) {
        mTvCacheSpace.setText(size);
    }


    private class CheckedChangeListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            switch (view.getId()) {
                case R.id.switch_mobileNet_play_setting:  //流量播放
                    if (isChecked) {
                        showNetworkConfirmDialog();
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_PLAY, false);
                        sendNetConnectBoardCast();
                    }
                    break;
                case R.id.switch_history_setting:   //首页播放历史提醒
                    if (isChecked) {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_HOMEPAGE_HISTORY, true);
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_HOMEPAGE_HISTORY, false);
                    }
                    break;
                case R.id.switch_push_setting:              //推送
                    Context context = TestApp.getContext().getApplicationContext();
                    if (isChecked) {
                        JPushInterface.resumePush(context);
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_PUSH_NOTICE, false);
                    } else {
                        JPushInterface.stopPush(context);
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_PUSH_NOTICE, false);
                    }
                    break;
                case R.id.switch_autoPlayer_setting:       // 自动播放下一集
                    if (isChecked) {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_AUTO_PLAY, true);
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_AUTO_PLAY, false);
                    }
                    break;
                case R.id.switch_mobileNet_down_setting:    //移动流量下载
                    if (isChecked) {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_DOWN, true);
                    } else {
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_DOWN, false);
                        //如果不是wifi网络，则停止所有下载任务
                        if (!NetworkUtils.isWifiConnected(SettingActivity.this)) {
                            mPresenter.stopAllDownloadTask();
                        }
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
                    if (mSwitchMobilePlay != null) {
                        mSwitchMobilePlay.setChecked(false);
                    }
                    dialog.dismiss();
                })
                .negativeText("取消")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive(((dialog, which) -> {
                    SpUtils.putBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_PLAY, true);
                    sendNetConnectBoardCast();
                    dialog.dismiss();
                }))
                .positiveText("确定")
                .build()
                .show();
    }
}
