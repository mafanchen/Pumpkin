package com.video.test.module.feedback

import com.video.test.network.RxExceptionHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import me.drakeet.multitype.Items

class FeedbackHistoryPresenter : FeedbackHistoryContract.Presenter<FeedbackHistoryModel>() {
    override fun subscribe() {
    }

    override fun getFeedbacks() {
        val subscribe = mModel.getFeedbacks()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.showRefreshLayout() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    mView.hideRefreshLayout(true)
                    val items = Items(it)
                    mView.setData(items)
                }, RxExceptionHandler {
                    mView.hideRefreshLayout(false)
                    mView.showNetworkErrorView()
                })
        addDisposable(subscribe)
    }
}