package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class FeedbackBean(
        val id: String,
        @SerializedName("user_id")
        val userId: String,
        //反馈类型
        @SerializedName("tag_name")
        val tagName: String,
        //反馈时间
        @SerializedName("add_time")
        val addTime: String,
        //反馈内容
        @SerializedName("feed_cont")
        val feedContent: String,
        //回复内容
        @SerializedName("feed_back")
        val replyContent: String,
        //回复时间
        @SerializedName("save_time")
        val replyTime: String

)