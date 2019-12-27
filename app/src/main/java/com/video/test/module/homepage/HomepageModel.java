package com.video.test.module.homepage;

import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.HomeDialogBean;
import com.video.test.javabean.IndexPidBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.SearchHotWordBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class HomepageModel implements HomepageContract.Model {


    @Override
    public Observable<UserCenterBean> getUserInfo(String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .getUserInfo(userToken, userTokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<LoginBean> login(String oldId, String newId, String recommendId) {
        return RetrofitHelper.getInstance().userLogin(oldId, newId, recommendId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<VersionInfoBean.InfoBean> getVersionInfo() {
        return RetrofitHelper.getInstance()
                .getVersionInfo()
                .compose(RxSchedulers.handleResult())
                .map(new Function<VersionInfoBean, VersionInfoBean.InfoBean>() {
                    @Override
                    public VersionInfoBean.InfoBean apply(VersionInfoBean versionInfoBean) {
                        return versionInfoBean.getAndroid();
                    }
                }).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<List<IndexPidBean>> getPidIndex() {
        return RetrofitHelper.getInstance()
                .getIndexPid()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }


    @Override
    public Observable<List<HomeDialogBean>> getHomeDialogData() {
        return RetrofitHelper.getInstance()
                .getHomeDialogData()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());

    }

    @Override
    public Observable<Boolean> getHotWords() {
        return RetrofitHelper.getInstance()
                .getHotWords()
                .compose(RxSchedulers.handleResult())
                .map(new Function<List<SearchHotWordBean>, Boolean>() {
                    @Override
                    public Boolean apply(List<SearchHotWordBean> searchHotWordBeans) {
                        if (!searchHotWordBeans.isEmpty()) {
                            DBManager.getInstance(TestApp.getContext()).deleteAllHotHistoryWord();
                            for (SearchHotWordBean searchHotWordBean : searchHotWordBeans) {
                                LogUtils.d("HomepageModel", "keyword : " + searchHotWordBean.getVod_keyword()
                                        + " vodId : " + searchHotWordBean.getWord_id());
                                DBManager.getInstance(TestApp.getContext()).insertSearchHotWord(searchHotWordBean);
                            }
                            return true;
                        }
                        return false;
                    }
                }).compose(RxSchedulers.io_main());
    }
}
