package com.video.test.module.share;

import com.video.test.javabean.ExchangeBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.ShareInfoBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class ShareModel implements ShareContract.Model {


    @Override
    public Observable<ExchangeBean> exchangeVip(String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .exchageVip(userToken, userTokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<ShareInfoBean> getShareInfo(String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .getShareInfo(userToken, userTokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

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
}
