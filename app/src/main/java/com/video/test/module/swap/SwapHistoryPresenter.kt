package com.video.test.module.swap

import com.video.test.utils.LogUtils
import com.video.test.utils.ToastUtils

class SwapHistoryPresenter : SwapHistoryContract.Presenter<SwapHistoryModel>() {

    private var mPage = 1
    private val limit = 20

    override fun getMore() {
        getHistoryList(mPage + 1, limit)
    }

    override fun refresh() {
        mPage = 1
        getHistoryList(mPage, limit)
    }

    private val TAG = "SwapHistoryPresenter"

    override fun subscribe() {
    }

    override fun getHistoryList(page: Int, limit: Int) {
        val subscribe = mModel.getHistoryList(page, limit)
                .subscribe({ result ->
                    if (page == 1) {
                        mView.hideRefreshLayout(true)
                        mView.setData(result.list)
                    } else {
                        if (result.listCount == 0) {
                            mView.finishLoadMoreWithNoMoreData()
                        } else {
                            mView.addData(result.list)
                            mView.loadMoreComplete()
                        }
                    }
                    mPage = page
                }, { throwable ->
                    mView.hideRefreshLayout(false)
                    mView.loadMoreComplete()
                    LogUtils.e(TAG, "getHistoryList Error == " + throwable.message)
                    ToastUtils.showLongToast(throwable.message)
                })
        addDisposable(subscribe)
    }
}