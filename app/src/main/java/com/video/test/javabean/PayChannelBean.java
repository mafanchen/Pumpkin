package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/11/21.
 */
public class PayChannelBean {

    /**
     * pay_name : dokpay
     * type : 1
     * url : http://test.beansauce.net/App/UserInfo/addVip?user_id=90
     */

    private String pay_name;
    private int type;
    private String url;

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
