package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;
import com.video.test.javabean.base.IPageJumpBean;

/**
 * 主页通知
 *
 * @author : AhhhhDong
 * @date : 2019/3/28 10:37
 * @since v1.2.0
 */
public class HomePageNoticeBean implements IPageJumpBean {
    private String id;
    /**
     * 通知图片
     */
    @SerializedName("notice_pic")
    private String pic;
    /**
     * 通知文本内容
     */
    @SerializedName("notice_cont")
    private String content;
    @SerializedName("type")
    private String type;
    @SerializedName("vod_id")
    private String vodId;
    @SerializedName("web_url")
    private String webUrl;
    @SerializedName("android_router")
    private String androidRouter;
    /**
     * 专题
     */
    @SerializedName("zt_id")
    private String ztId;
    @SerializedName("zt_router")
    private BannerTopicBean ztRouter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getVodId() {
        return vodId;
    }

    @Override
    public BannerTopicBean getTopicRouter() {
        return getZtRouter();
    }

    @Override
    public String getTargetName() {
        return "";
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String getAndroidRouter() {
        return androidRouter;
    }

    public void setAndroidRouter(String androidRouter) {
        this.androidRouter = androidRouter;
    }

    public String getZtId() {
        return ztId;
    }

    public void setZtId(String ztId) {
        this.ztId = ztId;
    }

    public BannerTopicBean getZtRouter() {
        return ztRouter;
    }

    public void setZtRouter(BannerTopicBean ztRouter) {
        this.ztRouter = ztRouter;
    }
}
