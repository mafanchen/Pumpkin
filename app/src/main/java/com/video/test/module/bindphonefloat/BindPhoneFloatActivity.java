package com.video.test.module.bindphonefloat;

import android.annotation.SuppressLint;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.R;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.KeyboardListenLayout;
import com.video.test.utils.InputManagerHelper;
import com.video.test.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

/**
 * java类作用描述
 *
 * @author : AhhhhDong
 * @date : 2019/5/14 13:45
 */
@Route(path = "/bindphone/activity/float")
public class BindPhoneFloatActivity extends BaseActivity<BindPhoneFloatPresenter> implements BindPhoneFloatContract.View {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_countryCode)
    TextView mTvCountryCode;
    @BindView(R.id.tv_verificationCode)
    TextView mTvVerificationCode;
    @BindView(R.id.et_phone)
    TextView mEtPhone;
    @BindView(R.id.group_phone)
    Group mGroupPhone;
    @BindView(R.id.rv_countryCode)
    RecyclerView mRvCountryCode;
    @BindView(R.id.tv_phone_number)
    TextView mTvPhoneNumber;
    @BindView(R.id.et_verificationCode)
    EditText mEtVerificationCode;
    @BindView(R.id.tv_verificationCode_timer)
    TextView mTvVerificationTimer;
    @BindView(R.id.btn_confirm)
    TextView mTvConfirm;
    @BindView(R.id.group_verificationCode)
    Group mGroupVerificationCode;
    @BindView(R.id.layout_keyboard_listen)
    KeyboardListenLayout mLayoutKeyboard;
    @BindView(R.id.layout_content)
    ConstraintLayout mLayoutContent;
    @BindArray(R.array.countryCode)
    String[] mCountryCode;
    @BindArray(R.array.country)
    String[] mCountryName;
    private CountryCodeAdapter mAdapter;
    private ObservableEmitter<Object> mEmitterGetCode;
    private Disposable mDisposableGetCode;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_bind_phone_float;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setStatueBarColor() {
    }

    @Override
    protected void initView() {
        super.initView();
        mTvTitle.setText("绑定手机后可评论");
        mTvCountryCode.setText("+86");
        mIvBack.setVisibility(View.GONE);
        InputManagerHelper.attachToActivity(this).bind(mLayoutKeyboard, mLayoutContent).offset(16);
        mDisposableGetCode = Observable.create(emitter -> mEmitterGetCode = emitter)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> mPresenter.getVerificationCode(getPhone()));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new CountryCodeAdapter(mCountryCode, mCountryName, (code, name) -> {
                showPhoneView();
                mTvCountryCode.setText("+" + code);
                mPresenter.selectCountry(code);
            });
        }
        if (mRvCountryCode.getLayoutManager() == null) {
            mRvCountryCode.setLayoutManager(new LinearLayoutManager(this));
        }
        mRvCountryCode.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.iv_close, R.id.tv_countryCode, R.id.tv_verificationCode_timer, R.id.btn_confirm, R.id.tv_verificationCode})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                showPhoneView();
                break;
            case R.id.iv_close:
                close();
                break;
            case R.id.tv_countryCode:
                showCountryView();
                break;
            case R.id.tv_verificationCode_timer:
            case R.id.tv_verificationCode:
                if (mEmitterGetCode != null) {
                    mEmitterGetCode.onNext(new Object());
                }
                break;
            case R.id.btn_confirm:
                mPresenter.bindPhone(mEtPhone.getText().toString().trim(), getVerificationCode(), "");
                break;
            default:
                break;
        }
    }

    @OnTextChanged(R.id.et_phone)
    public void onPhoneChanged(CharSequence text) {
        mTvVerificationCode.setEnabled(text.length() >= 8);
    }

    @OnTextChanged(R.id.et_verificationCode)
    public void onVerificationCodeChanged(CharSequence text) {
        mTvConfirm.setEnabled(text.length() >= 5);
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void setPhoneNumberText(String phoneNumberText) {
        mTvPhoneNumber.setText(phoneNumberText);
    }

    @Override
    public TextView getTextViewVerification() {
        return mTvVerificationTimer;
    }

    @Override
    public void showPhoneView() {
        mTvTitle.setText("绑定手机后可评论");
        setViewVisiable(mIvBack, false);
        setViewVisiable(mGroupPhone, true);
        setViewVisiable(mRvCountryCode, false);
        setViewVisiable(mGroupVerificationCode, false);
    }

    @Override
    public void showCountryView() {
        mTvTitle.setText("国家或地区");
        setViewVisiable(mIvBack, true);
        setViewVisiable(mRvCountryCode, true);
        setViewVisiable(mGroupPhone, false);
        setViewVisiable(mGroupVerificationCode, false);
    }

    @Override
    public void showVerificationCodeView() {
        mTvTitle.setText("输入短信验证码");
        setViewVisiable(mIvBack, true);
        setViewVisiable(mGroupPhone, false);
        setViewVisiable(mRvCountryCode, false);
        setViewVisiable(mGroupVerificationCode, true);
    }

    @Override
    public void showToast(String message) {
        ToastUtils.showToast(this, message);
    }

    @Override
    public void close() {
        finish();
    }

    private void setViewVisiable(View view, boolean visiable) {
        if (view == null) {
            return;
        }
        view.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBindConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title("请您再次确认")
                .content("该手机号码已被其他设备绑定，之前设备的权益将会取消，是否继续操作？")
                .positiveText(R.string.dialog_confirm)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> mPresenter.bindPhone(getPhone(), getVerificationCode(), "1"))
                .onNegative(((dialog, which) -> dialog.dismiss()))
                .show();
    }

    @NotNull
    private String getPhone() {
        return mEtPhone.getText().toString().trim();
    }

    @NotNull
    private String getVerificationCode() {
        return mEtVerificationCode.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        if (mDisposableGetCode != null) {
            mDisposableGetCode.dispose();
            mDisposableGetCode = null;
        }
        mEmitterGetCode = null;
        super.onDestroy();
    }
}
