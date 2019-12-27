package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author Enoch Created on 2018/7/27.
 */
public class UserCenterBean implements Serializable {


    /**
     * user_id : 7
     * username : 豆-86-4241
     * is_vip : 1
     * vip_time :
     * pic :
     * email : guoyangd@gmail.com
     * country_code : 86
     * mobile : 17760014241
     */

    private String user_id;
    private String username;
    private String is_vip;
    private String vip_time;
    private String pic;
    private String email;
    private String country_code;
    private String mobile;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("share_num")
    private String shareNum;
    @SerializedName("total_num")
    private String totalNum;


    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getShareNum() {
        return shareNum;
    }

    public void setShareNum(String shareNum) {
        this.shareNum = shareNum;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}
