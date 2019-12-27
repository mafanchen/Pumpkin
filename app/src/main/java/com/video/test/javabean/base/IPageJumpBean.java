package com.video.test.javabean.base;

import com.video.test.javabean.BannerTopicBean;

/**
 * 服务器返回的广告图，banner，notice等点击时，跳转的抽象接口
 *
 * @author : AhhhhDong
 * @date : 2019/3/28 15:09
 */
public interface IPageJumpBean {

    String getType();

    String getAndroidRouter();

    String getWebUrl();

    String getVodId();

    BannerTopicBean getTopicRouter();

    String getTargetName();
}
