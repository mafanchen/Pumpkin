package com.video.test.module.setphone;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.javabean.UserCenterBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/setPhone/activity")
public class SetPhoneActivity extends BaseActivity<SetPhonePresenter> implements SetPhoneContract.View {
    private static final String TAG = "SetPhoneActivity";

    @BindView(R.id.et_checkCode_setPhone)
    EditText mEtCheckCode;
    @BindView(R.id.tv_countryCode_newPhone_setPhone)
    TextView mTvCountryCode;
    @BindView(R.id.et_newPhone_setPhone)
    EditText mEtPhone;
    @BindView(R.id.tv_selectCountry_newPhone_setPhone)
    TextView mTvSelectCountry;
    @BindArray(R.array.countryCode)
    String[] mCountryCode;
    @BindArray(R.array.country)
    String[] mCountryName;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    @BindView(R.id.tv_title_toolbar)
    TextView mtvTitle;
    @BindView(R.id.tv_phoneNum_setPhone)
    TextView mTvPhoneNum;
    @BindView(R.id.tv_btn_check_setPhone)
    TextView mTvBtnCheckCode;
    private MaterialDialog mProgressDialog;
    @Autowired
    String phone;
    @Autowired
    String code;


    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_set_phone;
    }

    @Override
    protected void initData() {
        //这里当用户从banner进入时，没有带入参数，从sp获取
        if (code == null || phone == null) {
            UserCenterBean userInfo = SpUtils.getSerializable(this, AppConstant.USER_INFO);
            if (userInfo != null) {
                code = userInfo.getCountry_code();
                phone = userInfo.getMobile();
            }
        }
        if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(phone)) {
            StringBuffer buffer = new StringBuffer();
            if (phone.length() == 11) {
                for (int i = 0; i < phone.length(); i++) {
                    if (i < 3 || i > 6) {
                        char c = phone.charAt(i);
                        buffer.append(c);
                    } else {
                        buffer.append("*");
                    }
                }
            } else {
                buffer.append(phone);
            }
            mTvPhoneNum.setText(getString(R.string.setPhone_bond_number, code, buffer.toString()));
        } else {
            mTvPhoneNum.setText(getString(R.string.setPhone_can_not_bind));
        }
    }

    @Override
    protected void initToolBar() {
        if (null != mIbBack && null != mtvTitle) {
            mIbBack.setVisibility(View.VISIBLE);
            mtvTitle.setText(R.string.activity_userCenter_bind_phone);
        }
    }


    @Override
    public void getCheckCodeMessage(String message) {
        ToastUtils.showToast(TestApp.getContext(), message);
    }

    @Override
    public void bindPhoneSuccess() {
        ToastUtils.showLongToast(TestApp.getContext(), "绑定成功");
        finish();
    }

    @Override
    public void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .content(R.string.dialog_progress_waiting)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideProgressDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showSetPhoneDialog() {
        if (!TextUtils.isEmpty(mEtCheckCode.getText())) {
            if (!TextUtils.isEmpty(mEtPhone.getText())) {
                new MaterialDialog.Builder(this)
                        .title("请您再次确认")
                        .content("手机号码为:\n（+" + mTvCountryCode.getText() + "） " + mEtPhone.getText())
                        .positiveText(R.string.dialog_confirm)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (!TextUtils.isEmpty(phone)) {
                                    mPresenter.updatePhone(mTvCountryCode, mEtPhone, mEtCheckCode, "");
                                } else {
                                    mPresenter.bindPhone(mTvCountryCode, mEtPhone, mEtCheckCode, "");
                                }
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();
            } else {
                ToastUtils.showToast(TestApp.getContext(), "新手机号不能为空");
            }
        } else {
            ToastUtils.showToast(TestApp.getContext(), "验证码不能为空");
        }
    }

    @Override
    public void showBindConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title("请您再次确认")
                .content("该手机号码已被其他设备绑定，之前设备的权益将会取消，是否继续操作？")
                .positiveText(R.string.dialog_confirm)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> {
                    mPresenter.bindPhone(mTvCountryCode, mEtPhone, mEtCheckCode, "1");
                }).onNegative(((dialog, which) -> {
            dialog.dismiss();
        })).show();
    }

    @Override
    public void showUpdateConfirmDialog() {
        new MaterialDialog.Builder(this)
                .title("请您再次确认")
                .content("该设备已绑定其他手机号码，之前手机号码会被解除绑定，是否继续操作？")
                .positiveText(R.string.dialog_confirm)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> {
                    mPresenter.updatePhone(mTvCountryCode, mEtPhone, mEtCheckCode, "1");
                }).onNegative(((dialog, which) -> {
            dialog.dismiss();
        })).show();
    }


    @OnClick({R.id.tv_btn_check_setPhone, R.id.tv_background_newCountryCode_setPhone, R.id.tv_btn_confirm_setPhone, R.id.ib_back_toolbar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_background_newCountryCode_setPhone:
                new MaterialDialog.Builder(this)
                        .title("请选择您的地区")
                        .items(R.array.countryCodeDisplay)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                LogUtils.d(TAG, "onSelection  position & text ==" + position + " & " + text + "countryCode == " + mCountryCode[position]);
                                mTvCountryCode.setText(mCountryCode[position]);
                                mTvSelectCountry.setText(mCountryName[position]);
                            }
                        }).show();
                break;
            case R.id.tv_btn_check_setPhone:
                mPresenter.getCheckCode(mTvCountryCode, mEtPhone, mTvBtnCheckCode);
                break;
            case R.id.tv_btn_confirm_setPhone:
                LogUtils.d(TAG, "确认");
                showSetPhoneDialog();
                break;
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
                break;
        }
    }


    private class ToolbarClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
