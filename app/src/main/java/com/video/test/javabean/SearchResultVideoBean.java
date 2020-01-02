package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/7/26.
 */
public class SearchResultVideoBean {

    /**
     * vod_id : 15918
     * vod_name : 蝙蝠侠无极限：怪兽来袭
     * vod_pic : http://www.ffpic.net/vod/2016-04/571668a185e74.jpg
     * vod_scroe : 5.8
     * vod_area : 美国
     * vod_actor : 特罗伊·贝克,Brian,T.Delaney,克里斯·达玛托普拉斯
     * vod_director : Butch.Lukic
     * vod_keywords : 动作,冒险,动画
     * vod_addtime : 2018-06-08
     */

    private String vod_id;
    private String vod_year;
    private String vod_name;
    private String vod_pic;
    private String vod_scroe;
    private String vod_area;
    private String vod_actor;
    private String vod_director;
    private String vod_keywords;
    private String vod_addtime;
    private String vod_douban_name;
    private String vod_continu;
    private boolean is_collect;
    private String collect_id;
    /**
     * 视频类型，用于搜索筛选，
     * 1=>电影 , 2=>电视剧 ,3=>综艺 ,4=>动漫
     */
    @SerializedName("t_id")
    private int videoType;
    @SerializedName("is_end")
    private boolean isEnd;

    public String getVod_douban_name() {
        return vod_douban_name;
    }

    public void setVod_douban_name(String vod_douban_name) {
        this.vod_douban_name = vod_douban_name;
    }

    public String getVod_id() {
        return vod_id;
    }

    public void setVod_id(String vod_id) {
        this.vod_id = vod_id;
    }

    public String getVod_name() {
        return vod_name;
    }

    public void setVod_name(String vod_name) {
        this.vod_name = vod_name;
    }

    public String getVod_pic() {
        return vod_pic;
    }

    public void setVod_pic(String vod_pic) {
        this.vod_pic = vod_pic;
    }

    public String getVod_scroe() {
        return vod_scroe;
    }

    public void setVod_scroe(String vod_scroe) {
        this.vod_scroe = vod_scroe;
    }

    public String getVod_area() {
        return vod_area;
    }

    public void setVod_area(String vod_area) {
        this.vod_area = vod_area;
    }

    public String getVod_actor() {
        return vod_actor;
    }

    public void setVod_actor(String vod_actor) {
        this.vod_actor = vod_actor;
    }

    public String getVod_director() {
        return vod_director;
    }

    public void setVod_director(String vod_director) {
        this.vod_director = vod_director;
    }

    public String getVod_keywords() {
        return vod_keywords;
    }

    public void setVod_keywords(String vod_keywords) {
        this.vod_keywords = vod_keywords;
    }

    public String getVod_addtime() {
        return vod_addtime;
    }

    public void setVod_addtime(String vod_addtime) {
        this.vod_addtime = vod_addtime;
    }

    public String getVod_year() {
        return vod_year;
    }

    public void setVod_year(String vod_year) {
        this.vod_year = vod_year;
    }

    public String getVod_continu() {
        return vod_continu;
    }

    public void setVod_continu(String vod_continu) {
        this.vod_continu = vod_continu;
    }

    public boolean isIs_collect() {
        return is_collect;
    }

    public void setIs_collect(boolean is_collect) {
        this.is_collect = is_collect;
    }

    public String getCollect_id() {
        return collect_id;
    }

    public void setCollect_id(String collect_id) {
        this.collect_id = collect_id;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
