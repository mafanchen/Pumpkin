package com.video.test.module.download;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.M3U8DownloadBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface DownloadContract {

    interface Model extends IModel {
        Observable<Long> getSDCardFreeSize();

        Observable<String> getSDCardTotalSize();

        Observable<Integer> getSDCardUsedPercent();


    }

    interface View extends IView {

        void showNoCacheBackground();

        void hideNoCacheBackground();

        void showCleanCacheDialog();

        void setDownloadBeans(List<Object> downloadBeans);

        void showEditBtn();

        void hideEditBtn();

        void showToast(String text);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getSDCardFreeSize();

        abstract void getM3U8Tasks(String userLevel);

        abstract void initM3U8Listener();

//        abstract void deleteM3U8Task(String taskUrl, List<Object> taskList);

        abstract void pauseAllTasks();

        public abstract void deleteM3U8Task(List<Object> taskList, String... taskUrl);

        abstract void startM3U8Task(M3U8DownloadBean m3U8DownloadingBean);

        abstract void pauseM3U8Task(M3U8DownloadBean m3U8DownloadingBean);


    }

}
