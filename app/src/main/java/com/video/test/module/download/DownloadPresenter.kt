package com.video.test.module.download

import android.text.TextUtils
import com.video.test.R
import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.DownloadBean
import com.video.test.javabean.DownloadingBean
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.utils.RxSchedulers
import com.video.test.utils.SDCardUtils
import io.reactivex.Observable
import java.util.HashSet
import java.util.concurrent.TimeUnit

class DownloadPresenter : DownloadContract.Presenter<DownloadModel>() {

    private var mSelectedSet: HashSet<DownloadBean> = HashSet()


    override fun getSDCardFreeSize() {
        val subscribe = Observable.timer(2, TimeUnit.SECONDS)
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

    override fun subscribe() {

    }

    override fun getAllVideoTasks() {
        val subscribe = mModel!!.getAllTasks(TestApp.getContext())
                .map { list ->
//                    list.forEach { setDownloadDynamicData(it) }
                    list
                }
                .compose(RxSchedulers.io_main())
                .subscribe({

                }, {

                })
        addDisposable(subscribe)
    }

    /**
     * 设置下载状态、下载速度、下载第几集、占用空间等动态数据
     * 此方法可能存在耗时操作
     */
//    private fun setDownloadDynamicData(data: DownloadedBean): DownloadedBean {
//        val tasks = data.tasks
//        return data
//    }

    /**
     * 根据分集任务列表的下载状态判断视频下载的状态
     * 当有任意一个分集正在
     * 排队[com.video.test.AppConstant.M3U8_TASK_PENDING]
     * 准备[com.video.test.AppConstant.M3U8_TASK_PREPARE]
     * 下载中[com.video.test.AppConstant.M3U8_TASK_DOWNLOADING]时，则此视频为下载状态，
     * 否则，当有任意一个分集处在
     * 出错[com.video.test.AppConstant.M3U8_TASK_ERROR]
     * 暂停[com.video.test.AppConstant.M3U8_TASK_PAUSE]
     * 空间不足[com.video.test.AppConstant.M3U8_TASK_ENOSPC]时，则此视频为暂停状态，
     * 其他情况，将视为已经下载完成
     */
//    private fun getDownloadState(tasks: List<M3U8DownloadBean>): Int {
//        var state = DownloadedBean.STATE_DOWNLOADED
//        tasks.forEach { task ->
//            when (task.taskStatus) {
//                AppConstant.M3U8_TASK_PENDING, AppConstant.M3U8_TASK_PREPARE, AppConstant.M3U8_TASK_DOWNLOADING -> {
//                    state = DownloadedBean.STATE_DOWNLOADING
//                    return@forEach
//                }
//                AppConstant.M3U8_TASK_ERROR, AppConstant.M3U8_TASK_PAUSE, AppConstant.M3U8_TASK_ENOSPC -> state = DownloadedBean.STATE_PAUSED
//            }
//        }
//        return state
//    }

    /**
     * 计算下载任务占用空间
     */
//    private fun getM3u8TaskSize(tasks: List<M3U8DownloadBean>): String {
//        var size: Long = 0
//        tasks.forEach { task ->
//            val spaceBytes = SDCardUtils.getTotalSpaceBytes(task.dirFilePath)
//            size += spaceBytes
//        }
//        return SDCardUtils.getSizeString(size.toDouble())
//    }


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
}