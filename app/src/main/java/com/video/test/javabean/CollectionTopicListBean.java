package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class CollectionTopicListBean {
    @SerializedName("id")
    private String topicId;

    @SerializedName("tag")
    private String topicTag;

    @SerializedName("zt_title")
    private String topicTitle;

    @SerializedName("zt_pic")
    private String topicPic;

    @SerializedName("zt_num")
    private String topicNum;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicTag() {
        return topicTag;
    }

    public void setTopicTag(String topicTag) {
        this.topicTag = topicTag;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicPic() {
        return topicPic;
    }

    public void setTopicPic(String topicPic) {
        this.topicPic = topicPic;
    }

    public String getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(String topicNum) {
        this.topicNum = topicNum;
    }

    @NotNull
    @Override
    public String toString() {
        return "CollectionTopicListBean{" +
                "topicId='" + topicId + '\'' +
                ", topicTag='" + topicTag + '\'' +
                ", topicTitle='" + topicTitle + '\'' +
                ", topicPic='" + topicPic + '\'' +
                ", topicNum='" + topicNum + '\'' +
                '}';
    }
}
