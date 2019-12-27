package com.video.test.ui.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;

import com.video.test.AppConstant;
import com.video.test.R;

/**
 * @author Enoch Created on 2018/9/28.
 */
public class ShareDialogFragment extends DialogFragment {
    private static final String TAG = "ShareDialogFragment";

    ShareItemClickListener shareItemClickListener;


    public static ShareDialogFragment newInstance() {
        Bundle bundle = new Bundle();
        ShareDialogFragment fragment = new ShareDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomDialog);
    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        //设置 dialog的背景为 透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //去除标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.bean_dialog_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvCancel = view.findViewById(R.id.tv_cancel_shareDialog);
        TextView tvWeiXin = view.findViewById(R.id.tv_weixin_icon_shareDialog);
        TextView tvFriends = view.findViewById(R.id.tv_friends_icon_shareDialog);


        tvCancel.setOnClickListener(new ItemClickListener());
        tvWeiXin.setOnClickListener(new ItemClickListener());
        tvFriends.setOnClickListener(new ItemClickListener());

    }


    private class ItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_weixin_icon_shareDialog:
                    shareItemClickListener.onShareItemClick(AppConstant.WX_SCENE_SESSION);
                    break;
                case R.id.tv_friends_icon_shareDialog:
                    shareItemClickListener.onShareItemClick(AppConstant.WX_SCENE_TIMELINE);
                    break;
                default:
                    dismiss();
                    break;
            }
            //这里分享完成后不隐藏dialog
//            dismiss();
        }
    }

    public void setShareItemClickListener(ShareItemClickListener shareItemClickListener) {
        this.shareItemClickListener = shareItemClickListener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);

        } catch (IllegalStateException e) {
            Log.d(TAG, "show state error: " + e.getMessage());
        }
    }


    public interface ShareItemClickListener {

        void onShareItemClick(int position);

    }
}
