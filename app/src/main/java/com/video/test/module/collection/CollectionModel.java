package com.video.test.module.collection;

import com.video.test.javabean.CollectionBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class CollectionModel implements CollectionContract.Model {

    @Override
    public Observable<String> deleteTopicCollection(String token, String tokenId, String topicArrayIds) {
        return RetrofitHelper.getInstance()
                .delTopicCollection(token, tokenId, topicArrayIds)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> deleteVideoCollection(String ids, String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .delCollectionList(userToken, userTokenId, ids)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());

    }

    @Override
    public Observable<CollectionBean> getAllCollection() {
        return RetrofitHelper.getInstance()
                .getAllCollection()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
