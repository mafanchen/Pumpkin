package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/10/24.
 */
public class ShareInfoBean {
    /**
     * share_pic_url : http://imgs.jdanzhan.com/201810241658529new.jpg
     * share_detail : 独乐乐不如众乐乐，各类最新最火高清资源这里应有尽有，快动动手指，邀请小伙伴们一起观看！\n在这里，优质内容值得您拥有，现在邀请 6 位小伙伴成功注册豆瓣酱，即可获得1个月的VIP会员服务呦~
     * share_must_num : 6
     * add_time : 1973-07-10
     * share_url : http://test.beansauce.net/App/Share/share?appkey=22BK7K77E6RA2H&recommend=1
     * share_num : 2
     */

    @SerializedName("total_num")
    private String totalNum;
    @SerializedName("share_pic")
    private String sharePic;
    @SerializedName("web_url")
    private String webUrl;
    @SerializedName("share_url")
    private String shareUrl;

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getSharePic() {
        return sharePic;
    }

    public void setSharePic(String sharePic) {
        this.sharePic = sharePic;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
