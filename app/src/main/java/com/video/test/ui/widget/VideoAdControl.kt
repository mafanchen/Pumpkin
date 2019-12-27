package com.video.test.ui.widget

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.javabean.VideoAdBean
import com.video.test.utils.IntentUtils
import com.video.test.utils.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class VideoAdControl {

    /**
     * 默认广告5秒关闭
     */
    private val mHeadImageAdLength = 5
    private val mPauseAdLength = 3

    var isVip: Boolean = false

    var mPlayer: AdVideoPlayer? = null

    /**
     * 片头广告的时间，用于播放片头广告后，暂停，继续播放
     */
    private var mHeadAdTimeLog: Int = 0
    /**
     * 是否播放页面暂停（回到桌面)
     */
    var isActivityPause: Boolean = false

    /**
     * 片头的广告
     */
    private var mHeadAd: VideoAdBean? = null
    /**
     * 播放时的广告
     */
    private var mPlayAd: VideoAdBean? = null
    /**
     * 暂停时的广告
     */
    private var mPauseAd: VideoAdBean? = null

    private var mHeadAdTimer: Disposable? = null
    /**
     * 片头图片广告的定时器
     */
    private var mHeadImageAdTimer: Disposable? = null
    /**
     * 片头视频广告的定时器
     */
    private var mHeadVideoAdTimer: Disposable? = null

    private var mPlayAdTimer: Disposable? = null

    private var mPauseAdTimer: Disposable? = null

    /**
     * 用来纪录展示过的卡片广告
     */
    private val mShowedPlayAdList: ArrayList<VideoAdBean.AdTime> = ArrayList()


    fun initAd(headAd: VideoAdBean?, playAd: VideoAdBean?, pauseAd: VideoAdBean?) {
        release()
        //这里当后天没有配置广告的时候，也回返回广告对象，但是里面的type为空
        mHeadAd = if (headAd == null || TextUtils.isEmpty(headAd.adType)) null else headAd
        mPlayAd = if (playAd == null || TextUtils.isEmpty(playAd.adType)) null else playAd
        mPauseAd = if (pauseAd == null || TextUtils.isEmpty(pauseAd.adType)) null else pauseAd
        mShowedPlayAdList.clear()
        startPlayAdTimer()
    }

    /**
     * 显示暂停广告
     */
    fun showPauseAd(): Boolean {
        if (mPauseAd == null || TextUtils.isEmpty(mPauseAd?.adType)) {
            return false
        }
        mPlayer?.showPauseAd(mPauseAd!!.adUrl)
        // 特权会员直接隐藏 倒计时框, 直接展示关闭
        if (isVip) {
            mPlayer?.getPauseAdCloseIv()?.visibility = View.VISIBLE
            mPlayer?.getPauseAdTimeTextView()?.visibility = View.GONE
        } else {
            mPlayer?.getPauseAdCloseIv()?.visibility = View.GONE
            mPlayer?.getPauseAdTimeTextView()?.visibility = View.VISIBLE
            startPauseAdTimer()
        }
        return true
    }


    private fun startPauseAdTimer() {
        stopPauseAdTimer()
        mPlayer?.getPauseAdTimeTextView()?.isEnabled = false
        mPlayer?.getPauseAdTimeTextView()?.text = "广告"
        mPauseAdTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { timeCount ->
                    if (mPlayer != null) {
                        val time = mPauseAdLength - timeCount
                        if (time <= 0) {
                            mPauseAdTimer?.dispose()
                            // 非特权会员倒计时结束后, 隐藏倒计时框 显示关闭框
                            mPlayer?.getPauseAdTimeTextView()?.text = ""
                            mPlayer?.getPauseAdTimeTextView()?.visibility = View.GONE
                            mPlayer?.getPauseAdCloseIv()?.visibility = View.VISIBLE
                        } else {
                            mPlayer?.getPauseAdTimeTextView()?.text = "$time"
                        }
                    }
                }
    }

    fun stopPauseAdTimer() {
        if (mPauseAdTimer != null && !mPauseAdTimer!!.isDisposed) {
            mPauseAdTimer!!.dispose()
        }
    }

    /**
     * 播放片头广告, 这里分为图片和视频两种
     */
    fun playHeadAd(): Boolean = when {
        mHeadAd == null -> false
        mHeadAd!!.adUrl == null -> false
        TextUtils.isEmpty(mHeadAd!!.adType) -> false
        mHeadAd!!.adType!!.toInt() == VideoAdBean.AD_TYPE_VIDEO -> {
            stopHeadVideoTimer()
            //开始播放视频广告
            mPlayer?.startPlayVideoAd(mHeadAd!!.adUrl!!)
//            if (!isVip) {
//                //开始计时
//                startHeadVideoAdTimer()
//            } else {
//                mPlayer?.getAdTimeTextView()?.isEnabled = true
//                mPlayer?.setHeadAdTimeText("跳过")
//            }
            startHeadAdTimer()
            true
        }
        mHeadAd!!.adType!!.toInt() == VideoAdBean.AD_TYPE_IMAGE -> {
            stopHeadVideoTimer()
            //开始显示图片广告
            mPlayer?.startShowImageAd(mHeadAd!!.adUrl!!)
//            if (!isVip) {
//                //开始计时
//                startHeadImageAdTimer()
//            } else {
//                mPlayer?.getAdTimeTextView()?.isEnabled = true
//                mPlayer?.setHeadAdTimeText("跳过")
//            }
            startHeadAdTimer()
            true
        }
        else -> false
    }

    private fun startHeadAdTimer() {
        mHeadAdTimeLog = 0
        mPlayer?.setHeadAdSkipEnable(isVip)
        mHeadAdTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (isActivityPause) return@subscribe
                    mHeadAdTimeLog++
                    if (mHeadAdTimeLog >= mHeadImageAdLength) {
                        mHeadAdTimeLog = 0
                        mPlayer?.setHeadAdTimeText("${mHeadImageAdLength}s")
                        //超过5秒倒计时结束,停止计时
                        mHeadAdTimer?.dispose()
                        mPlayer?.startPlayVideo()
                    } else {
                        mPlayer?.setHeadAdTimeText("${(mHeadImageAdLength - mHeadAdTimeLog)}s")
                    }
                }
    }

    private fun startHeadVideoAdTimer() {
        mPlayer?.setHeadAdSkipEnable(isVip)
        mPlayer?.setHeadAdTimeText("广告")
        mHeadVideoAdTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (mPlayer != null) {
                        val position = mPlayer!!.getAdPosition()
                        val duration = mPlayer!!.getAdDuration()
                        //这里有时候开始获取视频的长度为0
                        if (duration == 0L) {
                            return@subscribe
                        }
                        LogUtils.d("VideoAdControl", "position = $position")
                        LogUtils.d("VideoAdControl", "duration = $duration")
                        val time = (duration - position) / 1000
                        if (time <= 0) {
                            mHeadVideoAdTimer?.dispose()
                            mPlayer?.setHeadAdTimeText("跳过")
                            mPlayer?.setHeadAdSkipEnable(true)
                        } else {
                            if (isVip) {
                                mPlayer?.setHeadAdTimeText("跳过$time")
                                mPlayer?.setHeadAdSkipEnable(true)
                            } else {
                                mPlayer?.setHeadAdTimeText("广告$time")
                                mPlayer?.setHeadAdSkipEnable(false)
                            }
                        }
                    }
                }
    }

    private fun startHeadImageAdTimer() {
        mPlayer?.setHeadAdSkipEnable(isVip)
        mPlayer?.setHeadAdTimeText("广告")
        mHeadImageAdTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { aLong: Long ->
                    if (aLong >= mHeadImageAdLength) {
                        //超过5秒倒计时结束,停止计时
                        mHeadImageAdTimer?.dispose()
                        mPlayer?.setHeadAdTimeText("跳过")
                        mPlayer?.setHeadAdSkipEnable(true)
                    } else {
                        if (isVip) {
                            mPlayer?.setHeadAdTimeText("跳过${(mHeadImageAdLength - aLong)}")
                            mPlayer?.setHeadAdSkipEnable(true)
                        } else {
                            mPlayer?.setHeadAdTimeText("广告 ${(mHeadImageAdLength - aLong)}")
                            mPlayer?.setHeadAdSkipEnable(false)
                        }
                    }
                }
    }

    private fun startPlayAdTimer() {
        if (mPlayAdTimer != null && !mPlayAdTimer!!.isDisposed) {
            mPlayAdTimer!!.dispose()
        }
        mPlayAdTimer = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkPlayAdShouldShow()
                }
    }

    private fun checkPlayAdShouldShow() {
        if (mPlayer == null) return
        //正在显示片头广告则不显示卡片广告
        if (mPlayer!!.isShowingHeadAd()) return
        if (mPlayAd == null) return
        if (TextUtils.isEmpty(mPlayAd!!.adType)) return
        if (mPlayAd!!.adTime == null || mPlayAd!!.adTime!!.isEmpty()) return
        mPlayAd!!.adTime!!.forEach {
            val adPosition = (mPlayer!!.getAdPosition() / 1000).toFloat()
            if (!TextUtils.isEmpty(it.begin) && !TextUtils.isEmpty(it.end) && it.begin.toLong() <= adPosition && adPosition < it.end.toLong()) {
                if (!mShowedPlayAdList.contains(it)) {
                    mPlayer?.showPlayAd(isVip, mPlayAd!!.adUrl!!)
                    mShowedPlayAdList.add(it)
                }
                return
            }
        }
        mPlayer?.hidePlayAd()
    }

    fun setPlayer(player: AdVideoPlayer) {
        mPlayer = player
    }

    fun stopHeadVideoTimer() {
        if (mHeadVideoAdTimer != null && !mHeadVideoAdTimer!!.isDisposed) {
            mHeadVideoAdTimer!!.dispose()
        }
        if (mHeadImageAdTimer != null && !mHeadImageAdTimer!!.isDisposed) {
            mHeadImageAdTimer!!.dispose()
        }
        if (mHeadAdTimer != null && !mHeadAdTimer!!.isDisposed) {
            mHeadAdTimer!!.dispose()
        }
    }


    fun release() {
        mHeadImageAdTimer?.dispose()
        mHeadImageAdTimer = null
        mHeadVideoAdTimer?.dispose()
        mHeadVideoAdTimer = null
        mPlayAdTimer?.dispose()
        mPlayAdTimer = null
        mPauseAdTimer?.dispose()
        mPauseAdTimer = null
        mPlayer = null
    }

    fun clickPauseAd(context: Context) {
        clickAd(mPauseAd, context, AppConstant.AD_TYPE_VIDEO_PAUSE)
    }

    fun clickPlayAd(context: Context) {
        clickAd(mPlayAd, context, AppConstant.AD_TYPE_VIDEO_CARD)
    }

    fun clickHeadAd(context: Context) {
        clickAd(mHeadAd, context, AppConstant.AD_TYPE_VIDEO_HEAD)
    }

    fun clickAd(bean: VideoAdBean?, context: Context, adType: Int) {
        if (bean == null) return
        if (bean.webUrl == null) return
        if (TextUtils.isEmpty(bean.showType)) return
        if (bean.showType!!.toInt() == VideoAdBean.AD_SHOW_TYPE_APP) {
            ARouter.getInstance().build("/browser/activity")
                    .withString("mWebUrl", bean.webUrl)
                    .navigation()
            mPlayer?.addAddInfo(adType, bean.id)
        } else if (bean.showType!!.toInt() == VideoAdBean.AD_SHOW_TYPE_BROWSER) {
            TestApp.getContext().startActivity(IntentUtils.getBrowserIntent(bean.webUrl))
            mPlayer?.addAddInfo(adType, bean.id)
        }
    }

    interface AdVideoPlayer {

        fun setHeadAdTimeText(text: String)

        fun startPlayVideo()

        fun startShowImageAd(adUrl: String)

        fun startPlayVideoAd(adUrl: String)

        fun setHeadAdSkipEnable(enable: Boolean)

        fun getAdPosition(): Long

        fun getAdDuration(): Long

        fun showPlayAd(vip: Boolean, pic: String?)

        fun hidePlayAd()

        fun isShowingHeadAd(): Boolean

        fun getPauseAdTimeTextView(): TextView?

        fun getPauseAdCloseIv(): ImageView?

        fun showPauseAd(pic: String?)

        fun addAddInfo(adType: Int, adId: String?)

    }

}