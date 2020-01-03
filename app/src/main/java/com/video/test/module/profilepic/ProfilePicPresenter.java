package com.video.test.module.profilepic;

import com.video.test.javabean.ProfilePictureBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.utils.LogUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ProfilePicPresenter extends ProfilePicContarct.Presenter<ProfilePicModel> {
    private static final String TAG = "ProfilePicPresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void getProfilePics() {
        Disposable disposable = mModel.getProfilePics()
                .subscribe(new Consumer<List<ProfilePictureBean>>() {
                    @Override
                    public void accept(List<ProfilePictureBean> profilePictureBeans) throws Exception {
                        LogUtils.d(TAG, "getProfilePics" + "Success");
                        mView.getProfilePics(profilePictureBeans);
                        mView.hideRefreshLayout(true);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideRefreshLayout(false);
                        LogUtils.e(TAG, "getProfilePics" + "Fail Message : " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void updateProfilePic(String picId) {
        Disposable disposable = mModel.updateProfilePic(picId).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.d(TAG, "updateProfilePic" + "Success Message : " + s);
                mView.updateProfileSuccess(s);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.e(TAG, "updateProfilePic" + "Fail Message : " + throwable.getMessage());
                mView.updateProfileFailed(throwable.getMessage());
            }
        });
        addDisposable(disposable);
    }
}
