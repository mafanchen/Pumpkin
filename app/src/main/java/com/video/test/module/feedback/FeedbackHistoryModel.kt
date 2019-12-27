package com.video.test.module.feedback

import com.video.test.javabean.FeedbackBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class FeedbackHistoryModel : FeedbackHistoryContract.Model {
    override fun getFeedbacks(): Observable<List<FeedbackBean>> {
        return RetrofitHelper.getInstance()
                .getFeedbacks()
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }
}