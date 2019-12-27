package com.video.test.module.download

import android.content.Context
import com.video.test.framework.IModel
import com.video.test.framework.IView
import com.video.test.javabean.DownloadBean
import com.video.test.javabean.M3U8DownloadBean
import com.video.test.javabean.SdCardInfoBean
import com.video.test.ui.adapter.DownloadVideoAdapter
import io.reactivex.Observable

interface DownloadContract {

    interface Model : IModel {

        /**
         * 获取sd卡容量信息
         */
        fun getSDCardInfo(): Observable<SdCardInfoBean>

        fun getAllTasks(context: Context): Observable<MutableList<M3U8DownloadBean>>
    }

    interface View : IView {

        abstract var adapter: DownloadVideoAdapter?

        /**
         * 展示sd使用情况
         * @param used 已使用
         * @param total 总容量
         * @param percent 已使用占用总容量百分比
         */
        fun setSDCardProgress(used: String, total: String, percent: Int)

        /**
         * 显示无内容时展示的图片
         */
        fun showNoCacheBackground()

        /**
         * 隐藏无内容时的图片
         */
        fun hideNoCacheBackground()

        /**
         * 显示编辑按钮
         */
        fun showEditBtn()

        /**
         * 隐藏编辑按钮
         */
        fun hideEditBtn()

        fun setEditMode(isEditMode: Boolean)
        fun setSelectCountText(text: String)
        fun setSelectAllText(text: String)
        fun setDeleteBtnEnable(enable: Boolean)
        fun showDeleteConfirmDialog()
        fun showToast(it: String)
        fun setResultOk()
    }

    abstract class Presenter<M : Model> : BaseDownloadPresenter<Model, View>() {

        /**
         * 获取sd卡容量和总容量
         */
        abstract fun getSDCardFreeSize()

        /**
         * 获取视频下载任务列表
         */
        abstract fun getAllVideoTasks()

        /**
         * 删除视频下载任务
         * 这里如果是选中删除下载中的任务，则会把所有除开完成状态的任务全部删除，选中其他完成的下载完成的视频，则会根据该视频的videoId查询下载完成的任务进行删除\
         */
        abstract fun deleteTasks(vararg downloadBeans: DownloadBean)

        /**
         * 编辑模式选中视频
         * @param selected 是否选中
         * @param bean 选中的视频
         */
        abstract fun onSelectVideo(selected: Boolean, bean: DownloadBean)

        /**
         * 点击了全选按钮
         */
        abstract fun onSelectAllClick()

        /**
         * 点击了删除按钮
         */
        abstract fun onDeleteClick()

        abstract fun deleteSelected()
        abstract fun deselectAll()
    }
}