package com.video.test.network;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch  Created on 22/03/2018.
 */

public class BaseException extends Exception {
    @SerializedName("code")
    private int errorCode;
    @SerializedName("msg")
    private String errorMsg;

    public BaseException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


}

