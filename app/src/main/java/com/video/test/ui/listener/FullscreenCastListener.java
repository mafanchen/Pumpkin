package com.video.test.ui.listener;

import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

/**
 * 全屏播放时的投屏回调
 *
 * @author : AhhhhDong
 * @date : 2019/5/9 16:27
 */
public interface FullscreenCastListener {

    /**
     * 退出横屏
     */
    void onCastBackPressed();

    /**
     * 获取设备列表,如果是vip则返回true，播放器会弹出列表
     */
    boolean onGetDeviceList();

    /**
     * 退出投屏
     */
    void onExitCast();

    /**
     * 切换设备
     */
    void onSwitchDevice();

    /**
     * 开始播放
     */
    void onStartAndPauseCastPlay();

    /**
     * 调整进度
     *
     * @param progress
     */
    void onSeekProgress(int progress);

    /**
     * 增大音量
     */
    void onIncreaseVolume();

    /**
     * 减少音量
     */
    void onReduceVolume();

    /**
     * 选中了播放设备
     */
    void onChooseDevice(LelinkServiceInfo info);

    /**
     * 没有选择设备，隐藏了设备列表l
     */
    void onCancelChooseDevice();
}
