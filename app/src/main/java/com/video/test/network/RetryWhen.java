package com.video.test.network;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * java类作用描述
 *
 * @author : AhhhhDong
 * @date : 2019/5/15 17:40
 */
public class RetryWhen implements Function<Observable<Throwable>, ObservableSource<?>> {

    private static final String TAG = "RetryWhen";
    private final int maxRetries = 4;
    private final int retryDelay = 1;
    private int retryCount;

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries) {
                    Log.d(TAG, "Observable get error, it will try after " + retryDelay
                            + " second, retry count " + retryCount);
                    return Observable.timer(retryDelay, TimeUnit.SECONDS);
                } else {
                    return Observable.error(throwable);
                }
            }
        });
    }
}
