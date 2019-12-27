package com.video.test.javabean;

import com.video.test.javabean.base.ISelectableBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author Enoch Created on 2018/7/27.
 */
@Entity
public class HistoryListBean implements ISelectableBean {

    /**
     * id : 49
     * vod_id : 12350
     * vod_name : 我盛大的希腊婚礼2
     * vod_pic : http://www.ffpic.net/vod/2016-05/572bf7db2a741.jpg
     * play_title : 测试视频2
     * play_url : ffhd://testurl2
     * play_degree : 1.00%
     * nowtime : 10000
     * totaltime : 1000000
     */

    @Transient
    private String id;
    @Unique
    private String vod_id;
    private String vod_name;
    private String vod_pic;
    private String play_title;
    private String play_url;
    private String play_degree;
    private String nowtime;
    private String totaltime;
    @Transient
    private boolean isSelected;

    @Generated(hash = 1918243466)
    public HistoryListBean(String vod_id, String vod_name, String vod_pic,
            String play_title, String play_url, String play_degree, String nowtime,
            String totaltime) {
        this.vod_id = vod_id;
        this.vod_name = vod_name;
        this.vod_pic = vod_pic;
        this.play_title = play_title;
        this.play_url = play_url;
        this.play_degree = play_degree;
        this.nowtime = nowtime;
        this.totaltime = totaltime;
    }

    @Generated(hash = 1196187479)
    public HistoryListBean() {
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

    public String getPlay_title() {
        return play_title;
    }

    public void setPlay_title(String play_title) {
        this.play_title = play_title;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getPlay_degree() {
        return play_degree;
    }

    public void setPlay_degree(String play_degree) {
        this.play_degree = play_degree;
    }

    public String getNowtime() {
        return nowtime;
    }

    public void setNowtime(String nowtime) {
        this.nowtime = nowtime;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    @Override
    public String toString() {
        return "HistoryListBean{" +
                "id='" + id + '\'' +
                ", vod_id='" + vod_id + '\'' +
                ", vod_name='" + vod_name + '\'' +
                ", play_title='" + play_title + '\'' +
                ", getIsSelected=" + isSelected +
                '}';
    }
}
