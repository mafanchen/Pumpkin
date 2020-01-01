package com.video.test.module.usercenter;

import com.video.test.javabean.AdBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.UploadAvatarBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class UserCenterModel implements UserCenterContract.Model {


    @Override
    public Observable<UserCenterBean> getUserInfo(String userToken, String userTokenId) {
        return RetrofitHelper.getInstance()
                .getUserInfo(userToken, userTokenId)
                .compose(RxSchedulers.<UserCenterBean>handleResult())
                .compose(RxSchedulers.<UserCenterBean>io_main());

    }

    @Override
    public Observable<UploadAvatarBean> uploadAvatar(RequestBody userToken, RequestBody userTokenId, MultipartBody.Part imageFile) {
        return RetrofitHelper.getInstance()
                .uploadAvatar(userToken, userTokenId, imageFile)
                .compose(RxSchedulers.<UploadAvatarBean>handleResult())
                .compose(RxSchedulers.<UploadAvatarBean>io_main());

    }

    @Override
    public Observable<String> setAvatarUrl(String userToken, String userTokenId, String avatarUrl) {
        return RetrofitHelper.getInstance()
                .updateAvatarUrl(userToken, userTokenId, avatarUrl)
                .compose(RxSchedulers.<String>handleResult())
                .compose(RxSchedulers.<String>io_main());
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
                .map(VersionInfoBean::getAndroid).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<AdBean> getUserCenterAdInfo() {
        return RetrofitHelper.getInstance()
                .getUserCenterAdInfo()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
