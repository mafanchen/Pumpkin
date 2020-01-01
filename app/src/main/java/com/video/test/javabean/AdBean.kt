package com.video.test.javabean

import com.google.gson.annotations.SerializedName

open class AdBean(
        @SerializedName("is_ad")
        val isAd: Boolean,
        @SerializedName("ad_info")
        val adInfo: AdInfoBean
)