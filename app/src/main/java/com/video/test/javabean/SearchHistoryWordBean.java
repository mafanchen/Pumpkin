package com.video.test.javabean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * @author Enoch Created on 2018/7/18.
 */
@Entity
public class SearchHistoryWordBean {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String keyword;


    @Generated(hash = 81582086)
    public SearchHistoryWordBean(Long id, @NotNull String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    @Generated(hash = 1900119225)
    public SearchHistoryWordBean() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
