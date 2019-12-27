package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Enoch Created on 2018/7/30.
 */
public class VideoListBean {

    private String title;
    private int listCount;
    private List<VideoBean> list;
    //专题图片
    @SerializedName("zt_pic")
    private String ztPic;
    @SerializedName("zt_detail")
    private String ztDetail;
    //专题描述

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }

    public List<VideoBean> getList() {
        return list;
    }

    public void setList(List<VideoBean> list) {
        this.list = list;
    }

    public String getZtPic() {
        return ztPic;
    }

    public void setZtPic(String ztPic) {
        this.ztPic = ztPic;
    }

    public String getZtDetail() {
        return ztDetail;
    }

    public void setZtDetail(String ztDetail) {
        this.ztDetail = ztDetail;
    }
}
