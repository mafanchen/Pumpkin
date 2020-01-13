package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * 横向推荐视频
 *
 * @author : AhhhhDong
 * @date : 2019/3/19 13:52
 */
public class VideoRecommendBean {
    /**
     * 主标题
     */
    @SerializedName("c_name")
    private String mainTitle;
    /**
     * 副标题
     */
    @SerializedName("cf_name")
    private String subTitle;
    /**
     * 图片地址
     */
    @SerializedName("c_pic")
    private String imageUrl;

    @SerializedName("vod_id")
    private String videoId;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("vod_continu")
    private String vodContinue;

    @SerializedName("vod_douban_score")
    private String vodScore;

    @SerializedName("d_total")
    private String vodTotal;

    @SerializedName("is_end")
    private boolean vodIsEnd;

    @SerializedName("pid")
    private int vodPid;

    /**
     * 资源类型 1电影 2电视剧 3综艺 4动漫
     */
    @SerializedName("t_id")
    private String vodType;

    public int getVodPid() {
        return vodPid;
    }

    public void setVodPid(int vodPid) {
        this.vodPid = vodPid;
    }

    public String getVodTotal() {
        return vodTotal;
    }

    public void setVodTotal(String vodTotal) {
        this.vodTotal = vodTotal;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getVodContinue() {
        return vodContinue;
    }

    public void setVodContinue(String vodContinue) {
        this.vodContinue = vodContinue;
    }

    public String getVodScore() {
        return vodScore;
    }

    public void setVodScore(String vodScore) {
        this.vodScore = vodScore;
    }

    public String getVodType() {
        return vodType;
    }

    public void setVodType(String vodType) {
        this.vodType = vodType;
    }

    public boolean isVodIsEnd() {
        return vodIsEnd;
    }

    public void setVodIsEnd(boolean vodIsEnd) {
        this.vodIsEnd = vodIsEnd;
    }

    @Override
    public String toString() {
        return "VideoRecommendBean{" +
                "mainTitle='" + mainTitle + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoId='" + videoId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", vodContinue='" + vodContinue + '\'' +
                ", vodScore='" + vodScore + '\'' +
                ", vodTotal='" + vodTotal + '\'' +
                ", vodIsEnd=" + vodIsEnd +
                ", vodPid=" + vodPid +
                ", vodType='" + vodType + '\'' +
                '}';
    }
}
