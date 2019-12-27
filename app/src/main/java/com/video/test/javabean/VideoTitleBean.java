package com.video.test.javabean;

/**
 * @author Enoch Created on 2018/8/6.
 */
public class VideoTitleBean {
    /**
     * 横向推荐视频
     */
    public static final int TYPE_RECOMMEND = 1;
    /**
     * 首页最热视频
     */
    public static final int TYPE_HOTEST = 2;
    /**
     * 分类视频（如恐怖片、动作片）
     */
    public static final int TYPE_CATEGORY = 3;

    private int pid;
    private String tag;
    private String type;
    private String typePic;
    private String parentId;
    /**
     * 首页热门视频点击更多时的id
     */
    private String showId;
    private boolean showDivider = true;

    /**
     * 栏目类型
     *
     * @see #TYPE_CATEGORY
     * @see #TYPE_HOTEST
     * @see #TYPE_RECOMMEND
     */
    private int columnType = -1;


    public VideoTitleBean() {

    }

    public String getTypePic() {
        return typePic;
    }

    public void setTypePic(String typePic) {
        this.typePic = typePic;
    }

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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isShowDivider() {
        return showDivider;
    }

    public void setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }
}
