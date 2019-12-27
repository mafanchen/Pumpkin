package com.video.test.module.videolist;

import com.video.test.javabean.VideoListBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoListPresenter extends VideoListContract.Presenter<VideoListModel> {
    private static final String TAG = "VideoListPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getVideoList(int pid, String tag, String type, int page, int limit) {
        mView.showRefreshLayout();
        Disposable disposable = mModel.getVideoList(pid, tag, type, page, limit)
                .subscribe(videoListBean -> {
                    mView.hideRefreshLayout(true);
                    mView.setVideoList(videoListBean.getList());
                    mView.setPageTitle(videoListBean.getTitle());
                }, new RxExceptionHandler<>(throwable -> {
                    mView.hideRefreshLayout(false);
                    LogUtils.e(TAG, "getVideoList Error " + throwable.getMessage());
                }));
        addDisposable(disposable);
    }

    @Override
    void addVideoList(int pid, String tag, String type, int page, int limit) {
        Disposable disposable = mModel.addVideoList(pid, tag, type, page, limit)
                .subscribe(new Consumer<VideoListBean>() {
                    @Override
                    public void accept(VideoListBean videoListBean) throws Exception {
                        mView.addVideoList(videoListBean.getList());
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(TAG, "getVideoList Error " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }
}
