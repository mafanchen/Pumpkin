package com.video.test.module.setting;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.widget.SwitchButton;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.FileUtils;
import com.video.test.utils.RxSchedulers;
import com.video.test.utils.ToastUtils;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import jaygoo.library.m3u8downloader.M3U8Downloader;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SettingPresenter extends SettingContract.Presenter<SettingModel> {
    private static final String TAG = "SettingPresenter";

    @Override
    public void subscribe() {

    }


    @Override
    void clearAllHistory() {
        DBManager.getInstance(TestApp.getContext()).deleteAllSearchHistoryWord();
        ToastUtils.showToast(TestApp.getContext(), "搜索历史记录已清除");
    }


    @Override
    void showCleanHistoryDialog(Context context) {
        new MaterialDialog.Builder(context).title(R.string.dialog_confirm_box)
                .content(R.string.dialog_clear_history)
                .positiveText(R.string.dialog_confirm)
                .negativeText(R.string.dialog_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        clearAllHistory();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    void showCleanVideoCacheDialog(Context context) {
        new MaterialDialog.Builder(context).title(R.string.dialog_confirm_box)
                .content(R.string.dialog_clear_video)
                .positiveText(R.string.dialog_confirm)
                .negativeText(R.string.dialog_cancel)
                .widgetColor(context.getResources().getColor(R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        dialog.dismiss();
                        clearAllCache();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@android.support.annotation.NonNull MaterialDialog dialog, @android.support.annotation.NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    protected void getSwitchButtonStatus(SwitchButton switchButton) {
        if (null != switchButton) {
            boolean switchButtonStatus = SpUtils.getBoolean(TestApp.getContext(), "netSwitchButton", true);
            if (switchButtonStatus) {
                switchButton.setChecked(true);
            } else {
                switchButton.setChecked(false);
            }
        }
    }

    @Override
    public void getSwitchHistoryButtonStatus(SwitchButton switchButton) {
        if (null != switchButton) {
            boolean switchButtonStatus = SpUtils.getBoolean(TestApp.getContext(), "historySwitchButton", true);
            if (switchButtonStatus) {
                switchButton.setChecked(true);
            } else {
                switchButton.setChecked(false);
            }
        }
    }

    @Override
    public void getSwitchPushButtonStatus(SwitchButton switchButton) {
        if (null != switchButton) {
            boolean isOpen = !JPushInterface.isPushStopped(TestApp.getContext().getApplicationContext());
            if (isOpen) {
                switchButton.setChecked(true);
            } else {
                switchButton.setChecked(false);
            }
        }
    }

    private void clearAllCache() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            List<M3U8DownloadBean> allTaskList = DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks();
            for (M3U8DownloadBean bean : allTaskList) {
                M3U8Downloader.getInstance().cancel(bean.getVideoUrl());
            }
            DBManager.getInstance(TestApp.getContext()).deleteAllM3U8Task();
            deleteChildFile(DownloadUtil.getDownloadDirFile());
            deleteChildFile(DownloadUtil.getCacheDirFile());
            deleteChildFile(DownloadUtil.getImageDirFile());
            emitter.onNext("本地缓存已清理");
            emitter.onComplete();
        })
                .compose(RxSchedulers.io_main())
                .subscribe(
                        s -> ToastUtils.showLongToast(TestApp.getContext(), s),
                        throwable -> ToastUtils.showLongToast(TestApp.getContext(), "清理失败,请您稍后重试"));
        addDisposable(subscribe);
    }

    private void deleteChildFile(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return;
        }
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                FileUtils.deleteDir(child);
            } else {
                FileUtils.deleteFile(child);
            }
        }
    }
}
