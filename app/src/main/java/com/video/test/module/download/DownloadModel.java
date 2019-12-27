package com.video.test.module.download;

import com.video.test.TestApp;
import com.video.test.sp.SpUtils;
import com.video.test.utils.RxSchedulers;
import com.video.test.utils.SDCardUtils;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class DownloadModel implements DownloadContract.Model {
    @Override
    public Observable<Long> getSDCardFreeSize() {
        String cachePath = SpUtils.getString(TestApp.getContext(), "cachePath", SDCardUtils.getSDRootPath());
        return Observable.just(SDCardUtils.getFreeSpaceBytes(cachePath))
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> getSDCardTotalSize() {
        String cachePath = SpUtils.getString(TestApp.getContext(), "cachePath", SDCardUtils.getSDRootPath());
        return Observable.just(SDCardUtils.getTotalSpaceBytes(cachePath))
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long aLong) {
                        return SDCardUtils.getSizeString(aLong);
                    }
                }).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<Integer> getSDCardUsedPercent() {
        String cachePath = SpUtils.getString(TestApp.getContext(), "cachePath", SDCardUtils.getSDRootPath());
        Observable<Long> freeSpace = Observable.just(SDCardUtils.getFreeSpaceBytes(cachePath));
        Observable<Long> totalSpace = Observable.just(SDCardUtils.getTotalSpaceBytes(cachePath));

        return Observable.zip(totalSpace, freeSpace, new BiFunction<Long, Long, Integer>() {
            @Override
            public Integer apply(@NonNull Long aLong, @NonNull Long aLong2) {
                return (int) ((double) (aLong - aLong2) / aLong * 100.0);
            }
        });
    }


}
