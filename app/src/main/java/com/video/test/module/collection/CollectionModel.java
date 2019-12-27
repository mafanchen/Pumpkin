package com.video.test.module.collection;

import com.video.test.javabean.CollectionListBean;
import com.video.test.network.ListResult;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class CollectionModel implements CollectionContract.Model {
    @Override
    public Observable<ListResult<CollectionListBean>> getUserCollection(String userToken, String userTokenId, int page, int limit) {
        return RetrofitHelper.getInstance()
                .getCollectionList(userToken, userTokenId, page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> deleteCollection(String ids, String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .delCollectionList(userToken, userTokenId, ids)
                .compose(RxSchedulers.<String>handleResult())
                .compose(RxSchedulers.<String>io_main());

    }
}
