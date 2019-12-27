package com.video.test.module.download

import com.video.test.TestApp
import com.video.test.db.DBManager
import com.video.test.framework.BasePresenter
import com.video.test.framework.IView
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.utils.FileUtils
import jaygoo.library.m3u8downloader.M3U8Downloader
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig
import java.io.File
import java.util.*

abstract class BaseDownloadPresenter<M, V : IView> : BasePresenter<M, V>() {

    /**
     * 根据下载任务的url来删除下载任务
     * 需要先查询所有下载任务，过滤掉需要删除的的任务，留下来的就是保留的文件夹。然后删除下载目录下除了需要保留的任务的其他文件夹
     * 注意的是，此方法有很多io操作，不能在主线程调用
     */
    fun deleteM3U8Task(allTask: List<M3U8DownloadBean>, vararg taskUrl: String) {
        //先暂停需要删除的任务
        for (task in taskUrl) {
            M3U8Downloader.getInstance().cancel(task)
        }
        //需要批量删除数据库中的下载任务集合
        val deleteTaskBeans: MutableList<M3U8DownloadBean> = ArrayList()
        //需要保留的下载文件夹集合
        val pathList: MutableList<String> = ArrayList()
        //这里遍历所有下载任务，将要删除的任务添加到删除的集合，将要保留的任务的文件夹添加到保留的文件夹集合
        for (task in allTask) {
            //这里通过url，筛选出需要删除的数据库任务数据，然后批量删除
            if (taskUrl.contains(task.videoUrl)) {
                deleteTaskBeans.add(task)
            }
            //如果不是删除的任务，则通过此任务的url获取其下载文件夹，将其添加到需要保留的文件夹集合中
            else {
//                pathList.add(task.dirFilePath)
                val m3U8Path = M3U8Downloader.getInstance().getM3U8Path(task.videoUrl)
                val m3u8DirPath = m3U8Path.substring(0, m3U8Path.lastIndexOf('/'))
                pathList.add(m3u8DirPath)
            }
        }
        //批量删除数据库中的下载任务
        DBManager.getInstance(TestApp.getContext()).deleteM3U8Task(deleteTaskBeans)
        //获取下载根目录
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
    }
}