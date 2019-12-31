package com.video.test.javabean.event

import android.support.annotation.IntDef
import jaygoo.library.m3u8downloader.bean.M3U8Task

class DownloadEvent(
        @Type
        val type: Int,
        val task: M3U8Task?
) {

    @IntDef(Type.TYPE_DELETE, Type.TYPE_UPDATE_STATUS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type {
        companion object {
            const val TYPE_DELETE = 1
            const val TYPE_UPDATE_STATUS = 2
            const val TYPE_PROGRESS = 3
        }
    }
}