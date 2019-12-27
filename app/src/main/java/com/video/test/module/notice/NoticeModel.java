package com.video.test.module.notice;

import com.video.test.javabean.NoticeBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class NoticeModel implements NoticeContract.Model {

    @Override
    public Observable<List<NoticeBean>> getSystemNotice() {
        return RetrofitHelper.getInstance()
                .getSystemNotice()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
