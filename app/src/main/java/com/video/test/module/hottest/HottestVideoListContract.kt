package com.video.test.module.hottest

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.HottestVideoBean
import io.reactivex.Observable
import me.drakeet.multitype.Items

interface HottestVideoListContract {
    interface View : IView {
        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun setVideoData(items: Items)

        fun showNetworkErrorView()
    }

    interface Model : IModel {
        fun getHottestVideo(showId: String): Observable<List<HottestVideoBean>>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun getHottestVideo(showId: String)
    }
}