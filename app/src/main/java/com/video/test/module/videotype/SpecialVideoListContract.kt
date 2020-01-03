package com.video.test.module.videotype

import com.video.test.javabean.SpecialVideoColumnBean
import com.video.test.javabean.SpecialVideoTopVideoBean

interface SpecialVideoListContract {

    interface View : VideoTypeListContract.View {
        fun setTopVideo(topVideo: SpecialVideoTopVideoBean?)
        fun setVideoColumn(columnBean: SpecialVideoColumnBean?)
    }
}