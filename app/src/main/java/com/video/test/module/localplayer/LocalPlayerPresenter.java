package com.video.test.module.localplayer;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView;
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
import com.video.test.javabean.PlayerUrlListBean;
import com.video.test.sp.SpUtils;
import com.video.test.ui.listener.VideoFunctionListener;
import com.video.test.ui.widget.LocalLandVideoPlayer;
import com.video.test.utils.LogUtils;
import com.video.test.utils.ToastUtils;
import com.video.test.utils.WeChatUtil;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class LocalPlayerPresenter extends LocalPlayerContract.Presenter<LocalPlayerModel> {
    private static final String TAG = "LocalPlayerPresenter";
    private IWXAPI mWxApi;
    private static final int THUMB_SIZE = 150;

    @Override
    public void subscribe() {
    }


    @Override
    void initVideoPlayer(LocalLandVideoPlayer videoPlayer) {
//        /*可拖拽调控*/
//        videoPlayer.setIsTouchWiget(true);
//        /*显示返回按键*/
//        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
//        /*增加Title*/
//        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
//        /*设置完成后开始播放*/
//        videoPlayer.startPlayLogic();
        /*如果影音不同步,选择丢弃帧,保持影音同步*/
        initIJKPlayer();

        LogUtils.d(TAG, "initVideoPlayer");
        GSYVideoOptionBuilder videoOption = new GSYVideoOptionBuilder();
        videoOption
                .setIsTouchWiget(true)
                .setNeedLockFull(true)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(false)
                .setCacheWithPlay(false)
                .setDismissControlTime(5000)
                .setSeekRatio(8)
                .build(videoPlayer);
        addPlayerCallBack(videoPlayer);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);

    }

    private void addPlayerCallBack(LocalLandVideoPlayer videoPlayer) {
        videoPlayer.getStartButton().setOnClickListener(v -> {
            switch (videoPlayer.getCurrentState()) {
                case GSYVideoView.CURRENT_STATE_NORMAL:
                case GSYVideoView.CURRENT_STATE_AUTO_COMPLETE:
                case GSYVideoView.CURRENT_STATE_ERROR:
                    break;
                case GSYVideoView.CURRENT_STATE_PLAYING:
                    videoPlayer.onVideoPause();
                    break;
                case GSYVideoPlayer.CURRENT_STATE_PAUSE:
                    videoPlayer.onVideoResume(true);
                    break;
                default:
                    break;
            }
        });
        videoPlayer.setVideoAllCallBack(new VideoAllCallBack() {
            @Override
            public void onStartPrepared(String url, Object... objects) {

            }

            @Override
            public void onPrepared(String url, Object... objects) {

            }

            @Override
            public void onClickStartIcon(String url, Object... objects) {

            }

            @Override
            public void onClickStartError(String url, Object... objects) {

            }

            @Override
            public void onClickStop(String url, Object... objects) {

            }

            @Override
            public void onClickStopFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickResume(String url, Object... objects) {

            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {

            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {

            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                DBManager.getInstance(TestApp.getContext()).saveLocalHistory(mView.getVideoUrl(), 1, 0);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {

            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {

            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {

            }

            @Override
            public void onPlayError(String url, Object... objects) {

            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {

            }

            @Override
            public void onClickBlank(String url, Object... objects) {

            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {

            }
        });

        videoPlayer.setVideoFunctionListener(new VideoFunctionListener() {

            @Override
            public void onFeedbackClick() {

            }

            @Override
            public void onCastClick() {

            }

            @Override
            public void onSelectVideo(int position, PlayerUrlListBean bean) {

            }

            @Override
            public void onShareWX() {
                LogUtils.d(TAG, "videoPlayer share to wx");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                share2WeChat(TestApp.getContext().getResources(), userShareUrl, AppConstant.WX_SCENE_SESSION);
            }

            @Override
            public void onShareFriends() {
                LogUtils.d(TAG, "videoPlayer share to 朋友圈");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                share2WeChat(TestApp.getContext().getResources(), userShareUrl, AppConstant.WX_SCENE_TIMELINE);
            }

            @Override
            public void onCopyUrl() {
                LogUtils.d(TAG, "videoPlayer share copy url");
                String userShareUrl = SpUtils.getString(TestApp.getContext(), "userShareUrl", BuildConfig.OFFICAL_WEBSITE);
                ClipboardManager manager = (ClipboardManager) TestApp.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String shareText = TestApp.getContext().getString(R.string.activity_share_text_share_url, userShareUrl);
                manager.setPrimaryClip(ClipData.newPlainText(null, shareText));
                ToastUtils.showToast(TestApp.getContext(), "已将分享链接复制到剪切板");
            }

            @Override
            public void onVideoCollect(boolean isCollected) {

            }

            @Override
            public void onTimerClose(int minutes) {

            }

            @Override
            public void onTimerCloseByCurrentLength() {

            }

            @Override
            public void onCaptureImage(File file, File cover, boolean isGif) {
                LogUtils.d(TAG, "videoPlayer capture image");
                //显示分享图片的dialog
                mView.showShareImageDialog(file, cover, isGif);
            }

            @Override
            public void startFullScreen() {

            }

            @Override
            public void resolveToNormal() {

            }

            @Override
            public void closeActivity() {

            }

            @Override
            public void addAdInfo(int adType, @Nullable String adId) {

            }

            @Override
            public boolean hasStoragePermission() {
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(TestApp.getContext(), perms)) {
                    return true;
                }
                EasyPermissions.requestPermissions(((LocalPlayerActivity) mView), TestApp.getContext().getString(R.string.dialog_perm_storage), AppConstant.PERSSION_READ_AND_WRITE_EXTERNAL_STORAGE, perms);
                return false;
            }

        });

    }

    @Override
    void initVideoInfo(LocalLandVideoPlayer videoPlayer, String videoName, String videoUrl) {

        LogUtils.i(TAG, "url : " + videoUrl);
        videoPlayer.setUp(videoUrl, false, videoName);
        videoPlayer.startPlayLogic();
//        /*显示返回按键*/
//        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
//        /*增加Title*/
//        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
//        /*设置拖动比例*/
//        videoPlayer.setSeekRatio(6);
//        /*锁定屏幕按键功能*/
//        videoPlayer.setNeedLockFull(true);
//        /*全屏锁定屏幕*/
//        videoPlayer.setLockLand(false);
//        /*根据尺寸,自动选择全屏*/
//        videoPlayer.setAutoFullWithSize(true);
//        /*如果影音不同步,选择丢弃帧,保持影音同步*/
//        initIJKPlayer();
        /*设置完成后开始播放*/

    }

    /**
     *
     */
    private void initIJKPlayer() {
        ArrayList<VideoOptionModel> list = new ArrayList<>();
        //降低视频的倍数,使影音同步,但会导致丢帧
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 6));
        //解决切换进度导致无限加载
//        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1));
        //解决miui10 android9以上 倍速播放无效的问题
        list.add(new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1));
        GSYVideoManager.instance().setOptionModelList(list);
    }

    @Override
    void initWeChatApi() {
        mWxApi = WXAPIFactory.createWXAPI(TestApp.getContext(), BuildConfig.WECHAT_APP_ID);
    }

    private void share2WeChat(Resources resources, String shareUrl, int targetSession) {
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

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 保存截图成功，通知系统相册刷新
     */
    void notifyUpdateImages(File image) {
        if (image == null) {
            return;
        }
        LogUtils.d(TAG, "通知系统刷新相册" + image.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(image);
        intent.setData(uri);
        TestApp.getContext().sendBroadcast(intent);
//        MediaScannerConnection.scanFile(this
//                , new String[]{image.getAbsolutePath()}
//                , new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(String path, Uri uri) {
//                        LogUtils.d(TAG, "通知系统刷新相册" + path);
//                    }
//                });
    }
}
