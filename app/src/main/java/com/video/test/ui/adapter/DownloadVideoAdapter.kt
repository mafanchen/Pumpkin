package com.video.test.ui.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.video.test.R
import com.video.test.javabean.DownloadBean
import com.video.test.javabean.DownloadedBean
import com.video.test.javabean.DownloadingBean
import jaygoo.library.m3u8downloader.utils.MUtils

class DownloadVideoAdapter : RecyclerView.Adapter<DownloadVideoAdapter.DownloadViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DOWNLOADING = 1
        private const val VIEW_TYPE_DOWNLOADED = 2
    }

    var data: List<DownloadBean>? = null

    var isManager = false

    var onSelectListener: OnSelectListener? = null

    override fun getItemViewType(position: Int): Int = if (data!![position] is DownloadingBean) VIEW_TYPE_DOWNLOADING else VIEW_TYPE_DOWNLOADED

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): DownloadViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (type == VIEW_TYPE_DOWNLOADING)
            DownloadingViewHolder(inflater.inflate(R.layout.bean_recycle_item_video_downloading, parent, false))
        else
            DownloadedViewHolder(inflater.inflate(R.layout.bean_recycle_item_video_downloaded, parent, false))
    }

    override fun getItemCount(): Int = if (data == null) 0 else data!!.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val bean = data!![position]
        holder.checkboxSelect.visibility = if (isManager) View.VISIBLE else View.GONE
        holder.checkboxSelect.isChecked = bean.selected
        holder.checkboxSelect.setOnClickListener {
            val select = holder.checkboxSelect.isChecked
            bean.selected = select
            onSelectListener?.onSelect(select, bean)
        }
        if (bean is DownloadingBean && holder is DownloadingViewHolder) {
            holder.tvVideoCount.text = bean.tasks.size.toString()
            if (bean.isDownloading) {
                holder.tvVideoName.visibility = View.VISIBLE
                holder.tvVideoName.text = bean.videoName
                holder.progressBar.progress = (bean.progress * 100).toInt()
                holder.tvSpeed.text = MUtils.formatFileSize(bean.downloadSpeed) + "/s"
            } else {
                holder.tvVideoName.visibility = View.GONE
                holder.progressBar.progress = 0
                holder.tvSpeed.text = "已暂停"
            }
        } else if (bean is DownloadedBean && holder is DownloadedViewHolder) {
            holder.tvVideoName.text = bean.videoName
            holder.tvVideoCount.text = "${bean.tasks.size}个视频"
            holder.tvVideoSize.text = MUtils.formatFileSize(bean.size)
        }
        holder.itemView.setOnClickListener {
            ARouter.getInstance().build("/download/videoList/activity").withString("videoId", bean.videoId).navigation()
        }
    }

    fun deSelectAll() {
        data?.forEach { it.selected = false }
    }

    fun selectAll() {
        data?.forEach { it.selected = true }
    }

    class DownloadingViewHolder(itemView: View) : DownloadViewHolder(itemView) {
        override val tvVideoName: TextView = itemView.findViewById(R.id.tv_download_video_name)
        override val tvVideoCount: TextView = itemView.findViewById(R.id.tv_download_video_count)
        override val checkboxSelect: CheckBox = itemView.findViewById(R.id.checkbox_select)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_download)
        val tvSpeed: TextView = itemView.findViewById(R.id.tv_download_speed)
    }

    private class DownloadedViewHolder(itemView: View) : DownloadViewHolder(itemView) {
        override val tvVideoName: TextView = itemView.findViewById(R.id.tv_download_video_name)
        override val tvVideoCount: TextView = itemView.findViewById(R.id.tv_download_video_count)
        override val checkboxSelect: CheckBox = itemView.findViewById(R.id.checkbox_select)
        val tvVideoSize: TextView = itemView.findViewById(R.id.tv_download_video_size)
    }

    abstract class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract val tvVideoName: TextView
        abstract val tvVideoCount: TextView
        abstract val checkboxSelect: CheckBox
    }

    interface OnSelectListener {
        fun onSelect(isSelected: Boolean, bean: DownloadBean)
    }
}