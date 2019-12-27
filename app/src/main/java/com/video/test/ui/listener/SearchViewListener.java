package com.video.test.ui.listener;

/**
 * @author  Created by enoch on 11/09/2017.
 * 自定义的SearchView 的回调监听器
 */

public interface SearchViewListener {

    /**  暂未实现
     * @param text 自动搜索关键字
     */
    void onRefreshAutoComplete(String text);

    /**
     *
     * @param text 搜索关键字
     */
    void onSearch(String text);

}
