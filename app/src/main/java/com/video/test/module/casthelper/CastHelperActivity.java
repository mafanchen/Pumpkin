package com.video.test.module.casthelper;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.R;
import com.video.test.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author Enoch Created on 2018/6/27.
 */

@Route(path = "/castHelper/activity")
public class CastHelperActivity extends BaseActivity<CastHelperPresenter> implements CastHelperContract.View {

    private static final String TAG = "CastHelperActivity";
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageView mIvBack;


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_cast_helper;
    }


    @Override
    protected void initData() {
        if (null != mTvTitle && null != mIvBack) {
            mTvTitle.setText(R.string.dialog_cast_castHelper);
            mIvBack.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ib_back_toolbar)
    void backClick() {
        finish();
    }

}
