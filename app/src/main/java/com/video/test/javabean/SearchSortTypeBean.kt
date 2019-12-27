package com.video.test.javabean

import com.google.gson.annotations.SerializedName

data class SearchSortTypeBean(
        @SerializedName("order_key")
        val key: String,
        @SerializedName("order_val")
        val value: String
)