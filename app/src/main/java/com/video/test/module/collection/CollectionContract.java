package com.video.test.module.collection;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.CollectionListBean;
import com.video.test.network.ListResult;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface CollectionContract {

    interface Model extends IModel {
        Observable<ListResult<CollectionListBean>> getUserCollection(String userToken, String userTokenId, int page, int limit);

        Observable<String> deleteCollection(String ids, String userToken, String userTokenId);
    }

    interface View extends IView {
        void getDeleteCollectionMessage(String message);

        void setUserCollection(List<CollectionListBean> collectionListBeans);

        void addMoreCollection(List<CollectionListBean> collectionListBeans);

        void loadingComplete();

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

        void cancelRefreshLayout(int page);

        void showNetworkErrorView();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getUserCollection(int page, int limit);

        abstract void getMoreCollection(int page, int limit);

        abstract void deleteCollection(String ids);
    }
}
