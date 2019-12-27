package com.video.test.javabean.event

data class CollectEvent(
        val isCollect: Boolean,
        val vodId: String?,
        val collectId: String?
)