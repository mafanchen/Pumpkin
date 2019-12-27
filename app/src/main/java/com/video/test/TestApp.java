package com.video.test;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkSetting;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.video.test.sp.SpUtils;
import com.video.test.utils.AppInfoUtils;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.EncryptUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig;
import zlc.season.rxdownload3.core.DownloadConfig;
import zlc.season.rxdownload3.extension.ApkInstallExtension;
import zlc.season.rxdownload3.extension.ApkOpenExtension;

/**
 * @author Enoch Created on 2018/6/25.
 */
public class TestApp extends MultiDexApplication {
    private static final String TAG = "TestApp";
    private static Context sContext;
    private static TestApp sApp;
    private static boolean isOpen = true;

    @Override
    public void onCreate() {
        super.onCreate();
//        MultiDex.install(this);
        //防二次打包
        if (illegalKey()) {
            ToastUtils.showToast(this, "您的安装包异常，请前往官网下载最新安装包");
            new Handler().postDelayed(() -> System.exit(0), 5000);
        }

        LogUtils.d(TAG, "onCreate");
        sApp = this;

        sContext = getApplicationContext();

        String processName = getProcessName(Process.myPid());
        //确保只有一个进程初始化东西
        if (getPackageName().equals(processName)) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
            initJPush(this);
            initARouter();
            initZXing();
            initOpenInstall();
            initRxDownload();
            initStrictMode();
            initLeCast();
            initM3U8();
        }
    }

    private void initM3U8() {
        M3U8DownloaderConfig config = M3U8DownloaderConfig.build(this);
        config.setSaveDir(DownloadUtil.getDownloadDirPath())
                .setConnTimeout(10 * 1000)
                .setReadTimeout(30 * 60 * 1000)
                .setThreadCount(3);
    }

    private void initJPush(Context context) {
        if (BuildConfig.DEBUG) {
            JPushInterface.setDebugMode(true);
        }
        JPushInterface.init(context);
    }

    private void initLeCast() {
        LelinkSetting lelinkSetting = new LelinkSetting.LelinkSettingBuilder(BuildConfig.LECAST_APP_ID, BuildConfig.LECAST_APP_SECRET).build();
        ILelinkServiceManager lelinkServiceManager = LelinkServiceManager.getInstance(sContext);
        lelinkServiceManager.setLelinkSetting(lelinkSetting);

    }

    private void initOpenInstall() {
        LogUtils.d(TAG, "initOpenInstall");
        if (isMainProcess()) {
            OpenInstall.init(sContext);
            OpenInstall.getInstall(new AppInstallAdapter() {
                @Override
                public void onInstall(AppData appData) {
                    onGetInstallData(appData);
                }
            });
        }
    }

    private void onGetInstallData(AppData appData) {
        //获取渠道数据
        String channelCode = appData.getChannel();
        if (TextUtils.isEmpty(channelCode)) {
            channelCode = "Official";
        }
        LogUtils.d(TAG, "OpenInstall : channelCode = " + channelCode);
        //初始化友盟
        initUmeng(channelCode);
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

    public static TestApp getInstance() {
        return sApp;
    }

    public static Context getContext() {
        return sContext;
    }


    private void initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();

        }
        ARouter.init(this);
    }

    private void initZXing() {
        ZXingLibrary.initDisplayOpinion(this);
    }

    /**
     * 获取 DaoSession
     *
     * @return
     */


    /*获取当前软件的版本号码*/
    public static int getAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /*获取当前软件的版本名称*/
    public static String getAppVersionName() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*解决字体使用sp时,高度会随系统变化的情况*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        /*如果是非默认值*/
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        /*如果是非默认值*/
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            /*设置默认*/
            newConfig.setToDefaults();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                createConfigurationContext(newConfig);
            } else {
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
        }
        return res;
    }

    /**
     * 判断当前进程是否是应用的主进程
     *
     * @return
     */
    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }


    /**
     * 初始化友盟SDK
     */

    private void initUmeng(@NonNull String channel) {
        UMConfigure.init(this, BuildConfig.UMENG_APP_KEY, channel, UMConfigure.DEVICE_TYPE_PHONE, "bb1408dabca67fdbb5d2e0d16b297701");
        //加密日志
        UMConfigure.setEncryptEnabled(true);
        //取消自动统计功能 自己添加在 BaseFragment 和 BaseActivity 中
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
        }
    }

    /**
     * 初始化RxDownload
     */
    private void initRxDownload() {
        DownloadConfig.Builder builder = DownloadConfig.Builder.Companion.create(this)
                .setDefaultPath(DownloadUtil.getDownloadDirPath())
                .enableDb(true)
                .enableService(true)
                .setMaxRange(10)
                .setMaxMission(3)
                .addExtension(ApkInstallExtension.class)
                .addExtension(ApkOpenExtension.class);

        DownloadConfig.INSTANCE.init(builder);

    }


    /**
     * DEBUG 启用 严格模式 测试使用
     */
    private void initStrictMode() {
        if (BuildConfig.DEBUG) {

            LogUtils.d(TAG, "测试严格模式开启");
            //针对线程的相关测试
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            );

            //针对VM的相关策略
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    //.penaltyDeath()
                    .build());

        }
    }

    public String getProcessName(int pid) {
        ActivityManager activityManager = (ActivityManager) TestApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (null == runningAppProcesses) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 防止二次打包
     *
     * @return
     */
    public boolean illegalKey() {
        String sha1Native = EncryptUtils.getSHA1FromJNI();
        String sha1Now = AppInfoUtils.getSignInfo(this, getPackageName(), AppInfoUtils.SHA1);
        Log.d(TAG, "sha1Now = " + sha1Now);
        return !TextUtils.equals(sha1Native, sha1Now);
    }

    public static boolean isOpen() {
        return isOpen;
    }

    public static void setOpen(boolean isOpen) {
        TestApp.isOpen = isOpen;
    }
}


