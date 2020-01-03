package com.video.test.ui.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.VideoBean

class ColumnVideoHorizontalAdapter : RecyclerView.Adapter<ColumnVideoHorizontalAdapter.ViewHolder>() {

    private val data: MutableList<VideoBean> = ArrayList()

    fun setData(data: List<VideoBean>) {
        this.data.clear()
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bean_item_recycle_column_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        GlideApp.with(holder.itemView.context)
                .load(item.vod_pic)
                .override(240, 360)
                .centerCrop()
                .transform(RoundedCorners(15))
                .into(holder.ivCover)
        holder.tvName.text = item.vod_name

        val vodScore: String = item.vod_scroe
        if (TextUtils.isEmpty(vodScore) || vodScore.toDouble() == 0.0 || vodScore.toDouble() == 10.0) {
            holder.tvScore.setTextColor(ContextCompat.getColor(holder.tvScore.context, R.color.homepage_font_episode))
            holder.tvScore.text = "暂无评分"
        } else {
            holder.tvScore.setTextColor(ContextCompat.getColor(holder.tvScore.context, R.color.homepage_font_grade))
            holder.tvScore.text = vodScore
        }
        holder.itemView.setOnClickListener {
            ARouter.getInstance().build("/player/activity").withString("vodId", item.vod_id).navigation()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        val tvScore: TextView = itemView.findViewById(R.id.tv_score)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
    }
}