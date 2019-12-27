package com.video.test.module.history;

import com.video.test.javabean.HistoryListBean;
import com.video.test.network.ListResult;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class HistoryModel implements HistoryContract.Model {
    @Override
    public Observable<ListResult<HistoryListBean>> getUserHistory(String userToken, String userTokenId, int page, int limit) {
        return RetrofitHelper.getInstance()
                .getHistoryList(userToken, userTokenId, page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> deleteHistory(String ids, String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .delHistoryList(userToken, userTokenId, ids)
                .compose(RxSchedulers.<String>handleResult())
                .compose(RxSchedulers.<String>io_main())
                ;
    }
}
