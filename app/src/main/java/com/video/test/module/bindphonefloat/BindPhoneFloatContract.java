package com.video.test.module.bindphonefloat;

import android.widget.TextView;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.BindPhoneBean;

import io.reactivex.Observable;

/**
 * @author : AhhhhDong
 * @date : 2019/5/14 13:46
 */
public interface BindPhoneFloatContract {
    interface View extends IView {
        void setTitle(String title);

        void setPhoneNumberText(String phoneNumberText);

        TextView getTextViewVerification();

        void showPhoneView();

        void showCountryView();

        void showVerificationCodeView();

        void showToast(String message);

        void close();

        void showBindConfirmDialog();
    }

    interface Model extends IModel {
        Observable<BindPhoneBean> bindPhone(String countryCode, String phone, String captcha,
                                            String isForce, String token, String tokenId);

        Observable<String> getVerificationCode(String countryCode, String mobile);
    }

    abstract class Presenter<M extends Model> extends BasePresenter<M, View> {

        abstract void selectCountry(String code);

        abstract void getVerificationCode(String phone);

        abstract void bindPhone(String phone, String verificationCode, String isForce);

    }
}
