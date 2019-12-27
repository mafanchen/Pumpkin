package com.video.test.module.swap

import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.javabean.GiftBean
import com.video.test.javabean.ShareExchangeListBean
import com.video.test.javabean.event.SwapEvent
import com.video.test.sp.SpUtils
import com.video.test.utils.LogUtils
import com.video.test.utils.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class SwapListPresenter : SwapListContract.Presenter<SwapListModel>() {

    private val TAG = "SwapListPresenter"

    override fun subscribe() {
    }


    override fun getShareVip(shareId: String, shareSetNum: Int) {
        val userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no")
        val userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no")
        val subscribe = mModel.getShareVip(userToken, userTokenId, shareId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.showLoading() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ activityGiftBean ->
                    mView.hideLoading()
                    getShareVipSuccess(activityGiftBean.activity)
                }, { throwable ->
                    mView.hideLoading()
                    LogUtils.e(TAG, "getShareVip Error == " + throwable.message)
                    ToastUtils.showLongToast(TestApp.getContext(), throwable.message)
                })
        addDisposable(subscribe)
    }

    override fun getShareExchange() {
        val userToken = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN, "no")
        val userTokenId = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_ID, "no")
        val disposable = mModel.getShareExchange(userToken, userTokenId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.setRefresh(true) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ shareExchangeListBean ->
                    mView.setRefresh(false)
                    getShareExchangeSuccess(shareExchangeListBean)
                }, { throwable ->
                    mView.setRefresh(false)
                    LogUtils.e(TAG, "getShareExchange Error == " + throwable.message)
                    ToastUtils.showLongToast(TestApp.getContext(), throwable.message)
                })
        addDisposable(disposable)
    }

    private fun getShareExchangeSuccess(bean: ShareExchangeListBean) {
        val shareNum = bean.shareNum
        //通知顶部外部activity的顶部刷新
        EventBus.getDefault().post(SwapEvent(shareNum))
        mView.setShareNum(shareNum)
        if (bean.shareExchange != null) {
            mView.setShareExchange(bean.shareExchange)
        }
    }

    private fun getShareVipSuccess(bean: GiftBean) {
        mView.showSwapSuccessDialog(bean)
        getShareExchange()
    }


}