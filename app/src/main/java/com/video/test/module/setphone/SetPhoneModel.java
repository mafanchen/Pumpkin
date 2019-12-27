package com.video.test.module.setphone;

import com.video.test.javabean.BindPhoneBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SetPhoneModel implements SetPhoneContract.Model {


    @Override
    public Observable<BindPhoneBean> updatePhone(String countryCode, String phone, String captcha, String isForce, String token, String tokenId) {
        return RetrofitHelper.getInstance().updatePhone(countryCode, phone, captcha, isForce, token, tokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<BindPhoneBean> bindPhone(String countryCode, String phone, String captcha, String isForce, String token, String tokenId) {
        return RetrofitHelper.getInstance().bindPhone(countryCode, phone, captcha, isForce, token, tokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> getCheckCode(String countryCode, String mobile) {
        return RetrofitHelper.getInstance().getCheckCode(countryCode, mobile)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
