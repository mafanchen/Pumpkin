package com.video.test.module.videorecommend;

import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.BannerBean;
import com.video.test.javabean.HomePageNoticeBean;
import com.video.test.javabean.HomepageVideoBean;
import com.video.test.javabean.VideoTitleBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoRecommendPresenter extends VideoRecommendContract.Presenter<VideoRecommendModel> {

    private static final String TAG = "VideoRecommendPresenter";

    @Override
    public void subscribe() {

    }

    /**
     * 获取banner和通知
     */
    @Override
    void getBannerAndNotice() {
        Disposable disposable = mModel.getBannerAndNotice()
                .subscribe(this::setBannerAndNotice,
                        new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, "getBannerAndNotice Error == " + throwable.getMessage())));
        addDisposable(disposable);
    }

    /**
     * 根据获取到的数据，加载页面的banner和通知
     *
     * @param data 获取到的banner和通知数据
     */
    private void setBannerAndNotice(BannerAndNoticeListBean data) {
        //banner
        List<BannerBean> bannerList = data.getBannerList();
        if (bannerList != null) {
            int count = bannerList.size();
            List<String> picUrls = new ArrayList<>(count);
            List<String> bannerContent = new ArrayList<>(count);
            for (BannerBean bannerBean : bannerList) {
                picUrls.add(bannerBean.getSlidePic());
                bannerContent.add(bannerBean.getBanner_content());
                LogUtils.i(TAG, "picUrls == " + bannerBean.getSlidePic() + " vodId == " + bannerBean.getVodId() + " bannerContent : " + bannerBean.getBanner_content());
            }
            mView.initBanner(picUrls, bannerContent, bannerList);
        }
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
