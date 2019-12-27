package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/11/27.
 */
public class VideoCommentBean {

    private String vod_comment;
    private String user_id;
    private String add_time;
    private String username;
    private String vip_time;
    private String pic;
    private int is_vip;

    public String getVod_comment() {
        return vod_comment;
    }

    public void setVod_comment(String vod_comment) {
        this.vod_comment = vod_comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVip_time() {
        return vip_time;
    }

    public void setVip_time(String vip_time) {
        this.vip_time = vip_time;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    @Override
    public String toString() {
        return "VideoCommentBean{" +
                "vod_comment='" + vod_comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", add_time='" + add_time + '\'' +
                ", username='" + username + '\'' +
                ", vip_time='" + vip_time + '\'' +
                ", pic='" + pic + '\'' +
                ", is_vip=" + is_vip +
                '}';
    }
}
