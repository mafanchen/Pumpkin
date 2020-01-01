package com.video.test.module.videolist;

import com.video.test.javabean.VideoListBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoListModel implements VideoListContract.Model {
    @Override
    public Observable<VideoListBean> getVideoList(int pid, String tag, String type, int page, int limit) {
        return RetrofitHelper.getInstance()
                .getTopicVideoList(pid, tag, type, page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VideoListBean> addVideoList(int pid, String tag, String type, int page, int limit) {
        return RetrofitHelper.getInstance()
                .getTopicVideoList(pid, tag, type, page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
