package com.video.test.module.splash;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.utils.LogUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPluginPlatformInterface;
import cn.jpush.android.api.JPushInterface;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/splash/activity")
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View, EasyPermissions.RationaleCallbacks {
    private static final String TAG = "SplashActivity";
    private JPluginPlatformInterface pHuaweiPushInterface;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pHuaweiPushInterface = new JPluginPlatformInterface(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        pHuaweiPushInterface.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        pHuaweiPushInterface.onStop(this);
    }


    @Override
    protected void initData() {
        SpUtils.putInt(TestApp.getContext(), AppConstant.LAUNCH_COUNT, AppConstant.LAUNCH_FIRST);
        //openInstall
        mPresenter.shareGetInfo(getIntent());
        //获取广告
        mPresenter.getSplashBean();
        //  mPresenter.getDomainUrls();   使用Gradle 动态编译时配置域名,改接口暂停使用.
        mPresenter.registerWeChat();
        requirePhonePerm();
        // encryptAES();
        String xiaomiRegId = MiPushClient.getRegId(getApplicationContext());
        if (xiaomiRegId != null) {
            Log.d("JPUSH", "xiaomi_regId=" + xiaomiRegId);
        }
        if (BuildConfig.DEBUG) {
            String registrationID = JPushInterface.getRegistrationID(this);
            if (registrationID != null) {
                Log.d("JPUSH", "registrationID=" + registrationID);
            }
            Set<String> set = new HashSet<>();
            set.add("test");
            JPushInterface.setTags(this, 0, set);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(TAG, "onActivityResult requestCode :" + requestCode + " resultCode :" + resultCode);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String[] perms = {Manifest.permission.READ_PHONE_STATE};
            if (EasyPermissions.hasPermissions(this, perms)) {
                LogUtils.d(TAG, "setting havePermissions");
                // 从设置页面返回后,如果已经有了权限,就可以直接开始计时,跳过该页面
                mPresenter.countDownSplash();
            } else {
                LogUtils.d(TAG, "setting not havePermissions");
                // 依然没有读取状态权限,就弹出退出对话框
                showPermsExitDialog(perms);
            }
        }
        //JPush中调用HMS SDK解决错误的接口传入的requestCode为10001,开发者调用是请注意不要同样使用10001
        else if (requestCode == JPluginPlatformInterface.JPLUGIN_REQUEST_CODE) {
            pHuaweiPushInterface.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void skipSplashActivity() {
        ARouter.getInstance().build("/homepage/activity").navigation();
        finish();
    }

    @Override
    public void jumpToAdPage(String adName, String jumpUrl, String picUrl, String adId) {
        ARouter.getInstance().build("/ad/activity")
                .withString("ad_name", adName)
                .withString("jump_url", jumpUrl)
                .withString("pic_url", picUrl)
                .withString("ad_id", adId)
                .withBoolean("isSplash", true)
                .navigation();
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(TAG, "onNewIntent");
        mPresenter.shareGetInfo(getIntent());
    }

    @Override
    @AfterPermissionGranted(AppConstant.PERSSION_READ_PHONE_STATE)
    protected void requirePhonePerm() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");
            mPresenter.countDownSplash();
        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perms);
        }
    }


    @Override
    public void onRationaleAccepted(int requestCode) {
        LogUtils.d(TAG, "onRationaleAccepted requestCode : " + requestCode);

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        LogUtils.d(TAG, "onRationaleDenied  requestCode : " + requestCode);
        finish();
    }


    private void showPermsExitDialog(String[] perms) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_perm_remind)
                .setCancelable(false)
                .setMessage(R.string.permission_exit_content)
                .setPositiveButton(R.string.permission_exit_confirm, (dialog, which) -> {
                    EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perms);
                })
                .setNegativeButton(R.string.permission_exit_exit, ((dialog, which) -> {
                    finish();
                }))
                .show();
    }

}
