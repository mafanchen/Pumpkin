package com.video.test.javabean

/**
 * 下载页面外部列表中展示正在下载的视频的bean
 */
class DownloadingBean : DownloadBean() {
    /**
     * 下载速度
     */
    var downloadSpeed: String? = null
    /**
     * 下载进度
     */
    var progress = 0
    /**
     * 是否在下载
     */
    var isDownloading = false

    init {
        videoId = "_downloadingVideo"
    }
}