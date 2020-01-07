package com.video.test;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.video.test.db.DBManager;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.javabean.SplashBean;
import com.video.test.javabean.event.DownloadEvent;
import com.video.test.sp.SpUtils;
import com.video.test.utils.AppInfoUtils;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.EncryptUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.NetworkUtils;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;
import jaygoo.library.m3u8downloader.M3U8Downloader;
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig;
import jaygoo.library.m3u8downloader.OnM3U8DownloadListener;
import jaygoo.library.m3u8downloader.bean.M3U8;
import jaygoo.library.m3u8downloader.bean.M3U8Task;
import zlc.season.rxdownload3.core.DownloadConfig;
import zlc.season.rxdownload3.extension.ApkInstallExtension;
import zlc.season.rxdownload3.extension.ApkOpenExtension;

/**
 * @author Enoch Created on 2018/6/25.
 */
public class TestApp extends MultiDexApplication {
    private static final String TAG = "TestApp";
    private static final String M3U8TAG = "PlayM3U8Listener";
    private static Context sContext;
    private static TestApp sApp;
    private static boolean isOpen = true;
    /**
     * 应用是否是后台
     */
    private boolean isBackground = false;
    /**
     * 记录切换到后台的时间，用来判断当再次切换到前台时，是否播放广告
     */
    private long currentBackgroundTime;

    @Override
    public void onCreate() {
        super.onCreate();
//        MultiDex.install(this);
        //防二次打包
//        if (illegalKey()) {
//            ToastUtils.showToast(this, "您的安装包异常，请前往官网下载最新安装包");
//            new Handler().postDelayed(() -> System.exit(0), 5000);
//        }

        LogUtils.d(TAG, "onCreate");
        sApp = this;
        sContext = getApplicationContext();

        //确保只有一个进程初始化东西
        initJPush(this);
        initARouter();
        initZXing();
        initOpenInstall();
        initRxDownload();
        initStrictMode();
        initLeCast();
        initM3U8();
        registActivityLifecycle();
    }


    private void initM3U8() {
        M3U8DownloaderConfig config = M3U8DownloaderConfig.build(this);
        config.setSaveDir(DownloadUtil.getDownloadDirPath())
                .setConnTimeout(10 * 1000)
                .setReadTimeout(30 * 60 * 1000)
                .setThreadCount(3);
        M3U8Downloader.getInstance().setOnM3U8DownloadListener(new OnM3U8DownloadListener() {

            /**
             * 监听到下载进度变化
             * @param task
             * @param itemFileSize
             * @param totalTs
             * @param curTs
             */
            @Override
            public void onDownloadItem(M3U8Task task, long itemFileSize, int totalTs, int curTs) {
                super.onDownloadItem(task, itemFileSize, totalTs, curTs);
                LogUtils.d(M3U8TAG, "videoId : " + task.getVideoId() + " name : " + task.getVideoName() + " itemFileSize : "
                        + itemFileSize + " curTs : " + curTs + " totalTs : " + totalTs);
                updateM3U8TaskTsItem(task, totalTs, curTs);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_PROGRESS, task));
            }

            /**
             * 监听到下载成功
             * @param task
             */
            @Override
            public void onDownloadSuccess(M3U8Task task) {
                super.onDownloadSuccess(task);
                LogUtils.d(M3U8TAG, "Success taskName : " + task.getVideoName());
//                ToastUtils.showLongToast(TestApp.getContext(), task.getVideoName() + " 已完成缓存");
                updateM3U8TaskSuccess(task);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_UPDATE_STATUS, task));
            }

            /**
             * 监听到下载暂停
             * @param task
             */
            @Override
            public void onDownloadPause(M3U8Task task) {
                super.onDownloadPause(task);
                LogUtils.d(M3U8TAG, "Pause  taskName : " + task.getVideoName());
//                ToastUtils.showLongToast(TestApp.getContext(), task.getVideoName() + " 已暂停");
                updateM3U8TaskStatus(task);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_UPDATE_STATUS, task));
            }

            @Override
            public void onDownloadPending(M3U8Task task) {
                super.onDownloadPending(task);
                LogUtils.d(M3U8TAG, "Pending task status : " + task.getState());
//                ToastUtils.showLongToast(TestApp.getContext(), task.getVideoName() + " 已添加缓存队列");
                updateM3U8TaskStatus(task);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_UPDATE_STATUS, task));
            }

            @Override
            public void onDownloadPrepare(M3U8Task task) {
                super.onDownloadPrepare(task);
                LogUtils.d(M3U8TAG, "Prepare task status : " + task.getState());
                updateM3U8TaskStatus(task);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_UPDATE_STATUS, task));
            }

            @Override
            public void onDownloadError(M3U8Task task, Throwable errorMsg) {
                super.onDownloadError(task, errorMsg);
                LogUtils.d(M3U8TAG, "error task status : " + task.getState());
                updateM3U8TaskStatus(task);
                EventBus.getDefault().post(new DownloadEvent(DownloadEvent.Type.TYPE_UPDATE_STATUS, task));
            }
        });
        //如果是wifi则自动开始任务
        if (NetworkUtils.isWifiConnected(this)) {
            startDownloadAllTask();
        }
        //如果是移动网络
        else if (NetworkUtils.isMobileConnected(this)) {
            //如果允许移动网络下载，则启动下载所有任务
            boolean mobileNetworkOpen = SpUtils.getBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_DOWN, true);
            if (mobileNetworkOpen) {
                startDownloadAllTask();
            }
        }
    }

    /**
     * 下载全部任务
     */
    private void startDownloadAllTask() {
        new Thread(() -> {

        }).run();
        Disposable subscribe = Observable
                .zip(Observable
                                .create(e -> {
                                    List<M3U8DownloadBean> allTask = DBManager.getInstance(TestApp.this).queryM3U8DownloadingTasks();
                                    for (M3U8DownloadBean bean : allTask) {
                                        e.onNext(bean);
                                    }
                                    e.onComplete();
                                })
                        , Observable.interval(200, TimeUnit.MILLISECONDS)
                        , (BiFunction<M3U8DownloadBean, Long, M3U8DownloadBean>) (m3U8DownloadBean, aLong) -> {
                            Log.d("startDownloadTask", String.valueOf(System.currentTimeMillis()));
                            return m3U8DownloadBean;
                        }

                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean -> M3U8Downloader.getInstance().download(bean.getVideoUrl(), bean.getVideoId(), bean.getVideoName(), bean.getVideoTotalName()));

    }

    private void updateM3U8TaskTsItem(M3U8Task task, int totalTs, int curTs) {
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setCurTs(curTs);
            m3U8DownloadBean.setTotalTs(totalTs);
            m3U8DownloadBean.setTaskStatus(task.getState());
            m3U8DownloadBean.setProgress(task.getProgress());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
    }


    private void updateM3U8TaskStatus(M3U8Task task) {
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setTaskStatus(task.getState());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
    }


    private void updateM3U8TaskSuccess(M3U8Task task) {
        M3U8 m3U8 = task.getM3U8();
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setTaskStatus(task.getState());
            m3U8DownloadBean.setProgress(1.0f);
            m3U8DownloadBean.setIsDownloaded(true);
            m3U8DownloadBean.setDirFilePath(m3U8.getDirFilePath());
            m3U8DownloadBean.setM3u8FilePath(m3U8.getM3u8FilePath());
            m3U8DownloadBean.setTotalFileSize(m3U8.getFileSize());
            m3U8DownloadBean.setTotalTime(m3U8.getTotalTime());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
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

    private void registActivityLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (isBackground) {
                    isBackground = false;
                    if (System.currentTimeMillis() - currentBackgroundTime >= AppConstant.TIME_PLAY_AD_WHEN_BACKGROUND && isOpen) {
                        //这里判断是否有缓存
                        SplashBean saveSplashBean = SpUtils.getSerializable(TestApp.getContext(), "splashBean");
                        if (saveSplashBean != null && !TextUtils.isEmpty(saveSplashBean.getJump_url()) && !TextUtils.isEmpty(saveSplashBean.getPic_url())) {
                            ARouter.getInstance().build("/ad/activity")
                                    .withString("ad_name", saveSplashBean.getAd_name())
                                    .withString("jump_url", saveSplashBean.getJump_url())
                                    .withString("pic_url", saveSplashBean.getPic_url())
                                    .withString("ad_id", saveSplashBean.getId())
                                    .withInt("showTime", saveSplashBean.getShow_time())
                                    .navigation();
                        }
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });
        registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                if (level == TRIM_MEMORY_UI_HIDDEN) {
                    isBackground = true;
                    currentBackgroundTime = System.currentTimeMillis();
                    Log.d(TAG, "应用切换到后台");
                }
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {

            }

            @Override
            public void onLowMemory() {

            }
        });
    }

}


