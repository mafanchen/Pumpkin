package com.video.test.module.ad

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.video.test.AppConstant
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.network.RxExceptionHandler
import com.video.test.utils.DownloadUtil
import com.video.test.utils.LogUtils
import com.video.test.utils.RxCountdown
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

class AdPresenter : AdContract.Presenter<AdModel>() {

    val TAG = "AdPresenter"

    override fun addAdInfo(adId: String?) {
        if (adId != null) {
            val subscribe = mModel.addAdInfo(AppConstant.AD_TYPE_SPLASH, adId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer {}, RxExceptionHandler<Throwable>(Consumer { e -> LogUtils.e("AdPresenter", e.message) }))
            addDisposable(subscribe)
        }
    }

    override fun countDownSplash(context: Context) {
        val subscribe = RxCountdown.countdown(5).subscribe({ integer ->
            mView.getLayoutSkip()?.isEnabled = false
            mView.getTvSkip()?.text = context.resources.getString(R.string.splash_skip_count, integer)
        }, {}, {
            mView.getLayoutSkip()?.isEnabled = true
            mView.getTvSkip()?.text = "进入"
            mView.getTvSkip()?.setBackgroundResource(R.drawable.bg_btn_close)
            if (!mView.isSplash()) {
                mView.close()
            }
        })
        addDisposable(subscribe)
    }

    override fun subscribe() {
    }

    /**
     * @param url 图片网络路径
     * @return 根据图片url地址获取保存在本地图片的地址
     */
    private fun getImageLocalPathByUrl(url: String): String {
        return url.substring(url.lastIndexOf("/"), url.length)
    }

    /**
     * 下载并且展示图片
     * 如果图片已经被下载，则直接展示该图片，否则会先下载保存图片并且展示
     */
    override fun saveAndShowImage(picUrl: String, ivAd: ImageView) {
        val adImage = File(DownloadUtil.getImageDirFile(), getImageLocalPathByUrl(picUrl))
        if (adImage.exists()) {
            LogUtils.d(TAG, "获取广告图片缓存成功：${adImage.absolutePath}")
            GlideApp.with(mView as Activity).load(adImage.absolutePath).transition(DrawableTransitionOptions.withCrossFade()).into(ivAd)
        } else {
            GlideApp.with(mView as Activity)
                    .asBitmap()
                    .load(picUrl)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            if (resource != null) {
                                try {
                                    val os = FileOutputStream(adImage)
                                    resource.compress(CompressFormat.PNG, 100, os)
                                    os.close()
                                    LogUtils.d(TAG, "广告图片保存成功：${adImage.absolutePath}")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            return false
                        }

                    })
                    .into(ivAd)
        }
    }
}