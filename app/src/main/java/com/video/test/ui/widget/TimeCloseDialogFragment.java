package com.video.test.ui.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.video.test.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 定时关闭视频的弹窗
 *
 * @author : AhhhhDong
 * @date : 2019/5/20 17:23
 */
public class TimeCloseDialogFragment extends DialogFragment {

    private TextView mTvExit;

    private OnButtonClickListener onButtonClickListener;

    private int closeDialogTime = 15;

    private int currentCloseTime = 0;
    private Disposable closeTimer;

    public static TimeCloseDialogFragment newInstance(OnButtonClickListener onButtonClickListener) {
        TimeCloseDialogFragment fragment = new TimeCloseDialogFragment();
        fragment.onButtonClickListener = onButtonClickListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.bean_fragment_time_close, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mTvContinue = view.findViewById(R.id.tv_continue);
        mTvExit = view.findViewById(R.id.tv_exit);
        mTvContinue.setOnClickListener(view1 -> onContinue());
        mTvExit.setOnClickListener(view1 -> onExit());
        closeTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    currentCloseTime++;
                    //倒计时结束，关闭窗体
                    mTvExit.setText(String.format(Locale.getDefault(), "退出播放(%ds)", closeDialogTime - currentCloseTime));
                    if (currentCloseTime >= closeDialogTime) {
                        onExit();
                    }
                });
    }

    private void onExit() {
        if (closeTimer != null) {
            closeTimer.dispose();
        }
        if (onButtonClickListener != null) {
            onButtonClickListener.onExit();
        }
        dismiss();
    }

    private void onContinue() {
        if (closeTimer != null) {
            closeTimer.dispose();
        }
        if (onButtonClickListener != null) {
            onButtonClickListener.onContinue();
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (closeTimer != null) {
            closeTimer.dispose();
        }
    }

    public interface OnButtonClickListener {
        /**
         * 退出播放
         */
        void onExit();

        /**
         * 继续观看
         */
        void onContinue();
    }

}
