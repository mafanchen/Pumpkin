package com.video.test.module.bindphonefloat;

import android.text.TextUtils;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.javabean.UserCenterBean;
import com.video.test.network.BaseException;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : AhhhhDong
 * @date : 2019/5/14 13:45
 */
public class BindPhoneFloatPresenter extends BindPhoneFloatContract.Presenter<BindPhoneFloatModel> {

    public static final String TAG = "BindPhoneFloatPresenter";

    private String countryCode = "86";
    private Disposable mTimer;

    @Override
    public void subscribe() {

    }

    @Override
    void selectCountry(String code) {
        countryCode = code;
    }

    @Override
    void getVerificationCode(String phone) {
        LogUtils.d(TAG, "code :" + countryCode + " phone : " + phone);
        if (!TextUtils.isEmpty(countryCode)) {
            if (!TextUtils.isEmpty(phone)) {
                Disposable disposable = mModel.getVerificationCode(countryCode, phone)
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                //开始计时，跳转页面
                                mView.showToast(s);
                                String secretPhone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
                                mView.setPhoneNumberText(String.format(Locale.getDefault(), "已发送短信验证码至+%s %s", countryCode, secretPhone));
                                mView.showVerificationCodeView();
                                startTimer();
                            }
                        }, new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, "getCheckCode Error == " + throwable.getMessage())));
                addDisposable(disposable);
            } else {
                ToastUtils.showLongToast(TestApp.getContext(), "手机号码不能为空");
            }
        } else {
            ToastUtils.showLongToast(TestApp.getContext(), "国家号不能为空");
        }
    }

    private void startTimer() {
        if (mTimer != null && !mTimer.isDisposed()) {
            mTimer.dispose();
        }
        mTimer = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long l) throws Exception {
                        int time = (int) (60 - l);
                        TextView view = mView.getTextViewVerification();
                        if (time == 0) {
                            view.setEnabled(true);
                            view.setText("重新发送");
                            mTimer.dispose();
                        } else {
                            view.setText(String.format(Locale.getDefault(), "重新发送%d秒", time));
                            view.setEnabled(false);
                        }
                    }
                });
        addDisposable(mTimer);
    }

    @Override
    void bindPhone(String phone, String verificationCode, String isForce) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.bindPhone(countryCode, phone, verificationCode, isForce, userToken, userTokenId)
                .subscribe(bindPhoneBean -> {
                    UserCenterBean userInfo = SpUtils.getSerializable(TestApp.getContext(), AppConstant.USER_INFO);
                    //更改保存的电话
                    if (userInfo != null) {
                        userInfo.setMobile(phone);
                        SpUtils.putSerializable(TestApp.getContext(), AppConstant.USER_INFO, userInfo);
                    }
                    EventBus.getDefault().post(bindPhoneBean);
                    SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN, bindPhoneBean.getToken());
                    SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, bindPhoneBean.getToken_id());
                    mView.showToast("绑定手机号成功");
                    mView.close();
                }, new RxExceptionHandler<>(throwable -> {
                    if (throwable instanceof BaseException) {
                        ((BaseException) throwable).getErrorCode();
                        ((BaseException) throwable).getErrorMsg();
                        LogUtils.e(TAG, "bindPhone ErrorCode == " + ((BaseException) throwable).getErrorCode() + "Message : " + throwable.getMessage());
                        mView.showBindConfirmDialog();
                    }
                }));
        addDisposable(disposable);
    }
}
