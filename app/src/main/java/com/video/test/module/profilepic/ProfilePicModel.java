package com.video.test.module.profilepic;

import com.video.test.javabean.ProfilePictureBean;
import com.video.test.network.RetrofitHelper;
import com.video.test.utils.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;

public class ProfilePicModel implements ProfilePicContarct.Model {

    @Override
    public Observable<List<ProfilePictureBean>> getProfilePics() {
        return RetrofitHelper.getInstance().getProfilePics()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<String> updateProfilePic(String picId) {
        return RetrofitHelper.getInstance().updateProfilePic(picId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main());
    }
}
