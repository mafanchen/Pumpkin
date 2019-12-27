package com.video.test.module.videorecommend;

import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.BannerBean;
import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.module.videotype.BaseVideoTypeListPresenter;
import com.video.test.module.videotype.VideoTypeListContract;

import java.util.List;

import io.reactivex.Observable;


/**
 * @author Enoch Created on 2018/6/27.
 */
public interface VideoRecommendContract {

    interface Model extends VideoTypeListContract.Model {
        Observable<BannerAndNoticeListBean> getBannerAndNotice();
    }

    interface View extends VideoTypeListContract.View {

        void initBanner(List<String> bannerList, List<String> bannerContent, List<BannerBean> bannerBeanList);

        void initNotice(List<HomePageNoticeBean> noticeList);
    }

    abstract class Presenter<M extends Model> extends BaseVideoTypeListPresenter<M, View> {

        abstract void getBannerAndNotice();
    }

}
