package com.video.test.module.simplevideo

import com.video.test.javabean.HomePageVideoListBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable

class SimpleVideoModel : SimpleVideoContract.Model {

    override fun getHomePageVideoList(): Observable<HomePageVideoListBean> {
        return RetrofitHelper
                .getInstance()
                .simpleHomePage
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }
}