package com.video.test.module.topicvideolist;

import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class TopicVideoListPresenter extends TopicVideoListContract.Presenter<TopicVideoListModel> {
    private static final String TAG = "TopicVideoListPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getVideoList(int pid, String tag, String type) {
        int mPage = 1;
        int mLimit = 1000;
        Disposable disposable = mModel.getVideoList(pid, tag, type, mPage, mLimit)
                .subscribe(videoListBean -> {
                    mView.setVideoList(videoListBean.getList());
                    mView.setPageTitle(videoListBean.getTitle());
                    mView.setPic(videoListBean.getZtPic());
                    mView.setContent(videoListBean.getZtDetail());
                    mView.setTopicNum();
                    mView.hideRefreshLayout(true);
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "getVideoList Error " + throwable.getMessage());
                    mView.hideRefreshLayout(false);
                    mView.showNetworkErrorView();
                }));
        addDisposable(disposable);
    }

}
