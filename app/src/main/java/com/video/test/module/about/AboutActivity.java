package com.video.test.module.about;

import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.TestApp;
import com.video.test.R;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/about/activity")
public class AboutActivity extends BaseActivity<AboutPresenter> implements AboutContarct.View {
    private static final String TAG = "AboutActivity";

    @BindView(R.id.tv_version_about)
    TextView mTvVersion;
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    private long mClickTime;


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_about;
    }

    @Override
    protected void initToolBar() {
        if (null != mTvTitle && null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        String appVersionName = TestApp.getAppVersionName();
        mTvVersion.setText(String.format("V %s", appVersionName));
    }


    @OnClick({R.id.ib_back_toolbar, R.id.tv_version_about})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back_toolbar:
                finish();
                break;
            case R.id.tv_version_about:
                if (System.currentTimeMillis() - mClickTime < 500) {
                    showBetaVersion();
                } else {
                    mClickTime = System.currentTimeMillis();
                }
                break;
            default:
                break;
        }
    }

    private void showBetaVersion() {
        int appVersion = TestApp.getAppVersion();
        String appVersionName = TestApp.getAppVersionName();
        String betaVersion = getString(R.string.about_beta_version, BuildConfig.BETA_VERSION);
        String serverCode = SpUtils.getString(TestApp.getContext(), AppConstant.SERVER_VERSION_CODE, "");
        new AlertDialog.Builder(this)
                .setMessage("VersionName : " + appVersionName + "\n" +
                        "VersionCode : " + appVersion + "\n" +
                        "VersionBeta : " + appVersionName + betaVersion + "\n" +
                        "ServerCode :" + serverCode)
                .show();
    }
}
