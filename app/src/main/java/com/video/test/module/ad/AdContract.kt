package com.video.test.module.ad

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView

interface AdContract {
    interface View : IView {
        fun getTvSkip(): TextView?

        fun getLayoutSkip(): FrameLayout?

        fun isSplash(): Boolean

        fun close()
    }

    interface Model : IModel

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun countDownSplash(context: Context, showTime: Int)

        abstract fun addAdInfo(adId: String?)

        abstract fun saveAndShowImage(picUrl: String, ivAd: ImageView)
    }
}