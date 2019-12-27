package com.video.test.network;

import java.util.List;

/**
 * 收藏/历史等页面的数据分页数据封装
 *
 * @author : AhhhhDong
 * @date : 2019/4/28 15:17
 */
public class ListResult<T> {
    private List<T> list;
    private int listCount;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }
}
