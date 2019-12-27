package com.video.test.javabean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * @author Enoch Created on 2019/1/21.
 */
@Entity
public class DomainNameBean {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String rul;

    @Generated(hash = 1308285543)
    public DomainNameBean(Long id, @NotNull String rul) {
        this.id = id;
        this.rul = rul;
    }

    @Generated(hash = 1033834298)
    public DomainNameBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRul() {
        return this.rul;
    }

    public void setRul(String rul) {
        this.rul = rul;
    }

}

