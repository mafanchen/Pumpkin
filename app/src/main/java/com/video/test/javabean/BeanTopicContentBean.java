package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/8/2.
 */
public class BeanTopicContentBean {
    /**
     * id : 1
     * zt_title : 测试专题
     * zt_pic : http://www.ffmovie.net/uploads/slide/2018-07-04/5e68568de058f0d8d90caea5eddd11e1.png
     * zt_content : 这是一个测试专题
     */

    private String id;
    private String zt_f_pic;
    private String zt_title;
    private String zt_pic;
    private String zt_content;
    /**
     * 专题下的资源数量
     */
    private String zt_num;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZt_f_pic() {
        return zt_f_pic;
    }

    public void setZt_f_pic(String zt_f_pic) {
        this.zt_f_pic = zt_f_pic;
    }

    public String getZt_title() {
        return zt_title;
    }

    public void setZt_title(String zt_title) {
        this.zt_title = zt_title;
    }

    public String getZt_pic() {
        return zt_pic;
    }

    public void setZt_pic(String zt_pic) {
        this.zt_pic = zt_pic;
    }

    public String getZt_content() {
        return zt_content;
    }

    public void setZt_content(String zt_content) {
        this.zt_content = zt_content;
    }

    public String getZt_num() {
        return zt_num;
    }

    public void setZt_num(String zt_num) {
        this.zt_num = zt_num;
    }
}
