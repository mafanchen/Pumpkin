package com.video.test.module.notice;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.NoticeBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface NoticeContract {

    interface Model extends IModel {
        Observable<List<NoticeBean>> getSystemNotice();

    }

    interface View extends IView {

        void setSystemNotice(List<NoticeBean> noticeBeanList);

        void setSwipeRefreshStatus(Boolean status);

        void showNetworkErrorView();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getSystemNotice();


    }

}
