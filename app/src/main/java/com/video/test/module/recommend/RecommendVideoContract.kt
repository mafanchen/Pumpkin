package com.video.test.module.recommend

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.VideoRecommendBean
import io.reactivex.Observable
import me.drakeet.multitype.Items

interface RecommendVideoContract {

    interface View : IView {
        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun setVideoData(items: Items)

        fun showNetworkErrorView()
    }

    interface Model : IModel {
        fun getRecommendVideo(parentId: Int): Observable<List<VideoRecommendBean>>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun getRecommendVideo(parentId: Int)
    }

}