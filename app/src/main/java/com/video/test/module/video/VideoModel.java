package com.video.test.module.video;

import com.video.test.javabean.HotSearchWordListBean;
import com.video.test.javabean.ScreenBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoModel implements VideoContract.Model {

    @Override
    public Observable<ScreenBean> getScreenTypes() {
        return RetrofitHelper.getInstance()
                .getScreenType()
                .compose(RxSchedulers.handleResult())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<HotSearchWordListBean> getHotSearchWord() {
        return RetrofitHelper.getInstance()
                .getHotSearchWord()
                .compose(RxSchedulers.handleResult())
                .subscribeOn(Schedulers.io());
    }
}
