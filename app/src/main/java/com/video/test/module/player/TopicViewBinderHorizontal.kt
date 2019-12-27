package com.video.test.module.player

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.BeanTopicContentBean
import me.drakeet.multitype.ItemViewBinder

class TopicViewBinderHorizontal : ItemViewBinder<BeanTopicContentBean, TopicViewBinderHorizontal.TopicViewHolder>() {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TopicViewHolder, item: BeanTopicContentBean) {
        GlideApp.with(holder.itemView.context)
                .load(item.zt_pic)
                .transform(RoundedCorners(15))
                .into(holder.ivBg)
        holder.tvTitle.text = item.zt_title
        holder.tvCount.text = "${item.zt_num}部影片"
        holder.itemView.setOnClickListener {
            ARouter.getInstance()
                    .build("/topicVideoList/activity")
                    .withInt("pid", 2)
                    .withString("tag", item.id)
                    .withString("type", item.zt_title)
                    .navigation()
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): TopicViewHolder {
        val view = inflater.inflate(R.layout.bean_recycle_item_search_recommend_topic, parent, false)
        return TopicViewHolder(view)
    }

    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val ivBg: ImageView = itemView.findViewById(R.id.iv_topic_bg)
        internal val tvTitle: TextView = itemView.findViewById(R.id.tv_topic_title)
        internal val tvCount: TextView = itemView.findViewById(R.id.tv_topic_video_count)
    }

}