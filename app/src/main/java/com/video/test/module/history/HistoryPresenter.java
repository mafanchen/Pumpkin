package com.video.test.module.history;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.javabean.HistoryListBean;
import com.video.test.network.ListResult;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class HistoryPresenter extends HistoryContract.Presenter<HistoryModel> {
    private static final String TAG = "HistoryPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getUserHistory(int page, int limit) {
        mView.showRefreshLayout();
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.getUserHistory(userToken, userTokenId, page, limit)
                .subscribe(new Consumer<ListResult<HistoryListBean>>() {
                    @Override
                    public void accept(ListResult<HistoryListBean> result) {
                        mView.getUserHistory(result.getList());
                        mView.hideRefreshLayout(true);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getUserHistory Error == " + throwable.getMessage());
                        mView.hideRefreshLayout(false);
                        mView.showNetworkErrorView();
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void getMoreHistory(int page, int limit) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.getUserHistory(userToken, userTokenId, page, limit)
                .subscribe(new Consumer<ListResult<HistoryListBean>>() {
                    @Override
                    public void accept(ListResult<HistoryListBean> result) {
                        mView.addMoreHistory(result.getList());
                        mView.loadingComplete();
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getUserHistory Error == " + throwable.getMessage());
                        mView.loadingComplete();
                    }
                }));
        addDisposable(disposable);

    }

    @Override
    void deleteHistory(String ids) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.deleteHistory(ids, userToken, userTokenId)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        mView.getDeleteHistoryMessage(s);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "deleteHistory Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }
}
