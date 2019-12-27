package com.video.test.module.sharepage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.R;
import com.video.test.module.share.ShareFragment;
import com.video.test.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/share/activity")
public class SharePageActivity extends BaseActivity<SharePagePresenter> implements SharePageContract.View {
    private static final String TAG = "ShareActivity";
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_share;
    }


    @Override
    protected void initToolBar() {
        if (null != mIbBack) {
            mIbBack.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        super.initView();
        FragmentManager manager = getSupportFragmentManager();
        Fragment shareFragment = manager.findFragmentByTag("shareFragment");
        if (shareFragment == null) {
            shareFragment = ShareFragment.newInstance("shareFragment", true);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.container, shareFragment, "shareFragment");
            transaction.commit();
        }
    }

    @OnClick({R.id.ib_back_toolbar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
                break;
        }
    }

}
