package com.video.test.javabean

import android.text.TextUtils

open class DownloadBean {
    /**
     * 视频id
     */
    var videoId: String? = null
    /**
     * 视频名称
     */
    var videoName: String? = null
    /**
     * 包含的下载的任务
     */
    var tasks: List<M3U8DownloadBean>? = null

    /**
     * 编辑模式中是否被选中
     */
    var selected = false

    override fun equals(other: Any?): Boolean {
        return if (other is DownloadBean) {
            TextUtils.equals(videoId, other.videoId)
        } else {
            super.equals(other)
        }
    }
}