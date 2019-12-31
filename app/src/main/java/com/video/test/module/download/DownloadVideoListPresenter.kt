package com.video.test.module.download

import android.text.TextUtils
import android.util.Log
import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.javabean.event.DownloadEvent
import com.video.test.network.RxExceptionHandler
import com.video.test.sp.SpUtils
import com.video.test.utils.LogUtils
import com.video.test.utils.RxSchedulers
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import jaygoo.library.m3u8downloader.M3U8Downloader
import jaygoo.library.m3u8downloader.bean.M3U8TaskState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DownloadVideoListPresenter : DownloadVideoListContract.Presenter<DownloadVideoListModel>() {

    private val TAG = "DownloadPresenter"
    private var mUserLevel: String? = null
    private var isRequestingTask = false
    override fun subscribe() {
        mUserLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL)
    }

    override fun attachView(view: DownloadVideoListContract.View?) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    override fun unSubscribe() {
        super.unSubscribe()
        EventBus.getDefault().unregister(this)
    }

    override fun getM3U8Tasks() {
        if (isRequestingTask) {
            return
        }
        isRequestingTask = true
        var observable: Observable<List<M3U8DownloadBean>> =
                if (videoId == null) {
                    mModel.getDownloadingTask()
                } else {
                    mModel.getDownloadedTask()
                            .flatMap { list -> Observable.fromIterable(list) }
                            .filter { TextUtils.equals(it.videoId, videoId) }
                            .toList()
                            .toObservable()
                }
        val subscribe = observable
                .compose(RxSchedulers.io_main())
                .subscribe(Consumer { list: List<M3U8DownloadBean> ->
                    isRequestingTask = false
                    if (list.isEmpty()) {
                        mView.showNoCacheBackground()
                        mView.hideEditBtn()
                    } else {
                        mView.showEditBtn()
                        mView.hideNoCacheBackground()
                        mView.setDownloadBeans(list)
                    }
                }, RxExceptionHandler(Consumer { throwable: Throwable ->
                    isRequestingTask = false
                    LogUtils.e(TAG, "getM3U8Tasks Error : " + throwable.message)
                }))
        addDisposable(subscribe)
    }

    override fun pauseAllTasks() {
        val list = DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks()
        for (m3u8 in list) {
            if (!m3u8.isDownloaded()) {
                m3u8.taskStatus = AppConstant.M3U8_TASK_PAUSE
                DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3u8)
            }
        }
    }

    override fun startM3U8Task(m3U8DownloadingBean: M3U8DownloadBean) {
        M3U8Downloader.getInstance().download(m3U8DownloadingBean.videoUrl, m3U8DownloadingBean.videoId, m3U8DownloadingBean.videoName, m3U8DownloadingBean.videoTotalName)
    }

    override fun pauseM3U8Task(m3U8DownloadingBean: M3U8DownloadBean) {
        M3U8Downloader.getInstance().pause(m3U8DownloadingBean.videoUrl)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleDownloadEvent(event: DownloadEvent) {
        if (event.type == DownloadEvent.Type.TYPE_PROGRESS) {
            //videoId不为空说明此页面展示的是下载完成的视频
            if (videoId != null) return
            val task = event.task ?: return
            mView ?: return
            val adapter = mView.adapter ?: return
            val data = adapter.data
            if (data != null && data.isNotEmpty()) {
                adapter.updateDownloadingStatus(task)
            } else {
                getM3U8Tasks()
            }
        } else if (event.type == DownloadEvent.Type.TYPE_UPDATE_STATUS) {
            val task = event.task ?: return
            if (videoId == null) {
                getM3U8Tasks()
            } else {
                if (task.state == M3U8TaskState.SUCCESS && TextUtils.equals(videoId, task.videoId)) {
                    getM3U8Tasks()
                }
            }
        } else {
            Log.d(TAG, "handle download delete event,start get data")
            getM3U8Tasks()
        }
    }

}