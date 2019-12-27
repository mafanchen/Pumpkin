package com.video.test.module.swap

import com.video.test.javabean.SwapHistoryBean
import com.video.test.network.ListResult
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class SwapHistoryModel : SwapHistoryContract.Model {
    override fun getHistoryList(page: Int, limit: Int): Observable<ListResult<SwapHistoryBean>> {
        return RetrofitHelper.getInstance()
                .getSwapHistoryList(page, limit)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }
}