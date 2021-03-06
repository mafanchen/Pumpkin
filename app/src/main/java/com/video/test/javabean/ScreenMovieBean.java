package com.video.test.javabean;

import java.util.List;

/**
 * @author Enoch Created on 2018/12/14.
 */
public class ScreenMovieBean {

    private List<ScreenAreaBean> screen_area;
    private List<ScreenTypeBean> screen_type;
    private List<ScreenYearBean> screen_year;
    private List<ScreenSortBean> screen_play;

    public List<ScreenAreaBean> getScreen_area() {

        return screen_area;
    }

    public void setScreen_area(List<ScreenAreaBean> screen_area) {
        this.screen_area = screen_area;
    }

    public List<ScreenTypeBean> getScreen_type() {
        return screen_type;
    }

    public void setScreen_type(List<ScreenTypeBean> screen_type) {
        this.screen_type = screen_type;
    }

    public List<ScreenYearBean> getScreen_year() {
        return screen_year;
    }

    public void setScreen_year(List<ScreenYearBean> screen_year) {
        this.screen_year = screen_year;
    }

    public List<ScreenSortBean> getScreen_play() {
        return screen_play;
    }

    public void setScreen_play(List<ScreenSortBean> screen_play) {
        this.screen_play = screen_play;
    }
}
