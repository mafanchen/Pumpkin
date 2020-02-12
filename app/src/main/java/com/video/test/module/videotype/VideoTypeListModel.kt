package com.video.test.module.videotype

import com.video.test.javabean.BannerAndNoticeListBean
import com.video.test.javabean.HomePageVideoListBean
import com.video.test.network.RetrofitHelper
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable


/**
 * @author : AhhhhDong
 * @date : 2019/4/9 16:53
 */
open class VideoTypeListModel : VideoTypeListContract.Model {

    override fun getHomepageVideoList(pid: Int): Observable<HomePageVideoListBean> {
        return RetrofitHelper.getInstance()
                .getHomepage(pid)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

    override fun getBannerAndNotice(pid: Int): Observable<BannerAndNoticeListBean> {
        return RetrofitHelper.getInstance()
                .getBannerAndNotice(pid)
                .compose(RxSchedulers.handleResult())
                .compose(RxSchedulers.io_main())
    }

}