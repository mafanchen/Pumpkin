package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class SwapHistoryBean(
        val id: String? = null,
        @SerializedName("share_type")
        val type: String? = null,
        @SerializedName("share_title")
        val title: String? = null,
        @SerializedName("order_cont")
        val content: String? = null,
        @SerializedName("order_pic")
        val image: String? = null,
        @SerializedName("order_num")
        val num: String? = null,
        @SerializedName("add_time")
        val date: String? = null,
        @SerializedName("be_user_id")
        val userId: String? = null,
        @SerializedName("share_id")
        val shareId: String? = null
) {
    companion object {
        const val TYPE_INVITE = 1
        const val TYPE_SWAP = 2
    }
}