package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;
import com.video.test.javabean.base.ISelectableBean;

import org.jetbrains.annotations.NotNull;

/**
 * @author Enoch Created on 2018/7/27.
 */
public class CollectionListBean implements ISelectableBean {

    /**
     * id : 23
     * vod_id : 12334
     * vod_pic : http://www.ffpic.net/vod/2015-04/5541f7ad909e3.jpg
     * vod_name : 三十九级台阶
     */

    private String id;
    private String vod_id;
    private String vod_pic;
    private String vod_name;
    // 收藏资源的类型  1 电影  2电视剧  3综艺 4动漫 5 专题
    @SerializedName("t_id")
    private String vodType;
    private boolean isSelected;


    public String getVodType() {
        return vodType;
    }

    public void setVodType(String vodType) {
        this.vodType = vodType;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVod_id() {
        return vod_id;
    }

    public void setVod_id(String vod_id) {
        this.vod_id = vod_id;
    }

    public String getVod_pic() {
        return vod_pic;
    }

    public void setVod_pic(String vod_pic) {
        this.vod_pic = vod_pic;
    }

    public String getVod_name() {
        return vod_name;
    }

    public void setVod_name(String vod_name) {
        this.vod_name = vod_name;
    }

    @NotNull
    @Override
    public String toString() {
        return "CollectionListBean{" +
                "id='" + id + '\'' +
                ", vod_id='" + vod_id + '\'' +
                ", vod_pic='" + vod_pic + '\'' +
                ", vod_name='" + vod_name + '\'' +
                ", getIsSelected=" + isSelected +
                '}';
    }
}
