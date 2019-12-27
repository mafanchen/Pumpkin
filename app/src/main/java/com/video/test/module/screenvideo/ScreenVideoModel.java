package com.video.test.module.screenvideo;

import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.ScreenResultBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class ScreenVideoModel implements ScreenVideoContract.Model {

    @Override
    public Observable<ScreenBean> getScreenTypes() {
        return RetrofitHelper.getInstance()
                .getScreenType()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<ScreenResultBean> getScreenResult(int page, int limit, int videoPid, String area, String type, String year, String sort) {
        return RetrofitHelper.getInstance()
                .getScreenResult(page, limit, videoPid, area, type, year, sort)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<ScreenResultBean> moreScreenResult(int page, int limit, int videoPid, String area, String type, String year, String sort) {
        return RetrofitHelper.getInstance()
                .getScreenResult(page, limit, videoPid, area, type, year, sort)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
