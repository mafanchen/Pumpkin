package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Reus
 * 获取兑换会员列表返回的实体类
 */
public class ShareExchangeListBean {
    /**
     * 已经分享的好友个数
     */
    @SerializedName("share_num")
    private int shareNum;
    /**
     * 兑换会员列表
     */
    @SerializedName("share_exchange")
    private List<ShareExchangeBean> shareExchange;

    public int getShareNum() {
        return shareNum;
    }

    public void setShareNum(int shareNum) {
        this.shareNum = shareNum;
    }

    public List<ShareExchangeBean> getShareExchange() {
        return shareExchange;
    }

    public void setShareExchange(List<ShareExchangeBean> shareExchange) {
        this.shareExchange = shareExchange;
    }

    public static class ShareExchangeBean {
        private String id;

        @SerializedName("order_num")
        private int orderNum;
        /**
         * 会员名称
         */
        @SerializedName("share_name")
        private String shareName;
        /**
         * 需要邀请的人数
         */
        @SerializedName("share_set_num")
        private int shareSetNum;
        /**
         * 会员时间（月数）
         */
        @SerializedName("share_set_time")
        private int shareSetTime;
        /**
         * 图片地址
         */
        @SerializedName("share_set_pic")
        private String shareSetPic;

        public int getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        public String getShareName() {
            return shareName;
        }

        public void setShareName(String shareName) {
            this.shareName = shareName;
        }

        public int getShareSetNum() {
            return shareSetNum;
        }

        public void setShareSetNum(int shareSetNum) {
            this.shareSetNum = shareSetNum;
        }

        public int getShareSetTime() {
            return shareSetTime;
        }

        public void setShareSetTime(int shareSetTime) {
            this.shareSetTime = shareSetTime;
        }

        public String getShareSetPic() {
            return shareSetPic;
        }

        public void setShareSetPic(String shareSetPic) {
            this.shareSetPic = shareSetPic;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
