package com.video.test.module.swap

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.video.test.R
import com.video.test.framework.GlideApp
import com.video.test.javabean.SwapHistoryBean
import me.drakeet.multitype.ItemViewBinder

class SwapHistoryViewBinder : ItemViewBinder<SwapHistoryBean, SwapHistoryViewBinder.ViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val view = inflater.inflate(R.layout.bean_recycle_item_swap_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SwapHistoryBean) {
        holder.tvName.text = item.title
        holder.tvContent.text = item.content
        holder.tvNum.text = item.num
        holder.tvDate.text = item.date
        GlideApp.with(holder.itemView.context)
                .load(item.image)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .error(R.drawable.ic_avatar_default)
                .into(holder.ivImage)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        val ivImage: ImageView = itemView.findViewById(R.id.iv_icon)
        val tvNum: TextView = itemView.findViewById(R.id.tv_num)
        val tvDate: TextView = itemView.findViewById(R.id.tv_time)

    }
}