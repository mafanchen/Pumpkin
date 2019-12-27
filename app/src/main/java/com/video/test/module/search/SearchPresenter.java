package com.video.test.module.search;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.javabean.BeanTopicContentBean;
import com.video.test.javabean.FooterViewBean;
import com.video.test.javabean.HomepageVideoBean;
import com.video.test.javabean.SearchNoMoreDataBean;
import com.video.test.javabean.SearchRecommendBean;
import com.video.test.javabean.SearchResultBean;
import com.video.test.javabean.SearchResultVideoBean;
import com.video.test.javabean.SearchSortTypeBean;
import com.video.test.javabean.SearchTopicBean;
import com.video.test.javabean.event.CollectEvent;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.Items;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SearchPresenter extends SearchContract.Presenter<SearchModel> {
    private static final String TAG = "SearchPresenter";
    private List<SearchSortTypeBean> sortTypeList;
    /**
     * 当前选中的筛选条件
     */
    private String currentSortType;
    /**
     * 当前搜索的关键词
     */
    private String currentSearchWord;

    @Override
    public void subscribe() {
    }

    @Override
    public void attachView(SearchContract.View view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        EventBus.getDefault().unregister(this);
    }

    @Override
    void addCollections(String vodId) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.addCollections(vodId, userToken, userTokenId)
                .subscribe(addCollectionBean -> {
                    mView.addCollectionSuccess(addCollectionBean.getCollect_id(), vodId);
                    ToastUtils.showToast(TestApp.getContext(), "添加收藏成功");
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "addCollections Error == " + throwable.getMessage());
                    ToastUtils.showToast(TestApp.getContext(), "添加收藏失败");
                    mView.addCollectionError(vodId);
                }));
        addDisposable(disposable);
    }

    /**
     * 统计添加收藏数
     */
    private void addCollectCount(String vodId) {
        Disposable subscribe = mModel.addCollectCount(vodId)
                .subscribeOn(Schedulers.io())
                .subscribe(baseResult -> {
                }, new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, throwable.getMessage())));
        addDisposable(subscribe);
    }

    @Override
    void delCollections(String ids) {
        List<String> idList = new ArrayList<>();
        Gson gson = new Gson();
        idList.add(ids);
        String jsonList = gson.toJson(idList);
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        LogUtils.d(TAG, "delCollections json == " + idList);
        Disposable disposable = mModel.delCollections(jsonList, userToken, userTokenId)
                .subscribe(s -> {
                    ToastUtils.showToast(TestApp.getContext(), "取消收藏成功");
                    mView.removeCollected(true, ids);
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "delCollections Error " + throwable.getMessage());
                    ToastUtils.showToast(TestApp.getContext(), "取消收藏失败");
                    mView.removeCollected(false, ids);
                }));
        addDisposable(disposable);
    }

    @Override
    public void onCollect(boolean isCollect, String vodId, String collectId) {
        if (isCollect) {
            if (!TextUtils.isEmpty(vodId)) {
                addCollections(vodId);
                addCollectCount(vodId);
            }
        } else {
            if (!TextUtils.isEmpty(collectId)) {
                delCollections(collectId);
            }
        }
    }

    @Override
    void getSearchResult(String keyword, String sortType) {
        if (keyword == null) {
            return;
        }
        currentSortType = sortType;
        currentSearchWord = keyword;
        mView.setSwipeRefreshStatus(true);
        //如果没有获取到排序规则，则先获取排序规则，然后取第一个
        Observable<SearchResultBean> observable = sortTypeList == null || currentSortType == null ? mModel.getSortType()
                .flatMap((Function<List<SearchSortTypeBean>, ObservableSource<SearchResultBean>>) searchSortTypeBeans -> {
                    sortTypeList = searchSortTypeBeans;
                    //初始化筛选条件的videoGroup
                    mView.initSortRadioGroup(sortTypeList);
                    //将第一个筛选条件设为默认
                    currentSortType = sortTypeList.get(0).getValue();
                    return mModel.getSearchResult(currentSearchWord, currentSortType);
                }) : mModel.getSearchResult(currentSearchWord, currentSortType);
        Disposable disposable = observable.subscribe(searchResultBean -> {
            mView.setSwipeRefreshStatus(false);
            searchSuccess(searchResultBean);
        }, throwable -> {
            mView.setSwipeRefreshStatus(false);
            mView.showNetworkErrorView();
            LogUtils.d(TAG, "getSearchResult  Error == " + throwable.getMessage());
        });
        addDisposable(disposable);
    }

    private void searchSuccess(SearchResultBean resultBean) {
        Items items = new Items();
        List<SearchResultVideoBean> searchVideoList = resultBean.getList();
        items.addAll(searchVideoList);
        if (searchVideoList.isEmpty()) {
            mView.hideSortType();
        } else {
            mView.showSortType();
        }
        //少于两条显示推荐视频和专题
        if (searchVideoList.size() <= 1) {
            addRecommend(resultBean, items);
        } else {
            addFooter(items);
        }
        mView.setSearchResult(items, currentSearchWord);
    }

    private void addRecommend(SearchResultBean resultBean, Items items) {
        SearchRecommendBean recommendBean = resultBean.getRecommendBean();
        HomepageVideoBean recommendVideoListBean = recommendBean.getRecommendVideoListBean();

        String noMoreDataStr = null;
        if (items.isEmpty()) {
            String searchWord = null;
            if (currentSearchWord.length() > 10) {
                searchWord = currentSearchWord.substring(0, 3) + "...";
            } else {
                searchWord = currentSearchWord;
            }
            noMoreDataStr = "暂无" + "\"" + searchWord + "\"" + "搜索结果 小编为您推荐";
        } else {
            noMoreDataStr = "暂无更多 小编为您推荐";
        }
        //暂无更多小编为您推荐
        SearchNoMoreDataBean noMoreDataBean = new SearchNoMoreDataBean(noMoreDataStr);
        items.add(noMoreDataBean);
        //推荐视频
        List recommendVideoList = recommendVideoListBean.getList();
        items.addAll(recommendVideoList);
        //专题列表
        List<BeanTopicContentBean> topicList = recommendBean.getTopicList();
        if (topicList != null && !topicList.isEmpty()) {
            topicList.set(0, new SearchTopicBean(topicList.get(0), true));
            items.addAll(topicList);
        }
    }

    private void addFooter(Items items) {
        int size = items.size();
        if (size > 0 && !(items.get(size - 1) instanceof FooterViewBean)) {
            items.add(new FooterViewBean());
        }
    }


    private void initSortType(List<SearchSortTypeBean> sortTypeList) {
        if (sortTypeList == null) {
            return;
        }
        this.sortTypeList = sortTypeList;
        //初始化筛选条件的videoGroup
        mView.initSortRadioGroup(sortTypeList);
        //将第一个筛选条件设为默认
        currentSortType = sortTypeList.get(0).getValue();
    }

    @Override
    void getSearchResult(String keyword) {
        if (keyword != null) {
            //这里每次重新输入关键词搜索时，要初始化筛选条件
            initSortType(sortTypeList);
            getSearchResult(keyword, currentSortType);
        }
    }

    @Override
    void getSearchResult(SearchSortTypeBean sortType) {
        if (sortType != null) {
            getSearchResult(currentSearchWord, sortType.getValue());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandleCollectEvent(CollectEvent event) {
        if (event.isCollect()) {
            mView.addCollectionSuccess(event.getCollectId(), event.getVodId());
        } else {
            mView.removeCollected(true, event.getCollectId());
        }
    }

}
