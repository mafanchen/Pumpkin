package com.video.test.module.videotype

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.HomePageVideoListBean
import com.video.test.network.BaseResult
import io.reactivex.Observable
import me.drakeet.multitype.Items


/**
 * @author : AhhhhDong
 * @date : 2019/4/9 16:17
 */
interface VideoTypeListContract {
    interface View : IView {

        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun setVideoData(items: Items)

        fun showNetworkErrorView()
    }

    interface Model : IModel {

        fun getHomepageVideoList(pid: Int): Observable<HomePageVideoListBean>

        fun addAdInfo(adType: Int, adId: String): Observable<BaseResult<Any>>
    }

    abstract class Presenter<M : Model, V : View> : BasePresenter<M, V>() {

        abstract fun getHomePageVideoList(pid: Int)

        abstract fun addAdInfo(adType: Int, adId: String)
    }
}