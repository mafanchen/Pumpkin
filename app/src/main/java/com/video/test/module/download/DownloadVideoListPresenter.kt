package com.video.test.module.download

import com.video.test.AppConstant
import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.network.RxExceptionHandler
import com.video.test.sp.SpUtils
import com.video.test.utils.FileUtils
import com.video.test.utils.LogUtils
import com.video.test.utils.RxSchedulers
import com.video.test.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import jaygoo.library.m3u8downloader.M3U8Downloader
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig
import jaygoo.library.m3u8downloader.OnM3U8DownloadListener
import jaygoo.library.m3u8downloader.bean.M3U8Task
import java.io.File
import java.util.*

class DownloadVideoListPresenter : DownloadVideoListContract.Presenter<DownloadVideoListModel>() {

    private val TAG = "DownloadVideoListPresenter"
    private val M3U8TAG = "DownM3U8Listener"
    private var mUserLevel: String? = null
    private var isRequestingTask = false

    override fun subscribe() {}

    override fun getM3U8Tasks(userLevel: String?) {
        if (isRequestingTask) {
            return
        }
        if (videoId == null) {
            return
        }
        isRequestingTask = true
        val subscribe = mModel.getAllTask(videoId!!)
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


    override fun initM3U8Listener() {
        mUserLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL)
        M3U8Downloader.getInstance().setOnM3U8DownloadListener(object : OnM3U8DownloadListener() {
            override fun onDownloadItem(task: M3U8Task, itemFileSize: Long, totalTs: Int, curTs: Int) {
                super.onDownloadItem(task, itemFileSize, totalTs, curTs)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + " itemFileSize : " + itemFileSize + " curTs : " + curTs + " totalTs : " + totalTs)
                updateM3U8StartTask(task, totalTs, curTs)
            }

            override fun onDownloadSuccess(task: M3U8Task) {
                super.onDownloadSuccess(task)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "Success taskName : " + task.videoName)
                updateM3U8TaskSuccess(task)
                mView.showToast(task.videoName + " 已完成缓存")
                getM3U8Tasks(mUserLevel)
            }

            override fun onDownloadPause(task: M3U8Task) {
                super.onDownloadPause(task)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "Pause  taskName : " + task.videoName)
                mView.showToast(task.videoName + " 已暂停")
                updateM3U8TaskStatus(task)
                getM3U8Tasks(mUserLevel)
            }

            override fun onDownloadPending(task: M3U8Task) {
                super.onDownloadPending(task)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "Pending task status : " + task.state)
                mView.showToast(task.videoName + " 已添加缓存队列")
                insertM3U8Task(task)
            }

            override fun onDownloadPrepare(task: M3U8Task) {
                super.onDownloadPrepare(task)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "Prepare task status : " + task.state)
                updateM3U8TaskStatus(task)
            }

            override fun onDownloadProgress(task: M3U8Task) {
                super.onDownloadProgress(task)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "progress task progress : " + task.progress)
                //mView.notifyDataChange(task.getFormatSpeed());
            }

            override fun onDownloadError(task: M3U8Task, errorMsg: Throwable) {
                super.onDownloadError(task, errorMsg)
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().name + "error task status : " + task.state)
                updateM3U8TaskStatus(task)
                getM3U8Tasks(mUserLevel)
            }
        })
    }

    private fun insertM3U8Task(task: M3U8Task) {
        val downloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.url)
        if (null == downloadBean) {
            val m3U8DownloadBean = M3U8DownloadBean()
            m3U8DownloadBean.videoUrl = task.url
            m3U8DownloadBean.videoId = task.videoId
            m3U8DownloadBean.videoName = task.videoName
            m3U8DownloadBean.setIsDownloaded(false)
            m3U8DownloadBean.taskStatus = task.state
            val rowID = DBManager.getInstance(TestApp.getContext()).insertM3U8Task(m3U8DownloadBean)
        } else { // 已经存在的情况下 ,更新该资源的状态
            LogUtils.d(TAG, "数据库已存在该数据")
            downloadBean.taskStatus = task.state
            downloadBean.progress = task.progress
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(downloadBean)
        }
    }

    private fun updateM3U8StartTask(task: M3U8Task, totalTs: Int, curTs: Int) {
        val m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.url)
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.curTs = curTs
            m3U8DownloadBean.totalTs = totalTs
            m3U8DownloadBean.taskStatus = AppConstant.M3U8_TASK_DOWNLOADING
            m3U8DownloadBean.progress = task.progress
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean)
        } else {
            LogUtils.d(TAG, "数据库未查询到数据")
        }
    }

    private fun updateM3U8TaskStatus(task: M3U8Task) {
        val m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.url)
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.taskStatus = task.state
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean)
        } else {
            LogUtils.d(TAG, "数据库未查询到数据")
        }
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

    private fun updateM3U8TaskSuccess(task: M3U8Task) {
        val m3U8 = task.m3U8
        val m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.url)
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.taskStatus = task.state
            //下载完成的百分比应该都是 100%
            m3U8DownloadBean.progress = 1.0f
            m3U8DownloadBean.setIsDownloaded(true)
            m3U8DownloadBean.dirFilePath = m3U8.dirFilePath
            m3U8DownloadBean.m3u8FilePath = m3U8.m3u8FilePath
            m3U8DownloadBean.totalFileSize = m3U8.fileSize
            m3U8DownloadBean.totalTime = m3U8.totalTime
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean)
        } else {
            LogUtils.d(TAG, "数据库未查询到数据")
        }
    }

    override fun deleteM3U8Task(taskList: List<Any>, vararg taskUrl: String) {
        val disposable = Observable.just(listOf(*taskUrl))
                .map { taskUrls ->
                    for (task in taskUrls) {
                        M3U8Downloader.getInstance().cancel(task)
                    }
                    //批量删除数据库中的下载任务
                    val deleteTaskBeans: MutableList<M3U8DownloadBean> = ArrayList()
                    for (task in DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks()) {
                        if (taskUrls.contains(task.videoUrl)) {
                            deleteTaskBeans.add(task)
                        }
                    }
                    DBManager.getInstance(TestApp.getContext()).deleteM3U8Task(deleteTaskBeans)
                    //这里遍历所有下载任务，排除要删除的任务，将要保留的任务添加到新的集合
                    val pathList: MutableList<String> = ArrayList()
                    for (o in taskList) {
                        if (o is M3U8DownloadBean) {
                            val url = o.videoUrl
                            if (!taskUrls.contains(url)) {
                                val m3U8Path = M3U8Downloader.getInstance().getM3U8Path(url)
                                val m3u8DirPath = m3U8Path.substring(0, m3U8Path.lastIndexOf('/'))
                                pathList.add(m3u8DirPath)
                            }
                        }
                    }
                    val saveDir = File(M3U8DownloaderConfig.getSaveDir())
                    if (!saveDir.exists()) {
                        saveDir.mkdirs()
                    }
                    //这里遍历下载文件夹下的所有文件夹，如果子文件夹不是要保留的下载任务，则删除
                    val files = saveDir.listFiles()
                    if (files != null && files.isNotEmpty()) {
                        for (file in files) {
                            if (file.isDirectory && !pathList.contains(file.absolutePath)) {
                                FileUtils.deleteDir(file)
                            }
                        }
                    }
                    true
                }.compose(RxSchedulers.io_main())
                .subscribe({ aBoolean ->
                    if (aBoolean) {
                        ToastUtils.showLongToast(TestApp.getContext(), "操作成功")
                        getM3U8Tasks(mUserLevel)
                    } else {
                        ToastUtils.showLongToast(TestApp.getContext(), "操作失败,请您稍后重试")
                    }
                }
                ) { throwable ->
                    LogUtils.e(TAG, "deleteM3U8Task Error : " + throwable.message)
                    ToastUtils.showLongToast(TestApp.getContext(), "操作失败,请您稍后重试")
                }
        addDisposable(disposable)
    }

    override fun startM3U8Task(m3U8DownloadingBean: M3U8DownloadBean) {
        M3U8Downloader.getInstance().download(m3U8DownloadingBean.videoUrl, m3U8DownloadingBean.videoId, m3U8DownloadingBean.videoName)
    }

    override fun pauseM3U8Task(m3U8DownloadingBean: M3U8DownloadBean) {
        M3U8Downloader.getInstance().pause(m3U8DownloadingBean.videoUrl)
    }

}