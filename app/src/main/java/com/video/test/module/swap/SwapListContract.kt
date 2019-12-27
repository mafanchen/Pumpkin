package com.video.test.module.swap

import com.video.test.framework.BasePresenter
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.ActivityGiftBean
import com.video.test.javabean.GiftBean
import com.video.test.javabean.ShareExchangeListBean
import io.reactivex.Observable

interface SwapListContract {

    interface View : IView {
        fun showLoading()

        fun hideLoading()

        fun setRefresh(refresh: Boolean)

        fun setShareNum(shareNum: Int)

        fun setShareExchange(shareExchange: List<ShareExchangeListBean.ShareExchangeBean>)

        fun showSwapSuccessDialog(bean: GiftBean)
    }

    interface Model : IModel {
        fun getShareExchange(userToken: String, userTokenId: String): Observable<ShareExchangeListBean>

        fun getShareVip(userToken: String, userTokenId: String, shareId: String): Observable<ActivityGiftBean>
    }

    abstract class Presenter<M : Model> : BasePresenter<M, View>() {

        /**
         * 获取兑换会员的类型列表
         */
        abstract fun getShareExchange()

        /**
         * 兑换会员
         */
        abstract fun getShareVip(shareId: String, shareSetNum: Int)

    }
}