package com.video.test.module.videotype

import com.video.test.javabean.*
import com.video.test.network.RxExceptionHandler
import com.video.test.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import me.drakeet.multitype.Items


/**
 * @author : AhhhhDong
 * @date : 2019/4/9 16:16
 */
abstract class BaseVideoTypeListPresenter<M : VideoTypeListContract.Model, V : VideoTypeListContract.View> : VideoTypeListContract.Presenter<M, V>() {

    protected var TAG = "VideoTypeListPresenter"

    override fun subscribe() {
    }

    override fun getHomePageVideoList(pid: Int) {
        mView.showRefreshLayout()
        val disposable = mModel.getHomepageVideoList(pid)
                .subscribe(Consumer { homepageVideoBeans ->
                    getVideoListSuccess(homepageVideoBeans)
                    mView.hideRefreshLayout(true)
                }, RxExceptionHandler { throwable ->
                    mView.hideRefreshLayout(false)
                    mView.showNetworkErrorView()
                    LogUtils.e(TAG, "getHomePageVideoList Error == " + throwable.message)
                })
        addDisposable(disposable)
    }

    protected open fun getVideoListSuccess(data: HomePageVideoListBean) {
        val homepageVideoBeans = data.vod
        val items = Items()
        for (homepageVideoBean in homepageVideoBeans) {
            //推荐视频或广告位
            if (homepageVideoBean.parentId != null && Integer.parseInt(homepageVideoBean.parentId) > 0) {
                //横向推荐视频
                items.addAll(buildRecommendVideos(homepageVideoBean))
                //广告位
                if (homepageVideoBean.isAd) {
                    if (homepageVideoBean.adInfo != null) {
                        items.add(homepageVideoBean.adInfo)
                    }
                }
            } else {
                items.add(buildTitle(homepageVideoBean))
                items.addAll(homepageVideoBean.list)
            }//普通视频分类
        }
        addFooter(items)
        mView.setVideoData(items)
    }

    /**
     * 横向推荐视频
     */
    protected open fun buildRecommendVideos(homepageVideoBean: HomepageVideoBean): List<Any> {
        val videoList = homepageVideoBean.list ?: return ArrayList()
        val list: ArrayList<Any> = ArrayList()
        val titleBean = buildTitle(homepageVideoBean)
        titleBean.isShowDivider = false
        titleBean.columnType = VideoTitleBean.TYPE_RECOMMEND
        list.add(0, titleBean)
        if (videoList.isNotEmpty()) {
            videoList.forEach { list.add(it!!) }
        }
        return list
    }

    protected open fun addFooter(items: Items) {
        val footerSize = items.size
        when {
            footerSize == 0 -> LogUtils.d(TAG, "addFooter items is empty")
            items[footerSize - 1] !is FooterViewBean -> items.add(footerSize, FooterViewBean())
            else -> LogUtils.d(TAG, "addFooter have Footer")
        }
    }

    protected open fun buildTitle(homepageVideoBean: HomepageVideoBean): VideoTitleBean {
        val videoTitleBean = VideoTitleBean()
        LogUtils.d(TAG, "VideoTitle == " + homepageVideoBean.type)
        videoTitleBean.parentId = homepageVideoBean.parentId
        videoTitleBean.pid = homepageVideoBean.pid
        videoTitleBean.tag = homepageVideoBean.tag
        videoTitleBean.type = homepageVideoBean.type
        videoTitleBean.typePic = homepageVideoBean.typePic
        videoTitleBean.showId = homepageVideoBean.showId
        videoTitleBean.columnType = VideoTitleBean.TYPE_CATEGORY
        videoTitleBean.isShowDivider = true
        return videoTitleBean
    }

    override fun addAdInfo(adType: Int, adId: String) {
        val subscribe = mModel.addAdInfo(adType, adId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { }, RxExceptionHandler<Throwable>(Consumer { e -> LogUtils.e(TAG, e.message) }))
        addDisposable(subscribe)
    }

    /**
     * 获取banner和通知
     */
    override fun getBannerAndNotice(pid: Int) {
        val disposable: Disposable = mModel.getBannerAndNotice(pid)
                .subscribe(Consumer<BannerAndNoticeListBean> { data: BannerAndNoticeListBean -> this.setBannerAndNotice(data) },
                        RxExceptionHandler<Throwable>(Consumer { throwable: Throwable -> LogUtils.e(TAG, "getBannerAndNotice Error == " + throwable.message) }))
        addDisposable(disposable)
    }

    /**
     * 根据获取到的数据，加载页面的banner和通知
     *
     * @param data 获取到的banner和通知数据
     */
    protected open fun setBannerAndNotice(data: BannerAndNoticeListBean) { //banner
        val bannerList = data.bannerList
        if (bannerList != null) {
            val count = bannerList.size
            val picUrls: MutableList<String> = java.util.ArrayList(count)
            val bannerContent: MutableList<String> = java.util.ArrayList(count)
            for (bannerBean in bannerList) {
                picUrls.add(bannerBean.slidePic)
                bannerContent.add(bannerBean.banner_content)
                LogUtils.i(TAG, "picUrls == " + bannerBean.slidePic + " vodId == " + bannerBean.vodId + " bannerContent : " + bannerBean.banner_content)
            }
            mView.initBanner(picUrls, bannerContent, bannerList)
        }
    }
}

