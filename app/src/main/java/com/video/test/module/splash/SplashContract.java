package com.video.test.module.splash;

import android.content.Intent;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.DomainNameBean;
import com.video.test.javabean.SplashBean;

import java.util.List;

import io.reactivex.Observable;


/**
 * @author Enoch Created on 2018/6/27.
 */
public interface SplashContract {


    interface Model extends IModel {

        Observable<SplashBean> getSplashInfo();

        Observable<List<DomainNameBean>> getDomainUrls();

    }

    interface View extends IView {

        void skipSplashActivity();

        void jumpToAdPage(String adName, String jumpUrl, String picUrl, String adId, int showTime);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void registerWeChat();

        abstract void getSplashBean();

        abstract void countDownSplash();

        abstract void shareGetInfo(Intent intent);

    }
}
