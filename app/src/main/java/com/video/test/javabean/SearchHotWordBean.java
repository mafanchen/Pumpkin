package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * @author Enoch Created on 2018/11/27.
 */
@Entity
public class SearchHotWordBean {


    @SerializedName("id")
    private String word_id;

    @NotNull
    private String vod_keyword;


    @Generated(hash = 2001572682)
    public SearchHotWordBean(String word_id, @NotNull String vod_keyword) {
        this.word_id = word_id;
        this.vod_keyword = vod_keyword;
    }

    @Generated(hash = 117930719)
    public SearchHotWordBean() {
    }


    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
    }

    public String getVod_keyword() {
        return vod_keyword;
    }

    public void setVod_keyword(String vod_keyword) {
        this.vod_keyword = vod_keyword;
    }
}
