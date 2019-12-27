package com.video.test.module.swap

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.SwapHistoryBean
import com.video.test.network.ListResult
import io.reactivex.Observable

interface SwapHistoryContract {
    interface View : IView {

        fun showRefreshLayout()

        fun hideRefreshLayout(isSuccess: Boolean)

        fun finishLoadMoreWithNoMoreData()
        fun loadMoreComplete()
        fun setData(list: List<SwapHistoryBean>)
        fun addData(list: List<SwapHistoryBean>)
    }

    interface Model : IModel {
        fun getHistoryList(page: Int, limit: Int): Observable<ListResult<SwapHistoryBean>>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {
        abstract fun getHistoryList(page: Int, limit: Int)
        abstract fun getMore()
        abstract fun refresh()
    }
}