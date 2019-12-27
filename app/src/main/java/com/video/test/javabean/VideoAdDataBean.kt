package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class VideoAdDataBean(
        @SerializedName("ptData")
        val headAd: VideoAdBean? = null,
        @SerializedName("cardData")
        val playAd: VideoAdBean? = null,
        @SerializedName("ztData")
        val pauseAd: VideoAdBean? = null
)