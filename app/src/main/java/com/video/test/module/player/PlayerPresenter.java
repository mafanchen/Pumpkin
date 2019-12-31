package com.video.test.module.player;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hpplay.common.utils.LeLog;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.video.test.AppConstant;
import com.video.test.BuildConfig;
import com.video.test.R;
import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.AddCollectionBean;
import com.video.test.javabean.HistoryListBean;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.javabean.VideoAdDataBean;
import com.video.test.javabean.VideoCommentBean;
import com.video.test.javabean.VideoPlayerBean;
import com.video.test.javabean.event.CollectEvent;
import com.video.test.javabean.event.DownloadEvent;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.ui.widget.LandLayoutVideo;
import com.video.test.ui.widget.TimeCloseDialogFragment;
import com.video.test.utils.FileUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.M3U8Utils;
import com.video.test.utils.NetworkUtils;
import com.video.test.utils.RxSchedulers;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;
import com.video.test.utils.cast.AllCast;
import com.video.test.utils.cast.LelinkHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.cache.Sp;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import jaygoo.library.m3u8downloader.M3U8Downloader;
import jaygoo.library.m3u8downloader.bean.M3U8Task;
import jaygoo.library.m3u8downloader.bean.M3U8TaskState;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class PlayerPresenter extends PlayerContract.Presenter<PlayerModel> {
    private static final String TAG = "PlayerPresenter";
    private static final String SP_KEY_FIRST_MOBILE_DOWNLOAD = "FIRST_MOBILE_DOWNLOAD";
    private boolean isFirstMobileDownload = false;
    private IWXAPI mWxApi;
    private static final int THUMB_SIZE = 150;
    private Disposable closeTimerDisposable;
    private long mWatchTime = 0L;
    /**
     * 选中的倒计时关闭类型
     */
    private int mMinutes = LandLayoutVideo.CLOSE_TIME_NONE;

    /**
     * 剩余倒计时秒数
     */
    private int mCloseSeconds = 0;
    private Disposable mWatchVideoTimer;

    @Override
    public void subscribe() {

    }

    @Override
    public void attachView(PlayerContract.View view) {
        super.attachView(view);
        isFirstMobileDownload = SpUtils.getBoolean(TestApp.getContext(), SP_KEY_FIRST_MOBILE_DOWNLOAD, true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void unSubscribe() {
        super.unSubscribe();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取整套电视剧信息，包括每一集的信息
     *
     * @param videoId
     */
    @Override
    void getVideoPlayerContent(String videoId) {
        mView.setSwipeRefreshStatus(true);
        Disposable disposable = Observable.zip(Observable.create(emitter -> {
                    DBManager manager = DBManager.getInstance(TestApp.getContext());
                    HistoryListBean bean = manager.queryHistoryByVodId(videoId);
                    if (bean == null) {
                        bean = new HistoryListBean();
                    }
                    emitter.onNext(bean);
                })
                , mModel.getVideoAd()
                , mModel.getVideoPlayerContent(getQueryHashMap(videoId))
                , (Function3<HistoryListBean, VideoAdDataBean, VideoPlayerBean, VideoPlayerBean>) (historyListBean, videoAdDataBean, videoPlayerBean) -> {
                    if (TextUtils.equals(videoId, historyListBean.getVod_id())) {
                        String url = historyListBean.getPlay_url();
                        String degree = historyListBean.getPlay_degree();
                        Log.d(TAG, String.format("restore play history from local db,url = %s,degree = %s"
                                , url == null ? "null" : url, degree == null ? "null" : degree));
                        mView.setVideoUrl(url);
                        mView.setDegree(degree);
                    }
                    if (videoAdDataBean != null) {
                        mView.setAdData(videoAdDataBean);
                    }
                    return videoPlayerBean;
                })
                .compose(RxSchedulers.io_main())
                .subscribe(videoPlayerBean -> {
                    LogUtils.d(TAG, "getVideoPlayerContent Success == ");
                    mView.getVideoPlayerContentSuccess(videoPlayerBean);
                    mView.setSwipeRefreshStatus(false);
                }, new RxExceptionHandler<>(throwable -> {
                    LogUtils.e(TAG, "getVideoPlayerContent Error == " + throwable.getMessage());
                    mView.setSwipeRefreshStatus(false);
                    mView.showNetworkErrorView();
                }));
        addDisposable(disposable);
    }


    @Override
    void addCollections(String vodId) {

        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
        Disposable disposable = mModel.addCollections(vodId, userToken, userTokenId)
                .subscribe(new Consumer<AddCollectionBean>() {
                    @Override
                    public void accept(AddCollectionBean addCollectionBean) {
                        mView.addCollectionSuccess(addCollectionBean);
                        ToastUtils.showToast(TestApp.getContext(), "添加收藏成功");
                        EventBus.getDefault().post(new CollectEvent(true, vodId, addCollectionBean.getCollect_id()));
                        mView.setVideoCollected(true);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "addCollections Error == " + throwable.getMessage());
                        ToastUtils.showToast(TestApp.getContext(), "添加收藏失败");
                        mView.setVideoCollected(false);
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void delCollections(String ids) {
        List<String> idList = new ArrayList<>();
        Gson gson = new Gson();
        idList.add(ids);
        String jsonList = gson.toJson(idList);
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        LogUtils.d(TAG, "delCollections json == " + idList);
        Disposable disposable = mModel.delCollections(jsonList, userToken, userTokenId)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        ToastUtils.showToast(TestApp.getContext(), "取消收藏成功");
                        mView.setVideoCollected(false);
                        EventBus.getDefault().post(new CollectEvent(false, null, ids));
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "delCollections Error " + throwable.getMessage());
                        ToastUtils.showToast(TestApp.getContext(), "取消收藏失败");
                        mView.setVideoCollected(true);
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void addHistory(String vodId, String videoTitle, String videoName, String playUrl, long currentTime, long totalTime) {
        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        mModel.addHistory(vodId, videoName, playUrl, currentTime, totalTime, userToken, userTokenId)
                .subscribe(new Observer<String>() {
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.disposable = d;
                    }

                    @Override
                    public void onNext(String message) {
                        LogUtils.d(TAG, "addHistory onNext == " + message);
                        mView.addHistoryBeanSuccess(message);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d(TAG, "addHistory Error == " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        LogUtils.d(TAG, "addHistory onComplete == " + disposable.isDisposed());
                    }
                });
        //添加到本地播放纪录
        SpUtils.putString(TestApp.getContext(), "historyVodId", vodId);
        String degree = String.format(Locale.getDefault(), "%.2f%%", (float) currentTime * 100 / totalTime);
        HistoryListBean bean = new HistoryListBean(vodId, videoTitle + videoName, null, null, playUrl, degree, String.valueOf(currentTime), String.valueOf(totalTime));
        Log.d(TAG, String.format("save play history to local db,url = %s,degree = %s"
                , playUrl, degree));
        DBManager.getInstance(TestApp.getContext()).addOrReplaceHistory(bean);
    }

    @Override
    void getVideoComment(String videoId) {
        Disposable disposable = mModel.getVideoComment(videoId)
                .subscribe(new Consumer<List<VideoCommentBean>>() {
                    @Override
                    public void accept(List<VideoCommentBean> commentBeanList) {
                        boolean isEmpty = commentBeanList.isEmpty();
                        LogUtils.d(TAG, "getVideoComment success isEmpty : " + isEmpty);
                        if (isEmpty) {
                            mView.showSofaBackground();
                        } else {
                            mView.hideSofaBackground();
                            mView.setVideoComment(commentBeanList);
                        }
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "getVideoComment Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }


    @Override
    void feedbackSuggest(String vodId, String suggestMessage) {
        Disposable disposable = mModel.feedbackSuggest(vodId, suggestMessage)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        mView.feedbackMessageSuccess(s);
                    }
                }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(TAG, "feedbackSuggest Error == " + throwable.getMessage());
                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void getVideoPlayUrl(String videoUrl, String videoName) {
        mView.videoReset();
        if (null != videoUrl) {
            Disposable disposable = Observable.just(videoUrl)
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String originUrl) throws Exception {
                            LogUtils.d(TAG, "apply Thread : " + Thread.currentThread().getName());
                            return M3U8Utils.parseIndex(originUrl);
                        }
                    })
                    .compose(RxSchedulers.io_main())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String videoPlayUrl) throws Exception {
                            mView.startVideoPlay(videoPlayUrl, videoName, videoUrl);
                            isMobileNetwork();
                            mView.setNetSpeed("视频加载中...");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.d(TAG, "getVideoPlayUrl error : " + throwable.getMessage());
                            ToastUtils.showLongToast(TestApp.getContext(), "视频解析异常，请您稍后重试");
                        }
                    });
            addDisposable(disposable);
        } else {
            ToastUtils.showLongToast(TestApp.getContext(), "视频地址异常,请您联系我们反馈");
        }
    }

    /**
     * 开始投屏
     *
     * @param helper
     * @param videoUrl
     * @param videoName
     */
    @Override
    void castVideoUrl(LelinkHelper helper, String videoUrl, String videoName) {
        if (videoUrl == null) {
            return;
        }
        Disposable disposable = Observable.just(videoUrl)
                .map(originUrl -> {
                    LogUtils.d(TAG, "castVideoUrl Thread : " + Thread.currentThread().getName());
                    return M3U8Utils.parseIndex(originUrl);
                })
                .compose(RxSchedulers.io_main())
                .subscribe(url -> {
                    LogUtils.d(TAG, "castVideoUrl success url : " + url);
                    helper.playNetMedia(url, AllCast.MEDIA_TYPE_VIDEO, null);
                }, throwable -> {
                    LogUtils.d(TAG, "castVideoUrl error : " + throwable.getMessage());
                    ToastUtils.showLongToast(TestApp.getContext(), "投屏解析异常，请您稍后重试");
                });
        addDisposable(disposable);
    }

    @Override
    void commentVideo(String vodId, EditText etContent, TextView tvComment) {
        if (null != etContent && null != tvComment) {
            tvComment.setEnabled(false);
            etContent.setEnabled(false);
            LogUtils.e(TAG, "commentVideo click ");
            String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
            String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");
            String content = etContent.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) {
                Disposable disposable = mModel.commentVideo(vodId, content, userToken, userTokenId)
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String message) {
                                LogUtils.e(TAG, "commentVideo Success  message : " + message);
                                ToastUtils.showLongToast(TestApp.getContext(), message);
                                etContent.setText("");
                                tvComment.setEnabled(true);
                                etContent.setEnabled(true);
                                getVideoComment(vodId);
                            }
                        }, new RxExceptionHandler<>(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                LogUtils.e(TAG, "commentVideo Error == " + throwable.getMessage());
                                tvComment.setEnabled(true);
                                etContent.setEnabled(true);

                            }
                        }));

                addDisposable(disposable);
            } else {
                ToastUtils.showLongToast(TestApp.getContext(), "评论内容不能为空");
            }

        }
    }

    private boolean isOpenMobileNetwork = false;

    @Override
    void isMobileNetwork() {
        int networkStatus = SpUtils.getInt(TestApp.getContext(), "networkStatus", 0);
//        if (AppConstant.MOBILE_NETWORK_CAN_USE == networkStatus) {
//            mView.showMobileNetworkDialog();
//        } else

//        if (AppConstant.MOBILE_NETWORK_CAN_NOT_USE == networkStatus) {
//            mView.isOpenMobileSwitch();
//        } else {
//            mView.startPlayLogic();
//        }
        if (!isOpenMobileNetwork && AppConstant.MOBILE_NETWORK_CAN_NOT_USE == networkStatus) {
            isOpenMobileNetwork = true;
            mView.showMobileNetworkDialog();
        } else {
            mView.startPlayLogic();
        }

    }

    @Override
    void isMobileNetworkDownload(String downloadUrl, String videoId, String videoName, String videoItemName) {
        int networkStatus = SpUtils.getInt(TestApp.getContext(), "networkStatus", 0);
        if (AppConstant.MOBILE_NETWORK_CAN_USE == networkStatus) {
            mView.showMobileNetworkDownloadDialog(downloadUrl, videoId, videoName, videoItemName);
        } else if (AppConstant.MOBILE_NETWORK_CAN_NOT_USE == networkStatus) {
            mView.isOpenMobileSwitch();
        } else {
            setDownloadUrl(downloadUrl, videoId, videoName, videoItemName);
        }
    }

    @Override
    void setDownloadUrl(String downloadUrl, String videoId, String videoName, String videoItemName) {
        LogUtils.d(TAG, "downUrl : " + downloadUrl);
        if (NetworkUtils.isWifiConnected(TestApp.getContext())) {
            startDownload(downloadUrl, videoId, videoName, videoItemName, true);
        } else {
            /*
            如果是流量则，判断流量下载开关是否打开，
            若打开（默认打开），则第一次会弹框提醒，后面会直接开始下载
            若关闭，则只是把任务添加到数据库，并且弹出toast提醒
             */
            boolean mobileNetworkOpen = SpUtils.getBoolean(TestApp.getContext(), AppConstant.SWITCH_MOBILE_DOWN, true);
            if (mobileNetworkOpen) {
                if (isFirstMobileDownload) {
                    isFirstMobileDownload = false;
                    SpUtils.putBoolean(TestApp.getContext(), SP_KEY_FIRST_MOBILE_DOWNLOAD, false);
                    mView.showFirstMobileDownloadConfirmDialog(downloadUrl, videoId, videoName, videoItemName);
                } else {
                    startDownload(downloadUrl, videoId, videoName, videoItemName, true);
                }
            } else {
                startDownload(downloadUrl, videoId, videoName, videoItemName, false);
                ToastUtils.showToast(TestApp.getContext(), "已缓存至缓存列表 ,接入wifi时将开始缓存");
            }
        }
    }

    void startDownload(String downloadUrl, String videoId, String videoName, String videoItemName, boolean downloadImmediately) {
        if (downloadImmediately) {
            M3U8Downloader.getInstance().download(downloadUrl, videoId, videoItemName, videoName);
        }
        insertM3U8Task(downloadUrl, videoId, videoName, videoItemName);
    }

    private void insertM3U8Task(String url, String videoId, String videoName, String videoItemName) {
        M3U8DownloadBean downloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(url);
        if (null == downloadBean) {
            M3U8DownloadBean m3U8DownloadBean = new M3U8DownloadBean();
            m3U8DownloadBean.setVideoUrl(url);
            m3U8DownloadBean.setVideoId(videoId);
            m3U8DownloadBean.setVideoName(videoName + videoItemName);
            m3U8DownloadBean.setVideoTotalName(videoName);
            m3U8DownloadBean.setIsDownloaded(false);
            m3U8DownloadBean.setTaskStatus(M3U8TaskState.DEFAULT);
            DBManager.getInstance(TestApp.getContext()).insertM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库已存在该数据");
        }
    }

    @NonNull
    private HashMap<String, String> getQueryHashMap(String videoId) {
        HashMap<String, String> queryMap;

        String userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no");
        String userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no");

        queryMap = new HashMap<>(3);
        queryMap.put("vod_id", videoId);
        queryMap.put("token", userToken);
        queryMap.put("token_id", userTokenId);
        return queryMap;
    }

    @Override
    void setItemSelectedPosition(int selectedPosition) {
        LogUtils.d(TAG, "setItemSelectedPosition == " + selectedPosition);
        mView.getSelectedItemPosition(selectedPosition);
    }

    @Override
    void getSDCardFreeSize() {
        Disposable disposable = mModel.getSDCardFreeSize()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long freeSpace) {
                        LogUtils.d(TAG, "getSDCardFreeSize == " + freeSpace);
                        if (AppConstant.REMIND_SPACE_SIZE > freeSpace) {
                            mView.showCleanCacheDialog();
                        }
                    }
                }, new RxExceptionHandler<Throwable>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(getClass(), "getSDCardFreeSize onError" + throwable.getMessage());

                    }
                }));
        addDisposable(disposable);
    }

    @Override
    void initWeChatApi() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
    }

    @Override
    void share2WeChat(Resources resources, String shareUrl, int targetSession) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject(shareUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = TestApp.getContext().getString(R.string.share_invite_title);
        message.description = TestApp.getContext().getString(R.string.share_invite_description);
        Bitmap logo = BitmapFactory.decodeResource(resources, R.mipmap.ic_logo);
        Bitmap thumbLogo = Bitmap.createScaledBitmap(logo, THUMB_SIZE, THUMB_SIZE, true);
        logo.recycle();
        message.thumbData = WeChatUtil.bmpToByteArray(thumbLogo, true);

        //构造一个 Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = targetSession;
        if (mWxApi == null) {
            mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
        }
        if (mWxApi.isWXAppInstalled()) {
            mWxApi.sendReq(req);
        } else {
            ToastUtils.showToast(TestApp.getContext(), "您还未安装微信");
        }
    }

    @Override
    void getCurrentNetSpeed(final LandLayoutVideo landLayoutVideo) {
        if (null != landLayoutVideo) {
            Disposable disposable = Observable.interval(0, 1, TimeUnit.SECONDS).compose(RxSchedulers.io_main())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) {
                            String netSpeedText = landLayoutVideo.getNetSpeedText();
                            //LogUtils.i(TAG, "getCurrentNetSpeed ==" + netSpeedText);
                            mView.setNetSpeed(netSpeedText);
                        }
                    });
            addDisposable(disposable);
        }
    }

    @Override
    void initBrowserListener() {

    }

    @Override
    void updateDeviceAdapter() {
        Disposable disposable = Observable.timer(1, TimeUnit.SECONDS)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        LogUtils.d(TAG, "updateDeviceAdapter long = " + aLong);
                        mView.updateBrowseAdapter();
                    }
                });
        addDisposable(disposable);
    }

    @Override
    boolean isContains(LelinkServiceInfo selectInfo, LelinkServiceInfo info) {
        try {
            if (info.getUid() != null && selectInfo.getUid() != null && TextUtils.equals(info.getUid(), selectInfo.getUid())) {
                return true;
            } else if (TextUtils.equals(info.getIp(), selectInfo.getIp()) && TextUtils.equals(info.getName(), selectInfo.getName())) {
                return true;
            }
        } catch (Exception e) {
            LeLog.w(TAG, e);
            return false;
        }
        return false;
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 定时关闭
     */
    private void startCloseTimer(int minutes, int closeSeconds) {
        stopCloseTimer();
        //重置显示的文字
        mView.resetTimerCloseText();
        mMinutes = minutes;
        mView.setPlayerCloseTimeStatus(minutes);
        if (minutes == LandLayoutVideo.CLOSE_TIME_NONE || minutes == LandLayoutVideo.CLOSE_TIME_CURRENT_VIDEO) {
            //重置剩余秒数，防止保存错误数据
            mCloseSeconds = 0;
            return;
        }
        if (closeSeconds == 0) {
            mCloseSeconds = minutes * 60;
        } else {
            mCloseSeconds = closeSeconds;
        }
        Log.d(TAG, "mCloseSeconds =" + mCloseSeconds);
        //每隔一秒进行更改播放器中计时文字，当倒计时结束时弹窗提醒是否关闭
        closeTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> {
                    mCloseSeconds -= 1;
                    Log.d(TAG, "mCloseSeconds =" + mCloseSeconds);
                    if (0 < mCloseSeconds) {
                        //设置播放器文字
                        mView.setPlayerCloseTimerText(getTimeString(mCloseSeconds));
                    } else {
                        onTimerClose();
                    }
                });
        addDisposable(closeTimerDisposable);
    }

    /**
     * 定时关闭
     */
    @Override
    void startCloseTimer(int minutes) {
        startCloseTimer(minutes, 0);
    }

    /**
     * 当定时结束，调用此方法，有两个地方，1 倒计时结束 2 设置为播完当前集，由播放器触发
     */
    @Override
    void onTimerClose() {
        //取消计时器
        stopCloseTimer();
        //暂停播放
        mView.pausePlay();
        //显示dialog
        showTimeCloseDialog();
        //还原播放器文字,还原播放器计时状态
        mView.resetTimerCloseText();
    }


    private void showTimeCloseDialog() {
        //弹窗提醒
        mView.showTimerCloseDialog(new TimeCloseDialogFragment.OnButtonClickListener() {
            @Override
            public void onExit() {
                //重置保存的计时，关闭页面
                mMinutes = LandLayoutVideo.CLOSE_TIME_NONE;
                mCloseSeconds = 0;
                mView.finishActivity();
            }

            @Override
            public void onContinue() {
                if (mMinutes == LandLayoutVideo.CLOSE_TIME_CURRENT_VIDEO) {
                    //继续播放下一集
                    mView.playNext();
                } else {
                    //继续播放
                    mView.resumePlay();
                }
                //重置保存的计时
                mMinutes = LandLayoutVideo.CLOSE_TIME_NONE;
                mCloseSeconds = 0;
                //设置播放器的定时关闭为不开启
                mView.setPlayerCloseTimeStatus(mMinutes);
            }
        });
    }

    /**
     * 根据剩余秒数，设置播放器显示的倒计时文字
     *
     * @param seconds 剩余秒数
     * @return 返回xx:xx
     */
    private String getTimeString(int seconds) {
        int second = seconds % 60;
        int minute = seconds / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
    }

    /**
     * 停止关闭页面的定时器
     */
    @Override
    void stopCloseTimer() {
        if (closeTimerDisposable != null) {
            closeTimerDisposable.dispose();
        }
        closeTimerDisposable = null;
    }

    /**
     * 当页面关闭时调用此方法
     */
    @Override
    void onDestroy() {
        //保存倒计时状态
        SpUtils.putInt(TestApp.getContext(), "closeTimeType", mMinutes);
        SpUtils.putInt(TestApp.getContext(), "closeTimeValue", mCloseSeconds);
    }

    /**
     * 页面初始化时，加载关闭倒计时
     */
    @Override
    void initCloseTimer() {
        mMinutes = SpUtils.getInt(TestApp.getContext(), "closeTimeType", LandLayoutVideo.CLOSE_TIME_NONE);
        mCloseSeconds = SpUtils.getInt(TestApp.getContext(), "closeTimeValue", 0);
        Log.d(TAG, "initCloseTimer type = " + mMinutes);
        Log.d(TAG, "initCloseTimer value = " + mCloseSeconds);
        startCloseTimer(mMinutes, mCloseSeconds);
    }

    @Override
    void addPlayCount(String vodId) {
        addVideoInfo(vodId, "1");
    }

    @Override
    void addDownloadCount(String vodId) {
        addVideoInfo(vodId, "2");
    }

    @Override
    void addCollectCount(String vodId) {
        addVideoInfo(vodId, "3");
    }

    @Override
    void addAdInfo(int adType, @Nullable String adId) {
        if (adId == null) {
            return;
        }
        Disposable subscribe = mModel.addAdInfo(adType, adId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResult -> {
                }, new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, throwable.getMessage())));
        addDisposable(subscribe);
    }

    @Override
    void deleteDownloadTask(String videoUrl) {
        Disposable subscribe = Observable.just(videoUrl)
                .map((Function<String, Object>) s -> {
                    M3U8Downloader.getInstance().cancel(videoUrl);
                    DBManager.getInstance(TestApp.getContext()).deleteM3U8TaskByUrl(videoUrl);
                    String m3U8Path = M3U8Downloader.getInstance().getM3U8Path(videoUrl);
                    String m3u8DirPath = m3U8Path.substring(0, m3U8Path.lastIndexOf('/'));
                    FileUtils.deleteDir(m3u8DirPath);
                    return videoUrl;
                }).compose(RxSchedulers.io_main())
                .subscribe(o -> Log.d(TAG, "deleteDownloadTask success"), throwable -> Log.d(TAG, "deleteDownloadTask error," + throwable.getMessage()));
        addDisposable(subscribe);
    }

    /**
     * 统计添加收藏数、下载数、播放数
     *
     * @param type 1播放 2下载 3收藏
     */
    private void addVideoInfo(String vodId, String type) {
        Disposable subscribe = mModel.addVideoInfo(vodId, type)
                .subscribeOn(Schedulers.io())
                .subscribe(baseResult -> {
                }, new RxExceptionHandler<>(throwable -> LogUtils.e(TAG, throwable.getMessage())));
        addDisposable(subscribe);
    }

    public List<PlayerUrlListBean> queryDownloadStatus(List<PlayerUrlListBean> array, String videoId) {
        List<M3U8DownloadBean> m3U8DownloadBeans = DBManager.getInstance(TestApp.getContext())
                .queryM3U8TasksByVideoId(videoId);
        for (PlayerUrlListBean playerUrlListBean : array) {
            playerUrlListBean.setDownloadStatus(AppConstant.M3U8_TASK_DEFAULT);
            for (M3U8DownloadBean bean : m3U8DownloadBeans) {
                if (playerUrlListBean.getVideoUrl().equals(bean.getVideoUrl())) {
                    playerUrlListBean.setDownloadStatus(bean.getTaskStatus());
                    break;
                }
            }
        }
        return array;
    }


    /**
     * @param intervalTime    定时间隔
     * @param landLayoutVideo 播放器
     * @param watchTime       当前已观看的时间
     */
    @Override
    public void watchVideoTimer(LandLayoutVideo landLayoutVideo) {
        long watchPeriod = 1000L;
        int duration = landLayoutVideo.getDuration();
        // 保证仅能有1个 Timer 存在
        if (null != mWatchVideoTimer && !mWatchVideoTimer.isDisposed()) {
            return;
        }
        mWatchVideoTimer = Observable.interval(watchPeriod, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return mWatchTime + watchPeriod;
                    }
                })
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        long currentPos = landLayoutVideo.getGSYVideoManager().getCurrentPosition();
                        LogUtils.d(TAG, "mWatchVideoTimer : " + mWatchTime + " currentPos : " + currentPos + " duration : " + duration);
                        mWatchTime = aLong;
                        //播放时间不足 5秒时, 执行
                        if (5000 > duration - currentPos) {
                            mView.showPlayNextNotice();
                        }
                        if (0 == mWatchTime % 180000) {
                            mView.uploadWatchTime();
                        }
                    }
                });

        addDisposable(mWatchVideoTimer);
    }

    @Override
    void cancelWatchVideoTimer() {
        if (null != mWatchVideoTimer && !mWatchVideoTimer.isDisposed()) {
            mWatchVideoTimer.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHandleDownloadEvent(DownloadEvent event) {
        M3U8Task task = event.getTask();
        String videoId = task.getVideoId();
        //是正在播放的视频的分集
        if (TextUtils.equals(videoId, mView.getVideoId())) {
            PlayerDownloadSelectItemAdapter adapter = mView.getDownloadAdapter();
            List<PlayerUrlListBean> data = adapter.getData();
            PlayerUrlListBean bean = null;
            for (int i = 0; i < data.size(); i++) {
                PlayerUrlListBean temp = data.get(i);
                if (TextUtils.equals(temp.getVideoUrl(), task.getUrl())) {
                    bean = temp;
                    break;
                }
            }
            if (bean == null) {
                return;
            }
            if (event.getType() == DownloadEvent.Type.TYPE_DELETE) {
                bean.setDownloadStatus(M3U8TaskState.DEFAULT);
            } else if (event.getType() == DownloadEvent.Type.TYPE_UPDATE_STATUS) {
                bean.setDownloadStatus(task.getState());
            } else {
                return;
            }
            adapter.notifyDataSetChanged();
        }
    }
}
