package com.video.test.module.videorecommend;

import com.video.test.javabean.BannerBean;
import com.video.test.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class VideoRecommendModel extends VideoRecommendContract.Model {

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
}
