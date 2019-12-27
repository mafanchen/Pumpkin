package com.video.test.module.history;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.HistoryListBean;
import com.video.test.network.ListResult;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface HistoryContract {

    interface Model extends IModel {
        Observable<ListResult<HistoryListBean>> getUserHistory(String userToken, String userTokenId, int page, int limit);

        Observable<String> deleteHistory(String ids, String userToken, String userTokenId);

    }

    interface View extends IView {
        void getDeleteHistoryMessage(String message);

        void getUserHistory(List<HistoryListBean> historyListBeans);

        void addMoreHistory(List<HistoryListBean> historyListBeans);

        void loadingComplete();

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

        void showNetworkErrorView();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getUserHistory(int page, int limit);

        abstract void getMoreHistory(int page, int limit);

        abstract void deleteHistory(String ids);

    }

}
