package com.video.test.module.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.ta.utdid2.device.UTDevice;
import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.IndexPidBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.TabEntityBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.module.beantopic.BeanTopicFragment;
import com.video.test.module.share.ShareFragment;
import com.video.test.module.usercenter.UserCenterFragment;
import com.video.test.module.video.VideoFragment;
import com.video.test.network.BaseException;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.ui.widget.UpdateDialogFragment;
import com.video.test.utils.DeviceHelper;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class HomepagePresenter extends HomepageContract.Presenter<HomepageModel> {
    private static final String TAG = "HomepagePresenter";
    private String mRecommendId;
    private int mRequestTimes = 0;

    @Override
    public void subscribe() {

    }

    /**
     * 初始化 Fragment
     *
     * @return FragmentList
     */
    @Override
    ArrayList<Fragment> initFragmentList() {
        ArrayList<Fragment> fragments = new ArrayList<>();

        Fragment videoFragment = VideoFragment.newInstance("video");
        Fragment columnFragment = BeanTopicFragment.newInstance("column");
        Fragment shareFragment = ShareFragment.newInstance("share");
        Fragment userCenterFragment = UserCenterFragment.newInstance("userCenter");

        fragments.add(videoFragment);
        fragments.add(columnFragment);
        fragments.add(shareFragment);
        fragments.add(userCenterFragment);
        return fragments;
    }

    /**
     * 初始化 底部TabEntities
     *
     * @return TabEntities
     */
    @Override
    ArrayList<CustomTabEntity> initTabEntities() {
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        String[] mTabTitles = TestApp.isOpen() ? new String[]{"首页", "专题", "分享", "我的"} : new String[]{"首页", "分享", "我的"};
        int[] mTabSelectRes = TestApp.isOpen() ? new int[]{R.drawable.ic_tool_home, R.drawable.ic_tool_spec, R.drawable.ic_tool_sha, R.drawable.ic_tool_m}
                : new int[]{R.drawable.ic_tool_home, R.drawable.ic_tool_sha, R.drawable.ic_tool_m};
        int[] mTabNoSelectRes = TestApp.isOpen() ? new int[]{R.drawable.ic_tool_home2, R.drawable.ic_tool_spec2, R.drawable.ic_tool_sha2, R.drawable.ic_tool_m2}
                : new int[]{R.drawable.ic_tool_home2, R.drawable.ic_tool_sha2, R.drawable.ic_tool_m2};
        for (int i = 0; i < mTabTitles.length; i++) {
            mTabEntities.add(new TabEntityBean(mTabTitles[i], mTabSelectRes[i], mTabNoSelectRes[i]));
        }
        return mTabEntities;
    }

    @Override
    void login() {
//        String androidID = DeviceUtils.getAndroidID();
        String androidID = DeviceHelper.getDeviceId(TestApp.getContext());
        String utdid = UTDevice.getUtdid(TestApp.getContext());
        mRecommendId = SpUtils.getString(TestApp.getContext(), AppConstant.SP_RECOMMEND_ID_KEY, "");
        Log.d(TAG, "login with share recommendId,recommendId = " + mRecommendId);
        Disposable disposable = mModel.login(utdid, androidID, mRecommendId)
                .subscribe(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        LogUtils.i(TAG, "login success");
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN, loginBean.getToken());
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, loginBean.getToken_id());
                        SpUtils.putBoolean(TestApp.getContext(), AppConstant.USER_IS_LOGIN, true);
                        SpUtils.putString(TestApp.getContext(), AppConstant.SERVER_VERSION_CODE, loginBean.getServerCode());
                        //只有登录成功了 获取了正常的 Token 和 Token ID 才会请求一次用户信息
                        getUserInfo(loginBean.getToken(), loginBean.getToken_id());
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof BaseException) {
                            int errorCode = ((BaseException) throwable).getErrorCode();
                            // 301 Token 失效的时候,重新申请一次 Token
                            if (AppConstant.REQUEST_INVALID_TOKEN == errorCode && mRequestTimes < AppConstant.REQUEST_LOGIN_MAXTIME) {
                                login();
                            }
                        }
                        LogUtils.e(TAG, "login Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);

    }


    @Override
    void getUserInfo(String userToken, String userTokenId) {

        Disposable disposable = mModel.getUserInfo(userToken, userTokenId)
                .subscribe(new Consumer<UserCenterBean>() {
                    @Override
                    public void accept(UserCenterBean userCenterBean) {
                        mView.setUserInfo(userCenterBean);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getUserInfo Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void getPidIndex() {
        Disposable disposable = mModel.getPidIndex()
                .subscribe(new Consumer<List<IndexPidBean>>() {
                    @Override
                    public void accept(List<IndexPidBean> indexPidBeans) {
                        mView.setPidIndex(indexPidBeans);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getPidIndex Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void getVersionInfo() {
        Disposable disposable = mModel.getVersionInfo()
                .subscribe(new Consumer<VersionInfoBean.InfoBean>() {
                    @Override
                    public void accept(VersionInfoBean.InfoBean infoBean) {
                        LogUtils.d(TAG, "getVersionInfo success == " + infoBean.toString());
                        mView.setVersionInfo(infoBean);

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getVersionInfo Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void getHomeDialogData() {
        Disposable disposable = mModel.getHomeDialogData()
                .subscribe(list -> {
                    LogUtils.d(TAG, "getHomeDialogData success");
                    if (TestApp.isOpen()) {
                        mView.setHomeDialogData(list);
                    }
                }, new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, "getHomeDialogData error == " + throwable.getMessage())));
        addDisposable(disposable);
    }

    @Override
    void getHotWords() {
        Disposable disposable = mModel.getHotWords()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isInsert) {
                        LogUtils.d(TAG, "getHomeDialogData isInsert = " + isInsert);

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getHotWords error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }


    @Override
    void getLatestVersion(FragmentActivity activity, VersionInfoBean.InfoBean infoBean) {
        LogUtils.i("UpdateDialog", "getLatestVersion");
        String versionTitle = activity.getString(R.string.dialog_check_newVersion, infoBean.getVersions_name());
        String versionSize = activity.getString(R.string.dialog_version_size, infoBean.getSize());
        String downloadUrl = infoBean.getDownload();
        String versionContent = infoBean.getUpdate_details();
        boolean isForce = infoBean.isIs_update();

        Bundle bundle = new Bundle();
        bundle.putString("versionTitle", versionTitle);
        bundle.putString("versionSize", versionSize);
        bundle.putString("versionInfo", versionContent);
        bundle.putString("downloadUrl", downloadUrl);
        bundle.putBoolean("versionIsForce", isForce);
        UpdateDialogFragment fragment = UpdateDialogFragment.newInstance(bundle);
        fragment.setCancelable(!isForce);
        fragment.show(activity.getSupportFragmentManager(), "updateDialog");
    }


    @Override
    void initM3U8DownloadConfig() {
        int connTimeout = M3U8DownloaderConfig.getConnTimeout();
        int readTimeout = M3U8DownloaderConfig.getReadTimeout();
        String saveDir = M3U8DownloaderConfig.getSaveDir();
        int threadCount = M3U8DownloaderConfig.getThreadCount();
        LogUtils.d(TAG, "initM3U8DownloadConfig conntimeout : "
                + connTimeout + " readTimeout : " + readTimeout + " threadCount : " + threadCount + " saveDir : " + saveDir);
    }


    @Override
    void shareGetInfo(Intent intent) {
        LogUtils.d(TAG, "shareGetInfo");
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                //获取渠道数据
                String channelCode = appData.getChannel();
                LogUtils.d("OpenInstall", "getInstall : channelCode = " + channelCode);
                //获取自定义数据
                String bindData = appData.getData();
                LogUtils.d("OpenInstall", "getInstall : bindData = " + bindData);
            }
        });
    }

    @Override
    void initTimeClose() {
        SpUtils.removeInt(TestApp.getContext(), "closeTimeType");
        SpUtils.removeInt(TestApp.getContext(), "closeTimeValue");
    }
}
