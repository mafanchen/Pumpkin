package com.video.test.javabean;

public class UserCenterAdBean {

    /**
     * is_ad : true
     * ad_info : {"choice_id":"","type":"1","ad_pic":"https://storage.taifutj.com/admin/201912311842626new.jpg","ad_url":"http://www.baidu.com","ios_url":"","android_url":"","id":"13"}
     */

    private boolean is_ad;
    private AdInfoBean ad_info;

    public boolean isIs_ad() {
        return is_ad;
    }

    public void setIs_ad(boolean is_ad) {
        this.is_ad = is_ad;
    }

    public AdInfoBean getAd_info() {
        return ad_info;
    }

    public void setAd_info(AdInfoBean ad_info) {
        this.ad_info = ad_info;
    }

    public static class AdInfoBean {
        /**
         * choice_id :
         * type : 1
         * ad_pic : https://storage.taifutj.com/admin/201912311842626new.jpg
         * ad_url : http://www.baidu.com
         * ios_url :
         * android_url :
         * id : 13
         */

        private String choice_id;
        private String type;
        private String ad_pic;
        private String ad_url;
        private String ios_url;
        private String android_url;
        private String id;

        public String getChoice_id() {
            return choice_id;
        }

        public void setChoice_id(String choice_id) {
            this.choice_id = choice_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAd_pic() {
            return ad_pic;
        }

        public void setAd_pic(String ad_pic) {
            this.ad_pic = ad_pic;
        }

        public String getAd_url() {
            return ad_url;
        }

        public void setAd_url(String ad_url) {
            this.ad_url = ad_url;
        }

        public String getIos_url() {
            return ios_url;
        }

        public void setIos_url(String ios_url) {
            this.ios_url = ios_url;
        }

        public String getAndroid_url() {
            return android_url;
        }

        public void setAndroid_url(String android_url) {
            this.android_url = android_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
