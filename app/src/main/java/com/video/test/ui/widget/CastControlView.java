package com.video.test.ui.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.video.test.R;
import com.video.test.utils.LogUtils;

/**
 * @author Enoch Created on 2018/12/29.
 */
public class CastControlView extends ConstraintLayout {
    private static final String TAG = "CastControlView";
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    ControlListener mControlListener;
    private View mRootView;
    private TextView mIvBack;
    private TextView mTvCastTitle;
    private TextView mTvCastStatus;
    private TextView mTvCurrentTime;
    private TextView mTvTotalTime;
    private TextView mTvExitCast;
    private TextView mTvSwitchDevice;
    private ImageView mIvVolumePlus;
    private ImageView mIvVolumeMinus;
    private ImageView mIvStart;
    private TextView mTvFeedback;
    private SeekBar mSeekBarProgress;

    public CastControlView(Context context) {
        super(context, null);
    }

    public CastControlView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }


    public CastControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRootView = inflate(context, R.layout.bean_video_cast_control, this);
        initView();
    }

    private void initView() {
        mIvBack = mRootView.findViewById(R.id.iv_back_castControl);
        mTvCastTitle = mRootView.findViewById(R.id.tv_castTitle_castControl);
        mTvCastStatus = mRootView.findViewById(R.id.tv_castStatus_castControl);
        mTvCurrentTime = mRootView.findViewById(R.id.current_castControl);
        mTvTotalTime = mRootView.findViewById(R.id.total_castControl);
        mTvExitCast = mRootView.findViewById(R.id.tv_exitCast_castControl);
        mTvSwitchDevice = mRootView.findViewById(R.id.tv_switchDevice_castControl);
        mIvVolumePlus = mRootView.findViewById(R.id.ib_volume_plus_castControl);
        mIvVolumeMinus = mRootView.findViewById(R.id.ib_volume_minus_castControl);
        mIvStart = mRootView.findViewById(R.id.iv_start_castControl);
        mSeekBarProgress = mRootView.findViewById(R.id.progress_castControl);
        //初始化监听器
        initListener();

    }

    private void initListener() {
        mTvExitCast.setOnClickListener(new ViewClickListener());
        mTvFeedback.setOnClickListener(new ViewClickListener());
        mIvStart.setOnClickListener(new ViewClickListener());
        mIvVolumeMinus.setOnClickListener(new ViewClickListener());
        mIvVolumePlus.setOnClickListener(new ViewClickListener());
        mTvSwitchDevice.setOnClickListener(new ViewClickListener());
        mIvBack.setOnClickListener(new ViewClickListener());
        if (null != mSeekBarChangeListener) {
            mSeekBarProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);
        }
    }

    private void setSeekbarListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.mSeekBarChangeListener = onSeekBarChangeListener;


    }

    private void setmControlListener(ControlListener controlListener) {
        this.mControlListener = controlListener;
    }

    private void setCastTitle(String castTitle) {
        if (null != mTvCastTitle) {
            mTvCastTitle.setText(castTitle);
        }
    }

    private void setCastStatus(String castStatus) {
        if (null != mTvCastStatus) {
            mTvCastStatus.setText(castStatus);
        }
    }

    private void setCurrentTime(String currentTime) {
        if (null != mTvCurrentTime) {
            mTvCurrentTime.setText(currentTime);
        }
    }

    private void setTotalTime(String totalTime) {
        if (null != mTvTotalTime) {
            mTvTotalTime.setText(totalTime);
        }
    }

    private interface ControlListener {

        void volumePlus();

        void volumeMinus();

        void exitCast();

        void switchDevice();

        void exitPlayer();

        void startOrPausePlayer();

    }

    private class ViewClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (null != mControlListener) {
                switch (v.getId()) {
                    case R.id.iv_start_castControl:
                        LogUtils.d(TAG, "start_castControl");
                        mControlListener.startOrPausePlayer();
                        break;
                    case R.id.ib_volume_plus_castControl:
                        LogUtils.d(TAG, "ib_volume_plus_castControl");
                        mControlListener.volumePlus();
                        break;
                    case R.id.ib_volume_minus_castControl:
                        LogUtils.d(TAG, "ib_volume_minus_castControl");
                        mControlListener.volumeMinus();
                        break;
                    case R.id.tv_switchDevice_castControl:
                        LogUtils.d(TAG, "tv_switchDevice_castControl");
                        mControlListener.switchDevice();
                        break;
                    case R.id.tv_exitCast_castControl:
                        LogUtils.d(TAG, "tv_exitCast_castControl");
                        mControlListener.exitCast();
                        break;
                    case R.id.iv_back_castControl:
                        LogUtils.d(TAG, "iv_back_castControl");
                        mControlListener.exitPlayer();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
