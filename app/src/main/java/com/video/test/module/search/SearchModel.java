package com.video.test.module.search;

import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.SearchResultBean;
import com.video.test.javabean.SearchSortTypeBean;
import com.video.test.network.BaseResult;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SearchModel implements SearchContract.Model {

    @Override
    public Observable<SearchResultBean> getSearchResult(String keyword, String sort) {
        return RetrofitHelper.getInstance()
                .getSearchResult(keyword, sort)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<List<SearchSortTypeBean>> getSortType() {
        return RetrofitHelper.getInstance()
                .getSortType()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<AddCollectionBean> addCollections(String vodId, String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .addCollectionList(userToken, userTokenId, vodId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> delCollections(String ids, String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .delCollectionList(userToken, userTokenId, ids)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<BaseResult> addCollectCount(String vodId) {
        return RetrofitHelper.getInstance()
                .addVideoInfo(vodId, "3");
    }

    @Override
    public Observable<List<String>> getAssociationWord(String searchWord) {
        return RetrofitHelper.getInstance()
                .getClewWord(searchWord)
                .compose(RxSchedulers.io_main())
                .compose(RxSchedulers.handleResult());
    }
}
