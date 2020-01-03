package com.video.test.module.profilepic;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.ProfilePictureBean;

import java.util.List;

import io.reactivex.Observable;

public interface ProfilePicContarct {
    interface Model extends IModel {
        Observable<List<ProfilePictureBean>> getProfilePics();

        Observable<String> updateProfilePic(String picId);

    }

    interface View extends IView {

        void getProfilePics(List<ProfilePictureBean> pics);

        void updateProfileSuccess(String message);

        void updateProfileFailed(String message);

        void showRefreshLayout();

        void hideRefreshLayout(boolean isSuccess);

    }


    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void getProfilePics();

        abstract void updateProfilePic(String picId);

    }

}
