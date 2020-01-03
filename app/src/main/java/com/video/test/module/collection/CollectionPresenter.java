package com.video.test.module.collection;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.CollectionBean;
import com.video.test.javabean.base.ISelectableBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class CollectionPresenter extends CollectionContract.Presenter<CollectionModel> {
    private static final String TAG = "CollectionPresenter";
    private int currentPageType = PAGE_TYPE_MOVIE;
    private static final int PAGE_TYPE_MOVIE = 1;
    private static final int PAGE_TYPE_TELEPLAY = 2;
    private static final int PAGE_TYPE_VARIETY = 3;
    private static final int PAGE_TYPE_CARTOON = 4;
    private static final int PAGE_TYPE_TOPIC = 5;
    private CollectionBean collectionBean;

    @Override
    public void subscribe() {

    }

    @Override
    void getUserCollection() {
        mView.showRefreshLayout();
        Disposable disposable = mModel.getAllCollection()
                .subscribe(collectionBean -> {
                    mView.hideRefreshLayout(true);
                    getCollectionBeanSuccess(collectionBean);
                }, throwable -> {
                    LogUtils.d(TAG, "getUserCollection Error == " + throwable.getMessage());
                    mView.hideRefreshLayout(false);
                    mView.showNetworkErrorView();
                });
        addDisposable(disposable);
    }

    private void getCollectionBeanSuccess(CollectionBean collectionBean) {
        this.collectionBean = collectionBean;
        changePage(currentPageType);
    }

    public void changePage(int pageType) {
        currentPageType = pageType;
        //这里需要切换recyclerView的layoutManager以及divider
        RecyclerView.ItemDecoration decoration;
        RecyclerView.LayoutManager manager;
        if (currentPageType == PAGE_TYPE_TOPIC) {
            manager = new LinearLayoutManager(mView.getContext());
            decoration =
                    new android.support.v7.widget.DividerItemDecoration(TestApp.getContext(), android.support.v7.widget.DividerItemDecoration.VERTICAL);
            Drawable drawable = ContextCompat.getDrawable(mView.getContext(), R.drawable.shape_bg_item_divider_1dp);
            assert drawable != null;
            ((android.support.v7.widget.DividerItemDecoration) decoration).setDrawable(drawable);
            mView.setItemDecoration(decoration);
        } else {
            manager = new GridLayoutManager(mView.getContext(), 3);
            int leftRight = PixelUtils.dp2px(mView.getContext(), 3);
            decoration = new DividerItemDecoration(leftRight, leftRight, Color.WHITE);
        }
        mView.setLayoutManager(manager);
        mView.setItemDecoration(decoration);
        //筛选
        List<ISelectableBean> list = filterCollection(pageType);
        mView.setUserCollection(list);
    }

    private List<ISelectableBean> filterCollection(int pageType) {
        if (collectionBean == null) {
            return new ArrayList<>();
        }
        ArrayList<ISelectableBean> list = new ArrayList<>();
        switch (pageType) {
            case PAGE_TYPE_MOVIE:
                list.addAll(collectionBean.getMovieList());
                break;
            case PAGE_TYPE_TELEPLAY:
                list.addAll(collectionBean.getTeleplayList());
                break;
            case PAGE_TYPE_VARIETY:
                list.addAll(collectionBean.getVarietyShowList());
                break;
            case PAGE_TYPE_CARTOON:
                list.addAll(collectionBean.getCartoonList());
                break;
            case PAGE_TYPE_TOPIC:
                list.addAll(collectionBean.getTopicList());
                break;
        }
        return list;
    }

    @Override
    void deleteCollection(String ids) {
        //这里要根据页面类型判断是删除视频还是专题
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Observable<String> observable = currentPageType == PAGE_TYPE_TOPIC ?
                mModel.deleteTopicCollection(userToken, userTokenId, ids) : mModel.deleteVideoCollection(ids, userToken, userTokenId);
        Disposable disposable = observable
                .subscribe(
                        s -> mView.getDeleteCollectionMessage(s),
                        new RxExceptionHandler<>(throwable ->
                                LogUtils.e(TAG, "deleteVideoCollection Error == " + throwable.getMessage())
                        )
                );
        addDisposable(disposable);
    }
}
