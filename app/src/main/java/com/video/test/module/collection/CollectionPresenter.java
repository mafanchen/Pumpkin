package com.video.test.module.collection;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.javabean.CollectionListBean;
import com.video.test.network.ListResult;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class CollectionPresenter extends CollectionContract.Presenter<CollectionModel> {
    private static final String TAG = "CollectionPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getUserCollection(int page, int limit) {
        mView.showRefreshLayout();
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.getUserCollection(userToken, userTokenId, page, limit)
                .subscribe(collectionListBeans -> {
                    mView.setUserCollection(collectionListBeans.getList());
                    mView.hideRefreshLayout(true);
                    mView.cancelRefreshLayout(page);
                }, throwable -> {
                    LogUtils.d(TAG, "getUserCollection Error == " + throwable.getMessage());
                    mView.hideRefreshLayout(false);
                    mView.showNetworkErrorView();
                });
        addDisposable(disposable);
    }

    @Override
    void getMoreCollection(int page, int limit) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.getUserCollection(userToken, userTokenId, page, limit)
                .subscribe(new Consumer<ListResult<CollectionListBean>>() {
                    @Override
                    public void accept(ListResult<CollectionListBean> collectionListBeans) {
                        mView.addMoreCollection(collectionListBeans.getList());
                        mView.loadingComplete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.d(TAG, "getUserCollection Error == " + throwable.getMessage());
                        mView.loadingComplete();
                    }
                });
        addDisposable(disposable);

    }

    @Override
    void deleteCollection(String ids) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.deleteCollection(ids, userToken, userTokenId)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        mView.getDeleteCollectionMessage(s);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "deleteCollection Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }
}
