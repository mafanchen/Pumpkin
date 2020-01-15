package com.video.test.network;


import com.video.test.AppConstant;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 22/03/2018.
 */

public class RxExceptionHandler<T extends Throwable> implements Consumer<T> {
    private static final String TAG = "RxExceptionHandler";
    private Consumer<? super Throwable> mOnError;

    private static final String TIMEOUT_EXCEPTION = "网络连接超时，请检查您的网络状态，稍后重试";
    private static final String CONNECT_EXCEPTION = "网络连接异常，请检查您的网络状态";
    private static final String JSON_EXCEPTION = "数据获取异常";
    private static final String UNKNOWN_HOST_EXCEPTION = "网络异常，请检查您的网络状态";
    private static final String IO_EXCEPTION = "网络读取异常，请检查您的网络状态";


    public RxExceptionHandler(Consumer<? super Throwable> onError) {
        this.mOnError = onError;
    }


    @Override
    public void accept(T t) throws Exception {
        LogUtils.i(TAG, "accept : " + t.getMessage());
        if (t instanceof SocketTimeoutException) {
            LogUtils.e(TAG, "onError: SocketTimeoutException---");
            ToastUtils.showToast(TIMEOUT_EXCEPTION);
            mOnError.accept(t);
        } else if (t instanceof ConnectException) {
            LogUtils.e(TAG, "onError: ConnectException---");
            ToastUtils.showToast(CONNECT_EXCEPTION);
            mOnError.accept(t);
        } else if (t instanceof UnknownHostException) {
            LogUtils.e(TAG, "onError: UnknownHostException---");
            ToastUtils.showToast(UNKNOWN_HOST_EXCEPTION);
            mOnError.accept(t);
        } else if (t instanceof JSONException) {
            LogUtils.e(TAG, "onError: JSONException---");
            ToastUtils.showToast(JSON_EXCEPTION);
            mOnError.accept(t);
        } else if (t instanceof IOException) {
            LogUtils.e(TAG, "onError: IOException---");
            mOnError.accept(t);
            ToastUtils.showToast(IO_EXCEPTION);
        } else if (t instanceof BaseException) {
            LogUtils.e(TAG, "onError: BaseException---");
            int errorCode = ((BaseException) t).getErrorCode();
            if (errorCode == AppConstant.REQUEST_INVALID_TOKEN) {
                LogUtils.i(TAG, "onError : " + AppConstant.REQUEST_INVALID_TOKEN);
                ((BaseException) t).setErrorCode(AppConstant.REQUEST_INVALID_TOKEN);
                ((BaseException) t).setErrorMsg("TOKEN 已失效");
                mOnError.accept(t);
                return;
            } else if (errorCode == AppConstant.REQUEST_FAILED) {
                LogUtils.i(TAG, "onError :" + AppConstant.REQUEST_FAILED + " message = " + ((BaseException) t).getErrorMsg());
                mOnError.accept(t);
                ToastUtils.showToast(((BaseException) t).getErrorMsg());
                return;
            } else if (errorCode == AppConstant.REQUEST_USED_PHONE) {
                // TODO 手机已经被绑定,确认是否再次绑定
                LogUtils.i(TAG, "onError : " + AppConstant.REQUEST_USED_PHONE + " message = " + ((BaseException) t).getErrorMsg());
                ((BaseException) t).setErrorCode(AppConstant.REQUEST_USED_PHONE);
                ((BaseException) t).setErrorMsg("该手机已绑定");
                mOnError.accept(t);
            } else {
                LogUtils.i(TAG, "onError : " + errorCode);
                mOnError.accept(t);
                ToastUtils.showToast(((BaseException) t).getErrorMsg());
            }
        } else {
            try {
                mOnError.accept(t);
            } catch (Exception e) {
                LogUtils.i(TAG, "BaseException accept : " + e.getMessage());
            }
        }
    }
}
