package com.video.test.module.topicvideolist;

import android.util.Log;

import com.google.gson.Gson;
import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.javabean.VideoBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class TopicVideoListPresenter extends TopicVideoListContract.Presenter<TopicVideoListModel> {
    private static final String TAG = "TopicVideoListPresenter";

    private String ztCollectId = null;

    @Override
    public void subscribe() {

    }

    @Override
    void getVideoList(int pid, String tag, String type) {
        int mPage = 1;
        int mLimit = 1000;
        Disposable disposable = mModel.getVideoList(pid, tag, type, mPage, mLimit)
                .subscribe(videoListBean -> {
                    mView.setCollectCheckBoxChecked(videoListBean.isZtIsCollect());
                    List<VideoBean> videoList = videoListBean.getList();
                    ztCollectId = videoListBean.getZtCollectId();
                    mView.setVideoList(videoList);
                    mView.setPageTitle(videoListBean.getTitle());
                    mView.setPic(videoListBean.getZtPic());
                    mView.setContent(videoListBean.getZtDetail());
                    mView.setTopicNum(videoList.size());
                    mView.hideRefreshLayout(true);
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "getTopicVideoList Error " + throwable.getMessage());
                    mView.hideRefreshLayout(false);
                    mView.showNetworkErrorView();
                }));
        addDisposable(disposable);
    }

    public void collect(boolean checked, String topicId) {
        if (checked) {
            addCollect(topicId);
        } else {
            deleteCollect(ztCollectId);
        }
    }

    private void addCollect(String topicId) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable subscribe = mModel.addTopicCollection(userToken, userTokenId, topicId)
                .subscribe(i -> {
                    ztCollectId = i.getCollect_id();
                    mView.showToast("收藏成功");
                }, new RxExceptionHandler<>(throwable -> {
                    // 失败不提示，直接修改状态
                    LogUtils.e(TAG, "addCollect Error " + throwable.getMessage());
                    collectFailure(true);
                }));
        addDisposable(subscribe);
    }

    private void deleteCollect(String ztCollectId) {
        if (ztCollectId == null) {
            mView.showToast("操作失败，请稍后重试");
            collectFailure(false);
            return;
        }
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        String topicIds = new Gson().toJson(new String[]{ztCollectId});
        Log.d(TAG, "deleteCollect,ids = " + topicIds);
        Disposable subscribe = mModel.delTopicCollection(userToken, userTokenId, topicIds)
                .subscribe(i -> {
                    mView.showToast("取消收藏成功");
                }, new RxExceptionHandler<>(throwable -> {
                    // 失败不提示，直接修改状态
                    LogUtils.e(TAG, "deleteCollect Error " + throwable.getMessage());
                    collectFailure(false);
                }));
        addDisposable(subscribe);
    }

    /**
     * 收藏或者删除收藏失败后，需要把收藏按钮状态还原
     *
     * @param isCollect
     */
    private void collectFailure(boolean isCollect) {
        mView.setCollectCheckBoxChecked(!isCollect);
    }
}
