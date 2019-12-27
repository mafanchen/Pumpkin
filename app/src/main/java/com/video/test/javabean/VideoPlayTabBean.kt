package com.video.test.javabean

import com.flyco.tablayout.listener.CustomTabEntity

class VideoPlayTabBean(
        val title: String,
        val startPosition: Int,
        val endPosition: Int,
        val data: List<PlayerUrlListBean>
) : CustomTabEntity {

    override fun getTabUnselectedIcon(): Int = 0

    override fun getTabSelectedIcon(): Int = 0

    override fun getTabTitle(): String = title
}