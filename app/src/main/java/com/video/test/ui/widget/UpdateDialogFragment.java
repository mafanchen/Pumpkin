package com.video.test.ui.widget;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Deleted;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Normal;
import zlc.season.rxdownload3.core.Status;
import zlc.season.rxdownload3.core.Succeed;
import zlc.season.rxdownload3.core.Suspend;
import zlc.season.rxdownload3.core.Waiting;
import zlc.season.rxdownload3.extension.ApkInstallExtension;
import zlc.season.rxdownload3.helper.UtilsKt;

/**
 * @author Enoch Created on 2018/10/28.
 */
public class UpdateDialogFragment extends DialogFragment {
    private static final String TAG = "UpdateDialogFragment";

    private TextView mTvTitle;
    private TextView mTvSize;
    private TextView mTvUpdateInfo;
    private Button mBtnConfirm;
    private TextView mTvIgnore;
    private NumberProgressBar mPbNumber;
    private LinearLayout mLLClose;
    private String mDownloadUrl;
    private Status mCurrentStatus = new Status();
    private Disposable mDisposable;

    public static UpdateDialogFragment newInstance(Bundle args) {
        UpdateDialogFragment updateDialogFragment = new UpdateDialogFragment();
        if (null != args) {
            updateDialogFragment.setArguments(args);
        }
        return updateDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.update_dialog);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bean_dialog_update, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int height = lp.height;
        int width = lp.width;
        LogUtils.d(TAG, "UpdateDialog height : " + height + " width : " + width);
        dialogWindow.setAttributes(lp);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UtilsKt.dispose(mDisposable);
    }

    private void initView(View view) {
        mTvTitle = view.findViewById(R.id.tv_title_update_dialog);
        mTvSize = view.findViewById(R.id.tv_update_size_update_dialog);
        mTvUpdateInfo = view.findViewById(R.id.tv_update_info_update_dialog);
        mBtnConfirm = view.findViewById(R.id.btn_confirm_update_dialog);
        mTvIgnore = view.findViewById(R.id.tv_ignore_update_dialog);
        mPbNumber = view.findViewById(R.id.npb_update_dialog);
        mLLClose = view.findViewById(R.id.ll_close_update_dialog);

    }

    private void initData() {
        if (null != getArguments()) {
            Bundle bundle = getArguments();
            String versionTitle = bundle.getString("versionTitle");
            String versionSize = bundle.getString("versionSize");
            String versionInfo = bundle.getString("versionInfo");
            mDownloadUrl = bundle.getString("downloadUrl");
            boolean versionIsForce = bundle.getBoolean("versionIsForce");

            mTvTitle.setText(versionTitle);
            mTvSize.setText(versionSize);
            mTvUpdateInfo.setText(versionInfo);
            if (versionIsForce) {
                mLLClose.setVisibility(View.GONE);
            } else {
                mLLClose.setVisibility(View.VISIBLE);
            }
            //创建下载任务
            create(mDownloadUrl);
        }
    }


    private void setListener() {
        mBtnConfirm.setOnClickListener(new ItemOnClickListener());
        mTvIgnore.setOnClickListener(new ItemOnClickListener());
        mLLClose.setOnClickListener(new ItemOnClickListener());

    }


    private void startDownload(String downloadUrl) {
        if (downloadUrl.startsWith(AppConstant.HTTP_HEAD) || downloadUrl.startsWith(AppConstant.HTTPS_HEAD)) {
            dispatchClick();
        } else {
            ToastUtils.showLongToast("下载地址异常，请您稍后重试");
        }
    }


    private void create(String downladUrl) {
        mDisposable = RxDownload.INSTANCE.create(downladUrl, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Status>() {
                    @Override
                    public void accept(Status status) {
                        LogUtils.d(TAG, "create status ==" + status);
                        mCurrentStatus = status;
                        setProgress(status);
                        setActionText(status);
                        if (status instanceof Succeed
                                || status instanceof ApkInstallExtension.Installing
                                || status instanceof Normal) {
                            mPbNumber.setVisibility(View.GONE);
                        } else {
                            mPbNumber.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void setProgress(Status status) {
        LogUtils.d(TAG, "setProgress stauts == " + status);
        mPbNumber.setMax((int) status.getTotalSize());
        mPbNumber.setProgress((int) status.getDownloadSize());
        LogUtils.d(TAG, "totalSize == " + status.getTotalSize() + " downloadSize == " + status.getDownloadSize());
    }

    private void install() {
        RxDownload.INSTANCE.extension(mDownloadUrl, ApkInstallExtension.class).subscribe();
    }

    private void stop() {
        RxDownload.INSTANCE.stop(mDownloadUrl).subscribe();
    }

    private void start() {
        RxDownload.INSTANCE.start(mDownloadUrl).subscribe();
    }

    private void dispatchClick() {
        LogUtils.d(TAG, "dispatchClick  status == " + mCurrentStatus);
        if (mCurrentStatus instanceof Normal) {
            LogUtils.d(TAG, "dispatchClick == Normal");
            start();
        } else if (mCurrentStatus instanceof Suspend) {
            LogUtils.d(TAG, "dispatchClick == Suspend");
            start();
        } else if (mCurrentStatus instanceof Failed) {
            LogUtils.d(TAG, "dispatchClick == Failed");
            start();
        } else if (mCurrentStatus instanceof Downloading) {
            LogUtils.d(TAG, "dispatchClick == Downloading");
            stop();
        } else if (mCurrentStatus instanceof Succeed) {
            LogUtils.d(TAG, "dispatchClick == Succeed");
            install();
        } else if (mCurrentStatus instanceof Deleted) {
            start();
            LogUtils.d(TAG, "dispatchClick == Deleted");
        }
    }

    private void setActionText(Status status) {
        String text = "";
        if (status instanceof Normal) {
            text = "开始";
        } else if (status instanceof Suspend) {
            text = "已暂停";
        } else if (status instanceof Waiting) {
            text = "等待中";
        } else if (status instanceof Downloading) {
            text = "暂停";
        } else if (status instanceof Failed) {
            text = "失败";
        } else if (status instanceof Succeed) {
            text = "安装";
        } else if (status instanceof ApkInstallExtension.Installing) {
            text = "安装中";
        } else if (status instanceof ApkInstallExtension.Installed) {
            text = "打开";
        } else if (status instanceof Deleted) {
            text = "开始";
        }
        mBtnConfirm.setText(text);
    }


    private class ItemOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_confirm_update_dialog:
                    LogUtils.d(TAG, "btn_confirm_update_dialog url == " + mDownloadUrl);
                    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                    if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                        startDownload(mDownloadUrl);
                    } else {
                        EasyPermissions.requestPermissions(getActivity(), getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
                    }
                    break;
                case R.id.tv_ignore_update_dialog:
                    LogUtils.d(TAG, "tv_ignore_update_dialog");

                    break;
                case R.id.ll_close_update_dialog:
                    LogUtils.d(TAG, "ll_close_update_dialog");
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    // TODO 解决方式不够优雅
    @Override
    public void show(FragmentManager manager, String tag) {

        try {
            super.show(manager, tag);

        } catch (IllegalStateException e) {
            Log.d(TAG, "show state error: " + e.getMessage());
        }
    }

}
