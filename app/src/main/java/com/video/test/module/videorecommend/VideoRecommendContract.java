package com.video.test.module.videorecommend;

import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.module.videotype.BaseVideoTypeListPresenter;
import com.video.test.module.videotype.VideoTypeListContract;
import com.video.test.module.videotype.VideoTypeListModel;

import java.util.List;


/**
 * @author Enoch Created on 2018/6/27.
 */
public interface VideoRecommendContract {

    abstract class Model extends VideoTypeListModel {
    }

    interface View extends VideoTypeListContract.View {

        void initNotice(List<HomePageNoticeBean> noticeList);
    }

    abstract class Presenter<M extends Model> extends BaseVideoTypeListPresenter<M, View> {

    }

}
