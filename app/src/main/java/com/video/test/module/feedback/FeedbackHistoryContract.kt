package com.video.test.module.feedback

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.FeedbackBean
import io.reactivex.Observable
import me.drakeet.multitype.Items

interface FeedbackHistoryContract {
    interface View : IView {
        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun setData(items: Items)
        fun showNetworkErrorView()
    }

    interface Model : IModel {
        fun getFeedbacks(): Observable<List<FeedbackBean>>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        internal abstract fun getFeedbacks()
    }
}