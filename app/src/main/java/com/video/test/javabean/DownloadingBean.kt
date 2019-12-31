package com.video.test.javabean

/**
 * 下载页面外部列表中展示正在下载的视频的bean
 */
class DownloadingBean : DownloadBean() {
    /**
     * 下载速度
     */
    var downloadSpeed: Long = 0
    /**
     * 下载进度
     */
    var progress = 0f
    /**
     * 是否在下载
     */
    var isDownloading = false

}