package com.video.test.module.homepage;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.HomeDialogBean;
import com.video.test.javabean.IndexPidBean;
import com.video.test.javabean.LoginBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VersionInfoBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface HomepageContract {

    interface Model extends IModel {

        Observable<UserCenterBean> getUserInfo(String userToken, String userTokenId);

        Observable<LoginBean> login(String oldId, String newId, String recommendId);

        Observable<VersionInfoBean.InfoBean> getVersionInfo();

        Observable<List<IndexPidBean>> getPidIndex();

        Observable<List<HomeDialogBean>> getHomeDialogData();

        Observable<Boolean> getHotWords();

    }

    interface View extends IView {
        void setUserInfo(UserCenterBean userCenterBean);

        void setPidIndex(List<IndexPidBean> indexPidBeans);

        void setVersionInfo(VersionInfoBean.InfoBean infoBean);

        void showProgressDialog();

        void hideProgressDialog();

        void setHomeDialogData(List<HomeDialogBean> list);

    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {


        abstract ArrayList<Fragment> initFragmentList();

        abstract ArrayList<CustomTabEntity> initTabEntities();

        abstract void login();

        abstract void getUserInfo(String userToken, String userTokenid);

        abstract void getPidIndex();

        abstract void getVersionInfo();

        abstract void getLatestVersion(FragmentActivity activity, VersionInfoBean.InfoBean infoBean);

        abstract void getHomeDialogData();

        abstract void getHotWords();

        abstract void initM3U8DownloadConfig();

        abstract void shareGetInfo(Intent intent);

        abstract void initTimeClose();
    }

}
