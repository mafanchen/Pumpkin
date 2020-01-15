package com.video.test.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.video.test.R;
import com.video.test.ui.listener.VideoFunctionListener;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.GifHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.TimeUtils;
import com.video.test.utils.ToastUtils;

import java.io.File;
import java.util.Locale;

/**
 * 本地播放器，只有横屏模式
 *
 * @author : AhhhhDong
 * @date : 2019/5/21 17:21
 */
public class LocalLandVideoPlayer extends StandardGSYVideoPlayer {

    private static final String TAG = "LocalLandVideoPlayer";

    /**
     * 播放速度
     */
    public static final float PLAY_SPEED_100X = 1.00f;
    public static final float PLAY_SPEED_125X = 1.25f;
    public static final float PLAY_SPEED_150X = 1.50f;
    public static final float PLAY_SPEED_175X = 1.75f;
    public static final float PLAY_SPEED_200X = 2.00f;

    private VideoFunctionListener mVideoFunctionListener;
    /**
     * 快进
     */
    private TextView mIvFastForward;
    /**
     * 快退
     */
    private TextView mIvRewind;
    /**
     * 倍速播放
     */
    private TextView mTvSpeed;
    /**
     * 倍速播放的RadioGroup
     */
    private RadioGroup mGroupSpeed;
    /**
     * 当前选中的播放速度
     */
    private float mSpeed = PLAY_SPEED_100X;
    /**
     * 分享
     */
    private ImageView mIvShare;
    private View mLayoutShare;
    private TextView mTvShareWX;
    private TextView mTvShareFriends;
    private TextView mTvShareUrl;
    private RelativeLayout mLayoutControl;


    /**
     * 录制gif的计时界面
     */
    private LinearLayout mLayoutCapture;
    private TextView mTvCaptureTimer;
    private TextView mTVCaptureStatus;
    /**
     * 截屏
     */
    private ImageView mIvCapturePhoto;
    /**
     * 录制GIF
     */
    private ImageView mIvCaptureGif;
    private GifHelper mGifHelper;
    /**
     * 图片保存路径
     */
    private static final File CAPTURE_CACHE_FILE = DownloadUtil.getCacheDirFile();
    /**
     * 开始截屏的时间
     */
    private long mStartCaptureTime;
    /**
     * 是否正在录制
     */
    private boolean mIsCapturing;

    private static final int MIN_CAPTURE_LENGTH = 3000;
    private static final int MAX_CAPTURE_LENGTH = 10000;
    private static final int CAPTURE_TIMER_DELAY = 50;
    private CenterDrawableTextView mTvTimeBatter;

    public LocalLandVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public LocalLandVideoPlayer(Context context) {
        super(context);
    }

    public LocalLandVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.bean_video_player_land;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mIvFastForward = findViewById(R.id.tv_fast_forward);
        mIvRewind = findViewById(R.id.tv_rewind);
        mTvSpeed = findViewById(R.id.tv_speed);
        mGroupSpeed = findViewById(R.id.radio_group_speed);
        mIvShare = findViewById(R.id.iv_share);
        mLayoutShare = findViewById(R.id.layout_share);
        mTvShareWX = findViewById(R.id.tv_share_wx);
        mTvShareFriends = findViewById(R.id.tv_share_friends);
        mTvShareUrl = findViewById(R.id.tv_share_url);
        mLayoutCapture = findViewById(R.id.layout_capture);
        mIvCapturePhoto = findViewById(R.id.iv_capture_phone);
        mIvCaptureGif = findViewById(R.id.iv_capture_gif);
        mTvCaptureTimer = findViewById(R.id.tv_capture_gif_timer);
        mTVCaptureStatus = findViewById(R.id.tv_capture_gif_status);
        mLayoutControl = findViewById(R.id.layout_control);
        mTvTimeBatter = findViewById(R.id.cdtv_time_batter);

        hideUselessView();
        //快退
        if (mIvRewind != null) {
            mIvRewind.setOnClickListener(view -> rewind());
        }
        //快进
        if (mIvFastForward != null) {
            mIvFastForward.setOnClickListener(view -> fastForward());
        }
        //播放速度
        if (mGroupSpeed != null) {
            mGroupSpeed.setOnCheckedChangeListener((radioGroup, radioId) -> {
                float speed;
                switch (radioId) {
                    case R.id.radio_speed_100x:
                        speed = PLAY_SPEED_100X;
                        break;
                    case R.id.radio_speed_125x:
                        speed = PLAY_SPEED_125X;
                        break;
                    case R.id.radio_speed_150x:
                        speed = PLAY_SPEED_150X;
                        break;
                    case R.id.radio_speed_175x:
                        speed = PLAY_SPEED_175X;
                        break;
                    case R.id.radio_speed_200x:
                        speed = PLAY_SPEED_200X;
                        break;
                    default:
                        speed = PLAY_SPEED_100X;
                        break;
                }
                hideRightDialogView(mGroupSpeed);
                //因为每次切换横竖屏会重新创建页面，这里纪录下选择的播放速度，避免触发回调
                if (mSpeed == speed) {
                    return;
                }
                mSpeed = speed;
                if (mVideoFunctionListener != null) {
                    changeSpeed(mSpeed);
                }
            });
        }
        if (mTvSpeed != null) {
            mTvSpeed.setOnClickListener(view -> showRightDialogView(mGroupSpeed));
        }
        //分享
        if (mIvShare != null) {
            mIvShare.setOnClickListener(view -> {
                hideAllWidget();
                showRightDialogView(mLayoutShare);
            });
        }
        //分享到微信
        if (mTvShareWX != null) {
            mTvShareWX.setOnClickListener(view -> {
                if (mVideoFunctionListener != null) {
                    mVideoFunctionListener.onShareWX();
                }
                hideRightDialogView(mLayoutShare);
            });
        }
        //分享到朋友圈
        if (mTvShareFriends != null) {
            mTvShareFriends.setOnClickListener(view -> {
                if (mVideoFunctionListener != null) {
                    mVideoFunctionListener.onShareFriends();
                }
                hideRightDialogView(mLayoutShare);
            });
        }
        //分享复制链接
        if (mTvShareUrl != null) {
            mTvShareUrl.setOnClickListener(view -> {
                if (mVideoFunctionListener != null) {
                    mVideoFunctionListener.onCopyUrl();
                }
                hideRightDialogView(mLayoutShare);
            });
        }
        if (mIvCaptureGif != null) {
            mGifHelper = new GifHelper(this, new GifHelper.GifSaveListener() {

                @Override
                public void result(boolean success, File gif, File cover) {
                    //切换到主线程
                    post(() -> {
                        setViewShowState(mLayoutCapture, INVISIBLE);
                        if (success) {
                            showShareGifView(gif, cover, true);
                        } else {
                            ToastUtils.showToast("生成gif异常");
                        }
                    });
                }

            });
            mIvCaptureGif.setOnTouchListener((view, motionEvent) -> {
                if (mVideoFunctionListener == null || !mVideoFunctionListener.hasStoragePermission()) {
                    return false;
                }
                if (mCurrentState == CURRENT_STATE_PREPAREING || mCurrentState == CURRENT_STATE_ERROR) {
                    return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    startCaptureGif();
                    //开始录制，需要隐藏其他控件，显示录制按钮和录制界面，并且把录制按钮背景改为按住的背景
                    setViewShowState(mIvCaptureGif, VISIBLE);
                    mIvCaptureGif.setBackgroundResource(R.drawable.shape_bg_capture_gif_pressed);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //录制完成后，先隐藏录制按钮
                    mIvCaptureGif.setBackgroundResource(0);
                    setViewShowState(mIvCaptureGif, INVISIBLE);
                    stopCaptureGif();
                }
                return true;
            });
            if (mIvCapturePhoto != null) {
                mIvCapturePhoto.setOnClickListener(view -> {
                    if (mVideoFunctionListener == null || !mVideoFunctionListener.hasStoragePermission()) {
                        return;
                    }
                    if (mCurrentState == CURRENT_STATE_PREPAREING || mCurrentState == CURRENT_STATE_ERROR) {
                        return;
                    }
                    File file = new File(CAPTURE_CACHE_FILE, "capture" + System.currentTimeMillis() + ".png");
                    saveFrame(file, (success, file1) -> {
                        if (success) {
                            showShareGifView(file1, null, false);
                        } else {
                            ToastUtils.showToast("截图异常");
                        }
                    });
                });
            }

        }
    }

    private void hideUselessView() {
        setViewShowState(findViewById(R.id.iv_more), GONE);
        setViewShowState(findViewById(R.id.tv_choose_video), GONE);
        setViewShowState(findViewById(R.id.iv_next), GONE);
    }

    /**
     * 开始录制gif
     */
    @SuppressLint("SetTextI18n")
    private void startCaptureGif() {
        mIsCapturing = true;
        hideAllWidget();
        mGifHelper.startGif(CAPTURE_CACHE_FILE);
        setViewShowState(mLayoutCapture, VISIBLE);
        if (mTvCaptureTimer != null) {
            mTvCaptureTimer.setText("0.00 S");
            mTVCaptureStatus.setText("正在截取gif...");
        }
        Runnable timerRun = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis() - mStartCaptureTime;
                //最大10秒
                if (time >= MAX_CAPTURE_LENGTH) {
                    time = MAX_CAPTURE_LENGTH;
                    stopCaptureGif();
                }
                if (mTvCaptureTimer != null) {
                    mTvCaptureTimer.setText(String.format(Locale.getDefault(), "%.2f\tS", time / 1000f));
                }
                //判断是否继续计时
                if (mIvCaptureGif != null && mIsCapturing) {
                    mIvCaptureGif.postDelayed(this, CAPTURE_TIMER_DELAY);
                }
            }
        };
        //开始计时
        mStartCaptureTime = System.currentTimeMillis();
        mIvCaptureGif.postDelayed(timerRun, CAPTURE_TIMER_DELAY);
    }

    @SuppressLint("SetTextI18n")
    private void stopCaptureGif() {
        //如果因为达到最大时间自己停了，则无需处理
        if (!mIsCapturing) {
            return;
        }
        mIsCapturing = false;
        long endTime = System.currentTimeMillis();
        //小于3秒不处理
        if (endTime - mStartCaptureTime < MIN_CAPTURE_LENGTH) {
            mGifHelper.cancelTask();
            ToastUtils.showToast("gif录制时间最短3秒");
            setViewShowState(mLayoutCapture, INVISIBLE);
        } else {
            File file = new File(CAPTURE_CACHE_FILE, "capture" + endTime + ".gif");
            mGifHelper.stopGif(file);
            mTVCaptureStatus.setText("正在生成gif...");
        }
    }

    private void showShareGifView(File file, File cover, boolean isGif) {
        //先暂停播放
        onVideoPause();
        if (mVideoFunctionListener != null) {
            mVideoFunctionListener.onCaptureImage(file, cover, isGif);
        }
    }

    protected void showRightDialogView(View view) {
        if (view == null || view.getVisibility() == View.VISIBLE) {
            return;
        }
        hideAllWidget();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.video_play_dialog_show_right);
        view.startAnimation(animation);
        view.setVisibility(VISIBLE);
    }

    protected void hideRightDialogView(View view) {
        if (view == null || view.getVisibility() != View.VISIBLE) {
            return;
        }
        hideAllWidget();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.video_play_dialog_hide_right);
        view.startAnimation(animation);
        view.setVisibility(INVISIBLE);
    }

    @Override
    protected void updateStartImage() {
//        if (mIfCurrentIsFullscreen) {
        if (mStartButton instanceof TextView) {
            TextView textView = (TextView) mStartButton;
            if (mCurrentState == CURRENT_STATE_PLAYING) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_pause_center, 0, 0);
                textView.setText(R.string.player_text_pause);
            } else if (mCurrentState == CURRENT_STATE_ERROR) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_play_center, 0, 0);
                textView.setText(R.string.player_text_play);
            } else {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_play_center, 0, 0);
                textView.setText(R.string.player_text_play);
            }
//        }
        } else {
            super.updateStartImage();
        }
    }

    /**
     * 触摸屏幕事件
     */
    @Override
    protected void onClickUiToggle() {
//        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
        if (mLockCurScreen) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mGroupSpeed != null && mGroupSpeed.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mGroupSpeed);
            return;
        }
        if (mLayoutShare != null && mLayoutShare.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mLayoutShare);
            return;
        }
        super.onClickUiToggle();
    }

    /**
     * 处理锁屏屏幕触摸逻辑
     */
    @Override
    protected void lockTouchLogic() {
        if (mLockCurScreen) {
            mLockScreen.setImageResource(R.drawable.ic_unlock);
            mLockCurScreen = false;
        } else {
            mLockScreen.setImageResource(R.drawable.ic_lock);
            mLockCurScreen = true;
            hideAllWidget();
        }
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        setViewShowState(mIvFastForward, INVISIBLE);
        setViewShowState(mIvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        //如果是正在录制gif，则不隐藏gif按钮,
        // 这里由于如果对页面不操作，几秒过后会自动调用此方法隐藏所用控件，但是不应该在录制gif时隐藏gif按钮
        if (!mIsCapturing) {
            setViewShowState(mIvCaptureGif, INVISIBLE);
        }
        setViewShowState(mGroupSpeed, INVISIBLE);
        setViewShowState(mLayoutShare, INVISIBLE);
        // 自动隐藏 controlView 时,也隐藏黑色半透明遮罩层
        if (null != mLayoutControl) {
            mLayoutControl.setBackground(null);
        }

    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        setViewShowState(mIvFastForward, VISIBLE);
        setViewShowState(mIvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
        if (null != mLayoutControl) {
            mLayoutControl.setBackgroundResource(R.drawable.shape_background_player_control);
        }
        //获取电量信息
        getBatterStatus();
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        setViewShowState(mIvFastForward, VISIBLE);
        setViewShowState(mIvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        setViewShowState(mIvFastForward, VISIBLE);
        setViewShowState(mIvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        changeUiToClear();
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        changeUiToClear();
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear();
        setViewShowState(mIvFastForward, INVISIBLE);
        changeUiToClear();
    }

    @Override
    protected void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
        changeUiToClear();
    }

    @Override
    protected void changeUiToCompleteClear() {
        super.changeUiToCompleteClear();
        changeUiToClear();
    }

    @Override
    protected void changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear();
        changeUiToClear();
    }

    @Override
    protected void changeUiToClear() {
        super.changeUiToClear();
        setViewShowState(mIvFastForward, INVISIBLE);
        setViewShowState(mIvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
        if (null != mLayoutControl) {
            mLayoutControl.setBackground(null);
        }
    }

    public void setVideoFunctionListener(VideoFunctionListener videoFunctionListener) {
        LogUtils.d(TAG, "setVideoFunctionListener ");
        this.mVideoFunctionListener = videoFunctionListener;
    }

    /**
     * 快进
     */
    private void fastForward() {
        LogUtils.d(TAG, "mVideoPlayer fastForward 15s");
        //总时长
        long duration = getGSYVideoManager().getDuration();
        //当前播放的位置
        long currentPosition = getGSYVideoManager().getCurrentPosition();
        //预计快进之后的位置
        long afterPosition = currentPosition + 15 * 1000;
        if (afterPosition > duration) {
            afterPosition = duration;
        }
        seekTo(afterPosition);
    }

    /**
     * 快退
     */
    private void rewind() {
        LogUtils.d(TAG, "mVideoPlayer rewind 15s");
        //当前播放的位置
        long currentPosition = getGSYVideoManager().getCurrentPosition();
        //预计快进之后的位置
        long afterPosition = currentPosition - 15 * 1000;
        if (afterPosition < 0) {
            afterPosition = 0;
        }
        seekTo(afterPosition);
    }

    /**
     * 播放速度
     *
     * @param speed 播放速度
     */
    private void changeSpeed(float speed) {
        LogUtils.d(TAG, "mVideoPlayer change play speed to " + speed);
        setSpeed(speed);
    }


    void getBatterStatus() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, intentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        String nowHourMinuteString = TimeUtils.getNowHourMinuteString();
        LogUtils.d(TAG, "Batter Pct : " + batteryPct + " level : " + level + " scale : " + scale);
        if (null != mTvTimeBatter) {
            mTvTimeBatter.setText(nowHourMinuteString);
            if (0.8f < batteryPct && 1f >= batteryPct) {
                mTvTimeBatter.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_batter_100, 0, 0);
            } else if (0.5f < batteryPct && 0.8f >= batteryPct) {
                mTvTimeBatter.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_batter_80, 0, 0);
            } else if (0.2f < batteryPct && 0.5f >= batteryPct) {
                mTvTimeBatter.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_batter_50, 0, 0);
            } else if (0.1f < batteryPct && 0.2f >= batteryPct) {
                mTvTimeBatter.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_batter_20, 0, 0);
            } else if (0.1f >= batteryPct) {
                mTvTimeBatter.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_batter_10, 0, 0);
            }
        }
    }

}
