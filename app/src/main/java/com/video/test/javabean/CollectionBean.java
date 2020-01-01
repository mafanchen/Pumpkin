package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 新收藏分类别
 */
public class CollectionBean {

    @SerializedName("1")
    private List<CollectionListBean> movieList;
    @SerializedName("2")
    private List<CollectionListBean> teleplayList;
    @SerializedName("3")
    private List<CollectionListBean> varietyShowList;
    @SerializedName("4")
    private List<CollectionListBean> cartoonList;
    @SerializedName("5")
    private List<CollectionTopicListBean> topicList;

    public List<CollectionListBean> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<CollectionListBean> movieList) {
        this.movieList = movieList;
    }

    public List<CollectionListBean> getTeleplayList() {
        return teleplayList;
    }

    public void setTeleplayList(List<CollectionListBean> teleplayList) {
        this.teleplayList = teleplayList;
    }

    public List<CollectionListBean> getVarietyShowList() {
        return varietyShowList;
    }

    public void setVarietyShowList(List<CollectionListBean> varietyShowList) {
        this.varietyShowList = varietyShowList;
    }

    public List<CollectionListBean> getCartoonList() {
        return cartoonList;
    }

    public void setCartoonList(List<CollectionListBean> cartoonList) {
        this.cartoonList = cartoonList;
    }

    public List<CollectionTopicListBean> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<CollectionTopicListBean> topicList) {
        this.topicList = topicList;
    }
}
