package com.video.test.ui.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.framework.GlideApp;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author Enoch Created on 2018/10/29.
 */
public class RegisterDialogFragment extends DialogFragment {
    private static final String TAG = "GiftDialogFragment";

    DialogItemClickListener mDialogItemClickListener;
    private ImageView mIvClose;
    private ImageView mIvPic;
    private TextView mTvExpireTime;

    public static RegisterDialogFragment newInstance(Bundle args) {
        RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
        if (null != args) {
            registerDialogFragment.setArguments(args);
        }
        return registerDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.update_dialog);
    }


    @Override
    public void onStart() {
        super.onStart();

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = PixelUtils.dp2px(dialogWindow.getContext(), 80);
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.bean_dialog_gift, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    private void initView(View view) {
        mIvClose = view.findViewById(R.id.iv_confirm_gift_dialog);
        mIvPic = view.findViewById(R.id.iv_pic_home_dialog);
        mTvExpireTime = view.findViewById(R.id.tv_expireTime_gift_dialog);
        mIvClose.setOnClickListener(v -> mDialogItemClickListener.onClick());
    }


    private void initData() {
        Bundle arguments = getArguments();
        if (null != arguments) {
            String closeUrl = arguments.getString("closeUrl");
            String picUrl = arguments.getString("picUrl");
            String expireTime = arguments.getString("expireTime");

            LogUtils.d(TAG, "initData " + closeUrl + " = " + picUrl + " = " + expireTime);

            GlideApp.with(this).load(closeUrl).transition(withCrossFade()).into(mIvClose);
            GlideApp.with(this).load(picUrl).centerInside().transition(withCrossFade()).into(mIvPic);

            mTvExpireTime.setText(expireTime);

        }
    }

    public void setDialogItemClickListener(DialogItemClickListener dialogItemClickListener) {
        this.mDialogItemClickListener = dialogItemClickListener;
    }


    public interface DialogItemClickListener {
        void onClick();
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);

        } catch (IllegalStateException e) {
            Log.d(TAG, "show state error: " + e.getMessage());
        }

    }

}
