package com.video.test.module.bindphonefloat;

import com.video.test.javabean.BindPhoneBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;

/**
 * @author : AhhhhDong
 * @date : 2019/5/14 13:49
 */
public class BindPhoneFloatModel implements BindPhoneFloatContract.Model {
    @Override
    public Observable<BindPhoneBean> bindPhone(String countryCode, String phone, String captcha, String isForce, String token, String tokenId) {
        return RetrofitHelper.getInstance().bindPhone(countryCode, phone, captcha, isForce, token, tokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> getVerificationCode(String countryCode, String mobile) {
        return RetrofitHelper.getInstance().getCheckCode(countryCode, mobile)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

}
