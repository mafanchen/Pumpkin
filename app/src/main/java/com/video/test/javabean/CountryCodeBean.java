package com.video.test.javabean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * @author Enoch Created on 2018/7/18.
 */
@Entity
public class CountryCodeBean {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private int countryCode;

    @NotNull
    @Unique
    private String country;

    @NotNull
    @Unique
    private String displayCode;


    @Generated(hash = 1805992057)
    public CountryCodeBean(Long id, int countryCode, @NotNull String country,
            @NotNull String displayCode) {
        this.id = id;
        this.countryCode = countryCode;
        this.country = country;
        this.displayCode = displayCode;
    }

    @Generated(hash = 665538801)
    public CountryCodeBean() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDisplayCode() {
        return this.displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }


}
