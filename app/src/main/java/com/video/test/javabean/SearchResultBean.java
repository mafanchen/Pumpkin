package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Enoch Created on 2018/8/5.
 */
public class SearchResultBean {

    private List<SearchResultVideoBean> list;

    @SerializedName("czList")
    private SearchRecommendBean recommendBean;

    public List<SearchResultVideoBean> getList() {
        return list;
    }

    public void setList(List<SearchResultVideoBean> list) {
        this.list = list;
    }

    public SearchRecommendBean getRecommendBean() {
        return recommendBean;
    }

    public void setRecommendBean(SearchRecommendBean recommendBean) {
        this.recommendBean = recommendBean;
    }
}

