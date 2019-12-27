package com.video.test.module.setphone;

import android.widget.EditText;
import android.widget.TextView;

import com.video.test.framework.BasePresenter;
import com.video.test.framework.IModel;
import com.video.test.framework.IView;
import com.video.test.javabean.BindPhoneBean;

import io.reactivex.Observable;

/**
 * @author Enoch Created on 2018/6/27.
 */
public interface SetPhoneContract {

    interface Model extends IModel {
        Observable<BindPhoneBean> updatePhone(String countryCode, String phone, String captcha,
                                              String isForce, String token, String tokenId);

        Observable<BindPhoneBean> bindPhone(String countryCode, String phone, String captcha,
                                            String isForce, String token, String tokenId);

        Observable<String> getCheckCode(String countryCode, String mobile);
    }

    interface View extends IView {

        void getCheckCodeMessage(String message);

        void bindPhoneSuccess();

        void showProgressDialog();

        void hideProgressDialog();

        void showSetPhoneDialog();

        void showBindConfirmDialog();

        void showUpdateConfirmDialog();
    }

    abstract class Presenter<M extends Model> extends BasePresenter<Model, View> {

        abstract void updatePhone(TextView tvCountryCode, EditText etPhone, EditText etCheckCode, String isForce);

        abstract void bindPhone(TextView tvCountryCode, EditText etPhone, EditText etCheckCode, String isForce);

        abstract void getCheckCode(TextView tvCode, EditText etPhone, TextView tvBtnCode);

        abstract void suspendCheckCode(TextView tvBtnCode);

    }

}
