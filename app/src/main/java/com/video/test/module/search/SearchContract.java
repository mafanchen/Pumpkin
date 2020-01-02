package com.video.test.module.search;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.SearchResultBean;
import com.video.test.javabean.SearchSortTypeBean;
import com.video.test.network.BaseResult;

import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import me.drakeet.multitype.Items;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface SearchContract {

    interface Model extends IModel {

        Observable<SearchResultBean> getSearchResult(String keyword, String sort);

        Observable<List<SearchSortTypeBean>> getSortType();

        Observable<AddCollectionBean> addCollections(String vodId, String userToken, String userTokenId);

        Observable<String> delCollections(String ids, String userToken, String userTokenId);

        Observable<BaseResult> addCollectCount(String vodId);

        Observable<List<String>> getAssociationWord(String searchWord);
    }

    interface View extends IView {

        void setSearchResult(Items items, String searchWord);

        void setSwipeRefreshStatus(Boolean status);

        void initVideoTypeRadioGroup(LinkedHashMap<String, Integer> videoTypeList);

        void initSortRadioGroup(List<SearchSortTypeBean> sortTypeList);

        void addCollectionSuccess(String collectId, String vodId);

        void addCollectionError(String vodId);

        void removeCollected(boolean success, String collectedId);

        void hideSortType();

        void showSortType();

        void showNetworkErrorView();

        void setAssociationWords(List<String> data);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getSearchResult(String keyword, String sortType);

        abstract void getSearchResult(String keyword);

        abstract void getSearchResult(SearchSortTypeBean sortType);

        abstract void addCollections(String vodId);

        abstract void delCollections(String ids);

        abstract void onCollect(boolean isCollect, String vodId, String collectId);

        public abstract void getAssociationWord(String searchWord);

        public abstract void filterSearchResultByVideoType(int type);
    }

}
