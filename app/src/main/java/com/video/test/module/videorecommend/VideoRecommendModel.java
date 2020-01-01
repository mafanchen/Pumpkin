package com.video.test.module.videorecommend;

import com.video.test.AppConstant;
import com.video.test.javabean.BannerAndNoticeListBean;
import com.video.test.javabean.BannerBean;
import com.video.test.javabean.HomePageVideoListBean;
import com.video.test.network.BaseResult;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxSchedulers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoRecommendModel implements VideoRecommendContract.Model {

    @NotNull
    @Override
    public Observable<HomePageVideoListBean> getHomepageVideoList(int pid) {
        return RetrofitHelper.getInstance()
                .getHomepage(pid)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<BannerAndNoticeListBean> getBannerAndNotice() {
        return RetrofitHelper.getInstance()
                // 热门栏目Banner 获取广告的pid 为1
                .getBannerAndNotice(AppConstant.VIDEO_LIST_PID_HOT)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());

    }

    private List<String> getImageUrls(List<BannerBean> gameBanners) {
        ArrayList<String> imageUrls = new ArrayList<>();
        for (BannerBean bannerBean : gameBanners) {
            String picUrl = bannerBean.getSlidePic();
            imageUrls.add(picUrl);
        }
        LogUtils.d("VideoRecommendModel", "getImageUrls==" + imageUrls.toString());
        return imageUrls;
    }

    private List<String> getImageIds(List<BannerBean> gameBanners) {
        ArrayList<String> imageIds = new ArrayList<>();
        for (BannerBean bannerBean : gameBanners) {
            String picUrl = bannerBean.getVodId();
            imageIds.add(picUrl);
        }
        LogUtils.d("VideoRecommendModel", "imageIds==" + imageIds.toString());
        return imageIds;
    }

    @NotNull
    @Override
    public Observable<BaseResult<Object>> addAdInfo(int adType, @NotNull String adId) {
        return RetrofitHelper.getInstance()
                .addAdInfo(adType,adId);
    }
}
