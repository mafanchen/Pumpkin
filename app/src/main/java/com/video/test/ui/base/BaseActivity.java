package com.video.test.ui.base;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.framework.BasePresenter;
import com.video.test.framework.IView;
import com.video.test.utils.ClazzUtils;
import com.video.test.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Enoch on 2017/5/8.
 */

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView, EasyPermissions.PermissionCallbacks {

    protected P mPresenter;
    private Unbinder mBinder;
    protected boolean isLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(getContextViewId());
        mBinder = ButterKnife.bind(this);
        mPresenter = ClazzUtils.getGenericInstance(this, 0);
        if (this instanceof IView && mPresenter != null) {
            mPresenter.attachView(this);
        }
        initData();
        initToolBar();
        initView();
        setAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatueBarColor();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected void setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.nav_background));
            window.setNavigationBarColor(getResources().getColor(R.color.navigation_background));
            ViewGroup contentView = this.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (null != childView) {
                ViewCompat.setFitsSystemWindows(childView, true);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null && isLoad) {
            mPresenter.subscribe();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.unSubscribe();
            mPresenter = null;
            isLoad = false;
        }
        if (mBinder != null) {
            mBinder.unbind();
            mBinder = null;
        }
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 动态权限
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(getClass().getSimpleName(), "onPermissionsGranted = " + requestCode + " : " + perms.size());

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        LogUtils.d(getClass().getSimpleName(), "onPermissionsDenied = " + requestCode + " : " + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "somePermissionPermanentlyDenied");
            if (requestCode == AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE) {
                LogUtils.d(getClass().getSimpleName(), "forever deny permission Code : " + requestCode);
                showPermSettingDialog(R.string.dialog_perm_remind_storage);
            } else if (requestCode == AppConstant.PERSSION_READ_PHONE_STATE) {
                LogUtils.d(getClass().getSimpleName(), "forever deny permission Code : " + requestCode);
                showPermSettingDialog(R.string.dialog_perm_remind_phone_state);
            } else if (requestCode == AppConstant.PERSSION_CAMERA) {
                LogUtils.d(getClass().getSimpleName(), "forever deny permission Code : " + requestCode);
                showPermSettingDialog(R.string.dialog_perm_remind_camera);
            } else {
                LogUtils.d(getClass().getSimpleName(), "onPermissionsDenied == other");
            }
        } else {
            if (requestCode == AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE) {
                LogUtils.d(getClass().getSimpleName(), "deny permission Code : " + requestCode);
                String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perm);
            } else if (requestCode == AppConstant.PERSSION_READ_PHONE_STATE) {
                LogUtils.d(getClass().getSimpleName(), "deny permission Code : " + requestCode);
                String[] perm = {Manifest.permission.READ_PHONE_STATE};
                EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perm);
            } else if (requestCode == AppConstant.PERSSION_CAMERA) {
                LogUtils.d(getClass().getSimpleName(), "deny permission Code : " + requestCode);
                String[] perm = {Manifest.permission.CAMERA};
                EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_camera), AppConstant.PERSSION_CAMERA, perm);
            } else {
                LogUtils.d(getClass().getSimpleName(), "onPermissionsDenied == other");
            }
        }
    }


    @AfterPermissionGranted(AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE)
    protected void requireStoragePerm() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");

        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
        }
    }

    @AfterPermissionGranted(AppConstant.PERSSION_READ_PHONE_STATE)
    protected void requirePhonePerm() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");

        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perms);
        }
    }

    @AfterPermissionGranted(AppConstant.PERSSION_CAMERA)
    protected void requireCameraPerm() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");
        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_camera), AppConstant.PERSSION_CAMERA, perms);
        }
    }

    private void showPermSettingDialog(int contentId) {
        new AppSettingsDialog.Builder(this)
                .setTitle(R.string.dialog_perm_remind)
                .setRationale(contentId)
                .setPositiveButton(R.string.dialog_perm_confirm)
                .setNegativeButton("")
                .build()
                .show();
    }

    /*如果需要在加载布局前设置一些信息，请重写该方法*/
    protected void beforeSetContentView() {

    }

    /*获取布局的资源*/
    protected abstract int getContextViewId();


    /*一些初始化操作*/
    protected abstract void initData();

    /*初始化一些与界面相关的工作 ,如果需要 可以重写该方法*/
    protected void initView() {

    }

    /*设置适配器使用，需要的话重写该方法*/
    protected void setAdapter() {
    }

    /*初始化toolbar*/
    protected void initToolBar() {
    }

}
