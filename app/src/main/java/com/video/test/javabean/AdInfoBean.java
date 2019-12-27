package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * 广告位
 *
 * @author : AhhhhDong
 * @date : 2019/3/28 17:49
 */
public class AdInfoBean {
    private String id;

    private String type;

    @SerializedName("ad_pic")
    private String adPic;

    @SerializedName("ad_url")
    private String adUrl;

    @SerializedName("android_url")
    private String androidUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdPic() {
        return adPic;
    }

    public void setAdPic(String adPic) {
        this.adPic = adPic;
    }

    public String getAdUrl() {
        return adUrl;
    }

    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    public String getAndroidUrl() {
        return androidUrl;
    }

    public void setAndroidUrl(String androidUrl) {
        this.androidUrl = androidUrl;
    }

    public interface Type {
        String WEB = "1";

        String DOWNLOAD = "2";
    }
}
