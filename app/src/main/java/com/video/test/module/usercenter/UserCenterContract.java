package com.video.test.module.usercenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.constraint.Group;
import android.widget.TextView;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.AdBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.UploadAvatarBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;
import com.video.test.network.BaseResult;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface UserCenterContract {

    interface Model extends IModel {
        Observable<UserCenterBean> getUserInfo(String userToken, String userTokenId);

        Observable<UploadAvatarBean> uploadAvatar(RequestBody userToken, RequestBody userTokenId, MultipartBody.Part imageFile);

        Observable<String> setAvatarUrl(String userToken, String userTokenId, String avatarUrl);

        Observable<LoginBean> login(String oldId, String newId, String recommendId);

        Observable<VersionInfoBean.InfoBean> getVersionInfo();

        Observable<AdBean> getUserCenterAdInfo();

        Observable<BaseResult<Object>> addAdInfo(int adType, String adId);
    }

    interface View extends IView {

        void setUserInfo(UserCenterBean userCenterBean);

        void getUploadAvatarMessage(UploadAvatarBean uploadAvatarBean);

        void getSetAvatarUrl(String message);

        void showProgressDialog();

        void hideProgressDialog();

        void setVersionInfo(VersionInfoBean.InfoBean infoBean);

        void getLatestVersion(Context context, VersionInfoBean.InfoBean infoBean);

        void setAdInfo(AdBean adBean);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getUserInfo();

        abstract void uploadAvatar(File imageFile);

        abstract void setAvatarUrl(String avatarUrl);

        abstract void getImageFromGallery(Intent data);

        abstract void getVersionInfo();

        abstract void initWeChatApi();

        abstract void share2WeChat(Resources resources, String shareUrl, int targetSession);

        abstract void login();

        abstract void hideUserInfo(Group group);

        abstract void showVisitorInfo(TextView tvLoginBtn);

        abstract void showUserInfo(Group group);

        abstract void hideVisitorInfo(TextView tvLoginBtn);
    }


}
