package com.video.test.ui.widget;

import android.view.View;
import android.widget.ImageView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.HomeDialogBean;

import org.jetbrains.annotations.NotNull;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/10/29.
 */
public class HomeDialogFragment extends BaseHomeDialogFragment {
    private static final String TAG = "HomeDialogFragment";


    private ImageView mIvPic;

    public static HomeDialogFragment newInstance(HomeDialogBean bean) {
        HomeDialogFragment homeDialogFragment = new HomeDialogFragment();
        homeDialogFragment.mDialogBean = bean;
        return homeDialogFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.bean_dialog_home;
    }

    @Override
    protected void initView(@NotNull View view) {
        mIvClose = view.findViewById(R.id.iv_close_home_dialog);
        mIvPic = view.findViewById(R.id.iv_pic_home_dialog);
        mIvPic.setOnClickListener(view1 -> {
            if (mDialogItemClickListener != null && mDialogBean != null) {
                mDialogItemClickListener.onClick(mDialogBean, HomeDialogFragment.this);
            }
        });
        if (mDialogBean != null) {
            GlideApp.with(this).load(mDialogBean.getPic()).transition(withCrossFade()).into(mIvPic);
        }
    }
}
