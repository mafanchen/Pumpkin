package com.video.test.module.topicvideolist;

import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.VideoListBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class TopicVideoListModel implements TopicVideoListContract.Model {

    @Override
    public Observable<VideoListBean> getVideoList(int pid, String tag, String type, int page, int limit) {
        return RetrofitHelper.getInstance()
                .getTopicVideoList(pid, tag, type, page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<AddCollectionBean> addTopicCollection(String token, String tokenId, String topicId) {
        return RetrofitHelper.getInstance()
                .addTopicCollection(token, tokenId, topicId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> delTopicCollection(String token, String tokenId, String topicArrayIds) {
        return RetrofitHelper.getInstance()
                .delTopicCollection(token, tokenId, topicArrayIds)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
