package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/7/27.
 */
public class SetEmailBean {

    /**
     * code : 200
     * msg : 邮件发送成功，请前往确认绑定
     */

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
