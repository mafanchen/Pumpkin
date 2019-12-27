package com.video.test.module.swap

import com.video.test.javabean.ActivityGiftBean
import com.video.test.javabean.ShareExchangeListBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class SwapListModel : SwapListContract.Model {

    override fun getShareVip(userToken: String, userTokenId: String, shareId: String): Observable<ActivityGiftBean> {
        return RetrofitHelper.getInstance()
                .getShareVip(userToken, userTokenId, shareId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getShareExchange(userToken: String, userTokenId: String): Observable<ShareExchangeListBean> {
        return RetrofitHelper.getInstance()
                .getShareExchange(userToken, userTokenId)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

}