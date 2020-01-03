package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class SpecialVideoTopVideoBean(
        @SerializedName("vod_id")
        val vodId: String,
        @SerializedName("big_pic")
        val imageUrl: String,
        @SerializedName("big_name")
        val name: String,
        @SerializedName("big_f_name")
        val content: String
)