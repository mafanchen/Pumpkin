package com.video.test.module.simplevideo

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.HomePageVideoListBean
import io.reactivex.Observable
import me.drakeet.multitype.Items

interface SimpleVideoContract {

    interface View : IView {
        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun setVideoData(items: Items)
    }

    interface Model : IModel {
        fun getHomePageVideoList(): Observable<HomePageVideoListBean>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun getHomePageVideoList()
    }
}