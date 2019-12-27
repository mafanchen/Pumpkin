package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class FeedbackTypeBean(
        val id: String,
        @SerializedName("tag_name")
        val name: String
)