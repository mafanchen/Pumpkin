package com.video.test.module.share;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.video.test.R;
import com.video.test.javabean.ShareJoinEventBean;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

@Route(path = "/share/detail/activity")
public class ShareDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_title_toolbar)
    TextView mTvTitle;
    @BindView(R.id.ib_back_toolbar)
    ImageButton mIbBack;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_detail);
        unbinder = ButterKnife.bind(this);
        initToolBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatueBarColor();
        }
    }

    protected void setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.nav_background));
            window.setNavigationBarColor(getResources().getColor(R.color.navigation_background));
            ViewGroup contentView = this.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (null != childView) {
                ViewCompat.setFitsSystemWindows(childView, true);
            }
        }
    }

    @OnClick({R.id.btn_join, R.id.ib_back_toolbar})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_join:
                EventBus.getDefault().post(new ShareJoinEventBean());
                finish();
                break;
            case R.id.ib_back_toolbar:
                finish();
                break;
            default:
        }
    }

    private void initToolBar() {
        mTvTitle.setText(getString(R.string.nav_vip));
        mIbBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
