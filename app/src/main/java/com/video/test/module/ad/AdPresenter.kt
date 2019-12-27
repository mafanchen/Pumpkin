package com.video.test.module.ad

import android.content.Context
import com.video.test.AppConstant
import com.video.test.R
import com.video.test.network.RxExceptionHandler
import com.video.test.utils.LogUtils
import com.video.test.utils.RxCountdown
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class AdPresenter : AdConstract.Presenter<AdModel>() {

    override fun addAdInfo(adId: String?) {
        if (adId != null) {
            val subscribe = mModel.addAdInfo(AppConstant.AD_TYPE_SPLASH, adId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer { }, RxExceptionHandler<Throwable>(Consumer { e -> LogUtils.e("AdPresenter", e.message) }))
            addDisposable(subscribe)
        }
    }

    override fun countDownSplash(context: Context) {
        val subscribe = RxCountdown.countdown(5).subscribe({ integer ->

            mView.getLayoutSkip()?.isEnabled = false
            mView.getTvSkip()?.text = integer.toString()
        }, {}, {
            mView.getLayoutSkip()?.isEnabled = true
            mView.getTvSkip()?.text = ""
            mView.getTvSkip()?.setBackgroundResource(R.drawable.bg_splash_btn_close)
        })
        addDisposable(subscribe)
    }

    override fun subscribe() {
    }
}