package com.video.test.module.share;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.video.test.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Reus
 * @date 2019/3/6
 * 活动规则弹窗页面
 */
public class RuleDialogFragment extends DialogFragment {

    public static final String TAG = "RuleDialogFragment";

    private Unbinder mBinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去除标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.bean_fragment_share_rule, container, false);
        if (null == mBinder) {
            mBinder = ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }
}
