package com.video.test.module.videorecommend;

import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.javabean.HomepageVideoBean;
import com.video.test.javabean.VideoTitleBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoRecommendPresenter extends VideoRecommendContract.Presenter<VideoRecommendModel> {

    private static final String TAG = "VideoRecommendPresenter";

    @Override
    public void subscribe() {

    }

    /**
     * 根据获取到的数据，加载页面的banner和通知
     *
     * @param data 获取到的banner和通知数据
     */
    @Override
    protected void setBannerAndNotice(BannerAndNoticeListBean data) {
        super.setBannerAndNotice(data);
        //通知
        List<HomePageNoticeBean> noticeList = data.getNoticeList();
        mView.initNotice(noticeList);
    }


    @NotNull
    @Override
    protected VideoTitleBean buildTitle(@NotNull HomepageVideoBean homepageVideoBean) {
        VideoTitleBean titleBean = super.buildTitle(homepageVideoBean);
        titleBean.setColumnType(VideoTitleBean.TYPE_HOTEST);
        return titleBean;
    }
}
