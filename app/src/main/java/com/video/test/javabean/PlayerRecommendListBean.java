package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/8/1.
 */
public class PlayerRecommendListBean {
    private String vod_id;
    private String vod_name;
    private String vod_pic;
    private String vod_scroe;


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

    @Override
    public String toString() {
        return "PlayerRecommendListBean{" +
                "vod_id='" + vod_id + '\'' +
                ", vod_name='" + vod_name + '\'' +
                ", vod_pic='" + vod_pic + '\'' +
                ", vod_scroe='" + vod_scroe + '\'' +
                '}';
    }
}
