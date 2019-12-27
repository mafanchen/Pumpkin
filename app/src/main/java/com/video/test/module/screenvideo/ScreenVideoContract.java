package com.video.test.module.screenvideo;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.ScreenBean;
import com.video.test.javabean.ScreenResultBean;
import com.video.test.javabean.VideoBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface ScreenVideoContract {

    interface Model extends IModel {

        Observable<ScreenBean> getScreenTypes();

        Observable<ScreenResultBean> getScreenResult(int page, int limnt, int videoPid,
                                                     String area, String type, String year, String sort);

        Observable<ScreenResultBean> moreScreenResult(int page, int limnt, int videoPid,
                                                      String area, String type, String year, String sort);

    }

    interface View extends IView {

        void setVideoList(List<VideoBean> videoList);

        void moreVideoList(List<VideoBean> videoBeanList);

        void setScreenTypes(ScreenBean screenBean);

        void loadingComplete();

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {


        abstract void getScreenResult(int page, int limnt, int videoPid,
                                      String area, String type, String year, String sort);

        abstract void moreScreenResult(int page, int limnt, int videoPid,
                                       String area, String type, String year, String sort);

        abstract void getScreenTypes();
    }


}
