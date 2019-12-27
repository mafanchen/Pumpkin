package com.video.test.module.ad

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.network.BaseResult
import io.reactivex.Observable

interface AdConstract {
    interface View : IView {
        fun getTvSkip(): TextView?

        fun getLayoutSkip(): FrameLayout?
    }

    interface Model : IModel {
        fun addAdInfo(adType: Int, adId: String): Observable<BaseResult<Any>>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun countDownSplash(context: Context)

        abstract fun addAdInfo(adId: String?)
    }
}