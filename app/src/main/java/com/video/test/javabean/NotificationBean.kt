package com.video.test.javabean

import com.google.gson.annotations.SerializedName
import com.video.test.javabean.base.IPageJumpBean

data class NotificationBean(
        @JvmField
        @SerializedName("vod_id")
        val vodId: String? = null,
        @JvmField
        val type: String? = null,
        @JvmField
        @SerializedName("web_url")
        val webUrl: String? = null,
        @SerializedName("android_router")
        val router: String? = null,
        @SerializedName("zt_tag")
        val ztTag: String? = null,
        @SerializedName("zt_type")
        val ztType: String? = null,
        @SerializedName("zt_pid")
        val ztPid: Int? = null
) : IPageJumpBean {
    override fun getType(): String? {
        return type
    }

    override fun getAndroidRouter(): String? {
        return router
    }

    override fun getWebUrl(): String? {
        return webUrl
    }

    override fun getVodId(): String? {
        return vodId
    }

    override fun getTopicRouter(): BannerTopicBean {
        val bean = BannerTopicBean()
        bean.zt_pid = ztPid ?: 0
        bean.zt_tag = ztTag
        bean.zt_type = ztType
        return bean
    }

    override fun getTargetName(): String {
        return ""
    }

}