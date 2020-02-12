package com.video.test.module.player;

import com.video.test.TestApp;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.sp.SpUtils;
import com.video.test.utils.RxSchedulers;
import com.video.test.utils.SDCardUtils;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class PlayerModel implements PlayerContract.Model {
    @Override
    public Observable<VideoPlayerBean> getVideoPlayerContent(Map<String, String> fieldMap) {
        return RetrofitHelper.getInstance()
                .getVideoDetails(fieldMap)
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
    public Observable<String> addHistory(String vodId, String videoName, String palyUrl, long currentTime, long totalTime,
                                         String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .addHistoryList(userToken, userTokenId, vodId, videoName, palyUrl, totalTime, currentTime)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<List<VideoCommentBean>> getVideoComment(String videoId) {
        return RetrofitHelper.getInstance()
                .getVideoComment(videoId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> commentVideo(String vodId, String content, String token, String tokenId) {
        return RetrofitHelper.getInstance()
                .commentVideo(vodId, content, token, tokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> feedbackSuggest(String vodId, String suggestMessage) {
        return RetrofitHelper.getInstance()
                .feedbackInfo(vodId, suggestMessage)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<Long> getSDCardFreeSize() {
        String cachePath = SpUtils.getString(TestApp.getContext(), "cachePath", SDCardUtils.getSDRootPath());
        return Observable.just(SDCardUtils.getFreeSpaceBytes(cachePath))
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VideoAdDataBean> getVideoAd() {
        return RetrofitHelper.getInstance()
                .getVideoAd()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
