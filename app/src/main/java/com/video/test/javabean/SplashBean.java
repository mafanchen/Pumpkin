package com.video.test.javabean;

import java.io.Serializable;

/**
 * @author Enoch Created on 2018/10/24.
 */
public class SplashBean implements Serializable {

    private String id;
    private String pic_url;
    private String jump_url;
    private String ad_name;
    private int show_time;     // 系统可以在后排配置默认时间  默认单位为秒
    private String open_status;
    /**
     * 进入后台xx秒后，进入前台展示广告
     */
    private int count_time;


    public int getShow_time() {
        return show_time;
    }

    public void setShow_time(int show_time) {
        this.show_time = show_time;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getOpen_status() {
        return open_status;
    }

    public void setOpen_status(String open_status) {
        this.open_status = open_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount_time() {
        return count_time;
    }

    public void setCount_time(int count_time) {
        this.count_time = count_time;
    }
}
