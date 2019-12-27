package com.video.test.javabean;

/**
 * 对按钮的封装类
 *
 * @author Reus
 * @date 2019/3/5
 */
public class ButtonBean {

    /**
     * 用于区分点击功能
     */
    private int id;
    /**
     * 标题
     */
    private String name;
    /**
     * 图片的本地id
     */
    private int imageId;
    /**
     * 网络图片的url
     */
    private String imageUrl;

    public ButtonBean(int id, String name, int imageId) {
        this.id = id;
        this.name = name;
        this.imageId = imageId;
    }

    public ButtonBean(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public ButtonBean(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public ButtonBean(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
