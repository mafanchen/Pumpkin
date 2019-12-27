package com.video.test.module.notice;

import com.video.test.javabean.NoticeBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class NoticePresenter extends NoticeContract.Presenter<NoticeModel> {
    private static final String TAG = "VideoListPresenter";

    @Override
    public void subscribe() {

    }


    @Override
    void getSystemNotice() {
        mView.setSwipeRefreshStatus(true);
        Disposable disposable = mModel.getSystemNotice()
                .subscribe(new Consumer<List<NoticeBean>>() {
                    @Override
                    public void accept(List<NoticeBean> noticeBeans) {
                        mView.setSwipeRefreshStatus(false);
                        mView.setSystemNotice(noticeBeans);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getSystemNotice Error" + throwable.getMessage());
                        mView.setSwipeRefreshStatus(false);
                        mView.showNetworkErrorView();
                    }
                }));
        addDisposable(disposable);
    }
}
