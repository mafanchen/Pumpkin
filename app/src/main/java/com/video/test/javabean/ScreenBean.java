package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/12/14.
 */
public class ScreenBean {

    /**
     * 3 : {"screen_area":[{"area_key":"全部地区","area_val":"1"},{"area_key":"大陆","area_val":"'大陆'"},{"area_key":"香港","area_val":"'香港'"},{"area_key":"台湾","area_val":"'台湾'"},{"area_key":"欧美","area_val":"'欧美','法国','美国'"},{"area_key":"日本","area_val":"'日本'"},{"area_key":"韩国","area_val":"'韩国'"},{"area_key":"其他","area_val":"'其他'"}],"screen_type":[{"type_key":"全部类型","type_val":"5,31,25,26,27,28,29,30,32"},{"type_key":"喜剧片","type_val":"26,33,17"},{"type_key":"爱情片","type_val":"26,27"},{"type_key":"动作片","type_val":"25,31,23"},{"type_key":"悬疑片","type_val":"31"},{"type_key":"科幻片","type_val":"25,28"},{"type_key":"纪录片","type_val":"32"}],"screen_year":[{"year_key":"全部年份","year_val":"1"},{"year_key":"2018","year_val":"2018"},{"year_key":"2017","year_val":"2017"},{"year_key":"2016","year_val":"2016"},{"year_key":"2015-2010","year_val":"2015,2014,2013,2012,2011,2010"},{"year_key":"00年代","year_val":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"year_key":"90年代","year_val":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"year_key":"80年代","year_val":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"year_key":"更早","year_val":"2"}],"screen_play":[{"play_key":"最高评分","play_val":"1"},{"play_key":"最新更新","play_val":"2"}]}
     * 4 : {"screen_area":[{"area_key":"全部地区","area_val":"3,20,17,18,19,21,22,23,24"},{"area_key":"国产剧","area_val":"17,23"},{"area_key":"港台剧","area_val":"18,22"},{"area_key":"美剧","area_val":"19"},{"area_key":"英剧","area_val":"19"},{"area_key":"韩剧","area_val":"23"},{"area_key":"日剧","area_val":"20"},{"area_key":"新马泰","area_val":"21"}],"screen_year":[{"year_key":"全部年份","year_val":"1"},{"year_key":"2018","year_val":"2018"},{"year_key":"2017","year_val":"2017"},{"year_key":"2016","year_val":"2016"},{"year_key":"2015-2010","year_val":"2015,2014,2013,2012,2011,2010"},{"year_key":"00年代","year_val":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"year_key":"90年代","year_val":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"year_key":"80年代","year_val":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"year_key":"更早","year_val":"2"}],"screen_play":[{"play_key":"最高评分","play_val":"1"},{"play_key":"最新更新","play_val":"2"}]}
     * 5 : {"screen_area":[{"area_key":"全部地区","area_val":"1"},{"area_key":"国漫专区","area_val":"'大陆'"},{"area_key":"日本动漫","area_val":"'日本'"},{"area_key":"韩国动漫","area_val":"'韩国','欧美'"},{"area_key":"美国动漫","area_val":"'欧美'"},{"area_key":"台湾动漫","area_val":"'台湾','日本','大陆','欧美'"}],"screen_year":[{"year_key":"全部年份","year_val":"1"},{"year_key":"2018","year_val":"2018"},{"year_key":"2017","year_val":"2017"},{"year_key":"2016","year_val":"2016"},{"year_key":"2015-2010","year_val":"2015,2014,2013,2012,2011,2010"},{"year_key":"00年代","year_val":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"year_key":"90年代","year_val":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"year_key":"80年代","year_val":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"year_key":"更早","year_val":"2"}],"screen_play":[{"play_key":"最高评分","play_val":"1"},{"play_key":"最新更新","play_val":"2"}]}
     * 6 : {"screen_area":[{"area_key":"全部地区","area_val":"1"},{"area_key":"大陆综艺","area_val":"'大陆'"},{"area_key":"韩国综艺","area_val":"'韩国'"},{"area_key":"日本综艺","area_val":"'台湾','日本','欧美'"},{"area_key":"欧美综艺","area_val":"'欧美'"},{"area_key":"港台综艺","area_val":"'台湾','香港'"}],"screen_year":[{"year_key":"全部年份","year_val":"1"},{"year_key":"2018","year_val":"2018"},{"year_key":"2017","year_val":"2017"},{"year_key":"2016","year_val":"2016"},{"year_key":"2015-2010","year_val":"2015,2014,2013,2012,2011,2010"},{"year_key":"00年代","year_val":"2000,2001,2002,2003,2004,2005,2006,2007,2008,2009"},{"year_key":"90年代","year_val":"1990,1991,1992,1993,1994,1995,1996,1997,1998,1999"},{"year_key":"80年代","year_val":"1980,1981,1982,1983,1984,1985,1986,1987,1988,1989"},{"year_key":"更早","year_val":"2"}],"screen_play":[{"play_key":"最高评分","play_val":"1"},{"play_key":"最新更新","play_val":"2"}]}
     */

    @SerializedName("3")
    private ScreenMovieBean movieBeans;
    @SerializedName("4")
    private ScreenTeleplayBean teleplayBeans;
    @SerializedName("5")
    private ScreenCartoonBean cartoonBeans;
    @SerializedName("6")
    private ScreenVarietyBean varietyBeans;

    public ScreenMovieBean getMovieBeans() {
        return movieBeans;
    }

    public void setMovieBeans(ScreenMovieBean movieBeans) {
        this.movieBeans = movieBeans;
    }

    public ScreenTeleplayBean getTeleplayBeans() {
        return teleplayBeans;
    }

    public void setTeleplayBeans(ScreenTeleplayBean teleplayBeans) {
        this.teleplayBeans = teleplayBeans;
    }

    public ScreenCartoonBean getCartoonBeans() {
        return cartoonBeans;
    }

    public void setCartoonBeans(ScreenCartoonBean cartoonBeans) {
        this.cartoonBeans = cartoonBeans;
    }

    public ScreenVarietyBean getVarietyBeans() {
        return varietyBeans;
    }

    public void setVarietyBeans(ScreenVarietyBean varietyBeans) {
        this.varietyBeans = varietyBeans;
    }
}
