package com.video.test.module.simplevideo

import com.video.test.javabean.FooterViewBean
import com.video.test.javabean.HomePageVideoListBean
import com.video.test.javabean.HomepageVideoBean
import com.video.test.javabean.VideoTitleBean
import com.video.test.utils.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.drakeet.multitype.Items
import java.util.*

class SimpleVideoPresenter : SimpleVideoContract.Presenter<SimpleVideoModel>() {

    companion object {
        const val TAG = "SimpleVideoPresenter"
    }

    override fun getHomePageVideoList() {
        val subscribe = mModel.getHomePageVideoList()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { mView.showRefreshLayout() }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { mView.hideRefreshLayout(true) }
                .doOnError { mView.hideRefreshLayout(false) }
                .subscribe({
                    getVideoListSuccess(it)
                }, { throwable ->
                    LogUtils.e(TAG, "getHomePageVideoList Error == " + throwable.message)
                })
        addDisposable(subscribe)
    }

    private fun getVideoListSuccess(data: HomePageVideoListBean) {
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

    private fun buildTitle(homepageVideoBean: HomepageVideoBean): VideoTitleBean {
        val videoTitleBean = VideoTitleBean()
        LogUtils.d(TAG, "VideoTitle == " + homepageVideoBean.type)
        videoTitleBean.parentId = homepageVideoBean.parentId
        videoTitleBean.pid = homepageVideoBean.pid
        videoTitleBean.tag = homepageVideoBean.tag
        videoTitleBean.type = homepageVideoBean.type
        videoTitleBean.typePic = homepageVideoBean.typePic
        return videoTitleBean
    }

    private fun buildRecommendVideos(homepageVideoBean: HomepageVideoBean): List<*> {
        val list = homepageVideoBean.list ?: return ArrayList<Any>()
        if (list.isNotEmpty()) {
            list.add(0, buildTitle(homepageVideoBean))
        }
        return list
    }

    private fun addFooter(items: Items) {
        val footerSize = items.size
        if (items[footerSize - 1] !is FooterViewBean) {
            items.add(footerSize, FooterViewBean())
        } else {
            LogUtils.d(TAG, "addFooter have Header")
        }
    }


    override fun subscribe() {
    }


}