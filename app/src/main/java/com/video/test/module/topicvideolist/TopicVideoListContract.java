package com.video.test.module.topicvideolist;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.VideoBean;
import com.video.test.javabean.VideoListBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface TopicVideoListContract {

    interface Model extends IModel {

        Observable<VideoListBean> getVideoList(int pid, String tag, String type, int page, int limit);


    }

    interface View extends IView {

        void setVideoList(List<VideoBean> videoList);

        void setPageTitle(String titleName);

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

        void setPic(String ztPic);

        void setContent(String ztDetail);

        void setTopicNum();

        void showNetworkErrorView();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getVideoList(int pid, String tag, String type);

    }


}
