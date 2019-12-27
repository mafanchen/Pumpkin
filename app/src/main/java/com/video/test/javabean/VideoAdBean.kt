package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class VideoAdBean(
        var id: String? = null,
        @SerializedName("play_ad_type")
        var adType: String? = null,
        @SerializedName("play_ad_location")
        var location: String? = null,
        @SerializedName("play_ad_show")
        var showType: String? = null,
        @SerializedName("play_ad_title")
        var title: String? = null,
        @SerializedName("play_ad_url")
        var adUrl: String? = null,
        @SerializedName("play_ad_web")
        var webUrl: String? = null,
        @SerializedName("ad_time")
        var adTime: List<AdTime>? = null) {

    companion object {
        const val AD_TYPE_VIDEO = 1
        const val AD_TYPE_IMAGE = 2
        const val AD_LOCATION_HEAD = 1
        const val AD_LOCATION_PLAYING = 2
        const val AD_LOCATION_PAUSE = 3
        const val AD_SHOW_TYPE_APP = 1
        const val AD_SHOW_TYPE_BROWSER = 2

    }

    data class AdTime(
            var begin: String,
            var end: String
    )
}