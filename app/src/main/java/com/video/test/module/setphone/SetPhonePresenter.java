package com.video.test.module.setphone;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.javabean.BindPhoneBean;
import com.video.test.network.BaseException;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxCountdown;
import com.video.test.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class SetPhonePresenter extends SetPhoneContract.Presenter<SetPhoneModel> {
    private static final String TAG = "SetPhonePresenter";

    @Override
    public void subscribe() {

    }

    @Override
    void bindPhone(TextView tvCountryCode, EditText etPhone, EditText etCheckCode, String isForce) {
        String countryCode = tvCountryCode.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String checkCode = etCheckCode.getText().toString().trim();
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        Disposable disposable = mModel.bindPhone(countryCode, phone, checkCode, isForce, userToken, userTokenId)
                .subscribe(new Consumer<BindPhoneBean>() {
                    @Override
                    public void accept(BindPhoneBean bindPhoneBean) throws Exception {
                        mView.hideProgressDialog();
                        mView.bindPhoneSuccess();
                        EventBus.getDefault().post(bindPhoneBean);
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN, bindPhoneBean.getToken());
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, bindPhoneBean.getToken_id());

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof BaseException) {
                            ((BaseException) throwable).getErrorCode();
                            ((BaseException) throwable).getErrorMsg();
                            LogUtils.e(TAG, "bindPhone ErrorCode == " + ((BaseException) throwable).getErrorCode() + "Message : " + throwable.getMessage());
                            mView.showBindConfirmDialog();
                        }
                        mView.hideProgressDialog();
                    }
                }));
        addDisposable(disposable);

    }


    @Override
    void updatePhone(TextView tvCountryCode, EditText etPhone, EditText etCheckCode, String isForce) {
        String countryCode = tvCountryCode.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String checkCode = etCheckCode.getText().toString().trim();
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.updatePhone(countryCode, phone, checkCode, isForce, userToken, userTokenId)
                .subscribe(new Consumer<BindPhoneBean>() {
                    @Override
                    public void accept(BindPhoneBean bindPhoneBean) throws Exception {
                        mView.hideProgressDialog();
                        mView.bindPhoneSuccess();
                        EventBus.getDefault().post(bindPhoneBean);
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN, bindPhoneBean.getToken());
                        SpUtils.putString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, bindPhoneBean.getToken_id());

                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof BaseException) {
                            ((BaseException) throwable).getErrorCode();
                            ((BaseException) throwable).getErrorMsg();
                            LogUtils.e(TAG, "bindPhone ErrorCode == " + ((BaseException) throwable).getErrorCode() + "Message : " + throwable.getMessage());
                            mView.showUpdateConfirmDialog();
                        }
                        mView.hideProgressDialog();
                    }
                }));
        addDisposable(disposable);

    }


    @Override
    void getCheckCode(TextView tvCode, EditText etPhone, TextView tvBtnCode) {
        String code = tvCode.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        LogUtils.d(TAG, "code :" + code + " phone : " + phone);
        if (!TextUtils.isEmpty(code)) {
            if (!TextUtils.isEmpty(phone)) {
                mView.showProgressDialog();
                Disposable disposable = mModel.getCheckCode(code, phone)
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                mView.getCheckCodeMessage(s);
                                mView.hideProgressDialog();
                                suspendCheckCode(tvBtnCode);
                            }
                        }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                LogUtils.e(TAG, "getCheckCode Error == " + throwable.getMessage());
                                mView.hideProgressDialog();
                            }
                        }));
                addDisposable(disposable);
            } else {
                ToastUtils.showLongToast("手机号码不能为空");
            }
        } else {
            ToastUtils.showLongToast("国家号不能为空");
        }
    }

    @Override
    void suspendCheckCode(TextView tvBtnCode) {
        if (null != tvBtnCode) {
            tvBtnCode.setEnabled(false);
            RxCountdown.countdown(60)
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            addDisposable(d);
                        }

                        @Override
                        public void onNext(Integer integer) {
                            String count = tvBtnCode.getContext().getResources().getString(R.string.setPhone_checkCode_count_again, integer);
                            tvBtnCode.setText(count);
                            tvBtnCode.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }

                        @Override
                        public void onError(Throwable e) {
                            tvBtnCode.setEnabled(true);
                            tvBtnCode.setText(R.string.setPhone_checkCode_again);
                            Drawable arrowDrawable = ContextCompat.getDrawable(tvBtnCode.getContext(), R.drawable.ic_com_rig);
                            tvBtnCode.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
                        }

                        @Override
                        public void onComplete() {
                            tvBtnCode.setEnabled(true);
                            tvBtnCode.setText(R.string.setPhone_checkCode_again);
                            Drawable arrowDrawable = ContextCompat.getDrawable(tvBtnCode.getContext(), R.drawable.ic_com_rig);
                            tvBtnCode.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
                        }
                    });
        }
    }
}

