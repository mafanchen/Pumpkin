package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 新增接口，由原来返回的banner列表新增了一个notice
 * 接口地址 : /App/Index/newBanner
 * @author : AhhhhDong
 * @date : 2019/3/28 10:34
 * @since V1.2.0
 */
public class BannerAndNoticeListBean {
    /**
     * banner列表
     */
    @SerializedName("bannerData")
    private List<BannerBean> bannerList;
    /**
     * 通知
     */
    @SerializedName("noticeNewData")
    private List<HomePageNoticeBean> noticeList;

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }

    public List<HomePageNoticeBean> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(List<HomePageNoticeBean> noticeBean) {
        this.noticeList = noticeBean;
    }
}
