package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2019-04-24.
 */
public class RequestBean {

    private String token;
    @SerializedName("token_id")
    private String tokenId;
    @SerializedName("request_key")
    private String requestInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(String requestInfo) {
        this.requestInfo = requestInfo;
    }
}
