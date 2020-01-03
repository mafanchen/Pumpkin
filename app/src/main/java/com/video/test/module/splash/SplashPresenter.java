package com.video.test.module.splash;

import android.content.Intent;
import android.text.TextUtils;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.TestApp;
import com.video.test.javabean.SplashBean;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SplashPresenter extends SplashContract.Presenter<SplashModel> {
    private static final String TAG = "SplashPresenter";
    private IWXAPI mWxApi;
    private SplashBean splashBean;

    @Override
    public void subscribe() {

    }

    @Override
    void registerWeChat() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        mWxApi.registerApp(BuildConfig.WECHAT_APP_ID);
    }

    @Override
    void getSplashBean() {
        Disposable disposable = mModel.getSplashInfo()
                .subscribe(splashBean -> {
                    LogUtils.i(TAG, "getSplashBean == success");
                    this.splashBean = splashBean;
                    TestApp.setOpen(!(TextUtils.equals(splashBean.getOpen_status(), "2") && BuildConfig.FLAVOR == "TestStore"));
                }, throwable -> LogUtils.i(TAG, "getSplashBean == error : " + throwable.getMessage()));
        addDisposable(disposable);
    }

    @Override
    void countDownSplash() {
        Disposable subscribe = Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(aLong -> {
                    //先读取保存的广告数据
                    SplashBean saveSplashBean = SpUtils.getSerializable(TestApp.getContext(), "splashBean");
                    if (splashBean != null) {
                        //如果获取到网络数据，则保存
                        SpUtils.putSerializable(TestApp.getContext(), "splashBean", splashBean);
                    }
                    //如果有缓存，则以缓存的数据为主
                    if (saveSplashBean != null) {
                        splashBean = saveSplashBean;
                    }
                    //判断是否有广告，有广告，跳转到广告页面，没广告，跳转到主页
                    if (splashBean == null) {
                        mView.skipSplashActivity();
                    } else if (!TestApp.isOpen()) {
                        mView.skipSplashActivity();
                    } else if (TextUtils.isEmpty(splashBean.getPic_url()) || TextUtils.isEmpty(splashBean.getJump_url())) {
                        mView.skipSplashActivity();
                    } else {
                        mView.jumpToAdPage(splashBean.getAd_name(), splashBean.getJump_url(), splashBean.getPic_url(), splashBean.getId(),splashBean.getShow_time());
                    }
                });
        addDisposable(subscribe);
    }

    @Override
    void shareGetInfo(Intent intent) {
        LogUtils.d(TAG, "shareGetInfo");

//        OpenInstall.getInstall(new AppInstallAdapter() {
//            @Override
//            public void onInstall(AppData appData) {
//                onGetInstallData(appData);
//            }
//        });

        OpenInstall.getWakeUp(intent, new AppWakeUpAdapter() {
            @Override
            public void onWakeUp(AppData appData) {
                onGetInstallData(appData);
            }
        });
    }

    private void onGetInstallData(AppData appData) {
        //获取渠道数据
        String channelCode = appData.getChannel();
        LogUtils.d(TAG, "OpenInstall : channelCode = " + channelCode);
        //获取自定义数据
        String bindData = appData.getData();
        LogUtils.d(TAG, "OpenInstall : bindData = " + bindData);
        String recommendId;
        try {
            JSONObject jsonObject = new JSONObject(bindData);
            recommendId = jsonObject.getString("recommend");
            LogUtils.i(TAG, "OpenInstall recommendId == " + recommendId);

        } catch (JSONException e) {
            recommendId = "";
            LogUtils.e(TAG, "OpenInstall Error : " + e.getMessage());
        }
        if (!TextUtils.isEmpty(recommendId)) {
            SpUtils.putString(TestApp.getContext(), AppConstant.SP_RECOMMEND_ID_KEY, recommendId);
        }
    }
}
