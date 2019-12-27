package com.video.test.module.ad

import com.video.test.network.BaseResult
import com.video.test.network.RetrofitHelper
import io.reactivex.Observable

class AdModel : AdConstract.Model {
    override fun addAdInfo(adType: Int, adId: String): Observable<BaseResult<Any>> {
        return RetrofitHelper.getInstance()
                .addAdInfo(adType, adId)
    }
}