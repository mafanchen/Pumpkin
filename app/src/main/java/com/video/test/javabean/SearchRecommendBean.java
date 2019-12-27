package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchRecommendBean {

    @SerializedName("cData")
    private HomepageVideoBean recommendVideoListBean;

    @SerializedName("zData")
    private List<BeanTopicContentBean> topicList;

    public HomepageVideoBean getRecommendVideoListBean() {
        return recommendVideoListBean;
    }

    public void setRecommendVideoListBean(HomepageVideoBean recommendVideoListBean) {
        this.recommendVideoListBean = recommendVideoListBean;
    }

    public List<BeanTopicContentBean> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<BeanTopicContentBean> topicList) {
        this.topicList = topicList;
    }
}
