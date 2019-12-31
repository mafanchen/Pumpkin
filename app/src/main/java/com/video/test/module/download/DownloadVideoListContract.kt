package com.video.test.module.download

import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.M3U8DownloadBean
import io.reactivex.Observable

interface DownloadVideoListContract {

    interface View : IView {
        var adapter: DownloadAdapter?

        fun showNoCacheBackground()

        fun hideNoCacheBackground()

        fun setDownloadBeans(downloadBeans: List<M3U8DownloadBean>)

        fun showEditBtn()

        fun hideEditBtn()

        fun showToast(text: String)
    }

    interface Model : IModel {

        fun getDownloadingTask(): Observable<List<M3U8DownloadBean>>
        fun getDownloadedTask(): Observable<List<M3U8DownloadBean>>
    }

    abstract class Presenter<M : Model> : BaseDownloadPresenter<M, View>() {

        var videoId: String? = null

        abstract fun getM3U8Tasks()

        abstract fun pauseAllTasks()

        abstract fun startM3U8Task(m3U8DownloadingBean: M3U8DownloadBean)

        abstract fun pauseM3U8Task(m3U8DownloadingBean: M3U8DownloadBean)
    }
}