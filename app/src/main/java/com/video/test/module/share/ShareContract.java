package com.video.test.module.share;

import android.content.res.Resources;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.ExchangeBean;
import com.video.test.javabean.GiftBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.ShareInfoBean;
import com.video.test.javabean.UserCenterBean;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface ShareContract {

    interface Model extends IModel {

        Observable<ExchangeBean> exchangeVip(String userToken, String userTokenId);

        Observable<ShareInfoBean> getShareInfo(String userToken, String userTokenId);

        Observable<UserCenterBean> getUserInfo(String userToken, String userTokenId);

        Observable<LoginBean> login(String oldId, String newId, String recommendId);

    }

    interface View extends IView {

        void setShareInfo(ShareInfoBean shareInfo);

        void createQRCode(String webUrl);

        void showGiftDialog(GiftBean giftBean);

        void setShareCount(String count);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {


        abstract void initWeChatApi();

        abstract void share2WeChat(Resources resources, String shareUrl, int targetSession);

        abstract void getShareInfo();

        abstract void exchangeVip();

        abstract void login();


    }


}
