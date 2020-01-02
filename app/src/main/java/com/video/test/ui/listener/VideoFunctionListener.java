package com.video.test.ui.listener;

import com.video.test.javabean.PlayerUrlListBean;

import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Enoch Created on 2018/8/22.
 */
public interface VideoFunctionListener {

    /**
     * 反馈按键
     */
    void onFeedbackClick();

    /**
     * 电视投屏按钮
     */
    void onCastClick();

    /**
     * 选集
     */
    void onSelectVideo(int position, PlayerUrlListBean bean);

    /**
     * 分享到微信
     */
    void onShareWX();

    /**
     * 分享到朋友圈
     */
    void onShareFriends();

    /**
     * 复制分享链接
     */
    void onCopyUrl();

    /**
     * 收藏
     */
    void onVideoCollect(boolean isCollected);

    /**
     * 定时播放
     *
     * @param minutes 0:不开启 -1:播完当前集 其他:对应分钟数
     */
    void onTimerClose(int minutes);

    /**
     * 当选中播完当前关闭视频，并且当前视频播放完成时执行的回调，会关闭页面
     */
    void onTimerCloseByCurrentLength();

    /**
     * 截屏或录制gif
     *
     * @param file
     * @param cover
     * @param isGif
     */
    void onCaptureImage(File file, File cover, boolean isGif);

    void startFullScreen();

    void resolveToNormal();

    void closeActivity();

    void addAdInfo(int adType, @Nullable String adId);

    /**
     * 是否有存储权限权限
     */
    boolean hasStoragePermission();


    /**
     * @param btnType 点击类型  1 快进 2快退
     */

    void clickBackOrForward(int btnType);

}
