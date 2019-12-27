package com.video.test.javabean;

import java.util.List;

/**
 * 首页各中视频列表页面获取视频列表返回的数据
 * api : /App/Index/newIndex?pid=
 *
 * @author : AhhhhDong
 * @date : 2019/3/28 16:55
 */
public class HomePageVideoListBean {

    private List<HomepageVideoBean> vod;

    public List<HomepageVideoBean> getVod() {
        return vod;
    }

    public void setVod(List<HomepageVideoBean> vod) {
        this.vod = vod;
    }
}
