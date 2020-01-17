package com.video.test.module.homepage;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.HomeDialogBean;
import com.video.test.javabean.IndexPidBean;
import com.video.test.javabean.ShareJoinEventBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.javabean.base.IPageJumpBean;
import com.video.test.module.beantopic.BeanTopicFragment;
import com.video.test.module.share.ShareFragment;
import com.video.test.module.simplevideo.SimpleVideoFragment;
import com.video.test.module.usercenter.UserCenterFragment;
import com.video.test.module.video.VideoFragment;
import com.video.test.receiver.DownloadReceiver;
import com.video.test.receiver.NetConnectReceiver;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.BaseHomeDialogFragment;
import com.video.test.ui.widget.HomeDialogFragment;
import com.video.test.ui.widget.HomeNoticeDialogFragment;
import com.video.test.utils.AppInfoUtils;
import com.video.test.utils.EncryptUtils;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Enoch Created on 2018/6/25.
 */
@Route(path = "/homepage/activity")
public class HomepageActivity extends BaseActivity<HomepagePresenter> implements HomepageContract.View {
    private static final String TAG = "HomepageActivity";
    @BindView(R.id.CommonTabLayout)
    CommonTabLayout mTabLayout;
    /**
     * 退出倒计时
     */
    private long mExitTime = 0;
    private NetConnectReceiver mNetConnectReceiver;
    private MaterialDialog mProgressDialog;
    private Fragment mVideoFragment;
    private Fragment mColumnFragment;
    private Fragment mShareFragment;
    private Fragment mUserCenterFragment;
    private DownloadReceiver mDownloadReceiver;
    private List<Fragment> fragments;


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_homepage;
    }

    @Override
    protected void initData() {
        mPresenter.initTimeClose();
        // 获取推荐信息
        mPresenter.shareGetInfo(getIntent());
        // 获取列表数据  暂不使用
        mPresenter.getPidIndex();
        // 初始化广播
        initNetReceiver();
        // 移动流量模式未开启流量开关,进行提醒
        isOpenMobileSwitch();
        //  TODO  检测新版本信息 如果检测过了  GooglePlay 版本 跳过
        mPresenter.getVersionInfo();
        //  显示活动Dialog
        mPresenter.getHomeDialogData();
        // 获取热搜关键词
        mPresenter.getHotWords();
        // 登陆
        mPresenter.login();
        //初始化 M3U8 下载器
        mPresenter.initM3U8DownloadConfig();
        //初始化广告点击下载的广播接收器
        initAdDownloadReceiver();

        EventBus.getDefault().register(this);

        initVideoInfos(getIntent());

        TestApp.getInstance().registerActivityLifecycle();
    }

    /**
     * 初始化广告点击下载的广播接收器
     */
    private void initAdDownloadReceiver() {
        mDownloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mDownloadReceiver, filter);
    }


    /**
     * 初始化广播接收服务 用来检测网络变化的情况
     */
    private void initNetReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction("enableP2PNetwork");
        mNetConnectReceiver = new NetConnectReceiver();
        registerReceiver(mNetConnectReceiver, intentFilter);
        LogUtils.i(getClass(), "initNetReceiver : 已注册");

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments(savedInstanceState);
        if (illegalKey()) {
            showIllegalKeyDialog();
        }
    }

    @Override
    protected void initView() {
        ArrayList<CustomTabEntity> tabEntities = mPresenter.initTabEntities();
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

    private void initFragments(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (null != savedInstanceState) {
            mVideoFragment = getSupportFragmentManager().findFragmentByTag("video");
            mColumnFragment = getSupportFragmentManager().findFragmentByTag("column");
            mShareFragment = getSupportFragmentManager().findFragmentByTag("share");
            mUserCenterFragment = getSupportFragmentManager().findFragmentByTag("userCenter");
            fragments = new ArrayList<>();
            fragments.add(mVideoFragment);
            if (TestApp.isOpen()) {
                fragments.add(mColumnFragment);
            }
            fragments.add(mShareFragment);
            fragments.add(mUserCenterFragment);
        } else {
            mVideoFragment = TestApp.isOpen() ? VideoFragment.newInstance("video") : new SimpleVideoFragment();
            mColumnFragment = BeanTopicFragment.newInstance("column");
            mShareFragment = ShareFragment.newInstance("share");
            mUserCenterFragment = UserCenterFragment.newInstance("userCenter");
            fragments = new ArrayList<>();
            fragments.add(mVideoFragment);
            transaction.add(R.id.fl_homepage_activity, mVideoFragment, "video");
            if (TestApp.isOpen()) {
                fragments.add(mColumnFragment);
                transaction.add(R.id.fl_homepage_activity, mColumnFragment, "column");
            }
            fragments.add(mShareFragment);
            transaction.add(R.id.fl_homepage_activity, mShareFragment, "share");
            fragments.add(mUserCenterFragment);
            transaction.add(R.id.fl_homepage_activity, mUserCenterFragment, "userCenter");
        }

        transaction.commit();
        switchTo(currentTabPosition);
        mTabLayout.setCurrentTab(currentTabPosition);
    }


    private void switchTo(int position) {
        LogUtils.d(TAG, " 主页 position : " + position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (i == position) {
                    transaction.show(fragment);
                } else {
                    transaction.hide(fragment);
                }
            }
            transaction.commitAllowingStateLoss();
        }
//        switch (position) {
//            //首页
//            case 0:
//                transaction.show(mVideoFragment);
//                transaction.hide(mColumnFragment);
//                transaction.hide(mShareFragment);
//                transaction.hide(mUserCenterFragment);
//                transaction.commitAllowingStateLoss();
//                break;
//            //专题
//            case 1:
//                transaction.hide(mVideoFragment);
//                transaction.show(mColumnFragment);
//                transaction.hide(mShareFragment);
//                transaction.hide(mUserCenterFragment);
//                transaction.commitAllowingStateLoss();
//                break;
//            //分享
//            case 2:
//                transaction.hide(mVideoFragment);
//                transaction.hide(mColumnFragment);
//                transaction.show(mShareFragment);
//                transaction.hide(mUserCenterFragment);
//                transaction.commitAllowingStateLoss();
//                break;
//            //我的
//            case 3:
//                transaction.hide(mVideoFragment);
//                transaction.hide(mColumnFragment);
//                transaction.hide(mShareFragment);
//                transaction.show(mUserCenterFragment);
//                transaction.commitAllowingStateLoss();
//                break;
//            default:
//                break;
//        }

    }


    @Override
    protected void setAdapter() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy");
        unregisterReceiver(mNetConnectReceiver);
        unregisterReceiver(mDownloadReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showToast("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .content("请稍后...")
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideProgressDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setHomeDialogData(List<HomeDialogBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<BaseHomeDialogFragment> dialogList = new ArrayList<>();
        for (HomeDialogBean dialogBean : list) {
            BaseHomeDialogFragment fragment = null;
            if (dialogBean.getActivityType() == HomeDialogBean.ACTIVITY_TYPE_IMAGE) {
                fragment = HomeDialogFragment.newInstance(dialogBean);
            } else if (dialogBean.getActivityType() == HomeDialogBean.ACTIVITY_TYPE_TEXT) {
                fragment = HomeNoticeDialogFragment.Companion.newInstance(dialogBean);
            }
            if (fragment == null) {
                continue;
            }
            dialogList.add(fragment);
            fragment.setDialogItemClickListener(new BaseHomeDialogFragment.DialogItemClickListener() {
                @Override
                public void onClick(@NotNull HomeDialogBean bean, @NotNull DialogFragment dialog) {
                    String type = bean.getType();
                    if (TextUtils.isEmpty(type) || type.equals(AppConstant.TYPE_CLOSE_NOTICE)) {
                        int index = dialogList.indexOf(dialog);
                        dialog.dismiss();
                        if (index == -1 || index >= dialogList.size() - 1) {
                            return;
                        }
                        index++;
                        dialogList.get(index).show(getSupportFragmentManager(), "dialog" + index);
                        return;
                    }
                    onActivityClick(bean);
                }
            });
            fragment.setDialogDismissListener(fragment1 -> {
                int index = dialogList.indexOf(fragment1);
                if (index == -1 || index >= dialogList.size() - 1) {
                    return;
                }
                index++;
                dialogList.get(index).show(getSupportFragmentManager(), "dialog" + index);
            });
        }
        if (!dialogList.isEmpty()) {
            dialogList.get(0).show(getSupportFragmentManager(), "dialog0");
        }
    }

    /**
     * 点击活动
     */
    private void onActivityClick(IPageJumpBean jumpBean) {

        switch (jumpBean.getType()) {
            case AppConstant.BANNER_TYPE_VIDEO:
                LogUtils.d(TAG, "BANNER_TYPE_VIDEO");
                ARouter.getInstance().build("/player/activity").withString("vodId", jumpBean.getVodId()).navigation();
                break;
            case AppConstant.BANNER_TYPE_ROUTER:
                LogUtils.d(TAG, "BANNER_TYPE_ROUTER");
                String path = jumpBean.getAndroidRouter();
                if (TextUtils.isEmpty(path)) {
                    break;
                }
                ARouter.getInstance().build(path).navigation();
                break;
            case AppConstant.BANNER_TYPE_WEBURL:
                LogUtils.d(TAG, "BANNER_TYPE_WEBURL = " + jumpBean.getWebUrl());
                startActivity(IntentUtils.getBrowserIntent(jumpBean.getWebUrl()));
                MobclickAgent.onEvent(TestApp.getContext(), "click_ads_banner", jumpBean.getTargetName());
                break;
            case AppConstant.BANNER_TYPE_TOPIC:
                LogUtils.d(TAG, "BANNER_TYPE_Topic");
                int pid = jumpBean.getTopicRouter().getZt_pid();
                String tag = jumpBean.getTopicRouter().getZt_tag();
                String type = jumpBean.getTopicRouter().getZt_type();

                ARouter.getInstance().build("/topicVideoList/activity")
                        .withInt("pid", pid)
                        .withString("tag", tag)
                        .withString("type", type)
                        .navigation();
                break;
            default:
                break;
        }
    }

    @Override
    public void setUserInfo(UserCenterBean userCenterBean) {
        SpUtils.putSerializable(TestApp.getContext(), AppConstant.USER_INFO, userCenterBean);
        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, userCenterBean.getIs_vip());
        SpUtils.putString(TestApp.getContext(), AppConstant.USER_SHARE_URL, userCenterBean.getShareUrl());
    }


    @Override
    public void setPidIndex(List<IndexPidBean> indexPidBeans) {
        //TODO 暂未使用

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(TAG, "onNewIntent");
        initVideoInfos(intent);
        mPresenter.shareGetInfo(intent);
    }

    @Override
    public void setVersionInfo(VersionInfoBean.InfoBean infoBean) {

        int appVersion = TestApp.getAppVersion();

        //TODO  默认为 appVersion < Integer.valueOf(infoBean.getVersions()

        if (appVersion < Integer.valueOf(infoBean.getVersions()) && TestApp.isOpen()) {
            LogUtils.d(TAG, "setVersionInfo 有新版本");
            mPresenter.getLatestVersion(this, infoBean);
        } else {
            LogUtils.d(TAG, "setVersionInfo 没有新版本");
        }
        //判断完版本之后 请求相应的权限
        requirePhonePerm();
    }


    /**
     * 用来判断移动网络模式下 是否已经开启了流量开关
     */
    private void isOpenMobileSwitch() {
        int networkStatus = SpUtils.getInt(TestApp.getContext(), "networkStatus", 0);
        if (networkStatus == AppConstant.MOBILE_NETWORK_CAN_NOT_USE) {
            new MaterialDialog.Builder(this)
                    .content(R.string.dialog_mobileNetwork_switchButton)
                    .positiveText(R.string.dialog_setMobileSwitch)
                    .negativeText(R.string.dialog_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            ARouter.getInstance().build("/setting/activity").navigation();
                        }
                    }).show();
        }
    }

    private void initVideoInfos(Intent intent) {
        LogUtils.d(TAG, "Intent data : " + intent.toString());
        if (null != intent.getData()) {
            LogUtils.d(TAG, "Intent data H5页面传递参数");
            String vodId = intent.getData().getQueryParameter("vodId");
            LogUtils.d(TAG, "Intent data  query : " + vodId);
            //如果是h5传参数，直接跳转至播放页播放
            ARouter.getInstance().build("/player/activity").withString("vodId", vodId).navigation();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandleShareJoinEvent(ShareJoinEventBean bean) {
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment instanceof ShareFragment) {
                    mTabLayout.setCurrentTab(i);
                    switchTo(i);
                    return;
                }
            }
        }
    }


    private boolean illegalKey() {
        String sha1Native = EncryptUtils.getSHA1FromJNI();
        String sha1Now = AppInfoUtils.getSignInfo(this, getPackageName(), AppInfoUtils.SHA1);
        Log.d(TAG, "sha1Now = " + sha1Now);
        return !TextUtils.equals(sha1Native, sha1Now);
    }

    private void showIllegalKeyDialog() {
        new MaterialDialog.Builder(this)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content("您的安装包异常，请前往官网下载最新安装包。")
                .contentColor(Color.parseColor("#333333"))
                .positiveColor(Color.parseColor("#ffad43"))
                .positiveText("确定")
                .onPositive((dialog, which) -> {
                    startActivity(IntentUtils.getBrowserIntent(BuildConfig.OFFICAL_WEBSITE));
                    finish();
                })
                .negativeText("取消")
                .onNegative(((dialog, which) -> System.exit(0)))
                .build()
                .show();
    }
}
