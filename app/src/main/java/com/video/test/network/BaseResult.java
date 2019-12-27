package com.video.test.network;

import com.google.gson.annotations.SerializedName;
import com.video.test.AppConstant;

/**
 * @author Enoch Created on 22/03/2018.
 */

public class BaseResult<T> {

    @SerializedName("code")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private T data;

    public boolean isSuccess() {
        return status == AppConstant.REQUEST_SUCCESS;
    }

    public int getCode() {
        return status;
    }

    public void setCode(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "BaseResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';

    }
}
