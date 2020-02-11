package com.video.test.module.download

import android.text.TextUtils
import com.video.test.R
import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.DownloadBean
import com.video.test.javabean.DownloadedBean
import com.video.test.javabean.DownloadingBean
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.javabean.event.DownloadEvent
import com.video.test.utils.RxSchedulers
import com.video.test.utils.SDCardUtils
import io.reactivex.Observable
import jaygoo.library.m3u8downloader.bean.M3U8TaskState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DownloadPresenter : DownloadContract.Presenter<DownloadModel>() {
    private var isRequestData = false

    private var mSelectedSet: HashSet<DownloadBean> = HashSet()

    override fun subscribe() {
    }

    override fun attachView(view: DownloadContract.View?) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    override fun unSubscribe() {
        super.unSubscribe()
        EventBus.getDefault().unregister(this)
    }

    override fun getSDCardFreeSize() {
        val subscribe = Observable.interval(2, TimeUnit.SECONDS)
                .flatMap { mModel!!.getSDCardInfo() }
                .compose(RxSchedulers.io_main())
                .subscribe({
                    val usedSpaceBytes = it.totalSize - it.freeSize
                    val usedSpace = SDCardUtils.getSizeString(usedSpaceBytes.toDouble())
                    val totalSpace = SDCardUtils.getSizeString(it.totalSize.toDouble())
                    val percent = (usedSpaceBytes.toDouble() / it.totalSize * 100.0).toInt()
                    mView?.setSDCardProgress(usedSpace, totalSpace, percent)
                }, { it.printStackTrace() })
        addDisposable(subscribe)
    }

    override fun getAllVideoTasks() {
        if (isRequestData) return
        isRequestData = true
        val subscribe = mModel!!.getAllTasks(TestApp.getContext())
                .map { list ->
                    var downloadingBean: DownloadingBean? = null
                    val downloadList = ArrayList<DownloadBean>()
                    list.forEach { m3u8 ->
                        if (m3u8.isDownloaded()) {
                            var downloadBean: DownloadBean?
                            downloadBean = downloadList.firstOrNull { TextUtils.equals(it.videoId, m3u8.videoId) }
                            if (downloadBean == null) {
                                downloadBean = DownloadedBean()
                                downloadBean.videoId = m3u8.videoId
                                downloadBean.videoName = m3u8.videoTotalName
                                downloadList.add(downloadBean)
                            }
                            downloadBean as DownloadedBean
                            downloadBean.tasks.add(m3u8)
                            downloadBean.size += m3u8.totalFileSize
                        } else {
                            if (downloadingBean == null) {
                                downloadingBean = DownloadingBean()
                                downloadList.add(0, downloadingBean!!)
                            }
                            downloadingBean!!.tasks.add(m3u8)
                            if (m3u8.taskStatus == M3U8TaskState.DOWNLOADING || m3u8.taskStatus == M3U8TaskState.PREPARE) {
                                downloadingBean!!.progress = m3u8.progress
                                downloadingBean!!.isDownloading = true
                                downloadingBean!!.videoName = m3u8.videoTotalName
                            }
                        }
                    }
                    downloadList
                }
                .compose(RxSchedulers.io_main())
                .doAfterTerminate { isRequestData = false }
                .subscribe({ list ->
                    mView.adapter?.data = list
                    if (list.isEmpty()) {
                        mView.hideEditBtn()
                        mView.showNoCacheBackground()
                    } else {
                        mView.showEditBtn()
                        mView.hideNoCacheBackground()
                    }
                    mView.adapter?.notifyDataSetChanged()
                }, { it.printStackTrace() })
        addDisposable(subscribe)
    }

    private fun selectAll() {
        if (mView!!.adapter != null) {
            val adapter = mView!!.adapter!!
            adapter.selectAll()
            adapter.notifyDataSetChanged()
            adapter.data?.let {
                mSelectedSet.addAll(it)
            }
            setSelectCountText(mSelectedSet.size)
            setSelectAllText(mSelectedSet.size)
        }
    }

    override fun deselectAll() {
        if (mView!!.adapter != null) {
            val adapter = mView!!.adapter!!
            adapter.deSelectAll()
            adapter.notifyDataSetChanged()
        }
        mSelectedSet.clear()
        setSelectCountText(0)
        setSelectAllText(0)
    }

    private fun setSelectCountText(count: Int) {
        mView!!.setDeleteBtnEnable(count > 0)
        if (count <= 0) {
            mView?.setSelectCountText(TestApp.getContext().getString(R.string.activity_download_delete))
        } else {
            val text = TestApp.getContext().getString(R.string.activity_download_delete).toString() + "(" + count + ")"
            mView?.setSelectCountText(text)
        }
    }

    private fun setSelectAllText(count: Int) {
        val adapter = mView!!.adapter!!
        if (count == 0 || adapter.data == null || adapter.data!!.isEmpty()) {
            mView!!.setSelectAllText(TestApp.getContext().getString(R.string.activity_download_selectAll))
        } else {
            val allTaskCount = adapter.data!!.size
            if (allTaskCount > count) {
                mView!!.setSelectAllText(TestApp.getContext().getString(R.string.activity_download_selectAll))
            } else {
                mView!!.setSelectAllText(TestApp.getContext().getString(R.string.activity_download_deselectAll))
            }
        }
    }

    override fun onSelectVideo(selected: Boolean, bean: DownloadBean) {
        if (selected) {
            mSelectedSet.add(bean)
        } else {
            mSelectedSet.remove(bean)
        }
        setSelectCountText(mSelectedSet.size)
        setSelectAllText(mSelectedSet.size)
    }

    override fun onSelectAllClick() {
        if (mView!!.adapter == null || mView!!.adapter!!.data == null || mView!!.adapter!!.data!!.isEmpty()) {
            return
        }
        if (mSelectedSet.isEmpty()) {
            selectAll()
        } else {
            val allTaskCount = mView!!.adapter!!.data!!.size
            val selectCount = mSelectedSet.size
            if (allTaskCount > selectCount) {
                selectAll()
            } else {
                deselectAll()
            }
        }
    }

    override fun onDeleteClick() {
        if (mSelectedSet.size >= 2) { //大于两个弹窗
            mView?.showDeleteConfirmDialog()
        } else if (mSelectedSet.size > 0) {
            deleteSelected()
        }
    }

    /**
     * 批量删除
     */
    override fun deleteSelected() {
        deleteTasks(*mSelectedSet.toArray(arrayOf()))
        mView?.setEditMode(false)
        deselectAll()
        mView.setResultOk()
    }

    override fun deleteTasks(vararg downloadBeans: DownloadBean) {
        val subscribe = Observable.just(downloadBeans)
                .map { array ->
                    val allTask = DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks()
                    val allTaskTemp = ArrayList(allTask)
                    val deleteUrls = array.flatMap { getDownloadTasksByVideo(allTaskTemp, it) }.map { it.videoUrl }.toTypedArray()
                    deleteM3U8Task(allTask, *deleteUrls)
                    return@map "操作成功"
                }
                .compose(RxSchedulers.io_main())
                .doAfterTerminate { getAllVideoTasks() }
                .subscribe({
                    mView.showToast(it)
                }, { it.printStackTrace() })
        addDisposable(subscribe)
    }

    /**
     * 遍历整个下载列表，取出其中满足匹配条件的任务，并将其组合成一个新列表并返回
     * @param downloadBean 选中的视频
     * @param allTask 整个下载任务列表
     */
    private fun getDownloadTasksByVideo(allTask: MutableList<M3U8DownloadBean>, downloadBean: DownloadBean): List<M3U8DownloadBean> {
        //用来筛选的函数,返回true则为筛选通过
        val filter =
                if (downloadBean is DownloadingBean)
                //如果是下载中的任务，则遍历整个下载列表，取出下载中的任务
                    fun(m3u8: M3U8DownloadBean, _: DownloadBean): Boolean = !m3u8.isDownloaded()
                else
                //如果是某个视频，则遍历整个下载列表，取出与其videoId相匹配并且已下载完成的任务
                    fun(m3u8: M3U8DownloadBean, downloadBean: DownloadBean): Boolean = TextUtils.equals(m3u8.videoId, downloadBean.videoId) && m3u8.isDownloaded()
        val list: MutableList<M3U8DownloadBean> = ArrayList()
        for (index in allTask.size - 1 downTo 0) {
            val m3U8DownloadBean = allTask[index]
            if (filter(m3U8DownloadBean, downloadBean)) {
                list.add(m3U8DownloadBean)
                allTask.removeAt(index)
            }
        }
        return list
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHandleDownloadEvent(event: DownloadEvent) {
        if (event.type == DownloadEvent.Type.TYPE_PROGRESS) {
            val task = event.task ?: return
            mView ?: return
            val adapter = mView.adapter ?: return
            val data = adapter.data
            if (data != null && data.isNotEmpty() && data[0] is DownloadingBean) {
                val downloadBean = data[0] as DownloadingBean
                val m3U8DownloadBean = downloadBean.tasks.firstOrNull { TextUtils.equals(task.url, it.videoUrl) }
                m3U8DownloadBean?.taskStatus = task.state
                m3U8DownloadBean?.progress = task.progress
                downloadBean.videoName = task.videoName
                downloadBean.downloadSpeed = task.speed
                downloadBean.progress = task.progress
                downloadBean.isDownloading = true
                adapter.updateProgress(downloadBean)
            } else {
                getAllVideoTasks()
            }
        } else {
            getAllVideoTasks()
        }
    }
}