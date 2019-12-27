package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;
import com.video.test.javabean.base.IPageJumpBean;

/**
 * @author Enoch Created on 2018/7/26.
 */
public class BannerBean implements IPageJumpBean {

    /**
     * slidePic : http://www.ffmovie.net/uploads/slide/2018-07-04/64b2db071398e448476476214f3ad392.png
     * vodId : 65
     */
    private String id;
    @SerializedName("slide_pic")
    private String slidePic;
    @SerializedName("zt_id")
    private String ztId;
    @SerializedName("vod_id")
    private String vodId;
    /**
     * type = 3,上报广告
     */
    private String type;
    @SerializedName("web_url")
    private String webUrl;
    private String android_router;
    @SerializedName("target_name")
    private String targetName;
    private String banner_content;
    private BannerTopicBean zt_router;

    public String getBanner_content() {
        return banner_content;
    }

    public void setBanner_content(String banner_content) {
        this.banner_content = banner_content;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAndroidRouter() {
        return getAndroid_router();
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getAndroid_router() {
        return android_router;
    }

    public void setAndroid_router(String android_router) {
        this.android_router = android_router;
    }

    public String getSlidePic() {
        return slidePic;
    }

    public void setSlidePic(String slidePic) {
        this.slidePic = slidePic;
    }

    @Override
    public String getVodId() {
        return vodId;
    }

    @Override
    public BannerTopicBean getTopicRouter() {
        return getZt_router();
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public BannerTopicBean getZt_router() {
        return zt_router;
    }

    public void setZt_router(BannerTopicBean zt_router) {
        this.zt_router = zt_router;
    }

    public String getZtId() {
        return ztId;
    }

    public void setZtId(String ztId) {
        this.ztId = ztId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
