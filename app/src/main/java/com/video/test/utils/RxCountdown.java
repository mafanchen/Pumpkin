package com.video.test.utils;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Enoch on 2017/5/8.
 */

public class RxCountdown {

    private RxCountdown() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    public static Observable<Integer> countdown(int time) {
        if (time < 0) {
            time = 0;
        }
        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long increaseTime) throws Exception {
                        return countTime - increaseTime.intValue();
                    }
                })
                .take(countTime + 1)
                .compose(RxSchedulers.<Integer>io_main());
    }
}
