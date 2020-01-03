package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class SpecialVideoColumnBean(
        @SerializedName("module_name")
        val name: String,
        @SerializedName("type_pic")
        val image: String,
        @SerializedName("list")
        val list: List<VideoBean>
)