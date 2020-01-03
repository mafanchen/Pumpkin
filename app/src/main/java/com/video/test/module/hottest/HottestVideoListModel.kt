package com.video.test.module.hottest

import com.video.test.javabean.HottestVideoBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class HottestVideoListModel : HottestVideoListContract.Model {
    override fun getHottestVideo(showId: String, showPid: String?): Observable<List<HottestVideoBean>> =
            RetrofitHelper.getInstance()
                    .getHottestVideos(showId, showPid)
                    .compose(RxSchedulers.io_main())
                    .compose(RxSchedulers.handleResult())
}