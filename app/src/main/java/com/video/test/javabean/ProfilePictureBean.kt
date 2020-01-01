package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class ProfilePictureBean(
        @SerializedName("id")
        val picId: String,
        @SerializedName("user_pic")
        val picUrl: String
)