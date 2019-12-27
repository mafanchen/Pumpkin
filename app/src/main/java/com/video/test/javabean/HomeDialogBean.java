package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;
import com.video.test.javabean.base.IPageJumpBean;

/**
 * @author Enoch Created on 2018/11/9.
 */
public class HomeDialogBean implements IPageJumpBean {

    public static final int ACTIVITY_TYPE_IMAGE = 1;
    public static final int ACTIVITY_TYPE_TEXT = 2;

    private String id;
    @SerializedName("activity_name")
    private String name;
    @SerializedName("activity_pic")
    private String pic;
    private String type;
    @SerializedName("activity_type")
    private int activityType;
    @SerializedName("order_num")
    private String orderNum;
    @SerializedName("vod_id")
    private String vodId;
    @SerializedName("web_url")
    private String webUrl;
    @SerializedName("android_router")
    private String androidRouter;
    @SerializedName("zt_router")
    private BannerTopicBean ztRouter;
    @SerializedName("txt_title")
    private String title;
    @SerializedName("txt_cont")
    private String content;
    @SerializedName("txt_but")
    private String button;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAndroidRouter() {
        return androidRouter;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }

    @Override
    public String getVodId() {
        return vodId;
    }

    @Override
    public BannerTopicBean getTopicRouter() {
        return ztRouter;
    }

    @Override
    public String getTargetName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setVodId(String vodId) {
        this.vodId = vodId;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setAndroidRouter(String androidRouter) {
        this.androidRouter = androidRouter;
    }

    public BannerTopicBean getZtRouter() {
        return ztRouter;
    }

    public void setZtRouter(BannerTopicBean ztRouter) {
        this.ztRouter = ztRouter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }
}
