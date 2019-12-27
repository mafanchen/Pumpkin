package com.video.test.module.splash;

import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.DomainNameBean;
import com.video.test.javabean.SplashBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxSchedulers;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SplashModel implements SplashContract.Model {
    private static final String TAG = "SplashModel";

    @Override
    public Observable<SplashBean> getSplashInfo() {
        return RetrofitHelper.getInstance()
                .getSplashInfo()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<List<DomainNameBean>> getDomainUrls() {
        return RetrofitHelper.getInstance()
                .getDomianUrls()
                .compose(RxSchedulers.handleResult())
                .map(new Function<List<String>, List<DomainNameBean>>() {
                    @Override
                    public List<DomainNameBean> apply(List<String> strings) throws Exception {
                        //每次更新数据库的时候,要先把之前的数据都清空
                        DBManager.getInstance(TestApp.getContext()).deleteAllDomainUrls();
                        ArrayList<DomainNameBean> domainNameBeans = new ArrayList<>();
                        DomainNameBean domainNameBean;
                        for (String url : strings) {
                            LogUtils.d(TAG, "domain url : " + url + " Thread : " + Thread.currentThread().getName());
                            domainNameBean = new DomainNameBean();
                            domainNameBean.setRul(url);
                            //都从插入到数据库中
                            DBManager.getInstance(TestApp.getContext()).insertDomainUrls(domainNameBean);
                            domainNameBeans.add(domainNameBean);
                        }
                        return domainNameBeans;
                    }
                }).compose(RxSchedulers.io_main());
    }

}
