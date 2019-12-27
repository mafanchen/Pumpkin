package com.video.test.module.screenvideo;

import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.ScreenResultBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class ScreenVideoPresenter extends ScreenVideoContract.Presenter<ScreenVideoModel> {
    private static final String TAG = "VideoListPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getScreenResult(int page, int limnt, int videoPid, String area, String type, String year, String sort) {
        mView.showRefreshLayout();
        Disposable disposable = mModel.getScreenResult(page, limnt, videoPid, area, type, year, sort)
                .subscribe(screenResultBean -> {
                    LogUtils.d(TAG, "getScreenResult Success ");
                    mView.setVideoList(screenResultBean.getList());
                    mView.hideRefreshLayout(true);
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "getScreenResult Error : " + throwable.getMessage());
                    mView.hideRefreshLayout(false);
                }));
        addDisposable(disposable);
    }


    @Override
    void moreScreenResult(int page, int limnt, int videoPid, String area, String type, String year, String sort) {
        Disposable disposable = mModel.getScreenResult(page, limnt, videoPid, area, type, year, sort)
                .subscribe(new Consumer<ScreenResultBean>() {
                    @Override
                    public void accept(ScreenResultBean screenResultBean) {
                        LogUtils.d(TAG, "getScreenResult Success ");
                        mView.moreVideoList(screenResultBean.getList());
                        mView.loadingComplete();
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getScreenResult Error : " + throwable.getMessage());
                        mView.loadingComplete();
                    }
                }));
        addDisposable(disposable);
    }


    @Override
    void getScreenTypes() {
        Disposable disposable = mModel.getScreenTypes()
                .subscribe(new Consumer<ScreenBean>() {
                    @Override
                    public void accept(ScreenBean screenBean) {
                        LogUtils.d(TAG, "getScreenTypes success Thread == " + Thread.currentThread().getName());
                        mView.setScreenTypes(screenBean);

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getScreenTypes Error = " + throwable.getMessage());

                    }
                }));
        addDisposable(disposable);
    }
}
