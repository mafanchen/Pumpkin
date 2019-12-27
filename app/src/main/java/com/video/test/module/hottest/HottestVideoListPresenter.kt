package com.video.test.module.hottest

import com.video.test.network.RxExceptionHandler
import io.reactivex.functions.Consumer
import me.drakeet.multitype.Items

class HottestVideoListPresenter : HottestVideoListContract.Presenter<HottestVideoListModel>() {
    override fun subscribe() {
    }

    override fun getHottestVideo(showId: String) {
        val subscribe = mModel.getHottestVideo(showId)
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