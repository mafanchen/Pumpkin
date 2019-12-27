package com.video.test.utils;


import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.network.BaseException;
import com.video.test.network.BaseResult;
import com.video.test.sp.SpUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by enochguo on 16/03/2018.
 */

public class RxSchedulers {
    private static final String TAG = "RxSchedulers";

    private RxSchedulers() {
        throw new UnsupportedOperationException("UtilsClass can't initialze");
    }


    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<BaseResult<T>, T> handleResult() {
        return new ObservableTransformer<BaseResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<BaseResult<T>> upstream) {
                return upstream.flatMap(new Function<BaseResult<T>, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(BaseResult<T> tBaseResult) {
                        String threadName = Thread.currentThread().getName();

                        int code = tBaseResult.getCode();
                        LogUtils.i(TAG, "handleResult Code = " + code + " Message = " + tBaseResult.getMsg() + " Thread == " + threadName);
                        if (tBaseResult.isSuccess()) {
                            if (null != tBaseResult.getData()) {
                                return Observable.just(tBaseResult.getData());
                            }
                            return (ObservableSource<T>) Observable.just(tBaseResult.getMsg());

                        } else if (AppConstant.REQUEST_INVALID_TOKEN == code) {
                            LogUtils.i(TAG, "handleResult Session InValid");

                            SpUtils.removeBoolean(TestApp.getContext(), AppConstant.USER_IS_LOGIN);
                            SpUtils.removeString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL);

                            return Observable.error(new BaseException(tBaseResult.getCode(), tBaseResult.getMsg()));
                        } else {

                            LogUtils.i(TAG, "handleResult other error Code == " + tBaseResult.getCode() + " msg == " + tBaseResult.getMsg());
                            return Observable.error(new BaseException(tBaseResult.getCode(), tBaseResult.getMsg()));
                        }
                    }
                });
            }
        };
    }
}


