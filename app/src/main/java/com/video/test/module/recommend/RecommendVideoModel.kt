package com.video.test.module.recommend

import com.video.test.javabean.VideoRecommendBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class RecommendVideoModel : RecommendVideoContract.Model {
    override fun getRecommendVideo(parentId: Int): Observable<List<VideoRecommendBean>> {
        return RetrofitHelper.getInstance()
                .getRecommendVideo(parentId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }
}