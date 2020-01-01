package com.video.test.javabean.event

import android.support.annotation.IntDef

class NetworkChangeEvent(
        @NetworkType
        val type: Int
) {
    @IntDef(NetworkType.NONE, NetworkType.WIFI, NetworkType.MOBILE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class NetworkType {
        companion object {
            const val NONE = 0
            const val WIFI = 1
            const val MOBILE = 2
        }
    }
}