package com.video.test.module.search

import android.annotation.SuppressLint
import android.support.constraint.Group
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.BeanTopicContentBean
import com.video.test.javabean.SearchTopicBean
import me.drakeet.multitype.ItemViewBinder

class SearchTopicViewBinder : ItemViewBinder<BeanTopicContentBean, SearchTopicViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.bean_recycle_search_recommend_topic, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, item: BeanTopicContentBean) {
        GlideApp.with(holder.itemView.context)
                .load(item.zt_pic)
                .centerCrop()
                .transform(RoundedCorners(6))
                .transition(withCrossFade()).into(holder.mIvCover)
        holder.mTvTitle.text = item.zt_title
        holder.mTvVideoCount.text = "${item.zt_num}部影片"
        if (item is SearchTopicBean && item.isShowTitle) {
            holder.mGroupColumn.visibility = View.VISIBLE
        } else {
            holder.mGroupColumn.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            ARouter.getInstance()
                    .build("/topicVideoList/activity")
                    .withInt("pid", 2)
                    .withString("tag", item.id)
                    .withString("type", item.zt_title)
                    .navigation()
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val mTvTitle: TextView = itemView.findViewById(R.id.tv_topic_title)
        internal val mTvVideoCount: TextView = itemView.findViewById(R.id.tv_topic_video_count)
        internal val mIvCover: ImageView = itemView.findViewById(R.id.iv_topic_bg)
        internal val mGroupColumn: Group = itemView.findViewById(R.id.group_column)
    }

}