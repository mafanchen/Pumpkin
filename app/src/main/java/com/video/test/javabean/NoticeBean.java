package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/12/14.
 */
public class NoticeBean {


    /**
     * id : 1
     * sys_title : 测试标题
     * sys_content : 爱搜ID阿里山达鲁大师李达康解百纳会计师那山口川那算超级案件那是点啊速度快你电脑拉十多年你阿斯兰的年拉屎的可能澳康达啊大大大啊ad啊ad阿克蒙德啊
     * sys_end_name : 测试跳转1111
     * sys_end_url : www.beansauce.com
     * sys_time : 2018.12.14
     */

    private String id;
    @SerializedName("sys_title")
    private String title;
    @SerializedName("sys_content")
    private String content;
    @SerializedName("sys_end_name")
    private String webTitle;
    @SerializedName("sys_end_url")
    private String webUrl;
    @SerializedName("sys_time")
    private String time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
