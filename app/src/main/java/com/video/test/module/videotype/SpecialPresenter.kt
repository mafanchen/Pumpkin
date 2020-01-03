package com.video.test.module.videotype

import com.video.test.javabean.HomePageVideoListBean
import com.video.test.javabean.HomepageVideoBean
import com.video.test.javabean.VideoTitleBean
import me.drakeet.multitype.Items

class SpecialPresenter : BaseVideoTypeListPresenter<VideoTypeListModel, SpecialVideoListContract.View>() {

    override fun getVideoListSuccess(data: HomePageVideoListBean) {
        mView.setTopVideo(data.topVideo)
        mView.setVideoColumn(data.columnBean)
        super.getVideoListSuccess(data)
    }

    override fun addFooter(items: Items) {}

    override fun buildTitle(homepageVideoBean: HomepageVideoBean): VideoTitleBean {
        val titleBean = super.buildTitle(homepageVideoBean)
        titleBean.columnType = VideoTitleBean.TYPE_HOTEST
        return titleBean
    }

}