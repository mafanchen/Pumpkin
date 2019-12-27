package com.video.test.javabean;

import java.util.List;

/**
 * @author Enoch Created on 2018/8/1.
 */
public class PlayerVideoListBean {

    private String vod_id;
    private String vod_year;
    private String vod_name;
    private String vod_scroe;
    private String vod_keywords;
    private String vod_addtime;
    private String vod_area;
    private String vod_actor;
    private String vod_director;
    private String vod_pic;
    private String vod_use_content;
    private String vod_douban_name;
    private List<PlayerUrlListBean> vod_urlArr;


    public String getVod_year() {
        return vod_year;
    }

    public void setVod_year(String vod_year) {
        this.vod_year = vod_year;
    }

    public String getVod_pic() {
        return vod_pic;
    }

    public void setVod_pic(String vod_pic) {
        this.vod_pic = vod_pic;
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

    public String getVod_addtime() {
        return vod_addtime;
    }

    public void setVod_addtime(String vod_addtime) {
        this.vod_addtime = vod_addtime;
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

    public String getVod_use_content() {
        return vod_use_content;
    }

    public void setVod_use_content(String vod_use_content) {
        this.vod_use_content = vod_use_content;
    }

    public List<PlayerUrlListBean> getVod_urlArr() {
        return vod_urlArr;
    }

    public void setVod_urlArr(List<PlayerUrlListBean> vod_urlArr) {
        this.vod_urlArr = vod_urlArr;
    }

}
