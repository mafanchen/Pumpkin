package com.video.test.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.VideoAdControl;
import com.video.test.ui.adapter.PlayerChooseCastDeviceAdapter;
import com.video.test.ui.adapter.PlayerChooseVideoAdapter;
import com.video.test.ui.listener.FullscreenCastListener;
import com.video.test.ui.listener.VideoFunctionListener;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.GifHelper;
import com.video.test.utils.LogUtils;
import com.video.test.utils.StringUtils;
import com.video.test.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * 视频播放器,带投屏
 *
 * @author AhhhhDong
 */
public class LandLayoutVideo extends StandardGSYVideoPlayer implements VideoAdControl.AdVideoPlayer {
    private static final String TAG = "LandLayoutVideo";

    /**
     * 定时关闭
     */
    public static final int CLOSE_TIME_NONE = 0;
    public static final int CLOSE_TIME_CURRENT_VIDEO = -1;
    public static final int CLOSE_TIME_30_MINUTES = 30;
    public static final int CLOSE_TIME_60_MINUTES = 60;

    /**
     * 播放速度
     */
    public static final float PLAY_SPEED_100X = 1.00f;
    public static final float PLAY_SPEED_125X = 1.25f;
    public static final float PLAY_SPEED_150X = 1.50f;
    public static final float PLAY_SPEED_175X = 1.75f;
    public static final float PLAY_SPEED_200X = 2.00f;

    /**
     * 默认
     */
    public static final int RATIO_DEFAULT = GSYVideoType.SCREEN_TYPE_DEFAULT;
    /**
     * 平铺
     */
    public static final int RATIO_TILE = GSYVideoType.SCREEN_TYPE_FULL;
    /**
     * 拉伸
     */
    public static final int RATIO_STRETCH = GSYVideoType.SCREEN_MATCH_FULL;

    private boolean isLinkScroll = false;

    private TextView mFeedBack;
    private TextView mNetSpeed;
    private VideoFunctionListener mVideoFunctionListener;
    private ImageView mTouchPic;
    /**
     * 播放下一集
     */
    private ImageView mIvNext;
    /**
     * 投屏
     */
    private View mIvTvCast;
    /**
     * 快进
     */
    private TextView mTvFastForward;
    /**
     * 快退
     */
    private TextView mTvRewind;
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
     * 选集
     */
    private TextView mTvChoose;
    /**
     * 分享
     */
    private ImageView mIvShare;
    private View mLayoutShare;
    private TextView mTvShareWX;
    private TextView mTvShareFriends;
    private TextView mTvShareUrl;

    /**
     * 更多
     */
    private ImageView mIvMore;
    private View mLayoutMore;

    /**
     * 收藏，在{@link #mLayoutMore}里面
     */
    private CheckBox mCheckBoxCollect;
    /**
     * 是否收藏
     */
    private boolean mCollected = false;

    /**
     * 定时关闭,在{@link #mLayoutMore}里面
     */
    private RadioGroup mGroupTimerClose;
    /**
     * 当前选中的关闭时间
     */
    private int mCloseTime = CLOSE_TIME_NONE;

    /**
     * 画面比例
     */
    private RadioGroup mGroupRatio;
    /**
     * 当前选中的画面比例
     */
    private int mRatio = RATIO_DEFAULT;

    /**
     * 连续播放
     */
    private RadioGroup mGroupContinuePlay;
    /**
     * 是否连续播放,默认开启
     */
    private boolean mIsContinuePlay = true;


    /**
     * 选集播放
     */
    private RecyclerView mRvVideoList;
    private PlayerChooseVideoAdapter mAdapterVideoList = null;

    /**
     * 投屏设备列表
     */
    private LinearLayout mLayoutCastDevice;
    private RecyclerView mRvCastDeviceList;


    /**
     * 横屏播放时的横屏投屏控制界面，由于使用乐播投屏，非播放器播放，故使用接口回调
     */
    private ConstraintLayout mLayoutCastControl;
    private ImageView mIvBackCastControl;
    private TextView mTvTitleCastControl;
    private TextView mTvDeviceNameCastControl;
    private TextView mTvStatusCastControl;
    private TextView mTvExitCastControl;
    private TextView mTvSwitchDeviceCastControl;
    private ImageButton mIbVolumePlusCastControl;
    private ImageButton mIbVolumeMinusCastControl;
    private ImageView mIvStartCastControl;
    private TextView mTvCurrentCastControl;
    private TextView mTvTotalCastControl;
    private SeekBar mSeekBarProgressCastControl;
    private TextView mTvChooseVideoCastControl;
    /**
     * 横屏投屏回调
     */
    private FullscreenCastListener mCastListener;

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

    private RelativeLayout mLayoutControl;
    /**
     * 播放广告时的横竖屏切换按钮
     */
    private ImageView mFullscreenAd;
    /**
     * 播放广告时的返回按钮
     */
    private ImageView mIvBackAd;
    /**
     * 广告剩余时间
     */
    private TextView mTvAdTime;
    /**
     * 跳过片头广告
     */
    private ImageView mIvSkipHeadAd;
    /**
     * 广告背景图片
     */
    private ImageView mIvAdBackground;
    /**
     * 是否正在显示片头广告
     */
    private boolean mIsShowingHeadAd;
    /**
     * 卡片广告
     */
    private FrameLayout mLayoutPlayAd;
    /**
     * 卡片广告关闭按钮
     */
    private TextView mTvClosePlayAd;
    /**
     * 卡片广告背景
     */
    private ImageView mIvBackgroundPlayAd;
    /**
     * 是否正在展示暂停广告
     */
    private boolean mIsShowingPauseAd;
    /**
     * 暂停广告
     */
    private FrameLayout mLayoutPauseAd;
    /**
     * 关闭暂停广告的按钮
     */
    private TextView mTvClosePauseAd;
    /**
     * 暂停广告的背景
     */
    private ImageView mIvBackgroundPauseAd;
    /**
     * 当前的视频源地址,用于播放完片头广告后，完成切换
     */
    private String mCurrentOriginUrl;
    private VideoAdControl mAdControl;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public LandLayoutVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public LandLayoutVideo(Context context) {
        super(context);
    }

    public LandLayoutVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    //这个必须配置最上面的构造才能生效

    @Override
    public int getLayoutId() {
        if (mIfCurrentIsFullscreen) {
            return R.layout.bean_video_player_land;
        }
        return R.layout.bean_video_player_normal;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mFeedBack = findViewById(R.id.feedback);
        mNetSpeed = findViewById(R.id.netSpeed);
        mTouchPic = findViewById(R.id.iv_touchPic);
        mIvTvCast = findViewById(R.id.iv_tv_cast);
        mTvFastForward = findViewById(R.id.tv_fast_forward);
        mTvRewind = findViewById(R.id.tv_rewind);
        mTvChoose = findViewById(R.id.tv_choose_video);
        mTvSpeed = findViewById(R.id.tv_speed);
        mGroupSpeed = findViewById(R.id.radio_group_speed);
        mRvVideoList = findViewById(R.id.rv_video_list);
        mIvShare = findViewById(R.id.iv_share);
        mLayoutShare = findViewById(R.id.layout_share);
        mTvShareWX = findViewById(R.id.tv_share_wx);
        mTvShareFriends = findViewById(R.id.tv_share_friends);
        mTvShareUrl = findViewById(R.id.tv_share_url);
        mIvMore = findViewById(R.id.iv_more);
        mLayoutMore = findViewById(R.id.layout_more);
        mCheckBoxCollect = findViewById(R.id.checkbox_collect);
        mGroupTimerClose = findViewById(R.id.radio_group_timer);
        mGroupRatio = findViewById(R.id.radio_group_ratio);
        mGroupContinuePlay = findViewById(R.id.radio_group_continue);
        mLayoutCastDevice = findViewById(R.id.layout_cast_device);
        mRvCastDeviceList = findViewById(R.id.rv_cast_device_list);
        mLayoutCastControl = findViewById(R.id.layout_cast_control);
        mIvBackCastControl = findViewById(R.id.iv_back_castControl);
        mTvTitleCastControl = findViewById(R.id.tv_castTitle_castControl);
        mTvDeviceNameCastControl = findViewById(R.id.tv_device_name_castControl);
        mTvStatusCastControl = findViewById(R.id.tv_castStatus_castControl);
        mTvExitCastControl = findViewById(R.id.tv_exitCast_castControl);
        mTvSwitchDeviceCastControl = findViewById(R.id.tv_switchDevice_castControl);
        mIbVolumePlusCastControl = findViewById(R.id.ib_volume_plus_castControl);
        mIbVolumeMinusCastControl = findViewById(R.id.ib_volume_minus_castControl);
        mIvStartCastControl = findViewById(R.id.iv_start_castControl);
        mTvCurrentCastControl = findViewById(R.id.tv_current_castControl);
        mTvTotalCastControl = findViewById(R.id.tv_total_castControl);
        mSeekBarProgressCastControl = findViewById(R.id.seekbar_progress_castControl);
        mTvChooseVideoCastControl = findViewById(R.id.tv_choose_video_castControl);
        mLayoutCapture = findViewById(R.id.layout_capture);
        mIvCapturePhoto = findViewById(R.id.iv_capture_phone);
        mIvCaptureGif = findViewById(R.id.iv_capture_gif);
        mTvCaptureTimer = findViewById(R.id.tv_capture_gif_timer);
        mTVCaptureStatus = findViewById(R.id.tv_capture_gif_status);
        mIvNext = findViewById(R.id.iv_next);
        mFullscreenAd = findViewById(R.id.fullscreen_ad);
        mIvBackAd = findViewById(R.id.iv_head_ad_back);
        mLayoutControl = findViewById(R.id.layout_control);
        mTvAdTime = findViewById(R.id.tv_ad_time);
        mIvSkipHeadAd = findViewById(R.id.iv_close_head_ad);
        mIvAdBackground = findViewById(R.id.iv_background_ad);
        mLayoutPlayAd = findViewById(R.id.layout_play_ad);
        mTvClosePlayAd = findViewById(R.id.tv_close_play_ad);
        mIvBackgroundPlayAd = findViewById(R.id.iv_bg_play_ad);
        mLayoutPauseAd = findViewById(R.id.layout_pause_ad);
        mTvClosePauseAd = findViewById(R.id.tv_close_pause_ad);
        mIvBackgroundPauseAd = findViewById(R.id.iv_bg_pause_ad);
        if (mLayoutPauseAd != null) {
            mLayoutPauseAd.setOnClickListener(v -> mAdControl.clickPauseAd(mContext));
        }
        if (mTvClosePauseAd != null) {
            mTvClosePauseAd.setOnClickListener(v -> {
                if (mAdControl != null) {
                    mAdControl.stopPauseAdTimer();
                }
                mIsShowingPauseAd = false;
                if (mLayoutPauseAd != null) {
                    mLayoutPauseAd.setVisibility(GONE);
                }
                changeAdUIState();
            });
        }
        if (mLayoutPlayAd != null) {
            mLayoutPlayAd.setOnClickListener(v -> mAdControl.clickPlayAd(mContext));
        }
        if (mTvClosePlayAd != null) {
            mTvClosePlayAd.setOnClickListener(v -> {
                if (mLayoutPlayAd != null) {
                    mLayoutPlayAd.setVisibility(GONE);
                }
            });
        }
        //播放片头广告时点击返回按钮，横竖屏切换
        if (mFullscreenAd != null) {
            mFullscreenAd.setOnClickListener(view -> {
                if (mVideoFunctionListener != null) {
                    if (mIfCurrentIsFullscreen) {
                        mVideoFunctionListener.resolveToNormal();
                    } else {
                        mVideoFunctionListener.startFullScreen();
                    }
                }
            });
        }
        if (mIvBackAd != null) {
            mIvBackAd.setOnClickListener(v -> {
                if (mVideoFunctionListener != null) {
                    if (mIfCurrentIsFullscreen) {
                        mVideoFunctionListener.resolveToNormal();
                    } else {
                        mVideoFunctionListener.closeActivity();
                    }
                }
            });
        }
        if (mIvAdBackground != null) {
            mIvAdBackground.setOnClickListener(view -> mAdControl.clickHeadAd(mContext));
        }
        //播放片头广告时的跳过
        if (mTvAdTime != null) {
            mTvAdTime.setOnClickListener(view -> startPlayVideo());
        }
        if (mIvSkipHeadAd != null) {
            mIvSkipHeadAd.setOnClickListener(view -> startPlayVideo());
        }
        //播放下一集
        if (mIvNext != null) {
            mIvNext.setOnClickListener(view -> playNext());
        }

        mFeedBack.setOnClickListener(v -> {
            if (null != mVideoFunctionListener) {
                mVideoFunctionListener.onFeedbackClick();
            }
        });

        mIvTvCast.setOnClickListener(v -> {
            if (null != mVideoFunctionListener) {
                //判断是是否是横屏，如果是，则右侧弹出设备框，如果不是，则在activity中弹出设备框
                if (mIfCurrentIsFullscreen) {
                    hideRightDialogView(mLayoutMore);
                    //告诉activity需要启动乐播投屏，并且获取设备列表
                    if (mCastListener != null) {
                        //这里返回true则代表是vip,弹出播放列表
                        if (mCastListener.onGetDeviceList()) {
                            showRightDialogView(mLayoutCastDevice);
                        }
                    }
                } else {
                    mVideoFunctionListener.onCastClick();
                }
            }
        });

        mTouchPic.setOnClickListener(v -> {
            if (mTouchPic.getVisibility() == VISIBLE) {
                LogUtils.d(TAG, "hide touch pic");
                mTouchPic.setVisibility(GONE);
            }
        });
        //快退
        if (mTvRewind != null) {
            mTvRewind.setOnClickListener(view -> rewind());
        }
        //快进
        if (mTvFastForward != null) {
            mTvFastForward.setOnClickListener(view -> fastForward());
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
        //选集播放
        if (mAdapterVideoList == null) {
            mAdapterVideoList = new PlayerChooseVideoAdapter();
        }
        if (mRvVideoList != null) {
            mRvVideoList.setLayoutManager(new GridLayoutManager(mContext, 3));
            mRvVideoList.setAdapter(mAdapterVideoList);
            mAdapterVideoList.setSelectedListener((position, bean) -> {
                if (mVideoFunctionListener != null) {
                    mVideoFunctionListener.onSelectVideo(position, bean);
                }
                hideRightDialogView(mRvVideoList);
            });
        }
        if (mTvChoose != null) {
            mTvChoose.setOnClickListener(view -> showRightDialogView(mRvVideoList));
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
        //更多
        if (mIvMore != null) {
            mIvMore.setOnClickListener(view -> {
                hideAllWidget();
                showRightDialogView(mLayoutMore);
            });
        }
        //收藏
        if (mCheckBoxCollect != null) {
            mCheckBoxCollect.setOnClickListener(view -> {
                mCollected = !mCheckBoxCollect.isChecked();
                if (mVideoFunctionListener != null) {
                    mVideoFunctionListener.onVideoCollect(mCollected);
                }
                hideRightDialogView(mLayoutMore);
            });
        }
        //定时关闭
        if (mGroupTimerClose != null) {
            mGroupTimerClose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int childId) {
                    int closeTime;
                    switch (childId) {
                        //不开启
                        case R.id.radio_timer_none:
                            closeTime = CLOSE_TIME_NONE;
                            break;
                        //播完当前集
                        case R.id.radio_timer_current_length:
                            closeTime = CLOSE_TIME_CURRENT_VIDEO;
                            break;
                        //30分钟
                        case R.id.radio_timer_30_minutes:
                            closeTime = CLOSE_TIME_30_MINUTES;
                            break;
                        //60分钟
                        case R.id.radio_timer_60_minutes:
                            closeTime = CLOSE_TIME_60_MINUTES;
                            break;
                        default:
                            closeTime = CLOSE_TIME_NONE;
                            break;
                    }
                    hideRightDialogView(mLayoutMore);
                    //因为每次切换横竖屏会重新创建页面，这里纪录下选择的时间，避免触发回调，导致切换横竖屏导致重新计时
                    if (mCloseTime == closeTime) {
                        return;
                    }
                    mCloseTime = closeTime;
                    if (mVideoFunctionListener != null) {
                        mVideoFunctionListener.onTimerClose(mCloseTime);
                    }
                }
            });
        }
        //画面比例
        if (mGroupRatio != null) {
            mGroupRatio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int childId) {
                    int ratio;
                    switch (childId) {
                        //默认
                        case R.id.radio_ratio_default:
                            ratio = RATIO_DEFAULT;
                            break;
                        //平铺
                        case R.id.radio_ratio_tile:
                            ratio = RATIO_TILE;
                            break;
                        //拉伸
                        case R.id.radio_ratio_stretch:
                            ratio = RATIO_STRETCH;
                            break;
                        default:
                            ratio = RATIO_DEFAULT;
                            break;
                    }
                    hideRightDialogView(mLayoutMore);
                    //因为每次切换横竖屏会重新创建页面，这里纪录下选择的画面比例，避免触发回调
                    if (mRatio == ratio) {
                        return;
                    }
                    mRatio = ratio;
                    changeRatio(mRatio);
                }
            });
        }

        if (mGroupContinuePlay != null) {
            mGroupContinuePlay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int childId) {
                    switch (childId) {
                        //开启
                        case R.id.radio_continue_open:
                            mIsContinuePlay = true;
                            break;
                        //关闭
                        case R.id.radio_continue_close:
                            mIsContinuePlay = false;
                            break;
                        default:
                            mIsContinuePlay = false;
                            break;
                    }
                    hideRightDialogView(mLayoutMore);
                    LogUtils.d(TAG, "mVideoPlayer switch continue play to " + mIsContinuePlay);
                }
            });
        }
        //全屏投屏模式下的返回键，和播放时的返回键不同
        if (mIvBackCastControl != null) {
            mIvBackCastControl.setOnClickListener(view -> {
                //退出全屏播放
                if (mCastListener != null) {
                    mCastListener.onCastBackPressed();
                }
            });
        }
        //退出投屏
        if (mTvExitCastControl != null) {
            mTvExitCastControl.setOnClickListener(view -> {
                if (mCastListener != null) {
                    mCastListener.onExitCast();
                    hideCastLayout();
                }
            });
        }
        //切换投屏设备
        if (mTvSwitchDeviceCastControl != null) {
            mTvSwitchDeviceCastControl.setOnClickListener(view -> {
                if (mCastListener != null) {
                    hideAllWidget();
                    showRightDialogView(mLayoutCastDevice);
                    mCastListener.onSwitchDevice();
                }
            });
        }
        if (mLayoutCastDevice != null) {
            mLayoutCastDevice.setOnClickListener(v -> {
                if (mCastListener != null) {
                    hideRightDialogView(mLayoutCastDevice);
                    mCastListener.onCancelChooseDevice();
                }
            });
        }
        //增加投屏音量
        if (mIbVolumePlusCastControl != null) {
            mIbVolumePlusCastControl.setOnClickListener(view -> {
                if (mCastListener != null) {
                    mCastListener.onIncreaseVolume();
                }
            });
        }
        //减少投屏音量
        if (mIbVolumeMinusCastControl != null) {
            mIbVolumeMinusCastControl.setOnClickListener(view -> {
                if (mCastListener != null) {
                    mCastListener.onReduceVolume();
                }
            });
        }
        //投屏时切换进度
        if (mSeekBarProgressCastControl != null) {
            mSeekBarProgressCastControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress();
                    LogUtils.d(TAG, "seek click progress : " + progress);
                    if (mCastListener != null) {
                        mCastListener.onSeekProgress(progress);
                    }
                }
            });
        }
        //投屏开始和暂停播放
        if (mIvStartCastControl != null) {
            mIvStartCastControl.setOnClickListener(view -> {
                if (mCastListener != null) {
                    mCastListener.onStartAndPauseCastPlay();
                }
            });
        }
        //投屏选集
        if (mTvChooseVideoCastControl != null) {
            mTvChooseVideoCastControl.setOnClickListener(view -> showRightDialogView(mRvVideoList));
        }
        if (mLayoutCastControl != null) {
            mLayoutCastControl.setOnClickListener(view -> {
                if (mLayoutCastDevice != null && mLayoutCastDevice.getVisibility() == VISIBLE) {
                    hideRightDialogView(mLayoutCastDevice);
                    /*
                    这里隐藏设备列表分为两种情况
                    1.没有正在投屏，此时播放器已经暂停，需要恢复播放
                    2.已经正在投屏，此时无需任何操作
                    故需要回调
                     */
                    if (mCastListener != null) {
                        mCastListener.onCancelChooseDevice();
                    }
                }
                if (mRvVideoList != null && mRvVideoList.getVisibility() == View.VISIBLE) {
                    hideRightDialogView(mRvVideoList);
                }
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
                            ToastUtils.showToast(TestApp.getContext(), "生成gif异常");
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
                            ToastUtils.showToast(TestApp.getContext(), "截图异常");
                        }
                    });
                });
            }

        }
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
            ToastUtils.showToast(TestApp.getContext(), "gif录制时间最短3秒");
            setViewShowState(mLayoutCapture, INVISIBLE);
        } else {
            File file = new File(CAPTURE_CACHE_FILE, "capture" + endTime + ".gif");
            mGifHelper.stopGif(file);
            mTVCaptureStatus.setText("正在生成gif...");
        }
    }

    private void showShareGifView(File file, File cover, boolean isGif) {
//        先暂停播放
        super.onVideoPause();
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
    public boolean setUp(String url, boolean cacheWithPlay, String title) {
        //记录下视频的播放地址，方便在片头给广告结束后进行视频播放
        mCurrentOriginUrl = url;
        if (mTvTitleCastControl != null) {
            mTvTitleCastControl.setText(title);
        }
        if (mSpeed != 0) {
            changeSpeed(mSpeed);
        }
        return super.setUp(url, cacheWithPlay, title);
    }

    /**
     * 含广告的初始化
     *
     * @param videoUrl 视频地址
     * @param title    标题
     */
    public void setAdUp(VideoAdDataBean adData, String videoUrl, String title, boolean isVip) {
        if (mAdControl == null) {
            mAdControl = new VideoAdControl();
        }
        //是否是vip
        mAdControl.setVip(isVip);
        mAdControl.initAd(adData.getHeadAd(), adData.getPlayAd(), adData.getPauseAd());
        mAdControl.setPlayer(this);
        setUp(videoUrl, false, title);
        mIsShowingPauseAd = false;
        mIsShowingHeadAd = false;
    }


    /**
     * 这里覆写此方法，是为了在播放时，进行播放广告的拦截
     */
    @Override
    public void startPlayLogic() {
        //这里进行拦截，如果有广告则先播放广告
        if (mAdControl != null && mAdControl.playHeadAd()) {
            //将状态设置为正在显示片头广告，在此状态下，将隐藏视频正常的操作按钮
            mIsShowingHeadAd = true;
            changeAdUIState();
        } else {
            super.startPlayLogic();
        }
    }

    /**
     * 根据是否广告url修改ui显示状态
     */
    protected void changeAdUIState() {
        //显示广告时间控件
        if (mTvAdTime != null) {
            mTvAdTime.setVisibility(mIsShowingHeadAd ? VISIBLE : GONE);
        }
        //广告模式下的全屏按钮
        if (mFullscreenAd != null) {
            mFullscreenAd.setVisibility(mIsShowingHeadAd ? VISIBLE : GONE);
        }
        //片头广告下的返回按钮
        if (mIvBackAd != null) {
            mIvBackAd.setVisibility(mIsShowingHeadAd ? VISIBLE : GONE);
        }
        //隐藏其他控件
        if (mLayoutControl != null) {
            mLayoutControl.setVisibility(mIsShowingHeadAd || mIsShowingPauseAd ? GONE : VISIBLE);
        }
        //广告背景
        if (mIvAdBackground != null) {
            mIvAdBackground.setVisibility(mIsShowingHeadAd ? VISIBLE : GONE);
        }
        if (mLayoutPlayAd != null && mIsShowingHeadAd) {
            mLayoutPlayAd.setVisibility(GONE);
        }
        if (mLayoutPauseAd != null && mIsShowingHeadAd) {
            mLayoutPauseAd.setVisibility(GONE);
        }
    }

    /**
     * 开始播放片头广告视频
     *
     * @param adUrl 广告链接
     */
    @Override
    public void startPlayVideoAd(@NotNull String adUrl) {
        mIsShowingHeadAd = true;
        //这里避免广告倍速播放
        changeSpeed(PLAY_SPEED_100X);
        super.setUp(adUrl, false, mTitle);
        super.startPlayLogic();
    }

    /**
     * 跳过片头广告
     */
    @Override
    public void startPlayVideo() {
        //如果没有停止计时，则停止计时
        if (mAdControl != null) {
            mAdControl.stopHeadVideoTimer();
        }
        setViewShowState(mIvSkipHeadAd, GONE);
        mIsShowingPauseAd = false;
        //播放完视频广告后，继续播放视频
        mIsShowingHeadAd = false;
        mIvAdBackground.setImageDrawable(null);
        changeAdUIState();
        setUp(mCurrentOriginUrl, false, mTitle);
        super.startPlayLogic();
    }

    /**
     * 当视频播放完成时的方法
     * 这里可以判断是否关闭视频(和设置定时关闭有关 {@link #mCloseTime})
     * 可以判断是否自动播放下一集
     * 当片头广告播放完之后，也通过此方法开始播放视频
     */
    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        if (mIsShowingHeadAd) {
            //需要点击跳过按钮才开始播放视频
//            startPlayVideo();
        } else {
            //播放完当前视频关闭页面
            if (mCloseTime == CLOSE_TIME_CURRENT_VIDEO && mVideoFunctionListener != null) {
                mVideoFunctionListener.onTimerCloseByCurrentLength();
                return;
            }
            if (mIsContinuePlay) {
                playNext();
            }
        }
    }

    @Override
    protected void updateStartImage() {
        if (mIfCurrentIsFullscreen) {
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
            }
        } else {
            if (mStartButton instanceof ImageView) {
                ImageView imageView = (ImageView) mStartButton;
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    imageView.setImageResource(R.drawable.ic_pause_vertical);
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    imageView.setImageResource(R.drawable.ic_play_vertical);
                } else {
                    imageView.setImageResource(R.drawable.ic_play_vertical);
                }
            } else {
                super.updateStartImage();
            }
        }
    }

    /**
     * 触摸屏幕事件
     */
    @Override
    protected void onClickUiToggle() {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        if (mGroupSpeed != null && mGroupSpeed.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mGroupSpeed);
            return;
        }
        if (mRvVideoList != null && mRvVideoList.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mRvVideoList);
            return;
        }
        if (mLayoutShare != null && mLayoutShare.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mLayoutShare);
            return;
        }
        if (mLayoutMore != null && mLayoutMore.getVisibility() == View.VISIBLE) {
            hideRightDialogView(mLayoutMore);
            return;
        }
        if (mLayoutCastDevice != null && mLayoutCastDevice.getVisibility() == VISIBLE) {
            hideRightDialogView(mLayoutCastDevice);
                    /*
                    这里隐藏设备列表分为两种情况
                    1.没有正在投屏，此时播放器已经暂停，需要恢复播放
                    2.已经正在投屏，此时无需任何操作
                    故需要回调
                     */
            if (mCastListener != null) {
                mCastListener.onCancelChooseDevice();
            }
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
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        //如果是正在录制gif，则不隐藏gif按钮,
        // 这里由于如果对页面不操作，几秒过后会自动调用此方法隐藏所用控件，但是不应该在录制gif时隐藏gif按钮
        if (!mIsCapturing) {
            setViewShowState(mIvCaptureGif, INVISIBLE);
        }
        setViewShowState(mGroupSpeed, INVISIBLE);
        setViewShowState(mRvVideoList, INVISIBLE);
        setViewShowState(mLayoutShare, INVISIBLE);
        setViewShowState(mLayoutMore, INVISIBLE);
    }

    @Override
    protected void changeUiToPlayingShow() {

        super.changeUiToPlayingShow();
        setViewShowState(mTvFastForward, VISIBLE);
        setViewShowState(mTvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
    }

    @Override
    protected void changeUiToPauseShow() {
        super.changeUiToPauseShow();
        setViewShowState(mTvFastForward, VISIBLE);
        setViewShowState(mTvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    protected void changeUiToCompleteShow() {
        super.changeUiToCompleteShow();
        setViewShowState(mTvFastForward, VISIBLE);
        setViewShowState(mTvRewind, VISIBLE);
        setViewShowState(mIvCapturePhoto, VISIBLE);
        setViewShowState(mIvCaptureGif, VISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    protected void changeUiToCompleteClear() {
        super.changeUiToCompleteClear();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    protected void changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    protected void changeUiToClear() {
        super.changeUiToClear();
        setViewShowState(mTvFastForward, INVISIBLE);
        setViewShowState(mTvRewind, INVISIBLE);
        setViewShowState(mIvCapturePhoto, INVISIBLE);
        setViewShowState(mIvCaptureGif, INVISIBLE);
    }

    @Override
    public int getEnlargeImageRes() {
        return R.drawable.ic_vedio_sl_b;
    }

    @Override
    public int getShrinkImageRes() {
        return R.drawable.ic_vedio_sl_s;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isLinkScroll && !isIfCurrentIsFullscreen()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }


    /**
     * 全屏的时候将对应的参数赋值给全屏播放器
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        LogUtils.d(TAG, "startWindowFullscreen");
        // hideNetSpeed();
        return super.startWindowFullscreen(context, actionBar, statusBar);

    }


    /**
     * 退出全屏的时候将对应的参数返回给非全屏播放器
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        LogUtils.d(TAG, "resolveNormalVideoShow");
        if (null != gsyVideoPlayer) {
            LandLayoutVideo landLayoutVideo = (LandLayoutVideo) gsyVideoPlayer;
            this.mVideoFunctionListener = landLayoutVideo.mVideoFunctionListener;
            this.mAdapterVideoList = landLayoutVideo.mAdapterVideoList;
            landLayoutVideo.dismissProgressDialog();
            landLayoutVideo.dismissVolumeDialog();
            landLayoutVideo.dismissBrightnessDialog();
            //showNetSpeed();
        }
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
    }

    @Override
    protected void resolveFullVideoShow(Context context, GSYBaseVideoPlayer gsyVideoPlayer, FrameLayout frameLayout) {
        LogUtils.d(TAG, "resolveFullVideoShow");
        final LandLayoutVideo landLayoutVideo = (LandLayoutVideo) gsyVideoPlayer;
        showTouchPic(landLayoutVideo.mTouchPic);
        super.resolveFullVideoShow(context, gsyVideoPlayer, frameLayout);
    }

    /**
     * 切换横竖屏时，会创建一个新的播放器，通过此方法将参数、状态等复制到新播放器
     *
     * @param from 被复制的
     * @param to   复制出来的
     */
    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        if (from instanceof LandLayoutVideo && to instanceof LandLayoutVideo) {
            LandLayoutVideo toPlayer = (LandLayoutVideo) to;
            LandLayoutVideo fromPlayer = (LandLayoutVideo) from;
            //复制回调
            toPlayer.mVideoFunctionListener = fromPlayer.mVideoFunctionListener;
            //复制分集选择的视频列表adapter
            toPlayer.mAdapterVideoList = fromPlayer.mAdapterVideoList;
            if (toPlayer.mRvVideoList != null) {
                toPlayer.mRvVideoList.setLayoutManager(new GridLayoutManager(mContext, 3));
                toPlayer.mRvVideoList.setAdapter(toPlayer.mAdapterVideoList);
                toPlayer.mAdapterVideoList.setSelectedListener((position, bean) -> {
                    if (mVideoFunctionListener != null) {
                        mVideoFunctionListener.onSelectVideo(position, bean);
                    }
                    hideRightDialogView(toPlayer.mRvVideoList);
                });
            }
            //复制收藏
            toPlayer.mCollected = fromPlayer.mCollected;
            if (toPlayer.mCheckBoxCollect != null) {
                toPlayer.mCheckBoxCollect.setChecked(toPlayer.mCollected);
            }
            //复制定时关闭信息
            toPlayer.setCloseTimeType(fromPlayer.mCloseTime);
            //复制倍速信息
            toPlayer.mSpeed = fromPlayer.mSpeed;
            if (toPlayer.mGroupSpeed != null) {
                int checkedRadioButtonId;
                if (toPlayer.mSpeed == PLAY_SPEED_100X) {
                    checkedRadioButtonId = R.id.radio_speed_100x;
                } else if (toPlayer.mSpeed == PLAY_SPEED_125X) {
                    checkedRadioButtonId = R.id.radio_speed_125x;
                } else if (toPlayer.mSpeed == PLAY_SPEED_150X) {
                    checkedRadioButtonId = R.id.radio_speed_150x;
                } else if (toPlayer.mSpeed == PLAY_SPEED_175X) {
                    checkedRadioButtonId = R.id.radio_speed_175x;
                } else if (toPlayer.mSpeed == PLAY_SPEED_200X) {
                    checkedRadioButtonId = R.id.radio_speed_200x;
                } else {
                    checkedRadioButtonId = R.id.radio_speed_100x;
                }
                ((RadioButton) toPlayer.findViewById(checkedRadioButtonId)).setChecked(true);
            }
            //复制画面比例
            toPlayer.mRatio = fromPlayer.mRatio;
            if (toPlayer.mGroupRatio != null) {
                int checkedRadioButtonId;
                switch (toPlayer.mRatio) {
                    case RATIO_DEFAULT:
                        checkedRadioButtonId = R.id.radio_ratio_default;
                        break;
                    case RATIO_TILE:
                        checkedRadioButtonId = R.id.radio_ratio_tile;
                        break;
                    case RATIO_STRETCH:
                        checkedRadioButtonId = R.id.radio_ratio_stretch;
                        break;
                    default:
                        checkedRadioButtonId = R.id.radio_ratio_default;
                        break;
                }
                ((RadioButton) toPlayer.findViewById(checkedRadioButtonId)).setChecked(true);
            }
            //复制连续播放
            toPlayer.mIsContinuePlay = fromPlayer.mIsContinuePlay;
            if (toPlayer.mGroupContinuePlay != null) {
                ((RadioButton) toPlayer
                        .findViewById(toPlayer.mIsContinuePlay ? R.id.radio_continue_open : R.id.radio_continue_close))
                        .setChecked(true);
            }
            toPlayer.mCastListener = fromPlayer.mCastListener;
            //片头广告
            toPlayer.mIsShowingHeadAd = fromPlayer.mIsShowingHeadAd;
            //改绑
            if (fromPlayer.mAdControl != null) {
                toPlayer.mAdControl = fromPlayer.mAdControl;
                toPlayer.mAdControl.setPlayer(toPlayer);
            }
            toPlayer.mCurrentOriginUrl = fromPlayer.mCurrentOriginUrl;
            toPlayer.mIvAdBackground.setImageDrawable(fromPlayer.mIvAdBackground.getDrawable());
            toPlayer.mTvAdTime.setText(fromPlayer.mTvAdTime.getText());
            toPlayer.mTvAdTime.setEnabled(fromPlayer.mTvAdTime.isEnabled());
            toPlayer.mIvSkipHeadAd.setVisibility(fromPlayer.mIvSkipHeadAd.getVisibility());
            toPlayer.mIsShowingPauseAd = fromPlayer.mIsShowingPauseAd;
            toPlayer.mLayoutPauseAd.setVisibility(fromPlayer.mLayoutPauseAd.getVisibility());
            toPlayer.mIvBackgroundPauseAd.setImageDrawable(fromPlayer.mIvBackgroundPauseAd.getDrawable());
            toPlayer.mTvClosePauseAd.setText(fromPlayer.mTvClosePauseAd.getText());
            toPlayer.mLayoutPlayAd.setVisibility(fromPlayer.mLayoutPlayAd.getVisibility());
            toPlayer.mIvBackgroundPlayAd.setImageDrawable(fromPlayer.mIvBackgroundPlayAd.getDrawable());
            toPlayer.mTvClosePlayAd.setVisibility(fromPlayer.mTvClosePlayAd.getVisibility());
            toPlayer.changeAdUIState();
        }
    }

    public void setLinkScroll(boolean linkScroll) {
        isLinkScroll = linkScroll;
    }

    public void setNetSpeed(String netSpeed) {
        if (!TextUtils.isEmpty(netSpeed)) {
            mNetSpeed.setText(netSpeed);
        }
    }

    public void showNetSpeed() {
        if (null != mNetSpeed) {
            mNetSpeed.setVisibility(VISIBLE);
        }
    }


    public void hideNetSpeed() {
        if (null != mNetSpeed) {
            mNetSpeed.setVisibility(GONE);
        }
    }

    public void setVideoFunctionListener(VideoFunctionListener videoFunctionListener) {
        LogUtils.d(TAG, "setVideoFunctionListener ");
        this.mVideoFunctionListener = videoFunctionListener;
    }

    public void setCastListener(FullscreenCastListener castListener) {
        this.mCastListener = castListener;
    }

    /**
     * 显示教学手势图
     */
    public void showTouchPic(ImageView imageView) {
        boolean touchPic = SpUtils.getBoolean(TestApp.getContext(), "touchPic", true);
        LogUtils.d(TAG, "showTouchPic == " + touchPic);

        if (null != imageView & touchPic) {
            LogUtils.d(TAG, "showTouchPic ");
            SpUtils.putBoolean(TestApp.getContext(), "touchPic", false);
            imageView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏教学手势图
     */
    public void hideTouchPic(ImageView imageView) {
        if (null != imageView) {
            imageView.setVisibility(GONE);
        }
    }

    /**
     * 外部获取到了分集信息，复制到播放器中
     *
     * @param list
     */
    public void setVideoList(List<PlayerUrlListBean> list) {
        if (mAdapterVideoList == null) {
            mAdapterVideoList = new PlayerChooseVideoAdapter();
        }
        mAdapterVideoList.setVideoList(list);
        mAdapterVideoList.notifyDataSetChanged();
    }

    /**
     * 当外部选择了其他集，对应的更新播放器中的选集
     *
     * @param position
     */
    public void selectVideo(int position) {
        if (mAdapterVideoList == null) {
            mAdapterVideoList = new PlayerChooseVideoAdapter();
        }
        mAdapterVideoList.setCurrentPosition(position);
        mAdapterVideoList.notifyDataSetChanged();
    }

    /**
     * 当外部收藏了视频，对应的在播放器里面也进行勾选
     *
     * @param collected
     */
    public void setCollected(boolean collected) {
        mCollected = collected;
        if (mCheckBoxCollect != null) {
            mCheckBoxCollect.setChecked(mCollected);
        }
    }

    /**
     * 继续播放下一集
     */
    public void playNext() {
        if (mAdapterVideoList != null) {
            int currentPosition = mAdapterVideoList.getCurrentPosition();
            if (currentPosition == -1) {
                return;
            }
            int nextPosition = currentPosition + 1;
            if (nextPosition >= mAdapterVideoList.getItemCount()) {
                return;
            }
            mVideoFunctionListener.onSelectVideo(nextPosition, mAdapterVideoList.getVideoList().get(nextPosition));
        }
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
     * 更改画面比例
     *
     * @param ratio {@link #RATIO_DEFAULT,#RATIO_STRETCH,#RATIO_TILE}
     */
    private void changeRatio(int ratio) {
        LogUtils.d(TAG, "mVideoPlayer change Ratio to " + ratio);
        GSYVideoType.setShowType(ratio);
        changeTextureViewShowType();
        if (mTextureView != null) {
            mTextureView.requestLayout();
        }
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

    /**
     * 当乐播投屏sdk获取到了可连接的设备列表，会调用此方法
     */
    public void setCastDeviceList(List<LelinkServiceInfo> list) {
        if (mRvCastDeviceList != null) {
            if (mRvCastDeviceList.getLayoutManager() == null) {
                mRvCastDeviceList.setLayoutManager(new LinearLayoutManager(mContext));
            }
            PlayerChooseCastDeviceAdapter adapter;
            if (mRvCastDeviceList.getAdapter() != null) {
                adapter = (PlayerChooseCastDeviceAdapter) mRvCastDeviceList.getAdapter();
            } else {
                adapter = new PlayerChooseCastDeviceAdapter();
            }
            adapter.setData(list);
            adapter.setOnItemClickListener(info -> {
                if (mCastListener != null) {
                    mCastListener.onChooseDevice(info);
                }
                //选中了某个设备，隐藏选择设备的页面
                hideRightDialogView(mLayoutCastDevice);
            });
            mRvCastDeviceList.setAdapter(adapter);
        }
    }

    /**
     * 设置横屏投屏时的播放进度
     */
    public void setCastPlayProgress(int duration, int position) {
        if (null != mSeekBarProgressCastControl && null != mTvCurrentCastControl && null != mTvTotalCastControl) {
            mSeekBarProgressCastControl.setMax(duration);
            mSeekBarProgressCastControl.setProgress(position);
            mTvCurrentCastControl.setText(StringUtils.stringForTime(position));
            mTvTotalCastControl.setText(StringUtils.stringForTime(duration));
        }
    }

    /**
     * 设置横屏投屏时的设备名称
     * 设置横屏投屏时的设备名称
     *
     * @param name
     */
    public void setCastDeviceName(String name) {
        if (mTvDeviceNameCastControl != null) {
            mTvDeviceNameCastControl.setText(name);
        }
    }

    /**
     * 设置横屏投屏时的状态
     *
     * @param status
     */
    public void setCastPlayStatus(String status) {
        if (mTvStatusCastControl != null) {
            mTvStatusCastControl.setText(status);
        }
    }

    public ImageView getCastStartButton() {
        return mIvStartCastControl;
    }

    public void showCastLayout() {
        setViewShowState(mLayoutCastControl, View.VISIBLE);
    }

    public void hideCastLayout() {
        setViewShowState(mLayoutCastControl, View.INVISIBLE);
    }

    public void setCloseTimerText(String text) {
        if (mGroupTimerClose == null) {
            return;
        }
        int checkedRadioButtonId;
        switch (mCloseTime) {
            case CLOSE_TIME_NONE:
                checkedRadioButtonId = R.id.radio_timer_none;
                break;
            case CLOSE_TIME_CURRENT_VIDEO:
                checkedRadioButtonId = R.id.radio_timer_current_length;
                break;
            case CLOSE_TIME_30_MINUTES:
                checkedRadioButtonId = R.id.radio_timer_30_minutes;
                break;
            case CLOSE_TIME_60_MINUTES:
                checkedRadioButtonId = R.id.radio_timer_60_minutes;
                break;
            default:
                return;
        }
        RadioButton button = findViewById(checkedRadioButtonId);
        if (button != null) {
            button.setText(text);
        }
    }

    public void setCloseTimeType(int closeTime) {
        mCloseTime = closeTime;
        if (mGroupTimerClose != null) {
            int checkedRadioButtonId;
            switch (mCloseTime) {
                case CLOSE_TIME_NONE:
                    checkedRadioButtonId = R.id.radio_timer_none;
                    break;
                case CLOSE_TIME_CURRENT_VIDEO:
                    checkedRadioButtonId = R.id.radio_timer_current_length;
                    break;
                case CLOSE_TIME_30_MINUTES:
                    checkedRadioButtonId = R.id.radio_timer_30_minutes;
                    break;
                case CLOSE_TIME_60_MINUTES:
                    checkedRadioButtonId = R.id.radio_timer_60_minutes;
                    break;
                default:
                    checkedRadioButtonId = R.id.radio_timer_none;
                    break;
            }
            ((RadioButton) findViewById(checkedRadioButtonId)).setChecked(true);
        }
    }

    public void resetTimerCloseText() {
        if (mGroupTimerClose != null) {
            ((RadioButton) findViewById(R.id.radio_timer_none)).setText("不开启");
            ((RadioButton) findViewById(R.id.radio_timer_current_length)).setText("播完当前");
            ((RadioButton) findViewById(R.id.radio_timer_30_minutes)).setText("30分钟");
            ((RadioButton) findViewById(R.id.radio_timer_60_minutes)).setText("60分钟");
        }
    }

    @Override
    public boolean isShowingHeadAd() {
        return mIsShowingHeadAd;
    }

    @Override
    public void setHeadAdTimeText(@NotNull String text) {
        if (mTvAdTime != null) {
            mTvAdTime.setText(text);
        }
    }

    @Override
    public long getAdPosition() {
        return getGSYVideoManager().getCurrentPosition();
    }


    @Override
    public long getAdDuration() {
        return getGSYVideoManager().getDuration();
    }

    @Override
    public void startShowImageAd(@NotNull String adUrl) {
        if (mIvAdBackground != null) {
            GlideApp.with(mContext)
                    .load(adUrl)
                    .centerCrop()
                    .into(mIvAdBackground);
        }
    }

    @Override
    public void showPlayAd(boolean vip, String pic) {
        if (mLayoutPlayAd != null) {
            mLayoutPlayAd.setVisibility(VISIBLE);
        }
        if (mTvClosePlayAd != null) {
            if (vip) {
                mTvClosePlayAd.setVisibility(VISIBLE);
            } else {
                mTvClosePlayAd.setVisibility(GONE);
            }
        }
        if (mIvBackgroundPlayAd != null && pic != null) {
            GlideApp.with(mContext)
                    .load(pic)
                    .centerCrop()
                    .into(mIvBackgroundPlayAd);
        }
    }

    @Override
    public void hidePlayAd() {
        if (mLayoutPlayAd != null) {
            mLayoutPlayAd.setVisibility(GONE);
        }
    }

    @Override
    public void onVideoPause() {
        super.onVideoPause();
        if (mAdControl != null && mAdControl.getMPlayer() == this) {
            if (mIsShowingHeadAd) {
                mAdControl.setActivityPause(true);
            } else {
                if (mAdControl.showPauseAd()) {
                    mIsShowingPauseAd = true;
                    changeAdUIState();
                }
            }
        }
    }

    /**
     * 暂停视频，并且不显示暂停广告
     */
    public void onVideoPauseWithoutPauseAd() {
        super.onVideoPause();
        if (mAdControl != null && mAdControl.getMPlayer() == this) {
            if (mIsShowingHeadAd) {
                mAdControl.setActivityPause(true);
            }
        }
    }

    @Override
    public void onVideoResume() {
        if (mAdControl != null) {
            mAdControl.setActivityPause(false);
        }
        //当展示暂停广告时,熄屏再打开，不可继续播放
        if (mIsShowingPauseAd) {
            return;
        }
        super.onVideoResume(false);
    }

    @Override
    public void release() {
        super.release();
        if (mAdControl != null) {
            mAdControl.release();
        }
    }

    @Nullable
    @Override
    public TextView getPauseAdTimeTextView() {
        return mTvClosePauseAd;
    }

    @Override
    public void showPauseAd(String pic) {
        mIsShowingPauseAd = true;
        if (mLayoutPauseAd != null) {
            mLayoutPauseAd.setVisibility(VISIBLE);
        }
        if (mIvBackgroundPauseAd != null && pic != null) {
            GlideApp.with(mContext)
                    .load(pic)
                    .centerCrop()
                    .into(mIvBackgroundPauseAd);
        }
    }

    @Override
    protected void touchDoubleUp() {
        /**
         * 播放片头和暂停广告时屏蔽双击
         */
        if (mIsShowingHeadAd || mIsShowingPauseAd) {
            return;
        }
        super.touchDoubleUp();
    }

    /**
     * 广告期间不需要触摸
     */
    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        if ((absDeltaX > mThreshold || absDeltaY > mThreshold)) {
            int screenWidth = CommonUtil.getScreenWidth(getContext());
            boolean isAdModel = mIsShowingPauseAd || mIsShowingHeadAd;
            if (isAdModel && absDeltaX >= mThreshold && Math.abs(screenWidth - mDownX) > mSeekEndOffset) {
                //防止全屏虚拟按键
                mChangePosition = true;
                mDownPosition = getCurrentPositionWhenPlaying();
            } else {
                super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
            }
        }
    }

    /**
     * 广告期间不需要触摸
     */
    @Override
    protected void touchSurfaceMove(float deltaX, float deltaY, float y) {
        boolean isAdModel = mIsShowingPauseAd || mIsShowingHeadAd;
        if (mChangePosition && isAdModel) {
            return;
        } else {
            super.touchSurfaceMove(deltaX, deltaY, y);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouch(v, event);
    }

    @Override
    public void setHeadAdSkipEnable(boolean enable) {
        mIvSkipHeadAd.setVisibility(enable ? VISIBLE : GONE);
        mTvAdTime.setEnabled(enable);
    }

    @Override
    public void addAddInfo(int adType, @Nullable String adId) {
        mVideoFunctionListener.addAdInfo(adType, adId);
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        // TODO: 2019/11/19 这里先隐藏拖拽的快进dialog
//        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
    }
}
