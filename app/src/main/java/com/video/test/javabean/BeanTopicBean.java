package com.video.test.javabean;

import java.util.List;

/**
 * @author Enoch Created on 2018/8/2.
 */
public class BeanTopicBean {

    /**
     * pid : 2
     * tag : 1
     * type : 测试专题
     * list : [{"id":"1","zt_title":"测试专题","zt_pic":"http://www.ffmovie.net/uploads/slide/2018-07-04/5e68568de058f0d8d90caea5eddd11e1.png","zt_content":"这是一个测试专题"}]
     */

    private int pid;
    private String tag;
    private String type;
    private List<BeanTopicContentBean> list;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BeanTopicContentBean> getList() {
        return list;
    }

    public void setList(List<BeanTopicContentBean> list) {
        this.list = list;
    }

}
