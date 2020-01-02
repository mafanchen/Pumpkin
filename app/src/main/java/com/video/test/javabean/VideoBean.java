package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/7/26.
 */
public class VideoBean {
    /**
     * vod_id : 33
     * vod_pic : http://www.ffpic.net/vod/2018-04/5ad429a637bc1.jpg
     * vod_name : 极限挑战第四季
     * vod_scroe : 8.0
     * vod_keywords : 真人秀
     * vod_area : 大陆
     */

    private String vod_id;
    private String vod_pic;
    private String vod_name;
    private String vod_scroe;
    private String vod_year;
    private String vod_keywords;
    private String vod_area;
    private String vod_douban_name;
    /**
     * 1.返回值为 0   返回这个值,代表的该资源 不是连载资源,一般都是电影这种资源,如果遇到参数为0, 右下方位置,展示  豆瓣评分设计即可
     * 2. 返回的是一个 十位数或者百位数,千位数(上1000集的电影), 这种情况 该资源基本上都是 电视剧,连载动漫的情况,  右下方的位置,展示  更新至  xxxx 集 即可.
     * 3. 返回的是一个 8位数 的日期 长度 如 20191111  ,这种情况,该资源 是一个综艺类的节目, 右下方的位置,展示  更新至 xxxxxxxx 期  即可.
     */
    private String vod_continu;

    // 电影资源类型
    @SerializedName("d_total")
    private String vodTotal;

    // 资源是否已经完结  完结为 true 未完结 为false
    @SerializedName("is_end")
    private boolean vodIsEnd;

    @SerializedName("pid")
    private int vodPid;

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

    public boolean isVodIsEnd() {
        return vodIsEnd;
    }

    public void setVodIsEnd(boolean vodIsEnd) {
        this.vodIsEnd = vodIsEnd;
    }

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

    public String getVod_scroe() {
        return vod_scroe;
    }

    public void setVod_scroe(String vod_scroe) {
        this.vod_scroe = vod_scroe;
    }

    public String getVod_keywords() {
        return vod_keywords;
    }

    public void setVod_keywords(String vod_keywords) {
        this.vod_keywords = vod_keywords;
    }

    public String getVod_area() {
        return vod_area;
    }

    public void setVod_area(String vod_area) {
        this.vod_area = vod_area;
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

    @Override
    public String toString() {
        return "VideoBean{" +
                "vod_id='" + vod_id + '\'' +
                ", vod_pic='" + vod_pic + '\'' +
                ", vod_name='" + vod_name + '\'' +
                ", vod_scroe='" + vod_scroe + '\'' +
                ", vod_keywords='" + vod_keywords + '\'' +
                ", vod_area='" + vod_area + '\'' +
                '}';
    }
}
