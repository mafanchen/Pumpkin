package com.video.test.module.collection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.CollectionBean;
import com.video.test.javabean.base.ISelectableBean;
import com.video.test.ui.widget.DividerItemDecoration;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface CollectionContract {

    interface Model extends IModel {

        Observable<String> deleteTopicCollection(String token, String tokenId, String topicArrayIds);

        Observable<String> deleteVideoCollection(String ids, String userToken, String userTokenId);

        Observable<CollectionBean> getAllCollection();
    }

    interface View extends IView {
        void getDeleteCollectionMessage(String message);

        void setUserCollection(List<ISelectableBean> collectionListBeans);

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

        void showNetworkErrorView();

        void setItemDecoration(RecyclerView.ItemDecoration decoration);

        Context getContext();

        void setLayoutManager(RecyclerView.LayoutManager manager);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getUserCollection();

        abstract void deleteCollection(String ids);
    }
}
