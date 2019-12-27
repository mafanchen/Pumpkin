package com.video.test.module.localplayer;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.video.test.R;
import com.video.test.ui.base.BaseActivity;
import com.video.test.ui.widget.LocalLandVideoPlayer;
import com.video.test.ui.widget.ShareImageDialogFragment;
import com.video.test.utils.LogUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.io.File;

import butterknife.BindView;
import jaygoo.library.m3u8downloader.server.M3U8HttpServer;

/**
 * @author Enoch Created on 2018/6/27.
 */
@Route(path = "/localPlayer/activity")
public class LocalPlayerActivity extends BaseActivity<LocalPlayerPresenter> implements LocalPlayerContract.View {

    private static final String TAG = "LocalPlayerActivity";

    @Autowired
    String localUrl;
    @Autowired
    String localName;
    @BindView(R.id.player_local_player)
    LocalLandVideoPlayer mVideoPlayer;
    M3U8HttpServer mHttpServer = new M3U8HttpServer();

    @Override
    protected void setStatueBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            ViewGroup contentView = this.findViewById(Window.ID_ANDROID_CONTENT);
//            View childView = contentView.getChildAt(0);
//            if (null != childView) {
//                ViewCompat.setFitsSystemWindows(childView, true);
//            }
        }
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        ARouter.getInstance().inject(this);
        mHttpServer.execute();
    }

    @Override
    protected int getContextViewId() {
        return R.layout.bean_activity_local_player;
    }

    @Override
    protected void initData() {
        LogUtils.d(TAG, "initData localName == " + localName + " localUrl == " + localUrl);
        mPresenter.initVideoPlayer(mVideoPlayer);
        mPresenter.initVideoInfo(mVideoPlayer, localName, mHttpServer.createLocalHttpUrl(localUrl));
    }


    @Override
    protected void initView() {
        mVideoPlayer.getBackButton().setOnClickListener(v -> {
            LogUtils.d(TAG, "getBackButton Click");
            onBackPressed();
        });
    }

    @Override
    public void showShareImageDialog(File file, File cover, boolean isGif) {
        String imagePath = file == null ? null : file.getAbsolutePath();
        String coverPth = cover == null ? null : cover.getAbsolutePath();
        ShareImageDialogFragment fragment = ShareImageDialogFragment.newInstance(imagePath, coverPth, isGif);
        fragment.setOnDismissListener(() -> mVideoPlayer.onVideoResume());
        //通知系统相册刷新
        fragment.setSaveImageListener((file1) -> mPresenter.notifyUpdateImages(file1));
        fragment.show(getSupportFragmentManager(), "ShareImageDialogFragment");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.onVideoResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayer.onVideoPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoPlayer != null) {
            mVideoPlayer.setVideoAllCallBack(null);
        }
        //释放所有
        GSYVideoManager.releaseAllVideos();
        if (null != mHttpServer) {
            mHttpServer.finish();
        }
    }

}


