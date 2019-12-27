package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/7/27.
 */
public class RegisterBean {


    /**
     * token : 1adb69c2f6b1da141b22921f6127c8f7
     * token_id : 7
     */

    private String token;
    private String token_id;
    private GiftBean activity;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }


    public GiftBean getActivity() {
        return activity;
    }

    public void setActivity(GiftBean activity) {
        this.activity = activity;
    }

}
