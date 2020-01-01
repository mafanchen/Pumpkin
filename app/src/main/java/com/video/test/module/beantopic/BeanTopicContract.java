package com.video.test.module.beantopic;

import android.content.res.Resources;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.BeanTopicBean;
import com.video.test.javabean.BeanTopicListBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface BeanTopicContract {

    interface Model extends IModel {
        Observable<BeanTopicListBean> getHomepageBeanTopicList(int pid, int order);

    }

    interface View extends IView {
        void setHomepageBeanTopicList(List<BeanTopicBean> beanTopicBeans);

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

        void showNetworkErrorView();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getHomepageBeanTopicList(int order);

        abstract void initWeChatApi();

        abstract void share2WeChat(Resources resources, String shareUrl, int targetSession);

    }


}
