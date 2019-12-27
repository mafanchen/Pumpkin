package com.video.test.ui.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView, EasyPermissions.PermissionCallbacks {

    protected P mPresenter;
    protected Context mContext;
    private Unbinder mBinder;
    private long mDuration = 0L;

    @Override
    public void onAttach(Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
        if (null == mBinder) {
            mBinder = ButterKnife.bind(this, view);
        }
        mPresenter = ClazzUtils.getGenericInstance(this, 0);
        if (this instanceof IView) {
            mPresenter.attachView(this);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //阿里云Fragment 页面埋点
        this.mDuration = SystemClock.elapsedRealtime();
        //友盟页面Fragment埋点
        MobclickAgent.onPageStart(getClass().getSimpleName());
        LogUtils.d(getClass().getSimpleName(), "onPageStart");
    }


    @Override
    public void onPause() {
        super.onPause();
        //友盟收集页面埋点信息
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        LogUtils.d(getClass().getSimpleName(), "onPageEnd");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
        initView();
        setAdapter();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mBinder) {
            mBinder.unbind();
            mBinder = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.unSubscribe();
            mPresenter = null;
        }
    }


    protected void setAdapter() {

    }

    /*初始化数据*/
    protected abstract void loadData();

    /*获取内容资源的Id*/
    protected abstract int getContentViewId();

    /*如果需要初始化一些控件,可以重写该方法*/
    protected void initView() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

        }
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
                showPermSettingDialog(R.string.dialog_perm_remind_storage);
            } else if (requestCode == AppConstant.PERSSION_READ_PHONE_STATE) {
                showPermSettingDialog(R.string.dialog_perm_read_phone_state);
            } else if (requestCode == AppConstant.PERSSION_CAMERA) {
                showPermSettingDialog(R.string.dialog_perm_camera);
            } else {
                LogUtils.d(getClass().getSimpleName(), "onPermissionsDenied == other");
            }
        }
    }

    @AfterPermissionGranted(AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE)
    protected void requireStoragePerm() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");

        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
        }
    }

    @AfterPermissionGranted(AppConstant.PERSSION_READ_PHONE_STATE)
    protected void requirePhonePerm() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");

        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perms);
        }
    }

    @AfterPermissionGranted(AppConstant.PERSSION_CAMERA)
    protected void requireCameraPerm() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(mContext, perms)) {
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
}
