package com.video.test.module.player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.browse.api.ILelinkServiceManager;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
import com.umeng.analytics.MobclickAgent;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.framework.GlideApp;
import com.video.test.javabean.AdInfoBean;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.BeanTopicContentBean;
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.javabean.PlayerVideoListBean;
import com.video.test.javabean.UserCenterBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoPlayTabBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.listener.FullscreenCastListener;
import com.video.test.ui.listener.VideoFunctionListener;
import com.video.test.ui.widget.CastDialogFragment;
import com.video.test.ui.widget.DividerItemDecoration;
import com.video.test.ui.widget.LandLayoutVideo;
import com.video.test.ui.widget.LoadingView;
import com.video.test.ui.widget.ShareDialogFragment;
import com.video.test.ui.widget.ShareImageDialogFragment;
import com.video.test.ui.widget.TimeCloseDialogFragment;
import com.video.test.utils.DownloadUtil;
import com.video.test.utils.IntentUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.PixelUtils;
import com.video.test.utils.StringUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.cast.AllCast;
import com.video.test.utils.cast.IUIUpdateListener;
import com.video.test.utils.cast.LelinkHelper;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import me.drakeet.multitype.MultiTypeAdapter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/player/activity")
public class PlayerActivity extends BaseActivity<PlayerPresenter> implements PlayerContract.View {
    public static final int ACTIVITY_REQUEST_CODE_DOWNLOAD = 1001;
    private static final String TAG = "PlayerActivity";
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    @BindView(R.id.videoPlayer_player)
    LandLayoutVideo mVideoPlayer;
    @BindView(R.id.layout_mobile_network)
    View mLayoutMobileNetwork;
    @BindView(R.id.rv_selectItem_player)
    RecyclerView mRvHorizontalSelectItem;
    @BindView(R.id.tv_videoName_player)
    TextView mTvVideoName;
    @BindView(R.id.ctx_collection_player)
    CheckedTextView mCtxCollection;
    @BindView(R.id.tv_beanPoint_player)
    TextView mTvBeanPoint;
    @BindView(R.id.tv_update_player)
    TextView mTvUpdate;
    @BindView(R.id.tv_region_player)
    TextView mTvVideoArea;
    @BindView(R.id.rv_recommendVideo_player)
    RecyclerView mRvRecommend;
    @BindView(R.id.tv_actors_videoDetails)
    TextView mTvActorsVideoDetails;
    @BindView(R.id.tv_beanPoint_videoDetails)
    TextView mTvBeanPointVideoDetails;
    @BindView(R.id.tv_director_videoDetails)
    TextView mTvDirectorVideoDetails;
    @BindView(R.id.tv_update_videoDetails)
    TextView mTvUpdateVideoDetails;
    @BindView(R.id.tv_synopsis_videoDetails)
    TextView mTvSynopsisVideoDetails;
    @BindView(R.id.tabLayout_videoSelect)
    SlidingTabLayout mTabLayoutSelect;
    //    @BindView(R.id.rv_videoItem_videoSelect)
    //    RecyclerView mRvVideoSelect;
    @BindView(R.id.pager_videoItem_videoSelect)
    ViewPager mPagerVideoSelect;
    @BindView(R.id.swipeRefresh_player)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.rv_videoItem_downloadSelect)
    RecyclerView mRvDownloadSelect;
    @BindView(R.id.fl_download_player)
    FrameLayout mFlDownload;
    @BindView(R.id.ll_details_player)
    ConstraintLayout mClDetails;
    @BindView(R.id.fl_selectItem_player)
    FrameLayout mFlSelect;
    @BindView(R.id.tv_selectTitle_videoDetail)
    TextView mTvVideoNameDetails;
    @BindView(R.id.iv_close_waitPic_player)
    ImageView mIvCloseWaitPic;
    @BindView(R.id.iv_waitPic_player)
    ImageView mIvWaitPic;
    @BindView(R.id.tv_netSpeed_player)
    TextView mTvNetSpeed;
    @BindView(R.id.rv_commentVideo_player)
    RecyclerView mRvComment;
    @BindView(R.id.refreshLayout_commentVideo)
    SmartRefreshLayout mRefreshComment;
    @BindView(R.id.tv_sendComment_player)
    TextView mTvSendComment;
    @BindView(R.id.et_comment_player)
    EditText mEtComment;
    @BindView(R.id.iv_share_player)
    ImageView mIvShare;
    @BindView(R.id.iv_download_player)
    ImageView mIvDownload;
    @BindView(R.id.iv_sofa_commentVideo_player)
    ImageView mIvSofa;
    @BindView(R.id.tv_year_videoDetails)
    TextView mTvYearDetails;
    @BindView(R.id.tv_area_videoDetails)
    TextView mTvAreaDetails;
    @BindView(R.id.cl_castControl_player)
    ConstraintLayout mCastControl;
    @BindView(R.id.tv_castTitle_castControl)
    TextView mTvCastTitleCast;
    @BindView(R.id.iv_start_castControl)
    ImageView mIvCastStart;
    @BindView(R.id.tv_castStatus_castControl)
    TextView mTvCastStatusCast;
    @BindView(R.id.current_castControl)
    TextView mCurrentTimeCast;
    @BindView(R.id.total_castControl)
    TextView mTotalTimeCast;
    @BindView(R.id.progress_castControl)
    SeekBar mProgressCast;
    @BindView(R.id.iv_ad)
    ImageView mIvAd;
    @BindView(R.id.group_topic)
    Group mGroupTopic;
    @BindView(R.id.tv_comment)
    TextView mTvCommentCount;
    @BindView(R.id.tv_commentTitle_player)
    TextView mTvCommentTitle;
    @BindView(R.id.rv_topic_horizontal)
    RecyclerView mRvTopic;
    @BindView(R.id.loadingView)
    LoadingView mLoadingView;
    @BindView(R.id.layout_player_no_network)
    View mLayoutPlayerNoNetwork;
    @Autowired(name = "vodId")
    String mVodId;
    /**
     * 当前播放的集数的url，用来判断历史纪录中播放的是哪一集
     */
    @Autowired(name = "videoUrl")
    String mVideoUrl;
    @Autowired(name = "videoDegree")
    String mVideoDegree;
    // 从上级页面传递过来的栏目类型
    @Autowired(name = "vodPid")
    String mVideoPid;
    // 资源类型
    private String mVideoCid;

    private boolean isBindingPhone = false;

    private static final int SPAN_COUNT = 6;
    private PlayerDownloadSelectItemAdapter mDownloadSelectItemAdapter;
    private PlayerHorizontalSelectItemAdapter mHorizontalSelectItemAdapter;
    //    private PlayerSelectItemAdapter mSelectItemAdapter;
    private SelectVideoPagerAdapter mSelectItemPagerAdapter;
    private PlayerRecommendItemAdapter mRecommendItemAdapter;
    private boolean isPlay;
    private boolean isPause;
    private OrientationUtils mOrientationUtils;
    private VideoPlayerBean mVideoPlayerBean;
    private String mVideoOriginalUrl;
    private String mVideoName;
    private String mVideoId;
    private String mUserLevel;
    private LinearLayoutManager mHorizontalLayoutManager;
    private long mCurrentTime;
    private String mVideoTitle;
    private PlayerCommentItemAdapter mPlayerCommentItemAdapter;
    private LelinkHelper mLinkHelper;
    private boolean castIsPause;
    private LelinkServiceInfo mSelectInfo;
    private int mCastPosition;
    private VideoAdDataBean mVideoAdData;
    private ArrayList<CustomTabEntity> mTabList;
    private MultiTypeAdapter mAdapterTopic;

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
    }


    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_player;
    }


    @Override
    protected void initData() {
        initSwipeRefresh();
        LogUtils.d(TAG, "initData mVodId == " + mVodId);
        mUserLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL);
        //获取视频信息
        mPresenter.getVideoPlayerContent(mVodId);
        mPresenter.addPlayCount(mVodId);
        mPresenter.initWeChatApi();
        mPresenter.initCloseTimer();
    }

    private IUIUpdateListener mIUIUpdateListener = new IUIUpdateListener() {

        @Override
        public void onUpdateState(int state, Object object) {
            LogUtils.d(TAG, "IUIUpdateListener state:" + state + " text:" + object);
            ImageView startButton;
            //进入到投屏状态时，首先停止定时器
            mPresenter.cancelWatchVideoTimer();
            switch (state) {
                case IUIUpdateListener.STATE_SEARCH_SUCCESS:
                    if (null != mPresenter) {
                        mPresenter.updateDeviceAdapter();
                    }
                    break;
                case IUIUpdateListener.STATE_SEARCH_ERROR:
                    ToastUtils.showToast(TestApp.getContext(), "Auth错误");
                    break;
                case IUIUpdateListener.STATE_SEARCH_NO_RESULT:
                    LogUtils.d(TAG, "IUI  搜索成功 无设备");
                    //判断横竖屏
                    if (null != mPresenter) {
                        mPresenter.updateDeviceAdapter();
                    }
                    break;
                case IUIUpdateListener.STATE_CONNECT_SUCCESS:
                    LogUtils.d(TAG, "IUI connect success:" + object);
                    // 更新列表
                    castPlay(mVideoOriginalUrl);
                    break;
                case IUIUpdateListener.STATE_DISCONNECT:
                    LogUtils.d(TAG, "IUI disConnect success:" + object);

                    // mBrowseAdapter.setSelectInfo(null);
                    // mBrowseAdapter.notifyDataSetChanged();
                    // 更新列表
                    //播放链接断开 停止链接
                    mPresenter.cancelWatchVideoTimer();
                    break;
                case IUIUpdateListener.STATE_CONNECT_FAILURE:
                    LogUtils.d(TAG, "IUI connect failure:" + object);
                    // LogUtils.d(TAG, "ToastUtil " + object);
                  /*  mBrowseAdapter.setSelectInfo(null);
                    mBrowseAdapter.notifyDataSetChanged();*/
                    break;
                case IUIUpdateListener.STATE_PLAY:
                    LogUtils.d(TAG, "IUI callback play");
                    castIsPause = false;
                    if (null != mIvCastStart) {
                        GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(mIvCastStart);
                    }
                    if (getCurPlay() != null) {
                        startButton = getCurPlay().getCastStartButton();
                        if (startButton != null) {
                            GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(startButton);
                        }
                    }
                    // 处于投屏幕播放状态时，后台开始计时
                    if (null != mVideoPlayer) {
                        mPresenter.watchVideoTimer(mVideoPlayer);
                    }
                    break;
                case IUIUpdateListener.STATE_LOADING:
                    LogUtils.d(TAG, "IUI callback loading");
                    castIsPause = false;
                    if (mIvCastStart != null) {
                        GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(mIvCastStart);
                    }
                    if (getCurPlay() != null) {
                        startButton = getCurPlay().getCastStartButton();
                        if (startButton != null) {
                            GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(startButton);
                        }
                    }
                    break;
                case IUIUpdateListener.STATE_PAUSE:
                    LogUtils.d(TAG, "IUI callback pause");
                    castIsPause = true;
                    if (null != mIvCastStart) {
                        GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_play_selector).into(mIvCastStart);
                    }
                    if (getCurPlay() != null) {
                        startButton = getCurPlay().getCastStartButton();
                        if (startButton != null) {
                            GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_play_selector).into(startButton);
                        }
                    }
                    break;
                case IUIUpdateListener.STATE_STOP:
                    LogUtils.d(TAG, "IUI callback stop");
                    castIsPause = false;
                    if (null != mIvCastStart) {
                        GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(mIvCastStart);
                    }
                    if (getCurPlay() != null) {
                        startButton = getCurPlay().getCastStartButton();
                        if (startButton != null) {
                            GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_pause_selector).into(startButton);
                        }
                    }
                    break;
                case IUIUpdateListener.STATE_SEEK:
                    LogUtils.d(TAG, "IUI callback seek:" + object);

                    break;
                case IUIUpdateListener.STATE_PLAY_ERROR:
                    LogUtils.d(TAG, "IUI callback error:" + object);
                    break;
                case IUIUpdateListener.STATE_POSITION_UPDATE:
                    LogUtils.d(TAG, "IUI callback position update:" + object);
                    long[] arr = (long[]) object;
                    long duration = arr[0];
                    long position = arr[1];
                    LogUtils.d(TAG, "IUI 总长度：" + duration + " 当前进度:" + position);
                    if (null != mProgressCast && null != mCurrentTimeCast && null != mTotalTimeCast) {
                        mProgressCast.setMax((int) duration);
                        mProgressCast.setProgress((int) position);
                        mCurrentTimeCast.setText(StringUtils.stringForTime((int) position));
                        mTotalTimeCast.setText(StringUtils.stringForTime((int) duration));
                        if (getCurPlay() != null) {
                            getCurPlay().setCastPlayProgress(((int) duration), ((int) position));
                        }
                    }
                    break;
                case IUIUpdateListener.STATE_COMPLETION:
                    LogUtils.d(TAG, "IUI callback completion");
                    if (null != mIvCastStart) {
                        GlideApp.with(PlayerActivity.this).load(R.drawable.video_click_play_selector).into(mIvCastStart);
                    }
                    break;
                case IUIUpdateListener.STATE_INPUT_SCREENCODE:
                    LogUtils.d(TAG, "IUI input screencode");

                    break;
                case IUIUpdateListener.RELEVANCE_DATA_UNSUPPORT:
                    LogUtils.d(TAG, "IUI unSupport relevance data");
                    ToastUtils.showToast(TestApp.getContext(), object.toString());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onUpdateText(String msg) {
            Log.d(TAG, "IUI onUpdateText: msg : " + msg);
            if (null != mTvCastStatusCast && !TextUtils.isEmpty(msg)) {
                mTvCastStatusCast.setText(msg);
                if (getCurPlay() != null) {
                    getCurPlay().setCastPlayStatus(msg);
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        resumePlay();
        isPause = false;
    }


    @Override
    protected void onPause() {
        //如果是评论时弹出绑定手机的窗口，则不暂停播放
        if (!isBindingPhone) {
            isPause = true;
            //暂停视频播放
            if (getCurPlay() != null) {
                // 暂停视频时, 停止任务定时器
                mPresenter.cancelWatchVideoTimer();
                getCurPlay().onVideoPauseWithoutPauseAd();
                //如果不是播放视频广告时退出，则添加历史纪录
                if (!getCurPlay().isShowingHeadAd()) {
                    long currentPosition = getCurPlay().getCurrentPositionWhenPlaying();
                    long duration = getCurPlay().getGSYVideoManager().getDuration();
                    mPresenter.addHistory(mVideoId, mVideoTitle, mVideoName, mVideoOriginalUrl, currentPosition, duration);
                    LogUtils.d(TAG, "onPause currentPosition == " + currentPosition + "duration == " + duration);

                }
            }
        }
        isBindingPhone = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (null != mOrientationUtils) {
            mOrientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPlay && !isPause) {
            mVideoPlayer.onConfigurationChanged(this, newConfig, mOrientationUtils, true, true);
        }
    }


    @Override
    public void getVideoPlayerContentSuccess(VideoPlayerBean videoPlayerBean) {
        setPlayerData(videoPlayerBean);
    }


    @Override
    public void addHistoryBeanSuccess(String message) {
        LogUtils.d(TAG, "addHistoryBeanSuccess == " + message);

    }

    @Override
    public void feedbackMessageSuccess(String message) {
        ToastUtils.showLongToast(TestApp.getContext(), message);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 点击推荐页面之后,不杀死当前的PlayerActivity  直接加载新资源.
        String newVodId = intent.getStringExtra("vodId");
        this.mVodId = newVodId;
        LogUtils.d(TAG, "onNewIntent newVodId : " + newVodId);
        mPresenter.getVideoPlayerContent(newVodId);
        mPresenter.addPlayCount(newVodId);
        if (mRefreshComment != null) {
            mRefreshComment.resetNoMoreData();
            mRefreshComment.finishLoadMore();
            mRefreshComment.finishRefresh();
        }
    }

    /**
     * 判断是否有播放历史 有播放历史,提示继续播放
     */
    private void havePlayHistory(String videoUrl, String videoTime, List<PlayerUrlListBean> playerUrlListBeans) {
        PlayerUrlListBean playerUrlListBean;
        //播放的集数在视频分集列表中的位置
        int currentPosition = 0;
        //判断是否有历史纪录
        if (null != videoUrl && null != videoTime) {
            /* 寻找播放地址,设置其属性为True */
            int urlSize = playerUrlListBeans.size();
            for (int i = 0; i < urlSize; i++) {
                playerUrlListBean = playerUrlListBeans.get(i);
                if (videoUrl.equals(playerUrlListBean.getVideoUrl())) {
                    LogUtils.d(TAG, "havePlayHistory have history, mVideoUrl == " + videoUrl + " mVideoDegree == " + videoTime);
                    currentPosition = i;
                    break;
                }
            }
        }
        playerUrlListBean = playerUrlListBeans.get(currentPosition);
//        mSelectItemAdapter.setHistoryPosition(currentPosition);
        for (int i = 0; i < mTabList.size(); i++) {
            VideoPlayTabBean tabBean = (VideoPlayTabBean) mTabList.get(i);
            if (currentPosition >= tabBean.getStartPosition() && currentPosition <= tabBean.getEndPosition()) {
                mTabLayoutSelect.setCurrentTab(i);
//                mSelectItemAdapter.setCurrentData(tabBean.getData());
                break;
            }
        }
//        mSelectItemAdapter.notifyDataSetChanged();
        mSelectItemPagerAdapter.setCurrentVideoPosition(currentPosition);

        mHorizontalSelectItemAdapter.setHistoryPosition(currentPosition);
        mRvHorizontalSelectItem.scrollToPosition(currentPosition);
        //通知播放器中分集列表刷新
        if (getCurPlay() != null) {
            getCurPlay().selectVideo(currentPosition);
        }
        LogUtils.d(TAG, "play url : " + playerUrlListBean.getVideoUrl() + " name : " + playerUrlListBean.getVideoName());
        mPresenter.getVideoPlayUrl(playerUrlListBean.getVideoUrl(), playerUrlListBean.getVideoName());
    }


    private void initGridLayoutManager(final VideoPlayerBean videoPlayerBean) {

        int leftRight = PixelUtils.dp2px(this, 3);

        mRvRecommend.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });


        if (null == mRvRecommend.getAdapter()) {
            mRvRecommend.setAdapter(mRecommendItemAdapter);
            mRvRecommend.addItemDecoration(new DividerItemDecoration(leftRight, leftRight, Color.TRANSPARENT));
        }


        final GridLayoutManager selectGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        GridLayoutManager downloadGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (videoPlayerBean.getVod_length() == 0) ? 1 : 2;
            }
        };

        selectGridLayoutManager.setSpanSizeLookup(spanSizeLookup);
//        mRvVideoSelect.setLayoutManager(selectGridLayoutManager);
//        if (null == mRvVideoSelect.getAdapter()) {
//            mRvVideoSelect.setAdapter(mSelectItemAdapter);
//        }

        downloadGridLayoutManager.setSpanSizeLookup(spanSizeLookup);
        mRvDownloadSelect.setLayoutManager(downloadGridLayoutManager);
        if (null == mRvDownloadSelect.getAdapter()) {
            mRvDownloadSelect.setAdapter(mDownloadSelectItemAdapter);
        }

        mHorizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvHorizontalSelectItem.setLayoutManager(mHorizontalLayoutManager);
        if (null == mRvHorizontalSelectItem.getAdapter()) {
            mRvHorizontalSelectItem.setAdapter(mHorizontalSelectItemAdapter);
        }

        if (null == mRvComment.getAdapter()) {
            mRvComment.setAdapter(mPlayerCommentItemAdapter);
            android.support.v7.widget.DividerItemDecoration decoration = new android.support.v7.widget.DividerItemDecoration(this, android.support.v7.widget.DividerItemDecoration.VERTICAL);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.shape_bg_item_divider_1dp);
            assert drawable != null;
            decoration.setDrawable(drawable);
            mRvComment.addItemDecoration(decoration);
        }
    }


    @Override
    public void setSwipeRefreshStatus(Boolean status) {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setRefreshing(status);
        }
    }

    @Override
    protected void setAdapter() {
        mAdapterTopic = new MultiTypeAdapter();
        mAdapterTopic.register(BeanTopicContentBean.class, new TopicViewBinderHorizontal());
        mRvTopic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvTopic.setAdapter(mAdapterTopic);
        mRecommendItemAdapter = new PlayerRecommendItemAdapter();
//      mSelectItemAdapter = new PlayerSelectItemAdapter(mPresenter);
        mSelectItemPagerAdapter = new SelectVideoPagerAdapter(mPresenter);
        mPagerVideoSelect.setAdapter(mSelectItemPagerAdapter);
        mDownloadSelectItemAdapter = new PlayerDownloadSelectItemAdapter(mPresenter);
        mHorizontalSelectItemAdapter = new PlayerHorizontalSelectItemAdapter(mPresenter);
        mPlayerCommentItemAdapter = new PlayerCommentItemAdapter();
        //   投屏控制进度条
        setCastProgressListener();

        // 设置播放器的返回监听
        if (null != mVideoPlayer) {
            mVideoPlayer.getBackButton().setOnClickListener(v -> onBackPressed());
        }
    }


    @Override
    public void startPlayLogic() {
        //如果处于投屏状态,就不执行播放器的播放逻辑,只投屏播放
        if (null != mCastControl && mCastControl.getVisibility() == View.VISIBLE) {
            LogUtils.d(TAG, "startPlayLogic CastControl");
            castPlay(mVideoOriginalUrl);
            return;
        }

        if (null != getCurPlay()) {
            LogUtils.d(TAG, "startPlayLogic videoPlayer");
            //设置开始播放的进度
            getCurPlay().startPlayLogic();
            initIJkOptions();

        }
    }


    @Override
    public void finishActivity() {
        finish();
    }


    @Nullable
    private LandLayoutVideo getCurPlay() {
        //TODO 这里mVideoPlayer会为空，先这样改，判断是什么情况下调用此方法播放器为空
        if (null != mVideoPlayer && null != mVideoPlayer.getFullWindowPlayer()) {
            return (LandLayoutVideo) mVideoPlayer.getFullWindowPlayer();
        }
        return mVideoPlayer;
    }


    private void initSwipeRefresh() {
        if (null != mSwipeRefresh) {
            mSwipeRefresh.setColorSchemeColors(Color.parseColor("#ff9900"), Color.parseColor("#aaaaaa"));
            mSwipeRefresh.setEnabled(false);
        }
        if (null != mRefreshComment) {
            mRefreshComment.setEnableRefresh(false);
            mRefreshComment.setRefreshFooter(new ClassicsFooter(this));
            mRefreshComment.setOnLoadMoreListener(refreshLayout -> {
                if (mPlayerCommentItemAdapter != null) {
                    mPlayerCommentItemAdapter.moreVideoComment();
                    mRefreshComment.postDelayed(() -> {
                        if (mRefreshComment != null && mPlayerCommentItemAdapter != null) {
                            if (mPlayerCommentItemAdapter.hasMoreData()) {
                                mRefreshComment.finishLoadMore();
                            } else {
                                mRefreshComment.finishLoadMoreWithNoMoreData();
                            }
                        }
                    }, 300);
                } else {
                    mRefreshComment.finishLoadMore();
                }
            });
        }
    }


    @Override
    public void showCleanCacheDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_memoryCache_not_enough)
                .positiveText(R.string.dialog_confirm_clearCache)
                .negativeText(R.string.dialog_cancel_clearCache)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ARouter.getInstance().build("/download/activity").navigation();
                    }
                }).show();
    }

    @Override
    public void showSofaBackground() {
        if (null != mIvSofa && null != mRefreshComment) {
            mRefreshComment.setVisibility(View.GONE);
            mIvSofa.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideSofaBackground() {
        if (null != mIvSofa && null != mRefreshComment) {
            mRefreshComment.setVisibility(View.VISIBLE);
            mIvSofa.setVisibility(View.GONE);

        }
    }


    @Override
    public void showWaitPic() {
        if (null != mIvWaitPic) {
            mIvWaitPic.setVisibility(View.VISIBLE);
            GlideApp.with(this).load(R.drawable.ic_player_loading).into(mIvWaitPic);
        }
    }


    @Override
    public void hideWaitPic() {
        if (null != mIvWaitPic) {
            mIvWaitPic.setVisibility(View.GONE);
            GlideApp.with(this).clear(mIvWaitPic);
        }
    }

    @Override
    public void setNetSpeed(String netSpeed) {
        if (null != mTvNetSpeed) {
            String netSpeedText = mVideoPlayer.getNetSpeedText();
            mTvNetSpeed.setText(netSpeedText);
        }
    }

    /**
     * 设置用户评论
     *
     * @param videoCommentBeans
     */
    @Override
    public void setVideoComment(List<VideoCommentBean> videoCommentBeans) {
        if (videoCommentBeans.isEmpty()) {
            mTvCommentCount.setText("评论");
        } else {
            String count = videoCommentBeans.size() + "评论";
            mTvCommentCount.setText(count);
        }
        if (mPlayerCommentItemAdapter != null) {
            mPlayerCommentItemAdapter.setData(videoCommentBeans, mVodId);
            if (mPlayerCommentItemAdapter.hasMoreData() && mRefreshComment != null) {
                mRefreshComment.resetNoMoreData();
            }
        }
    }


    @Override
    public void getSelectedItemPosition(int selectedPosition) {
//        if (null != mSelectItemAdapter) {
//            mSelectItemAdapter.setHistoryPosition(selectedPosition);
        for (int i = 0; i < mTabList.size(); i++) {
            VideoPlayTabBean tabBean = (VideoPlayTabBean) mTabList.get(i);
            if (selectedPosition >= tabBean.getStartPosition() && selectedPosition <= tabBean.getEndPosition()) {
                mTabLayoutSelect.setCurrentTab(i);
//                    mSelectItemAdapter.setCurrentData(tabBean.getData());
                break;
            }
        }
//            mSelectItemAdapter.notifyDataSetChanged();
        mSelectItemPagerAdapter.setCurrentVideoPosition(selectedPosition);
        mHorizontalLayoutManager.scrollToPosition(selectedPosition);
//        }

        if (null != mHorizontalSelectItemAdapter) {
            mHorizontalSelectItemAdapter.setHistoryPosition(selectedPosition);
            mRvHorizontalSelectItem.scrollToPosition(selectedPosition);
            mHorizontalSelectItemAdapter.notifyDataSetChanged();
        }

        //通知播放器中分集列表刷新
        if (getCurPlay() != null) {
            getCurPlay().selectVideo(selectedPosition);
        }
    }

    @Override
    protected void initView() {
        mLoadingView.setOnLoadingListener(new LoadingView.OnLoadingListener() {
            @Override
            public void onRetry() {
                PlayerActivity.this.onRetry();
            }

            @Override
            public void onSolve() {
                ARouter.getInstance().build("/solve/activity").navigation();
            }
        });

        //外部辅助的旋转 帮助全屏
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        // 不打开外部旋转
        mOrientationUtils.setEnable(false);
        //初始化乐播投屏
        initLeLinkHelper();
        initVideoPlayer();
        disableCopyAndPaste(mEtComment);
    }

    private void onRetry() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        mPresenter.getVideoPlayerContent(mVodId);
    }

    @Override
    public void showNetworkErrorView() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingView.showError();
        }
        if (mLayoutPlayerNoNetwork != null) {
            mLayoutPlayerNoNetwork.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用来判断移动网络模式下 是否已经开启了流量开关
     */
    @Override
    public void isOpenMobileSwitch() {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_mobileNetwork_switchButton)
                .positiveText(R.string.dialog_setMobileSwitch)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> ARouter.getInstance().build("/setting/activity").navigation()).show();
    }

    public void setPlayerData(final VideoPlayerBean videoPlayerBean) {
        setAdData(videoPlayerBean);
        steZtData(videoPlayerBean);
        // 每次加载新电视数据之后 投屏的播放时间归类为 0;
        mCastPosition = 0;

        LogUtils.d(TAG, "mUserLevel == " + mUserLevel);

        this.mVideoPlayerBean = videoPlayerBean;
        PlayerVideoListBean videoListBean = videoPlayerBean.getList().get(0);
        this.mVideoId = videoListBean.getVod_id();
        this.mVideoTitle = videoListBean.getVod_name();
        this.mVideoCid = videoListBean.getVodCid();
        initGridLayoutManager(videoPlayerBean);

        //判断是否收藏
        boolean isCollect = AppConstant.USER_COLLECTED == videoPlayerBean.getIs_collect();
        setVideoCollected(isCollect);

        //推荐电影数据
        mRecommendItemAdapter.setData(videoPlayerBean.getRecommenData());

        //首页数据
        mTvVideoName.setText(videoListBean.getVod_name());
//      mTvBeanPoint.setText(videoListBean.getVod_scroe());
        mTvUpdate.setText(videoListBean.getVod_addtime());
        mTvVideoArea.setText(videoListBean.getVod_area());
        //详情页
        String vodScroe = videoListBean.getVod_scroe();
        if (TextUtils.isEmpty(vodScroe) || Double.parseDouble(vodScroe) == 0 || Double.parseDouble(vodScroe) == 10) {
            vodScroe = "暂无评";
        }
        String score = getString(R.string.video_point, vodScroe);
        mTvBeanPointVideoDetails.setText(score);
        mTvBeanPoint.setText(score);
        mTvUpdateVideoDetails.setText(videoListBean.getVod_addtime());
        mTvActorsVideoDetails.setText(videoListBean.getVod_actor());
        mTvDirectorVideoDetails.setText(videoListBean.getVod_director());
        mTvVideoNameDetails.setText(videoListBean.getVod_name());
        mTvYearDetails.setText(videoListBean.getVod_year());
        mTvAreaDetails.setText(videoListBean.getVod_area());
        mTvSynopsisVideoDetails.setText(videoListBean.getVod_use_content());
        //让TextView 可以内部滚动
        mTvSynopsisVideoDetails.setMovementMethod(ScrollingMovementMethod.getInstance());


        //下载功能,传入信息
        mDownloadSelectItemAdapter.setData(mPresenter.queryDownloadStatus(videoListBean.getVod_urlArr(), mVodId));
        mDownloadSelectItemAdapter.setVideoInfo(mVideoId, mVideoTitle);

        //全部选集
//        mSelectItemAdapter.setData(videoListBean.getVod_urlArr());
        mSelectItemPagerAdapter.setVideoList(videoListBean.getVod_urlArr());
        //初始化全部选集的tab
        initSelectTabList(videoListBean.getVod_urlArr());
        //横滑选集
        mHorizontalSelectItemAdapter.setData(videoListBean.getVod_urlArr());
        //播放器选集
        mVideoPlayer.setVideoList(videoListBean.getVod_urlArr());

        // 初始化播放
        havePlayHistory(mVideoUrl, mVideoDegree, videoListBean.getVod_urlArr());

        //最后获取用户评论
        mPresenter.getVideoComment(videoListBean.getVod_id());
    }

    private void steZtData(VideoPlayerBean videoPlayerBean) {
        if (videoPlayerBean.isZt()) {
            //有专题
            mGroupTopic.setVisibility(View.VISIBLE);
            mAdapterTopic.setItems(videoPlayerBean.getZtInfo());
            mAdapterTopic.notifyDataSetChanged();
        } else {
            mGroupTopic.setVisibility(View.GONE);
        }
    }

    /**
     * 视频分页
     */
    private void initSelectTabList(List<PlayerUrlListBean> beanList) {
        mTabList = new ArrayList<>();
        int pageSize = 50;
        int listSize = beanList.size();
        int index = 0;
        //创建tab
        while (index <= listSize) {
            ArrayList<PlayerUrlListBean> pageVideoList = new ArrayList<>();
            for (int j = index; j < index + pageSize; j++) {
                if (j >= listSize) {
                    break;
                }
                PlayerUrlListBean bean = beanList.get(j);
                pageVideoList.add(bean);
            }
            if (!pageVideoList.isEmpty()) {
                String startName = pageVideoList.get(0).getVideoName();
                String endName = pageVideoList.get(pageVideoList.size() - 1).getVideoName();
                VideoPlayTabBean tab = new VideoPlayTabBean(getVideoNum(startName) + "-" + getVideoNum(endName), index, index + pageSize - 1, pageVideoList);
                mTabList.add(tab);
            }
            index += pageSize;
        }
        //当只有一个分页时，隐藏tabLayout
        if (mTabList.size() <= 1) {
            mTabLayoutSelect.setVisibility(View.GONE);
        } else {
            mTabLayoutSelect.setVisibility(View.VISIBLE);
        }
        ArrayList<String> tabTitles = new ArrayList<>();
        for (CustomTabEntity entity : mTabList) {
            tabTitles.add(((VideoPlayTabBean) entity).getTitle());
        }
        mSelectItemPagerAdapter.setTabList(mTabList);
        mSelectItemPagerAdapter.notifyDataSetChanged();
        mTabLayoutSelect.setViewPager(mPagerVideoSelect, tabTitles.toArray(new String[0]));
//        mTabLayoutSelect.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelect(int position) {
//                VideoPlayTabBean tab = ((VideoPlayTabBean) mTabList.get(position));
//                mSelectItemAdapter.setCurrentData(tab.getData());
//                mSelectItemAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onTabReselect(int position) {
//
//            }
//        });
    }

    /**
     * 根据视频的名称，获取视频的集数
     */
    private int getVideoNum(String name) {
        name = name.trim();
        StringBuilder result = new StringBuilder();
        if (!TextUtils.isEmpty(name)) {
            for (int i = 0; i < name.length(); i++) {
                if (name.charAt(i) >= 48 && name.charAt(i) <= 57) {
                    result.append(name.charAt(i));
                }
            }
        }
        try {
            return Integer.parseInt(result.toString());
        } catch (Exception ignored) {
            return 0;
        }
    }

    private void setAdData(VideoPlayerBean videoPlayerBean) {
        //广告
        if (videoPlayerBean.isAd()) {
            mIvAd.setVisibility(View.VISIBLE);
            AdInfoBean adInfo = videoPlayerBean.getAdInfo();
            GlideApp.with(this)
                    .load(adInfo.getAdPic())
                    .centerCrop()
                    .into(mIvAd);
            //这里直接setTag会导致Glide报错
            mIvAd.setTag(mIvAd.getId(), adInfo);
        } else {
            mIvAd.setVisibility(View.GONE);
        }
    }


    private IConnectListener mIConnectListener = new IConnectListener() {
        @Override
        public void onConnect(LelinkServiceInfo lelinkServiceInfo, int override) {
            if (TextUtils.isEmpty(lelinkServiceInfo.getName())) {
                // pin码，则全部去掉
                return;
            }
            LogUtils.d(TAG, "IConnect name : " + lelinkServiceInfo.getName());
            castDisConnect(false);
        }

        @Override
        public void onDisconnect(LelinkServiceInfo lelinkServiceInfo, int what, int extra) {

        }
    };


    private void commentVideo() {
        mPresenter.commentVideo(mVodId, mEtComment, mTvSendComment);
  /*      UserCenterBean userCenterBean = (UserCenterBean) SpUtils.getSerializable(this, AppConstant.USER_INFO);
        assert userCenterBean != null;
        if (TextUtils.isEmpty(userCenterBean.getMobile())) {
            ARouter.getInstance().build("/bindphone/activity/float").navigation();
        } else {
            mPresenter.commentVideo(mVodId, mEtComment, mTvSendComment);
        }*/
    }


    @OnTextChanged(R.id.et_comment_player)
    void onTextChanged(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            hideSentBtn();
        } else {
            showSentBtn();
        }
    }

    private void showShareDialog() {
        ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance();
        String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
        shareDialogFragment.setShareItemClickListener(new ShareDialogFragment.ShareItemClickListener() {
            @Override
            public void onShareItemClick(int position) {
                switch (position) {
                    case AppConstant.WX_SCENE_SESSION:
                        LogUtils.d(TAG, "WX_SCENE_SESSION");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_wechat", "详情页微信分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_SESSION);
                        break;
                    case AppConstant.WX_SCENE_TIMELINE:
                        LogUtils.d(TAG, "WX_SCENE_TIMELINE");
                        MobclickAgent.onEvent(TestApp.getContext(), "detail_share_via_circle", "详情页朋友圈分享");
                        mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_TIMELINE);
                        break;
                    default:
                        break;
                }
            }
        });
        shareDialogFragment.show(getSupportFragmentManager(), "shareDialog");
    }

    @Override
    protected void onDestroy() {
        if (null != mOrientationUtils) {
            mOrientationUtils.releaseListener();
            mOrientationUtils = null;
        }
        mPresenter.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (getCurPlay() != null) {
            getCurPlay().release();
        }
        super.onDestroy();
    }


    @Override
    public void showMobileNetworkDialog() {
//        new MaterialDialog.Builder(this)
//                .content(R.string.dialog_mobileNetwork)
//                .positiveText(R.string.dialog_continue_play)
//                .negativeText(R.string.dialog_cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        startPlayLogic();
//                        //  showWaitPic();
//                    }
//                }).show();
        if (mLayoutMobileNetwork != null) {
            mLayoutMobileNetwork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMobileNetworkDownloadDialog(final String downloadUrl, String videoId, String videoName, String videoItemName) {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_mobileNetwork)
                .positiveText(R.string.dialog_continue_play)
                .negativeText(R.string.dialog_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mPresenter.setDownloadUrl(downloadUrl, videoId, videoName, videoItemName);
                    }
                }).show();
    }

    @Override
    public void showDownloadFunctionDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_download_for_vip)
                .positiveText(R.string.dialog_invite)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, witch) -> ARouter.getInstance().build("/share/activity").navigation())
                .show();
    }

    @Override
    public void showCastFunctionDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_cast_for_vip)
                .positiveText(R.string.dialog_invite)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, witch) -> ARouter.getInstance().build("/share/activity").navigation())
                .show();
    }


    /**
     * 添加收藏之后 修改 collectionId
     */
    @Override
    public void addCollectionSuccess(AddCollectionBean addCollectionBean) {
        mVideoPlayerBean.setCollect_id(addCollectionBean.getCollect_id());
    }

    private void showFeedbackDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title("您遇到的问题是?")
                .items(R.array.videoFeedBack)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        LogUtils.d(TAG, "onSelection  position ==" + position + " & " + "problem ==" + text);
                        mPresenter.feedbackSuggest(mVideoId, text.toString());
                    }
                }).show();
    }


    private void showSentBtn() {      // 显示发送按钮的时候,需要隐藏其他的3个功能按钮
        if (null != mCtxCollection && null != mIvShare && null != mIvDownload && null != mTvSendComment) {
            mCtxCollection.setVisibility(View.GONE);
            mIvShare.setVisibility(View.GONE);
            mIvDownload.setVisibility(View.GONE);
            mTvSendComment.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 用来处理投屏监听
     */
    private void setCastProgressListener() {
        mProgressCast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                if (null != mLinkHelper) {
                    mLinkHelper.seekTo(progress);
                }
            }
        });
    }

    private void clickCollection() {
        if (mCtxCollection.isChecked()) {
            mPresenter.delCollections(mVideoPlayerBean.getCollect_id());
        } else {
            mPresenter.addCollectCount(mVideoId);
            mPresenter.addCollections(mVideoId);
        }
    }

    private void clickDownload() {
        requireStoragePerm();
        if (mUserLevel.equals(AppConstant.USER_VIP) || mUserLevel.equals(AppConstant.USER_VIP_LASTDAY)) {
            mFlDownload.setVisibility(View.VISIBLE);
        } else {
            showDownloadFunctionDialog();
        }
    }

    private boolean clickCast() {
        if (mUserLevel.equals(AppConstant.USER_VIP) || mUserLevel.equals(AppConstant.USER_VIP_LASTDAY)) {
            //投屏功能 必须要先获取相应的权限
            requirePhonePerm();
            return true;
        } else {
            showCastFunctionDialog();
            return false;
        }
    }

    private void initLeLinkHelper() {
        mLinkHelper = LelinkHelper.getInstance(TestApp.getContext());
        mLinkHelper.setUIUpdateListener(mIUIUpdateListener);
        mLinkHelper.setActivityConenctListener(mIConnectListener);
    }

    /**
     * 初始化设置播放器参数(除开url等)
     */
    private void initVideoPlayer() {
        LogUtils.d(TAG, "initVideoPlayer");
        GSYVideoOptionBuilder videoOption = new GSYVideoOptionBuilder();
        videoOption.setIsTouchWiget(true)
                .setRotateViewAuto(true)
                .setNeedLockFull(true)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setCacheWithPlay(false)
                .setDismissControlTime(6000)
                .setSeekRatio(4)
                .setNeedShowWifiTip(false)
                .build(mVideoPlayer);
        addPlayerCallBack();
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
    }

    private void initIJkOptions() {
        List<VideoOptionModel> list = new ArrayList<>();
        //解决切换进度导致无限加载
//       list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1));
        //解决miui10 android9以上 倍速播放无效的问题
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1));
        GSYVideoManager.instance().setOptionModelList(list);
    }

    private void addPlayerCallBack() {
        mVideoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                LogUtils.d(TAG, "onPrepared" + objects[0]);
                LogUtils.d(TAG, "onPrepared" + objects[1]);
                super.onPrepared(url, objects);
                //需要先播放片头广告
                if (getCurPlay() != null && !getCurPlay().isShowingHeadAd()) {
                    // 用来判断是否已经有播放进度 有播放进度获取播放进度 无进度 播放进度设置为 0
                    mCurrentTime = 0;
                    if (mVideoDegree != null && mVideoDegree.endsWith("%")) {
                        String percentStr = mVideoDegree.substring(0, mVideoDegree.length() - 1);
                        try {
                            float percentFloat = Float.parseFloat(percentStr) / 100;
                            mCurrentTime = (long) (getCurPlay().getGSYVideoManager().getDuration() * percentFloat);
                            getCurPlay().seekTo(mCurrentTime);
                        } catch (NumberFormatException e) {
                            LogUtils.e(TAG, e.getMessage());
                        }
                    }
                    mVideoDegree = null;
                    LogUtils.d(TAG, "startVideoPlay currentTime ==" + mCurrentTime);
                    mPresenter.watchVideoTimer(mVideoPlayer);
                }
                //开始播放了才能旋转和全屏
                if (null != mOrientationUtils) {
                    mOrientationUtils.setEnable(true);
                    isPlay = true;
                }
                //开始播放之后隐藏等待提示图
                // hideWaitPic();
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                //title
                Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);
                //当前全屏player
                Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                //title
                LogUtils.d(TAG, "onQuitFullscreen" + objects[0]);
                //当前非全屏player
                LogUtils.d(TAG, "onQuitFullscreen" + objects[1]);
                if (null != mOrientationUtils) {
                    mOrientationUtils.backToProtVideo();
                }
            }
        });
        mVideoPlayer.setLockClickListener((view, lock) -> {
            if (null != mOrientationUtils) {
                mOrientationUtils.setEnable(!lock);
            }
        });
        mVideoPlayer.getFullscreenButton().setOnClickListener(v -> {
            // 直接横屏
            if (null != mOrientationUtils) {
                mOrientationUtils.resolveByClick();
            }
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusBar
            mVideoPlayer.startWindowFullscreen(PlayerActivity.this, true, true);
            //隐藏软键盘
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != imm) {
                imm.hideSoftInputFromWindow(mEtComment.getWindowToken(), 0);
            }
        });
        mVideoPlayer.getStartButton().setOnClickListener(v -> {
            if (getCurPlay() == null) {
                return;
            }
            LogUtils.d(TAG, "getCurrentState ==" + getCurPlay().getCurrentState());
            switch (getCurPlay().getCurrentState()) {
                case GSYVideoView.CURRENT_STATE_NORMAL:
                case GSYVideoView.CURRENT_STATE_AUTO_COMPLETE:
                case GSYVideoView.CURRENT_STATE_ERROR:
                    if (null != mPresenter) {
                        mPresenter.isMobileNetwork();
                    }
                    break;
                case GSYVideoView.CURRENT_STATE_PLAYING:
                    getCurPlay().onVideoPause();
                    // 手动点击暂停按键时,也暂停计时器任务
                    mPresenter.cancelWatchVideoTimer();
                    break;
                case GSYVideoPlayer.CURRENT_STATE_PAUSE:
//                  getCurPlay().onVideoResume(false);
                    getCurPlay().startAfterPrepared();
                    // 手动点击开始按键时,开启计时器任务
                    mPresenter.watchVideoTimer(mVideoPlayer);
                    break;
                default:
                    break;
            }
        });

        mVideoPlayer.setVideoFunctionListener(new VideoFunctionListener() {

            @Override
            public void onFeedbackClick() {
                LogUtils.d(TAG, "mVideoPlayer Feedback");
                showFeedbackDialog(PlayerActivity.this);

            }

            @Override
            public void onCastClick() {
                LogUtils.d(TAG, "mVideoPlayer Click TV Cast");
                clickCast();
            }

            @Override
            public void onSelectVideo(int position, PlayerUrlListBean bean) {
                LogUtils.d(TAG, "mVideoPlayer choose video at " + position);
                mPresenter.setItemSelectedPosition(position);
                mPresenter.getVideoPlayUrl(bean.getVideoUrl(), bean.getVideoName());
            }

            @Override
            public void onShareWX() {
                LogUtils.d(TAG, "mVideoPlayer share to wx");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_SESSION);
            }

            @Override
            public void onShareFriends() {
                LogUtils.d(TAG, "mVideoPlayer share to 朋友圈");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                mPresenter.share2WeChat(getResources(), userShareUrl, AppConstant.WX_SCENE_TIMELINE);
            }

            @Override
            public void onCopyUrl() {
                LogUtils.d(TAG, "mVideoPlayer share copy url");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String shareText = getString(R.string.activity_share_text_share_url, userShareUrl);
                manager.setPrimaryClip(ClipData.newPlainText(null, shareText));
                ToastUtils.showToast(PlayerActivity.this, "已将分享链接复制到剪切板");
            }

            @Override
            public void onVideoCollect(boolean isCollected) {
                LogUtils.d(TAG, "mVideoPlayer set collection status to " + isCollected);
                clickCollection();
            }

            @Override
            public void onTimerClose(int minutes) {
                LogUtils.d(TAG, "mVideoPlayer start timer to close: " + minutes);
                mPresenter.startCloseTimer(minutes);
            }

            @Override
            public void onTimerCloseByCurrentLength() {
                LogUtils.d(TAG, "mVideoPlayer finish Activity by play finish");
                mPresenter.onTimerClose();
            }

            @Override
            public void onCaptureImage(File file, File cover, boolean isGif) {
                LogUtils.d(TAG, "mVideoPlayer capture image");
                //显示分享图片的dialog
                showShareImageDialog(file, cover, isGif);
            }

            @Override
            public void startFullScreen() {
                // 直接横屏
                if (null != mOrientationUtils) {
                    mOrientationUtils.resolveByClick();
                }
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusBar
                mVideoPlayer.startWindowFullscreen(PlayerActivity.this, true, true);
            }

            @Override
            public void resolveToNormal() {
                if (null != mOrientationUtils) {
                    mOrientationUtils.backToProtVideo();
                }
                GSYVideoManager.backFromWindowFull(PlayerActivity.this);
            }

            @Override
            public void closeActivity() {
                finishActivity();
            }

            @Override
            public void addAdInfo(int adType, @Nullable String adId) {
                mPresenter.addAdInfo(adType, adId);
            }

            @Override
            public boolean hasStoragePermission() {
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(PlayerActivity.this, perms)) {
                    return true;
                }
                EasyPermissions.requestPermissions(PlayerActivity.this, getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
                return false;
            }

            @Override
            public void clickBackOrForward(int btnType) {
                LogUtils.d(TAG, "clickBackOrForward" + "btnType : " + btnType);
                mPresenter.clickBackOrForward(btnType);
            }
        });

        mVideoPlayer.setCastListener(new FullscreenCastListener() {

            @Override
            public void onCastBackPressed() {
                onBackPressed();
            }

            @Override
            public boolean onGetDeviceList() {
                LogUtils.d(TAG, "mVideoPlayer Click TV Cast");
                return clickCast();
            }

            @Override
            public void onExitCast() {
                exitCast();
            }

            @Override
            public void onSwitchDevice() {
                castBrowse();
            }

            @Override
            public void onStartAndPauseCastPlay() {
                List<LelinkServiceInfo> connectInfos = null;
                if (null != mLinkHelper) {
                    connectInfos = mLinkHelper.getConnectInfos();
                }
                clickCastStart(connectInfos);
            }

            @Override
            public void onSeekProgress(int progress) {
                LogUtils.d(TAG, "seek click progress : " + progress);
                if (null != mLinkHelper) {
                    mLinkHelper.seekTo(progress);
                }
            }

            @Override
            public void onIncreaseVolume() {
                LogUtils.d(TAG, "cast volume_plus");
                List<LelinkServiceInfo> connectInfos = null;
                if (null != mLinkHelper) {
                    connectInfos = mLinkHelper.getConnectInfos();
                }
                if (null != connectInfos && !connectInfos.isEmpty()) {
                    mLinkHelper.voulumeUp();
                }
            }

            @Override
            public void onReduceVolume() {
                LogUtils.d(TAG, "cast volume_minus");
                List<LelinkServiceInfo> connectInfos = null;
                if (null != mLinkHelper) {
                    connectInfos = mLinkHelper.getConnectInfos();
                }
                if (null != connectInfos && !connectInfos.isEmpty()) {
                    mLinkHelper.voulumeDown();
                }
            }

            @Override
            public void onChooseDevice(LelinkServiceInfo info) {
                //开始投屏,先获取当前播放时间,存储在本地, 之后暂停播放器,防止播放器播放
                if (getCurPlay() != null) {
                    mCastPosition = getCurPlay().getCurrentPositionWhenPlaying();
                    getCurPlay().onVideoReset();
                }
                castStopBrowse();
                castConnect(info);
                mSelectInfo = info;
            }

            @Override
            public void onCancelChooseDevice() {
                cancelCast();
            }
        });
    }

    /**
     * 显示分享图片的dialog
     *
     * @param file
     * @param isGif
     */
    private void showShareImageDialog(File file, File cover, boolean isGif) {
        String imagePath = file == null ? null : file.getAbsolutePath();
        String coverPth = cover == null ? null : cover.getAbsolutePath();
        ShareImageDialogFragment fragment = ShareImageDialogFragment.newInstance(imagePath, coverPth, isGif);
        fragment.show(getSupportFragmentManager(), "ShareImageDialogFragment");
        fragment.setOnDismissListener(() -> {
            if (getCurPlay() != null) {
                getCurPlay().onVideoResume();
            }
        });
        //通知系统相册刷新
        fragment.setSaveImageListener(this::notifyUpdateImages);
    }

    /**
     * @param videoPlayUrl     视频的播放地址
     * @param videoName        视频的名称
     * @param videoOriginalUrl 未解析前的ffhd:// 地址
     */
    @Override
    public void startVideoPlay(final String videoPlayUrl, final String videoName, final String videoOriginalUrl) {
        LogUtils.d(TAG, "initVideoPlayer");
        String videoFullName = mVideoTitle + " " + videoName;
        // 开始播放后, 添加一次电影计数事件
        MobclickAgent.onEvent(TestApp.getContext(), "detail_video_name", mVideoTitle);
        mVideoOriginalUrl = videoOriginalUrl;
        mVideoName = videoName;
        mPresenter.getCurrentNetSpeed(getCurPlay());
        mPresenter.getSDCardFreeSize();
        LogUtils.d(TAG, "startVideoPlay videoPlayUrl ==" + videoPlayUrl);
        if (getCurPlay() == null) {
            return;
        }
        //切换播放地址，初始化播放器
        if (mVideoAdData == null) {
            getCurPlay().setUp(videoPlayUrl, false, videoFullName);
        } else {
            getCurPlay().setAdUp(mVideoAdData, videoPlayUrl, videoFullName, mUserLevel.equals(AppConstant.USER_VIP) || mUserLevel.equals(AppConstant.USER_VIP_LASTDAY));
            preloadImages(mVideoAdData);
        }
        //添加到历史纪录
        if (videoPlayUrl.startsWith(AppConstant.FFHD_HEAD)) {
            if (null != getCurPlay()) {
                mPresenter.addHistory(mVideoId, mVideoTitle, mVideoName, mVideoOriginalUrl, mCurrentTime, getCurPlay().getGSYVideoManager().getDuration());
            }
        } else {
            if (null != getCurPlay()) {
                mPresenter.addHistory(mVideoId, mVideoTitle, mVideoName, videoPlayUrl, mCurrentTime, getCurPlay().getGSYVideoManager().getDuration());
            }
        }
    }

    //使用 Glide 预加载暂停广告图 与 小卡片广告图
    private void preloadImages(VideoAdDataBean videoAdDataBean) {
        if (null != videoAdDataBean) {
            String pauseAdUrl = Objects.requireNonNull(videoAdDataBean.getPauseAd()).getAdUrl();
            String playAdUrl = Objects.requireNonNull(videoAdDataBean.getPlayAd()).getAdUrl();
            if (!TextUtils.isEmpty(pauseAdUrl)) {
                GlideApp.with(this).load(pauseAdUrl).preload();
            }
            if (!TextUtils.isEmpty(playAdUrl)) {
                GlideApp.with(this).load(playAdUrl).preload();
            }
        }
    }

    /**
     * 投屏显示投屏对话框
     */
    private void showCastDialog() {

        // 弹出投屏选择对话框之后开始搜索投屏设备
        castBrowse();
        CastDialogFragment castDialogFragment = CastDialogFragment.newInstance();

        castDialogFragment.setCastItemClickListener(new CastDialogFragment.CastItemClickListener() {
            @Override
            public void onCastItemClick(int position, LelinkServiceInfo info) {
                LogUtils.d(TAG, "dialog click device position : " + position + " name : " + info.getName());
                castDialogFragment.dismiss();
                castStopBrowse();
                castConnect(info);
                mSelectInfo = info;
            }


            @Override
            public void onCancelClick() {
                castDialogFragment.dismiss();
                cancelCast();
            }


            @Override
            public void onHelperClick() {
                ARouter.getInstance().build("/castHelper/activity").navigation();

            }
        });

        castDialogFragment.show(getSupportFragmentManager(), "castDialog");
    }

    private void cancelCast() {
        castStopBrowse();
        if (getCurPlay() != null) {
            getCurPlay().setSeekOnStart(mCastPosition);
            //调用次方法会导致再放一次广告
//        startPlayLogic();
            //调用次方法会直接开始播放视频，不会播放广告
            getCurPlay().startPlayVideo();
        }
    }

    private void hideSentBtn() {      // 隐藏发送那妞的时候,展示另3个功能按钮
        if (null != mCtxCollection && null != mIvShare && null != mIvDownload && null != mTvSendComment) {
            mCtxCollection.setVisibility(View.VISIBLE);
            mIvShare.setVisibility(View.VISIBLE);
            mIvDownload.setVisibility(View.VISIBLE);
            mTvSendComment.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.tv_selectAll_button_player, R.id.iv_share_player, R.id.iv_close_videoSelect, R.id.ctx_collection_player,
            R.id.iv_download_player, R.id.iv_close_videoDetail, R.id.iv_close_downloadSelect, R.id.tv_getDetails_player,
            R.id.iv_close_waitPic_player, R.id.tv_sendComment_player, R.id.tv_exitCast_castControl,
            R.id.tv_switchDevice_castControl, R.id.ib_volume_plus_castControl, R.id.ib_volume_minus_castControl, R.id.iv_start_castControl,
            R.id.iv_back_castControl, R.id.iv_ad, R.id.tv_continue_mobile_network_play, R.id.tv_comment, R.id.iv_comment, R.id.tv_feedback, R.id.iv_feedback,
            R.id.tv_download_jump, R.id.tv_retry, R.id.iv_back})
    public void onViewClicked(View view) {
        List<LelinkServiceInfo> connectInfos = null;
        if (null != mLinkHelper) {
            connectInfos = mLinkHelper.getConnectInfos();
        }
        switch (view.getId()) {
            case R.id.iv_share_player:
                LogUtils.d(TAG, "onViewClicked iv_share_player");
                showShareDialog();
                break;
            case R.id.ctx_collection_player:
                LogUtils.d(TAG, "onViewClicked cb_collection_player status ==" + mCtxCollection.isChecked());
                clickCollection();
                break;
            case R.id.tv_selectAll_button_player:
                LogUtils.d(TAG, "onViewClicked tv_selectAll_button_player");
                mFlSelect.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_close_videoSelect:
                LogUtils.d(TAG, "onViewClicked iv_close_videoSelect");
                mFlSelect.setVisibility(View.GONE);
                break;
            case R.id.iv_download_player:
                LogUtils.d(TAG, "onViewClicked iv_download_player");
                clickDownload();
                mPresenter.addDownloadCount(mVodId);
                break;
            case R.id.iv_close_downloadSelect:
                LogUtils.d(TAG, "onViewClicked iv_close_downloadSelect");
                mFlDownload.setVisibility(View.GONE);
                break;
            case R.id.tv_getDetails_player:
                LogUtils.d(TAG, "onViewClicked tv_getDetails_player");
                mClDetails.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_close_videoDetail:
                LogUtils.d(TAG, "onViewClicked iv_close_videoDetail");
                mClDetails.setVisibility(View.GONE);
                break;
            case R.id.iv_close_waitPic_player:
                hideWaitPic();
                break;
            case R.id.tv_sendComment_player:
                commentVideo();
                break;
            case R.id.tv_exitCast_castControl:
                exitCast();
                break;
            case R.id.tv_switchDevice_castControl:
                LogUtils.d(TAG, "cast switchDevice");
                showCastDialog();

                break;
            case R.id.ib_volume_plus_castControl:
                LogUtils.d(TAG, "cast volume_plus");
                if (null != mLinkHelper && null != connectInfos && !connectInfos.isEmpty()) {
                    mLinkHelper.voulumeUp();
                }

                break;
            case R.id.ib_volume_minus_castControl:
                LogUtils.d(TAG, "cast volume_minus");
                if (null != mLinkHelper && null != connectInfos && !connectInfos.isEmpty()) {
                    mLinkHelper.voulumeDown();
                }

                break;
            case R.id.iv_start_castControl:
                LogUtils.d(TAG, "cast start");
                clickCastStart(connectInfos);

                break;
            case R.id.iv_back_castControl:
                LogUtils.d(TAG, "cast back");
                if (null != mLinkHelper) {
                    mLinkHelper.stop();
                }
                finish();
                break;
            case R.id.iv_ad:
                onAdClick();
                break;
            case R.id.tv_continue_mobile_network_play:
                mLayoutMobileNetwork.setVisibility(View.GONE);
                startPlayLogic();
                break;
            case R.id.iv_comment:
            case R.id.tv_comment:
                //滚动到评论
                mScrollView.scrollTo(0, mTvCommentTitle.getTop());
                break;
            case R.id.iv_feedback:
            case R.id.tv_feedback:
                ARouter.getInstance().build("/feedback/activity").withString("vodId", mVideoId).navigation();
                break;
            case R.id.tv_download_jump:
                ARouter.getInstance().build("/download/activity").navigation(this, ACTIVITY_REQUEST_CODE_DOWNLOAD);
                break;
            case R.id.tv_retry:
                onRetry();
                break;
            case R.id.iv_back:
                finishActivity();
                break;
            default:
                break;
        }
    }

    private void exitCast() {
        LogUtils.d(TAG, "cast exitCast");
        mCastControl.setVisibility(View.GONE);
        if (null != mLinkHelper) {
            mLinkHelper.stop();
            castDisConnect(true);
            // 恢复本地播放器继续播放
            if (getCurPlay() != null) {
                getCurPlay().setSeekOnStart(mCastPosition);
                getCurPlay().startPlayLogic();
            }
        }
    }

    private void onAdClick() {
        Object tag = mIvAd.getTag(mIvAd.getId());
        if (tag instanceof AdInfoBean) {
            AdInfoBean adInfoBean = (AdInfoBean) tag;
            switch (adInfoBean.getType()) {
                case AdInfoBean.Type.WEB:
                    LogUtils.d(TAG, "AD_TYPE_WEB");
                    if (adInfoBean.getAdUrl() != null) {
                        startActivity(IntentUtils.getBrowserIntent(adInfoBean.getAdUrl()));
                    }
                    break;
                case AdInfoBean.Type.DOWNLOAD:
                    LogUtils.d(TAG, "AD_TYPE_DOWNLOAD");
                    String downloadUrl = adInfoBean.getAndroidUrl();
                    Context context = this;
                    if (downloadUrl != null) {
                        DownloadUtil.startDownloadOrOpenDownloadedFile(context, downloadUrl);
                    }
                    break;
                default:
                    break;
            }
            mPresenter.addAdInfo(AppConstant.AD_TYPE_PLAYER, adInfoBean.getId());
        }
    }


//    @OnTouch(R.id.et_comment_player)
//    boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                LogUtils.d(TAG, "et_comment_player touch down");
//                UserCenterBean userCenterBean = (UserCenterBean) SpUtils.getSerializable(this, AppConstant.USER_INFO);
//                assert userCenterBean != null;
//                if (TextUtils.isEmpty(userCenterBean.getMobile())) {
//                    isBindingPhone = true;
//                    ARouter.getInstance().build("/bindphone/activity/float").navigation();
//                    return true;
//                }
//                break;
//            default:
//                break;
//        }
//        return false;
//    }


    private void clickCastStart(List<LelinkServiceInfo> connectInfos) {
        if (null != mLinkHelper && null != connectInfos && !connectInfos.isEmpty()) {
            if (castIsPause) {
                mLinkHelper.resume();
            } else {
                mLinkHelper.pause();
            }
        }
    }

    private void castBrowse() {
        if (null != mLinkHelper) {
            //开始投屏,先获取当前播放时间,存储在本地, 之后暂停播放器,防止播放器播放
            if (getCurPlay() != null) {
                mCastPosition = getCurPlay().getCurrentPositionWhenPlaying();
                getCurPlay().onVideoReset();
            }
            mLinkHelper.browse(ILelinkServiceManager.TYPE_ALL);
            LogUtils.d(TAG, "browse type Success");
        } else {
            LogUtils.d(TAG, "browse type Failed");
            ToastUtils.showLongToast(TestApp.getContext(), "权限不足，请您同意权限申请");
        }
    }

    private void castStopBrowse() {
        if (null != mLinkHelper) {
            LogUtils.d(TAG, "castStopBrowse type Success");
            mLinkHelper.stopBrowse();
        } else {
            LogUtils.d(TAG, "castStopBrowse type Failed");
            ToastUtils.showLongToast(TestApp.getContext(), "未初始化");
        }
    }

    private void castConnect(LelinkServiceInfo info) {
        LogUtils.d(TAG, "castConnect name : " + info.getName());
        if (null != mLinkHelper) {
            LogUtils.d(TAG, "Start Connect name : " + info.getName());
            mLinkHelper.connect(info);
            mTvCastTitleCast.setText(info.getName());
            mCastControl.setVisibility(View.VISIBLE);
            if (getCurPlay() != null) {
                getCurPlay().setCastDeviceName(info.getName());
                getCurPlay().showCastLayout();
            }
        } else {
            ToastUtils.showLongToast(TestApp.getContext(), "未初始化或者未选择设备");
        }
    }

    @Override
    public void updateBrowseAdapter() {
        if (null != mLinkHelper) {
            List<LelinkServiceInfo> infos = mLinkHelper.getInfos();
            if (getCurPlay() != null) {
                getCurPlay().setCastDeviceList(infos);
            }
            if (null != infos) {
                EventBus.getDefault().post(infos);
            } else {
                LogUtils.d(TAG, "infos  : null");
            }
        }
    }


    @Override
    public void setVideoCollected(boolean isCollected) {
        if (getCurPlay() != null) {
            getCurPlay().setCollected(isCollected);
        }
        mCtxCollection.setChecked(isCollected);
    }

    @Override
    public void setVideoUrl(String playUrl) {
        mVideoUrl = playUrl;
    }

    @Override
    public void setDegree(String degree) {
        mVideoDegree = degree;
    }

    @Override
    public void setPlayerCloseTimerText(String time) {
        if (getCurPlay() != null) {
            getCurPlay().setCloseTimerText(time);
        }
    }

    @Override
    public void setPlayerCloseTimeStatus(int closeTime) {
        if (getCurPlay() != null) {
            getCurPlay().setCloseTimeType(closeTime);
        }
    }

    @Override
    public void showTimerCloseDialog(TimeCloseDialogFragment.OnButtonClickListener onButtonClickListener) {
        TimeCloseDialogFragment.newInstance(onButtonClickListener).show(getSupportFragmentManager(), "TimeCloseDialogFragment");
    }


    @Override
    public void videoReset() {
        if (null != getCurPlay()) {
            getCurPlay().onVideoReset();
            mPresenter.cancelWatchVideoTimer();
        }
    }

    @Override
    public void playNext() {
        if (getCurPlay() != null) {
            getCurPlay().playNext();
        }
    }

    @Override
    public void showPlayNextNotice() {
        if (null != getCurPlay()) {
            getCurPlay().showPlayNextNotice();
        }
    }

    @Override
    public void pausePlay() {
        if (getCurPlay() != null) {
            getCurPlay().onVideoPause();
            mPresenter.cancelWatchVideoTimer();
        }
    }

    @Override
    public void resumePlay() {
        if (getCurPlay() != null) {
            getCurPlay().onVideoResume();
            //恢复时, 继续开始定时任务  需要先判断当前播放器是否在播放状态.
            Log.d("PlayerPresenter", "PlayVideo statue : " + getCurPlay().getCurrentState() + " isPlay : " + getCurPlay().isInPlayingState());
            if (getCurPlay().isInPlayingState()) {
                mPresenter.watchVideoTimer(mVideoPlayer);
            }
        }
    }

    @Override
    public void resetTimerCloseText() {
        if (getCurPlay() != null) {
            getCurPlay().resetTimerCloseText();
        }
    }

    @Override
    public void uploadWatchTime() {
        //TODO  此处需要实现上报接口
        mPresenter.uploadWatchTime(mVideoCid, mVideoPid);
        LogUtils.d("PlayerPresenter", "uploadWatchTime :" + " Cid :" + mVideoCid + " Pid : " + mVideoPid);
    }

    @Override
    public void setAdData(VideoAdDataBean videoAdDataBean) {
        mVideoAdData = videoAdDataBean;
    }

    private void castDisConnect(boolean isBtnClick) {
        if (null != mLinkHelper) {
            List<LelinkServiceInfo> connectInfos = mLinkHelper.getConnectInfos();
            if (null != connectInfos) {
                if (isBtnClick) {
                    // 断开所有连接设备
                    for (LelinkServiceInfo info : connectInfos) {
                        mLinkHelper.disConnect(info);
                    }
                } else {
                    //断开除了当前选择连接意外的所有连接
                    for (LelinkServiceInfo info : connectInfos) {
                        if (!mPresenter.isContains(mSelectInfo, info)) {
                            mLinkHelper.disConnect(info);
                        }
                    }
                }
            } else {
                ToastUtils.showLongToast(TestApp.getContext(), "未初始化或者未选择设备");
            }
        }
    }

    private void castPlay(String url) {
        LogUtils.i(TAG, "castPlay Thread : " + Thread.currentThread().getName() + " url : " + url);
        if (null == mLinkHelper) {
            ToastUtils.showLongToast(getApplicationContext(), "未初始化或未选择设备");
            return;
        }
        List<LelinkServiceInfo> connectInfos = mLinkHelper.getConnectInfos();

        if (null == connectInfos || connectInfos.isEmpty()) {
            ToastUtils.showLongToast(getApplicationContext(), "请先连接设备");
            return;
        }

        if (castIsPause) {
            LogUtils.d(TAG, "castPlay resume click");
            castIsPause = false;
            //暂停中
            mLinkHelper.resume();
            return;
        } else {
            LogUtils.d(TAG, "castPlay play click");
        }

        LogUtils.i(TAG, "playUrl : " + url + " type : " + AllCast.MEDIA_TYPE_VIDEO);

        mPresenter.castVideoUrl(mLinkHelper, url, mVideoName);
    }

    @Override
    @AfterPermissionGranted(AppConstant.PERSSION_READ_PHONE_STATE)
    protected void requirePhonePerm() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            LogUtils.d(getClass().getSimpleName(), "Already have permission, do the thing");
            if (getCurPlay() != null) {
                if (getCurPlay().isIfCurrentIsFullscreen()) {
                    //搜索设备
//                    castBrowse();
                    if (mLinkHelper != null) {
                        mLinkHelper.browse(ILelinkServiceManager.TYPE_ALL);
                    }
                } else {
                    showCastDialog();
                }
            }
        } else {
            LogUtils.d(getClass().getSimpleName(), "Do not have permissions, request them now");
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_perm_read_phone_state), AppConstant.PERSSION_READ_PHONE_STATE, perms);
        }
    }

    /**
     * 保存截图成功，通知系统相册刷新
     */
    private void notifyUpdateImages(File image) {
        if (image == null) {
            return;
        }
        LogUtils.d(TAG, "通知系统刷新相册" + image.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(image);
        intent.setData(uri);
        sendBroadcast(intent);
        /*
                MediaScannerConnection.scanFile(this
                , new String[]{image.getAbsolutePath()}
                , new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        LogUtils.d(TAG, "通知系统刷新相册" + path);
                    }
                });
         */
    }

    /**
     * 禁止输入框复制粘贴菜单
     */
    @SuppressLint("ClickableViewAccessibility")
    public void disableCopyAndPaste(final EditText editText) {
        try {
            if (editText == null) {
                return;
            }
            editText.setOnLongClickListener(v -> true);
            editText.setLongClickable(false);
            editText.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // setInsertionDisabled when user touches the view
                    setInsertionDisabled(editText);
                    LogUtils.d(TAG, "et_comment_player touch down");
                    UserCenterBean userCenterBean = SpUtils.getSerializable(PlayerActivity.this, AppConstant.USER_INFO);
                    assert userCenterBean != null;
                    if (TextUtils.isEmpty(userCenterBean.getMobile())) {
                        isBindingPhone = true;
                        ARouter.getInstance().build("/bindphone/activity/float").navigation();
                        return true;
                    }
                }
                return false;
            });
            editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInsertionDisabled(EditText editText) {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(editText);

            // if this view supports insertion handles
            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);

            // if this view supports selection handles
            Field mSelectionControllerEnabledField = editorClass.getDeclaredField("mSelectionControllerEnabled");
            mSelectionControllerEnabledField.setAccessible(true);
            mSelectionControllerEnabledField.set(editorObject, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.navigation_background));
            ViewGroup contentView = this.findViewById(Window.ID_ANDROID_CONTENT);
            View childView = contentView.getChildAt(0);
            if (null != childView) {
                childView.setFitsSystemWindows(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CODE_DOWNLOAD && resultCode == RESULT_OK) {
            if (mDownloadSelectItemAdapter != null) {
                mDownloadSelectItemAdapter.setData(mPresenter.queryDownloadStatus(new ArrayList<>(mDownloadSelectItemAdapter.getData()), mVodId));
            }
        }
    }

    @Override
    public String getVideoId() {
        return mVideoId;
    }

    @Override
    public PlayerDownloadSelectItemAdapter getDownloadAdapter() {
        return mDownloadSelectItemAdapter;
    }

    @Override
    public void showFirstMobileDownloadConfirmDialog(String downloadUrl, String videoId, String videoName, String videoItemName) {
        new MaterialDialog.Builder(this)
                .content("当前已允许使用移动网络缓存视频，3G、4G网络下将继续缓存\n如您只想使用WIFI缓存视频，请到“设置”中关闭移动数据缓存")
                .negativeColor(Color.parseColor("#888888"))
                .onNegative((dialog, action) -> {
                    dialog.dismiss();
                    mPresenter.startDownload(downloadUrl, videoId, videoName, videoItemName, true);
                })
                .negativeText("允许流量缓存")
                .positiveColor(Color.parseColor("#ffad43"))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    ARouter.getInstance().build("/setting/activity").navigation();
                    mPresenter.startDownload(downloadUrl, videoId, videoName, videoItemName, true);
                })
                .positiveText("去设置")
                .build()
                .show();
    }
}
