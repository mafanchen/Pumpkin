package com.video.test.module.recommend

import com.video.test.network.RxExceptionHandler
import io.reactivex.functions.Consumer
import me.drakeet.multitype.Items

class RecommendVideoPresenter : RecommendVideoContract.Presenter<RecommendVideoModel>() {
    override fun subscribe() {
    }

    override fun getRecommendVideo(parentId: Int) {
        val subscribe = mModel.getRecommendVideo(parentId)
                .subscribe(Consumer { list ->
                    mView.hideRefreshLayout(true)
                    mView.setVideoData(Items(list))
                }, RxExceptionHandler {
                    mView.hideRefreshLayout(false)
                    mView.showNetworkErrorView()
                })
        addDisposable(subscribe)
    }
}